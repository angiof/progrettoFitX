package com.app.fityo.trueclone

import android.content.Context
import android.graphics.Bitmap
import com.app.fityo.trueclone.analysis.PoseBodyAnalyzer
import com.app.fityo.trueclone.capture.CapturedPhoto
import com.app.fityo.trueclone.capture.PhotoView
import com.app.fityo.trueclone.capture.TrueClonePhase
import com.app.fityo.trueclone.capture.TrueCloneProgress
import com.app.fityo.trueclone.comparison.MeshComparator
import com.app.fityo.trueclone.depth.MidasDepthEstimator
import com.app.fityo.trueclone.mesh.Mesh3D
import com.app.fityo.trueclone.mesh.ShapeParameters
import com.app.fityo.trueclone.mesh.SilhouetteMeshFitter
import com.app.fityo.trueclone.mesh.HumanBodyMeshGenerator
import com.app.fityo.trueclone.texture.TextureProjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * TrueClone Processor - Orchestratore principale per la creazione di avatar 3D fedeli.
 *
 * Pipeline:
 * 1. Cattura 3 foto (Front, Side, Back)
 * 2. Segmentazione corpo (MediaPipe)
 * 3. Stima profondità (MiDaS)
 * 4. Generazione mesh base (SMPL parametrico)
 * 5. Fitting silhouette (deformazione mesh)
 * 6. Proiezione texture (foto → UV)
 * 7. (Opzionale) Confronto con mesh precedente
 */
class TrueCloneProcessor(private val context: Context) {

    private val bodyGenerator = HumanBodyMeshGenerator()
    private val silhouetteFitter = SilhouetteMeshFitter()
    private val textureProjector = TextureProjector()
    private val meshComparator = MeshComparator()
    private var depthEstimator: MidasDepthEstimator? = null
    private var poseAnalyzer: PoseBodyAnalyzer? = null

    /**
     * Risultato completo della generazione TrueClone.
     */
    data class TrueCloneResult(
        val success: Boolean,
        val mesh: Mesh3D?,
        val textureMap: Bitmap?,
        val shapeParameters: ShapeParameters?,
        val zones: Map<Int, HumanBodyMeshGenerator.MuscleZone>?,
        val comparisonResult: MeshComparator.ComparisonResult?,
        val errorMessage: String? = null,
        val processingTimeMs: Long
    )

    /**
     * Genera un avatar 3D dalle foto catturate.
     * Emette progress updates durante l'elaborazione.
     */
    fun generateAvatar(
        photos: List<CapturedPhoto>,
        userHeightCm: Float,
        userWeightKg: Float,
        previousMesh: Mesh3D? = null,
        previousZones: Map<Int, HumanBodyMeshGenerator.MuscleZone>? = null
    ): Flow<Pair<TrueCloneProgress, TrueCloneResult?>> = flow {

        val startTime = System.currentTimeMillis()

        try {
            // Fase 1: Validazione input
            emit(TrueCloneProgress(
                phase = TrueClonePhase.ANALYZING_POSES,
                progress = 0.05f,
                message = "Validazione foto..."
            ) to null)

            if (photos.isEmpty()) {
                emit(errorResult("Nessuna foto fornita", startTime))
                return@flow
            }

            val frontPhoto = photos.find { it.view == PhotoView.FRONT }
            val sidePhoto = photos.find { it.view == PhotoView.SIDE }
            val backPhoto = photos.find { it.view == PhotoView.BACK }

            if (frontPhoto == null) {
                emit(errorResult("Foto frontale mancante", startTime))
                return@flow
            }

            // Fase 2: Segmentazione silhouette
            emit(TrueCloneProgress(
                phase = TrueClonePhase.EXTRACTING_SILHOUETTES,
                progress = 0.15f,
                message = "Estrazione silhouette..."
            ) to null)

            val silhouettes = withContext(Dispatchers.Default) {
                extractSilhouettes(photos)
            }

            // Fase 3: Stima profondità (opzionale ma migliora risultato)
            emit(TrueCloneProgress(
                phase = TrueClonePhase.ESTIMATING_DEPTH,
                progress = 0.30f,
                message = "Analisi profondità..."
            ) to null)

            val depthMaps = withContext(Dispatchers.Default) {
                estimateDepth(photos)
            }

            // Fase 4: Calcolo parametri forma
            emit(TrueCloneProgress(
                phase = TrueClonePhase.GENERATING_MESH,
                progress = 0.45f,
                message = "Calcolo proporzioni corpo..."
            ) to null)

            val shapeParams = withContext(Dispatchers.Default) {
                calculateShapeParameters(
                    photos, silhouettes, depthMaps,
                    userHeightCm, userWeightKg
                )
            }

            // Fase 5: Generazione mesh base
            emit(TrueCloneProgress(
                phase = TrueClonePhase.GENERATING_MESH,
                progress = 0.55f,
                message = "Generazione modello 3D..."
            ) to null)

            val generatedBody = withContext(Dispatchers.Default) {
                bodyGenerator.generateBody(shapeParams)
            }

            // Fase 6: Usa mesh generata direttamente (silhouette fitting disabilitato)
            emit(TrueCloneProgress(
                phase = TrueClonePhase.FITTING_SILHOUETTES,
                progress = 0.65f,
                message = "Ottimizzazione mesh..."
            ) to null)

            // Usa direttamente la mesh generata senza deformarla
            val fittedMesh = generatedBody.mesh

            // Fase 7: Applica colori zone
            emit(TrueCloneProgress(
                phase = TrueClonePhase.APPLYING_TEXTURE,
                progress = 0.80f,
                message = "Applicazione colori zone..."
            ) to null)

            // Applica i colori delle zone muscolari
            bodyGenerator.applyZoneColors(fittedMesh, generatedBody.zones)

            // Texture projection - skip for now, keep zone colors
            // Crea un placeholder bitmap 1x1
            val placeholderBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val textureResult = TextureProjector.TextureResult(
                textureMap = placeholderBitmap,
                uvCoordinates = emptyList(),
                coverage = 0f,
                processingTimeMs = 0
            )

            // Fase 8: Confronto (se mesh precedente disponibile)
            var comparisonResult: MeshComparator.ComparisonResult? = null

            if (previousMesh != null && previousZones != null) {
                emit(TrueCloneProgress(
                    phase = TrueClonePhase.FINALIZING,
                    progress = 0.90f,
                    message = "Calcolo progressi..."
                ) to null)

                comparisonResult = withContext(Dispatchers.Default) {
                    meshComparator.compareMeshes(fittedMesh, previousMesh, previousZones)
                }

                // Applica heatmap alla mesh
                meshComparator.applyHeatmapToMesh(fittedMesh, comparisonResult.vertexChanges)
            }
            // I colori zone sono già stati applicati sopra

            // Completato!
            emit(TrueCloneProgress(
                phase = TrueClonePhase.COMPLETE,
                progress = 1f,
                message = "Avatar completato!"
            ) to TrueCloneResult(
                success = true,
                mesh = fittedMesh,
                textureMap = textureResult.textureMap,
                shapeParameters = shapeParams,
                zones = generatedBody.zones,
                comparisonResult = comparisonResult,
                processingTimeMs = System.currentTimeMillis() - startTime
            ))

        } catch (e: Exception) {
            android.util.Log.e("TrueCloneProcessor", "Error generating avatar", e)
            emit(errorResult("Errore: ${e.message}", startTime))
        }
    }

    /**
     * Estrae le silhouette dalle foto usando segmentazione.
     */
    private suspend fun extractSilhouettes(
        photos: List<CapturedPhoto>
    ): Map<PhotoView, Bitmap> = withContext(Dispatchers.Default) {
        val silhouettes = mutableMapOf<PhotoView, Bitmap>()

        for (photo in photos) {
            // Usa la maschera di segmentazione se già presente
            val mask = photo.segmentationMask ?: createSimpleSilhouette(photo.bitmap)
            silhouettes[photo.view] = mask
        }

        silhouettes
    }

    /**
     * Crea una silhouette semplificata (fallback se segmentazione non disponibile).
     */
    private fun createSimpleSilhouette(bitmap: Bitmap): Bitmap {
        // Threshold semplice basato su luminosità
        val width = bitmap.width
        val height = bitmap.height
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val r = android.graphics.Color.red(pixel)
                val g = android.graphics.Color.green(pixel)
                val b = android.graphics.Color.blue(pixel)

                // Semplice threshold (sfondo tipicamente più chiaro/scuro uniforme)
                val variance = maxOf(r, g, b) - minOf(r, g, b)
                val isForeground = variance > 20  // Varianza colore indica corpo

                mask.setPixel(x, y, if (isForeground) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            }
        }

        return mask
    }

    /**
     * Stima la profondità usando MiDaS.
     */
    private suspend fun estimateDepth(
        photos: List<CapturedPhoto>
    ): Map<PhotoView, FloatArray> = withContext(Dispatchers.Default) {
        val depthMaps = mutableMapOf<PhotoView, FloatArray>()

        // Inizializza MiDaS se non già fatto
        if (depthEstimator == null) {
            depthEstimator = MidasDepthEstimator(context)
            val initialized = depthEstimator?.initialize() ?: false
            if (!initialized) {
                android.util.Log.w("TrueCloneProcessor", "MiDaS initialization failed, using fallback")
                return@withContext depthMaps
            }
        }

        for (photo in photos) {
            try {
                val result = depthEstimator?.estimateDepth(photo.bitmap)
                if (result != null) {
                    depthMaps[photo.view] = result.depthMap
                }
            } catch (e: Exception) {
                android.util.Log.e("TrueCloneProcessor", "Depth estimation failed for ${photo.view}", e)
            }
        }

        depthMaps
    }

    /**
     * Calcola i parametri di forma dalle foto usando MediaPipe Pose.
     * Estrae MISURAZIONI REALI dai 33 landmark del corpo.
     */
    private fun calculateShapeParameters(
        photos: List<CapturedPhoto>,
        silhouettes: Map<PhotoView, Bitmap>,
        depthMaps: Map<PhotoView, FloatArray>,
        userHeightCm: Float,
        userWeightKg: Float
    ): ShapeParameters {
        // Inizializza PoseBodyAnalyzer se necessario
        if (poseAnalyzer == null) {
            poseAnalyzer = PoseBodyAnalyzer(context)
        }

        // Estrai le bitmap dalle foto per l'analisi pose
        val bitmaps = photos.map { it.bitmap }

        // Analizza con MediaPipe Pose per ottenere misurazioni REALI
        val measurements = poseAnalyzer?.analyzeMultiplePhotos(bitmaps)

        if (measurements != null && measurements.confidence > 0.5f) {
            // Usa le misurazioni REALI estratte da MediaPipe
            android.util.Log.d("TrueCloneProcessor",
                "Pose analysis successful! Confidence: ${measurements.confidence}, " +
                "Shoulder/Hip ratio: ${measurements.shoulderToHipRatio}, " +
                "Landmarks: ${measurements.landmarksDetected}"
            )
            return poseAnalyzer!!.measurementsToShapeParameters(
                measurements, userHeightCm, userWeightKg
            )
        }

        // Fallback: usa silhouette se pose analysis fallisce
        android.util.Log.w("TrueCloneProcessor",
            "Pose analysis failed or low confidence, using silhouette fallback"
        )

        val frontSilhouette = silhouettes[PhotoView.FRONT]?.let {
            silhouetteFitter.extractSilhouette(it, 0f)
        }
        val sideSilhouette = silhouettes[PhotoView.SIDE]?.let {
            silhouetteFitter.extractSilhouette(it, 90f)
        }
        val backSilhouette = silhouettes[PhotoView.BACK]?.let {
            silhouetteFitter.extractSilhouette(it, 180f)
        }

        val baseParams = silhouetteFitter.calculateShapeFromSilhouettes(
            frontSilhouette, sideSilhouette, backSilhouette,
            userHeightCm
        )

        return baseParams.copy(
            heightCm = userHeightCm,
            weightKg = userWeightKg
        ).calculateBetaFromMeasurements()
    }

    /**
     * Adatta la mesh alle silhouette.
     */
    private fun fitMeshToSilhouettes(
        baseMesh: Mesh3D,
        silhouettes: Map<PhotoView, Bitmap>
    ): Mesh3D {
        val frontSilhouette = silhouettes[PhotoView.FRONT]?.let {
            silhouetteFitter.extractSilhouette(it, 0f)
        }
        val sideSilhouette = silhouettes[PhotoView.SIDE]?.let {
            silhouetteFitter.extractSilhouette(it, 90f)
        }
        val backSilhouette = silhouettes[PhotoView.BACK]?.let {
            silhouetteFitter.extractSilhouette(it, 180f)
        }

        val result = silhouetteFitter.fitMeshToSilhouettes(
            baseMesh, frontSilhouette, sideSilhouette, backSilhouette
        )

        android.util.Log.d("TrueCloneProcessor",
            "Mesh fitting: ${result.iterations} iterations, error=${result.finalError}, converged=${result.converged}"
        )

        return result.mesh
    }

    /**
     * Crea un risultato di errore.
     */
    private fun errorResult(message: String, startTime: Long): Pair<TrueCloneProgress, TrueCloneResult> {
        return TrueCloneProgress(
            phase = TrueClonePhase.ERROR,
            progress = 0f,
            message = message
        ) to TrueCloneResult(
            success = false,
            mesh = null,
            textureMap = null,
            shapeParameters = null,
            zones = null,
            comparisonResult = null,
            errorMessage = message,
            processingTimeMs = System.currentTimeMillis() - startTime
        )
    }

    /**
     * Pulisce le risorse.
     */
    fun cleanup() {
        depthEstimator?.close()
        depthEstimator = null
        poseAnalyzer?.close()
        poseAnalyzer = null
    }

    /**
     * Genera la heatmap di confronto tra due avatar.
     */
    fun generateComparisonHeatmap(
        currentMesh: Mesh3D,
        previousMesh: Mesh3D,
        zones: Map<Int, HumanBodyMeshGenerator.MuscleZone>
    ): MeshComparator.ComparisonResult {
        val result = meshComparator.compareMeshes(currentMesh, previousMesh, zones)
        meshComparator.applyHeatmapToMesh(currentMesh, result.vertexChanges)
        return result
    }

    /**
     * Genera il report testuale dei progressi.
     */
    fun generateProgressReport(comparisonResult: MeshComparator.ComparisonResult): String {
        return meshComparator.generateProgressReport(comparisonResult)
    }

    /**
     * Genera la legenda della heatmap.
     */
    fun generateHeatmapLegend(width: Int = 300, height: Int = 50): Bitmap {
        return meshComparator.generateHeatmapLegend(width, height)
    }
}
