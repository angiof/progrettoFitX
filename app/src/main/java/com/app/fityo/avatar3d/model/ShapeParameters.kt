package com.app.fityo.avatar3d.model

import com.app.fityo.avatar3d.processing.Video360Processor
import com.app.fityo.dominio.FfmiEvaluation
import com.app.fityo.dominio.UserProfile
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
import com.google.gson.Gson

/**
 * Parametri di forma per il modello 3D del corpo.
 * Basati sul sistema SMPL/MakeHuman.
 * Tutti i valori sono normalizzati 0.0-1.0.
 */
data class ShapeParameters(
    val height: Float = 0.5f,           // 0=150cm, 1=200cm
    val weight: Float = 0.5f,           // 0=sottopeso, 1=sovrappeso
    val muscle: Float = 0.5f,           // 0=scarso, 1=molto muscoloso
    val shoulderWidth: Float = 0.5f,    // 0=stretto, 1=largo
    val hipWidth: Float = 0.5f,         // 0=stretto, 1=largo
    val torsoLength: Float = 0.5f,      // 0=corto, 1=lungo
    val legLength: Float = 0.5f,        // 0=corto, 1=lungo
    val armLength: Float = 0.5f,        // 0=corto, 1=lungo
    val chestDepth: Float = 0.5f,       // 0=piatto, 1=profondo
    val waistWidth: Float = 0.5f        // 0=stretto, 1=largo
) {
    /**
     * Serializza i parametri in JSON.
     */
    fun toJson(): String = Gson().toJson(this)

    companion object {
        /**
         * Deserializza i parametri da JSON.
         */
        fun fromJson(json: String): ShapeParameters {
            return try {
                Gson().fromJson(json, ShapeParameters::class.java)
            } catch (e: Exception) {
                ShapeParameters()
            }
        }

        /**
         * Calcola i parametri shape dal profilo utente e dalle misurazioni 360°.
         */
        fun fromMeasurements(
            profile: UserProfile,
            measurements: Video360Processor.AggregatedMeasurements,
            bodyMetrics: BodyIntelligenceAnalyzer.BodyMetrics
        ): ShapeParameters {
            // Height: normalizzato tra 150cm e 200cm
            val height = ((profile.heightCm - 150f) / 50f).coerceIn(0f, 1f)

            // Weight: basato su BMI (18-35 range)
            val weight = ((profile.bmi - 18f) / 17f).coerceIn(0f, 1f)

            // Muscle: basato su FFMI evaluation
            val muscle = when (bodyMetrics.ffmiEvaluation) {
                FfmiEvaluation.ELITE -> 0.95f
                FfmiEvaluation.EXCELLENT -> 0.8f
                FfmiEvaluation.OPTIMAL -> 0.6f
                FfmiEvaluation.DEVELOPING -> 0.4f
                FfmiEvaluation.BELOW_AVERAGE -> 0.2f
            }

            // Shoulder width: basato sul rapporto spalle/fianchi
            val shoulderWidth = ((measurements.shoulderToHipRatio - 0.8f) / 0.8f).coerceIn(0f, 1f)

            // Hip width: inverso del rapporto spalle/fianchi
            val hipWidth = (1f - shoulderWidth * 0.5f).coerceIn(0.2f, 0.8f)

            // Torso length: stimato dal rapporto gamba/torso
            val torsoLength = (1f - (measurements.legToTorsoRatio - 0.8f)).coerceIn(0.3f, 0.7f)

            // Leg length: basato sul rapporto gamba/torso
            val legLength = ((measurements.legToTorsoRatio - 0.8f) / 0.6f).coerceIn(0.3f, 0.7f)

            // Arm length: basato sul rapporto braccio/torso
            val armLength = ((measurements.armToTorsoRatio - 0.3f) / 0.4f).coerceIn(0.3f, 0.7f)

            // Chest depth: correlato alla muscolatura e al rapporto spalle/vita
            val chestDepth = (muscle * 0.6f + shoulderWidth * 0.4f).coerceIn(0.2f, 0.9f)

            // Waist width: basato su BF% e rapporto vita
            val bfFactor = (bodyMetrics.estimatedBodyFatPercent / 30f).coerceIn(0f, 1f)
            val waistWidth = (0.3f + bfFactor * 0.4f + (1f - shoulderWidth) * 0.2f).coerceIn(0.2f, 0.8f)

            return ShapeParameters(
                height = height,
                weight = weight,
                muscle = muscle,
                shoulderWidth = shoulderWidth,
                hipWidth = hipWidth,
                torsoLength = torsoLength,
                legLength = legLength,
                armLength = armLength,
                chestDepth = chestDepth,
                waistWidth = waistWidth
            )
        }

        /**
         * Crea parametri di default per un profilo senza misurazioni 360°.
         */
        fun fromProfileOnly(
            profile: UserProfile,
            bodyMetrics: BodyIntelligenceAnalyzer.BodyMetrics
        ): ShapeParameters {
            val height = ((profile.heightCm - 150f) / 50f).coerceIn(0f, 1f)
            val weight = ((profile.bmi - 18f) / 17f).coerceIn(0f, 1f)

            val muscle = when (bodyMetrics.ffmiEvaluation) {
                FfmiEvaluation.ELITE -> 0.95f
                FfmiEvaluation.EXCELLENT -> 0.8f
                FfmiEvaluation.OPTIMAL -> 0.6f
                FfmiEvaluation.DEVELOPING -> 0.4f
                FfmiEvaluation.BELOW_AVERAGE -> 0.2f
            }

            // Valori medi per parametri non misurabili
            return ShapeParameters(
                height = height,
                weight = weight,
                muscle = muscle,
                shoulderWidth = 0.5f + muscle * 0.2f,
                hipWidth = 0.5f - muscle * 0.1f,
                torsoLength = 0.5f,
                legLength = 0.5f,
                armLength = 0.5f,
                chestDepth = 0.4f + muscle * 0.3f,
                waistWidth = 0.5f + weight * 0.2f - muscle * 0.1f
            )
        }
    }
}

/**
 * Colori per le zone muscolari basati sulla valutazione.
 */
data class ZoneColors(
    val shoulders: Long,
    val chest: Long,
    val arms: Long,
    val core: Long,
    val waist: Long,
    val hips: Long,
    val thighs: Long,
    val calves: Long
) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): ZoneColors {
            return try {
                Gson().fromJson(json, ZoneColors::class.java)
            } catch (e: Exception) {
                default()
            }
        }

        fun default(): ZoneColors = ZoneColors(
            shoulders = 0xFF808080,
            chest = 0xFF808080,
            arms = 0xFF808080,
            core = 0xFF808080,
            waist = 0xFF808080,
            hips = 0xFF808080,
            thighs = 0xFF808080,
            calves = 0xFF808080
        )

        /**
         * Crea colori zone dalla lista di analisi zone.
         */
        fun fromZoneAnalysis(
            zones: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>
        ): ZoneColors {
            val zoneMap = zones.associateBy { it.zone }

            return ZoneColors(
                shoulders = zoneMap[BodyIntelligenceAnalyzer.BodyZone.SHOULDERS]?.evaluation?.colorHex ?: 0xFF808080,
                chest = zoneMap[BodyIntelligenceAnalyzer.BodyZone.CHEST]?.evaluation?.colorHex ?: 0xFF808080,
                arms = zoneMap[BodyIntelligenceAnalyzer.BodyZone.ARMS]?.evaluation?.colorHex ?: 0xFF808080,
                core = zoneMap[BodyIntelligenceAnalyzer.BodyZone.CORE]?.evaluation?.colorHex ?: 0xFF808080,
                waist = zoneMap[BodyIntelligenceAnalyzer.BodyZone.WAIST]?.evaluation?.colorHex ?: 0xFF808080,
                hips = zoneMap[BodyIntelligenceAnalyzer.BodyZone.HIPS]?.evaluation?.colorHex ?: 0xFF808080,
                thighs = zoneMap[BodyIntelligenceAnalyzer.BodyZone.THIGHS]?.evaluation?.colorHex ?: 0xFF808080,
                calves = zoneMap[BodyIntelligenceAnalyzer.BodyZone.CALVES]?.evaluation?.colorHex ?: 0xFF808080
            )
        }
    }
}
