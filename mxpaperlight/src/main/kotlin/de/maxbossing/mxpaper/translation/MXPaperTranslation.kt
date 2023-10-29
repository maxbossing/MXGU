@file:Suppress("unused")
package de.maxbossing.mxpaper.translation

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Path
import java.util.Locale
import java.util.Properties
import kotlin.io.path.div
import java.net.URLClassLoader
import java.util.ResourceBundle
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException

/*
 * Source: https://github.com/l4zs/translations
 *
 * Usage:
 * Imagine having a test_en.properties and test_de.properties file in your project under
 *  src/main/resources/i18n/. Then the following line is all you need:
 *
 * MXPaperTranslation(plugin, Key.key("test"), Path.of("i18n"), listOf(Locale.ENGLISH, Locale.GERMAN))
 *
 * In your plugins datafolder you will be able to find the test_en.properties and test_de.properties
 *  files under /translations/i18n/. Those translation files will be registered to the GlobalTranslator,
 *  so your users can easily edit translations. Also, a test-translations.yml file will be created in
 *  your plugins datafolder under /translations/. Your users can configure which translations they want
 *  to enable in this file. They are also able to create new translations by creating new files
 *  in the /translations/i18n/ folder and adding the corresponding language code to the config.
 */

class MXPaperTranslation(
    private val plugin: JavaPlugin,
    private val key: Key,
    private val resourcePath: Path,
    private val templateLocales: List<Locale>,
) {

    private val baseConfigName = "translations.yml"
    private val dir = File(plugin.dataFolder, "translations").resolve(key.value())
    private val MXTranslationConfig = MXTranslationConfig(dir.parentFile, baseConfigName, key, templateLocales, plugin)
    private var translationRegistry = blankTranslationRegistry()

    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        reloadTranslations()
    }

    fun reloadTranslations() {
        saveTemplateBundles()
        loadTranslations()
    }

    private fun blankTranslationRegistry(): TranslationRegistry {
        return TranslationRegistry.create(key).apply {
            defaultLocale(MXTranslationConfig.fallbackLocale)
        }
    }

    private fun loadTranslations() {
        if (MXTranslationConfig.locales.isEmpty()) {
            MXTranslationConfig.locales = templateLocales
            plugin.componentLogger.warn(
                Component.text(
                    "No translations found. Automatically added the template translations (" +
                            "${templateLocales.joinToString(", ") { "'${it.toLanguageTag()}'" }})." +
                            " You can adjust this according to your needs in the ${key.value()}-$baseConfigName at plugins/${plugin.name}/translations/."
                )
            )
        }
        if (GlobalTranslator.translator().sources().contains(translationRegistry)) {
            GlobalTranslator.translator().removeSource(translationRegistry)
        }
        translationRegistry = blankTranslationRegistry()
        registerLocalesToRegistry()
    }

    private fun registerLocalesToRegistry() {
        GlobalTranslator.translator().addSource(translationRegistry)
        plugin.logger.info("Checking for new translations for ${key.value()}")
        var wasUpdated = false
        MXTranslationConfig.locales.forEach {
            if (updateProperties(dir.path, it)) {
                wasUpdated = true
            }
            val bundle = resourceBundleFromClassLoader(dir.path, key.value(), it)
            translationRegistry.registerAll(it, bundle, false)
        }
        if (wasUpdated) {
            plugin.logger.info("Found new translations for ${key.value()}. Make sure to update your custom translations.")
        } else {
            plugin.logger.info("No new translations found for ${key.value()}.")
        }
    }

    private fun saveTemplateBundles(override: Boolean = false) {
        templateLocales.forEach {
            val fileName = "${key.value()}_${it.toLanguageTag().replace("-", "_")}.properties"
            val bundleFile = (dir.toPath() / fileName).toFile()
            if (!bundleFile.exists() || override) {
                plugin.saveResource(
                    "$resourcePath/$fileName",
                    "${dir.path.substringAfter(plugin.dataFolder.path)}/$fileName", override
                )
            }
        }
    }

    private fun updateProperties(path: String, locale: Locale): Boolean {
        val fileName = "${key.value()}_${locale.toLanguageTag().replace("-", "_")}.properties"
        val bundleInputStream = plugin.getResource("$resourcePath/$fileName") ?: return false
        val bundle = Properties()
        bundle.load(bundleInputStream)
        val file = (dir.toPath() / fileName).toFile()
        if (!file.exists()) {
            return false
        }
        val properties = loadProperties("$path/$fileName")
        var wasUpdated = false
        bundle.toSortedMap(compareBy { it.toString() }).forEach { (key, value) ->
            if (!properties.containsKey(key)) {
                properties[key] = value
                wasUpdated = true
            }
        }
        saveProperties("$path/$fileName", properties)
        return wasUpdated
    }
}

internal fun JavaPlugin.saveResource(resourcePath: String, savePath: String, replace: Boolean = false) {
    getResource(resourcePath)
        ?.readBytes()?.let {
            val file = File(dataFolder, savePath)
            if (!file.exists() || replace) {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeBytes(it)
            }
        }
}

internal fun loadProperties(path: String): Properties {
    val inputStream = File(path).inputStream()
    val properties = Properties()
    properties.load(inputStream)
    inputStream.close()
    return properties
}

internal fun saveProperties(path: String, properties: Properties) {
    val outputStream = File(path).outputStream()
    properties.store(outputStream, null)
    outputStream.close()
}

internal fun resourceBundleFromClassLoader(path: String, bundleName: String, locale: Locale): ResourceBundle {
    val file = File(path)
    val urls = arrayOf(file.toURI().toURL())
    val loader: ClassLoader = URLClassLoader(urls)
    return ResourceBundle.getBundle(bundleName, locale, loader)
}

class MXTranslationConfig(dir: File, name: String, private val key: Key, templateLocales: List<Locale>, private val plugin: JavaPlugin) {

    private val translationsComment = listOf(
        "The enabled translations",
        "The first one will be used as a fallback and therefore should (like really) be complete (all keys set)",
        "",
        "Add as many translations as you want (create a new file for each)",
        "The file must be named like the language code",
        "i.E. ${key.value()}_en.properties for English,",
        "${key.value()}_en_us.properties for American English",
        "${key.value()}_en_uk.properties for British English, etc.",
        "Then add the language code (en, en_us, en_uk, etc.) to this list",
    )
    private val file: File
    private val yml: YamlConfiguration

    var locales: List<Locale>
        get() = yml.getStringList("translations").mapNotNull { Locale.forLanguageTag(it.replace("_", "-")) }
        set(value) {
            yml.set("translations", value.map { it.toLanguageTag().replace("-", "_") })
            yml.setComments("translations", translationsComment)
            save()
        }

    val fallbackLocale: Locale
        get() = locales.firstOrNull() ?: Locale.ENGLISH

    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        file = File(dir, "${key.value()}-$name")
        val firstInit: Boolean = !file.exists()
        if (!file.exists()) {
            try {
                plugin.saveResource(name, file.path.substringAfter("${plugin.dataFolder.path}/").replace(key.value() + "/", ""))
            } catch (e: IOException) {
                plugin.componentLogger.error("Could not save default config for ${key.value()}", e)
            }
        }
        yml = YamlConfiguration.loadConfiguration(file)
        if (locales.isEmpty() && firstInit) {
            locales = templateLocales
        }
    }

    private fun save() {
        try {
            yml.save(file)
        } catch (e: IOException) {
            plugin.componentLogger.error("Could not save config for ${key.value()}", e)
        }
    }
}