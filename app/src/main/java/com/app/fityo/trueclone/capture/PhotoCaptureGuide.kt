package com.app.fityo.trueclone.capture

import android.graphics.Bitmap

/**
 * Enum per le 3 viste richieste.
 */
enum class PhotoView(val angle: Float, val displayName: String, val instruction: String) {
    FRONT(0f, "Frontale", "Posizionati di fronte alla fotocamera\nBraccia lungo i fianchi"),
    SIDE(90f, "Laterale", "Ruota di 90° a sinistra\nGuarda davanti a te"),
    BACK(180f, "Posteriore", "Ruota completamente\nMostra la schiena")
}

/**
 * Dati di una foto catturata.
 */
data class CapturedPhoto(
    val view: PhotoView,
    val bitmap: Bitmap,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val landmarks: List<LandmarkPoint>? = null,
    val segmentationMask: Bitmap? = null,
    val isValid: Boolean = true,
    val validationMessage: String? = null
)

/**
 * Punto landmark MediaPipe.
 */
data class LandmarkPoint(
    val index: Int,
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float
)

/**
 * Stato del processo di cattura.
 */
sealed class CaptureState {
    object Idle : CaptureState()
    data class Preparing(val view: PhotoView) : CaptureState()
    data class Countdown(val view: PhotoView, val secondsRemaining: Int) : CaptureState()
    data class Capturing(val view: PhotoView) : CaptureState()
    data class Processing(val view: PhotoView) : CaptureState()
    data class Review(val photo: CapturedPhoto) : CaptureState()
    data class Complete(val photos: List<CapturedPhoto>) : CaptureState()
    data class Error(val message: String) : CaptureState()
}

/**
 * Risultato della validazione pose.
 */
data class PoseValidation(
    val isValid: Boolean,
    val bodyVisible: Boolean,
    val poseCorrect: Boolean,
    val lightingGood: Boolean,
    val distanceOk: Boolean,
    val message: String,
    val suggestions: List<String> = emptyList()
)

/**
 * Guida per la cattura delle 3 foto.
 */
object PhotoCaptureGuide {

    /**
     * Sequenza delle viste da catturare.
     */
    val captureSequence = listOf(PhotoView.FRONT, PhotoView.SIDE, PhotoView.BACK)

    /**
     * Ottiene la prossima vista da catturare.
     */
    fun getNextView(capturedPhotos: List<CapturedPhoto>): PhotoView? {
        val capturedViews = capturedPhotos.map { it.view }.toSet()
        return captureSequence.firstOrNull { it !in capturedViews }
    }

    /**
     * Verifica se tutte le foto sono state catturate.
     */
    fun isComplete(capturedPhotos: List<CapturedPhoto>): Boolean {
        val capturedViews = capturedPhotos.map { it.view }.toSet()
        return captureSequence.all { it in capturedViews }
    }

    /**
     * Ottiene le istruzioni per una vista specifica.
     */
    fun getInstructions(view: PhotoView): List<String> {
        return when (view) {
            PhotoView.FRONT -> listOf(
                "Posizionati a 2-3 metri dalla fotocamera",
                "Braccia rilassate lungo i fianchi",
                "Piedi alla larghezza delle spalle",
                "Guarda dritto verso la fotocamera",
                "Indossa abiti aderenti o costume"
            )
            PhotoView.SIDE -> listOf(
                "Ruota di 90° verso sinistra",
                "Mantieni braccia lungo i fianchi",
                "Guarda dritto davanti a te",
                "Non girare la testa verso la fotocamera",
                "Mantieni la postura eretta"
            )
            PhotoView.BACK -> listOf(
                "Ruota completamente di spalle",
                "Braccia rilassate lungo i fianchi",
                "Piedi alla larghezza delle spalle",
                "Guarda dritto davanti a te",
                "Mantieni la postura eretta"
            )
        }
    }

    /**
     * Genera suggerimenti per migliorare la foto.
     */
    fun getSuggestions(validation: PoseValidation): List<String> {
        val suggestions = mutableListOf<String>()

        if (!validation.bodyVisible) {
            suggestions.add("Assicurati che tutto il corpo sia visibile")
            suggestions.add("Fai un passo indietro dalla fotocamera")
        }

        if (!validation.poseCorrect) {
            suggestions.add("Mantieni le braccia lungo i fianchi")
            suggestions.add("Stai dritto con la schiena eretta")
        }

        if (!validation.lightingGood) {
            suggestions.add("Cerca una zona più illuminata")
            suggestions.add("Evita controluce (luce dietro di te)")
        }

        if (!validation.distanceOk) {
            suggestions.add("Mantieni una distanza di 2-3 metri")
        }

        return suggestions
    }

    /**
     * Landmark chiave per ogni vista.
     */
    fun getKeyLandmarks(view: PhotoView): List<Int> {
        // MediaPipe Pose landmark indices
        return when (view) {
            PhotoView.FRONT, PhotoView.BACK -> listOf(
                0,   // Nose
                11, 12,  // Shoulders
                23, 24,  // Hips
                25, 26,  // Knees
                27, 28   // Ankles
            )
            PhotoView.SIDE -> listOf(
                0,   // Nose
                11,  // Left shoulder (or 12 for right)
                23,  // Left hip (or 24)
                25,  // Left knee (or 26)
                27   // Left ankle (or 28)
            )
        }
    }

    /**
     * Verifica la validità della posa per una vista specifica.
     */
    fun validatePose(
        landmarks: List<LandmarkPoint>,
        view: PhotoView,
        imageWidth: Int,
        imageHeight: Int
    ): PoseValidation {
        if (landmarks.size < 33) {
            return PoseValidation(
                isValid = false,
                bodyVisible = false,
                poseCorrect = false,
                lightingGood = true,
                distanceOk = false,
                message = "Corpo non rilevato completamente"
            )
        }

        // Verifica visibilità landmark chiave
        val keyLandmarks = getKeyLandmarks(view)
        val avgVisibility = keyLandmarks
            .mapNotNull { idx -> landmarks.getOrNull(idx)?.visibility }
            .average()
            .toFloat()

        val bodyVisible = avgVisibility > 0.5f

        // Verifica posizione (corpo centrato e visibile per intero)
        val minY = landmarks.minOfOrNull { it.y } ?: 0f
        val maxY = landmarks.maxOfOrNull { it.y } ?: 1f
        val bodyHeight = maxY - minY
        val distanceOk = bodyHeight in 0.6f..0.95f  // 60-95% dell'immagine

        // Verifica posa corretta
        val poseCorrect = when (view) {
            PhotoView.FRONT, PhotoView.BACK -> {
                // Spalle allineate orizzontalmente
                val leftShoulder = landmarks[11]
                val rightShoulder = landmarks[12]
                val shoulderDiff = kotlin.math.abs(leftShoulder.y - rightShoulder.y)
                shoulderDiff < 0.05f
            }
            PhotoView.SIDE -> {
                // Verifica orientamento laterale
                val nose = landmarks[0]
                val leftShoulder = landmarks[11]
                val rightShoulder = landmarks[12]
                // Una spalla dovrebbe essere significativamente più avanti dell'altra
                kotlin.math.abs(leftShoulder.z - rightShoulder.z) > 0.1f
            }
        }

        val isValid = bodyVisible && distanceOk && poseCorrect

        val message = when {
            !bodyVisible -> "Corpo non completamente visibile"
            !distanceOk -> if (bodyHeight < 0.6f) "Avvicinati alla fotocamera" else "Allontanati dalla fotocamera"
            !poseCorrect -> when (view) {
                PhotoView.FRONT, PhotoView.BACK -> "Raddrizza le spalle"
                PhotoView.SIDE -> "Ruota di più di lato"
            }
            else -> "Posizione corretta!"
        }

        return PoseValidation(
            isValid = isValid,
            bodyVisible = bodyVisible,
            poseCorrect = poseCorrect,
            lightingGood = true,  // Difficile da verificare senza analisi avanzata
            distanceOk = distanceOk,
            message = message,
            suggestions = if (!isValid) getSuggestions(
                PoseValidation(isValid, bodyVisible, poseCorrect, true, distanceOk, message)
            ) else emptyList()
        )
    }
}

/**
 * Progress del processo TrueClone completo.
 */
data class TrueCloneProgress(
    val phase: TrueClonePhase,
    val progress: Float,  // 0-1
    val message: String,
    val currentView: PhotoView? = null
)

enum class TrueClonePhase {
    IDLE,
    CAPTURING_PHOTOS,
    ANALYZING_POSES,
    EXTRACTING_SILHOUETTES,
    ESTIMATING_DEPTH,
    GENERATING_MESH,
    FITTING_SILHOUETTES,
    APPLYING_TEXTURE,
    FINALIZING,
    COMPLETE,
    ERROR
}
