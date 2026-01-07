package com.app.fityo.analytics

import android.content.Context
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.UserProfileEntity
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import java.io.File

data class WorkoutModelState(
    val lastUpdatedEpochDay: Long,
    val emaVolume: Float,
    val emaIntensity: Float,
    val emaProgress: Float,
    val minVolume: Float,
    val maxVolume: Float,
    val minIntensity: Float,
    val maxIntensity: Float,
    val sessionsSeen: Int,
    val version: Int = 1
)

interface WorkoutModelStateStore {
    fun load(): WorkoutModelState?
    fun save(state: WorkoutModelState)
}

class SharedPrefsWorkoutModelStateStore(
    context: Context
) : WorkoutModelStateStore {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): WorkoutModelState? {
        if (!prefs.contains(KEY_VERSION)) return null
        return WorkoutModelState(
            lastUpdatedEpochDay = prefs.getLong(KEY_LAST_UPDATED, 0L),
            emaVolume = prefs.getFloat(KEY_EMA_VOLUME, 0f),
            emaIntensity = prefs.getFloat(KEY_EMA_INTENSITY, 0f),
            emaProgress = prefs.getFloat(KEY_EMA_PROGRESS, 0f),
            minVolume = prefs.getFloat(KEY_MIN_VOLUME, 0f),
            maxVolume = prefs.getFloat(KEY_MAX_VOLUME, 0f),
            minIntensity = prefs.getFloat(KEY_MIN_INTENSITY, 0f),
            maxIntensity = prefs.getFloat(KEY_MAX_INTENSITY, 0f),
            sessionsSeen = prefs.getInt(KEY_SESSIONS, 0),
            version = prefs.getInt(KEY_VERSION, 1)
        )
    }

    override fun save(state: WorkoutModelState) {
        prefs.edit()
            .putLong(KEY_LAST_UPDATED, state.lastUpdatedEpochDay)
            .putFloat(KEY_EMA_VOLUME, state.emaVolume)
            .putFloat(KEY_EMA_INTENSITY, state.emaIntensity)
            .putFloat(KEY_EMA_PROGRESS, state.emaProgress)
            .putFloat(KEY_MIN_VOLUME, state.minVolume)
            .putFloat(KEY_MAX_VOLUME, state.maxVolume)
            .putFloat(KEY_MIN_INTENSITY, state.minIntensity)
            .putFloat(KEY_MAX_INTENSITY, state.maxIntensity)
            .putInt(KEY_SESSIONS, state.sessionsSeen)
            .putInt(KEY_VERSION, state.version)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "analytics_model_state"
        private const val KEY_LAST_UPDATED = "last_updated_epoch_day"
        private const val KEY_EMA_VOLUME = "ema_volume"
        private const val KEY_EMA_INTENSITY = "ema_intensity"
        private const val KEY_EMA_PROGRESS = "ema_progress"
        private const val KEY_MIN_VOLUME = "min_volume"
        private const val KEY_MAX_VOLUME = "max_volume"
        private const val KEY_MIN_INTENSITY = "min_intensity"
        private const val KEY_MAX_INTENSITY = "max_intensity"
        private const val KEY_SESSIONS = "sessions_seen"
        private const val KEY_VERSION = "version"
    }
}

interface WorkoutAnalyticsEngine {
    fun analyze(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        profile: UserProfileEntity?,
        muscleDistribution: List<GruppoMuscolarePercentuale>
    ): WorkoutInsights
}

class TfliteWorkoutAnalyzer(
    private val modelFile: File,
    private val fallback: WorkoutAnalyticsEngine
) : WorkoutAnalyticsEngine {
    override fun analyze(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        profile: UserProfileEntity?,
        muscleDistribution: List<GruppoMuscolarePercentuale>
    ): WorkoutInsights {
        if (!modelFile.exists()) {
            return fallback.analyze(schede, esercizi, profile, muscleDistribution)
        }
        // TODO: replace with TFLite inference when the model is available.
        return fallback.analyze(schede, esercizi, profile, muscleDistribution)
    }
}

object AnalyticsEngineProvider {
    private const val MODEL_FILE_NAME = "analytics_model.tflite"

    fun create(context: Context): WorkoutAnalyticsEngine {
        val modelStore = SharedPrefsWorkoutModelStateStore(context)
        val fallback = TensorWorkoutAnalyzer(modelStore)
        val modelFile = File(context.filesDir, MODEL_FILE_NAME)
        return TfliteWorkoutAnalyzer(modelFile, fallback)
    }
}
