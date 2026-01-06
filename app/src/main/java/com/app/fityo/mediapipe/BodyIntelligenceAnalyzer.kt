package com.app.fityo.mediapipe

import android.graphics.Bitmap
import android.graphics.Color
import com.app.fityo.biometrics.BiometricMetrics
import com.app.fityo.biometrics.BodyCompositionResult
import com.app.fityo.biometrics.RealBodyMeasurements
import com.app.fityo.dominio.AthleticDiscipline
import com.app.fityo.dominio.BiologicalSex
import com.app.fityo.dominio.FfmiEvaluation
import com.app.fityo.dominio.UserProfile
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Analizzatore Body Intelligence.
 * Combina MediaPipe con i dati biometrici per fornire un'analisi corporea contestualizzata.
 */
class BodyIntelligenceAnalyzer {

    /**
     * Risultato completo dell'analisi Body Intelligence.
     */
    data class AnalysisResult(
        val profile: UserProfile,
        val bodyMetrics: BodyMetrics,
        val bodyZoneAnalysis: List<BodyZoneAnalysis>,
        val overallEvaluation: OverallEvaluation,
        val recommendations: List<Recommendation>,
        val biometricMetrics: BiometricMetrics? = null,
        val bodyComposition: BodyCompositionResult? = null,
        val realMeasurements: RealBodyMeasurements? = null
    )

    /**
     * Metriche corporee calcolate.
     */
    data class BodyMetrics(
        val bmi: Float,
        val estimatedBodyFatPercent: Float,
        val leanMassKg: Float,
        val fatMassKg: Float,
        val ffmi: Float,
        val ffmiEvaluation: FfmiEvaluation,
        val waistToHipRatio: Float?,
        val shoulderToWaistRatio: Float?
    )

    /**
     * Analisi di una singola zona corporea.
     */
    data class BodyZoneAnalysis(
        val zone: BodyZone,
        val evaluation: ZoneEvaluation,
        val percentageScore: Float,      // 0-100
        val comparisonToIdeal: String,   // "Sopra media", "Nella media", "Sotto media"
        val suggestion: String?
    )

    /**
     * Zone corporee analizzabili.
     */
    enum class BodyZone(
        val displayName: String,
        val landmarkIndices: List<Int>
    ) {
        SHOULDERS("Spalle", listOf(11, 12)),
        CHEST("Petto", listOf(11, 12, 23, 24)),
        ARMS("Braccia", listOf(11, 13, 15, 12, 14, 16)),
        CORE("Core/Addome", listOf(11, 12, 23, 24)),
        WAIST("Vita", listOf(23, 24)),
        HIPS("Fianchi", listOf(23, 24)),
        THIGHS("Cosce", listOf(23, 25, 24, 26)),
        CALVES("Polpacci", listOf(25, 27, 26, 28))
    }

    /**
     * Valutazione di una zona.
     */
    enum class ZoneEvaluation(val colorHex: Long) {
        EXCELLENT(0xFF4CAF50),    // Verde brillante
        GOOD(0xFF8BC34A),         // Verde chiaro
        AVERAGE(0xFFFFEB3B),      // Giallo
        NEEDS_WORK(0xFFFF9800),   // Arancione
        UNDERDEVELOPED(0xFFFF5722) // Rosso/Arancione scuro
    }

    /**
     * Valutazione complessiva.
     */
    data class OverallEvaluation(
        val score: Float,              // 0-100
        val category: String,          // "Eccellente", "Buono", etc.
        val disciplineMatch: Float,    // 0-100, quanto il fisico matcha la disciplina
        val strengths: List<String>,
        val areasToImprove: List<String>,
        val motivationalMessage: String
    )

    /**
     * Raccomandazione personalizzata.
     */
    data class Recommendation(
        val zone: BodyZone?,
        val title: String,
        val description: String,
        val priority: Int  // 1 = alta, 3 = bassa
    )

    /**
     * Esegue l'analisi completa del corpo.
     */
    fun analyze(
        bitmap: Bitmap,
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile
    ): AnalysisResult {
        // Calcola metriche base
        val bodyMetrics = calculateBodyMetrics(poseResult, segmentationMask, profile)

        // Analizza ogni zona
        val zoneAnalysis = analyzeBodyZones(poseResult, segmentationMask, profile, bodyMetrics)

        // Valutazione complessiva
        val overallEvaluation = calculateOverallEvaluation(bodyMetrics, zoneAnalysis, profile)

        // Genera raccomandazioni
        val recommendations = generateRecommendations(zoneAnalysis, bodyMetrics, profile)

        return AnalysisResult(
            profile = profile,
            bodyMetrics = bodyMetrics,
            bodyZoneAnalysis = zoneAnalysis,
            overallEvaluation = overallEvaluation,
            recommendations = recommendations
        )
    }

    /**
     * Calcola le metriche corporee.
     */
    private fun calculateBodyMetrics(
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile
    ): BodyMetrics {
        // BMI
        val bmi = profile.bmi

        // Stima Body Fat % basata su BMI, età, sesso e analisi visiva
        val estimatedBf = estimateBodyFatPercent(poseResult, segmentationMask, profile)

        // Massa magra e grassa
        val fatMassKg = profile.weightKg * (estimatedBf / 100f)
        val leanMassKg = profile.weightKg - fatMassKg

        // FFMI
        val ffmi = profile.calculateFfmi(estimatedBf)
        val ffmiEvaluation = profile.evaluateFfmi(ffmi)

        // Rapporti corporei
        val waistToHipRatio = calculateWaistToHipRatio(poseResult, segmentationMask)
        val shoulderToWaistRatio = calculateShoulderToWaistRatio(poseResult, segmentationMask)

        return BodyMetrics(
            bmi = bmi,
            estimatedBodyFatPercent = estimatedBf,
            leanMassKg = leanMassKg,
            fatMassKg = fatMassKg,
            ffmi = ffmi,
            ffmiEvaluation = ffmiEvaluation,
            waistToHipRatio = waistToHipRatio,
            shoulderToWaistRatio = shoulderToWaistRatio
        )
    }

    /**
     * Stima la percentuale di grasso corporeo.
     * Combina formula empirica con analisi visiva.
     */
    private fun estimateBodyFatPercent(
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile
    ): Float {
        // Formula base (Deurenberg et al.)
        val sexFactor = if (profile.sex == BiologicalSex.MALE) 1 else 0
        val baseBf = (1.20f * profile.bmi) + (0.23f * profile.age) - (10.8f * sexFactor) - 5.4f

        // Aggiustamento basato sulla disciplina
        val disciplineAdjustment = when (profile.discipline) {
            AthleticDiscipline.POWERLIFTING -> -3f  // Più massa muscolare
            AthleticDiscipline.BODYBUILDING -> -4f  // Molto muscolo, basso grasso
            AthleticDiscipline.CALISTHENICS -> -2f  // Leggeri ma muscolosi
            AthleticDiscipline.CROSSFIT -> -2f
            AthleticDiscipline.ATHLETICS -> -1f
            AthleticDiscipline.WELLNESS -> 0f
        }

        // Aggiustamento basato su analisi visiva (proporzioni corpo)
        val visualAdjustment = calculateVisualBodyFatAdjustment(poseResult)

        // Risultato finale con limiti ragionevoli
        val result = baseBf + disciplineAdjustment + visualAdjustment
        return result.coerceIn(
            if (profile.sex == BiologicalSex.MALE) 5f else 12f,
            45f
        )
    }

    /**
     * Aggiustamento BF% basato sull'analisi visiva delle proporzioni.
     */
    private fun calculateVisualBodyFatAdjustment(poseResult: PoseLandmarkerResult): Float {
        if (poseResult.landmarks().isEmpty()) return 0f

        val landmarks = poseResult.landmarks()[0]
        if (landmarks.size < 33) return 0f

        // Calcola rapporto spalle/vita
        val shoulderWidth = calculateDistance(landmarks[11], landmarks[12])
        val waistWidth = calculateDistance(landmarks[23], landmarks[24])

        if (waistWidth <= 0) return 0f

        val ratio = shoulderWidth / waistWidth

        // Un rapporto alto indica più muscolo e meno grasso
        return when {
            ratio > 1.6f -> -4f   // V-shape pronunciato
            ratio > 1.4f -> -2f   // Buon V-shape
            ratio > 1.2f -> 0f    // Normale
            ratio > 1.0f -> 2f    // Poco V-shape
            else -> 4f            // Accumulo centrale
        }
    }

    /**
     * Calcola il rapporto vita/fianchi.
     */
    private fun calculateWaistToHipRatio(
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?
    ): Float? {
        if (poseResult.landmarks().isEmpty()) return null

        val landmarks = poseResult.landmarks()[0]
        if (landmarks.size < 25) return null

        val mask = segmentationMask ?: return null
        val band = maskBand(mask.height)

        val hipY = toPixel(
            (landmarks[23].y() + landmarks[24].y()) / 2f,
            mask.height
        )
        val shoulderY = toPixel(
            (landmarks[11].y() + landmarks[12].y()) / 2f,
            mask.height
        )
        val waistY = ((hipY + shoulderY) / 2f).toInt().coerceIn(0, mask.height - 1)

        val hipWidth = measureMaskWidth(mask, hipY, band)
        val waistWidth = measureMaskWidth(mask, waistY, band)

        return if (hipWidth > 0f && waistWidth > 0f) waistWidth / hipWidth else null
    }

    /**
     * Calcola il rapporto spalle/vita.
     */
    private fun calculateShoulderToWaistRatio(
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?
    ): Float? {
        if (poseResult.landmarks().isEmpty()) return null

        val landmarks = poseResult.landmarks()[0]
        if (landmarks.size < 25) return null

        val mask = segmentationMask
        val (shoulderWidth, waistWidth) = if (mask != null) {
            val band = maskBand(mask.height)
            val shoulderY = toPixel(
                (landmarks[11].y() + landmarks[12].y()) / 2f,
                mask.height
            )
            val hipY = toPixel(
                (landmarks[23].y() + landmarks[24].y()) / 2f,
                mask.height
            )
            val waistY = ((hipY + shoulderY) / 2f).toInt().coerceIn(0, mask.height - 1)

            val shoulder = measureMaskWidth(mask, shoulderY, band)
            val waist = measureMaskWidth(mask, waistY, band)
            Pair(shoulder, waist)
        } else {
            Pair(
                calculateDistance(landmarks[11], landmarks[12]),
                calculateDistance(landmarks[23], landmarks[24])
            )
        }

        return if (waistWidth > 0) shoulderWidth / waistWidth else null
    }

    /**
     * Analizza tutte le zone corporee.
     */
    private fun analyzeBodyZones(
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile,
        bodyMetrics: BodyMetrics
    ): List<BodyZoneAnalysis> {
        val zones = mutableListOf<BodyZoneAnalysis>()

        // Analizza ogni zona
        BodyZone.entries.forEach { zone ->
            val analysis = analyzeZone(zone, poseResult, segmentationMask, profile, bodyMetrics)
            zones.add(analysis)
        }

        return zones
    }

    /**
     * Analizza una singola zona corporea.
     */
    private fun analyzeZone(
        zone: BodyZone,
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile,
        bodyMetrics: BodyMetrics
    ): BodyZoneAnalysis {
        // Calcola score per la zona (0-100)
        val score = calculateZoneScore(zone, poseResult, segmentationMask, profile, bodyMetrics)

        // Determina valutazione
        val evaluation = when {
            score >= 85 -> ZoneEvaluation.EXCELLENT
            score >= 70 -> ZoneEvaluation.GOOD
            score >= 50 -> ZoneEvaluation.AVERAGE
            score >= 30 -> ZoneEvaluation.NEEDS_WORK
            else -> ZoneEvaluation.UNDERDEVELOPED
        }

        // Comparazione con ideale per la disciplina
        val comparison = when {
            score >= 80 -> "Sopra la media"
            score >= 50 -> "Nella media"
            else -> "Sotto la media"
        }

        // Suggerimento specifico
        val suggestion = generateZoneSuggestion(zone, score, profile.discipline)

        return BodyZoneAnalysis(
            zone = zone,
            evaluation = evaluation,
            percentageScore = score,
            comparisonToIdeal = comparison,
            suggestion = suggestion
        )
    }

    /**
     * Calcola lo score per una zona specifica.
     * Score alto = zona sviluppata, Score basso = zona da migliorare
     */
    private fun calculateZoneScore(
        zone: BodyZone,
        poseResult: PoseLandmarkerResult,
        segmentationMask: Bitmap?,
        profile: UserProfile,
        bodyMetrics: BodyMetrics
    ): Float {
        if (poseResult.landmarks().isEmpty()) return 50f

        val landmarks = poseResult.landmarks()[0]
        if (landmarks.size < 33) return 50f

        // Score base da FFMI (contribuisce al 30% dello score finale)
        val baseScore = mapValueToScore(
            bodyMetrics.ffmi,
            profile.discipline.idealFfmiRange.start - 3,
            profile.discipline.idealFfmiRange.endInclusive + 3
        ) * 0.3f

        // Score specifico per zona (contribuisce al 70%)
        val zoneSpecificScore = when (zone) {
            BodyZone.SHOULDERS -> {
                // Rapporto spalle/fianchi - spalle larghe = score alto
                val shoulderWidth = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val shoulderY = toPixel(
                        (landmarks[11].y() + landmarks[12].y()) / 2f,
                        mask.height
                    )
                    measureMaskWidth(mask, shoulderY, band)
                } ?: calculateDistance(landmarks[11], landmarks[12])

                val hipWidth = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val hipY = toPixel(
                        (landmarks[23].y() + landmarks[24].y()) / 2f,
                        mask.height
                    )
                    measureMaskWidth(mask, hipY, band)
                } ?: calculateDistance(landmarks[23], landmarks[24])

                if (hipWidth > 0) {
                    val ratio = shoulderWidth / hipWidth
                    when (profile.discipline) {
                        AthleticDiscipline.BODYBUILDING -> mapRatioToScore(ratio, 1.1f, 1.7f)
                        AthleticDiscipline.POWERLIFTING -> mapRatioToScore(ratio, 1.0f, 1.5f)
                        AthleticDiscipline.CALISTHENICS -> mapRatioToScore(ratio, 1.1f, 1.6f)
                        else -> mapRatioToScore(ratio, 1.0f, 1.5f)
                    }
                } else 50f
            }

            BodyZone.CHEST -> {
                // Petto: valuta l'ampiezza del torso superiore rispetto alla vita
                // Petto sviluppato = torso superiore più largo della vita
                val shoulderMidY = (landmarks[11].y() + landmarks[12].y()) / 2
                val hipMidY = (landmarks[23].y() + landmarks[24].y()) / 2
                val torsoHeight = abs(hipMidY - shoulderMidY)

                val (chestWidth, waistWidth) = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val shoulderY = toPixel(shoulderMidY, mask.height)
                    val hipY = toPixel(hipMidY, mask.height)
                    val chestY = ((shoulderY + hipY) / 2.5f).toInt().coerceIn(0, mask.height - 1)
                    val waistY = ((shoulderY + hipY) / 2f).toInt().coerceIn(0, mask.height - 1)
                    val chest = measureMaskWidth(mask, chestY, band)
                    val waist = measureMaskWidth(mask, waistY, band)
                    Pair(chest, waist)
                } ?: Pair(
                    calculateDistance(landmarks[11], landmarks[12]),
                    calculateDistance(landmarks[23], landmarks[24])
                )

                // Rapporto petto: spalle larghe + torso "solido" = petto sviluppato
                if (waistWidth > 0 && torsoHeight > 0) {
                    val chestRatio = chestWidth / waistWidth
                    // Bonus per rapporto V-shape (indica petto sviluppato)
                    val vShapeBonus = when {
                        chestRatio > 1.5f -> 20f
                        chestRatio > 1.3f -> 10f
                        chestRatio > 1.1f -> 5f
                        else -> 0f
                    }
                    val baseChestScore = mapRatioToScore(chestRatio, 1.0f, 1.6f)
                    (baseChestScore + vShapeBonus).coerceAtMost(100f)
                } else 50f
            }

            BodyZone.ARMS -> {
                val maskScore = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val leftShoulderX = toPixel(landmarks[11].x(), mask.width)
                    val rightShoulderX = toPixel(landmarks[12].x(), mask.width)

                    val leftArmY = toPixel(
                        (landmarks[11].y() + landmarks[13].y()) / 2f,
                        mask.height
                    )
                    val rightArmY = toPixel(
                        (landmarks[12].y() + landmarks[14].y()) / 2f,
                        mask.height
                    )

                    val leftRange = 0 until leftShoulderX.coerceAtLeast(1)
                    val rightRange = rightShoulderX.coerceAtMost(mask.width - 1) until mask.width

                    val armWidths = listOf(
                        measureMaskWidth(mask, leftArmY, band, leftRange),
                        measureMaskWidth(mask, rightArmY, band, rightRange)
                    ).filter { it > 0f }

                    val shoulderWidth = measureMaskWidth(
                        mask,
                        toPixel((landmarks[11].y() + landmarks[12].y()) / 2f, mask.height),
                        band
                    )

                    if (armWidths.isNotEmpty() && shoulderWidth > 0f) {
                        val avgArmWidth = armWidths.average().toFloat()
                        val armRatio = avgArmWidth / shoulderWidth
                        mapRatioToScore(armRatio, 0.15f, 0.35f)
                    } else {
                        null
                    }
                }

                maskScore ?: run {
                // Braccia: valuta la lunghezza/proporzione delle braccia
                // Braccia più "spesse" hanno landmark più distanti tra spalla-gomito-polso
                val leftUpperArm = calculateDistance(landmarks[11], landmarks[13])
                val leftForearm = calculateDistance(landmarks[13], landmarks[15])
                val rightUpperArm = calculateDistance(landmarks[12], landmarks[14])
                val rightForearm = calculateDistance(landmarks[14], landmarks[16])

                val avgArmLength = (leftUpperArm + leftForearm + rightUpperArm + rightForearm) / 4

                // Rapporto braccia rispetto all'altezza del torso
                val torsoHeight = abs(landmarks[11].y() - landmarks[23].y())

                if (torsoHeight > 0) {
                    val armRatio = avgArmLength / torsoHeight
                    // Braccia più lunghe relative al torso indicano sviluppo
                    when (profile.discipline) {
                        AthleticDiscipline.BODYBUILDING -> mapRatioToScore(armRatio, 0.3f, 0.7f)
                        AthleticDiscipline.POWERLIFTING -> mapRatioToScore(armRatio, 0.3f, 0.6f)
                        else -> mapRatioToScore(armRatio, 0.3f, 0.6f)
                    }
                } else 50f
                }
            }

            BodyZone.CORE -> {
                // Core: combinazione di FFMI (muscolo) e BF% inverso (definizione)
                val ffmiScore = mapValueToScore(
                    bodyMetrics.ffmi,
                    profile.discipline.idealFfmiRange.start,
                    profile.discipline.idealFfmiRange.endInclusive
                )
                // BF% basso = core più visibile
                val bfScore = (100f - bodyMetrics.estimatedBodyFatPercent * 2.5f).coerceIn(0f, 100f)
                (ffmiScore * 0.6f + bfScore * 0.4f)
            }

            BodyZone.WAIST -> {
                // Vita stretta è meglio - usa rapporto spalle/vita
                val ratio = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val shoulderY = toPixel(
                        (landmarks[11].y() + landmarks[12].y()) / 2f,
                        mask.height
                    )
                    val hipY = toPixel(
                        (landmarks[23].y() + landmarks[24].y()) / 2f,
                        mask.height
                    )
                    val waistY = ((shoulderY + hipY) / 2f).toInt().coerceIn(0, mask.height - 1)
                    val shoulderWidth = measureMaskWidth(mask, shoulderY, band)
                    val waistWidth = measureMaskWidth(mask, waistY, band)
                    if (waistWidth > 0f) shoulderWidth / waistWidth else null
                } ?: bodyMetrics.shoulderToWaistRatio

                ratio?.let { value ->
                    // Ratio alto = vita stretta rispetto alle spalle = score alto
                    mapRatioToScore(value, 1.0f, 1.8f)
                } ?: 50f
            }

            BodyZone.HIPS -> {
                // Fianchi: dipende dalla disciplina
                val hipWidth = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val hipY = toPixel(
                        (landmarks[23].y() + landmarks[24].y()) / 2f,
                        mask.height
                    )
                    measureMaskWidth(mask, hipY, band)
                } ?: calculateDistance(landmarks[23], landmarks[24])

                val shoulderWidth = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val shoulderY = toPixel(
                        (landmarks[11].y() + landmarks[12].y()) / 2f,
                        mask.height
                    )
                    measureMaskWidth(mask, shoulderY, band)
                } ?: calculateDistance(landmarks[11], landmarks[12])

                if (shoulderWidth > 0) {
                    val hipToShoulderRatio = hipWidth / shoulderWidth
                    when (profile.discipline) {
                        // Per bodybuilding, fianchi stretti sono preferiti
                        AthleticDiscipline.BODYBUILDING ->
                            mapRatioToScore(1f - hipToShoulderRatio, 0.2f, 0.5f)
                        // Per powerlifting, fianchi più larghi danno stabilità
                        AthleticDiscipline.POWERLIFTING ->
                            mapRatioToScore(hipToShoulderRatio, 0.6f, 0.9f)
                        else -> 50f + (0.8f - hipToShoulderRatio) * 50f
                    }.coerceIn(30f, 100f)
                } else 50f
            }

            BodyZone.THIGHS -> {
                val maskScore = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val hipCenterX = toPixel(
                        (landmarks[23].x() + landmarks[24].x()) / 2f,
                        mask.width
                    )
                    val safeCenterX = hipCenterX.coerceIn(1, mask.width - 2)
                    val leftRange = 0 until safeCenterX
                    val rightRange = safeCenterX until mask.width

                    val leftThighY = toPixel(
                        (landmarks[23].y() + landmarks[25].y()) / 2f,
                        mask.height
                    )
                    val rightThighY = toPixel(
                        (landmarks[24].y() + landmarks[26].y()) / 2f,
                        mask.height
                    )

                    val thighWidths = listOf(
                        measureMaskWidth(mask, leftThighY, band, leftRange),
                        measureMaskWidth(mask, rightThighY, band, rightRange)
                    ).filter { it > 0f }

                    val hipWidth = measureMaskWidth(
                        mask,
                        toPixel((landmarks[23].y() + landmarks[24].y()) / 2f, mask.height),
                        band
                    )

                    if (thighWidths.isNotEmpty() && hipWidth > 0f) {
                        val avgThighWidth = thighWidths.average().toFloat()
                        val thighRatio = avgThighWidth / hipWidth
                        val multiplier = when (profile.discipline) {
                            AthleticDiscipline.POWERLIFTING -> 1.3f
                            AthleticDiscipline.BODYBUILDING -> 1.1f
                            AthleticDiscipline.CALISTHENICS -> 0.9f
                            else -> 1.0f
                        }
                        (mapRatioToScore(thighRatio, 0.35f, 0.8f) * multiplier).coerceIn(20f, 100f)
                    } else {
                        null
                    }
                }

                maskScore ?: run {
                // Cosce: lunghezza del segmento coscia come proxy per sviluppo
                val leftThigh = calculateDistance(landmarks[23], landmarks[25])
                val rightThigh = calculateDistance(landmarks[24], landmarks[26])
                val avgThighLength = (leftThigh + rightThigh) / 2

                val torsoHeight = abs(landmarks[11].y() - landmarks[23].y())

                if (torsoHeight > 0) {
                    val thighRatio = avgThighLength / torsoHeight
                    val multiplier = when (profile.discipline) {
                        AthleticDiscipline.POWERLIFTING -> 1.3f // Cosce grandi = bonus
                        AthleticDiscipline.BODYBUILDING -> 1.1f
                        AthleticDiscipline.CALISTHENICS -> 0.9f // Cosce più leggere
                        else -> 1.0f
                    }
                    (mapRatioToScore(thighRatio, 0.4f, 0.8f) * multiplier).coerceIn(20f, 100f)
                } else 50f
                }
            }

            BodyZone.CALVES -> {
                val maskScore = segmentationMask?.let { mask ->
                    val band = maskBand(mask.height)
                    val centerX = toPixel(
                        (landmarks[23].x() + landmarks[24].x()) / 2f,
                        mask.width
                    )
                    val safeCenterX = centerX.coerceIn(1, mask.width - 2)
                    val leftRange = 0 until safeCenterX
                    val rightRange = safeCenterX until mask.width

                    val leftCalfY = toPixel(
                        (landmarks[25].y() + landmarks[27].y()) / 2f,
                        mask.height
                    )
                    val rightCalfY = toPixel(
                        (landmarks[26].y() + landmarks[28].y()) / 2f,
                        mask.height
                    )
                    val leftThighY = toPixel(
                        (landmarks[23].y() + landmarks[25].y()) / 2f,
                        mask.height
                    )
                    val rightThighY = toPixel(
                        (landmarks[24].y() + landmarks[26].y()) / 2f,
                        mask.height
                    )

                    val calfWidths = listOf(
                        measureMaskWidth(mask, leftCalfY, band, leftRange),
                        measureMaskWidth(mask, rightCalfY, band, rightRange)
                    ).filter { it > 0f }

                    val thighWidths = listOf(
                        measureMaskWidth(mask, leftThighY, band, leftRange),
                        measureMaskWidth(mask, rightThighY, band, rightRange)
                    ).filter { it > 0f }

                    if (calfWidths.isNotEmpty() && thighWidths.isNotEmpty()) {
                        val avgCalfWidth = calfWidths.average().toFloat()
                        val avgThighWidth = thighWidths.average().toFloat()
                        if (avgThighWidth > 0f) {
                            val calfRatio = avgCalfWidth / avgThighWidth
                            mapRatioToScore(calfRatio, 0.3f, 0.8f)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

                maskScore ?: run {
                    // Fallback: rapporto lunghezze ginocchio-caviglia / coscia
                    val leftCalf = calculateDistance(landmarks[25], landmarks[27])
                    val rightCalf = calculateDistance(landmarks[26], landmarks[28])
                    val avgCalfLength = (leftCalf + rightCalf) / 2

                    val leftThigh = calculateDistance(landmarks[23], landmarks[25])
                    val rightThigh = calculateDistance(landmarks[24], landmarks[26])
                    val avgThighLength = (leftThigh + rightThigh) / 2

                    if (avgThighLength > 0) {
                        val calfRatio = avgCalfLength / avgThighLength
                        mapRatioToScore(calfRatio, 0.7f, 1.1f)
                    } else 50f
                }
            }
        }

        // Score finale: combinazione di base + specifico per zona
        val finalScore = baseScore + (zoneSpecificScore * 0.7f)
        return finalScore.coerceIn(0f, 100f)
    }

    /**
     * Mappa un rapporto a uno score 0-100.
     */
    private fun mapRatioToScore(value: Float, min: Float, max: Float): Float {
        return ((value - min) / (max - min) * 100f).coerceIn(0f, 100f)
    }

    /**
     * Mappa un valore a uno score 0-100.
     */
    private fun mapValueToScore(value: Float, min: Float, max: Float): Float {
        return ((value - min) / (max - min) * 100f).coerceIn(0f, 100f)
    }

    /**
     * Genera suggerimento per una zona.
     */
    private fun generateZoneSuggestion(
        zone: BodyZone,
        score: Float,
        discipline: AthleticDiscipline
    ): String? {
        if (score >= 70) return null

        return when (zone) {
            BodyZone.SHOULDERS -> when (discipline) {
                AthleticDiscipline.BODYBUILDING -> "Aggiungi più lavoro sui deltoidi laterali per aumentare la larghezza"
                AthleticDiscipline.POWERLIFTING -> "Rafforza le spalle con overhead press pesanti"
                else -> "Includi esercizi per le spalle 2-3 volte a settimana"
            }
            BodyZone.CHEST -> "Aumenta il volume di allenamento per il petto con variazioni di angolazione"
            BodyZone.ARMS -> "Aggiungi lavoro di isolamento per bicipiti e tricipiti"
            BodyZone.CORE -> "Includi esercizi per il core e considera la nutrizione per la definizione"
            BodyZone.WAIST -> "Focus sulla nutrizione e cardio per ridurre il girovita"
            BodyZone.HIPS -> "Lavora su hip thrusts e abductor machine"
            BodyZone.THIGHS -> when (discipline) {
                AthleticDiscipline.POWERLIFTING -> "Aumenta il volume di squat e leg press"
                else -> "Includi più lavoro per quadricipiti e femorali"
            }
            BodyZone.CALVES -> "Aggiungi lavoro specifico per i polpacci 3-4 volte a settimana"
        }
    }

    /**
     * Calcola la valutazione complessiva.
     */
    private fun calculateOverallEvaluation(
        bodyMetrics: BodyMetrics,
        zoneAnalysis: List<BodyZoneAnalysis>,
        profile: UserProfile
    ): OverallEvaluation {
        // Score medio delle zone
        val avgScore = zoneAnalysis.map { it.percentageScore }.average().toFloat()

        // Quanto il fisico matcha la disciplina
        val disciplineMatch = calculateDisciplineMatch(bodyMetrics, zoneAnalysis, profile)

        // Categoria
        val category = when {
            avgScore >= 85 -> "Eccellente"
            avgScore >= 70 -> "Molto Buono"
            avgScore >= 55 -> "Buono"
            avgScore >= 40 -> "Nella Media"
            else -> "In Sviluppo"
        }

        // Punti di forza
        val strengths = zoneAnalysis
            .filter { it.percentageScore >= 70 }
            .map { it.zone.displayName }

        // Aree da migliorare
        val areasToImprove = zoneAnalysis
            .filter { it.percentageScore < 50 }
            .map { it.zone.displayName }

        // Messaggio motivazionale
        val motivationalMessage = generateMotivationalMessage(avgScore, disciplineMatch, profile)

        return OverallEvaluation(
            score = avgScore,
            category = category,
            disciplineMatch = disciplineMatch,
            strengths = strengths,
            areasToImprove = areasToImprove,
            motivationalMessage = motivationalMessage
        )
    }

    /**
     * Calcola quanto il fisico matcha la disciplina.
     */
    private fun calculateDisciplineMatch(
        bodyMetrics: BodyMetrics,
        zoneAnalysis: List<BodyZoneAnalysis>,
        profile: UserProfile
    ): Float {
        var matchScore = 50f

        // FFMI nel range ideale
        if (bodyMetrics.ffmi in profile.discipline.idealFfmiRange) {
            matchScore += 20f
        }

        // BMI nel range ideale
        if (profile.isBmiIdealForDiscipline) {
            matchScore += 10f
        }

        // Zone prioritarie per la disciplina
        val priorityZones = profile.discipline.priorityAreas
        val priorityScores = zoneAnalysis
            .filter { priorityZones.any { pz -> it.zone.displayName.contains(pz, ignoreCase = true) } }
            .map { it.percentageScore }

        if (priorityScores.isNotEmpty()) {
            val avgPriorityScore = priorityScores.average().toFloat()
            matchScore += (avgPriorityScore - 50) * 0.4f
        }

        return matchScore.coerceIn(0f, 100f)
    }

    /**
     * Genera messaggio motivazionale personalizzato.
     */
    private fun generateMotivationalMessage(
        avgScore: Float,
        disciplineMatch: Float,
        profile: UserProfile
    ): String {
        return when {
            avgScore >= 80 && disciplineMatch >= 70 ->
                "Sei sulla strada giusta per ${profile.discipline.displayName}! Il tuo fisico è ben sviluppato e in linea con i tuoi obiettivi. Continua così! 💪"

            avgScore >= 60 && disciplineMatch >= 50 ->
                "Buoni progressi! Hai una base solida per ${profile.discipline.displayName}. Con costanza raggiungerai i tuoi obiettivi! 📈"

            avgScore >= 40 ->
                "Stai costruendo le fondamenta! Ogni sessione di allenamento ti avvicina ai tuoi obiettivi di ${profile.discipline.displayName}. Non mollare! 🎯"

            else ->
                "Ogni campione ha iniziato da qualche parte! Sei all'inizio del tuo percorso in ${profile.discipline.displayName}. La costanza è la chiave del successo! 🌟"
        }
    }

    /**
     * Genera raccomandazioni personalizzate.
     */
    private fun generateRecommendations(
        zoneAnalysis: List<BodyZoneAnalysis>,
        bodyMetrics: BodyMetrics,
        profile: UserProfile
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // Raccomandazioni per zone deboli
        zoneAnalysis
            .filter { it.percentageScore < 50 }
            .sortedBy { it.percentageScore }
            .take(3)
            .forEach { zone ->
                zone.suggestion?.let { suggestion ->
                    recommendations.add(
                        Recommendation(
                            zone = zone.zone,
                            title = "Migliora ${zone.zone.displayName}",
                            description = suggestion,
                            priority = if (zone.percentageScore < 30) 1 else 2
                        )
                    )
                }
            }

        // Raccomandazione nutrizionale se BF% alto
        if (bodyMetrics.estimatedBodyFatPercent > 20 && profile.sex == BiologicalSex.MALE ||
            bodyMetrics.estimatedBodyFatPercent > 28 && profile.sex == BiologicalSex.FEMALE) {
            recommendations.add(
                Recommendation(
                    zone = null,
                    title = "Ottimizza la Nutrizione",
                    description = "Considera un leggero deficit calorico per migliorare la composizione corporea e la definizione muscolare.",
                    priority = 2
                )
            )
        }

        // Raccomandazione per FFMI basso
        if (bodyMetrics.ffmiEvaluation == FfmiEvaluation.BELOW_AVERAGE ||
            bodyMetrics.ffmiEvaluation == FfmiEvaluation.DEVELOPING) {
            recommendations.add(
                Recommendation(
                    zone = null,
                    title = "Aumenta la Massa Muscolare",
                    description = "Focus su progressive overload e alimentazione adeguata con sufficiente proteine (1.6-2.2g/kg).",
                    priority = 1
                )
            )
        }

        return recommendations.sortedBy { it.priority }
    }

    /**
     * Calcola la distanza tra due landmark.
     */
    private fun calculateDistance(
        p1: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        p2: com.google.mediapipe.tasks.components.containers.NormalizedLandmark
    ): Float {
        val dx = p1.x() - p2.x()
        val dy = p1.y() - p2.y()
        return sqrt(dx * dx + dy * dy)
    }

    private fun toPixel(value: Float, size: Int): Int {
        return (value * size).toInt().coerceIn(0, size - 1)
    }

    private fun maskBand(height: Int): Int {
        return (height * 0.01f).toInt().coerceAtLeast(2)
    }

    private fun measureMaskWidth(
        mask: Bitmap,
        centerY: Int,
        band: Int,
        xRange: IntRange? = null
    ): Float {
        val width = mask.width
        val height = mask.height
        if (width == 0 || height == 0) return 0f

        val startY = (centerY - band).coerceIn(0, height - 1)
        val endY = (centerY + band).coerceIn(0, height - 1)
        val startX = xRange?.first?.coerceIn(0, width - 1) ?: 0
        val endX = xRange?.last?.coerceIn(0, width - 1) ?: (width - 1)
        if (startX >= endX) return 0f

        var totalWidth = 0
        var rows = 0

        for (y in startY..endY) {
            var minX = Int.MAX_VALUE
            var maxX = Int.MIN_VALUE
            for (x in startX..endX) {
                if (Color.alpha(mask.getPixel(x, y)) > MASK_ALPHA_THRESHOLD) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                }
            }
            if (maxX >= minX) {
                totalWidth += (maxX - minX)
                rows++
            }
        }

        return if (rows > 0) totalWidth.toFloat() / rows else 0f
    }

    companion object {
        private const val MASK_ALPHA_THRESHOLD = 128
    }
}
