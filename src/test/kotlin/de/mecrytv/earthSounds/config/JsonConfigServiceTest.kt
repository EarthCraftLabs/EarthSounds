package de.mecrytv.earthSounds.config

import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonConfigServiceTest {

    private val dir: File = Files.createTempDirectory("earthsounds-config").toFile()
    private val file = File(dir, "config.json")

    private fun service(defaults: ConfigDefaults = ConfigDefaults.model(PluginConfig())) =
        JsonConfigService(file = file, defaults = defaults)

    @AfterTest
    fun cleanup() {
        dir.deleteRecursively()
    }

    @Test
    fun `legt fehlende Datei mit Defaults an`() {
        val config = service()

        assertTrue(file.isFile, "config.json muss beim ersten Start erzeugt werden")
        assertEquals("earthcraft", config.getString("settings.namespace"))
        assertEquals(setOf("settings", "resourcePack", "messages"), config.keys())
    }

    @Test
    fun `behaelt Nutzerwerte und ergaenzt nur fehlende Defaults`() {
        file.parentFile.mkdirs()
        file.writeText("""{"settings":{"namespace":"custom"},"eigenerKey":42}""")

        val config = service()

        assertEquals("custom", config.getString("settings.namespace"), "Nutzerwert darf nicht ueberschrieben werden")
        assertEquals(1.0, config.getDouble("settings.defaultVolume"), "fehlender Default muss ergaenzt werden")
        assertEquals(42, config.getInt("eigenerKey"), "unbekannte Keys duerfen nicht verloren gehen")
        assertTrue(file.readText().contains("defaultVolume"), "ergaenzte Defaults muessen zurueckgeschrieben werden")
    }

    @Test
    fun `set legt Zwischenobjekte an, delete raeumt auf`() {
        val config = service()

        config.set("sounds.ambient.volume", 0.5)
        assertEquals(0.5, config.getDouble("sounds.ambient.volume"))

        assertTrue(config.delete("sounds.ambient.volume"))
        assertFalse(config.contains("sounds.ambient.volume"))
        assertFalse(config.delete("gibt.es.nicht"), "delete auf unbekanntem Pfad meldet false")
        assertNull(config.getString("gibt.es.nicht"))
    }

    @Test
    fun `save und reload halten Aenderungen fest`() {
        val config = service()
        config.set("settings.namespace", "geaendert")
        config.save()

        val neu = service()
        assertEquals("geaendert", neu.getString("settings.namespace"))

        neu.resetToDefaults()
        assertEquals("earthcraft", neu.getString("settings.namespace"))
    }

    @Test
    fun `kaputte Datei wird gesichert statt geladen`() {
        file.parentFile.mkdirs()
        file.writeText("{ das ist kein json")

        val config = service()

        assertEquals("earthcraft", config.getString("settings.namespace"))
        assertTrue(
            dir.listFiles().orEmpty().any { it.name.startsWith("config.json.broken-") },
            "die kaputte Datei muss als Backup erhalten bleiben",
        )
    }

    @Test
    fun `laedt Defaults aus dem resources-Ordner`() {
        val config = service(ConfigDefaults.resource("defaults-aus-resources.json"))

        assertEquals("aus-resources", config.getString("settings.namespace"))
        assertEquals(0.25, config.getDouble("settings.defaultVolume"))
        assertTrue(file.isFile, "die Ressource muss in den Plugin-Ordner kopiert werden")
    }

    @Test
    fun `nimmt Defaults die direkt beim Erstellen angegeben werden`() {
        val config = service(
            ConfigDefaults.values(
                "settings.namespace" to "inline",
                "settings.defaultVolume" to 0.75,
                "messages.prefix" to "> ",
            ),
        )

        assertEquals("inline", config.getString("settings.namespace"))
        assertEquals(0.75, config.getDouble("settings.defaultVolume"))
        assertEquals("> ", config.getString("messages.prefix"), "Punkt-Pfade erzeugen verschachtelte Objekte")
    }

    @Test
    fun `ersetzt Platzhalter in einem Durchlauf`() {
        val config = service()

        val message = config.getString(
            "messages.soundPlayed",
            "prefix" to "> ",
            "sound" to "ambient.wind",
            "player" to "MecryTv",
        )
        assertEquals("> <green>Sound <yellow>ambient.wind</yellow> fuer <yellow>MecryTv</yellow> abgespielt.", message)

        // Unbekannte Platzhalter bleiben stehen, damit Tippfehler auffallen.
        assertEquals("Hallo %unbekannt%", config.format("Hallo %unbekannt%", "sound" to "x"))
        // Ein eingesetzter Wert wird nicht erneut als Platzhalter gelesen.
        assertEquals("%sound%", config.format("%player%", "player" to "%sound%", "sound" to "BOOM"))
        assertEquals("", config.format("%leer%", "leer" to null))
    }

    @Test
    fun `typisierte Zugriffe und falsche Typen`() {
        val config = service()

        assertEquals(Settings(), config.get<Settings>("settings"))
        assertEquals("earthcraft", config.asModel<PluginConfig>().settings.namespace)

        config.set("liste", listOf("a", "b"))
        assertEquals(listOf("a", "b"), config.getStringList("liste"))
        assertEquals(emptyList(), config.getStringList("settings"), "kein Array -> leere Liste")

        config.set("keineZahl", "abc")
        assertEquals(7, config.getInt("keineZahl", fallback = 7), "unparsbarer Wert faellt auf den Fallback zurueck")
    }
}
