package de.mxgu.mxtimer

import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.bukkit.plus
import de.maxbossing.mxpaper.main.MXPaper
import de.maxbossing.mxpaper.main.prefix
import de.maxbossing.mxpaper.translation.MXPaperTranslation
import de.mxgu.mxtimer.command.TimerCommands
import de.mxgu.mxtimer.timer.TimerManager
import de.mxgu.mxtimer.utils.info
import de.mxgu.mxtimer.utils.warning
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager
import net.kyori.adventure.key.Key
import java.nio.file.Path
import java.util.*

class MXTimerMain: MXPaper() {
    companion object {
        lateinit var mxtimer: MXTimerMain
    }

    // RyseInventory Manager
    private val invManager: InventoryManager = InventoryManager(this)

    override fun load() {
        // This needs to be called first
        // As Command Trees register themselves
        // And if the API is not loaded until then we crash
        CommandAPI.onLoad(CommandAPIBukkitConfig(this))

        // Set instance
        mxtimer = this

        // Set prefix
        prefix = cmp("[", cBase) + cmp("MXTimer", cAccent, true) + cmp("] ", cBase)

        if (debug) {
            warning("MXTimer is running in debug mode!")
            warning("This mode is only meant for developers and can cause odd behaviour and will clutter the console with messages!")
            warning("If you did not intend to use debug mode, and are not a developer, please contact the Support!")
            warning("https://dc.mxgu.de")
        }

        // Register Resource bundles to the Global Translator
        MXPaperTranslation(mxtimer, Key.key("common"), Path.of("i18n"), listOf(Locale.ENGLISH, Locale.GERMAN))
        MXPaperTranslation(mxtimer, Key.key("gui"), Path.of("i18n"), listOf(Locale.ENGLISH, Locale.GERMAN))
        MXPaperTranslation(mxtimer, Key.key("messages"), Path.of("i18n"), listOf(Locale.ENGLISH, Locale.GERMAN))

        info(cmp("MXTimer Loaded successfully"))
    }

    override fun startup() {
        // This activates CommandAPI to be able to register commands
        CommandAPI.onEnable()

        // Starts the RyseInventory Handlers
        invManager.invoke()

        // Activate the Timers
        TimerManager

        // Register the Commands
        TimerCommands.timerCommand
        TimerCommands.personalTimerCommand
        TimerCommands.debugCommand

        info(cmp("MXTimer Started Successfully"))
    }

    override fun shutdown() {
        TimerManager.shutdownTimers()
    }
}
/**
* Debug Mode
*
* In Debug Mode, Additional Information will be printed to the Console
*
* And the /debug Command is activated
*/
var debug: Boolean = true

// Plugin Instance
val mxtimer by lazy { MXTimerMain.mxtimer }