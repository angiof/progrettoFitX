package com.app.fityo.ui.musclecompare

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.avatar3d.model.ShapeParameters
import com.app.fityo.avatar3d.model.ZoneColors
import com.app.fityo.avatar3d.processing.Video360Processor
import com.app.fityo.data_layer.db.Avatar3DEntity
import com.app.fityo.data_layer.db.MuscleCompareEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.repository.MuscleCompareRepository
import com.app.fityo.data_layer.repository.UserProfileRepository
import com.app.fityo.dominio.CompareHistoryItem
import com.app.fityo.dominio.CompareResult
import com.app.fityo.dominio.CompareState
import com.app.fityo.dominio.HistoryState
import com.app.fityo.dominio.UserProfile
import com.app.fityo.dominio.ViewType
import com.app.fityo.dominio.WorkoutAnalysis
import com.app.fityo.dominio.WorkoutStats
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
import com.app.fityo.mediapipe.EncryptedPhotoStorage
import com.app.fityo.mediapipe.ImageSegmenterHelper
import com.app.fityo.mediapipe.MuscleAnalyzer
import com.app.fityo.mediapipe.PhotoNormalizer
import com.app.fityo.mediapipe.PoseLandmarkerHelper
import com.app.fityo.mediapipe.WorkoutAnalyzer
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MuscleCompareViewModel(
    application: Application,
    private val repository: MuscleCompareRepository,
    private val schedeDao: DaoSchede,
    private val userProfileRepository: UserProfileRepository
) : AndroidViewModel(application) {

    // State flows
    private val _compareState = MutableStateFlow<CompareState>(CompareState.Idle)
    val compareState: StateFlow<CompareState> = _compareState.asStateFlow()

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    private val _poseDetected = MutableStateFlow(false)
    val poseDetected: StateFlow<Boolean> = _poseDetected.asStateFlow()

    private val _alignmentPercent = MutableStateFlow(0f)
    val alignmentPercent: StateFlow<Float> = _alignmentPercent.asStateFlow()

    private val _ghostOverlay = MutableStateFlow<Bitmap?>(null)
    val ghostOverlay: StateFlow<Bitmap?> = _ghostOverlay.asStateFlow()

    // Camera state
    private val _useFrontCamera = MutableStateFlow(false)
    val useFrontCamera: StateFlow<Boolean> = _useFrontCamera.asStateFlow()

    private val _selectedViewType = MutableStateFlow<ViewType?>(null)
    val selectedViewType: StateFlow<ViewType?> = _selectedViewType.asStateFlow()

    // Live skeleton landmarks for real-time visualization
    private val _liveLandmarks = MutableStateFlow<PoseLandmarkerResult?>(null)
    val liveLandmarks: StateFlow<PoseLandmarkerResult?> = _liveLandmarks.asStateFlow()

    // Workout analysis state
    private val _workoutAnalysis = MutableStateFlow<WorkoutAnalysis?>(null)
    val workoutAnalysis: StateFlow<WorkoutAnalysis?> = _workoutAnalysis.asStateFlow()

    // Body Intelligence state
    private val _bodyIntelligenceState = MutableStateFlow<BodyIntelligenceState?>(null)
    val bodyIntelligenceState: StateFlow<BodyIntelligenceState?> = _bodyIntelligenceState.asStateFlow()

    // Current user profile
    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: StateFlow<UserProfile?> = _currentProfile.asStateFlow()

    // Profile editing state
    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile: StateFlow<Boolean> = _isEditingProfile.asStateFlow()

    // Helpers
    private val photoStorage = EncryptedPhotoStorage(application)
    private val workoutAnalyzer = WorkoutAnalyzer()
    private val photoNormalizer = PhotoNormalizer()
    private val muscleAnalyzer = MuscleAnalyzer()
    private val bodyIntelligenceAnalyzer = BodyIntelligenceAnalyzer()

    private var poseLandmarkerHelper: PoseLandmarkerHelper? = null
    private var imageSegmenterHelper: ImageSegmenterHelper? = null

    // Dati temporanei per il confronto
    private var photoABitmap: Bitmap? = null
    private var photoBBitmap: Bitmap? = null
    private var landmarksA: PoseLandmarkerResult? = null
    private var landmarksB: PoseLandmarkerResult? = null
    private var segmentationResultA: ImageSegmenterHelper.ResultBundle? = null

    init {
        loadHistory()
        initializeHelpers()
    }

    private fun initializeHelpers() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                poseLandmarkerHelper = PoseLandmarkerHelper(getApplication())
                imageSegmenterHelper = ImageSegmenterHelper(getApplication())
            } catch (e: Exception) {
                _compareState.value = CompareState.Error("Failed to initialize ML models: ${e.message}")
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = HistoryState.Loading
            try {
                repository.getAllComparesAsHistoryItems().collectLatest { items ->
                    _historyState.value = if (items.isEmpty()) {
                        HistoryState.Empty
                    } else {
                        HistoryState.Success(items)
                    }
                }
            } catch (e: Exception) {
                _historyState.value = HistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun startNewComparison() {
        resetComparison()
        _compareState.value = CompareState.SelectingViewType
    }

    fun selectViewType(viewType: ViewType) {
        _selectedViewType.value = viewType
        _compareState.value = CompareState.CapturingPhotoA(viewType)
    }

    fun toggleCamera() {
        _useFrontCamera.value = !_useFrontCamera.value
    }

    fun resetComparison() {
        photoABitmap?.recycle()
        photoBBitmap?.recycle()
        photoABitmap = null
        photoBBitmap = null
        landmarksA = null
        landmarksB = null
        segmentationResultA = null
        _ghostOverlay.value = null
        _poseDetected.value = false
        _alignmentPercent.value = 0f
        _selectedViewType.value = null
        _workoutAnalysis.value = null
        _compareState.value = CompareState.Idle
    }

    fun processPhotoA(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val currentState = _compareState.value
                val viewType = when (currentState) {
                    is CompareState.CapturingPhotoA -> currentState.viewType
                    else -> _selectedViewType.value ?: ViewType.FRONTAL
                }

                // Rileva pose
                val poseResult = poseLandmarkerHelper?.detectImage(bitmap)
                if (poseResult == null || !poseLandmarkerHelper!!.hasValidPose(poseResult.result)) {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.Error("Pose non rilevata. Assicurati che il corpo intero sia visibile.")
                    }
                    return@launch
                }

                // Segmenta immagine per ghost overlay
                val segResult = imageSegmenterHelper?.segmentImage(bitmap)

                // Salva i dati
                photoABitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                landmarksA = poseResult.result
                segmentationResultA = segResult

                // Crea ghost overlay
                if (segResult != null) {
                    val ghost = imageSegmenterHelper?.createGhostOverlay(bitmap, segResult.result, 80)
                    withContext(Dispatchers.Main) {
                        _ghostOverlay.value = ghost
                    }
                }

                // Salva foto criptata
                val filename = photoStorage.generateFilename("photoA")
                val saveResult = photoStorage.saveEncryptedPhoto(bitmap, filename)

                if (saveResult.isSuccess) {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.PhotoACaptured(saveResult.getOrThrow(), viewType)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.Error("Errore nel salvataggio della foto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _compareState.value = CompareState.Error("Errore: ${e.message}")
                }
            }
        }
    }

    fun proceedToPhotoB() {
        val currentState = _compareState.value
        if (currentState is CompareState.PhotoACaptured) {
            _compareState.value = CompareState.CapturingPhotoB(currentState.viewType)
        }
    }

    fun processPhotoB(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val currentState = _compareState.value
                if (currentState !is CompareState.CapturingPhotoB) return@launch

                val viewType = currentState.viewType

                // Rileva pose
                val poseResult = poseLandmarkerHelper?.detectImage(bitmap)
                if (poseResult == null || !poseLandmarkerHelper!!.hasValidPose(poseResult.result)) {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.Error("Pose non rilevata. Assicurati che il corpo intero sia visibile.")
                    }
                    return@launch
                }

                // Verifica allineamento
                val overlap = landmarksA?.let {
                    photoNormalizer.calculatePoseOverlap(it, poseResult.result)
                } ?: 0f

                if (overlap < 70f) {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.Error("Allineamento insufficiente (${overlap.toInt()}%). Allinea meglio la posa alla sagoma.")
                    }
                    return@launch
                }

                // Salva i dati
                photoBBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                landmarksB = poseResult.result

                // Salva foto criptata
                val filename = photoStorage.generateFilename("photoB")
                val saveResult = photoStorage.saveEncryptedPhoto(bitmap, filename)

                if (saveResult.isSuccess) {
                    // Recupera photoAPath salvato in precedenza
                    val filenameA = photoStorage.generateFilename("photoA_recovered")
                    val photoAPath = photoABitmap?.let {
                        photoStorage.saveEncryptedPhoto(it, filenameA)
                    }?.getOrNull() ?: ""

                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.PhotoBCaptured(photoAPath, saveResult.getOrThrow(), viewType)
                    }

                    // Avvia l'analisi
                    performAnalysis(saveResult.getOrThrow())
                } else {
                    withContext(Dispatchers.Main) {
                        _compareState.value = CompareState.Error("Errore nel salvataggio della foto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _compareState.value = CompareState.Error("Errore: ${e.message}")
                }
            }
        }
    }

    private suspend fun performAnalysis(photoBPath: String) {
        withContext(Dispatchers.Main) {
            _compareState.value = CompareState.Processing
        }

        try {
            val bitmapA = photoABitmap ?: throw IllegalStateException("Photo A not available")
            val bitmapB = photoBBitmap ?: throw IllegalStateException("Photo B not available")
            val lmA = landmarksA ?: throw IllegalStateException("Landmarks A not available")
            val lmB = landmarksB ?: throw IllegalStateException("Landmarks B not available")

            // Normalizza le foto
            val normResult = photoNormalizer.normalizePhotos(bitmapA, bitmapB, lmA, lmB)
                ?: throw IllegalStateException("Normalization failed")

            // Ottieni maschere
            val maskA = segmentationResultA?.let {
                imageSegmenterHelper?.createMaskBitmap(it.result, bitmapA.width, bitmapA.height)
            }
            val segResultB = imageSegmenterHelper?.segmentImage(normResult.normalizedBitmapB)
            val maskB = segResultB?.let {
                imageSegmenterHelper?.createMaskBitmap(it.result, normResult.normalizedBitmapB.width, normResult.normalizedBitmapB.height)
            }

            // Analizza
            val result = muscleAnalyzer.analyze(
                normResult.normalizedBitmapA,
                normResult.normalizedBitmapB,
                maskA,
                maskB,
                lmA,
                lmB,
                normResult.scaleFactorA,
                normResult.scaleFactorB
            ) ?: throw IllegalStateException("Analysis failed")

            withContext(Dispatchers.Main) {
                _compareState.value = CompareState.ResultReady(result)
            }

            // Perform workout analysis
            performWorkoutAnalysis(result)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _compareState.value = CompareState.Error("Analisi fallita: ${e.message}")
            }
        }
    }

    /**
     * Esegue l'analisi della correlazione tra allenamento e risultati muscolari.
     */
    private suspend fun performWorkoutAnalysis(compareResult: CompareResult) {
        try {
            // Ottieni dati workout
            val workoutDistribution = schedeDao.getPercentualePerGruppoMuscolare()
            val totalWorkouts = schedeDao.countSchede()
            val avgPerWeek = schedeDao.getAverageWorkoutsPerWeek() ?: 0.0
            val mostTrained = schedeDao.getMostTrainedMuscleGroup()
            val daysSince = schedeDao.getDaysSinceLastWorkout()

            // Trova il gruppo meno allenato
            val leastTrained = if (workoutDistribution.isNotEmpty()) {
                workoutDistribution.minByOrNull { it.percentuale }?.gruppoMuscolare
            } else null

            val stats = WorkoutStats(
                totalWorkouts = totalWorkouts,
                workoutsPerWeek = avgPerWeek,
                mostTrainedGroup = mostTrained,
                leastTrainedGroup = leastTrained,
                daysSinceLastWorkout = daysSince
            )

            val analysis = workoutAnalyzer.analyze(
                compareResult = compareResult,
                workoutDistribution = workoutDistribution,
                workoutStats = stats
            )

            withContext(Dispatchers.Main) {
                _workoutAnalysis.value = analysis
            }
        } catch (e: Exception) {
            // Non bloccare il risultato se l'analisi workout fallisce
            withContext(Dispatchers.Main) {
                _workoutAnalysis.value = null
            }
        }
    }

    fun saveComparisonResult(photoAPath: String, photoBPath: String, result: CompareResult) {
        viewModelScope.launch {
            try {
                val entity = MuscleCompareEntity(
                    createdAt = System.currentTimeMillis(),
                    photoAPath = photoAPath,
                    photoBPath = photoBPath,
                    armsVariation = result.armsResult.variationPercent,
                    absVariation = result.absResult.variationPercent,
                    legsVariation = result.legsResult.variationPercent,
                    glutesVariation = result.glutesResult.variationPercent,
                    scaleFactorA = result.scaleFactorA,
                    scaleFactorB = result.scaleFactorB
                )
                repository.insert(entity)
                loadHistory()
            } catch (e: Exception) {
                _compareState.value = CompareState.Error("Errore nel salvataggio: ${e.message}")
            }
        }
    }

    fun deleteComparison(id: Int) {
        viewModelScope.launch {
            try {
                val entity = repository.getById(id)
                entity?.let {
                    // Elimina le foto associate
                    photoStorage.deletePhoto(it.photoAPath)
                    photoStorage.deletePhoto(it.photoBPath)
                }
                repository.deleteById(id)
            } catch (e: Exception) {
                _historyState.value = HistoryState.Error("Errore nell'eliminazione: ${e.message}")
            }
        }
    }

    fun updatePoseDetection(detected: Boolean) {
        _poseDetected.value = detected
    }

    fun updateAlignmentPercent(percent: Float) {
        _alignmentPercent.value = percent
    }

    fun checkLivePoseAlignment(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val poseResult = poseLandmarkerHelper?.detectImage(bitmap)
                val hasValidPose = poseResult != null && poseLandmarkerHelper!!.hasValidPose(poseResult.result)

                withContext(Dispatchers.Main) {
                    _poseDetected.value = hasValidPose
                    // Update live landmarks for skeleton visualization
                    _liveLandmarks.value = poseResult?.result
                }

                if (hasValidPose && landmarksA != null) {
                    val overlap = photoNormalizer.calculatePoseOverlap(landmarksA!!, poseResult!!.result)
                    withContext(Dispatchers.Main) {
                        _alignmentPercent.value = overlap
                    }
                }
            } catch (e: Exception) {
                // Ignora errori durante il live check
                withContext(Dispatchers.Main) {
                    _liveLandmarks.value = null
                }
            }
        }
    }

    fun clearLiveLandmarks() {
        _liveLandmarks.value = null
    }

    override fun onCleared() {
        super.onCleared()
        poseLandmarkerHelper?.close()
        imageSegmenterHelper?.close()
        photoABitmap?.recycle()
        photoBBitmap?.recycle()
    }

    // ==================== Body Intelligence Methods ====================

    /**
     * Starts Body Intelligence mode - checks if user has a profile.
     */
    fun startBodyIntelligence() {
        _bodyIntelligenceState.value = BodyIntelligenceState.CheckingProfile
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getActiveProfile()
                withContext(Dispatchers.Main) {
                    if (profile != null) {
                        _currentProfile.value = profile
                        _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(profile)
                    } else {
                        _bodyIntelligenceState.value = BodyIntelligenceState.NoProfile
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error("Errore nel caricamento del profilo: ${e.message}")
                }
            }
        }
    }

    /**
     * Exits Body Intelligence mode.
     */
    fun exitBodyIntelligence() {
        _bodyIntelligenceState.value = null
        _isEditingProfile.value = false
    }

    /**
     * Starts profile editing/creation.
     */
    fun startEditProfile() {
        _isEditingProfile.value = true
    }

    /**
     * Cancels profile editing.
     */
    fun cancelEditProfile() {
        _isEditingProfile.value = false
    }

    /**
     * Saves a user profile.
     */
    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            try {
                if (profile.id != null) {
                    userProfileRepository.updateProfile(profile)
                } else {
                    userProfileRepository.createProfile(profile)
                }
                val savedProfile = userProfileRepository.getActiveProfile()
                withContext(Dispatchers.Main) {
                    _currentProfile.value = savedProfile
                    _isEditingProfile.value = false
                    if (savedProfile != null) {
                        _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(savedProfile)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error("Errore nel salvataggio: ${e.message}")
                }
            }
        }
    }

    /**
     * Deletes the current profile.
     */
    fun deleteProfile() {
        viewModelScope.launch {
            try {
                _currentProfile.value?.id?.let { id ->
                    userProfileRepository.deleteById(id)
                }
                withContext(Dispatchers.Main) {
                    _currentProfile.value = null
                    _bodyIntelligenceState.value = BodyIntelligenceState.NoProfile
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error("Errore nell'eliminazione: ${e.message}")
                }
            }
        }
    }

    /**
     * Starts the Body Intelligence photo capture flow.
     */
    fun startBodyIntelligenceCapture() {
        val profile = _currentProfile.value ?: return
        _bodyIntelligenceState.value = BodyIntelligenceState.CapturingPhoto(profile)
    }

    /**
     * Processes the captured photo for Body Intelligence analysis.
     */
    fun processBodyIntelligencePhoto(bitmap: Bitmap) {
        val profile = _currentProfile.value ?: return

        viewModelScope.launch(Dispatchers.Default) {
            try {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Analyzing(
                        profile = profile,
                        progress = 0.1f,
                        currentStep = "Rilevamento pose..."
                    )
                }

                // Detect pose
                val poseResult = poseLandmarkerHelper?.detectImage(bitmap)
                if (poseResult == null || !poseLandmarkerHelper!!.hasValidPose(poseResult.result)) {
                    withContext(Dispatchers.Main) {
                        _bodyIntelligenceState.value = BodyIntelligenceState.Error(
                            "Pose non rilevata. Assicurati che il corpo intero sia visibile."
                        )
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Analyzing(
                        profile = profile,
                        progress = 0.4f,
                        currentStep = "Segmentazione corporea..."
                    )
                }

                // Segment image
                val segResult = imageSegmenterHelper?.segmentImage(bitmap)
                val segmentationMask = segResult?.let {
                    imageSegmenterHelper?.createMaskBitmap(it.result, bitmap.width, bitmap.height)
                }

                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Analyzing(
                        profile = profile,
                        progress = 0.7f,
                        currentStep = "Analisi biometrica..."
                    )
                }

                // Perform Body Intelligence analysis
                val analysisResult = bodyIntelligenceAnalyzer.analyze(
                    bitmap = bitmap,
                    poseResult = poseResult.result,
                    segmentationMask = segmentationMask,
                    profile = profile
                )

                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Analyzing(
                        profile = profile,
                        progress = 1.0f,
                        currentStep = "Completato!"
                    )
                }

                // Small delay to show 100%
                kotlinx.coroutines.delay(300)

                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.ResultReady(analysisResult)
                }

                // Cleanup
                segmentationMask?.recycle()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error(
                        "Errore nell'analisi: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Saves the Body Intelligence result and returns to profile ready state.
     */
    fun saveBodyIntelligenceResult() {
        // For now, just return to profile ready state
        // In the future, we could save the result to database
        _currentProfile.value?.let { profile ->
            _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(profile)
        }
    }

    /**
     * Discards the Body Intelligence result and returns to profile ready state.
     */
    fun discardBodyIntelligenceResult() {
        _currentProfile.value?.let { profile ->
            _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(profile)
        }
    }

    /**
     * Returns to the profile ready state from capture mode.
     */
    fun cancelBodyIntelligenceCapture() {
        _currentProfile.value?.let { profile ->
            _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(profile)
        }
    }

    // ==================== Avatar 3D Methods ====================

    // Temporary storage for current analysis result (used for Avatar 3D generation)
    private var currentAnalysisResult: BodyIntelligenceAnalyzer.AnalysisResult? = null

    /**
     * Starts the Avatar 3D generation flow from Body Intelligence result.
     */
    fun startAvatar3DGeneration() {
        val currentState = _bodyIntelligenceState.value
        if (currentState is BodyIntelligenceState.ResultReady) {
            currentAnalysisResult = currentState.result
            _bodyIntelligenceState.value = BodyIntelligenceState.Recording360(currentState.result)
        }
    }

    /**
     * Handles completion of 360 video recording.
     * Starts video processing.
     */
    fun onVideo360RecordingComplete(videoPath: String) {
        val result = currentAnalysisResult ?: return

        _bodyIntelligenceState.value = BodyIntelligenceState.Processing360(
            result = result,
            videoPath = videoPath,
            progress = 0f,
            currentStep = "Inizializzazione..."
        )

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val processor = Video360Processor(getApplication())

                val processingResult = processor.processVideo(videoPath) { progress, step ->
                    viewModelScope.launch(Dispatchers.Main) {
                        _bodyIntelligenceState.value = BodyIntelligenceState.Processing360(
                            result = result,
                            videoPath = videoPath,
                            progress = progress,
                            currentStep = step
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    if (processingResult.success && processingResult.aggregatedMeasurements != null) {
                        _bodyIntelligenceState.value = BodyIntelligenceState.Avatar3DReady(
                            result = result,
                            measurements = processingResult.aggregatedMeasurements,
                            processingResult = processingResult
                        )
                    } else {
                        _bodyIntelligenceState.value = BodyIntelligenceState.Error(
                            processingResult.errorMessage ?: "Errore nell'elaborazione video"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error(
                        "Errore: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Cancels the 360 recording and returns to result screen.
     */
    fun cancelAvatar3DRecording() {
        currentAnalysisResult?.let { result ->
            _bodyIntelligenceState.value = BodyIntelligenceState.ResultReady(result)
        }
    }

    /**
     * Saves the generated Avatar 3D to database.
     */
    fun saveAvatar3D() {
        val currentState = _bodyIntelligenceState.value
        if (currentState !is BodyIntelligenceState.Avatar3DReady) return

        val profile = _currentProfile.value ?: return
        val profileId = profile.id ?: return

        viewModelScope.launch {
            try {
                // Create shape parameters from measurements
                val shapeParams = ShapeParameters.fromMeasurements(
                    profile = profile,
                    measurements = currentState.measurements,
                    bodyMetrics = currentState.result.bodyMetrics
                )

                // Create zone colors from analysis
                val zoneColors = ZoneColors.fromZoneAnalysis(currentState.result.bodyZoneAnalysis)

                // Create entity
                val entity = Avatar3DEntity(
                    userId = profileId,
                    createdAt = System.currentTimeMillis(),
                    meshDataPath = "", // Procedural mesh, no file needed
                    thumbnailPath = null,
                    shapeParametersJson = shapeParams.toJson(),
                    zoneColorsJson = zoneColors.toJson(),
                    videoSourcePath = null, // Could save video path if needed
                    processingDurationMs = currentState.processingResult.processingTimeMs,
                    framesAnalyzed = currentState.processingResult.framesAnalyzed,
                    confidence = currentState.measurements.overallConfidence
                )

                // Save to database
                val db = DbFit.getDatabase(getApplication())
                db.avatar3dDao().insert(entity)

                // Return to profile ready state
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.ProfileReady(profile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _bodyIntelligenceState.value = BodyIntelligenceState.Error(
                        "Errore nel salvataggio avatar: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Discards the Avatar 3D and returns to result screen.
     */
    fun discardAvatar3D() {
        currentAnalysisResult?.let { result ->
            _bodyIntelligenceState.value = BodyIntelligenceState.ResultReady(result)
        }
    }

    // ==================== Avatar History Methods ====================

    private val _avatarHistory = MutableStateFlow<List<Avatar3DEntity>>(emptyList())
    val avatarHistory: StateFlow<List<Avatar3DEntity>> = _avatarHistory.asStateFlow()

    /**
     * Loads avatar history for current user.
     */
    fun loadAvatarHistory() {
        viewModelScope.launch {
            try {
                val profileId = _currentProfile.value?.id ?: return@launch
                val db = DbFit.getDatabase(getApplication())
                db.avatar3dDao().getAvatarsByUserId(profileId).collectLatest { avatars ->
                    _avatarHistory.value = avatars
                }
            } catch (e: Exception) {
                // Handle error silently
                _avatarHistory.value = emptyList()
            }
        }
    }

    /**
     * Deletes an avatar by ID.
     */
    fun deleteAvatar(avatarId: Int) {
        viewModelScope.launch {
            try {
                val db = DbFit.getDatabase(getApplication())
                db.avatar3dDao().deleteById(avatarId)
                // Reload history
                loadAvatarHistory()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    /**
     * Loads a saved avatar for viewing.
     */
    fun viewSavedAvatar(avatarId: Int) {
        viewModelScope.launch {
            try {
                val db = DbFit.getDatabase(getApplication())
                val avatar = db.avatar3dDao().getById(avatarId) ?: return@launch

                // Parse shape parameters and zone colors
                val shapeParams = ShapeParameters.fromJson(avatar.shapeParametersJson)
                val zoneColors = ZoneColors.fromJson(avatar.zoneColorsJson)

                // Create a simplified result for viewing
                // In a full implementation, we'd reconstruct the full analysis
                // For now, just show the 3D viewer with saved parameters

                // Could transition to a special "ViewingSavedAvatar" state
                // For simplicity, we'll just log this for now
                android.util.Log.d("Avatar3D", "Viewing avatar $avatarId with confidence ${avatar.confidence}")

            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
