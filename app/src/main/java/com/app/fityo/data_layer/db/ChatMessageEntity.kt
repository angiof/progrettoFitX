package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity per la persistenza dei messaggi della chat AI.
 * Salva la cronologia delle conversazioni con il Body Intelligence Chatbot.
 */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    /** Testo del messaggio */
    val text: String,

    /** true = messaggio dell'utente, false = risposta AI */
    val isFromUser: Boolean,

    /** Timestamp in millisecondi */
    val timestamp: Long = System.currentTimeMillis(),

    /** Tipo di intent classificato: HISTORY, PERFORMANCE, PLANNING, GENERAL, COMPARISON */
    val intentType: String? = null,

    /** ID del profilo coach associato (null = tutti i profili) */
    val profileId: Int? = null,

    /** ID sessione per raggruppare messaggi della stessa conversazione */
    val sessionId: String? = null
)
