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
    compileOnly("com.google.code.gson:gson:2.11.0")
    implementation("com.zaxxer:HikariCP:6.2.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.2")

    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("com.google.code.gson:gson:2.11.0")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    test {
        useJUnitPlatform()
    }

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
        relocate("com.zaxxer.hikari", "com.example.plugin.libs.hikari")
        relocate("org.mariadb.jdbc", "com.example.plugin.libs.mariadb")
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