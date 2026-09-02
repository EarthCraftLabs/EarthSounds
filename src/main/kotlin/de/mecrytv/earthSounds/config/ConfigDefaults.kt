package de.mecrytv.earthSounds.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Woher die Standardwerte kommen. Beim Erstellen des [ConfigService] waehlst du eine
 * der drei Varianten aus - siehe [Companion.model], [Companion.resource], [Companion.values].
 *
 * Die Defaults werden bei jedem Start gegen die vorhandene Datei gemergt: fehlende
 * Keys kommen dazu, gesetzte Werte des Nutzers bleiben unangetastet.
 */
fun interface ConfigDefaults {

    fun load(gson: Gson): JsonObject

    companion object {

        /** Defaults aus einem Datenmodell - die Feldwerte der Klasse sind die Standardwerte. */
        fun model(model: Any): ConfigDefaults = ConfigDefaults { gson ->
            gson.toJsonTree(model).asJsonObject
        }

        /**
         * Defaults aus einer JSON-Datei im `resources`-Ordner; sie wandert mit ins Jar
         * und wird beim ersten Start in den `plugins/EarthSounds`-Ordner kopiert.
         */
        fun resource(
            path: String = "config.json",
            classLoader: ClassLoader = ConfigDefaults::class.java.classLoader,
        ): ConfigDefaults = ConfigDefaults {
            val stream = classLoader.getResourceAsStream(path)
                ?: error("Ressource '$path' fehlt im resources-Ordner des Plugins")
            val parsed = stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }
            parsed as? JsonObject ?: error("Ressource '$path' enthaelt kein JSON-Objekt")
        }

        /**
         * Defaults direkt an Ort und Stelle notieren, z.B. in `onEnable`:
         * `ConfigDefaults.values("settings.namespace" to "earthcraft", "settings.debug" to false)`.
         */
        fun values(vararg entries: Pair<String, Any?>): ConfigDefaults = ConfigDefaults { gson ->
            JsonObject().also { root ->
                entries.forEach { (path, value) -> JsonPaths.set(root, path, gson.toJsonTree(value)) }
            }
        }
    }
}
