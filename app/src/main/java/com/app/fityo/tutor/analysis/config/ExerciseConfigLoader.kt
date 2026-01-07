package com.app.fityo.tutor.analysis.config

import android.content.Context
import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseType
import org.json.JSONObject
import java.io.BufferedReader

/**
 * Carica le configurazioni degli esercizi dal file JSON in assets.
 * Permette di modificare soglie e messaggi senza ricompilare l'app.
 */
object ExerciseConfigLoader {

    private var configCache: Map<String, ExerciseConfig>? = null
    private var globalSettings: GlobalAnalysisSettings? = null

    private const val CONFIG_FILE = "exercise_configs.json"

    /**
     * Carica la configurazione per un esercizio specifico.
     */
    fun getConfig(context: Context, exerciseType: ExerciseType): ExerciseConfig? {
        ensureLoaded(context)
        return configCache?.get(exerciseType.name)
    }

    /**
     * Ottiene le impostazioni globali.
     */
    fun getGlobalSettings(context: Context): GlobalAnalysisSettings {
        ensureLoaded(context)
        return globalSettings ?: GlobalAnalysisSettings()
    }

    /**
     * Ottiene una soglia specifica per un checkpoint.
     */
    fun getThreshold(
        context: Context,
        exerciseType: ExerciseType,
        checkpointId: String,
        thresholdName: String
    ): ThresholdLevel? {
        val config = getConfig(context, exerciseType) ?: return null
        val checkpoint = config.checkpoints[checkpointId] ?: return null
        return checkpoint.thresholds.find { it.name == thresholdName }
    }

    /**
     * Valuta un valore rispetto alle soglie di un checkpoint.
     * Ritorna il livello di soglia corrispondente.
     */
    fun evaluateCheckpoint(
        context: Context,
        exerciseType: ExerciseType,
        checkpointId: String,
        value: Float
    ): CheckpointResult {
        val config = getConfig(context, exerciseType)
        val checkpoint = config?.checkpoints?.get(checkpointId)

        if (checkpoint == null) {
            return CheckpointResult(
                checkpointId = checkpointId,
                currentValue = value,
                thresholdLevel = null,
                correctionHint = null,
                isWithinTolerance = true
            )
        }

        // Trova il livello di soglia appropriato (dall'ordine nel JSON)
        val matchedThreshold = checkpoint.thresholds.find { threshold ->
            threshold.matches(value)
        }

        val correctionHint = matchedThreshold?.severity?.let { severity ->
            if (severity != ErrorSeverity.SUGGESTION || severity.isEncouraging) {
                checkpoint.correctionHints[severity.name]
            } else null
        }

        return CheckpointResult(
            checkpointId = checkpointId,
            currentValue = value,
            thresholdLevel = matchedThreshold,
            correctionHint = correctionHint,
            isWithinTolerance = matchedThreshold?.severity == ErrorSeverity.SUGGESTION ||
                    matchedThreshold?.severity?.name == "NONE"
        )
    }

    /**
     * Ottiene il messaggio di correzione per una severità.
     */
    fun getCorrectionHint(
        context: Context,
        exerciseType: ExerciseType,
        checkpointId: String,
        severity: ErrorSeverity
    ): String? {
        val config = getConfig(context, exerciseType) ?: return null
        val checkpoint = config.checkpoints[checkpointId] ?: return null
        return checkpoint.correctionHints[severity.name]
    }

    /**
     * Ricarica forzatamente la configurazione (utile per hot-reload in debug).
     */
    fun reload(context: Context) {
        configCache = null
        globalSettings = null
        ensureLoaded(context)
    }

    private fun ensureLoaded(context: Context) {
        if (configCache != null) return

        try {
            val jsonString = context.assets.open(CONFIG_FILE).bufferedReader().use(BufferedReader::readText)
            parseConfig(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback a configurazione vuota
            configCache = emptyMap()
            globalSettings = GlobalAnalysisSettings()
        }
    }

    private fun parseConfig(jsonString: String) {
        val root = JSONObject(jsonString)
        val exercises = root.getJSONObject("exercises")
        val configs = mutableMapOf<String, ExerciseConfig>()

        exercises.keys().forEach { exerciseId ->
            val exerciseJson = exercises.getJSONObject(exerciseId)
            configs[exerciseId] = parseExercise(exerciseId, exerciseJson)
        }

        configCache = configs

        // Parse global settings
        if (root.has("globalSettings")) {
            val settingsJson = root.getJSONObject("globalSettings")
            globalSettings = GlobalAnalysisSettings(
                consecutiveFramesThreshold = settingsJson.optInt("consecutiveFramesThreshold", 3),
                visibilityThreshold = settingsJson.optDouble("visibilityThreshold", 0.5).toFloat(),
                confidenceThreshold = settingsJson.optDouble("confidenceThreshold", 0.6).toFloat(),
                feedbackDelay = settingsJson.optLong("feedbackDelay", 500L)
            )
        }
    }

    private fun parseExercise(exerciseId: String, json: JSONObject): ExerciseConfig {
        val checkpointsJson = json.getJSONObject("checkpoints")
        val checkpoints = mutableMapOf<String, CheckpointConfig>()

        checkpointsJson.keys().forEach { checkpointId ->
            val checkpointJson = checkpointsJson.getJSONObject(checkpointId)
            checkpoints[checkpointId] = parseCheckpoint(checkpointJson)
        }

        return ExerciseConfig(
            exerciseId = exerciseId,
            displayName = json.getString("displayName"),
            description = json.getString("description"),
            checkpoints = checkpoints
        )
    }

    private fun parseCheckpoint(json: JSONObject): CheckpointConfig {
        val thresholdsJson = json.getJSONObject("thresholds")
        val thresholds = mutableListOf<ThresholdLevel>()

        thresholdsJson.keys().forEach { thresholdName ->
            val thresholdJson = thresholdsJson.getJSONObject(thresholdName)
            thresholds.add(parseThreshold(thresholdName, thresholdJson))
        }

        val correctionHintsJson = json.optJSONObject("correctionHints")
        val correctionHints = mutableMapOf<String, String>()
        correctionHintsJson?.keys()?.forEach { key ->
            correctionHints[key] = correctionHintsJson.getString(key)
        }

        return CheckpointConfig(
            joint = json.getString("joint"),
            description = json.getString("description"),
            thresholds = thresholds,
            correctionHints = correctionHints
        )
    }

    private fun parseThreshold(name: String, json: JSONObject): ThresholdLevel {
        val severityStr = json.optString("severity", "NONE")
        val severity = when (severityStr) {
            "NONE" -> ErrorSeverity.SUGGESTION // Tratta NONE come nessun errore
            "SUGGESTION" -> ErrorSeverity.SUGGESTION
            "WARNING" -> ErrorSeverity.WARNING
            "ERROR" -> ErrorSeverity.ERROR
            "CRITICAL" -> ErrorSeverity.CRITICAL
            else -> ErrorSeverity.SUGGESTION
        }

        return ThresholdLevel(
            name = name,
            minValue = if (json.has("min")) json.getDouble("min").toFloat() else null,
            maxValue = if (json.has("max")) json.getDouble("max").toFloat() else null,
            severity = severity,
            message = json.optString("message", "")
        )
    }
}
