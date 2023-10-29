package de.maxbossing.mxpaper.chat

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

/**
 * Translate MinyMessage into HTML or MCFunction Strings
 *
 * Source: https://github.com/MiraculixxT/MiniMessage-Convertor
 */
object MiniMessageConverter {
    private val mm = MiniMessage.miniMessage()
    private val mmGson = GsonComponentSerializer.gson()
    private val chatClearer = "{\"text\":\"${buildString { repeat(21) { append("\\n ") } }}\"}"

    fun rawColor(tag: String, word: String): String {
        return rawColor("$tag$word", ConverterType.HTML)
    }

    fun rawColor(input: String, type: ConverterType, functionInfo: FunctionInfo? = null): String {
        val component = mm.deserialize(input)
        val string = mmGson.serialize(component)
        val textObj = try {
            Json.decodeFromString<TextStyling>(string)
        } catch (_: Exception) {
            return ""
        }
        return when (type) {
            ConverterType.HTML -> calcHTML(
                textObj,
                textObj.bold,
                textObj.italic,
                textObj.underlined,
                textObj.strikethrough
            )

            ConverterType.MC_FUNCTION -> buildString {
                if (functionInfo == null) return "Invalid Request"
                append(
                    "# Message Convertor (by Miraculixx & To_Binio)\n#\n" +
                            "# Settings Input\n" +
                            "# - Text: $input\n" +
                            "# - Prefix: ${functionInfo.prefix}\n" +
                            "# - Chars per Tick: ${functionInfo.charsPerTick}\n" +
                            "# - Namespace: ${functionInfo.scoreName} -> ${functionInfo.target}\n"
                )
                val data = calcDataPack(textObj, textObj, functionInfo)
                append(data.first)
                append(
                    "\n\n# Looping & Reset\n" +
                            "scoreboard players add ${functionInfo.scoreName} text-ticker 1\n" +
                            "execute if score ${functionInfo.scoreName} text-ticker matches ..${data.second} run schedule function ${functionInfo.functionName} 1t replace\n" +
                            "execute if score ${functionInfo.scoreName} text-ticker matches ${data.second + 1}.. run scoreboard players set ${functionInfo.scoreName} text-ticker 0"
                )
            }
        }
    }

    private fun calcHTML(
        styling: TextStyling,
        bold: Boolean? = null,
        italic: Boolean? = null,
        underlined: Boolean? = null,
        strikethrough: Boolean? = null
    ): String {
        if (styling.extra != null) {
            return buildString {
                styling.extra.forEach { extra ->
                    append(calcHTML(extra, styling.bold, styling.italic, styling.underlined, styling.strikethrough))
                }
            }
        } else if (styling.text != null) {
            return buildString {
                append("<span class='mc' style='")
                styling.color?.let { append("color: ${styling.getHex()}; ") }
                bold?.let { append("font-weight: bold; ") }
                italic?.let { append("font-style: italic; ") }
                if (underlined != null || strikethrough != null) {
                    append("text-decoration: ")
                    underlined?.let { append("underline") }
                    strikethrough?.let { append(" line-through") }
                    append(";")
                }
                append("'>${styling.text.ifBlank { " " }}</span>")
            }
        }
        return ""
    }

    private fun calcDataPack(
        styling: TextStyling,
        preStyling: TextStyling,
        functionInfo: FunctionInfo
    ): Pair<String, Int> {
        return buildString {
            styling.text?.let { input ->
                val split = input.chunked(functionInfo.charsPerTick)
                var text = ""
                val styleTag = buildString style@{
                    styling.bold?.let { this@style.append(",\"bold\":$it") } ?: preStyling.bold?.let { this@style.append(",\"bold\":$it") }
                    styling.italic?.let { this@style.append(",\"italic\":$it") } ?: preStyling.italic?.let { this@style.append(",\"italic\":$it") }
                    styling.strikethrough?.let { this@style.append(",\"strikethrough\":$it") } ?: preStyling.strikethrough?.let { this@style.append(",\"strikethrough\":$it") }
                    styling.underlined?.let { this@style.append(",\"underlined\":$it") } ?: preStyling.underlined?.let { this@style.append(",\"underlined\":$it") }
                    styling.obfuscated?.let { this@style.append(",\"obfuscated\":$it") } ?: preStyling.obfuscated?.let { this@style.append(",\"obfuscated\":$it") }
                    styling.color?.let { this@style.append(",\"color\":\"$it\"") }
                }
                val prefix = functionInfo.prefix?.let { "$it," } ?: ""

                split.forEach { sequence ->
                    val currentPart = "{\"text\":\"$text$sequence\"$styleTag}"
                    val command = "execute if score ${functionInfo.scoreName} text-ticker matches ${functionInfo.currentTick} run tellraw ${functionInfo.target}"
                    val previousPart = if (functionInfo.previousPart.isNotBlank()) "${functionInfo.previousPart}," else ""
                    append("\n$command [\"\",$chatClearer,$prefix$previousPart$currentPart]")
//                    print("\n$command [\"\",$prefix$previousPart$currentPart]")

                    functionInfo.currentTick++
                    text += sequence
                }
                val fullTag = "{\"text\":\"$text\"$styleTag}"
                functionInfo.previousPart += if (functionInfo.previousPart.isBlank()) fullTag else ",$fullTag"
            }

            styling.extra?.let {
                it.forEach { extra ->
                    append(calcDataPack(extra, styling, functionInfo).first)
                }
            }
        } to functionInfo.currentTick
    }

    @Serializable
    private data class TextStyling(
        val extra: List<TextStyling>? = null,
        val color: String? = null,
        val bold: Boolean? = null,
        val italic: Boolean? = null,
        val underlined: Boolean? = null,
        val strikethrough: Boolean? = null,
        val obfuscated: Boolean? = null,
        //obfuscated ist für looser
        val text: String? = null
    ) {
        fun getHex() = color?.let {
            if (it[0] == '#') color
            else when (color) {
                "black" -> "#000000"
                "dark_gray" -> "#555555"
                "dark_blue" -> "#0000aa"
                "blue" -> "#5555ff"
                "dark_green" -> "#00aa00"
                "green" -> "#55ff55"
                "dark_aqua" -> "#00aaaa"
                "aqua" -> "#55ffff"
                "dark_red" -> "#aa0000"
                "red" -> "#ff5555"
                "dark_purple" -> "#ff55ff"
                "gold" -> "#ffaa00"
                "yellow" -> "#ffff55"
                "gray" -> "#aaaaaa"
                "white" -> "#ffffff"
                else -> color
            }
        } ?: ""
    }

    data class FunctionInfo(
        val functionName: String,
        val charsPerTick: Int,
        val scoreName: String,
        val target: String,
        val prefix: String? = null,
        var currentTick: Int,
        var previousPart: String
    )

    enum class ConverterType {
        HTML,
        MC_FUNCTION
    }
}