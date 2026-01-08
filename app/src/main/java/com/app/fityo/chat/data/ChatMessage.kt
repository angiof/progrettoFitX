package com.app.fityo.chat.data

import com.app.fityo.data_layer.db.ChatMessageEntity
import java.util.UUID

/**
 * Domain model per i messaggi della chat.
 * Rappresenta un singolo messaggio nella conversazione.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val intent: ChatIntent? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val profileId: Int? = null,
    val sessionId: String? = null
) {
    /**
     * Converte in entity per la persistenza nel database.
     */
    fun toEntity(): ChatMessageEntity {
        return ChatMessageEntity(
            id = null, // Auto-generated
            text = text,
            isFromUser = isFromUser,
            timestamp = timestamp,
            intentType = intent?.name,
            profileId = profileId,
            sessionId = sessionId
        )
    }

    companion object {
        /**
         * Crea un ChatMessage da un'entity del database.
         */
        fun fromEntity(entity: ChatMessageEntity): ChatMessage {
            return ChatMessage(
                id = entity.id?.toString() ?: UUID.randomUUID().toString(),
                text = entity.text,
                isFromUser = entity.isFromUser,
                timestamp = entity.timestamp,
                intent = entity.intentType?.let { ChatIntent.fromString(it) },
                isLoading = false,
                isError = false,
                profileId = entity.profileId,
                sessionId = entity.sessionId
            )
        }

        /**
         * Messaggio di benvenuto iniziale.
         */
        fun welcomeMessage(): ChatMessage {
            return ChatMessage(
                text = "Ciao! Sono il tuo assistente fitness.\n\nSeleziona una categoria qui sotto e scegli una domanda per ottenere informazioni sui tuoi allenamenti!",
                isFromUser = false
            )
        }

        /**
         * Messaggio di caricamento (placeholder).
         */
        fun loadingMessage(): ChatMessage {
            return ChatMessage(
                text = "",
                isFromUser = false,
                isLoading = true
            )
        }

        /**
         * Messaggio di errore.
         */
        fun errorMessage(error: String? = null): ChatMessage {
            return ChatMessage(
                text = error ?: "Mi dispiace, c'e stato un errore. Riprova!",
                isFromUser = false,
                isError = true
            )
        }
    }
}

/**
 * Categorie di domande predefinite.
 * L'utente puo solo selezionare queste - niente testo libero.
 */
object QuickSuggestions {
    // Categorie con emoji e domande predefinite
    data class SuggestionCategory(
        val emoji: String,
        val title: String,
        val questions: List<String>
    )

    val categories = listOf(
        SuggestionCategory(
            emoji = "📅",
            title = "Storico",
            questions = listOf(
                "Quando ho fatto gambe?",
                "Quando ho fatto petto?",
                "Quando ho fatto schiena?",
                "Ultimo allenamento?"
            )
        ),
        SuggestionCategory(
            emoji = "💪",
            title = "Performance",
            questions = listOf(
                "Quanto peso in panca?",
                "Quanto peso in squat?",
                "Quanto peso in stacco?",
                "Il mio record?"
            )
        ),
        SuggestionCategory(
            emoji = "📊",
            title = "Statistiche",
            questions = listOf(
                "Quanti allenamenti questa settimana?",
                "Quanti allenamenti questo mese?",
                "Muscolo piu allenato?",
                "Confronta con mese scorso"
            )
        ),
        SuggestionCategory(
            emoji = "💡",
            title = "Suggerimenti",
            questions = listOf(
                "Suggeriscimi 4 esercizi",
                "Cosa alleno oggi?",
                "Esercizi per gambe?",
                "Esercizi per petto?"
            )
        )
    )

    // Per retrocompatibilita
    val suggestions = categories.flatMap { it.questions }

    fun getRandom(count: Int = 4): List<String> {
        return suggestions.shuffled().take(count)
    }
}
