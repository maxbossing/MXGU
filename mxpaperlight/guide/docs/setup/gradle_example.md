An example for a Gradle build script of a project using MXPaper would be:

*(please note that the version in the following examples might be outdated)*

`build.gradle.kts`
```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "your.group"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.20-R0.1-SNAPSHOT")
    implementation("de.maxbossing:MXPaper:2.0.0")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}
```

`settings.gradle.kts`
```kotlin
rootProject.name = "projectname"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
```
