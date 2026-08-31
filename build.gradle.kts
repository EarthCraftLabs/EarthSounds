plugins {
    kotlin("jvm") version "2.1.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "de.mecrytv"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(21)
}

tasks {
    processResources {
        val props = mapOf(
            "version" to version,
            "description" to (project.description ?: "")
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set("EarthSounds")
        archiveClassifier.set("")
        archiveVersion.set("")

        // Wichtig bei Kotlin-Plugins: Verschiebt die Kotlin Runtime in dein Package,
        // damit es keine ClassLoader-Konflikte mit anderen Kotlin-Plugins gibt.
        relocate("kotlin", "de.mecrytv.earthsounds.libs.kotlin")
    }

    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.1")
        jvmArgs("-Xms2G", "-Xmx2G")
    }
}