package com.app.fityo.chat.engine

import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Provider per recuperare dati di allenamento dal database.
 * Fornisce metodi specifici per ogni tipo di query del chatbot.
 */
class WorkoutDataProvider(private val db: DbFit) {

    // ==================== HISTORY QUERIES ====================

    /**
     * Recupera l'ultima scheda per un gruppo muscolare specifico.
     */
    suspend fun getLastWorkoutByMuscle(muscle: String, profileId: Int?): SchedeEntity? = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede
            .filter { it.gruppoMuscolare.lowercase().contains(muscle.lowercase()) }
            .maxByOrNull { parseDate(it.data) ?: LocalDate.MIN }
    }

    /**
     * Recupera le ultime N schede.
     */
    suspend fun getLastNWorkouts(n: Int, profileId: Int?): List<SchedeEntity> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede
            .sortedByDescending { parseDate(it.data) ?: LocalDate.MIN }
            .take(n)
    }

    /**
     * Recupera schede di un giorno specifico della settimana.
     */
    suspend fun getWorkoutsOnDayOfWeek(dayOfWeek: java.time.DayOfWeek, profileId: Int?): List<SchedeEntity> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede.filter { scheda ->
            parseDate(scheda.data)?.dayOfWeek == dayOfWeek
        }
    }

    /**
     * Calcola l'orario medio di allenamento.
     */
    suspend fun getAverageGymTime(profileId: Int?): String? = withContext(Dispatchers.IO) {
        val schedeWithTime = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getSchedeWithTime()
        }

        val times = schedeWithTime.mapNotNull { it.ora?.let { ora -> parseTime(ora) } }
        if (times.isEmpty()) return@withContext null

        val avgMinutes = times.map { it.hour * 60 + it.minute }.average().toInt()
        val avgHour = avgMinutes / 60
        val avgMin = avgMinutes % 60
        String.format("%02d:%02d", avgHour, avgMin)
    }

    /**
     * Giorni dall'ultimo allenamento.
     */
    suspend fun getDaysSinceLastWorkout(profileId: Int?): Int = withContext(Dispatchers.IO) {
        val lastWorkout = getLastNWorkouts(1, profileId).firstOrNull()
        val lastDate = lastWorkout?.data?.let { parseDate(it) } ?: return@withContext -1
        ChronoUnit.DAYS.between(lastDate, LocalDate.now()).toInt()
    }

    // ==================== PERFORMANCE QUERIES ====================

    /**
     * Progressione di un esercizio specifico.
     */
    suspend fun getExerciseProgression(exerciseName: String, limit: Int = 10, profileId: Int?): List<ExerciseProgress> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val progression = mutableListOf<ExerciseProgress>()

        allSchede
            .sortedByDescending { parseDate(it.data) ?: LocalDate.MIN }
            .take(50) // Limita per performance
            .forEach { scheda ->
                scheda.id?.let { schedaId ->
                    val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                    exercises
                        .filter { it.nome.lowercase().contains(exerciseName.lowercase()) }
                        .forEach { ex ->
                            progression.add(
                                ExerciseProgress(
                                    date = scheda.data,
                                    exerciseName = ex.nome,
                                    sets = ex.nSerie,
                                    reps = ex.nRipetizione,
                                    weight = ex.peso,
                                    equipment = ex.attrezzo
                                )
                            )
                        }
                }
            }

        progression.take(limit)
    }

    /**
     * Peso massimo per un esercizio.
     */
    suspend fun getMaxWeight(exerciseName: String, profileId: Int?): Float? = withContext(Dispatchers.IO) {
        val progression = getExerciseProgression(exerciseName, 50, profileId)
        progression.mapNotNull { it.weight }.maxOrNull()
    }

    /**
     * Ultimo dettaglio di un esercizio.
     */
    suspend fun getLastExerciseDetails(exerciseName: String, profileId: Int?): EsserciziEntity? = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede
            .sortedByDescending { parseDate(it.data) ?: LocalDate.MIN }
            .forEach { scheda ->
                scheda.id?.let { schedaId ->
                    val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                    val match = exercises.firstOrNull {
                        it.nome.lowercase().contains(exerciseName.lowercase())
                    }
                    if (match != null) return@withContext match
                }
            }
        null
    }

    // ==================== PLANNING QUERIES ====================

    /**
     * Esercizi fatti negli ultimi N giorni.
     */
    suspend fun getExercisesFromLastNDays(days: Int, profileId: Int?): List<String> = withContext(Dispatchers.IO) {
        val cutoff = LocalDate.now().minusDays(days.toLong())
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val exercises = mutableSetOf<String>()
        allSchede
            .filter { (parseDate(it.data) ?: LocalDate.MIN) >= cutoff }
            .forEach { scheda ->
                scheda.id?.let { schedaId ->
                    val exList = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                    exercises.addAll(exList.map { it.nome })
                }
            }
        exercises.toList()
    }

    /**
     * Gruppi muscolari allenati di recente.
     */
    suspend fun getMuscleGroupsTrainedRecently(days: Int, profileId: Int?): List<String> = withContext(Dispatchers.IO) {
        val cutoff = LocalDate.now().minusDays(days.toLong())
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede
            .filter { (parseDate(it.data) ?: LocalDate.MIN) >= cutoff }
            .map { it.gruppoMuscolare }
            .distinct()
    }

    /**
     * Tutti gli esercizi disponibili (storici).
     */
    suspend fun getAllAvailableExercises(profileId: Int?): List<String> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val exercises = mutableSetOf<String>()
        allSchede.forEach { scheda ->
            scheda.id?.let { schedaId ->
                val exList = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                exercises.addAll(exList.map { it.nome })
            }
        }
        exercises.toList().sorted()
    }

    // ==================== GENERAL QUERIES ====================

    /**
     * Conteggio allenamenti questa settimana.
     */
    suspend fun getWeeklyWorkoutCount(profileId: Int?): Int = withContext(Dispatchers.IO) {
        val weekStart = LocalDate.now().minusDays(7)
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede.count { (parseDate(it.data) ?: LocalDate.MIN) >= weekStart }
    }

    /**
     * Conteggio allenamenti questo mese.
     */
    suspend fun getMonthlyWorkoutCount(profileId: Int?): Int = withContext(Dispatchers.IO) {
        val monthStart = LocalDate.now().minusDays(30)
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        allSchede.count { (parseDate(it.data) ?: LocalDate.MIN) >= monthStart }
    }

    /**
     * Muscolo piu allenato.
     */
    suspend fun getMostTrainedMuscle(profileId: Int?): String? = withContext(Dispatchers.IO) {
        if (profileId != null) {
            db.schedeDao().getMostTrainedMuscleGroupByCoach(profileId)
        } else {
            db.schedeDao().getMostTrainedMuscleGroup()
        }
    }

    /**
     * Media allenamenti settimanali.
     */
    suspend fun getWeeklyAverage(profileId: Int?): Double = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        if (allSchede.isEmpty()) return@withContext 0.0

        val dates = allSchede.mapNotNull { parseDate(it.data) }
        if (dates.isEmpty()) return@withContext 0.0

        val firstDate = dates.minOrNull() ?: return@withContext 0.0
        val weeks = ChronoUnit.WEEKS.between(firstDate, LocalDate.now()).coerceAtLeast(1)
        allSchede.size.toDouble() / weeks
    }

    // ==================== COMPARISON QUERIES ====================

    /**
     * Confronta volume settimanale corrente vs precedente.
     */
    suspend fun compareWeeklyVolume(profileId: Int?): VolumeComparison = withContext(Dispatchers.IO) {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(7)
        val twoWeeksAgo = today.minusDays(14)

        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val currentWeek = allSchede.filter {
            val date = parseDate(it.data) ?: LocalDate.MIN
            date >= weekAgo && date <= today
        }

        val previousWeek = allSchede.filter {
            val date = parseDate(it.data) ?: LocalDate.MIN
            date >= twoWeeksAgo && date < weekAgo
        }

        // Calculate volumes
        var currentVolume = 0
        currentWeek.forEach { scheda ->
            scheda.id?.let { schedaId ->
                val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                currentVolume += exercises.sumOf { it.nSerie * it.nRipetizione }
            }
        }

        var previousVolume = 0
        previousWeek.forEach { scheda ->
            scheda.id?.let { schedaId ->
                val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                previousVolume += exercises.sumOf { it.nSerie * it.nRipetizione }
            }
        }
        val change = if (previousVolume > 0) {
            ((currentVolume - previousVolume).toFloat() / previousVolume * 100).toInt()
        } else 0

        VolumeComparison(currentVolume, previousVolume, change)
    }

    /**
     * Confronta frequenza mensile.
     */
    suspend fun compareMonthlyFrequency(profileId: Int?): FrequencyComparison = withContext(Dispatchers.IO) {
        val today = LocalDate.now()
        val monthAgo = today.minusDays(30)
        val twoMonthsAgo = today.minusDays(60)

        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val currentMonth = allSchede.count {
            val date = parseDate(it.data) ?: LocalDate.MIN
            date >= monthAgo && date <= today
        }

        val previousMonth = allSchede.count {
            val date = parseDate(it.data) ?: LocalDate.MIN
            date >= twoMonthsAgo && date < monthAgo
        }

        val change = if (previousMonth > 0) {
            ((currentMonth - previousMonth).toFloat() / previousMonth * 100).toInt()
        } else 0

        FrequencyComparison(currentMonth, previousMonth, change)
    }

    // ==================== EQUIPMENT QUERIES ====================

    /**
     * Attrezzi usati per un gruppo muscolare.
     */
    suspend fun getEquipmentForMuscle(muscle: String, profileId: Int?): List<String> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val equipment = mutableSetOf<String>()
        allSchede
            .filter { it.gruppoMuscolare.lowercase().contains(muscle.lowercase()) }
            .forEach { scheda ->
                scheda.id?.let { schedaId ->
                    val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                    equipment.addAll(exercises.map { it.attrezzo }.filter { it.isNotBlank() })
                }
            }
        equipment.toList()
    }

    /**
     * Attrezzi piu usati.
     */
    suspend fun getMostUsedEquipment(limit: Int = 5, profileId: Int?): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val allSchede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val equipmentCount = mutableMapOf<String, Int>()
        allSchede.forEach { scheda ->
            scheda.id?.let { schedaId ->
                val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                exercises.forEach { ex ->
                    if (ex.attrezzo.isNotBlank()) {
                        equipmentCount[ex.attrezzo] = (equipmentCount[ex.attrezzo] ?: 0) + 1
                    }
                }
            }
        }

        equipmentCount.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key to it.value }
    }

    // ==================== UTILITY ====================

    private fun parseDate(dateStr: String): LocalDate? {
        if (dateStr.isBlank()) return null
        val datePart = dateStr.trim().let { if (it.length >= 10) it.substring(0, 10) else it }
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        for (fmt in formats) {
            try {
                return LocalDate.parse(datePart, fmt)
            } catch (_: Exception) {}
        }
        return null
    }

    private fun parseTime(timeStr: String): LocalTime? {
        return try {
            LocalTime.parse(timeStr.trim())
        } catch (_: Exception) {
            null
        }
    }
}

// Data classes per i risultati

data class ExerciseProgress(
    val date: String,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float?,
    val equipment: String
)

data class VolumeComparison(
    val currentWeekVolume: Int,
    val previousWeekVolume: Int,
    val changePercent: Int
)

data class FrequencyComparison(
    val currentMonthWorkouts: Int,
    val previousMonthWorkouts: Int,
    val changePercent: Int
)
