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
                text = "Ciao! Sono il tuo assistente fitness AI. Chiedimi qualsiasi cosa sui tuoi allenamenti!\n\nProva a chiedere:\n- Quando ho fatto gambe?\n- Quanto peso in panca?\n- Suggeriscimi 4 esercizi",
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
 * Quick suggestions per aiutare l'utente a iniziare.
 */
object QuickSuggestions {
    val suggestions = listOf(
        "Quando ho fatto gambe?",
        "Quanto peso in panca?",
        "Suggeriscimi 4 esercizi",
        "Quanti allenamenti questa settimana?",
        "A che ora vado in palestra?",
        "Qual e il mio record di squat?",
        "Confronta questo mese con il precedente",
        "Con quale attrezzo ho fatto l'ultimo esercizio?"
    )

    fun getRandom(count: Int = 4): List<String> {
        return suggestions.shuffled().take(count)
    }
}
