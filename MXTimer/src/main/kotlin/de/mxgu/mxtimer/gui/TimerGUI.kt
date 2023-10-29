package de.mxgu.mxtimer.gui

import de.maxbossing.mxpaper.MXColors
import de.maxbossing.mxpaper.MXHeads
import de.maxbossing.mxpaper.extensions.bukkit.*
import de.maxbossing.mxpaper.extensions.deserialized
import de.maxbossing.mxpaper.extensions.fancy
import de.maxbossing.mxpaper.items.*
import de.mxgu.mxtimer.cAccent
import de.mxgu.mxtimer.cBase
import de.mxgu.mxtimer.data.TimerDesign
import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.timer.*
import de.mxgu.mxtimer.utils.msg
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem
import io.github.rysefoxx.inventory.plugin.content.IntelligentItemError
import io.github.rysefoxx.inventory.plugin.content.InventoryContents
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider
import io.github.rysefoxx.inventory.plugin.pagination.Pagination
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta
import java.util.regex.Pattern
import kotlin.random.Random
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.DurationUnit

import kotlin.time.toDuration

class TimerGUI(val timer: Timer, val player: Player) {

    val locale = player.locale()

    class error : IntelligentItemError {
        override fun cantClick(player: Player?, item: IntelligentItem?) {
            super.cantClick(player, item)
        }

        override fun cantSee(player: Player?, item: IntelligentItem?) {
            super.cantSee(player, item)
        }
    }


    private val separator = cmp("-", cBase).lore()

    val click = msg("click", locale).color(cBase).lore() + cmp(" ∙ ", cAccent).lore()

    val rightClick = msg("right", locale).color(cBase).lore() + separator + click
    val shiftRightClick = msg("shift", locale).color(cBase).lore() + separator + rightClick

    val leftClick = msg("left", locale).color(cBase).lore() + separator + click
    val shiftLeftClick = msg("shift", locale).color(cBase).lore() + separator + click

    val second = msg("second", locale).color(cBase).lore()
    val minute = msg("minute", locale).color(cBase).lore()
    val hour = msg("hour", locale).color(cBase).lore()
    val day = msg("day", locale).color(cBase).lore()

    val seconds = msg("seconds", locale).color(cBase).lore()
    val minutes = msg("minutes", locale).color(cBase).lore()
    val hours = msg("hours", locale).color(cBase).lore()
    val days = msg("days", locale).color(cBase).lore()


    fun timeButton(unit: DurationUnit, item: Material) = IntelligentItem.of(
        itemStack(item) {
            meta {
                displayName(
                    when (unit) {
                        DurationUnit.DAYS -> days.color(cAccent)
                        DurationUnit.SECONDS -> seconds.color(cAccent)
                        DurationUnit.MINUTES -> minutes.color(cAccent)
                        DurationUnit.HOURS -> hours.color(cAccent)
                        else -> cmp("")
                    }
                )
                addLore {
                    val char = when (unit) {
                        DurationUnit.SECONDS -> "s"
                        DurationUnit.MINUTES -> "m"
                        DurationUnit.HOURS -> "h"
                        DurationUnit.DAYS -> "d"
                        else -> ""
                    }
                    lorelist += cmp("")
                    lorelist += leftClick + cmp("-1$char", cAccent).lore()
                    lorelist += shiftLeftClick + cmp("-10$char", cAccent).lore()
                    lorelist += cmp(
                        "                         ",
                        bold = true,
                        strikethrough = true,
                        color = MXColors.DARKGRAY
                    ).lore()
                    lorelist += rightClick + cmp("+1$char", cAccent).lore()
                    lorelist += shiftRightClick + cmp("+10$char", cAccent).lore()
                }
            }
        }
    ) {
        if (it.isLeftClick) {
            if (it.isShiftClick) {
                if (timer.time > ZERO && timer.time - 10.toDuration(unit) > ZERO)
                    timer.subTime(10.toDuration(unit))
            } else {
                if (timer.time > ZERO && timer.time - 1.toDuration(unit) > ZERO)
                    timer.subTime(1.toDuration(unit))
            }
        }
        if (it.isRightClick) {
            if (it.isShiftClick) {
                if (timer.time > ZERO && timer.time + 10.toDuration(unit) > ZERO)
                    timer.addTime(10.toDuration(unit))
            } else {
                if (timer.time > ZERO && timer.time + 1.toDuration(unit) > ZERO)
                    timer.addTime(1.toDuration(unit))
            }
        }
    }

    fun directionButton() = IntelligentItem.of(
        itemStack(Material.PINK_GLAZED_TERRACOTTA) {
            meta {
                displayName(msg("direction", locale).color(cAccent).lore())
                addLore {
                    lorelist += cmp("")
                    lorelist += if (timer.direction == TimerDirection.COUNTUP) cmp("-> ", cBase) + msg(
                        "countup",
                        locale
                    ).color(cAccent).lore() else msg("countup", locale).color(cBase).lore()
                    lorelist += if (timer.direction == TimerDirection.COUNTDOWN) cmp("-> ", cBase) + msg(
                        "countdown",
                        locale
                    ).color(cAccent).lore() else msg("countdown", locale).color(cBase).lore()
                }
            }
        }
    ) {
        timer.direction = when (timer.direction) {
            TimerDirection.COUNTUP -> TimerDirection.COUNTDOWN
            TimerDirection.COUNTDOWN -> TimerDirection.COUNTUP
        }
        it.currentItem!!.meta {
            setLore {
                lorelist += cmp("")
                lorelist += if (timer.direction == TimerDirection.COUNTUP) cmp("-> ", cBase) + msg(
                    "countup",
                    locale
                ).color(cAccent).lore() else msg("countup", locale).color(cBase).lore()
                lorelist += if (timer.direction == TimerDirection.COUNTDOWN) cmp("-> ", cBase) + msg(
                    "countdown",
                    locale
                ).color(cAccent).lore() else msg("countdown", locale).color(cBase).lore()
            }
        }
    }

    fun stateButton() = IntelligentItem.of(
        itemStack(if (timer.state == TimerState.RUNNING) Material.LIME_CONCRETE else Material.RED_CONCRETE) {
            meta {
                displayName(msg("status", locale).color(cAccent).lore())
                setLore {
                    lorelist += cmp("")
                    when (timer.state) {
                        TimerState.RUNNING -> {
                            lorelist += cmp("-> ", cBase) + msg("running", locale).color(cAccent).lore()
                            lorelist += msg("paused", locale).color(cBase).lore()
                            lorelist += msg("stopped", locale).color(cBase).lore()
                        }

                        TimerState.PAUSED -> {
                            lorelist += msg("running", locale).color(cBase).lore()
                            lorelist += cmp("-> ", cBase) + msg("paused", locale).color(cAccent).lore()
                            lorelist += msg("stopped", locale).color(cBase).lore()
                        }

                        TimerState.STOPPED -> {
                            lorelist += msg("running", locale).color(cBase).lore()
                            lorelist += msg("paused", locale).color(cBase).lore()
                            lorelist += cmp("-> ", cBase) + msg("stopped", locale).color(cAccent).lore()
                        }
                    }
                }
            }
        }
    ) {
        timer.state = if (timer.state != TimerState.RUNNING) {
            TimerState.RUNNING
        } else {
            TimerState.PAUSED
        }
        it.currentItem!!.type = if (timer.state == TimerState.RUNNING) Material.LIME_CONCRETE else Material.RED_CONCRETE
        it.currentItem!!.meta {
            setLore {
                lorelist += cmp("")
                when (timer.state) {
                    TimerState.RUNNING -> {
                        lorelist += cmp("-> ", cBase) + msg("running", locale).color(cAccent).lore()
                        lorelist += msg("paused", locale).color(cBase).lore()
                        lorelist += msg("stopped", locale).color(cBase).lore()
                    }

                    TimerState.PAUSED -> {
                        lorelist += msg("running", locale).color(cBase).lore()
                        lorelist += cmp("-> ", cBase) + msg("paused", locale).color(cAccent).lore()
                        lorelist += msg("stopped", locale).color(cBase).lore()
                    }

                    TimerState.STOPPED -> {
                        lorelist += msg("running", locale).lore()
                        lorelist += msg("paused", locale).color(cBase).lore()
                        lorelist += cmp("-> ", cBase) + msg("stopped", locale).color(cAccent).lore()
                    }
                }
            }
        }
    }

    fun settingsButton(): IntelligentItem {
        val head = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                displayName(msg("settings", locale).color(cAccent).lore())
                skullTexture(MXHeads.COMPUTER_OFF)
            }
        }

        return IntelligentItem.of(
            head
        ) {
            TimerSettingsGUI(timer, player)
        }
    }

    fun designPickerButton(): IntelligentItem {
         return IntelligentItem.of(itemStack(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE) {
            meta {
                displayName(msg("designs", locale).color(cAccent).lore())
                flag(ItemFlag.HIDE_ITEM_SPECIFICS)
            }
        }) {
            DesignPicker(timer, player)
        }
    }

    val timerMenu = RyseInventory
        .builder()
        .title(msg("gui.title", player.locale()))
        .rows(4)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.fill(itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp("")) } })

                //  0  1  2  3  4  5  6  7  8
                //  9 10 11 12 13 14 15 16 17
                // 18 19 20 21 22 23 24 25 26
                // 27 28 29 30 31 32 33 34 35

                // Slot 12 -> Days
                // Slot 13 -> Hours
                // Slot 21 -> Minutes
                // Slot 22 -> Seconds

                contents.set(12, timeButton(DurationUnit.DAYS, Material.ENDERMAN_SPAWN_EGG))
                contents.set(13, timeButton(DurationUnit.HOURS, Material.BAT_SPAWN_EGG))
                contents.set(21, timeButton(DurationUnit.MINUTES, Material.ENDERMITE_SPAWN_EGG))
                contents.set(22, timeButton(DurationUnit.SECONDS, Material.MULE_SPAWN_EGG))


                // Slot 16 -> Direction
                // Slot 25 -> State Manipulation (running/paused)

                contents.set(16, directionButton())
                contents.set(25, stateButton())

                // Slot 24 -> Settings
                // Personal timers don't have settings
                // As these settings affect all players on the Server
                if (timer == globalTimer)
                    contents.set(24, settingsButton())

                // Slot 10 -> Design Picker
                contents.set(10, designPickerButton())

            }
        })
        .build(mxtimer)


    init {
        timerMenu.open(player)
    }
}

class TimerSettingsGUI(val timer: Timer, val  player: Player) {
    val locale = player.locale()

    val back = msg("back", locale).color(cAccent).lore()

    fun backButton(): IntelligentItem {
        val head = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                displayName(back)
                skullTexture(MXHeads.ARROW_LEFT_WHITE)
            }
        }
        return IntelligentItem.of(head) { TimerGUI(timer, player)}

    }

    fun worldFreezeButton() = IntelligentItem.of(
        itemStack(Material.BLUE_ICE) {
            meta {
                displayName(msg("gui.settings.worldfreeze.n", locale).color(cAccent).lore())
                setLore {
                    for (l in msg("gui.settings.worldfreeze.l", locale).color(cBase).lore().split(Pattern.compile("\n"))) {
                        lorelist += l
                    }
                    lorelist += cmp("")
                    lorelist += if (timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("active", locale).color(cAccent).lore() else msg("active", locale).color(cBase).lore()
                    lorelist += if (!timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("notactive", locale).color(cAccent).lore() else msg("notactive", locale).color(cBase).lore()
                }
            }
        }
    ) {
        timer.settings.freezeOnPause = !timer.settings.freezeOnPause
        it.currentItem!!.meta {
            setLore {
                for (l in msg("gui.settings.worldfreeze.l", locale).color(cAccent).lore().split(Pattern.compile("\n"))) {
                    lorelist += l
                }
                lorelist += cmp("")
                lorelist += if (timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("active", locale).color(cAccent).lore() else msg("active", locale).color(cBase).lore()
                lorelist += if (!timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("notactive", locale).color(cAccent).lore() else msg("notactive", locale).color(cBase).lore()
            }
        }
    }

    fun allowJoinButton() = IntelligentItem.of(
        itemStack(Material.OAK_DOOR) {
            meta {
                displayName(msg("gui.settings.allowjoin.n", locale).color(cAccent).lore())
                setLore {
                    for (l in msg("gui.settings.allowjoin.l", locale).color(cBase).lore().split(Pattern.compile("\n"))) {
                        lorelist += l
                    }
                    lorelist += cmp("")
                    lorelist += if (timer.settings.allowJoin) cmp("-> ", cBase).lore() + msg("active", locale).color(cAccent).lore() else msg("active", locale).color(cBase).lore()
                    lorelist += if (!timer.settings.allowJoin) cmp("-> ", cBase).lore() + msg("notactive", locale).color(cAccent).lore() else msg("notactive", locale).color(cBase).lore()
                }
            }
        }
    ){
        timer.settings.allowJoin = !timer.settings.allowJoin
        it.currentItem!!.meta {
            setLore {
                for (l in msg("gui.settings.allowjoin.l", locale).color(cAccent).lore().split(Pattern.compile("\n"))) {
                    lorelist += l
                }
                lorelist += cmp("")
                lorelist += if (timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("active", locale).color(cAccent).lore() else msg("active", locale).color(cBase).lore()
                lorelist += if (!timer.settings.freezeOnPause) cmp("-> ", cBase).lore() + msg("notactive", locale).color(cAccent).lore() else msg("notactive", locale).color(cBase).lore()
            }
        }
    }


    val settingsMenu = RyseInventory
        .builder()
        .title(msg("gui.settings.title", locale))
        .rows(4)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.fill(itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp("")) } })

                //  0  1  2  3  4  5  6  7  8
                //  9 10 11 12 13 14 15 16 17
                // 18 19 20 21 22 23 24 25 26
                // 27 28 29 30 31 32 33 34 35

                // Slot 0 -> Back
                contents.set(0, backButton())

                // Slot 10 -> Worldfreeze

                contents.set(10, worldFreezeButton())

                // Slot 19 -> Allow Join While Running

                contents.set(19, allowJoinButton())
            }
        }).build(mxtimer)


    init {
        settingsMenu.open(player)
    }
}

class DesignPicker(val timer: Timer, val player: Player) {
    val locale = player.locale()

    fun backButton(): IntelligentItem {
        val head = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                displayName( msg("back", locale).color(cAccent).lore())
                skullTexture(MXHeads.ARROW_LEFT_WHITE)
            }
        }
        return IntelligentItem.of(head) { TimerGUI(timer, player)}

    }

    val pickerMenu = RyseInventory
        .builder()
        .rows(4)
        .title(msg("timer.gui.design.title", locale))
        .provider( object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.fillBorders(itemStack(Material.GRAY_STAINED_GLASS_PANE) {meta { displayName(cmp("")) }})

                contents.set(0, backButton())

                val pagination: Pagination = contents.pagination()

                pagination.iterator(SlotIterator.builder()
                    .startPosition(1, 1)
                    .endPosition(2, 7)
                    .type(SlotIterator.SlotIteratorType.HORIZONTAL)
                    .build())


                val headLeft = itemStack(Material.PLAYER_HEAD) {
                    amount = if (pagination.isFirst) 1 else pagination.page() -1
                    meta<SkullMeta> {
                        skullTexture(MXHeads.ARROW_LEFT_WHITE)
                        displayName(if (pagination.isFirst) cmp("«", strikethrough = true, bold = true, color = cBase) else cmp("«", strikethrough = false, bold = true, color = cBase))

                    }
                }

                contents.set(3, 3, IntelligentItem.of(headLeft) {
                    if (pagination.isFirst) return@of
                    pagination.inventory().open(player, pagination.previous().page())
                })


                val headRight = itemStack(Material.PLAYER_HEAD) {
                    amount = if (pagination.isLast) 1 else pagination.page()
                    meta<SkullMeta> {
                        skullTexture(MXHeads.ARROW_RIGHT_WHITE)
                        displayName(if (pagination.isLast) cmp("»", strikethrough = true, bold = true, color = cBase) else cmp("»", strikethrough = false, bold = true, color = cBase))
                    }

                }


                contents.set(3, 5, IntelligentItem.of(headRight) {
                    if (pagination.isLast) return@of
                    pagination.inventory().open(player, pagination.next().page())
                })

                TimerManager.designs.forEach { t, u ->
                    pagination.addItem(design(t, u))
                }

            }
        }).build(mxtimer)

    init {
        pickerMenu.open(player)
    }


    /**
     * Generates a Material for the Design
     *
     * The Material will always be the same for the same design
     *
     * As long as the design is not renamed
     */
    fun material(name: String): Material = Material.entries.filter { it.name.endsWith("ARMOR_TRIM_SMITHING_TEMPLATE") }.random(Random(name.hashCode()))

    /**
     * Creates an IntelligentItem for the given TimerDesign
     */
    //TODO: Show author
    //TODO: Maybe show preview
    //TODO: Highlight currently used design with Netherite Upgrade trim
    fun design(name: String, design: TimerDesign): IntelligentItem {
        return IntelligentItem.of(
            itemStack(if (timer.design == design) Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE else material(name)) {
                meta {
                    displayName(design.name.toComponent().color(cAccent).lore())
                    flag(ItemFlag.HIDE_ITEM_SPECIFICS)
                    setLore {
                        lorelist += msg("author", locale).color(cAccent).lore() + cmp(": ", cAccent).lore() + design.creator.toComponent().lore().color(cBase)
                        lorelist += msg("displayslot", locale).color(cAccent).lore() + cmp(": ", cAccent).lore() + design.displaySlot.toString().fancy.toComponent().color(cBase).lore()
                        lorelist += cmp("")
                        lorelist += design.description.split("\n").map { it.toComponent().lore().color(cBase) }
                    }
                }
            }
        ) {
            timer.design = design
            DesignPicker(timer, player)
        }
    }
}