package com.app.fityo.ui.musclecompare

import com.app.fityo.avatar3d.processing.Video360Processor
import com.app.fityo.dominio.UserProfile
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
import com.app.fityo.trueclone.TrueCloneProcessor
import com.app.fityo.trueclone.capture.CapturedPhoto
import com.app.fityo.trueclone.capture.PhotoView

/**
 * Stati UI per Body Intelligence.
 */
sealed class BodyIntelligenceState {
    /**
     * Stato iniziale - verifica se esiste un profilo.
     */
    object CheckingProfile : BodyIntelligenceState()

    /**
     * Nessun profilo trovato - richiede creazione.
     */
    object NoProfile : BodyIntelligenceState()

    /**
     * Profilo esistente - pronto per analisi.
     */
    data class ProfileReady(val profile: UserProfile) : BodyIntelligenceState()

    /**
     * Cattura foto frontale/laterale/posteriore in corso.
     */
    data class CapturingPhoto(val profile: UserProfile) : BodyIntelligenceState()

    /**
     * Foto catturata, in attesa di conferma.
     */
    data class PhotoCaptured(
        val profile: UserProfile,
        val photoPath: String
    ) : BodyIntelligenceState()

    /**
     * Analisi in corso.
     */
    data class Analyzing(
        val profile: UserProfile,
        val progress: Float = 0f,
        val currentStep: String = "Inizializzazione..."
    ) : BodyIntelligenceState()

    /**
     * Risultati pronti.
     */
    data class ResultReady(
        val result: BodyIntelligenceAnalyzer.AnalysisResult
    ) : BodyIntelligenceState()

    /**
     * Errore.
     */
    data class Error(val message: String) : BodyIntelligenceState()

    // ========== AVATAR 3D STATES ==========

    /**
     * Registrazione video 360° in corso.
     */
    data class Recording360(
        val result: BodyIntelligenceAnalyzer.AnalysisResult
    ) : BodyIntelligenceState()

    /**
     * Elaborazione video 360° in corso.
     */
    data class Processing360(
        val result: BodyIntelligenceAnalyzer.AnalysisResult,
        val videoPath: String,
        val progress: Float = 0f,
        val currentStep: String = "Inizializzazione...",
        val framesProcessed: Int = 0,
        val totalFrames: Int = 30,
        val validFrames: Int = 0
    ) : BodyIntelligenceState()

    /**
     * Avatar 3D pronto per visualizzazione.
     */
    data class Avatar3DReady(
        val result: BodyIntelligenceAnalyzer.AnalysisResult,
        val measurements: Video360Processor.AggregatedMeasurements,
        val processingResult: Video360Processor.ProcessingResult
    ) : BodyIntelligenceState()

    // ========== TRUECLONE 3D STATES ==========

    /**
     * Cattura foto TrueClone in corso (Front, Side, Back).
     */
    data class TrueCloneCapturing(
        val result: BodyIntelligenceAnalyzer.AnalysisResult,
        val capturedPhotos: List<CapturedPhoto> = emptyList(),
        val currentView: PhotoView = PhotoView.FRONT
    ) : BodyIntelligenceState()

    /**
     * Elaborazione TrueClone in corso.
     */
    data class TrueCloneProcessing(
        val result: BodyIntelligenceAnalyzer.AnalysisResult,
        val photos: List<CapturedPhoto>,
        val progress: Float = 0f,
        val currentStep: String = "Inizializzazione..."
    ) : BodyIntelligenceState()

    /**
     * TrueClone 3D pronto per visualizzazione.
     */
    data class TrueCloneReady(
        val result: BodyIntelligenceAnalyzer.AnalysisResult,
        val trueCloneResult: TrueCloneProcessor.TrueCloneResult
    ) : BodyIntelligenceState()
}
