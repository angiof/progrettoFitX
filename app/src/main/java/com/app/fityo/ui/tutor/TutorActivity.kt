package com.app.fityo.ui.tutor

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.repository.TutorRepository
import com.app.fityo.dominio.ExerciseType
import com.app.fityo.dominio.TutorState
import com.app.fityo.ui.tutor.compose.AnalysisProgressScreen
import com.app.fityo.ui.tutor.compose.ErrorScreen
import com.app.fityo.ui.tutor.compose.ExerciseSelectionScreen
import com.app.fityo.ui.tutor.compose.PlaybackScreen
import com.app.fityo.ui.tutor.compose.RecordingScreen
import com.app.fityo.ui.tutor.compose.TutorHistoryScreen
import com.app.fityo.ui.tutor.compose.TutorResultScreen
import com.app.fityo.ui.tutor.compose.TutorTheme
import com.app.fityo.ui.tutor.compose.VideoSourceScreen

class TutorActivity : ComponentActivity() {

    private val viewModel: TutorViewModel by viewModels {
        TutorViewModelFactory(
            application,
            TutorRepository(DbFit.getDatabase(application).tutorSessionDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TutorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorScreen(viewModel = viewModel)
                }
            }
        }
    }

    @Composable
    private fun TutorScreen(viewModel: TutorViewModel) {
        val tutorState by viewModel.tutorState.collectAsState()
        val historyState by viewModel.historyState.collectAsState()

        // Permission launcher for camera
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val state = viewModel.tutorState.value
                if (state is TutorState.SelectingVideoSource) {
                    viewModel.startRecording(state.exerciseType)
                }
            } else {
                Toast.makeText(this, "Permesso camera necessario", Toast.LENGTH_SHORT).show()
            }
        }

        // Video picker launcher
        val videoPickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { handleVideoSelected(it) }
        }

        when (val state = tutorState) {
            is TutorState.Idle -> {
                TutorHistoryScreen(
                    historyState = historyState,
                    onNewAnalysis = { viewModel.startNewAnalysis() },
                    onSessionClick = { sessionId -> viewModel.openSession(sessionId) },
                    onDeleteSession = { sessionId -> viewModel.deleteSession(sessionId) },
                    onBack = { finish() }
                )
            }

            is TutorState.SelectingExercise -> {
                ExerciseSelectionScreen(
                    onExerciseSelected = { exerciseType ->
                        viewModel.selectExercise(exerciseType)
                    },
                    onBack = { viewModel.resetToIdle() }
                )
            }

            is TutorState.SelectingVideoSource -> {
                VideoSourceScreen(
                    exerciseType = state.exerciseType,
                    onRecordVideo = {
                        checkCameraPermissionAndRecord(
                            cameraPermissionLauncher,
                            state.exerciseType
                        )
                    },
                    onSelectFromGallery = {
                        videoPickerLauncher.launch("video/*")
                    },
                    onBack = { viewModel.onBackPressed() }
                )
            }

            is TutorState.Recording -> {
                RecordingScreen(
                    exerciseType = state.exerciseType,
                    onRecordingComplete = { videoPath ->
                        viewModel.analyzeVideo(state.exerciseType, videoPath)
                    },
                    onBack = { viewModel.onBackPressed() }
                )
            }

            is TutorState.Analyzing -> {
                AnalysisProgressScreen(
                    exerciseType = state.exerciseType,
                    progress = state.progress,
                    currentFrame = state.currentFrame,
                    totalFrames = state.totalFrames,
                    statusText = "Analisi frame ${state.currentFrame} di ${state.totalFrames}",
                    videoPath = state.videoPath
                )
            }

            is TutorState.ResultReady -> {
                PlaybackScreen(
                    exerciseType = state.exerciseType,
                    videoPath = state.videoPath,
                    errors = state.errors,
                    overallScore = state.overallScore,
                    onSave = {
                        viewModel.saveAnalysisResult(
                            exerciseType = state.exerciseType,
                            videoPath = state.videoPath,
                            errors = state.errors,
                            overallScore = state.overallScore,
                            duration = state.duration
                        )
                    },
                    onDiscard = { viewModel.resetToIdle() },
                    onBack = { viewModel.onBackPressed() },
                    isNewAnalysis = true
                )
            }

            is TutorState.Playback -> {
                PlaybackScreen(
                    exerciseType = state.exerciseType,
                    videoPath = state.videoPath,
                    errors = state.errors,
                    overallScore = state.overallScore,
                    onSave = { viewModel.resetToIdle() },
                    onDiscard = { viewModel.resetToIdle() },
                    onBack = { viewModel.onBackPressed() },
                    isNewAnalysis = false
                )
            }

            is TutorState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.resetToIdle() }
                )
            }
        }
    }

    private fun checkCameraPermissionAndRecord(
        permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
        exerciseType: ExerciseType
    ) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startRecording(exerciseType)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handleVideoSelected(uri: Uri) {
        val state = viewModel.tutorState.value
        if (state is TutorState.SelectingVideoSource) {
            // Copia il video nella directory dell'app
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val videoFile = java.io.File(
                        filesDir,
                        "tutor_video_${System.currentTimeMillis()}.mp4"
                    )
                    videoFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                    inputStream.close()
                    viewModel.analyzeVideo(state.exerciseType, videoFile.absolutePath)
                } else {
                    Toast.makeText(this, "Impossibile aprire il video", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Errore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}
