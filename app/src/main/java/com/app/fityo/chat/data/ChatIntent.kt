package com.app.fityo.chat.data

/**
 * Tipi di intent per classificare le domande dell'utente.
 * Ogni tipo corrisponde a un template di prompt specifico.
 */
sealed class ChatIntent(val name: String) {

    /**
     * Domande sulla storia degli allenamenti.
     * Es: "Quando ho fatto gambe l'ultima volta?", "A che ora vado in palestra?"
     */
    object HistoryQuery : ChatIntent("HISTORY")

    /**
     * Domande su performance e statistiche.
     * Es: "Quanto peso in panca?", "Il mio record di squat?"
     */
    object PerformanceQuery : ChatIntent("PERFORMANCE")

    /**
     * Richieste di suggerimenti e pianificazione.
     * Es: "Suggeriscimi 4 esercizi", "Cosa faccio oggi?"
     */
    object PlanningQuery : ChatIntent("PLANNING")

    /**
     * Domande generali su statistiche e overview.
     * Es: "Quanti allenamenti questa settimana?", "Qual e il mio muscolo piu allenato?"
     */
    object GeneralQuery : ChatIntent("GENERAL")

    /**
     * Confronti tra periodi diversi.
     * Es: "Sto allenando piu di prima?", "Confronta questo mese con il precedente"
     */
    object ComparisonQuery : ChatIntent("COMPARISON")

    /**
     * Domande sugli attrezzi e equipaggiamento.
     * Es: "Con quale attrezzo ho fatto gambe?", "Quali attrezzi uso di piu?"
     */
    object EquipmentQuery : ChatIntent("EQUIPMENT")

    /**
     * Domande sui profili/atleti.
     * Es: "Quali profili ho?", "Mostra atleti", "Chi sono i miei clienti?"
     */
    object ProfileQuery : ChatIntent("PROFILE")

    /**
     * Intent non classificato - fallback.
     */
    object Unknown : ChatIntent("UNKNOWN")

    companion object {
        fun fromString(name: String): ChatIntent {
            return when (name.uppercase()) {
                "HISTORY" -> HistoryQuery
                "PERFORMANCE" -> PerformanceQuery
                "PLANNING" -> PlanningQuery
                "GENERAL" -> GeneralQuery
                "COMPARISON" -> ComparisonQuery
                "EQUIPMENT" -> EquipmentQuery
                "PROFILE" -> ProfileQuery
                else -> Unknown
            }
        }
    }
}
