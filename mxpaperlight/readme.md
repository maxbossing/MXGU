# MXPaper
[ ![Latest version](https://img.shields.io/maven-central/v/de.maxbossing/mxpaper?color=pink&label=latest%20version&style=for-the-badge) ](https://repo1.maven.org/maven2/net/axay/kspigot/)
[ ![Guide](https://img.shields.io/badge/guide-read-%23c2ff73?style=for-the-badge) ](https://mxpaper.maxbossing.de) <br>
MXPaper is a heavily modified Fork of the popular [KSpigot](https://jakobkmar/kspgot) Library, targetting Paper.

MXPaper extends KSpigot with a lot of features, and combines it with other popular open-source Libs
like [l4zs' translation System](https://github.com/l4zs/translations) 
or [Miraculixx' kpaper](https://github.com/miraculixxt/kpaper), 
but also drops some features in favour of simplicity and compatibility.
For a detailed explanation of features and changes, head over to [Major Changes](#major-changes)

---

# Usage
MXPaper is published on Maven central

> A full Setup guide is available in the [Guide](https://mxpaper.maxbossing.de/setup/gradle/)

Gradle Kts:
```kotlin
dependencies {
    implementation("de.maxbossing:mxpaper:2.0.0")
}
```

And dont forget to add it to your libraries inside the `plugin.yml`
```yml
[...]
libraries:
  - de.maxbossing:mxpaper:2.0.0
```

# Major Changes
### Component Extensions
Paper/Adventure Component system is very close to the original Minecraft Component System, offering everything _possible_
in minecraft at the price of becomming messy quickly. KSpigot itself targets this using `literalText` , but this still 
requires multiple steps and is one of the few cases where a Kotlin DSL builder is not the most fitting. 

MXPaper features a extension functions for creating Components with ease

Example: A message with an embedded link, bold and colored
```kotlin
// Using Paper API
Component.text("Click me! ")
    .color(NamedTextColor.GRAY)
    .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
    .clickEvent(ClickEvent.openUrl("https://maxbossing.de"))
    .append(
        Component.text("To open this link!")
            .color(NamedTextColor.BLUE)
    )

// Using literalText
literalText("Click me! ") {
    bold = true
    color = MXColors.GRAY
    clickEvent = onClickOpenURL("https://maxbossing.de")
    text("To open this link!") {
        color = MXColors.BLUE
    }
}

// Using MXPaper's cmp()
cmp("Click me! ", color = MXColors.GRAY, bold = true)
    .addUrl("https://maxbossing.de")
    + cmp("To open this link!", color = MXColors.BLUE)
```

Also, MXPaper has some features missing in the Adventure API for handling Components, 
for example, splitting Components by RegEx:
```kotlin
val component = cmp("Split me!", color = MXColors.RED, bold = true)

// Splitting Components with pure Paper
val splitted = PlainTextComponentSerializer.plainText().serialize(component).split(" ")
// We splitted them, but only have strings and would need to reapply formatting

//Splitting components with MXPaper
val splitted = component.split(" ")
```

### Audience Extensions
Audiences (by Adventure) represent every entity and CommandSender (like console).
An Audience object can contain multiple entities or CommandSender to simply bulk actions.
With MXPaper you can simply add multiple audiences together by using the + operator.

In addition MXPaper adds some utilities like easy titles packet with kotlin durations and more.

### Custom Head Library
When working with GUIs/Inventories you often use some kind of heads to display certain actions or just for a better look.
Instead of looking them up everytime, KPaper now implements a bunch of frequently used heads with a fancy preview.
If you have some general heads, simply create a new PR!

![Head Preview](https://i.imgur.com/yO2qt2y.png)

### Recipe Builder
Creating Recipes in Paper is a bit wierd to use and can be a tedious task. 
To target this problem, MXPaper features Kotlin-style DSL builders for Shaped and Shapeless Recipes
```kotlin

// Shaped Recipe using pure Paper
val key = NamespacedKey(PluginInstance, "cool_sword")
val result = ItemStack(Material.DIAMOND_SWORD)
val recipe = ShapedRecipe(key, result)
recipe.shape(" e ", " e ", " s ")
recipe.setIngredient('e', Material.EMERALD)
recipe.setIngredient('s', Material.STICK)
Bukkit.addRecipe(recipe)

// Shaped recipe using MXPaper
val recipe = shapedRecipe {
    result = ItemStack(Material.DIAMOND_SWORD)
    key = pluginKey("cool_sword")
    
    shape {
        row(1, ' ', 'e', ' ')
        row(2, ' ', 'e', ' ')
        row(3, ' ', 's', ' ')
        
        materials (
            'e' to Material.EMERALD,
            's' to Material.STICK
        )
    }
}

// Shapeless Recipe using pure Paper
val key = NamespacedKey(PluginInstance, "creeper_spawn_egg")
val result = ItemStack(Material.CREEPER_SPAWN_EGG)
val recipe = ShapelessRecipe(key, result)
recipe.addIngredient(4, Material.GUNPOWDER)
Bukkit.addRecipe(recipe)

// Shapeless Recipe using MXPaper

val recipe = shapelessRecipe {
    key = pluginKey("creeper_spawn_egg")
    result = ItemStack(Material.CREEPER_SPAWN_EGG)
    
    ingredient(4, Material.GUNPOWDER)
}

```

### Translation System
MXPaper implements the brilliant [Translations System by l4zs](https://github.com/l4zs/translations), which adds resource-bundle
based translations to MXPaper, which users also can edit and create their own translations. 

**Example:**

Imagine having a `test_en.properties` and `test_de.properties` file in your project under `src/main/resources/i18n/`.
Then the following line is all you need:

```kotlin
Translation(plugin, Key.key("test"), Path.of("i18n"), listOf(Locale.ENGLISH, Locale.GERMAN))
```

In your plugins datafolder you will be able to find the `test_en.properties` and `test_de.properties` files
under `/translations/i18n/`.
Those translation files will be registered to the GlobalTranslator, so your users can easily edit translations.
Also, a `test-translations.yml` file will be created in your plugins datafolder under `/translations/`.
Your users can configure which translations they want to enable in this file.
They are also able to create new translations by creating new files in the `/translations/i18n/` folder and adding the
corresponding language code to the config.

### MiniMessage Conversion
MXPaper features [MiniMessage Converter by Miraculixx](https://github.com/miraculixxt/MiniMessageConvertor) 
to automatically convert MiniMessag into other formats such as HTML or TellRaw JSOn  

### Serialization
MXPaper offers some helper classes to Serialize Data using kotlinx.serialization, 
for example UUIDs which normally do not have a serializer built-in.

Additionally, MXPaper offers wrappers around Bukkits [Configuration] Objects to make it easier to query data

# Removals
### Legacy API's
MXPaper is build with Paper and modern systems such as Components in mind,
which is why all Deprecated methods were removed, and most of the Systems that
rely on Strings instead of Components or such are removed too (for example `componentBuilder`). 

It's time to move on.

### IP Blocker
Alls of the provided services are paid closed source companies. Additionally, the feature to detect VPNs, proxies or similar should be implemented by an exact plugin targeting this. 

### GamePhase API
With the game phase api you could add simple mini-game states. Again, this feels like too specific to be in MXPaper and too pure for actual mini-game servers. 

### Brigardier Commands
Proably the most used feature that will be cut out of MXPaper.
The simple reason for this is the version independence.
The current implementation does not allow the use in multiple versions and can break even in a single minor Minecraft update.  

Nonetheless, I highly recommend staying at brigadier with a library specialized to support it in all versions.
The library [CommandAPI](https://commandapi.jorel.dev/8.8.0/kotlindsl.html) by Jorel follows a similar syntax and supports kotlin too.
To learn more about it, visit their [documentation](https://commandapi.jorel.dev/8.8.0/kotlinintro.html)

### Structure API
In favor of the WorldEdit or FAWE API. Using structures in general is a rare case and does not fit into MXPaper

# Documentation

A generall guide covering most big features can be found at [https://mxpaper.maxbossing.de](https://mxpaper.maxbossing.de).

Full documentation will be released soon
