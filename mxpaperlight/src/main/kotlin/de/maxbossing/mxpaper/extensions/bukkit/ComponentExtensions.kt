@file:Suppress("unused")
package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.main.MXPaperConfiguration
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.translation.GlobalTranslator
import java.util.Locale
import java.util.regex.Pattern

/**
 * Creates a [Component] from a [String]
 * @return The Component from the String
 */
fun String.toComponent(): Component = Component.text(this)

/**
 * Creates a [Component] from a legacy String
 *
 * Note: Render [TranslatableComponent]s before using this
 * @return The Component from the legacy String
 */
fun Component.toLegacyString(): String = LegacyComponentSerializer.legacyAmpersand().serialize(this)

/**
 * Returns a [String] from a [Component]
 *
 * @return The Component serialized to a String
 *
 * Note: Render [TranslatableComponent]s before using this
 */
fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)

/**
 * Renders a [TranslatableComponent] with the given [locale]
 * @param locale The [Locale] to render the Component with
 * @return The rendered Component
 */
fun TranslatableComponent.render(locale: Locale): Component = GlobalTranslator.render(this, locale)

/**
 * Create a basic [Component] with optional styles. By default, every style is deactivated and don't stack with previous ones!
 * @param text The Text of the Component
 * @param color The [TextColor] of the Component
 * @param bold if the Component should be bold
 * @param italic if the Component should be italic
 * @param strikethrough if the Component should be strikethrough
 * @param underlined if the Component should be underlined
 * @param obfuscated If the Component should be obfuscated
 * @return A Component from the given text with all formatting applied
 */
fun cmp(text: String, color: TextColor = MXPaperConfiguration.text.baseColor, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false, obfuscated: Boolean = false): Component =
    Component.text(text).color(color)
        .decorations(
            mapOf(
                TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
                TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
                TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
                TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined),
                TextDecoration.OBFUSCATED to TextDecoration.State.byBoolean(obfuscated)
            )
        )


/**
 * Append two components
 * @param other The [Component] to Append
 * @return The Combined [Component]
 * @see Component.append
 */
operator fun Component.plus(other: Component): Component = append(other)

/**
 * Shortcut to add a hover text
 * @param display The [Component] to display when hovering
 * @return The [Component] with added [net.kyori.adventure.text.event.HoverEvent]
 */
fun Component.addHover(display: Component): Component = hoverEvent(asHoverEvent().value(display))

/**
 * Shortcut to add an url [ClickEvent]
 * @param url the URL to link when clicking
 * @return The [Component] with added [ClickEvent]
 */
fun Component.addUrl(url: String): Component = clickEvent(ClickEvent.openUrl(url))

/**
 * Shortcut to add a run command [ClickEvent]
 * @param command The command to run when clicking
 * @return The [Component] with added [ClickEvent]
 */
fun Component.addCommand(command: String): Component = clickEvent(ClickEvent.runCommand(command))

/**
 * Shortcut to add a suggestion [ClickEvent]
 *
 * @param suggestion the Command to suggest when clicking
 *
 * @return The [Component] with added [ClickEvent]
 * 
 */
fun Component.addSuggest(suggestion: String): Component = clickEvent(ClickEvent.suggestCommand(suggestion))

/**
 * Shortcut to add a copy [ClickEvent]
 * @param copyPrompt The Prompt to copy when clicking
 * @return The [Component] with added [ClickEvent]
 */
fun Component.addCopy(copyPrompt: String): Component = clickEvent(ClickEvent.copyToClipboard(copyPrompt))

/**
 * Empty [Component] containing a single whitespace. Useful to bypass auto stripping
 */
fun emptyComponent() = Component.text(" ")

/**
 * Bulk decorate an existing [Component]
 * @param bold Whether the Component should be bold
 * @param italic Whether the Component should be italic
 * @param strikethrough Whether the Component should be Strikethrough
 * @param underlined whether the Component should be underlined
 * @param obfuscated Whether the Component should be obfuscated
 * @return The decorated [Component]
 */
fun Component.decorate(bold: Boolean? = null, italic: Boolean? = null, strikethrough: Boolean? = null, underlined: Boolean? = null, obfuscated: Boolean? = null): Component =
    apply {
        bold?.let { decoration(TextDecoration.BOLD, it) }
        italic?.let { decoration(TextDecoration.ITALIC, it) }
        strikethrough?.let { decoration(TextDecoration.STRIKETHROUGH, it) }
        underlined?.let { decoration(TextDecoration.UNDERLINED, it) }
        obfuscated?.let { decoration(TextDecoration.OBFUSCATED, it) }
    }

/**
 * Split a [Component] into multiple components using a regex pattern
 * @param regex the regex pattern to split the component
 * @return the split [Component]s
 */
fun Component.split(regex: Pattern) = ComponentSplitting.split(this, regex)

/**
 * Replace a part of a component with another component
 * @param old the text to replace
 * @param new the component to replace with
 * @return the replaced component
 */
fun Component.replace(old: String, new: Component) = this.replaceText(TextReplacementConfig.builder().match(old).replacement(new).build())

/**
 * Removes the italic flag as lores automatically add it
 * @return the Component without the italic flag and with [MXPaperConfiguration.text.baseColor] applied
 */
fun Component.lore(): Component {
    return this.decoration(TextDecoration.ITALIC, false)
}

/**
 * Removes the italic flag as lores automatically add it
 * @return the Components without the italic flag and with [MXPaperConfiguration.text.baseColor] applied
 */
fun List<Component>.lore(): List<Component> {
    return this.map {
        it.decoration(TextDecoration.ITALIC, false)
    }
}
