package com.app.fityo.import_scheda

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

/**
 * Helper per estrarre esercizi da file PDF usando PdfRenderer + ML Kit OCR + Gemma AI.
 *
 * Pipeline:
 * 1. PdfRenderer - Renderizza ogni pagina come immagine ad alta risoluzione (300 DPI)
 * 2. ML Kit OCR - Estrae il testo dalle immagini
 * 3. Gemma AI (opzionale) - Corregge errori e comprende il contesto fitness
 * 4. Pattern matching - Parsing strutturato dei dati esercizi
 *
 * Supporta PDF multi-pagina con unione intelligente dei risultati.
 */
class PdfOcrHelper(
    private val context: Context,
    private val useGemma: Boolean = true
) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val exerciseParser = WorkoutOcrHelper(context, useGemma)
    private var gemmaHelper: GemmaLlmHelper? = if (useGemma) GemmaLlmHelper.getInstance(context) else null

    companion object {
        private const val TAG = "PdfOcrHelper"
        private const val PDF_RENDER_DPI = 300 // Alta risoluzione per OCR migliore
    }

    /**
     * Inizializza Gemma AI per parsing intelligente
     */
    suspend fun initializeGemma(): Result<Unit> {
        return gemmaHelper?.initializeModelWithFallback(GemmaLlmHelper.GemmaProfile.OCR)
            ?: Result.failure(Exception("Gemma non configurato"))
    }

    /**
     * Verifica se Gemma Ã¨ pronto
     */
    fun isGemmaReady(): Boolean = gemmaHelper?.isReady() == true

    /**
     * Risultato dell'elaborazione PDF
     */
    data class PdfImportResult(
        val success: Boolean,
        val pageCount: Int,
        val allParsedRows: List<ParsedExerciseRow>,
        val pageResults: List<PageResult>,
        val errorMessage: String? = null
    )

    /**
     * Risultato per singola pagina
     */
    data class PageResult(
        val pageNumber: Int,
        val rawText: String,
        val parsedRows: List<ParsedExerciseRow>,
        val bitmap: Bitmap? = null
    )

    /**
     * Processa un PDF ed estrae gli esercizi da tutte le pagine.
     */
    suspend fun processPdf(uri: Uri): PdfImportResult {
        return withContext(Dispatchers.IO) {
            if (useGemma && !isGemmaReady() && gemmaHelper?.isModelAvailable() == true) {
                Log.d(TAG, "Initializing Gemma for PDF parsing...")
                val initResult = gemmaHelper?.initializeModelWithFallback(GemmaLlmHelper.GemmaProfile.OCR)
                if (initResult?.isFailure == true) {
                    Log.w(TAG, "Gemma init failed: ${initResult.exceptionOrNull()?.message}")
                }
            }
            var pfd: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null

            try {
                // Apri il PDF
                pfd = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: return@withContext PdfImportResult(
                        success = false,
                        pageCount = 0,
                        allParsedRows = emptyList(),
                        pageResults = emptyList(),
                        errorMessage = "Impossibile aprire il file PDF"
                    )

                renderer = PdfRenderer(pfd)
                val pageCount = renderer.pageCount

                Log.d(TAG, "PDF aperto con $pageCount pagine")

                val pageResults = mutableListOf<PageResult>()
                val allParsedRows = mutableListOf<ParsedExerciseRow>()

                // Processa ogni pagina
                for (pageIndex in 0 until pageCount) {
                    val pageResult = processPage(renderer, pageIndex)
                    pageResults.add(pageResult)
                    allParsedRows.addAll(pageResult.parsedRows)

                    Log.d(TAG, "Pagina ${pageIndex + 1}: trovati ${pageResult.parsedRows.size} esercizi")
                }

                PdfImportResult(
                    success = allParsedRows.isNotEmpty(),
                    pageCount = pageCount,
                    allParsedRows = allParsedRows,
                    pageResults = pageResults,
                    errorMessage = if (allParsedRows.isEmpty()) "Nessun esercizio trovato nel PDF" else null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante l'elaborazione del PDF", e)
                PdfImportResult(
                    success = false,
                    pageCount = 0,
                    allParsedRows = emptyList(),
                    pageResults = emptyList(),
                    errorMessage = "Errore: ${e.message}"
                )
            } finally {
                renderer?.close()
                pfd?.close()
            }
        }
    }

    /**
     * Processa una singola pagina del PDF.
     */
    private suspend fun processPage(renderer: PdfRenderer, pageIndex: Int): PageResult {
        var page: PdfRenderer.Page? = null

        try {
            page = renderer.openPage(pageIndex)

            // Calcola dimensioni ad alta risoluzione per OCR
            val scale = PDF_RENDER_DPI / 72f // 72 DPI Ã¨ la risoluzione base PDF
            val width = (page.width * scale).toInt()
            val height = (page.height * scale).toInt()

            // Crea bitmap con sfondo bianco
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            // Renderizza la pagina
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            page = null

            // Esegui OCR sulla pagina
            val text = recognizeText(bitmap)
            val rawText = text?.text ?: ""

            Log.d(TAG, "Pagina ${pageIndex + 1} OCR: ${rawText.take(200)}...")

            // Parsa gli esercizi dal testo
            val parsedRows = parseTextToExercises(text, rawText)

            return PageResult(
                pageNumber = pageIndex + 1,
                rawText = rawText,
                parsedRows = parsedRows,
                bitmap = bitmap
            )
        } finally {
            page?.close()
        }
    }

    /**
     * Esegue OCR su una bitmap.
     */
    private suspend fun recognizeText(bitmap: Bitmap): Text? =
        suspendCancellableCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    continuation.resume(text)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "OCR fallito", e)
                    continuation.resume(null)
                }
        }

    /**
     * Converte il testo OCR in lista di esercizi.
     * Usa Gemma AI se disponibile per parsing intelligente.
     */
    private suspend fun parseTextToExercises(text: Text?, rawText: String): List<ParsedExerciseRow> {
        // Se Gemma Ã¨ pronto, usa AI per parsing intelligente
        if (isGemmaReady() && rawText.isNotBlank()) {
            Log.d(TAG, "Using Gemma AI for PDF parsing...")
            val gemmaResult = gemmaHelper?.analyzeOcrText(rawText)
            if (gemmaResult?.isSuccess == true) {
                val result = gemmaResult.getOrThrow()
                if (result.exercises.isNotEmpty()) {
                    Log.d(TAG, "Gemma found ${result.exercises.size} exercises")
                    return result.exercises.map { ex ->
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
                            errors = emptyList()
                        )
                    }
                }
            }
            Log.w(TAG, "Gemma failed or returned empty, falling back to traditional parsing")
        }

        // Fallback: parsing tradizionale
        val exercises = mutableListOf<ParsedExerciseRow>()

        // Prima prova formato EvolutionFit
        val lines = rawText.split("\n", "\r\n").map { it.trim() }.filter { it.isNotEmpty() }
        for (line in lines) {
            val evolutionEx = OcrPatterns.parseEvolutionFitLine(line)
            if (evolutionEx != null) {
                exercises.add(evolutionEx)
            }
        }

        // Poi prova con i blocchi strutturati di ML Kit
        if (exercises.isEmpty()) {
            text?.textBlocks?.forEach { block ->
                block.lines.forEach { line ->
                    val lineText = line.text.trim()
                    val exercise = parseLineAsExercise(lineText, line.boundingBox)
                    if (exercise != null) {
                        exercises.add(exercise)
                    }
                }
            }
        }

        // Se non trova nulla, prova parsing aggressivo
        if (exercises.isEmpty()) {
            exercises.addAll(parseTextAggressively(rawText))
        }

        return exercises.distinctBy { it.exerciseName.lowercase() }
    }

    /**
     * Tenta di parsare una linea come esercizio.
     * Usa OcrPatterns.cleanExerciseName per pulizia intelligente.
     */
    private fun parseLineAsExercise(lineText: String, boundingBox: android.graphics.Rect?): ParsedExerciseRow? {
        if (lineText.length < 3) return null
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

        // Usa cleanExerciseName per pulizia intelligente
        val exerciseName = OcrPatterns.cleanExerciseName(lineText)

        if (exerciseName.length < 2 && sets.isEmpty() && reps.isEmpty()) {
            return null
        }

        // Calcola confidenza
        var confidence = 1.0f

        if (sets.isEmpty() || sets.toIntOrNull() == null) {
            confidence -= 0.25f
            errors.add(ValidationError("sets", "Serie non riconosciute"))
        }

        if (reps.isEmpty() || reps.toIntOrNull() == null) {
            confidence -= 0.25f
            errors.add(ValidationError("reps", "Ripetizioni non riconosciute"))
        }

        // Fuzzy matching per nome esercizio
        if (exerciseName.length >= 3) {
            val match = ExerciseDatabase.findBestMatch(exerciseName)
            if (match != null && match.second > 0.7f && match.second < 0.95f) {
                errors.add(ValidationError("exerciseName", "Nome simile: '${match.first}'?"))
                confidence *= match.second
            }
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
     * Usa OcrPatterns.cleanExerciseName per pulizia intelligente.
     */
    private fun parseTextAggressively(fullText: String): List<ParsedExerciseRow> {
        val exercises = mutableListOf<ParsedExerciseRow>()

        val lines = fullText.split("\n", "\r\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for ((index, line) in lines.withIndex()) {
            if (OcrPatterns.isHeaderLine(line)) continue
            if (OcrPatterns.isInstructionLine(line)) continue

            val setsReps = OcrPatterns.extractSetsReps(line)
            if (setsReps != null) {
                var exerciseName = OcrPatterns.cleanExerciseName(line)

                // Se nome vuoto, prova linea precedente
                if (exerciseName.length < 2 && index > 0) {
                    val prevLine = lines[index - 1]
                    if (!OcrPatterns.isHeaderLine(prevLine) && !OcrPatterns.isInstructionLine(prevLine)) {
                        exerciseName = OcrPatterns.cleanExerciseName(prevLine)
                    }
                }

                if (exerciseName.length >= 2) {
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
     * Pulisce le risorse.
     */
    fun close() {
        recognizer.close()
        exerciseParser.close()
        gemmaHelper?.close()
        gemmaHelper = null
    }
}

