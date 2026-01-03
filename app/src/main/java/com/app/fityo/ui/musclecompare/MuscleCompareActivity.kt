package com.app.fityo.ui.musclecompare

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.repository.MuscleCompareRepository
import com.app.fityo.data_layer.repository.UserProfileRepository
import com.app.fityo.dominio.CompareState
import com.app.fityo.dominio.ViewType
import com.app.fityo.ui.musclecompare.compose.Avatar3DViewerScreen
import com.app.fityo.ui.musclecompare.compose.BodyIntelligenceResultScreen
import com.app.fityo.ui.musclecompare.compose.CameraScreen
import com.app.fityo.ui.musclecompare.compose.HistoryScreen
import com.app.fityo.ui.musclecompare.compose.MuscleCompareTheme
import com.app.fityo.ui.musclecompare.compose.ProfileFormScreen
import com.app.fityo.ui.musclecompare.compose.ProfileViewScreen
import com.app.fityo.ui.musclecompare.compose.ResultScreen
import com.app.fityo.ui.musclecompare.compose.Video360RecordingScreen
import com.app.fityo.ui.musclecompare.compose.ViewTypeSelectionScreen

class MuscleCompareActivity : ComponentActivity() {

    private val viewModel: MuscleCompareViewModel by viewModels {
        val db = DbFit.getDatabase(application)
        MuscleCompareViewModelFactory(
            application,
            MuscleCompareRepository(db.muscleCompareDao()),
            db.schedeDao(),
            UserProfileRepository(db.userProfileDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MuscleCompareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MuscleCompareScreen(viewModel = viewModel)
                }
            }
        }
    }

    @Composable
    private fun MuscleCompareScreen(viewModel: MuscleCompareViewModel) {
        val compareState by viewModel.compareState.collectAsState()
        val historyState by viewModel.historyState.collectAsState()
        val poseDetected by viewModel.poseDetected.collectAsState()
        val alignmentPercent by viewModel.alignmentPercent.collectAsState()
        val ghostOverlay by viewModel.ghostOverlay.collectAsState()
        val useFrontCamera by viewModel.useFrontCamera.collectAsState()
        val liveLandmarks by viewModel.liveLandmarks.collectAsState()
        val workoutAnalysis by viewModel.workoutAnalysis.collectAsState()
        val bodyIntelligenceState by viewModel.bodyIntelligenceState.collectAsState()
        val currentProfile by viewModel.currentProfile.collectAsState()
        val isEditingProfile by viewModel.isEditingProfile.collectAsState()

        // Permission launcher for Muscle Compare
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.startNewComparison()
            } else {
                Toast.makeText(this, "Permesso camera necessario", Toast.LENGTH_SHORT).show()
            }
        }

        // Permission launcher for Body Intelligence
        val bodyIntelligencePermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.startBodyIntelligenceCapture()
            } else {
                Toast.makeText(this, "Permesso camera necessario", Toast.LENGTH_SHORT).show()
            }
        }

        // Gallery picker
        val galleryLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { handleGalleryImage(it) }
        }

        // Handle Body Intelligence states first (takes priority when active)
        val biState = bodyIntelligenceState
        if (biState != null) {
            BodyIntelligenceFlow(
                state = biState,
                currentProfile = currentProfile,
                isEditingProfile = isEditingProfile,
                poseDetected = poseDetected,
                liveLandmarks = liveLandmarks,
                useFrontCamera = useFrontCamera,
                onBack = { viewModel.exitBodyIntelligence() },
                onStartEditProfile = { viewModel.startEditProfile() },
                onCancelEditProfile = { viewModel.cancelEditProfile() },
                onSaveProfile = { viewModel.saveProfile(it) },
                onDeleteProfile = { viewModel.deleteProfile() },
                onStartCapture = {
                    checkCameraPermissionAndStartBodyIntelligence(bodyIntelligencePermissionLauncher)
                },
                onCancelCapture = { viewModel.cancelBodyIntelligenceCapture() },
                onCapturePhoto = { bitmap -> viewModel.processBodyIntelligencePhoto(bitmap) },
                onPoseUpdate = { bitmap -> viewModel.checkLivePoseAlignment(bitmap) },
                onToggleCamera = { viewModel.toggleCamera() },
                onSaveResult = { viewModel.saveBodyIntelligenceResult() },
                onDiscardResult = { viewModel.discardBodyIntelligenceResult() },
                onGenerateAvatar3D = { viewModel.startAvatar3DGeneration() },
                onVideo360Complete = { videoPath -> viewModel.onVideo360RecordingComplete(videoPath) },
                onCancelAvatar3D = { viewModel.cancelAvatar3DRecording() },
                onSaveAvatar3D = { viewModel.saveAvatar3D() },
                onDiscardAvatar3D = { viewModel.discardAvatar3D() }
            )
            return
        }

        when (val state = compareState) {
            is CompareState.Idle -> {
                HistoryScreen(
                    historyState = historyState,
                    onNewCompare = {
                        checkCameraPermissionAndStart(permissionLauncher)
                    },
                    onBodyIntelligence = {
                        viewModel.startBodyIntelligence()
                    },
                    onDeleteCompare = { id ->
                        viewModel.deleteComparison(id)
                    },
                    onBack = { finish() }
                )
            }

            is CompareState.SelectingViewType -> {
                ViewTypeSelectionScreen(
                    onViewTypeSelected = { viewType ->
                        viewModel.selectViewType(viewType)
                    },
                    onBack = {
                        viewModel.resetComparison()
                    }
                )
            }

            is CompareState.CapturingPhotoA -> {
                val viewType = state.viewType
                val (title, subtitle) = when (viewType) {
                    ViewType.FRONTAL -> Pair(
                        "Prima Foto - Vista Frontale",
                        "Posizionati di FRONTE alla camera.\nCorpo intero visibile dalla testa ai piedi."
                    )
                    ViewType.POSTERIOR -> Pair(
                        "Prima Foto - Vista Posteriore",
                        "Posizionati di SCHIENA alla camera.\nCorpo intero visibile dalla testa ai piedi."
                    )
                }
                CameraScreen(
                    title = title,
                    subtitle = subtitle,
                    ghostOverlay = null,
                    poseDetected = poseDetected,
                    alignmentPercent = 0f,
                    showAlignmentIndicator = false,
                    useFrontCamera = useFrontCamera,
                    liveLandmarks = liveLandmarks,
                    onCapture = { bitmap ->
                        viewModel.processPhotoA(bitmap)
                    },
                    onGallerySelect = {
                        galleryLauncher.launch("image/*")
                    },
                    onBack = {
                        viewModel.resetComparison()
                    },
                    onPoseUpdate = { bitmap ->
                        viewModel.checkLivePoseAlignment(bitmap)
                    },
                    onToggleCamera = {
                        viewModel.toggleCamera()
                    }
                )
            }

            is CompareState.PhotoACaptured -> {
                val viewType = state.viewType
                val subtitle = when (viewType) {
                    ViewType.FRONTAL -> "Foto frontale catturata. Procedi alla seconda foto."
                    ViewType.POSTERIOR -> "Foto posteriore catturata. Procedi alla seconda foto."
                }
                CameraScreen(
                    title = "Foto Catturata",
                    subtitle = subtitle,
                    ghostOverlay = null,
                    poseDetected = true,
                    alignmentPercent = 100f,
                    showAlignmentIndicator = false,
                    showPreview = true,
                    previewPath = state.photoPath,
                    useFrontCamera = useFrontCamera,
                    onCapture = { },
                    onGallerySelect = { },
                    onBack = { viewModel.resetComparison() },
                    onPoseUpdate = { },
                    onProceed = { viewModel.proceedToPhotoB() }
                )
            }

            is CompareState.CapturingPhotoB -> {
                val viewType = state.viewType
                val (title, subtitle) = when (viewType) {
                    ViewType.FRONTAL -> Pair(
                        "Seconda Foto - Vista Frontale",
                        "Ripeti la stessa posa frontale.\nSegui la sagoma semi-trasparente."
                    )
                    ViewType.POSTERIOR -> Pair(
                        "Seconda Foto - Vista Posteriore",
                        "Ripeti la stessa posa di schiena.\nSegui la sagoma semi-trasparente."
                    )
                }
                CameraScreen(
                    title = title,
                    subtitle = subtitle,
                    ghostOverlay = ghostOverlay,
                    poseDetected = poseDetected,
                    alignmentPercent = alignmentPercent,
                    showAlignmentIndicator = true,
                    useFrontCamera = useFrontCamera,
                    liveLandmarks = liveLandmarks,
                    onCapture = { bitmap ->
                        viewModel.processPhotoB(bitmap)
                    },
                    onGallerySelect = {
                        galleryLauncher.launch("image/*")
                    },
                    onBack = {
                        viewModel.resetComparison()
                    },
                    onPoseUpdate = { bitmap ->
                        viewModel.checkLivePoseAlignment(bitmap)
                    },
                    onToggleCamera = {
                        viewModel.toggleCamera()
                    }
                )
            }

            is CompareState.PhotoBCaptured -> {
                // Transizione automatica all'elaborazione
            }

            is CompareState.Processing -> {
                ProcessingScreen()
            }

            is CompareState.ResultReady -> {
                ResultScreen(
                    result = state.result,
                    workoutAnalysis = workoutAnalysis,
                    onSave = {
                        viewModel.saveComparisonResult("", "", state.result)
                        viewModel.resetComparison()
                    },
                    onDiscard = {
                        viewModel.resetComparison()
                    }
                )
            }

            is CompareState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = {
                        viewModel.resetComparison()
                    }
                )
            }
        }
    }

    @Composable
    private fun ProcessingScreen() {
        com.app.fityo.ui.musclecompare.compose.ProcessingScreen()
    }

    @Composable
    private fun ErrorScreen(message: String, onRetry: () -> Unit) {
        com.app.fityo.ui.musclecompare.compose.ErrorScreen(
            message = message,
            onRetry = onRetry
        )
    }

    private fun checkCameraPermissionAndStart(
        permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    ) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startNewComparison()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handleGalleryImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val state = viewModel.compareState.value
                when (state) {
                    is CompareState.CapturingPhotoA -> viewModel.processPhotoA(bitmap)
                    is CompareState.CapturingPhotoB -> viewModel.processPhotoB(bitmap)
                    else -> { }
                }
            } else {
                Toast.makeText(this, "Impossibile caricare l'immagine", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Errore: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermissionAndStartBodyIntelligence(
        permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    ) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startBodyIntelligenceCapture()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    @Composable
    private fun BodyIntelligenceFlow(
        state: BodyIntelligenceState,
        currentProfile: com.app.fityo.dominio.UserProfile?,
        isEditingProfile: Boolean,
        poseDetected: Boolean,
        liveLandmarks: com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult?,
        useFrontCamera: Boolean,
        onBack: () -> Unit,
        onStartEditProfile: () -> Unit,
        onCancelEditProfile: () -> Unit,
        onSaveProfile: (com.app.fityo.dominio.UserProfile) -> Unit,
        onDeleteProfile: () -> Unit,
        onStartCapture: () -> Unit,
        onCancelCapture: () -> Unit,
        onCapturePhoto: (Bitmap) -> Unit,
        onPoseUpdate: (Bitmap) -> Unit,
        onToggleCamera: () -> Unit,
        onSaveResult: () -> Unit,
        onDiscardResult: () -> Unit,
        onGenerateAvatar3D: () -> Unit,
        onVideo360Complete: (String) -> Unit,
        onCancelAvatar3D: () -> Unit,
        onSaveAvatar3D: () -> Unit,
        onDiscardAvatar3D: () -> Unit
    ) {
        // Show profile form if editing
        if (isEditingProfile) {
            ProfileFormScreen(
                existingProfile = currentProfile,
                onSave = onSaveProfile,
                onBack = onCancelEditProfile
            )
            return
        }

        when (state) {
            is BodyIntelligenceState.CheckingProfile -> {
                // Loading screen
                com.app.fityo.ui.musclecompare.compose.ProcessingScreen(
                    message = "Caricamento profilo..."
                )
            }

            is BodyIntelligenceState.NoProfile -> {
                // Show profile creation form
                ProfileFormScreen(
                    existingProfile = null,
                    onSave = onSaveProfile,
                    onBack = onBack
                )
            }

            is BodyIntelligenceState.ProfileReady -> {
                ProfileViewScreen(
                    profile = state.profile,
                    onEdit = onStartEditProfile,
                    onDelete = onDeleteProfile,
                    onBack = onBack,
                    onStartAnalysis = onStartCapture
                )
            }

            is BodyIntelligenceState.CapturingPhoto -> {
                CameraScreen(
                    title = "Body Intelligence",
                    subtitle = "Posizionati in piedi di fronte alla camera.\nCorpo intero visibile dalla testa ai piedi.",
                    ghostOverlay = null,
                    poseDetected = poseDetected,
                    alignmentPercent = 0f,
                    showAlignmentIndicator = false,
                    useFrontCamera = useFrontCamera,
                    liveLandmarks = liveLandmarks,
                    onCapture = onCapturePhoto,
                    onGallerySelect = { },
                    onBack = onCancelCapture,
                    onPoseUpdate = onPoseUpdate,
                    onToggleCamera = onToggleCamera
                )
            }

            is BodyIntelligenceState.PhotoCaptured -> {
                // Transition state, handled automatically
            }

            is BodyIntelligenceState.Analyzing -> {
                BodyIntelligenceAnalyzingScreen(
                    progress = state.progress,
                    currentStep = state.currentStep
                )
            }

            is BodyIntelligenceState.ResultReady -> {
                BodyIntelligenceResultScreen(
                    result = state.result,
                    onSave = onSaveResult,
                    onDiscard = onDiscardResult,
                    onBack = onDiscardResult,
                    onGenerateAvatar3D = onGenerateAvatar3D
                )
            }

            is BodyIntelligenceState.Error -> {
                com.app.fityo.ui.musclecompare.compose.ErrorScreen(
                    message = state.message,
                    onRetry = onBack
                )
            }

            // Avatar 3D states
            is BodyIntelligenceState.Recording360 -> {
                Video360RecordingScreen(
                    onRecordingComplete = onVideo360Complete,
                    onBack = onCancelAvatar3D
                )
            }

            is BodyIntelligenceState.Processing360 -> {
                Avatar3DViewerScreen(
                    measurements = null,
                    zoneAnalysis = state.result.bodyZoneAnalysis,
                    isProcessing = true,
                    processingProgress = state.progress,
                    processingStep = state.currentStep,
                    onSave = { },
                    onBack = onCancelAvatar3D
                )
            }

            is BodyIntelligenceState.Avatar3DReady -> {
                Avatar3DViewerScreen(
                    measurements = state.measurements,
                    zoneAnalysis = state.result.bodyZoneAnalysis,
                    isProcessing = false,
                    onSave = onSaveAvatar3D,
                    onBack = onDiscardAvatar3D
                )
            }
        }
    }

    @Composable
    private fun BodyIntelligenceAnalyzingScreen(
        progress: Float,
        currentStep: String
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(80.dp),
                    color = com.app.fityo.ui.musclecompare.compose.AccentGreen,
                    strokeWidth = 6.dp
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                androidx.compose.material3.Text(
                    text = "Body Intelligence",
                    color = com.app.fityo.ui.musclecompare.compose.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = currentStep,
                    color = com.app.fityo.ui.musclecompare.compose.TextSecondary,
                    fontSize = 14.sp
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Text(
                    text = "${(progress * 100).toInt()}%",
                    color = com.app.fityo.ui.musclecompare.compose.AccentGreen,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
