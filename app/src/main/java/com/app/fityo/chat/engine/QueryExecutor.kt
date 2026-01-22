package com.app.fityo.chat.engine

import com.app.fityo.chat.data.PredefinedQuestion
import com.app.fityo.chat.data.QueryType
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Esegue query predefinite e restituisce risposte formattate.
 * NESSUNA interpretazione AI - solo dati reali dal database.
 *
 * Supporta:
 * - Storico allenamenti
 * - Performance e record
 * - Statistiche avanzate
 * - Appuntamenti (Coach Mode)
 * - Suggerimenti intelligenti basati sui dati
 */
class QueryExecutor(private val db: DbFit) {

    private val dataProvider = WorkoutDataProvider(db)

    // Cache profili per lookup nome
    private var profilesCache: List<CoachProfileEntity> = emptyList()

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE
        private val DAYS_IT = listOf("Domenica", "Lunedi", "Martedi", "Mercoledi", "Giovedi", "Venerdi", "Sabato")
    }

    // ==================== CACHE MANAGEMENT ====================

    suspend fun refreshProfilesCache() {
        profilesCache = db.coachProfileDao().getAllProfilesSync()
    }

    fun getProfileName(profileId: Int?): String? {
        return profileId?.let { id -> profilesCache.find { it.id == id }?.name }
    }

    // ==================== MAIN EXECUTE ====================

    suspend fun execute(
        question: PredefinedQuestion,
        profileId: Int?
    ): String = withContext(Dispatchers.IO) {
        // Aggiorna cache se necessario
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }

        // Determina soggetto per la risposta
        val profileName = getProfileName(profileId)
        val subject = profileName ?: "Tu"
        val possessive = if (profileName != null) "di $profileName" else "tuo/a"
        val verb = if (profileName != null) "ha" else "hai"

        when (question.queryType) {
            // ===== STORICO =====
            QueryType.LAST_WORKOUT -> executeLastWorkout(profileId, subject, verb)
            QueryType.WORKOUT_HISTORY -> executeWorkoutHistory(profileId, possessive)
            QueryType.LAST_LEGS -> executeLastMuscle("gambe", profileId, subject, verb)
            QueryType.LAST_CHEST -> executeLastMuscle("petto", profileId, subject, verb)
            QueryType.LAST_BACK -> executeLastMuscle("schiena", profileId, subject, verb)
            QueryType.LAST_SHOULDERS -> executeLastMuscle("spalle", profileId, subject, verb)
            QueryType.LAST_ARMS -> executeLastMuscle("braccia", profileId, subject, verb)
            QueryType.LAST_ABS -> executeLastMuscle("addominali", profileId, subject, verb)
            QueryType.DAYS_SINCE_WORKOUT -> executeDaysSince(profileId, subject, verb)

            // ===== PERFORMANCE =====
            QueryType.MAX_BENCH -> executeMaxWeight("panca", profileId, possessive)
            QueryType.MAX_SQUAT -> executeMaxWeight("squat", profileId, possessive)
            QueryType.MAX_DEADLIFT -> executeMaxWeight("stacco", profileId, possessive)
            QueryType.LAST_BENCH_DETAILS -> executeLastExerciseDetails("panca", profileId, possessive)
            QueryType.LAST_SQUAT_DETAILS -> executeLastExerciseDetails("squat", profileId, possessive)
            QueryType.EXERCISE_PROGRESSION -> executeExerciseProgression(question.exerciseTarget ?: "panca", profileId, possessive)
            QueryType.PROGRESS_TREND -> executeProgressTrend(profileId, possessive)

            // ===== STATISTICHE AVANZATE =====
            QueryType.WEEKLY_COUNT -> executeWeeklyCount(profileId, subject, verb)
            QueryType.MONTHLY_COUNT -> executeMonthlyCount(profileId, subject, verb)
            QueryType.TOTAL_WORKOUTS -> executeTotalWorkouts(profileId, possessive)
            QueryType.MOST_TRAINED_MUSCLE -> executeMostTrained(profileId, possessive)
            QueryType.LEAST_TRAINED_MUSCLE -> executeLeastTrained(profileId, possessive)
            QueryType.WEEKLY_AVERAGE -> executeWeeklyAverage(profileId, possessive)
            QueryType.MUSCLE_DISTRIBUTION -> executeMuscleDistribution(profileId, possessive)
            QueryType.INTENSITY_ANALYSIS -> executeIntensityAnalysis(profileId, possessive)
            QueryType.VOLUME_TOTAL -> executeVolumeTotal(profileId, possessive)
            QueryType.CONSISTENCY_SCORE -> executeConsistencyScore(profileId, possessive)
            QueryType.FAVORITE_DAY -> executeFavoriteDay(profileId, possessive)
            QueryType.STREAK_DAYS -> executeStreakDays(profileId, subject, verb)

            // ===== CONFRONTI =====
            QueryType.COMPARE_WEEKS -> executeCompareWeeks(profileId, possessive)
            QueryType.COMPARE_MONTHS -> executeCompareMonths(profileId, possessive)

            // ===== SUGGERIMENTI =====
            QueryType.SUGGEST_EXERCISES -> executeSuggestExercises(4, profileId, subject)
            QueryType.WHAT_TODAY -> executeWhatToday(profileId, subject)
            QueryType.EXERCISES_FOR_LEGS -> executeExercisesForMuscle("gambe", profileId)
            QueryType.EXERCISES_FOR_CHEST -> executeExercisesForMuscle("petto", profileId)
            QueryType.EXERCISES_FOR_BACK -> executeExercisesForMuscle("schiena", profileId)
            QueryType.SMART_SUGGESTION -> executeSmartSuggestion(profileId, subject, possessive)
            QueryType.RECOVERY_ADVICE -> executeRecoveryAdvice(profileId, subject, possessive)

            // ===== ATTREZZI =====
            QueryType.MOST_USED_EQUIPMENT -> executeMostUsedEquipment(profileId, possessive)
            QueryType.EQUIPMENT_FOR_LEGS -> executeEquipmentForMuscle("gambe", profileId, subject, verb)
            QueryType.EQUIPMENT_FOR_CHEST -> executeEquipmentForMuscle("petto", profileId, subject, verb)
            QueryType.ALL_EQUIPMENT -> executeAllEquipment(profileId, possessive)

            // ===== APPUNTAMENTI =====
            QueryType.TODAY_APPOINTMENTS -> executeTodayAppointments(profileId, profileName)
            QueryType.WEEK_APPOINTMENTS -> executeWeekAppointments(profileId, profileName)
            QueryType.NEXT_APPOINTMENTS -> executeNextAppointments(profileId, profileName)
            QueryType.PENDING_APPOINTMENTS -> executePendingAppointments(profileId, profileName)
            QueryType.MONTH_APPOINTMENTS -> executeMonthAppointments(profileId, profileName)

            // ===== PROFILI =====
            QueryType.LIST_PROFILES -> executeListProfiles()
            QueryType.PROFILE_SUMMARY -> executeProfileSummary(profileId, profileName)
            QueryType.PROFILE_COMPARISON -> executeProfileComparison()
        }
    }

    // ==================== STORICO ====================

    private suspend fun executeLastWorkout(profileId: Int?, subject: String, verb: String): String {
        val lastWorkout = dataProvider.getLastNWorkouts(1, profileId).firstOrNull()
        return if (lastWorkout != null) {
            val daysSince = dataProvider.getDaysSinceLastWorkout(profileId)
            val daysText = when (daysSince) {
                0 -> "oggi"
                1 -> "ieri"
                else -> "$daysSince giorni fa"
            }
            buildString {
                append("📅 Ultimo allenamento $daysText\n\n")
                append("• Data: ${lastWorkout.data}\n")
                append("• Muscolo: ${lastWorkout.gruppoMuscolare}\n")
                append("• Intensita: ${lastWorkout.intesita}")
                lastWorkout.ora?.let { append("\n• Ora: $it") }
            }
        } else {
            "Non ci sono ancora allenamenti registrati."
        }
    }

    private suspend fun executeWorkoutHistory(profileId: Int?, possessive: String): String {
        val workouts = dataProvider.getLastNWorkouts(5, profileId)
        return if (workouts.isNotEmpty()) {
            buildString {
                append("📋 Ultimi ${workouts.size} allenamenti $possessive:\n\n")
                workouts.forEachIndexed { index, w ->
                    append("${index + 1}. ${w.data} - ${w.gruppoMuscolare}")
                    if (w.completed) append(" ✓")
                    append("\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora allenamenti registrati."
        }
    }

    private suspend fun executeLastMuscle(muscle: String, profileId: Int?, subject: String, verb: String): String {
        val lastWorkout = dataProvider.getLastWorkoutByMuscle(muscle, profileId)
        return if (lastWorkout != null) {
            val date = parseDate(lastWorkout.data)
            val daysSince = date?.let { ChronoUnit.DAYS.between(it, LocalDate.now()).toInt() } ?: 0
            val daysText = when (daysSince) {
                0 -> "oggi"
                1 -> "ieri"
                else -> "$daysSince giorni fa"
            }
            "L'ultima volta che $subject $verb allenato $muscle: ${lastWorkout.data} ($daysText)${lastWorkout.ora?.let { " alle $it" } ?: ""}"
        } else {
            "Non ho trovato allenamenti per $muscle nello storico."
        }
    }

    private suspend fun executeDaysSince(profileId: Int?, subject: String, verb: String): String {
        val days = dataProvider.getDaysSinceLastWorkout(profileId)
        return when {
            days < 0 -> "Non ci sono ancora allenamenti registrati."
            days == 0 -> "✨ $subject $verb allenato oggi! Ottimo lavoro!"
            days == 1 -> "$subject $verb allenato ieri. Un giorno di riposo."
            days <= 3 -> "Sono passati $days giorni dall'ultimo allenamento. Tutto nella norma!"
            days <= 7 -> "⚠️ Sono passati $days giorni dall'ultimo allenamento. Forse e ora di tornare in palestra?"
            else -> "🔴 Sono passati $days giorni dall'ultimo allenamento. Troppo tempo!"
        }
    }

    // ==================== PERFORMANCE ====================

    private suspend fun executeMaxWeight(exercise: String, profileId: Int?, possessive: String): String {
        val maxWeight = dataProvider.getMaxWeight(exercise, profileId)
        val lastDetails = dataProvider.getLastExerciseDetails(exercise, profileId)

        return buildString {
            append("🏆 Record $possessive in $exercise:\n\n")
            if (maxWeight != null) {
                append("• Peso massimo: ${maxWeight}kg\n")
            } else {
                append("• Peso massimo: nessun dato\n")
            }
            if (lastDetails != null) {
                append("• Ultimo allenamento: ${lastDetails.nSerie}x${lastDetails.nRipetizione}")
                lastDetails.peso?.let { append(" @ ${it}kg") }
            }
        }
    }

    private suspend fun executeLastExerciseDetails(exercise: String, profileId: Int?, possessive: String): String {
        val lastDetails = dataProvider.getLastExerciseDetails(exercise, profileId)
        return if (lastDetails != null) {
            buildString {
                append("📝 Dettagli ultimo $exercise $possessive:\n\n")
                append("• Serie: ${lastDetails.nSerie}\n")
                append("• Ripetizioni: ${lastDetails.nRipetizione}\n")
                lastDetails.peso?.let { append("• Peso: ${it}kg\n") }
                if (lastDetails.attrezzo.isNotBlank()) {
                    append("• Attrezzo: ${lastDetails.attrezzo}\n")
                }
                lastDetails.intervallo?.let { append("• Recupero: ${it}s") }
            }
        } else {
            "Non ho trovato dati per $exercise nello storico."
        }
    }

    private suspend fun executeExerciseProgression(exercise: String, profileId: Int?, possessive: String): String {
        val progression = dataProvider.getExerciseProgression(exercise, 5, profileId)
        return if (progression.isNotEmpty()) {
            buildString {
                append("📈 Progressione $exercise $possessive:\n\n")
                progression.forEach { p ->
                    append("• ${p.date}: ${p.sets}x${p.reps}")
                    p.weight?.let { append(" @ ${it}kg") }
                    append("\n")
                }
            }.trimEnd()
        } else {
            "Non ho abbastanza dati per mostrare la progressione di $exercise."
        }
    }

    private suspend fun executeProgressTrend(profileId: Int?, possessive: String): String {
        val weekComparison = dataProvider.compareWeeklyVolume(profileId)
        val monthComparison = dataProvider.compareMonthlyFrequency(profileId)

        val weekTrend = when {
            weekComparison.changePercent > 20 -> "📈 Ottimo!"
            weekComparison.changePercent > 0 -> "↗️ In crescita"
            weekComparison.changePercent > -10 -> "➡️ Stabile"
            else -> "↘️ In calo"
        }

        val monthTrend = when {
            monthComparison.changePercent > 20 -> "📈 Ottimo!"
            monthComparison.changePercent > 0 -> "↗️ In crescita"
            monthComparison.changePercent > -10 -> "➡️ Stabile"
            else -> "↘️ In calo"
        }

        return buildString {
            append("📊 Trend di progresso $possessive:\n\n")
            append("Volume settimanale: $weekTrend\n")
            append("• ${weekComparison.currentWeekVolume} vs ${weekComparison.previousWeekVolume} rep\n")
            append("• Variazione: ${if (weekComparison.changePercent >= 0) "+" else ""}${weekComparison.changePercent}%\n\n")
            append("Frequenza mensile: $monthTrend\n")
            append("• ${monthComparison.currentMonthWorkouts} vs ${monthComparison.previousMonthWorkouts} allenamenti\n")
            append("• Variazione: ${if (monthComparison.changePercent >= 0) "+" else ""}${monthComparison.changePercent}%")
        }
    }

    // ==================== STATISTICHE AVANZATE ====================

    private suspend fun executeWeeklyCount(profileId: Int?, subject: String, verb: String): String {
        val count = dataProvider.getWeeklyWorkoutCount(profileId)
        val emoji = when {
            count >= 5 -> "🔥"
            count >= 3 -> "💪"
            count >= 1 -> "👍"
            else -> "😴"
        }
        return when {
            count == 0 -> "$emoji $subject non $verb ancora allenato questa settimana."
            count == 1 -> "$emoji $subject $verb fatto 1 allenamento questa settimana."
            else -> "$emoji $subject $verb fatto $count allenamenti questa settimana."
        }
    }

    private suspend fun executeMonthlyCount(profileId: Int?, subject: String, verb: String): String {
        val count = dataProvider.getMonthlyWorkoutCount(profileId)
        val emoji = when {
            count >= 16 -> "🏆"
            count >= 12 -> "🔥"
            count >= 8 -> "💪"
            count >= 4 -> "👍"
            else -> "😴"
        }
        return when {
            count == 0 -> "$emoji $subject non $verb ancora allenato questo mese."
            count == 1 -> "$emoji $subject $verb fatto 1 allenamento questo mese."
            else -> "$emoji $subject $verb fatto $count allenamenti questo mese."
        }
    }

    private suspend fun executeTotalWorkouts(profileId: Int?, possessive: String): String {
        val schede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }
        val total = schede.size
        val completed = schede.count { it.completed }

        return buildString {
            append("📊 Totale allenamenti $possessive:\n\n")
            append("• Allenamenti totali: $total\n")
            append("• Completati: $completed\n")
            if (total > 0) {
                val rate = (completed * 100) / total
                append("• Tasso completamento: $rate%")
            }
        }
    }

    private suspend fun executeMostTrained(profileId: Int?, possessive: String): String {
        val muscle = dataProvider.getMostTrainedMuscle(profileId)
        return if (muscle != null) {
            "💪 Il muscolo piu allenato $possessive e: $muscle"
        } else {
            "Non ci sono ancora abbastanza dati per determinare il muscolo piu allenato."
        }
    }

    private suspend fun executeLeastTrained(profileId: Int?, possessive: String): String {
        val distribution = if (profileId != null) {
            db.schedeDao().getPercentualeByCoachProfile(profileId)
        } else {
            db.schedeDao().getPercentualePerGruppoMuscolare()
        }

        return if (distribution.isNotEmpty()) {
            val least = distribution.minByOrNull { it.percentuale }
            "⚠️ Il muscolo meno allenato $possessive e: ${least?.gruppoMuscolare ?: "N/A"} (${least?.percentuale?.toInt() ?: 0}%)"
        } else {
            "Non ci sono ancora abbastanza dati."
        }
    }

    private suspend fun executeWeeklyAverage(profileId: Int?, possessive: String): String {
        val average = dataProvider.getWeeklyAverage(profileId)
        val rating = when {
            average >= 4.0 -> "🏆 Eccellente!"
            average >= 3.0 -> "💪 Ottimo!"
            average >= 2.0 -> "👍 Buono"
            average >= 1.0 -> "😐 Nella media"
            else -> "😴 Sotto la media"
        }
        return if (average > 0) {
            "📊 Media settimanale $possessive: %.1f allenamenti\n$rating".format(average)
        } else {
            "Non ci sono ancora abbastanza dati per calcolare la media."
        }
    }

    private suspend fun executeMuscleDistribution(profileId: Int?, possessive: String): String {
        val distribution = if (profileId != null) {
            db.schedeDao().getPercentualeByCoachProfile(profileId)
        } else {
            db.schedeDao().getPercentualePerGruppoMuscolare()
        }

        return if (distribution.isNotEmpty()) {
            buildString {
                append("📊 Distribuzione muscoli $possessive:\n\n")
                distribution.sortedByDescending { it.percentuale }.forEach { d ->
                    val bar = "█".repeat((d.percentuale / 10).toInt().coerceIn(0, 10))
                    append("${d.gruppoMuscolare}: $bar ${d.percentuale.toInt()}%\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora dati sufficienti."
        }
    }

    private suspend fun executeIntensityAnalysis(profileId: Int?, possessive: String): String {
        val intensities = if (profileId != null) {
            db.schedeDao().getMediaIntensitaByCoachProfile(profileId)
        } else {
            db.schedeDao().getMediaIntensitaPerGruppoMuscolareAll()
        }

        return if (intensities.isNotEmpty()) {
            buildString {
                append("⚡ Analisi intensita $possessive:\n\n")
                intensities.forEach { i ->
                    val level = when {
                        i.mediaIntensita >= 12 -> "🔴 Alta"
                        i.mediaIntensita >= 8 -> "🟡 Media"
                        else -> "🟢 Bassa"
                    }
                    append("${i.gruppoMuscolare}: $level\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora dati sufficienti."
        }
    }

    private suspend fun executeVolumeTotal(profileId: Int?, possessive: String): String {
        val schede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        var totalVolume = 0f
        var totalReps = 0

        schede.forEach { scheda ->
            scheda.id?.let { schedaId ->
                val exercises = db.essercissiDao().getEserciziByschedaIdSync(schedaId)
                exercises.forEach { ex ->
                    val reps = ex.nSerie * ex.nRipetizione
                    totalReps += reps
                    ex.peso?.let { peso ->
                        totalVolume += reps * peso
                    }
                }
            }
        }

        return buildString {
            append("🏋️ Volume totale $possessive:\n\n")
            append("• Peso totale sollevato: ${String.format("%.0f", totalVolume)}kg\n")
            append("• Ripetizioni totali: $totalReps\n")
            if (schede.isNotEmpty()) {
                append("• Media per allenamento: ${String.format("%.0f", totalVolume / schede.size)}kg")
            }
        }
    }

    private suspend fun executeConsistencyScore(profileId: Int?, possessive: String): String {
        val schede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        val total = schede.size
        val completed = schede.count { it.completed }
        val score = if (total > 0) (completed * 100) / total else 0

        val rating = when {
            score >= 90 -> "🏆 Eccezionale!"
            score >= 75 -> "🔥 Ottimo!"
            score >= 50 -> "💪 Buono"
            score >= 25 -> "😐 Da migliorare"
            else -> "😴 Serve piu impegno"
        }

        return buildString {
            append("📈 Punteggio consistenza $possessive:\n\n")
            append("• Score: $score%\n")
            append("• Valutazione: $rating\n")
            append("• Completati: $completed/$total allenamenti")
        }
    }

    private suspend fun executeFavoriteDay(profileId: Int?, possessive: String): String {
        val weekdayCounts = if (profileId != null) {
            db.schedeDao().getWorkoutCountByWeekdayForCoach(profileId)
        } else {
            db.schedeDao().getWorkoutCountByWeekdayAll()
        }

        return if (weekdayCounts.isNotEmpty()) {
            val favorite = weekdayCounts.maxByOrNull { it.count }
            val dayName = favorite?.let { DAYS_IT.getOrNull(it.dayOfWeek) } ?: "N/A"
            buildString {
                append("📅 Giorno preferito $possessive: $dayName\n\n")
                weekdayCounts.sortedByDescending { it.count }.forEach { wc ->
                    val day = DAYS_IT.getOrNull(wc.dayOfWeek) ?: "?"
                    append("• $day: ${wc.count} allenamenti\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora dati sufficienti."
        }
    }

    private suspend fun executeStreakDays(profileId: Int?, subject: String, verb: String): String {
        val schede = if (profileId != null) {
            db.schedeDao().getSchedeByCoachProfile(profileId)
        } else {
            db.schedeDao().getAllSchede()
        }

        // Conta giorni con allenamento negli ultimi 7 giorni
        val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }
        val workoutDates = schede.mapNotNull { parseDate(it.data) }.toSet()
        val streak = last7Days.count { it in workoutDates }

        val emoji = when {
            streak >= 6 -> "🔥"
            streak >= 4 -> "💪"
            streak >= 2 -> "👍"
            else -> "😴"
        }

        return "$emoji Giorni allenamento ultimi 7 giorni: $streak/7"
    }

    // ==================== CONFRONTI ====================

    private suspend fun executeCompareWeeks(profileId: Int?, possessive: String): String {
        val comparison = dataProvider.compareWeeklyVolume(profileId)
        val trend = when {
            comparison.changePercent > 10 -> "📈 In crescita"
            comparison.changePercent < -10 -> "📉 In calo"
            else -> "➡️ Stabile"
        }
        return buildString {
            append("📊 Confronto settimanale $possessive:\n\n")
            append("Questa settimana: ${comparison.currentWeekVolume} rep\n")
            append("Settimana scorsa: ${comparison.previousWeekVolume} rep\n")
            append("Variazione: ${if (comparison.changePercent >= 0) "+" else ""}${comparison.changePercent}%\n\n")
            append("Trend: $trend")
        }
    }

    private suspend fun executeCompareMonths(profileId: Int?, possessive: String): String {
        val comparison = dataProvider.compareMonthlyFrequency(profileId)
        val trend = when {
            comparison.changePercent > 10 -> "📈 In crescita"
            comparison.changePercent < -10 -> "📉 In calo"
            else -> "➡️ Stabile"
        }
        return buildString {
            append("📊 Confronto mensile $possessive:\n\n")
            append("Questo mese: ${comparison.currentMonthWorkouts} allenamenti\n")
            append("Mese scorso: ${comparison.previousMonthWorkouts} allenamenti\n")
            append("Variazione: ${if (comparison.changePercent >= 0) "+" else ""}${comparison.changePercent}%\n\n")
            append("Trend: $trend")
        }
    }

    // ==================== SUGGERIMENTI ====================

    private suspend fun executeSuggestExercises(count: Int, profileId: Int?, subject: String): String {
        val recentExercises = dataProvider.getExercisesFromLastNDays(3, profileId)
        val available = dataProvider.getAllAvailableExercises(profileId)

        if (available.isEmpty()) {
            return "Non ci sono ancora esercizi registrati. Inizia a tracciare gli allenamenti!"
        }

        val suggestions = available
            .filter { it !in recentExercises }
            .shuffled()
            .take(count)

        return if (suggestions.isNotEmpty()) {
            buildString {
                append("💡 $count esercizi consigliati:\n\n")
                suggestions.forEachIndexed { index, ex ->
                    append("${index + 1}. $ex\n")
                }
            }.trimEnd()
        } else {
            "Hai fatto tutti gli esercizi del repertorio di recente. Riposa o prova qualcosa di nuovo!"
        }
    }

    private suspend fun executeWhatToday(profileId: Int?, subject: String): String {
        val recentMuscles = dataProvider.getMuscleGroupsTrainedRecently(2, profileId)
        val allMuscles = listOf("Gambe", "Petto", "Schiena", "Spalle", "Braccia", "Addominali")

        val suggested = allMuscles.filter { it.lowercase() !in recentMuscles.map { m -> m.lowercase() } }
            .shuffled()
            .firstOrNull()

        return if (suggested != null) {
            buildString {
                append("🎯 Consiglio per oggi: $suggested\n\n")
                if (recentMuscles.isNotEmpty()) {
                    append("Muscoli da evitare (allenati di recente):\n")
                    recentMuscles.forEach { append("• $it\n") }
                }
            }.trimEnd()
        } else {
            "Hai allenato tutto di recente. Prenditi un giorno di riposo! 😴"
        }
    }

    private suspend fun executeSmartSuggestion(profileId: Int?, subject: String, possessive: String): String {
        val daysSince = dataProvider.getDaysSinceLastWorkout(profileId)
        val recentMuscles = dataProvider.getMuscleGroupsTrainedRecently(3, profileId)
        val weeklyCount = dataProvider.getWeeklyWorkoutCount(profileId)
        val leastTrained = if (profileId != null) {
            db.schedeDao().getPercentualeByCoachProfile(profileId)
        } else {
            db.schedeDao().getPercentualePerGruppoMuscolare()
        }.minByOrNull { it.percentuale }?.gruppoMuscolare

        return buildString {
            append("🧠 Analisi intelligente $possessive:\n\n")

            // Riposo
            when {
                daysSince < 0 -> append("📝 Nessun allenamento registrato. Inizia oggi!\n\n")
                daysSince == 0 -> append("✅ Allenamento fatto oggi. Riposa o fai stretching.\n\n")
                daysSince >= 4 -> append("⚠️ $daysSince giorni senza allenamento. E' ora di tornare!\n\n")
                else -> append("✅ Ultimo allenamento $daysSince giorni fa. Buon ritmo!\n\n")
            }

            // Frequenza settimanale
            when {
                weeklyCount >= 5 -> append("🔥 $weeklyCount allenamenti questa settimana - Ottimo!\n\n")
                weeklyCount >= 3 -> append("💪 $weeklyCount allenamenti questa settimana - Buono!\n\n")
                weeklyCount > 0 -> append("👍 $weeklyCount allenamenti questa settimana.\n\n")
            }

            // Suggerimento muscolo
            if (leastTrained != null && leastTrained !in recentMuscles) {
                append("💡 Suggerimento: Allena $leastTrained (il meno allenato)")
            } else {
                val allMuscles = listOf("Gambe", "Petto", "Schiena", "Spalle", "Braccia")
                val suggested = allMuscles.filter { it.lowercase() !in recentMuscles.map { m -> m.lowercase() } }
                    .shuffled().firstOrNull()
                if (suggested != null) {
                    append("💡 Suggerimento: Allena $suggested")
                }
            }
        }
    }

    private suspend fun executeRecoveryAdvice(profileId: Int?, subject: String, possessive: String): String {
        val daysSince = dataProvider.getDaysSinceLastWorkout(profileId)
        val weeklyCount = dataProvider.getWeeklyWorkoutCount(profileId)
        val recentMuscles = dataProvider.getMuscleGroupsTrainedRecently(2, profileId)

        return buildString {
            append("🛌 Analisi recupero $possessive:\n\n")

            when {
                daysSince < 0 -> {
                    append("Non hai ancora allenamenti registrati.\n")
                    append("Consiglio: Inizia con calma e aumenta gradualmente!")
                }
                daysSince == 0 && weeklyCount >= 5 -> {
                    append("⚠️ Allenamento intenso questa settimana!\n")
                    append("Hai fatto $weeklyCount allenamenti.\n\n")
                    append("Consiglio: Prenditi 1-2 giorni di riposo attivo.")
                }
                daysSince == 0 -> {
                    append("✅ Allenamento completato oggi.\n\n")
                    append("Consiglio: Riposa, idratati e dormi bene!")
                }
                daysSince == 1 -> {
                    append("😴 Un giorno di riposo.\n\n")
                    if (weeklyCount >= 4) {
                        append("Consiglio: Va bene un altro giorno di riposo.")
                    } else {
                        append("Consiglio: Pronto per un nuovo allenamento!")
                    }
                }
                daysSince <= 3 -> {
                    append("✅ $daysSince giorni di riposo.\n\n")
                    append("Recupero adeguato. Sei pronto per allenarti!")
                }
                else -> {
                    append("😴 $daysSince giorni senza allenamento.\n\n")
                    append("Consiglio: E' ora di tornare in palestra!")
                }
            }

            if (recentMuscles.isNotEmpty()) {
                append("\n\nMuscoli allenati di recente (evitare): ${recentMuscles.joinToString(", ")}")
            }
        }
    }

    private suspend fun executeExercisesForMuscle(muscle: String, profileId: Int?): String {
        val available = dataProvider.getAllAvailableExercises(profileId)
        val muscleKeywords = getMuscleKeywords(muscle)
        val exercises = available.filter { ex ->
            muscleKeywords.any { kw -> ex.lowercase().contains(kw) }
        }.take(5)

        return if (exercises.isNotEmpty()) {
            buildString {
                append("🏋️ Esercizi per $muscle:\n\n")
                exercises.forEachIndexed { index, ex ->
                    append("${index + 1}. $ex\n")
                }
            }.trimEnd()
        } else {
            "Non ho trovato esercizi specifici per $muscle nello storico."
        }
    }

    private fun getMuscleKeywords(muscle: String): List<String> {
        return when (muscle.lowercase()) {
            "gambe" -> listOf("squat", "leg", "gambe", "quad", "stacco", "affondi", "pressa", "curl gambe", "polpacci")
            "petto" -> listOf("panca", "petto", "chest", "push", "croci", "dip", "pettorali")
            "schiena" -> listOf("schiena", "lat", "rematore", "trazioni", "pull", "dorsal", "back", "pulldown")
            "spalle" -> listOf("spalle", "shoulder", "military", "lento", "alzate", "deltoidi")
            "braccia" -> listOf("bicipiti", "tricipiti", "curl", "french", "braccia", "arm")
            "addominali" -> listOf("addominali", "abs", "crunch", "plank", "core")
            else -> listOf(muscle.lowercase())
        }
    }

    // ==================== ATTREZZI ====================

    private suspend fun executeMostUsedEquipment(profileId: Int?, possessive: String): String {
        val equipment = dataProvider.getMostUsedEquipment(5, profileId)
        return if (equipment.isNotEmpty()) {
            buildString {
                append("🏋️ Attrezzi piu usati $possessive:\n\n")
                equipment.forEachIndexed { index, (name, count) ->
                    append("${index + 1}. $name ($count volte)\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora attrezzi registrati."
        }
    }

    private suspend fun executeEquipmentForMuscle(muscle: String, profileId: Int?, subject: String, verb: String): String {
        val equipment = dataProvider.getEquipmentForMuscle(muscle, profileId)
        return if (equipment.isNotEmpty()) {
            "Per $muscle $subject $verb usato: ${equipment.joinToString(", ")}"
        } else {
            "Non ho dati sugli attrezzi usati per $muscle."
        }
    }

    private suspend fun executeAllEquipment(profileId: Int?, possessive: String): String {
        val equipment = dataProvider.getMostUsedEquipment(20, profileId)
        return if (equipment.isNotEmpty()) {
            buildString {
                append("🏋️ Tutti gli attrezzi $possessive:\n\n")
                equipment.forEach { (name, count) ->
                    append("• $name ($count usi)\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora attrezzi registrati."
        }
    }

    // ==================== APPUNTAMENTI ====================

    private suspend fun executeTodayAppointments(profileId: Int?, profileName: String?): String {
        val today = LocalDate.now().format(DATE_FORMAT)

        val appointments = if (profileId != null) {
            db.coachAppointmentDao()?.getAppointmentsByProfileAndDate(profileId, today) ?: emptyList()
        } else {
            db.coachAppointmentDao()?.getTodayAppointments(today) ?: emptyList()
        }

        val header = if (profileName != null) "📅 Appuntamenti oggi per $profileName:" else "📅 Appuntamenti di oggi:"

        return if (appointments.isNotEmpty()) {
            buildString {
                append("$header\n\n")
                appointments.forEach { a ->
                    val status = if (a.isCompleted) "✅" else "⏳"
                    append("$status ${a.time ?: "--:--"} - ${a.title}\n")
                    a.notes?.let { if (it.isNotBlank()) append("   📝 $it\n") }
                }
            }.trimEnd()
        } else {
            "$header\n\nNessun appuntamento per oggi."
        }
    }

    private suspend fun executeWeekAppointments(profileId: Int?, profileName: String?): String {
        val today = LocalDate.now()
        val weekEnd = today.plusDays(7)
        val startDate = today.format(DATE_FORMAT)
        val endDate = weekEnd.format(DATE_FORMAT)

        val appointments = if (profileId != null) {
            db.coachAppointmentDao()?.getAppointmentsInRange(profileId, startDate, endDate) ?: emptyList()
        } else {
            db.coachAppointmentDao()?.getAllAppointmentsInRange(startDate, endDate) ?: emptyList()
        }

        val header = if (profileName != null) "📆 Appuntamenti settimana per $profileName:" else "📆 Appuntamenti della settimana:"

        return if (appointments.isNotEmpty()) {
            buildString {
                append("$header\n\n")
                appointments.groupBy { it.date }.forEach { (date, dayApps) ->
                    append("📅 $date:\n")
                    dayApps.forEach { a ->
                        val status = if (a.isCompleted) "✅" else "⏳"
                        append("  $status ${a.time ?: "--:--"} - ${a.title}\n")
                    }
                    append("\n")
                }
            }.trimEnd()
        } else {
            "$header\n\nNessun appuntamento questa settimana."
        }
    }

    private suspend fun executeNextAppointments(profileId: Int?, profileName: String?): String {
        val today = LocalDate.now().format(DATE_FORMAT)

        val appointments = if (profileId != null) {
            db.coachAppointmentDao()?.getUpcomingAppointments(profileId, today, 5) ?: emptyList()
        } else {
            // Per tutti i profili, prendi gli appuntamenti futuri
            val weekEnd = LocalDate.now().plusDays(30).format(DATE_FORMAT)
            (db.coachAppointmentDao()?.getAllAppointmentsInRange(today, weekEnd) ?: emptyList()).take(5)
        }

        val header = if (profileName != null) "📋 Prossimi appuntamenti per $profileName:" else "📋 Prossimi appuntamenti:"

        return if (appointments.isNotEmpty()) {
            buildString {
                append("$header\n\n")
                appointments.forEach { a ->
                    val status = if (a.isCompleted) "✅" else "⏳"
                    append("$status ${a.date} ${a.time ?: ""} - ${a.title}\n")
                }
            }.trimEnd()
        } else {
            "$header\n\nNessun appuntamento in programma."
        }
    }

    private suspend fun executePendingAppointments(profileId: Int?, profileName: String?): String {
        val today = LocalDate.now().format(DATE_FORMAT)

        val allAppointments = if (profileId != null) {
            db.coachAppointmentDao()?.getAppointmentsByProfileSync(profileId) ?: emptyList()
        } else {
            db.coachAppointmentDao()?.getAllAppointments() ?: emptyList()
        }

        val pending = allAppointments.filter { !it.isCompleted }

        val header = if (profileName != null) "⏳ Appuntamenti da completare per $profileName:" else "⏳ Appuntamenti da completare:"

        return if (pending.isNotEmpty()) {
            buildString {
                append("$header\n\n")
                pending.take(10).forEach { a ->
                    append("• ${a.date} ${a.time ?: ""} - ${a.title}\n")
                }
                if (pending.size > 10) {
                    append("\n...e altri ${pending.size - 10}")
                }
            }.trimEnd()
        } else {
            "$header\n\nTutti gli appuntamenti sono completati! ✅"
        }
    }

    private suspend fun executeMonthAppointments(profileId: Int?, profileName: String?): String {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1).format(DATE_FORMAT)
        val monthEnd = today.withDayOfMonth(today.lengthOfMonth()).format(DATE_FORMAT)

        val appointments = if (profileId != null) {
            db.coachAppointmentDao()?.getAppointmentsInRange(profileId, monthStart, monthEnd) ?: emptyList()
        } else {
            db.coachAppointmentDao()?.getAllAppointmentsInRange(monthStart, monthEnd) ?: emptyList()
        }

        val completed = appointments.count { it.isCompleted }
        val pending = appointments.size - completed

        val header = if (profileName != null) "📊 Riepilogo mese per $profileName:" else "📊 Riepilogo appuntamenti mese:"

        return buildString {
            append("$header\n\n")
            append("• Totale appuntamenti: ${appointments.size}\n")
            append("• Completati: $completed ✅\n")
            append("• Da completare: $pending ⏳\n")
            if (appointments.isNotEmpty()) {
                append("• Tasso completamento: ${(completed * 100) / appointments.size}%")
            }
        }
    }

    // ==================== PROFILI ====================

    private suspend fun executeListProfiles(): String {
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }
        return if (profilesCache.isNotEmpty()) {
            buildString {
                append("👥 Profili disponibili:\n\n")
                profilesCache.forEach { profile ->
                    append("• ${profile.name}\n")
                }
            }.trimEnd()
        } else {
            "Non ci sono ancora profili registrati."
        }
    }

    private suspend fun executeProfileSummary(profileId: Int?, profileName: String?): String {
        if (profileId == null || profileName == null) {
            return "Seleziona un profilo per vedere il riepilogo."
        }

        val schede = db.schedeDao().getSchedeByCoachProfile(profileId)
        val totalWorkouts = schede.size
        val completed = schede.count { it.completed }
        val mostTrained = db.schedeDao().getMostTrainedMuscleGroupByCoach(profileId)
        val lastWorkout = db.schedeDao().getLastWorkoutDateByCoach(profileId)

        val today = LocalDate.now().format(DATE_FORMAT)
        val upcomingAppointments = db.coachAppointmentDao()?.countUpcomingByProfile(profileId, today) ?: 0

        return buildString {
            append("👤 Riepilogo profilo: $profileName\n\n")
            append("📊 Statistiche:\n")
            append("• Allenamenti totali: $totalWorkouts\n")
            append("• Completati: $completed\n")
            mostTrained?.let { append("• Muscolo preferito: $it\n") }
            lastWorkout?.let { append("• Ultimo allenamento: $it\n") }
            append("\n📅 Appuntamenti futuri: $upcomingAppointments")
        }
    }

    private suspend fun executeProfileComparison(): String {
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }

        if (profilesCache.size < 2) {
            return "Servono almeno 2 profili per un confronto."
        }

        return buildString {
            append("📊 Confronto profili:\n\n")
            profilesCache.take(5).forEach { profile ->
                val count = db.schedeDao().countSchedeByCoachProfile(profile.id ?: 0)
                append("${profile.name}: $count allenamenti\n")
            }
        }.trimEnd()
    }

    // ==================== UTILITY ====================

    private fun parseDate(dateStr: String): LocalDate? {
        if (dateStr.isBlank()) return null
        val datePart = dateStr.trim().let { if (it.length >= 10) it.substring(0, 10) else it }
        return try {
            LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (_: Exception) {
            try {
                LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (_: Exception) {
                null
            }
        }
    }
}
