package com.app.fityo.dominio

/**
 * Tipi di esercizi supportati dalla modalità Tutor.
 * Ogni esercizio ha una vista consigliata (laterale o frontale) per il rilevamento ottimale.
 */
enum class ExerciseType(
    val displayName: String,
    val description: String,
    val recommendedView: String,
    val musclesTargeted: List<String>,
    val category: ExerciseCategory = ExerciseCategory.LOWER_BODY
) {
    // Lower body exercises
    SQUAT(
        displayName = "Squat",
        description = "Accosciata completa con bilanciere o corpo libero",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Quadricipiti", "Glutei", "Core"),
        category = ExerciseCategory.LOWER_BODY
    ),
    LUNGES(
        displayName = "Affondi",
        description = "Passo in avanti con flessione delle ginocchia",
        recommendedView = "Frontale",
        musclesTargeted = listOf("Quadricipiti", "Glutei", "Equilibrio"),
        category = ExerciseCategory.LOWER_BODY
    ),
    DEADLIFT(
        displayName = "Stacco da Terra",
        description = "Sollevamento del peso da terra con schiena dritta",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Posteriore coscia", "Dorsali", "Core"),
        category = ExerciseCategory.LOWER_BODY
    ),

    // Upper body - Chest exercises
    BENCH_PRESS(
        displayName = "Panca Piana",
        description = "Distensione su panca con bilanciere",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Pettorali", "Tricipiti", "Deltoidi anteriori"),
        category = ExerciseCategory.CHEST
    ),
    DECLINE_BENCH_PRESS(
        displayName = "Panca Declinata",
        description = "Distensione su panca declinata per petto basso",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Pettorali bassi", "Tricipiti", "Deltoidi"),
        category = ExerciseCategory.CHEST
    ),
    CHAIN_BENCH_PRESS(
        displayName = "Panca con Catene",
        description = "Panca piana con catene per resistenza variabile",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Pettorali", "Tricipiti", "Stabilizzatori"),
        category = ExerciseCategory.CHEST
    ),
    CHAOS_PRESS(
        displayName = "Chaos Press",
        description = "Panca con kettlebell appesi per massima instabilità",
        recommendedView = "Laterale",
        musclesTargeted = listOf("Pettorali", "Core", "Stabilizzatori spalla"),
        category = ExerciseCategory.CHEST
    )
}

/**
 * Categorie di esercizi per raggruppamento UI e validazione.
 */
enum class ExerciseCategory(val displayName: String) {
    LOWER_BODY("Gambe"),
    CHEST("Petto"),
    BACK("Schiena"),
    SHOULDERS("Spalle"),
    ARMS("Braccia")
}
