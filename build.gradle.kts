plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.6" apply false
}
group = "de.mxgu"
version = "1"
repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/groups/public/") }
}
