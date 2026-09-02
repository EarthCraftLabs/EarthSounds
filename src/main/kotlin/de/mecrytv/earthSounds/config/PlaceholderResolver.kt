package de.mecrytv.earthSounds.config

/** Ersetzt Platzhalter in Texten aus der Konfiguration. */
fun interface PlaceholderResolver {

    fun resolve(input: String, placeholders: Map<String, Any?>): String
}

/**
 * Ersetzt Platzhalter der Form `%key%` in genau einem Durchlauf.
 *
 * Ein Durchlauf statt `replace()` pro Key heisst: eingesetzte Werte werden nicht
 * erneut als Platzhalter interpretiert (kein Rekursions-/Injection-Problem, wenn
 * z.B. ein Spielername `%prefix%` heisst).
 *
 * Unbekannte Platzhalter bleiben unveraendert stehen - so faellt ein Tippfehler
 * in der config.json sofort auf, statt still zu verschwinden.
 */
class PatternPlaceholderResolver(
    prefix: String = "%",
    suffix: String = "%",
) : PlaceholderResolver {

    private val pattern = Regex("${Regex.escape(prefix)}([A-Za-z0-9_.-]+)${Regex.escape(suffix)}")

    override fun resolve(input: String, placeholders: Map<String, Any?>): String {
        if (input.isEmpty() || placeholders.isEmpty()) return input
        return pattern.replace(input) { match ->
            val key = match.groupValues[1]
            if (placeholders.containsKey(key)) placeholders[key]?.toString().orEmpty() else match.value
        }
    }
}
