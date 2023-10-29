package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.mxgu.mxtimer.data.ConfigManager
import de.mxgu.mxtimer.data.TimerData
import de.mxgu.mxtimer.data.TimerDesign
import de.mxgu.mxtimer.data.TimerSettings
import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.utils.debug
import de.mxgu.mxtimer.utils.getResourceFolderFiles
import de.mxgu.mxtimer.utils.info
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.time.Duration

object TimerManager {

    var designs: MutableMap<String, TimerDesign>

    var timers: MutableMap<String, Timer>

    val globalTimer get() =  timers["global-timer"]!!

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
    fun getDesign(uuid: String): TimerDesign = if (designs.containsKey(uuid)) designs[uuid]!! else templateDesign()

    /**
     * Gets the UUID of a TimerDesign by design
     *
     * The UUID is the File name in timer/styles
     *
     * @param design The TimerDesign
     * @return The UUID if present
     */
    fun getDesignUUID(design: TimerDesign): String = designs.entries.associateBy({ it.value }) { it.key }[design]!!

    /**
     * Gets a Personal [Timer] from a Players UUID
     * If the Timer does not exist one will be created
     * @param uuid The Player UUID
     * @return His Personal Timer
     */
    fun getPersonalTimer(uuid: String): Timer {
        if (!timers.containsKey(uuid))
            timers += uuid to newTimer(global = false, playerUUID = uuid)
        return timers[uuid]!!
    }

    /**
     * Gets a personal [Timer] from a Players UUID
     *
     * If the Timer does not exists, one will be created.
     *
     * @param uuid The Players UUID
     * @return His Personal Timer
     */
    fun getPersonalTimer(uuid: UUID): Timer = getPersonalTimer(uuid.toString())


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
        timers.forEach {t, u ->
            u.state = TimerState.STOPPED
        }
        saveTimers()
    }

    init {

        // Create needed folders
        fileStructure()

        // Load Timer Designs

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


        // Create Template Designs
        val defaultdesigns = listOf(
            "default",
            "blacknwhite",
            "rainbow"
        )

        for (defaultdesign in defaultdesigns) {
            if (!designs.containsKey(defaultdesign)) {
                mxtimer.saveResource("designs/$defaultdesign.json", true)
                designs += defaultdesign to Json.decodeFromString(TimerDesign.serializer(), File(mxtimer.dataFolder.path + "/designs/$defaultdesign.json").readText())
            }
        }

        // Load Timers

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

                timers += file.nameWithoutExtension to timer.deserialize()
            }
        }

        // Create Global Timer if not present

        if (!timers.containsKey("global-timer")) {
            debug(cmp("Global Timer not found, creating..."))
            timers += "global-timer" to newTimer(global = true)
        }

        info(cmp("Timers Loaded!"))
    }

    fun fileStructure() {
        File(mxtimer.dataFolder.path + "/designs").mkdirs()
        File(mxtimer.dataFolder.path + "/timers").mkdirs()
    }


    /**
     * Creates a New [Timer], set up to be the global timer
     */
    fun newTimer(
        design: TimerDesign = templateDesign(),
        global: Boolean,
        playerUUID: String = UUID.randomUUID().toString()
    ): Timer {
        return Timer (
            design = design,
            activate = true,
            time = Duration.ZERO,
            playerUUID = if (global) null else playerUUID,
            direction = TimerDirection.COUNTUP,
            visible = true,
            settings = TimerSettings(
                true,
                true
            )
        )
    }

    /**
     * A default [TimerDesign] for a [Timer]
     */
    fun templateDesign(): TimerDesign = getDesign("default")
}

val globalTimer by lazy { TimerManager.globalTimer }
val timers by lazy { TimerManager.timers }