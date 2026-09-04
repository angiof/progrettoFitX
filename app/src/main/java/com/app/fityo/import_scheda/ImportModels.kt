package com.app.fityo.import_scheda

import android.graphics.Bitmap
import android.graphics.Rect

/**
 * Tipo di importazione disponibile
 */
enum class ImportType {
    FITYO_FORMAT,   // File JSON/formato nativo FitYo
    EXCEL,          // Foglio .xlsx: prosegue nel flusso di creazione scheda
    PDF,            // Importa da PDF
    CAMERA_OCR      // Scansione con fotocamera
}

/**
 * Rappresenta una riga di esercizio estratta dall'OCR
 */
data class ParsedExerciseRow(
    val id: String = java.util.UUID.randomUUID().toString(),
    val rawText: String,                    // Testo originale estratto
    val exerciseName: String,               // Nome esercizio estratto
    val sets: String,                       // Serie (es. "4")
    val reps: String,                       // Ripetizioni (es. "12")
    val weight: String? = null,             // Peso (es. "50kg")
    val rest: String? = null,               // Recupero (es. "90s")
    val notes: String? = null,              // Note aggiuntive
    val confidence: Float = 1f,             // Confidenza 0-1 (1 = sicuro)
    val boundingBox: Rect? = null,          // Posizione nella foto originale
    val errors: List<ValidationError> = emptyList()
)

/**
 * Errori di validazione per un campo
 */
data class ValidationError(
    val field: String,                      // "exerciseName", "sets", "reps", etc.
    val message: String,                    // Messaggio di errore
    val suggestion: String? = null          // Suggerimento di correzione
)

/**
 * Risultato dell'OCR su un'immagine
 */
data class OcrResult(
    val success: Boolean,
    val rawText: String,                    // Testo grezzo completo
    val parsedRows: List<ParsedExerciseRow>,
    val originalImage: Bitmap?,
    val errorMessage: String? = null,
    val processingTimeMs: Long = 0,
    val usedGemma: Boolean = false
)

/**
 * Stato dell'importazione
 */
sealed class ImportState {
    data object Idle : ImportState()
    data object SelectingType : ImportState()
    data object CapturingImage : ImportState()
    data class Processing(val progress: Float, val step: String) : ImportState()
    data class ReviewingData(
        val ocrResult: OcrResult,
        val editableRows: List<EditableExerciseRow>
    ) : ImportState()
    data class Error(val message: String) : ImportState()
    data object Success : ImportState()
}

/**
 * Riga editabile per la UI di revisione
 */
data class EditableExerciseRow(
    val id: String,
    val exerciseName: String,
    val sets: String,
    val reps: String,
    val weight: String,
    val rest: String,
    val notes: String,
    val confidence: Float,
    val hasError: Boolean,
    val errorMessage: String?,
    val imageSnippet: Bitmap? = null        // Ritaglio dell'immagine originale
) {
    companion object {
        fun fromParsed(parsed: ParsedExerciseRow, snippet: Bitmap? = null): EditableExerciseRow {
            val hasError = parsed.confidence < 0.8f || parsed.errors.isNotEmpty()
            val errorMsg = parsed.errors.firstOrNull()?.message
                ?: if (parsed.confidence < 0.8f) "Riconoscimento incerto" else null

            return EditableExerciseRow(
                id = parsed.id,
                exerciseName = parsed.exerciseName,
                sets = parsed.sets,
                reps = parsed.reps,
                weight = parsed.weight ?: "",
                rest = parsed.rest ?: "",
                notes = parsed.notes ?: "",
                confidence = parsed.confidence,
                hasError = hasError,
                errorMessage = errorMsg,
                imageSnippet = snippet
            )
        }
    }

    /**
     * Valida i campi e restituisce eventuali errori
     */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (exerciseName.isBlank()) {
            errors.add("Nome esercizio mancante")
        }
        if (sets.isBlank() || sets.toIntOrNull() == null) {
            errors.add("Serie non valide")
        }
        if (reps.isBlank() || reps.toIntOrNull() == null) {
            errors.add("Ripetizioni non valide")
        }

        return errors
    }

    fun isValid(): Boolean = validate().isEmpty()
}

/**
 * Pattern comuni per il parsing del testo OCR
 * Supporta formati comuni come EvolutionFit, schede palestra, Excel, etc.
 *
 * Formati supportati:
 * - Standard: "4x12", "4 x 12", "4X12"
 * - Piramidale: "5-3-2", "8-6-4"
 * - EvolutionFit: "NOME ESERCIZIO Serie e rip. (Rec. : 02.00) 5-3-2"
 * - Tabella: colonne separate (Nome, Serie, Rip, Peso, Rec)
 */
object OcrPatterns {

    // ======================= PATTERNS DI BASE =======================

    // Pattern per "4x12", "4 x 12", "4X12", "3×8", "4 X 12"
    val SETS_REPS_PATTERN = Regex("""(\d{1,2})\s*[xX×]\s*(\d{1,3})""")

    // Pattern per piramidali "5-3-2", "8-6-4", "10 - 8 - 6"
    val PYRAMID_PATTERN = Regex("""(\d{1,2})\s*[-–]\s*(\d{1,2})\s*[-–]\s*(\d{1,2})""")

    // Pattern per peso "50kg", "50 kg", "50Kg", "50 Kg", "50.5kg"
    val WEIGHT_PATTERN = Regex("""(\d+(?:[.,]\d+)?)\s*(?:kg|Kg|KG|chilogrammi?)""", RegexOption.IGNORE_CASE)

    // Pattern per recupero in vari formati
    // "90s", "90 sec", "90''", "1:30", "02.00", "Rec. : 02.00", "Rec.: 1'30", "(Rec. : 02.00)"
    val REST_PATTERN = Regex(
        """(?:Rec\.?\s*:?\s*)?(\d{1,2})[:.'](\d{2})(?:['"])?|(\d+)\s*(?:s|sec(?:ondi)?|[']{2})""",
        RegexOption.IGNORE_CASE
    )

    // Pattern per "Rec. : 02.00" o "(Rec. : 02.00)" - formato EvolutionFit
    val REST_EVOLUTION_PATTERN = Regex(
        """\(?Rec\.?\s*:?\s*(\d{1,2})[.:](\d{2})\)?""",
        RegexOption.IGNORE_CASE
    )

    // Pattern per "Serie e rip." format (EvolutionFit style)
    val SERIE_RIP_PATTERN = Regex("""Serie\s+e\s+rip\.?""", RegexOption.IGNORE_CASE)

    // Pattern per numeri isolati (potenziali serie o ripetizioni)
    val ISOLATED_NUMBER_PATTERN = Regex("""(?<!\d)(\d{1,2})(?!\d)""")

    // ======================= FUNZIONI DI ESTRAZIONE =======================

    /**
     * Estrae serie e ripetizioni da una stringa.
     * Prova diversi pattern in ordine di specificità.
     */
    fun extractSetsReps(text: String): Pair<String, String>? {
        // 1. Prova pattern standard "3x8", "3X8", "3 x 8"
        val standardMatch = SETS_REPS_PATTERN.find(text)
        if (standardMatch != null) {
            return Pair(standardMatch.groupValues[1], standardMatch.groupValues[2])
        }

        // 2. Prova pattern piramidale "5-3-2" -> (3 serie, prima rip)
        val pyramidMatch = PYRAMID_PATTERN.find(text)
        if (pyramidMatch != null) {
            val rep1 = pyramidMatch.groupValues[1].toIntOrNull() ?: 0
            // Per piramidali: 3 serie, reps = prima cifra (max reps)
            return Pair("3", rep1.toString())
        }

        // 3. Cerca numeri separati che potrebbero essere serie e reps
        // Es: "Panca piana 4 12" o linee di tabella
        val numbers = ISOLATED_NUMBER_PATTERN.findAll(text)
            .map { it.groupValues[1].toInt() }
            .filter { it in 1..30 } // Filtra numeri ragionevoli per serie/reps
            .toList()

        if (numbers.size >= 2) {
            val sets = numbers[0]
            val reps = numbers[1]
            // Validazione: serie tipicamente 1-10, reps 1-30
            if (sets in 1..10 && reps in 1..30) {
                return Pair(sets.toString(), reps.toString())
            }
        }

        return null
    }

    /**
     * Estrae il peso da una stringa
     */
    fun extractWeight(text: String): String? {
        val match = WEIGHT_PATTERN.find(text)
        return match?.groupValues?.get(1)?.let {
            // Normalizza la virgola in punto
            "${it.replace(',', '.')}kg"
        }
    }

    /**
     * Estrae il tempo di recupero da una stringa.
     * Supporta formati: "90s", "1:30", "02.00", "Rec.: 02.00"
     * Ritorna sempre in formato secondi "XXXs"
     */
    fun extractRest(text: String): String? {
        // Prima prova il formato EvolutionFit "Rec. : 02.00"
        val evolutionMatch = REST_EVOLUTION_PATTERN.find(text)
        if (evolutionMatch != null) {
            val minutes = evolutionMatch.groupValues[1].toIntOrNull() ?: 0
            val seconds = evolutionMatch.groupValues[2].toIntOrNull() ?: 0
            val totalSeconds = minutes * 60 + seconds
            if (totalSeconds > 0) return "${totalSeconds}s"
        }

        // Poi prova pattern generici
        val match = REST_PATTERN.find(text)
        return match?.let {
            when {
                // Formato "1:30" o "02.00" (minuti:secondi)
                it.groupValues[1].isNotEmpty() && it.groupValues[2].isNotEmpty() -> {
                    val minutes = it.groupValues[1].toIntOrNull() ?: 0
                    val seconds = it.groupValues[2].toIntOrNull() ?: 0
                    val totalSeconds = minutes * 60 + seconds
                    if (totalSeconds > 0) "${totalSeconds}s" else null
                }
                // Formato "90s" o "90 sec"
                it.groupValues[3].isNotEmpty() -> {
                    val secs = it.groupValues[3].toIntOrNull() ?: 0
                    if (secs > 0) "${secs}s" else null
                }
                else -> null
            }
        }
    }

    /**
     * Estrae il nome dell'esercizio pulendo i pattern numerici.
     * Mantiene solo il nome leggibile dell'esercizio.
     */
    fun cleanExerciseName(text: String): String {
        var cleaned = text

        // Rimuovi tutti i pattern numerici e di formato
        cleaned = cleaned
            .replace(SETS_REPS_PATTERN, " ")
            .replace(PYRAMID_PATTERN, " ")
            .replace(WEIGHT_PATTERN, " ")
            .replace(REST_PATTERN, " ")
            .replace(REST_EVOLUTION_PATTERN, " ")
            .replace(SERIE_RIP_PATTERN, " ")

        // Rimuovi pattern specifici
        cleaned = cleaned
            // Rimuovi parentesi con contenuto numerico "(Rec. : 02.00)"
            .replace(Regex("""\([^)]*(?:Rec|rec|\d)[^)]*\)""", RegexOption.IGNORE_CASE), " ")
            // Rimuovi numeri di riga "1 -", "2.", "1)"
            .replace(Regex("""^\s*\d+\s*[-–.)]\s*"""), "")
            // Rimuovi header/istruzioni
            .replace(Regex("""MINUTI?\s*(DI\s*)?RISCALDAMENTO""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""DA\s+ESEGUIRE.*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""PAUSE\s+DI.*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""STRETCHING.*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""DEFATICAMENTO.*$""", RegexOption.IGNORE_CASE), "")
            // Rimuovi "Serie:" "Rip:" etc.
            .replace(Regex("""(?:Serie|Rip(?:etizioni)?|Sets?|Reps?)\s*:?\s*\d*""", RegexOption.IGNORE_CASE), " ")
            // Rimuovi parentesi vuote
            .replace(Regex("""\(\s*\)"""), "")
            // Rimuovi caratteri speciali (mantieni accenti italiani)
            .replace(Regex("""[^\w\sàèéìòùÀÈÉÌÒÙ'-]"""), " ")
            // Normalizza spazi multipli
            .replace(Regex("""\s+"""), " ")

        return cleaned.trim()
    }

    /**
     * Estrae il gruppo muscolare dal testo
     */
    fun extractMuscleGroup(text: String): String? {
        val groups = mapOf(
            "Pettorali" to listOf("petto", "pettoral", "chest", "panca"),
            "Bicipiti" to listOf("bicip", "curl", "braccio"),
            "Tricipiti" to listOf("tricip", "french press", "pushdown"),
            "Dorsali" to listOf("dorsal", "schiena", "lat", "back", "remator"),
            "Spalle" to listOf("spall", "deltoid", "shoulder", "military"),
            "Quadricipiti" to listOf("quadricip", "gambe", "leg press", "squat", "leg extension"),
            "Femorali" to listOf("femoral", "leg curl", "stacco"),
            "Polpacci" to listOf("polpacc", "calf"),
            "Addominali" to listOf("addominal", "crunch", "plank", "abs"),
            "Glutei" to listOf("glute", "hip thrust"),
            "Trapezio" to listOf("trapez", "scrollat"),
            "Cardio" to listOf("cardio", "tapis", "cyclette", "corsa")
        )

        val lowerText = text.lowercase()
        for ((group, keywords) in groups) {
            if (keywords.any { lowerText.contains(it) }) {
                return group
            }
        }
        return null
    }

    /**
     * Verifica se una linea è un header di tabella
     */
    fun isHeaderLine(text: String): Boolean {
        val headerKeywords = listOf(
            "esercizio", "exercise", "serie", "sets", "ripetizioni", "reps",
            "peso", "weight", "recupero", "rest", "note", "nome", "giorno",
            "day", "settimana", "week", "allenamento", "workout"
        )
        val lowerText = text.lowercase()
        return headerKeywords.count { lowerText.contains(it) } >= 2
    }

    /**
     * Verifica se una linea è un'istruzione (non un esercizio)
     */
    fun isInstructionLine(text: String): Boolean {
        val instructionPatterns = listOf(
            Regex("""riscaldamento""", RegexOption.IGNORE_CASE),
            Regex("""defaticamento""", RegexOption.IGNORE_CASE),
            Regex("""stretching""", RegexOption.IGNORE_CASE),
            Regex("""minuti?\s+di""", RegexOption.IGNORE_CASE),
            Regex("""riposo\s+tra""", RegexOption.IGNORE_CASE),
            Regex("""note\s*:""", RegexOption.IGNORE_CASE),
            Regex("""istruzioni""", RegexOption.IGNORE_CASE)
        )
        return instructionPatterns.any { it.containsMatchIn(text) }
    }

    /**
     * Parse di una riga nel formato EvolutionFit:
     * "SPINTE PANCA PIANA BILANCERE Serie e rip. (Rec. : 02.00) 5-3-2"
     */
    fun parseEvolutionFitLine(text: String): ParsedExerciseRow? {
        // Controlla se contiene "Serie e rip."
        if (!SERIE_RIP_PATTERN.containsMatchIn(text)) {
            return null
        }

        // Estrai il recupero dal formato "(Rec. : 02.00)"
        val rest = extractRest(text)

        // Estrai serie/reps (pattern piramidale o standard)
        val setsReps = extractSetsReps(text)

        // Il nome è tutto prima di "Serie e rip."
        val nameMatch = Regex("""^(.+?)\s*Serie\s+e\s+rip""", RegexOption.IGNORE_CASE).find(text)
        val exerciseName = nameMatch?.groupValues?.get(1)
            ?.replace(Regex("""^\d+\s*[-–.)]\s*"""), "") // Rimuovi numero riga
            ?.trim()

        if (exerciseName.isNullOrBlank()) {
            return null
        }

        return ParsedExerciseRow(
            rawText = text,
            exerciseName = exerciseName,
            sets = setsReps?.first ?: "",
            reps = setsReps?.second ?: "",
            weight = extractWeight(text),
            rest = rest,
            confidence = if (setsReps != null) 0.9f else 0.6f,
            errors = if (setsReps == null) {
                listOf(ValidationError("setsReps", "Serie/ripetizioni non riconosciute"))
            } else emptyList()
        )
    }
}

/**
 * Database di esercizi comuni per il fuzzy matching
 */
object ExerciseDatabase {
    val commonExercises = listOf(
        "Panca Piana",
        "Panca Inclinata",
        "Panca Declinata",
        "Distensioni Manubri",
        "Croci Manubri",
        "Croci Cavi",
        "Push Up",
        "Dip",
        "Squat",
        "Leg Press",
        "Affondi",
        "Stacchi",
        "Stacchi Rumeni",
        "Leg Curl",
        "Leg Extension",
        "Calf Raise",
        "Trazioni",
        "Lat Machine",
        "Pulley",
        "Rematore",
        "Rematore Manubrio",
        "Scrollate",
        "Military Press",
        "Shoulder Press",
        "Alzate Laterali",
        "Alzate Frontali",
        "Alzate Posteriori",
        "Face Pull",
        "Curl Bilanciere",
        "Curl Manubri",
        "Curl Martello",
        "Curl Concentrato",
        "French Press",
        "Tricipiti Cavi",
        "Tricipiti Manubrio",
        "Skull Crusher",
        "Crunch",
        "Plank",
        "Russian Twist",
        "Leg Raise"
    )

    /**
     * Trova il match più simile per un nome esercizio
     * Usa la distanza di Levenshtein normalizzata
     */
    fun findBestMatch(input: String): Pair<String, Float>? {
        if (input.isBlank()) return null

        val inputLower = input.lowercase().trim()
        var bestMatch: String? = null
        var bestScore = 0f

        for (exercise in commonExercises) {
            val exerciseLower = exercise.lowercase()
            val score = calculateSimilarity(inputLower, exerciseLower)
            if (score > bestScore) {
                bestScore = score
                bestMatch = exercise
            }
        }

        return bestMatch?.let { Pair(it, bestScore) }
    }

    /**
     * Calcola la similarità tra due stringhe (0-1)
     */
    private fun calculateSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1f
        if (s1.isEmpty() || s2.isEmpty()) return 0f

        // Controlla se una contiene l'altra
        if (s1.contains(s2) || s2.contains(s1)) {
            return 0.85f
        }

        // Distanza di Levenshtein
        val distance = levenshteinDistance(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        return 1f - (distance.toFloat() / maxLen)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }
}
