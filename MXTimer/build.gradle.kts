import io.papermc.paperweight.userdev.internal.setup.util.paperweightHash
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper") version "1.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"

    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.mxgu"
version = "1"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/groups/public/") }
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    compileOnly("dev.jorel:commandapi-bukkit-core:9.2.0")
    compileOnly("dev.jorel:commandapi-bukkit-kotlin:9.2.0")

    implementation("io.github.rysefoxx.inventory:RyseInventory-Plugin:1.6.4")

    implementation(project(":mxpaperlight"))
}

kotlin {
    jvmToolchain(17)
}

tasks {
    assemble {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.20.2")
    }
    shadowJar {
        relocate("de.mxgu.mxpaper", "de.mxgu.mxtimer.mxpaper")
    }
}


bukkit {
    name = "MXTimer"
    website = "https://mxgu.de"
    author = "Max Bossing <info@maxbossing.de>"
    prefix = "MXTimer"

    // version is set automatic from project version

    main = "de.mxgu.mxtimer.MXTimerMain"

    libraries = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib:1.9.10",
        "org.jetbrains.kotlin:kotlin-reflect:1.9.0",
        "dev.jorel:commandapi-bukkit-shade:9.2.0",
        "dev.jorel:commandapi-bukkit-kotlin:9.2.0",
        "io.github.rysefoxx.inventory:RyseInventory-Plugin:1.6.4",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
    )

    apiVersion = "1.20"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

