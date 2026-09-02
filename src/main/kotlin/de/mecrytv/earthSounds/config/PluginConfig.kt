package de.mecrytv.earthSounds.config

/**
 * Schema *und* Defaults der config.json - die Feldwerte hier sind die Standardwerte.
 *
 * Neue Felder muessen nur hier ergaenzt werden; [JsonConfigService] schreibt sie beim
 * naechsten Start automatisch in bestehende Konfigurationen nach.
 */
data class PluginConfig(
    val settings: Settings = Settings(),
    val resourcePack: ResourcePack = ResourcePack(),
    val messages: Messages = Messages(),
)

data class Settings(
    val namespace: String = "earthcraft",
    val defaultVolume: Float = 1.0f,
    val defaultPitch: Float = 1.0f,
    val debug: Boolean = false,
)

data class ResourcePack(
    val enabled: Boolean = false,
    val url: String = "",
    val sha1: String = "",
    val required: Boolean = true,
)

data class Messages(
    val prefix: String = "<gray>[<gold>EarthSounds<gray>]</gray> ",
    val soundPlayed: String = "%prefix%<green>Sound <yellow>%sound%</yellow> fuer <yellow>%player%</yellow> abgespielt.",
    val soundNotFound: String = "%prefix%<red>Der Sound <yellow>%sound%</yellow> existiert nicht.",
    val noPermission: String = "%prefix%<red>Dir fehlt die Berechtigung <yellow>%permission%</yellow>.",
    val reloaded: String = "%prefix%<green>Konfiguration neu geladen (%duration%ms).",
)
