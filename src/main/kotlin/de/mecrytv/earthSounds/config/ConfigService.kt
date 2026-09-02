package de.mecrytv.earthSounds.config

import com.google.gson.JsonElement
import java.io.File

/**
 * Zugriff auf eine JSON-Konfiguration ueber Punkt-Pfade, z.B. `"messages.noPermission"`.
 *
 * Fehlende Werte werden beim Laden automatisch aus den Defaults ergaenzt und
 * zurueck in die Datei geschrieben - bereits vorhandene Werte bleiben unangetastet.
 */
interface ConfigService {

    /** Die verwaltete Datei auf der Platte. */
    val file: File

    // ---------------------------------------------------------------- Lifecycle

    /** Liest die Datei neu ein und mischt fehlende Defaults ein. */
    fun reload()

    /** Schreibt den aktuellen Stand atomar auf die Platte. */
    fun save()

    /** Verwirft alle Werte und stellt die Defaults wieder her (inkl. [save]). */
    fun resetToDefaults()

    // ---------------------------------------------------------------- Find

    /** `true`, wenn unter [path] ein Wert (kein `null`) hinterlegt ist. */
    fun contains(path: String): Boolean

    /** Rohzugriff auf den Teilbaum unter [path], oder `null` wenn nicht vorhanden. */
    fun find(path: String): JsonElement?

    /** Namen aller direkten Kind-Keys unter [path]; leerer Pfad = Wurzel. */
    fun keys(path: String = ""): Set<String>

    // ---------------------------------------------------------------- Read

    fun <T : Any> get(path: String, type: Class<T>): T?

    fun <T : Any> getOrDefault(path: String, type: Class<T>, fallback: T): T

    fun <T : Any> getList(path: String, type: Class<T>): List<T>

    /** Liest einen String und ersetzt darin alle [placeholders]. */
    fun getString(path: String, vararg placeholders: Pair<String, Any?>): String?

    fun getStringList(path: String, vararg placeholders: Pair<String, Any?>): List<String>

    fun getBoolean(path: String, fallback: Boolean = false): Boolean

    fun getInt(path: String, fallback: Int = 0): Int

    fun getLong(path: String, fallback: Long = 0L): Long

    fun getDouble(path: String, fallback: Double = 0.0): Double

    /** Deserialisiert die komplette Konfiguration in ein Modell-Objekt. */
    fun <T : Any> asModel(type: Class<T>): T

    // ---------------------------------------------------------------- Write

    /** Legt [value] unter [path] ab und erzeugt fehlende Zwischenobjekte. */
    fun set(path: String, value: Any?)

    /** Entfernt [path]; `true`, wenn dort etwas lag. */
    fun delete(path: String): Boolean

    // ---------------------------------------------------------------- Placeholder

    /** Ersetzt [placeholders] in einem beliebigen Text. */
    fun format(text: String, vararg placeholders: Pair<String, Any?>): String
}

/** `config.get<Settings>("settings")` statt `config.get("settings", Settings::class.java)`. */
inline fun <reified T : Any> ConfigService.get(path: String): T? = get(path, T::class.java)

inline fun <reified T : Any> ConfigService.getOrDefault(path: String, fallback: T): T =
    getOrDefault(path, T::class.java, fallback)

inline fun <reified T : Any> ConfigService.getList(path: String): List<T> = getList(path, T::class.java)

inline fun <reified T : Any> ConfigService.asModel(): T = asModel(T::class.java)
