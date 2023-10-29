@file:Suppress("Unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
package de.maxbossing.mxpaper.config

import de.maxbossing.mxpaper.extensions.kotlin.createIfNotExists
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException

/**
 * Class representing a Configuration File in YAML Format
 *
 * If the file does not exist, it will be created
 *
 * @param file The file from which the configuration should be loaded
 */
class YamlConfig(val file: File) {
    var config: YamlConfiguration = YamlConfiguration.loadConfiguration(file)

    /**
     * Saves the configuration in memory to the file specified in constructor
     */
    fun save(): Boolean {
        return try {
            config.save(file)
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * checks if a path is present in the config
     * @param node the path to check
     * @return if the path exists
     */
    fun contains(node: String): Boolean = config.contains(node)

    /**
     * gets an object from the config
     * @param node the path to get
     * @return the object, null if the path is not present
     */
    fun get(node: String): Any? = config.get(node)

    /**
     * set an object to a path
     * @param node the path under which the object should be saved
     * @param o the object to save
     * @param save if true, the config will be written to disk after the object has ben set
     */
    fun set (node: String, o: Any?, save: Boolean = true) {
        config.set(node, o)
        if (save) save()
    }

    /**
     * get a [String] from the config
     * @param node the path to get
     * @return the string under the path
     */
    fun string(node: String): String = get(node) as String

    /**
     * get an [Int] from the config
     * @param node the path to get
     * @return the Int under the path
     */
    fun int(node: String): Int = get(node) as Int

    /**
     * get a [Double] from the config
     * @param node the path to get
     * @return the double under the path
     */
    fun double(node: String): Double = get(node) as Double

    /**
     * get a [Float] from the config
     * @param node the path to get
     * @return the float under the path
     */
    fun float(node: String): Float = get(node) as Float

    /**
     * get a [Boolean] from the config
     * @param node the path to get
     * @return the boolean under the path
     */
    fun bool(node: String): Boolean = get(node) as Boolean

    /**
     * get an [ItemStack] from the config
     * @param node the path to get
     * @return the itemstack under the path
     */
    fun item(node: String): ItemStack = get(node) as ItemStack

    /**
     * get a [Location] from the config
     * @param node the path to get
     * @return the string under the path
     */
    fun location(node: String): Location = get(node) as Location

    /**
     * get a [ConfigurationSection] from the config
     * @param node the path to get
     * @return the section under the path
     */
    fun section(node: String): ConfigurationSection = get(node) as ConfigurationSection

    /**
     * get a [Vector] from the config
     * @param node the path to get
     * @return the vector under the path
     */
    fun vector(node: String): Vector = get(node) as Vector

    /**
     * get a [Block] from the config
     * @param node the path to get
     * @return the block under the path
     */
    fun block(node: String): Block = get(node) as Block

    /**
     * get a [List]<*> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun list(node: String): List<*> = get(node) as List<*>

    /**
     * get a [List]<[String]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun stringList(node: String): List<String> = list(node) as List<String>

    /**
     * get a [List]<[Int]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun intList(node: String): List<Int> = list(node) as List<Int>

    /**
     * get a [List]<[Double]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun doubleList(node: String): List<Double> = list(node) as List<Double>

    /**
     * get a [List]<[Float]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun floatList(node: String): List<Float> = list(node) as List<Float>

    /**
     * get a [List]<[ItemStack]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun itemList(node: String): List<ItemStack> = list(node) as List<ItemStack>

    /**
     * get a [List]<[Location]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun locationList(node: String): List<Location> = list(node) as List<Location>

    /**
     * get a [List]<[Vector]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun vectorList(node: String): List<Vector> = list(node) as List<Vector>

    /**
     * get a [List]<[Block]> from the config
     * @param node the path to get
     * @return the list under the path
     */
    fun blockList(node: String): List<Block> = list(node) as List<Block>
}
