package com.app.fityo.import_scheda

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.abs

/**
 * Helper per il riconoscimento OCR di schede di allenamento.
 *
 * Flusso INTELLIGENTE (con Gemma):
 * 1. Pre-processing immagine (grayscale, contrasto)
 * 2. OCR con ML Kit Text Recognition
 * 3. [OPZIONALE] Correzione intelligente con Gemma 2B LLM
 * 4. Parsing multi-strategia delle righe
 * 5. Validazione e assegnazione confidenza
 *
 * Supporta formati:
 * - EvolutionFit: "NOME Serie e rip. (Rec. : 02.00) 5-3-2"
 * - Standard: "Panca Piana 4x12 50kg"
 * - Tabellare: colonne separate
 * - Piramidale: "5-3-2", "8-6-4"
 *
 * Con Gemma attivo:
 * - Correzione automatica errori OCR
 * - Comprensione semantica del contesto fitness
 * - Validazione intelligente (es: "400kg" → "40kg")
 */
class WorkoutOcrHelper(
    private val context: android.content.Context? = null,
    private val useGemma: Boolean = false
) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var gemmaHelper: GemmaLlmHelper? = null

    companion object {
        private const val TAG = "WorkoutOcrHelper"
        private const val MIN_LINE_LENGTH = 3
        private const val MIN_EXERCISE_NAME_LENGTH = 2
    }

    init {
        if (useGemma && context != null) {
            gemmaHelper = GemmaLlmHelper.getInstance(context)
        }
    }

    /**
     * Verifica se Gemma è pronto per l'uso
     */
    fun isGemmaReady(): Boolean = gemmaHelper?.isReady() == true

    /**
     * Verifica se il modello Gemma è disponibile (file esiste)
     */
    fun isGemmaAvailable(): Boolean = gemmaHelper?.isModelAvailable() == true

    /**
     * Inizializza Gemma (operazione asincrona, ~5-10 secondi)
     */
    suspend fun initializeGemma(): Result<Unit> {
        return gemmaHelper?.initializeModel() ?: Result.failure(Exception("Gemma non configurato"))
    }

    /**
     * Processa un'immagine ed estrae gli esercizi.
     * Se Gemma è disponibile e pronto, usa l'intelligenza AI per correggere gli errori.
     */
    suspend fun processImage(bitmap: Bitmap): OcrResult {
        val startTime = System.currentTimeMillis()

        return try {
            // 1. Pre-processing
            val processedBitmap = preprocessImage(bitmap)

            // 2. OCR con ML Kit
            val text = recognizeText(processedBitmap)
            if (text == null) {
                return OcrResult(
                    success = false,
                    rawText = "",
                    parsedRows = emptyList(),
                    originalImage = bitmap,
                    errorMessage = "Impossibile riconoscere il testo nell'immagine"
                )
            }

            val rawText = text.text
            Log.d(TAG, "Raw OCR text:\n$rawText")

            // 3. Se Gemma è pronto, usa AI per parsing intelligente
            val parsedRows = if (isGemmaReady()) {
                Log.d(TAG, "Using Gemma AI for intelligent parsing...")
                parseWithGemma(rawText, text, bitmap)
            } else {
                // Fallback: parsing tradizionale multi-strategia
                Log.d(TAG, "Using traditional multi-strategy parsing...")
                parseTextMultiStrategy(text, rawText, bitmap)
            }

            val processingTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "Found ${parsedRows.size} exercises in ${processingTime}ms (Gemma: ${isGemmaReady()})")

            OcrResult(
                success = parsedRows.isNotEmpty(),
                rawText = rawText,
                parsedRows = parsedRows,
                originalImage = bitmap,
                errorMessage = if (parsedRows.isEmpty()) "Nessun esercizio rilevato. Prova con una foto più nitida." else null,
                processingTimeMs = processingTime
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            OcrResult(
                success = false,
                rawText = "",
                parsedRows = emptyList(),
                originalImage = bitmap,
                errorMessage = "Errore durante l'elaborazione: ${e.message}"
            )
        }
    }

    /**
     * Parsing intelligente con Gemma AI.
     * Combina OCR + LLM per risultati di alta qualità.
     */
    private suspend fun parseWithGemma(
        rawText: String,
        mlKitText: Text,
        originalBitmap: Bitmap
    ): List<ParsedExerciseRow> {
        val gemma = gemmaHelper ?: return parseTextMultiStrategy(mlKitText, rawText, originalBitmap)

        return try {
            val result = gemma.analyzeOcrText(rawText)
            if (result.isSuccess) {
                val gemmaResult = result.getOrThrow()
                Log.d(TAG, "Gemma found ${gemmaResult.exercises.size} exercises with ${gemmaResult.corrections.size} corrections")

                // Converti risultati Gemma in ParsedExerciseRow
                gemmaResult.exercises.map { ex ->
                    ParsedExerciseRow(
                        rawText = ex.originalText,
                        exerciseName = ex.name,
                        sets = ex.sets.toString(),
                        reps = ex.reps.toString(),
                        weight = ex.weight,
                        rest = ex.rest,
                        notes = ex.notes,
                        confidence = ex.confidence,
                        boundingBox = null,
                        errors = emptyList() // Gemma ha già corretto gli errori
                    )
                }.ifEmpty {
                    // Fallback se Gemma non trova nulla
                    Log.w(TAG, "Gemma returned empty, falling back to traditional parsing")
                    parseTextMultiStrategy(mlKitText, rawText, originalBitmap)
                }
            } else {
                Log.e(TAG, "Gemma failed: ${result.exceptionOrNull()?.message}")
                parseTextMultiStrategy(mlKitText, rawText, originalBitmap)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error using Gemma, falling back", e)
            parseTextMultiStrategy(mlKitText, rawText, originalBitmap)
        }
    }

    /**
     * Pre-processa l'immagine per migliorare l'OCR.
     */
    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        // Converti in grayscale e aumenta contrasto
        val grayscale = toGrayscale(bitmap)
        return increaseContrast(grayscale, 1.5f)
    }

    /**
     * Converte l'immagine in scala di grigi.
     */
    private fun toGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()

        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return grayscaleBitmap
    }

    /**
     * Aumenta il contrasto dell'immagine.
     */
    private fun increaseContrast(bitmap: Bitmap, contrast: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val contrastBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(contrastBitmap)
        val paint = Paint()

        val translate = (-.5f * contrast + .5f) * 255f
        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return contrastBitmap
    }

    /**
     * Esegue il riconoscimento OCR sull'immagine.
     */
    private suspend fun recognizeText(bitmap: Bitmap): Text? =
        suspendCancellableCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    continuation.resume(text)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Text recognition failed", e)
                    continuation.resume(null)
                }
        }

    /**
     * Parsing multi-strategia: prova diverse strategie in ordine di specificità.
     */
    private fun parseTextMultiStrategy(text: Text, rawText: String, originalBitmap: Bitmap): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()
        val processedLines = mutableSetOf<String>() // Evita duplicati

        // Strategia 0: PRIORITARIA - Parsing tabellare per posizione Y
        Log.d(TAG, "Trying positional table parsing...")
        val positionalExercises = parseTableByPosition(text, processedLines)
        positionalExercises.forEach { ex ->
            if (ex.exerciseName !in processedLines) {
                exercises.add(ex)
                processedLines.add(ex.exerciseName)
            }
        }
        Log.d(TAG, "Positional parsing found: ${positionalExercises.size}")

        // Strategia 1: Prova formato EvolutionFit "Serie e rip."
        Log.d(TAG, "Trying EvolutionFit format...")
        val evolutionExercises = parseEvolutionFitFormat(rawText)
        evolutionExercises.forEach { ex ->
            if (ex.exerciseName !in processedLines) {
                exercises.add(ex)
                processedLines.add(ex.exerciseName)
            }
        }
        Log.d(TAG, "EvolutionFit found: ${evolutionExercises.size}")

        // Strategia 2: Parsing per blocchi ML Kit
        Log.d(TAG, "Trying ML Kit blocks...")
        for (block in text.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text.trim()
                if (lineText.length < MIN_LINE_LENGTH) continue
                if (OcrPatterns.isHeaderLine(lineText)) continue
                if (OcrPatterns.isInstructionLine(lineText)) continue
                if (lineText in processedLines) continue

                val exercise = parseLineAsExercise(lineText, line.boundingBox)
                if (exercise != null && exercise.exerciseName !in processedLines) {
                    exercises.add(exercise)
                    processedLines.add(exercise.exerciseName)
                    Log.d(TAG, "ML Kit found: ${exercise.exerciseName} (${exercise.sets}x${exercise.reps})")
                }
            }
        }

        // Strategia 3: Parsing aggressivo per linea
        if (exercises.size < 3) {
            Log.d(TAG, "Trying aggressive line parsing...")
            val aggressiveExercises = parseTextAggressively(rawText, processedLines)
            exercises.addAll(aggressiveExercises)
            Log.d(TAG, "Aggressive found: ${aggressiveExercises.size}")
        }

        // Strategia 4: Se ancora poco, prova parsing tabellare
        if (exercises.size < 2) {
            Log.d(TAG, "Trying table parsing...")
            val tableExercises = parseAsTable(rawText, processedLines)
            exercises.addAll(tableExercises)
            Log.d(TAG, "Table found: ${tableExercises.size}")
        }

        return exercises
            .filter { it.exerciseName.length >= MIN_EXERCISE_NAME_LENGTH }
            .distinctBy { it.exerciseName.lowercase() }
    }

    /**
     * Parse formato EvolutionFit: "NOME Serie e rip. (Rec. : 02.00) 5-3-2"
     */

    /**
     * Parsing tabellare basato sulla posizione Y degli elementi.
     * Raggruppa elementi sulla stessa riga della tabella usando le coordinate dei bounding box.
     */
    private fun parseTableByPosition(text: Text, alreadyProcessed: Set<String>): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()

        // Classe per memorizzare elemento con posizione
        data class TextElement(
            val text: String,
            val centerY: Int,
            val left: Int,
            val boundingBox: Rect?
        )

        val elements = mutableListOf<TextElement>()

        // Raccogli tutti gli elementi con posizione
        for (block in text.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val box = element.boundingBox
                    if (box != null && element.text.isNotBlank()) {
                        elements.add(TextElement(
                            text = element.text.trim(),
                            centerY = (box.top + box.bottom) / 2,
                            left = box.left,
                            boundingBox = box
                        ))
                    }
                }
            }
        }

        if (elements.isEmpty()) return exercises

        // Calcola altezza media per tolleranza
        val avgHeight = elements.mapNotNull { it.boundingBox?.height() }.average().toInt().coerceAtLeast(20)
        val yTolerance = (avgHeight * 0.6).toInt().coerceIn(15, 40)

        Log.d(TAG, "Positional parsing: \${elements.size} elements, yTolerance=\$yTolerance")

        // Raggruppa elementi per coordinata Y (stessa riga)
        val sortedByY = elements.sortedBy { it.centerY }
        val rows = mutableListOf<MutableList<TextElement>>()
        var currentRow = mutableListOf<TextElement>()
        var lastY = -1000

        for (element in sortedByY) {
            if (abs(element.centerY - lastY) > yTolerance && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
            }
            currentRow.add(element)
            lastY = element.centerY
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        Log.d(TAG, "Positional parsing: found \${rows.size} rows")

        // Analizza ogni riga
        for (row in rows) {
            val sortedRow = row.sortedBy { it.left }
            val rowText = sortedRow.joinToString(" ") { it.text }

            if (OcrPatterns.isHeaderLine(rowText)) continue
            if (OcrPatterns.isInstructionLine(rowText)) continue
            if (isNonExerciseRow(sortedRow.map { it.text })) continue

            var exerciseName = ""
            var sets = ""
            var reps = ""
            var rest: String? = null
            var weight: String? = null
            val nameElements = mutableListOf<String>()

            for (elem in sortedRow) {
                val elemText = elem.text

                val setsRepsMatch = OcrPatterns.SETS_REPS_PATTERN.find(elemText)
                if (setsRepsMatch != null) {
                    sets = setsRepsMatch.groupValues[1]
                    reps = setsRepsMatch.groupValues[2]
                    continue
                }

                val restMatch = OcrPatterns.extractRest(elemText)
                if (restMatch != null) {
                    rest = restMatch
                    continue
                }

                val weightMatch = OcrPatterns.extractWeight(elemText)
                if (weightMatch != null) {
                    weight = weightMatch
                    continue
                }

                if (elemText.matches(Regex("""^\d{1,2}$"""))) {
                    if (sets.isEmpty()) {
                        val num = elemText.toIntOrNull() ?: 0
                        if (num in 1..10) { sets = elemText; continue }
                    } else if (reps.isEmpty()) {
                        val num = elemText.toIntOrNull() ?: 0
                        if (num in 1..30) { reps = elemText; continue }
                    }
                }

                if (!isNumericOrPattern(elemText) && elemText.length >= 2) {
                    nameElements.add(elemText)
                }
            }

            exerciseName = nameElements.joinToString(" ").let { OcrPatterns.cleanExerciseName(it) }

            if (exerciseName.length >= MIN_EXERCISE_NAME_LENGTH &&
                exerciseName.lowercase() !in alreadyProcessed.map { it.lowercase() } &&
                (sets.isNotEmpty() || reps.isNotEmpty())) {

                Log.d(TAG, "Positional found: \$exerciseName (\${sets}x\${reps}) rest=\$rest")

                exercises.add(ParsedExerciseRow(
                    rawText = rowText,
                    exerciseName = exerciseName,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    rest = rest,
                    confidence = 0.85f,
                    boundingBox = sortedRow.firstOrNull()?.boundingBox,
                    errors = emptyList()
                ))
            }
        }

        return exercises
    }

    private fun isNonExerciseRow(texts: List<String>): Boolean {
        if (texts.isEmpty()) return true
        if (texts.all { it.matches(Regex("""^\d{1,3}$""")) || it.length < 2 }) return true
        return false
    }

    private fun isNumericOrPattern(text: String): Boolean {
        return text.matches(Regex("""^\d+$""")) ||
               text.matches(Regex("""^\d+[xX]\d+$""")) ||
               text.matches(Regex("""^\d+[-]\d+[-]\d+$""")) ||
               text.matches(Regex("""^\d+[.,]\d+$""")) ||
               text.matches(Regex("""^\d+\s*(kg|s|sec|min).*$""", RegexOption.IGNORE_CASE)) ||
               text.matches(Regex("""^[xX]$""")) ||
               text.matches(Regex("""^Rec\.?.*$""", RegexOption.IGNORE_CASE))
    }
    private fun parseEvolutionFitFormat(fullText: String): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()
        val lines = fullText.split("\n", "\r\n").map { it.trim() }.filter { it.isNotEmpty() }

        for (line in lines) {
            val exercise = OcrPatterns.parseEvolutionFitLine(line)
            if (exercise != null) {
                exercises.add(exercise)
            }
        }

        return exercises
    }

    /**
     * Tenta di parsare una singola linea come esercizio.
     */
    private fun parseLineAsExercise(lineText: String, boundingBox: Rect?): ParsedExerciseRow? {
        if (lineText.length < MIN_LINE_LENGTH) return null
        if (OcrPatterns.isHeaderLine(lineText)) return null
        if (OcrPatterns.isInstructionLine(lineText)) return null

        val errors = mutableListOf<ValidationError>()

        // Estrai serie x ripetizioni
        val setsReps = OcrPatterns.extractSetsReps(lineText)
        val sets = setsReps?.first ?: ""
        val reps = setsReps?.second ?: ""

        // Estrai peso e recupero
        val weight = OcrPatterns.extractWeight(lineText)
        val rest = OcrPatterns.extractRest(lineText)

        // Usa la funzione cleanExerciseName per estrarre il nome pulito
        val exerciseName = OcrPatterns.cleanExerciseName(lineText)

        // Se non abbiamo un nome valido, skip
        if (exerciseName.length < MIN_EXERCISE_NAME_LENGTH && sets.isEmpty() && reps.isEmpty()) {
            return null
        }

        // Calcola confidenza
        var confidence = 1.0f

        // Valida serie
        if (sets.isEmpty() || sets.toIntOrNull() == null) {
            confidence -= 0.25f
            errors.add(ValidationError("sets", "Serie non riconosciute", "Inserisci manualmente"))
        }

        // Valida ripetizioni
        if (reps.isEmpty() || reps.toIntOrNull() == null) {
            confidence -= 0.25f
            errors.add(ValidationError("reps", "Ripetizioni non riconosciute", "Inserisci manualmente"))
        }

        // Valida nome esercizio con fuzzy matching
        if (exerciseName.length >= 3) {
            val match = ExerciseDatabase.findBestMatch(exerciseName)
            if (match != null && match.second > 0.7f && match.second < 0.95f) {
                errors.add(ValidationError("exerciseName", "Nome simile: '${match.first}'?"))
                confidence *= match.second
            }
        } else if (exerciseName.isNotEmpty() && exerciseName.length < 3) {
            confidence -= 0.2f
            errors.add(ValidationError("exerciseName", "Nome troppo corto"))
        }

        // Non restituire se la confidenza è troppo bassa e mancano i dati chiave
        if (confidence < 0.4f && sets.isEmpty() && reps.isEmpty()) {
            return null
        }

        return ParsedExerciseRow(
            rawText = lineText,
            exerciseName = exerciseName,
            sets = sets,
            reps = reps,
            weight = weight,
            rest = rest,
            confidence = confidence.coerceIn(0f, 1f),
            boundingBox = boundingBox,
            errors = errors
        )
    }

    /**
     * Parsing aggressivo per testi non strutturati.
     * Cerca qualsiasi pattern che assomigli a un esercizio.
     */
    private fun parseTextAggressively(fullText: String, alreadyProcessed: Set<String>): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()
        val lines = fullText.split("\n", "\r\n").map { it.trim() }.filter { it.isNotEmpty() }

        for ((index, line) in lines.withIndex()) {
            if (OcrPatterns.isHeaderLine(line)) continue
            if (OcrPatterns.isInstructionLine(line)) continue

            val setsReps = OcrPatterns.extractSetsReps(line)
            if (setsReps != null) {
                var exerciseName = OcrPatterns.cleanExerciseName(line)

                // Se il nome è vuoto o troppo corto, prova la linea precedente
                if (exerciseName.length < MIN_EXERCISE_NAME_LENGTH && index > 0) {
                    val prevLine = lines[index - 1]
                    if (!OcrPatterns.isHeaderLine(prevLine) && !OcrPatterns.isInstructionLine(prevLine)) {
                        exerciseName = OcrPatterns.cleanExerciseName(prevLine)
                    }
                }

                // Se ancora vuoto, prova la linea successiva (se è solo un nome)
                if (exerciseName.length < MIN_EXERCISE_NAME_LENGTH && index < lines.size - 1) {
                    val nextLine = lines[index + 1]
                    if (OcrPatterns.extractSetsReps(nextLine) == null &&
                        !OcrPatterns.isHeaderLine(nextLine) &&
                        !OcrPatterns.isInstructionLine(nextLine)) {
                        exerciseName = OcrPatterns.cleanExerciseName(nextLine)
                    }
                }

                if (exerciseName.length >= MIN_EXERCISE_NAME_LENGTH &&
                    exerciseName.lowercase() !in alreadyProcessed.map { it.lowercase() }) {
                    exercises.add(
                        ParsedExerciseRow(
                            rawText = line,
                            exerciseName = exerciseName,
                            sets = setsReps.first,
                            reps = setsReps.second,
                            weight = OcrPatterns.extractWeight(line),
                            rest = OcrPatterns.extractRest(line),
                            confidence = 0.65f,
                            errors = listOf(ValidationError("parsing", "Verifica i dati estratti"))
                        )
                    )
                }
            }
        }

        return exercises
    }

    /**
     * Tenta di parsare il testo come tabella (colonne separate).
     * Utile per schede Excel o formati tabulari.
     */
    private fun parseAsTable(fullText: String, alreadyProcessed: Set<String>): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()
        val lines = fullText.split("\n", "\r\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Cerca linee con numeri separati che potrebbero essere colonne
        for (line in lines) {
            if (OcrPatterns.isHeaderLine(line)) continue
            if (OcrPatterns.isInstructionLine(line)) continue

            // Pattern per tabelle: testo seguito da numeri separati
            // Es: "Panca Piana    4    12    50"
            val tablePattern = Regex("""^([A-Za-zàèéìòùÀÈÉÌÒÙ\s]+)\s+(\d+)\s+(\d+)(?:\s+(\d+(?:[.,]\d+)?))?""")
            val match = tablePattern.find(line)

            if (match != null) {
                val name = match.groupValues[1].trim()
                val col1 = match.groupValues[2]
                val col2 = match.groupValues[3]
                val col3 = match.groupValues.getOrNull(4)?.takeIf { it.isNotEmpty() }

                if (name.length >= MIN_EXERCISE_NAME_LENGTH &&
                    name.lowercase() !in alreadyProcessed.map { it.lowercase() }) {

                    val sets = col1
                    val reps = col2
                    val weight = col3?.let { "${it.replace(',', '.')}kg" }

                    exercises.add(
                        ParsedExerciseRow(
                            rawText = line,
                            exerciseName = name,
                            sets = sets,
                            reps = reps,
                            weight = weight,
                            rest = OcrPatterns.extractRest(line),
                            confidence = 0.7f,
                            errors = listOf(ValidationError("table", "Formato tabella - verifica"))
                        )
                    )
                }
            }
        }

        return exercises
    }

    /**
     * Ritaglia un'immagine basandosi su un bounding box.
     */
    fun cropImageToBoundingBox(bitmap: Bitmap, boundingBox: Rect): Bitmap? {
        return try {
            val left = boundingBox.left.coerceIn(0, bitmap.width - 1)
            val top = boundingBox.top.coerceIn(0, bitmap.height - 1)
            val width = (boundingBox.width()).coerceIn(1, bitmap.width - left)
            val height = (boundingBox.height()).coerceIn(1, bitmap.height - top)

            Bitmap.createBitmap(bitmap, left, top, width, height)
        } catch (e: Exception) {
            Log.e(TAG, "Error cropping image", e)
            null
        }
    }

    /**
     * Pulisce le risorse.
     */
    fun close() {
        recognizer.close()
        gemmaHelper?.close()
        gemmaHelper = null
    }
}
