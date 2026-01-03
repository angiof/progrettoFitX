package com.app.fityo.dominio

/**
 * Disciplina atletica dell'utente.
 * Influenza come l'IA interpreta le misure corporee.
 */
enum class AthleticDiscipline(
    val displayName: String,
    val description: String,
    val idealBmiRange: ClosedFloatingPointRange<Float>,
    val idealFfmiRange: ClosedFloatingPointRange<Float>,
    val priorityAreas: List<String>
) {
    POWERLIFTING(
        displayName = "Powerlifting",
        description = "Forza massimale su squat, panca, stacco",
        idealBmiRange = 25f..35f,
        idealFfmiRange = 22f..28f,
        priorityAreas = listOf("Core", "Quadricipiti", "Dorsali", "Glutei")
    ),
    BODYBUILDING(
        displayName = "Bodybuilding",
        description = "Simmetria, definizione e massa muscolare",
        idealBmiRange = 23f..30f,
        idealFfmiRange = 21f..26f,
        priorityAreas = listOf("Simmetria", "Definizione", "Proporzioni", "V-Taper")
    ),
    CALISTHENICS(
        displayName = "Calisthenics",
        description = "Controllo del corpo, forza relativa",
        idealBmiRange = 20f..26f,
        idealFfmiRange = 19f..24f,
        priorityAreas = listOf("Rapporto Forza/Peso", "Core", "Dorsali", "Spalle")
    ),
    WELLNESS(
        displayName = "Wellness/Fitness",
        description = "Salute generale e forma fisica",
        idealBmiRange = 18.5f..25f,
        idealFfmiRange = 17f..22f,
        priorityAreas = listOf("Composizione Corporea", "Salute Cardiovascolare", "Equilibrio")
    ),
    CROSSFIT(
        displayName = "CrossFit",
        description = "Fitness funzionale ad alta intensità",
        idealBmiRange = 22f..28f,
        idealFfmiRange = 20f..25f,
        priorityAreas = listOf("Resistenza", "Potenza", "Mobilità", "Core")
    ),
    ATHLETICS(
        displayName = "Atletica",
        description = "Performance sportiva generale",
        idealBmiRange = 19f..26f,
        idealFfmiRange = 18f..24f,
        priorityAreas = listOf("Esplosività", "Agilità", "Resistenza", "Forza")
    )
}

/**
 * Genere biologico per calcoli FFMI accurati.
 */
enum class BiologicalSex(val displayName: String) {
    MALE("Maschio"),
    FEMALE("Femmina")
}

/**
 * Profilo utente con dati biometrici e preferenze.
 */
data class UserProfile(
    val id: Int? = null,
    val name: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val sex: BiologicalSex,
    val discipline: AthleticDiscipline,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Calcola il BMI (Body Mass Index).
     */
    val bmi: Float
        get() {
            val heightM = heightCm / 100f
            return weightKg / (heightM * heightM)
        }

    /**
     * Categoria BMI standard (per riferimento).
     */
    val bmiCategory: String
        get() = when {
            bmi < 18.5f -> "Sottopeso"
            bmi < 25f -> "Normopeso"
            bmi < 30f -> "Sovrappeso"
            else -> "Obeso"
        }

    /**
     * Verifica se il BMI è nel range ideale per la disciplina.
     */
    val isBmiIdealForDiscipline: Boolean
        get() = bmi in discipline.idealBmiRange

    /**
     * Calcola il FFMI stimato (richiede body fat %).
     * FFMI = (Massa Magra / Altezza²) + 6.1 × (1.8 - Altezza)
     */
    fun calculateFfmi(bodyFatPercent: Float): Float {
        val heightM = heightCm / 100f
        val leanMass = weightKg * (1 - bodyFatPercent / 100f)
        val baseFfmi = leanMass / (heightM * heightM)
        // Correzione per altezza (normalizzazione a 1.8m)
        return baseFfmi + 6.1f * (1.8f - heightM)
    }

    /**
     * Valuta il FFMI rispetto alla disciplina.
     */
    fun evaluateFfmi(ffmi: Float): FfmiEvaluation {
        val range = discipline.idealFfmiRange
        return when {
            ffmi < range.start - 2 -> FfmiEvaluation.BELOW_AVERAGE
            ffmi < range.start -> FfmiEvaluation.DEVELOPING
            ffmi in range -> FfmiEvaluation.OPTIMAL
            ffmi <= range.endInclusive + 2 -> FfmiEvaluation.EXCELLENT
            else -> FfmiEvaluation.ELITE
        }
    }
}

/**
 * Valutazione del FFMI rispetto alla disciplina.
 */
enum class FfmiEvaluation(
    val displayName: String,
    val description: String,
    val colorHex: Long
) {
    BELOW_AVERAGE(
        "Sotto la Media",
        "Margine di miglioramento significativo",
        0xFFFF5722 // Deep Orange
    ),
    DEVELOPING(
        "In Sviluppo",
        "Sulla strada giusta, continua così",
        0xFFFF9800 // Orange
    ),
    OPTIMAL(
        "Ottimale",
        "Nel range ideale per la tua disciplina",
        0xFF4CAF50 // Green
    ),
    EXCELLENT(
        "Eccellente",
        "Sopra la media, ottimi risultati",
        0xFF2196F3 // Blue
    ),
    ELITE(
        "Elite",
        "Livello atleta avanzato/professionista",
        0xFF9C27B0 // Purple
    )
}
