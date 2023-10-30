package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.println
import de.mxgu.mxtimer.data.*
import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.utils.debug
import de.mxgu.mxtimer.utils.info
import io.netty.buffer.UnpooledUnsafeDirectByteBuf
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.concurrent.timer
import kotlin.time.Duration

object TimerManager {

    var designs: MutableMap<String, TimerDesign> = mutableMapOf()

    var timers: MutableMap<UUID, Timer> = mutableMapOf()

    /**
     * The global timer
     */
    lateinit var globalTimer: Timer
        private set

    /**
     * A default [TimerDesign] for a [Timer]
     */
    lateinit var defaultDesign: TimerDesign
        private set

    /**
     * Gets a timer design by its UUID
     *
     * The UUID is the File name in timer/styles
     *
     * If the design is not present, it will fallback to default style
     *
     * @param uuid the uuid of the style
     * @return the TimerDesign if present
     */
    fun getDesign(uuid: String): TimerDesign = if (designs.containsKey(uuid)) designs[uuid]!! else defaultDesign

    /**
     * Get the [UUID] of a [TimerDesign]
     * @param design The design to search for the uuid
     * @return the UUID of the Design
     */
    fun getUUID(design: TimerDesign): String = designs.entries.associateBy({ it.value }) { it.key }[design]!!

    /**
     * Get the [UUID] of a [Timer]
     * @param timer The timer to search for the UUID
     * @reurn the UUID of the Timer
     */
    fun getUUID(timer: Timer): UUID = timers.entries.associateBy({ it.value }) { it.key }[timer]!!

    /**
     * Checks whether this Player has a Personal timer
     * @param uuid the UUID of the Player to check
     */
    fun hasPersonalTimer(uuid: UUID): Boolean = timers.containsKey(uuid)

    /**
     * Gets a Personal [Timer] from a Players UUID
     *
     * May be null if the player does not have a timer
     * @param uuid The Player UUID
     * @return His Personal Timer
     */
    fun getPersonalTimer(uuid: UUID): Timer? = timers[uuid]

    /**
     * Gets a Personal [Timer] from a Player UUID
     *
     * If the player does not have a Personal timer, one will be created
     *
     * @param uuid The Players UUID
     * @param timer The timer to add if no timer exists
     */
    fun getOrAddPersonalTimer(uuid: UUID, timer: TimerData): Timer {
        if (hasPersonalTimer(uuid)) {
            return getPersonalTimer(uuid)!!
        }
        else {
            return addPersonalTimer(uuid, timer.deserialize())
        }
    }

    /**
     * Creates a new Personal timer for a Player
     *
     * The UUID of the timer must be set to the Players UUID!
     *
     * @param uuid The Players UUID
     * @param timer the timer to add to the player
     */
    fun addPersonalTimer(uuid: UUID, timer: Timer = newTimer(defaultDesign, playerUUID = uuid)): Timer {
        timers += uuid to timer
        return getPersonalTimer(uuid)!!
    }

    /**
     * Writes all timers to disk
     */
    fun saveTimers() {
        debug(cmp("Saving timers to disk"))
        val base = mxtimer.dataFolder.path + "/timers/"
        File(base).mkdirs()
        for (timer in timers) {
            debug(cmp("Saving timer ${timer.key}..."))

            val file = File(base + timer.key + ".json")
            if (!file.exists())
                file.createNewFile()
            file.writeText(Json.encodeToString(TimerData.serializer(), timer.value.serialize()))
        }
    }

    /**
     * Shutdowns all timers and writes them to disk
     */
    fun shutdownTimers() {
        timers.forEach {_, u ->
            u.state = TimerState.STOPPED
        }
        saveTimers()
    }

    init {
        // Create needed folders
        fileStructure()

        // Load Timer Designs
        loadDesigns()

        // Load Timers
        loadTimers()

    }

    /**
     * creates the file structure needed for designs and timers
     */
    private fun fileStructure() {
        File(mxtimer.dataFolder.path + "/designs").mkdirs()
        File(mxtimer.dataFolder.path + "/timers").mkdirs()
    }

    /**
     * Loads all designs from disk
     */
    fun loadDesigns() {
        info(cmp("Loading Timer Designs..."))
        designs = mutableMapOf()
        if (File(mxtimer.dataFolder.path + "/designs").listFiles() == null) {
            debug(cmp("No designs found"))
        } else {
            for (file in File(mxtimer.dataFolder.path + "/designs").listFiles()) {
                debug(cmp("Loading Design ${file.nameWithoutExtension}"))
                designs += file.nameWithoutExtension to Json.decodeFromString(TimerDesign.serializer(), file.readText())
            }
        }

        debug("Dumping default designs...")
        // Create Template Designs
        val defaultdesigns = listOf(
            "default",
            "blacknwhite",
            "rainbow",
            "classic"
        )

        for (defaultdesign in defaultdesigns) {
            mxtimer.saveResource("designs/$defaultdesign.json", true)

            if (defaultdesign == "default")
                defaultDesign = Json.decodeFromString(TimerDesign.serializer(), File(mxtimer.dataFolder.path + "/designs/$defaultdesign.json").readText())
            else if (!designs.containsKey(defaultdesign)) {
                designs += defaultdesign to Json.decodeFromString(TimerDesign.serializer(), File(mxtimer.dataFolder.path + "/designs/$defaultdesign.json").readText())
            } else {
                designs[defaultdesign] = Json.decodeFromString(TimerDesign.serializer(), File(mxtimer.dataFolder.path + "/designs/$defaultdesign.json").readText())
            }
        }
    }

    /**
     * Loads all timers from disk
     */
    private fun loadTimers() {
        info(cmp("Loading Timers..."))
        timers = mutableMapOf()
        if (File(mxtimer.dataFolder.path + "/timers").listFiles() == null) {
            debug(cmp("No Timers found"))
        } else {
            for (file in File(mxtimer.dataFolder.path + "/timers").listFiles()) {
                debug(cmp("Loading Timer ${file.nameWithoutExtension}"))
                val timer = Json.decodeFromString(
                    TimerData.serializer(),
                    file.readText()
                )
                // If personal timers are deactivated, do not start the timer tasks as they are not needed
                // And would just use resources
                // And if an Admin changes their mind and activates personal timers again, they will start when the players use /ptimer resume

                timer.activate = if (ConfigManager.config.personalTimers) timer.activate else false

                if (file.nameWithoutExtension == "global-timer")
                    globalTimer = timer.deserialize()
                else
                    timers += UUID.fromString(file.nameWithoutExtension) to timer.deserialize()
            }
        }

        // Create Global Timer if not present

        if (!this::globalTimer.isInitialized) {
            debug(cmp("Global Timer not found, creating..."))
            globalTimer =  newTimer(global = true)
        }

        info(cmp("Timers Loaded!"))
    }

    /**
     * Creates a New [Timer]
     * @param design The design
     * @param global whether this is the global timer or not
     * @param playerUUID The UUID of the target player, leave blank if global
     * @param direction The direction
     * @param time The Time of the timer
     * @param activate Whether to directly activate
     * @param visible if its visible
     * @param settings Additional Settings
     */
    fun newTimer(
        design: TimerDesign = defaultDesign,
        global: Boolean = false,
        playerUUID: UUID = UUID.randomUUID(),
        direction: TimerDirection = TimerDirection.COUNTUP,
        time: Duration = Duration.ZERO,
        activate: Boolean = true,
        visible: Boolean = true,
        settings: TimerSettings = TimerSettings(true, true)
    ): Timer {
        return Timer (
            design = design,
            activate = activate,
            time = time,
            playerUUID = if (global) null else playerUUID,
            direction = direction,
            visible = visible,
            settings = settings
        )
    }
}

val globalTimer by lazy { TimerManager.globalTimer }