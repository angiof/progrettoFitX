package com.app.fityo.chat.engine

import com.app.fityo.chat.data.ChatIntent

/**
 * Classifica le domande dell'utente in intent specifici.
 * Usa keyword matching per determinare il tipo di domanda.
 */
class IntentClassifier {

    private val historyKeywords = listOf(
        "quando", "ultima volta", "ultimo", "ultim", "che giorno", "che ora",
        "a che ora", "orario", "data", "scorso", "precedente", "fatto",
        "giorno", "settimana scorsa", "mese scorso", "ieri", "giovedi",
        "lunedi", "martedi", "mercoledi", "venerdi", "sabato", "domenica"
    )

    private val performanceKeywords = listOf(
        "peso", "carico", "kg", "quanto", "massimo", "record", "max",
        "progresso", "miglioramento", "serie", "ripetizioni", "rep",
        "set", "volume", "caricato", "sollevato", "1rm", "pr"
    )

    private val planningKeywords = listOf(
        "suggerisci", "consiglia", "cosa fare", "scheda", "allenamento",
        "esercizi", "proponi", "crea", "genera", "oggi", "domani",
        "prossimo", "programma", "routine", "workout", "suggerimento"
    )

    private val comparisonKeywords = listOf(
        "confronto", "rispetto", "meglio", "peggio", "piu", "meno",
        "differenza", "cambiato", "variazione", "trend", "andamento",
        "prima", "dopo", "migliorato", "peggiorato", "comparazione"
    )

    private val equipmentKeywords = listOf(
        "attrezzo", "attrezzatura", "bilanciere", "manubri", "macchina",
        "cavi", "sbarra", "kettlebell", "elastici", "panca", "rack",
        "con cosa", "quale attrezzo", "strumento"
    )

    private val generalKeywords = listOf(
        "quanti", "totale", "media", "statistiche", "stats", "riepilogo",
        "overview", "sommario", "conteggio", "frequenza"
    )

    private val profileKeywords = listOf(
        "profili", "profilo", "atleti", "atleta", "clienti", "cliente",
        "mostra profili", "quali profili", "chi sono", "lista clienti",
        "elenco atleti", "i miei atleti", "i miei clienti"
    )

    // Muscoli riconosciuti
    private val muscleGroups = listOf(
        "gambe", "petto", "pettorali", "dorso", "schiena", "spalle",
        "bicipiti", "tricipiti", "addominali", "glutei", "polpacci",
        "avambracci", "trapezio", "deltoidi", "quadricipiti", "femorali",
        "core", "braccia", "upper", "lower", "full body"
    )

    // Esercizi comuni
    private val commonExercises = listOf(
        "panca", "squat", "stacco", "deadlift", "bench press", "military press",
        "curl", "french press", "lat machine", "rematore", "pull up", "push up",
        "dip", "leg press", "leg extension", "leg curl", "calf raise",
        "shoulder press", "alzate laterali", "croci", "fly", "row"
    )

    /**
     * Classifica la domanda dell'utente.
     */
    fun classify(query: String): ChatIntent {
        val normalized = query.lowercase().trim()

        // Calcola punteggi per ogni categoria
        val scores = mutableMapOf(
            "history" to countMatches(normalized, historyKeywords),
            "performance" to countMatches(normalized, performanceKeywords),
            "planning" to countMatches(normalized, planningKeywords),
            "comparison" to countMatches(normalized, comparisonKeywords),
            "equipment" to countMatches(normalized, equipmentKeywords),
            "general" to countMatches(normalized, generalKeywords),
            "profile" to countMatches(normalized, profileKeywords)
        )

        // Boost per pattern specifici
        if (normalized.contains("quando") || normalized.contains("ultima volta")) {
            scores["history"] = scores["history"]!! + 2
        }
        if (normalized.contains("quanto") && normalized.contains("kg")) {
            scores["performance"] = scores["performance"]!! + 2
        }
        if (normalized.contains("suggerisci") || normalized.contains("consiglia")) {
            scores["planning"] = scores["planning"]!! + 2
        }
        if (normalized.contains("confronta") || normalized.contains("rispetto")) {
            scores["comparison"] = scores["comparison"]!! + 2
        }
        if (normalized.contains("attrezzo") || normalized.contains("con cosa")) {
            scores["equipment"] = scores["equipment"]!! + 2
        }
        if (normalized.contains("quali profili") || normalized.contains("mostra profili") ||
            normalized.contains("i miei atleti") || normalized.contains("i miei clienti")) {
            scores["profile"] = scores["profile"]!! + 2
        }

        // Trova la categoria con punteggio massimo
        val maxEntry = scores.maxByOrNull { it.value }

        return when {
            maxEntry == null || maxEntry.value == 0 -> ChatIntent.GeneralQuery
            maxEntry.key == "history" -> ChatIntent.HistoryQuery
            maxEntry.key == "performance" -> ChatIntent.PerformanceQuery
            maxEntry.key == "planning" -> ChatIntent.PlanningQuery
            maxEntry.key == "comparison" -> ChatIntent.ComparisonQuery
            maxEntry.key == "equipment" -> ChatIntent.EquipmentQuery
            maxEntry.key == "profile" -> ChatIntent.ProfileQuery
            else -> ChatIntent.GeneralQuery
        }
    }

    private fun countMatches(text: String, keywords: List<String>): Int {
        return keywords.count { text.contains(it) }
    }

    /**
     * Estrae il gruppo muscolare dalla domanda.
     */
    fun extractMuscleGroup(query: String): String? {
        val normalized = query.lowercase()
        return muscleGroups.firstOrNull { normalized.contains(it) }
    }

    /**
     * Estrae il nome dell'esercizio dalla domanda.
     */
    fun extractExerciseName(query: String): String? {
        val normalized = query.lowercase()
        return commonExercises.firstOrNull { normalized.contains(it) }
    }

    /**
     * Estrae il giorno della settimana dalla domanda.
     */
    fun extractDayOfWeek(query: String): String? {
        val normalized = query.lowercase()
        val days = mapOf(
            "lunedi" to "MONDAY",
            "martedi" to "TUESDAY",
            "mercoledi" to "WEDNESDAY",
            "giovedi" to "THURSDAY",
            "venerdi" to "FRIDAY",
            "sabato" to "SATURDAY",
            "domenica" to "SUNDAY",
            "ieri" to "YESTERDAY",
            "oggi" to "TODAY"
        )
        return days.entries.firstOrNull { normalized.contains(it.key) }?.value
    }

    /**
     * Estrae un periodo temporale (in giorni) dalla domanda.
     */
    fun extractTimeFrame(query: String): Int? {
        val normalized = query.lowercase()
        return when {
            normalized.contains("settimana") -> 7
            normalized.contains("mese") -> 30
            normalized.contains("anno") -> 365
            normalized.contains("ieri") -> 1
            normalized.contains("2 settimane") || normalized.contains("due settimane") -> 14
            normalized.contains("3 giorni") || normalized.contains("tre giorni") -> 3
            else -> null
        }
    }

    /**
     * Estrae un numero dalla domanda (es: "4 esercizi").
     */
    fun extractNumber(query: String): Int? {
        val numberPattern = Regex("\\b(\\d+)\\b")
        val match = numberPattern.find(query)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }
}
