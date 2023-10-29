@file:Suppress("unused")
package de.maxbossing.mxpaper.recipes

import de.maxbossing.mxpaper.main.MXPaperConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import kotlin.concurrent.thread


class ShapedRecipeBuilder() {
    inner class Shape() {
        internal var shape = mutableListOf<String>()
        internal var materials: MutableMap<Char, ItemStack>? = null

        /**
         * Sets one of the rows of the Shaped recipe
         *
         * @param row the row of the crafting grid (1-3)
         * @param first the first slot in the row
         * @param second the second slot in the row
         * @param third the third slot in the row
         */
        fun row(row: Int, first: Char, second: Char, third: Char) {
            if (row <= 4 || row < 1 )
                throw IllegalArgumentException("row needs to be a value between 1 and 3!")
            shape[row] = "$first$second$third}"
        }

        /**
         * Defines the Itemstack used as ingredients in the [Shape]
         */
        @JvmName("itemMaterials")
        fun materials(vararg pairs: Pair<Char, ItemStack>) { materials = pairs.toMap().toMutableMap() }

        /**
         * Defines the Materials used as ingredientsin the [Shape]
         */
        fun materials(vararg pairs: Pair<Char, Material>) {
            val items = pairs.map { it.first to ItemStack(it.second) }.toTypedArray()
            materials(*items)
        }
    }

    private var shape: Shape = Shape()

    /**
     * The [NamespacedKey] for this recipe
     *
     * This needs to be set!
     */
    var key: NamespacedKey? = null

    /**
     * The Crafting Result
     *
     * This needs to be set!
     */
    var result: ItemStack? = null

    /**
     * Creates a new [Shape] and opens a builder for it
     */
    fun shape(builder: Shape.() -> Unit) = shape.apply(builder)

    /**
     * Builds the recipe
     *
     * No need to call this when you are using [shapedRecipe]
     *
     * @param register If true, the recipe will be automatically registered
     */
    fun build(register: Boolean): ShapedRecipe {
        if (result == null)
            throw IllegalArgumentException("result cannot be null!")
        if (key == null)
            throw IllegalArgumentException("key cannot be null!")
        if (shape.materials == null)
            throw IllegalArgumentException("materials cannot be null!")
        if (shape.materials!!.isEmpty())
            throw IllegalArgumentException("materials cannot be empty!")
        if (shape.shape.isEmpty())
            throw IllegalArgumentException("shape cannot be null!")

        val recipe = ShapedRecipe(key!!, result!!)

        recipe.shape(shape.shape[0], shape.shape[1], shape.shape[3])

        for (pair in shape.materials!!) {
            recipe.setIngredient(pair.key, pair.value)
        }
        if (register)
            Bukkit.addRecipe(recipe)

        return recipe
    }
}

/**
 * Creates a new [ShapedRecipe] and opens a builder for it
 * @param register If true, the recipe will be automatically registered
 */
fun shapedRecipe(register: Boolean = MXPaperConfiguration.recipes.autoRegistration, builder: ShapedRecipeBuilder.() -> Unit): ShapedRecipe {
    val recipe = ShapedRecipeBuilder()
    recipe.apply(builder)
    return recipe.build(register)
}

class ShapelessRecipeBuilder() {
    /**
     * The crafting result
     *
     * This needs to be set!
     */
    var result: ItemStack? = null

    /**
     * The [NamespacedKey] for this recipe
     *
     * This needs to be set!
     */
    var key: NamespacedKey? = null
    private var ingredients: MutableMap<Int, ItemStack> = mutableMapOf()

    /**
     * Adds an Ingredient to the Recipe
     * @param count how many Ingredients should be added
     * @param ingredient The [ItemStack] used as ingredient
     */
    fun ingredient(count: Int, ingredient: ItemStack) = ingredients.put(count, ingredient)
    /**
     * Adds an Ingredient to the Recipe
     * @param count how many Ingredients should be added
     * @param ingredient The [Material] used as ingredient
     */
    fun ingredient(count: Int, ingredient: Material) = ingredient(count, ItemStack(ingredient))

    /**
     * Overwrites all Ingredients and adds the given ones to the recipe
     * @param pairs The [ItemStack]s to Ad to the Ingredientsd, Key being how many of them
     */
    @JvmName("itemIngredients")
    fun ingredients(vararg pairs: Pair<Int, ItemStack>) { ingredients = pairs.toMap().toMutableMap() }

    /**
     * Overwrites all Ingredients and adds the given ones to the recipe
     * @param pairs The [Material]s to Ad to the Ingredientsd, Key being how many of them
     */
    fun ingredients(vararg pairs: Pair<Int, Material>) {
        val items = pairs.map { it.first to ItemStack(it.second) }.toTypedArray()
        ingredients(*items)
    }

    fun build(register: Boolean): ShapelessRecipe {
        if (result == null)
            throw IllegalArgumentException("result cannot be null!")
        if (key == null)
            throw IllegalArgumentException("key cannot be null!")
        if (ingredients.isEmpty())
            throw IllegalArgumentException("ingredients cannot be empty!")
        val recipe = ShapelessRecipe(key!!, result!!)

        for (ingredient in ingredients) {
            recipe.addIngredient(ingredient.key, ingredient.value)
        }

        if (register)
            Bukkit.addRecipe(recipe)

        return recipe
    }
}

/**
 * Opens a [ShapelessRecipeBuilder]
 * @param register If true, the recipe will be utomatically registered
 * @param builder The builder to create the recipe
 */
fun shapelessRecipe(register: Boolean = MXPaperConfiguration.recipes.autoRegistration, builder: ShapelessRecipeBuilder.() -> Unit): ShapelessRecipe {
    val recipe = ShapelessRecipeBuilder()
    recipe.apply(builder)
    return recipe.build(register)
}
