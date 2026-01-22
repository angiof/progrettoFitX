package com.app.fityo.chat.data

/**
 * Sistema di domande predefinite con mapping diretto a query del database.
 * L'utente puo SOLO selezionare queste domande - niente input libero.
 * Ogni domanda esegue una query specifica e restituisce dati reali.
 *
 * ARCHITETTURA:
 * - Ogni QueryType mappa a una specifica operazione sul DB
 * - Le risposte sono SEMPRE basate sui dati reali
 * - Gemma viene usato SOLO per suggerimenti personalizzati (opzionale)
 */

/**
 * Tipo di query predefinita che mappa a una specifica operazione sul DB.
 */
enum class QueryType {
    // ===== STORICO =====
    LAST_WORKOUT,           // Ultimo allenamento generale
    LAST_LEGS,              // Ultima volta gambe
    LAST_CHEST,             // Ultima volta petto
    LAST_BACK,              // Ultima volta schiena
    LAST_SHOULDERS,         // Ultima volta spalle
    LAST_ARMS,              // Ultima volta braccia
    LAST_ABS,               // Ultima volta addominali
    DAYS_SINCE_WORKOUT,     // Giorni dall'ultimo allenamento
    WORKOUT_HISTORY,        // Ultimi 5 allenamenti

    // ===== PERFORMANCE =====
    MAX_BENCH,              // Record panca piana
    MAX_SQUAT,              // Record squat
    MAX_DEADLIFT,           // Record stacco
    LAST_BENCH_DETAILS,     // Ultimo allenamento panca
    LAST_SQUAT_DETAILS,     // Ultimo allenamento squat
    EXERCISE_PROGRESSION,   // Progressione di un esercizio

    // ===== STATISTICHE AVANZATE =====
    WEEKLY_COUNT,           // Allenamenti questa settimana
    MONTHLY_COUNT,          // Allenamenti questo mese
    TOTAL_WORKOUTS,         // Totale allenamenti
    MOST_TRAINED_MUSCLE,    // Muscolo piu allenato
    LEAST_TRAINED_MUSCLE,   // Muscolo meno allenato
    WEEKLY_AVERAGE,         // Media settimanale
    MUSCLE_DISTRIBUTION,    // Distribuzione % muscoli
    INTENSITY_ANALYSIS,     // Analisi intensita
    VOLUME_TOTAL,           // Volume totale (kg * reps * sets)
    CONSISTENCY_SCORE,      // Punteggio consistenza
    FAVORITE_DAY,           // Giorno preferito per allenarsi
    STREAK_DAYS,            // Giorni consecutivi di allenamento

    // ===== CONFRONTI =====
    COMPARE_WEEKS,          // Confronto settimana attuale vs precedente
    COMPARE_MONTHS,         // Confronto mese attuale vs precedente
    PROGRESS_TREND,         // Trend di progresso generale

    // ===== SUGGERIMENTI (con analisi dati) =====
    SUGGEST_EXERCISES,      // Suggerisci esercizi
    WHAT_TODAY,             // Cosa alleno oggi (analisi intelligente)
    EXERCISES_FOR_LEGS,     // Esercizi per gambe
    EXERCISES_FOR_CHEST,    // Esercizi per petto
    EXERCISES_FOR_BACK,     // Esercizi per schiena
    SMART_SUGGESTION,       // Suggerimento intelligente basato su tutti i dati
    RECOVERY_ADVICE,        // Consiglio recupero

    // ===== ATTREZZI =====
    MOST_USED_EQUIPMENT,    // Attrezzi piu usati
    EQUIPMENT_FOR_LEGS,     // Attrezzi per gambe
    EQUIPMENT_FOR_CHEST,    // Attrezzi per petto
    ALL_EQUIPMENT,          // Tutti gli attrezzi usati

    // ===== APPUNTAMENTI (Coach Mode) =====
    TODAY_APPOINTMENTS,     // Appuntamenti di oggi
    WEEK_APPOINTMENTS,      // Appuntamenti della settimana
    NEXT_APPOINTMENTS,      // Prossimi 5 appuntamenti
    PENDING_APPOINTMENTS,   // Appuntamenti non completati
    MONTH_APPOINTMENTS,     // Riepilogo appuntamenti mese

    // ===== PROFILI =====
    LIST_PROFILES,          // Lista profili disponibili
    PROFILE_SUMMARY,        // Riepilogo profilo attivo
    PROFILE_COMPARISON      // Confronto tra profili
}

/**
 * Rappresenta una domanda predefinita con il suo tipo di query.
 */
data class PredefinedQuestion(
    val displayText: String,           // Testo mostrato all'utente
    val queryType: QueryType,          // Tipo di query da eseguire
    val muscleTarget: String? = null,  // Gruppo muscolare se applicabile
    val exerciseTarget: String? = null,// Esercizio se applicabile
    val requiresProfile: Boolean = false // Richiede un profilo selezionato
)

/**
 * Categoria di domande per la UI.
 */
data class QuestionCategory(
    val emoji: String,
    val title: String,
    val questions: List<PredefinedQuestion>,
    val coachOnly: Boolean = false  // Visibile solo in coach mode
)

/**
 * Oggetto che contiene tutte le domande predefinite organizzate per categoria.
 */
object PredefinedQueries {

    val categories: List<QuestionCategory> = listOf(
        // ===== STORICO =====
        QuestionCategory(
            emoji = "📅",
            title = "Storico",
            questions = listOf(
                PredefinedQuestion("Ultimo allenamento?", QueryType.LAST_WORKOUT),
                PredefinedQuestion("Ultimi 5 allenamenti", QueryType.WORKOUT_HISTORY),
                PredefinedQuestion("Quando ho fatto gambe?", QueryType.LAST_LEGS, muscleTarget = "gambe"),
                PredefinedQuestion("Quando ho fatto petto?", QueryType.LAST_CHEST, muscleTarget = "petto"),
                PredefinedQuestion("Quando ho fatto schiena?", QueryType.LAST_BACK, muscleTarget = "schiena"),
                PredefinedQuestion("Quando ho fatto spalle?", QueryType.LAST_SHOULDERS, muscleTarget = "spalle"),
                PredefinedQuestion("Quando ho fatto braccia?", QueryType.LAST_ARMS, muscleTarget = "braccia"),
                PredefinedQuestion("Giorni senza allenarmi?", QueryType.DAYS_SINCE_WORKOUT)
            )
        ),

        // ===== PERFORMANCE =====
        QuestionCategory(
            emoji = "💪",
            title = "Performance",
            questions = listOf(
                PredefinedQuestion("Record in panca?", QueryType.MAX_BENCH, exerciseTarget = "panca"),
                PredefinedQuestion("Record in squat?", QueryType.MAX_SQUAT, exerciseTarget = "squat"),
                PredefinedQuestion("Record in stacco?", QueryType.MAX_DEADLIFT, exerciseTarget = "stacco"),
                PredefinedQuestion("Dettagli ultimo panca", QueryType.LAST_BENCH_DETAILS, exerciseTarget = "panca"),
                PredefinedQuestion("Dettagli ultimo squat", QueryType.LAST_SQUAT_DETAILS, exerciseTarget = "squat"),
                PredefinedQuestion("Come sto progredendo?", QueryType.PROGRESS_TREND)
            )
        ),

        // ===== STATISTICHE AVANZATE =====
        QuestionCategory(
            emoji = "📊",
            title = "Statistiche",
            questions = listOf(
                PredefinedQuestion("Allenamenti questa settimana?", QueryType.WEEKLY_COUNT),
                PredefinedQuestion("Allenamenti questo mese?", QueryType.MONTHLY_COUNT),
                PredefinedQuestion("Totale allenamenti?", QueryType.TOTAL_WORKOUTS),
                PredefinedQuestion("Distribuzione muscoli", QueryType.MUSCLE_DISTRIBUTION),
                PredefinedQuestion("Muscolo piu allenato?", QueryType.MOST_TRAINED_MUSCLE),
                PredefinedQuestion("Muscolo meno allenato?", QueryType.LEAST_TRAINED_MUSCLE),
                PredefinedQuestion("Media settimanale?", QueryType.WEEKLY_AVERAGE),
                PredefinedQuestion("Volume totale sollevato?", QueryType.VOLUME_TOTAL),
                PredefinedQuestion("Punteggio consistenza?", QueryType.CONSISTENCY_SCORE),
                PredefinedQuestion("Giorno preferito?", QueryType.FAVORITE_DAY),
                PredefinedQuestion("Confronto settimane", QueryType.COMPARE_WEEKS),
                PredefinedQuestion("Confronto mesi", QueryType.COMPARE_MONTHS)
            )
        ),

        // ===== SUGGERIMENTI INTELLIGENTI =====
        QuestionCategory(
            emoji = "🧠",
            title = "Suggerimenti",
            questions = listOf(
                PredefinedQuestion("Cosa dovrei allenare oggi?", QueryType.SMART_SUGGESTION),
                PredefinedQuestion("Suggeriscimi 4 esercizi", QueryType.SUGGEST_EXERCISES),
                PredefinedQuestion("Ho bisogno di riposo?", QueryType.RECOVERY_ADVICE),
                PredefinedQuestion("Esercizi per gambe", QueryType.EXERCISES_FOR_LEGS, muscleTarget = "gambe"),
                PredefinedQuestion("Esercizi per petto", QueryType.EXERCISES_FOR_CHEST, muscleTarget = "petto"),
                PredefinedQuestion("Esercizi per schiena", QueryType.EXERCISES_FOR_BACK, muscleTarget = "schiena")
            )
        ),

        // ===== ATTREZZI =====
        QuestionCategory(
            emoji = "🏋️",
            title = "Attrezzi",
            questions = listOf(
                PredefinedQuestion("Attrezzi piu usati?", QueryType.MOST_USED_EQUIPMENT),
                PredefinedQuestion("Tutti gli attrezzi", QueryType.ALL_EQUIPMENT),
                PredefinedQuestion("Attrezzi per gambe?", QueryType.EQUIPMENT_FOR_LEGS, muscleTarget = "gambe"),
                PredefinedQuestion("Attrezzi per petto?", QueryType.EQUIPMENT_FOR_CHEST, muscleTarget = "petto")
            )
        ),

        // ===== APPUNTAMENTI (Coach Mode) =====
        QuestionCategory(
            emoji = "📆",
            title = "Appuntamenti",
            questions = listOf(
                PredefinedQuestion("Appuntamenti di oggi?", QueryType.TODAY_APPOINTMENTS),
                PredefinedQuestion("Appuntamenti settimana?", QueryType.WEEK_APPOINTMENTS),
                PredefinedQuestion("Prossimi appuntamenti", QueryType.NEXT_APPOINTMENTS),
                PredefinedQuestion("Appuntamenti da completare", QueryType.PENDING_APPOINTMENTS),
                PredefinedQuestion("Riepilogo mese", QueryType.MONTH_APPOINTMENTS)
            ),
            coachOnly = true
        ),

        // ===== PROFILI =====
        QuestionCategory(
            emoji = "👥",
            title = "Profili",
            questions = listOf(
                PredefinedQuestion("Mostra profili", QueryType.LIST_PROFILES),
                PredefinedQuestion("Riepilogo profilo attivo", QueryType.PROFILE_SUMMARY, requiresProfile = true)
            )
        )
    )

    /**
     * Trova la domanda predefinita dal testo visualizzato.
     */
    fun findByDisplayText(text: String): PredefinedQuestion? {
        return categories.flatMap { it.questions }.find { it.displayText == text }
    }

    /**
     * Lista piatta di tutte le domande.
     */
    val allQuestions: List<PredefinedQuestion>
        get() = categories.flatMap { it.questions }

    /**
     * Ottiene categorie filtrate per modalita (coach vs personal).
     */
    fun getCategoriesForMode(isCoachMode: Boolean): List<QuestionCategory> {
        return if (isCoachMode) {
            categories // Mostra tutte le categorie
        } else {
            categories.filter { !it.coachOnly }
        }
    }

    /**
     * Ottiene domande casuali (per suggerimenti rapidi).
     */
    fun getRandomQuestions(count: Int = 4): List<PredefinedQuestion> {
        return allQuestions.shuffled().take(count)
    }
}
