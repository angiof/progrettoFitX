package com.app.fityo.schede.ai

data class WorkoutPlanRequest(
    val style: String,
    val intensity: String,
    val muscleGroups: List<String>
)

data class WorkoutPlanExercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int?,
    val equipment: String?,
    val weightKg: Float?,
    val notes: String?
)

data class WorkoutPlanResult(
    val exercises: List<WorkoutPlanExercise>,
    val rawResponse: String
)

interface WorkoutPlanGenerator {
    suspend fun generate(request: WorkoutPlanRequest): Result<WorkoutPlanResult>
}
