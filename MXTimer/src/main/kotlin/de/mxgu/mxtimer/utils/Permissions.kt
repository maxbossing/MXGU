@file:Suppress("MemberVisibilityCanBePrivate")
package de.mxgu.mxtimer.utils

object Permissions {
    // Bypasses the worldFreeze and AllowJoin Settings
    object Bypass {
        const val  BASE = "mxgu.bypass.timer"
        const val ALLOWJOIN = "$BASE.allowjoin"
        const val WORLDFREEZE = "$BASE.worldfreeze"
    }
    // Command permissions
    object Commands {

        // /timer
        object Timer {
            // Basic permission
            const val BASE = "mxgu.timer.command"

            // Timer GUI via /timer
            const val GUI = "$BASE.gui"

            // /timer resume and /timer pause
            const val STATE = "$BASE.state"

            // /timer add and /timer subtract
            const val TIME = "$BASE.time"

            // /timer direction
            const val DIRECTION = "$BASE.direction"

            // /timer settings <arg>
            const val SETTINGS = "$BASE.settings"

            // /timer reset
            const val RESET = "$BASE.reset"

            const val RELOAD = "$BASE.reload"
        }

        // /ptimer
        object PersonalTimer {
            // Basic permission
            const val BASE = "mxgu.ptimer.command"
        }
    }


}