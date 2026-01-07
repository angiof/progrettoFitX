package com.app.fityo.trueclone.mesh

import org.json.JSONObject

/**
 * Parametri di forma SMPL (β parameters).
 * Questi controllano la morfologia del corpo umano.
 *
 * SMPL usa 10 parametri principali che controllano:
 * - Altezza/peso generale
 * - Proporzioni torso/gambe/braccia
 * - Larghezza spalle/fianchi
 * - Massa muscolare
 */
data class ShapeParameters(
    // Parametri SMPL standard (β0-β9)
    val beta0: Float = 0f,  // Overall body size (height/weight)
    val beta1: Float = 0f,  // Upper/lower body proportion
    val beta2: Float = 0f,  // Torso width
    val beta3: Float = 0f,  // Hip width
    val beta4: Float = 0f,  // Shoulder width relative to hips
    val beta5: Float = 0f,  // Arm length
    val beta6: Float = 0f,  // Leg length
    val beta7: Float = 0f,  // Torso length
    val beta8: Float = 0f,  // Muscle mass
    val beta9: Float = 0f,  // Body fat distribution

    // Parametri semantici derivati (più intuitivi)
    val heightCm: Float = 175f,
    val weightKg: Float = 70f,
    val shoulderWidthCm: Float = 45f,
    val hipWidthCm: Float = 35f,
    val chestCircumferenceCm: Float = 95f,
    val waistCircumferenceCm: Float = 80f,
    val armLengthCm: Float = 60f,
    val legLengthCm: Float = 85f,
    val neckCircumferenceCm: Float = 38f,
    val bicepsCircumferenceCm: Float = 32f,
    val thighCircumferenceCm: Float = 55f,
    val calfCircumferenceCm: Float = 38f,

    // Proporzioni calcolate
    val shoulderToHipRatio: Float = 1.3f,
    val waistToHipRatio: Float = 0.85f,
    val armToTorsoRatio: Float = 1.0f,
    val legToTorsoRatio: Float = 1.8f,

    // Massa muscolare per zona (0-1)
    val chestMuscle: Float = 0.5f,
    val backMuscle: Float = 0.5f,
    val shoulderMuscle: Float = 0.5f,
    val armMuscle: Float = 0.5f,
    val absMuscle: Float = 0.5f,
    val glutesMuscle: Float = 0.5f,
    val quadsMuscle: Float = 0.5f,
    val hamstringsMuscle: Float = 0.5f,
    val calvesMuscle: Float = 0.5f
) {
    companion object {
        /**
         * Crea parametri di default per un corpo maschile medio.
         */
        fun defaultMale(): ShapeParameters = ShapeParameters(
            heightCm = 175f,
            weightKg = 75f,
            shoulderWidthCm = 46f,
            hipWidthCm = 34f,
            chestCircumferenceCm = 100f,
            waistCircumferenceCm = 85f,
            shoulderToHipRatio = 1.35f
        )

        /**
         * Crea parametri di default per un corpo femminile medio.
         */
        fun defaultFemale(): ShapeParameters = ShapeParameters(
            heightCm = 163f,
            weightKg = 60f,
            shoulderWidthCm = 38f,
            hipWidthCm = 36f,
            chestCircumferenceCm = 90f,
            waistCircumferenceCm = 70f,
            shoulderToHipRatio = 1.05f
        )

        /**
         * Crea da JSON.
         */
        fun fromJson(json: String): ShapeParameters {
            val obj = JSONObject(json)
            return ShapeParameters(
                beta0 = obj.optDouble("beta0", 0.0).toFloat(),
                beta1 = obj.optDouble("beta1", 0.0).toFloat(),
                beta2 = obj.optDouble("beta2", 0.0).toFloat(),
                beta3 = obj.optDouble("beta3", 0.0).toFloat(),
                beta4 = obj.optDouble("beta4", 0.0).toFloat(),
                beta5 = obj.optDouble("beta5", 0.0).toFloat(),
                beta6 = obj.optDouble("beta6", 0.0).toFloat(),
                beta7 = obj.optDouble("beta7", 0.0).toFloat(),
                beta8 = obj.optDouble("beta8", 0.0).toFloat(),
                beta9 = obj.optDouble("beta9", 0.0).toFloat(),
                heightCm = obj.optDouble("heightCm", 175.0).toFloat(),
                weightKg = obj.optDouble("weightKg", 70.0).toFloat(),
                shoulderWidthCm = obj.optDouble("shoulderWidthCm", 45.0).toFloat(),
                hipWidthCm = obj.optDouble("hipWidthCm", 35.0).toFloat(),
                chestCircumferenceCm = obj.optDouble("chestCircumferenceCm", 95.0).toFloat(),
                waistCircumferenceCm = obj.optDouble("waistCircumferenceCm", 80.0).toFloat(),
                armLengthCm = obj.optDouble("armLengthCm", 60.0).toFloat(),
                legLengthCm = obj.optDouble("legLengthCm", 85.0).toFloat(),
                neckCircumferenceCm = obj.optDouble("neckCircumferenceCm", 38.0).toFloat(),
                bicepsCircumferenceCm = obj.optDouble("bicepsCircumferenceCm", 32.0).toFloat(),
                thighCircumferenceCm = obj.optDouble("thighCircumferenceCm", 55.0).toFloat(),
                calfCircumferenceCm = obj.optDouble("calfCircumferenceCm", 38.0).toFloat(),
                shoulderToHipRatio = obj.optDouble("shoulderToHipRatio", 1.3).toFloat(),
                waistToHipRatio = obj.optDouble("waistToHipRatio", 0.85).toFloat(),
                chestMuscle = obj.optDouble("chestMuscle", 0.5).toFloat(),
                backMuscle = obj.optDouble("backMuscle", 0.5).toFloat(),
                shoulderMuscle = obj.optDouble("shoulderMuscle", 0.5).toFloat(),
                armMuscle = obj.optDouble("armMuscle", 0.5).toFloat(),
                absMuscle = obj.optDouble("absMuscle", 0.5).toFloat(),
                glutesMuscle = obj.optDouble("glutesMuscle", 0.5).toFloat(),
                quadsMuscle = obj.optDouble("quadsMuscle", 0.5).toFloat(),
                hamstringsMuscle = obj.optDouble("hamstringsMuscle", 0.5).toFloat(),
                calvesMuscle = obj.optDouble("calvesMuscle", 0.5).toFloat()
            )
        }
    }

    /**
     * Converte in array di β parameters per SMPL.
     */
    fun toBetaArray(): FloatArray = floatArrayOf(
        beta0, beta1, beta2, beta3, beta4,
        beta5, beta6, beta7, beta8, beta9
    )

    /**
     * Serializza in JSON.
     */
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("beta0", beta0.toDouble())
        obj.put("beta1", beta1.toDouble())
        obj.put("beta2", beta2.toDouble())
        obj.put("beta3", beta3.toDouble())
        obj.put("beta4", beta4.toDouble())
        obj.put("beta5", beta5.toDouble())
        obj.put("beta6", beta6.toDouble())
        obj.put("beta7", beta7.toDouble())
        obj.put("beta8", beta8.toDouble())
        obj.put("beta9", beta9.toDouble())
        obj.put("heightCm", heightCm.toDouble())
        obj.put("weightKg", weightKg.toDouble())
        obj.put("shoulderWidthCm", shoulderWidthCm.toDouble())
        obj.put("hipWidthCm", hipWidthCm.toDouble())
        obj.put("chestCircumferenceCm", chestCircumferenceCm.toDouble())
        obj.put("waistCircumferenceCm", waistCircumferenceCm.toDouble())
        obj.put("armLengthCm", armLengthCm.toDouble())
        obj.put("legLengthCm", legLengthCm.toDouble())
        obj.put("neckCircumferenceCm", neckCircumferenceCm.toDouble())
        obj.put("bicepsCircumferenceCm", bicepsCircumferenceCm.toDouble())
        obj.put("thighCircumferenceCm", thighCircumferenceCm.toDouble())
        obj.put("calfCircumferenceCm", calfCircumferenceCm.toDouble())
        obj.put("shoulderToHipRatio", shoulderToHipRatio.toDouble())
        obj.put("waistToHipRatio", waistToHipRatio.toDouble())
        obj.put("chestMuscle", chestMuscle.toDouble())
        obj.put("backMuscle", backMuscle.toDouble())
        obj.put("shoulderMuscle", shoulderMuscle.toDouble())
        obj.put("armMuscle", armMuscle.toDouble())
        obj.put("absMuscle", absMuscle.toDouble())
        obj.put("glutesMuscle", glutesMuscle.toDouble())
        obj.put("quadsMuscle", quadsMuscle.toDouble())
        obj.put("hamstringsMuscle", hamstringsMuscle.toDouble())
        obj.put("calvesMuscle", calvesMuscle.toDouble())
        return obj.toString()
    }

    /**
     * Calcola i β parameters da misurazioni fisiche.
     */
    fun calculateBetaFromMeasurements(): ShapeParameters {
        // Normalizza altezza (150-200 cm → -2 to +2 σ)
        val normalizedHeight = (heightCm - 175f) / 12.5f

        // Normalizza peso relativo all'altezza (BMI-based)
        val bmi = weightKg / ((heightCm / 100f) * (heightCm / 100f))
        val normalizedWeight = (bmi - 22f) / 5f

        // Proporzioni
        val shoulderHipDiff = (shoulderToHipRatio - 1.2f) / 0.3f
        val waistHipDiff = (waistToHipRatio - 0.85f) / 0.15f

        // Lunghezze relative
        val legProportion = (legLengthCm / heightCm - 0.48f) / 0.05f
        val armProportion = (armLengthCm / heightCm - 0.34f) / 0.03f

        // Massa muscolare media
        val avgMuscle = (chestMuscle + backMuscle + shoulderMuscle +
                         armMuscle + absMuscle + glutesMuscle +
                         quadsMuscle + hamstringsMuscle + calvesMuscle) / 9f
        val muscleFactor = (avgMuscle - 0.5f) * 4f

        return copy(
            beta0 = normalizedHeight.coerceIn(-3f, 3f),
            beta1 = legProportion.coerceIn(-3f, 3f),
            beta2 = normalizedWeight.coerceIn(-3f, 3f),
            beta3 = waistHipDiff.coerceIn(-3f, 3f),
            beta4 = shoulderHipDiff.coerceIn(-3f, 3f),
            beta5 = armProportion.coerceIn(-3f, 3f),
            beta6 = legProportion.coerceIn(-3f, 3f),
            beta7 = ((heightCm - legLengthCm) / heightCm - 0.52f) / 0.05f,
            beta8 = muscleFactor.coerceIn(-3f, 3f),
            beta9 = normalizedWeight.coerceIn(-3f, 3f)
        )
    }

    /**
     * Interpola tra due set di parametri.
     */
    fun lerp(other: ShapeParameters, t: Float): ShapeParameters {
        return ShapeParameters(
            beta0 = beta0 + (other.beta0 - beta0) * t,
            beta1 = beta1 + (other.beta1 - beta1) * t,
            beta2 = beta2 + (other.beta2 - beta2) * t,
            beta3 = beta3 + (other.beta3 - beta3) * t,
            beta4 = beta4 + (other.beta4 - beta4) * t,
            beta5 = beta5 + (other.beta5 - beta5) * t,
            beta6 = beta6 + (other.beta6 - beta6) * t,
            beta7 = beta7 + (other.beta7 - beta7) * t,
            beta8 = beta8 + (other.beta8 - beta8) * t,
            beta9 = beta9 + (other.beta9 - beta9) * t,
            heightCm = heightCm + (other.heightCm - heightCm) * t,
            weightKg = weightKg + (other.weightKg - weightKg) * t,
            shoulderWidthCm = shoulderWidthCm + (other.shoulderWidthCm - shoulderWidthCm) * t,
            hipWidthCm = hipWidthCm + (other.hipWidthCm - hipWidthCm) * t,
            chestCircumferenceCm = chestCircumferenceCm + (other.chestCircumferenceCm - chestCircumferenceCm) * t,
            waistCircumferenceCm = waistCircumferenceCm + (other.waistCircumferenceCm - waistCircumferenceCm) * t,
            armLengthCm = armLengthCm + (other.armLengthCm - armLengthCm) * t,
            legLengthCm = legLengthCm + (other.legLengthCm - legLengthCm) * t,
            chestMuscle = chestMuscle + (other.chestMuscle - chestMuscle) * t,
            backMuscle = backMuscle + (other.backMuscle - backMuscle) * t,
            shoulderMuscle = shoulderMuscle + (other.shoulderMuscle - shoulderMuscle) * t,
            armMuscle = armMuscle + (other.armMuscle - armMuscle) * t,
            absMuscle = absMuscle + (other.absMuscle - absMuscle) * t,
            glutesMuscle = glutesMuscle + (other.glutesMuscle - glutesMuscle) * t,
            quadsMuscle = quadsMuscle + (other.quadsMuscle - quadsMuscle) * t,
            hamstringsMuscle = hamstringsMuscle + (other.hamstringsMuscle - hamstringsMuscle) * t,
            calvesMuscle = calvesMuscle + (other.calvesMuscle - calvesMuscle) * t
        )
    }

    /**
     * Calcola la differenza tra due set di parametri (per confronto progressi).
     */
    fun diff(other: ShapeParameters): ShapeParameters {
        return ShapeParameters(
            beta0 = other.beta0 - beta0,
            beta1 = other.beta1 - beta1,
            beta2 = other.beta2 - beta2,
            beta3 = other.beta3 - beta3,
            beta4 = other.beta4 - beta4,
            beta5 = other.beta5 - beta5,
            beta6 = other.beta6 - beta6,
            beta7 = other.beta7 - beta7,
            beta8 = other.beta8 - beta8,
            beta9 = other.beta9 - beta9,
            heightCm = other.heightCm - heightCm,
            weightKg = other.weightKg - weightKg,
            shoulderWidthCm = other.shoulderWidthCm - shoulderWidthCm,
            hipWidthCm = other.hipWidthCm - hipWidthCm,
            chestCircumferenceCm = other.chestCircumferenceCm - chestCircumferenceCm,
            waistCircumferenceCm = other.waistCircumferenceCm - waistCircumferenceCm,
            armLengthCm = other.armLengthCm - armLengthCm,
            legLengthCm = other.legLengthCm - legLengthCm,
            chestMuscle = other.chestMuscle - chestMuscle,
            backMuscle = other.backMuscle - backMuscle,
            shoulderMuscle = other.shoulderMuscle - shoulderMuscle,
            armMuscle = other.armMuscle - armMuscle,
            absMuscle = other.absMuscle - absMuscle,
            glutesMuscle = other.glutesMuscle - glutesMuscle,
            quadsMuscle = other.quadsMuscle - quadsMuscle,
            hamstringsMuscle = other.hamstringsMuscle - hamstringsMuscle,
            calvesMuscle = other.calvesMuscle - calvesMuscle
        )
    }
}
