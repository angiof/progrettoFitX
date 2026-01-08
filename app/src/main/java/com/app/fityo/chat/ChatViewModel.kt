package com.app.fityo.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.chat.data.ChatIntent
import com.app.fityo.chat.data.ChatMessage
import com.app.fityo.chat.engine.IntentClassifier
import com.app.fityo.data_layer.db.DB.DbFit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel per la gestione dello stato della chat.
 * Gestisce messaggi, processamento domande e persistenza.
 */
class ChatViewModel(
    application: Application,
    private val db: DbFit,
    private val profileId: Int?
) : AndroidViewModel(application) {

    private val chatHelper = GemmaChatHelper(application, db)
    private val classifier = IntentClassifier()
    private val sessionId = UUID.randomUUID().toString()

    companion object {
        private const val TAG = "ChatViewModel"
    }

    // ==================== STATE ====================

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _gemmaAvailable = MutableStateFlow(false)
    val gemmaAvailable: StateFlow<Boolean> = _gemmaAvailable.asStateFlow()

    private val _gemmaReady = MutableStateFlow(false)
    val gemmaReady: StateFlow<Boolean> = _gemmaReady.asStateFlow()

    // ==================== INIT ====================

    init {
        checkGemmaAvailability()
        loadChatHistory()
    }

    private fun checkGemmaAvailability() {
        _gemmaAvailable.value = chatHelper.isGemmaAvailable()
        _gemmaReady.value = chatHelper.isGemmaReady()
        Log.d(TAG, "Gemma available: ${_gemmaAvailable.value}, ready: ${_gemmaReady.value}")
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            try {
                // Carica messaggi recenti dal database
                val recentMessages = db.chatDao().getRecentMessages(50)

                if (recentMessages.isEmpty()) {
                    // Prima volta: mostra messaggio di benvenuto
                    val welcome = ChatMessage.welcomeMessage().copy(
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    _messages.value = listOf(welcome)
                    saveMessage(welcome)
                } else {
                    // Carica storico e aggiungi separatore per nuova sessione
                    val history = recentMessages
                        .reversed() // Dal piu vecchio al piu recente
                        .map { ChatMessage.fromEntity(it) }

                    _messages.value = history
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading chat history", e)
                // Fallback: mostra solo il benvenuto
                _messages.value = listOf(ChatMessage.welcomeMessage())
            }
        }
    }

    // ==================== ACTIONS ====================

    /**
     * Invia un messaggio dell'utente e genera risposta.
     */
    fun sendMessage(text: String) {
        if (text.isBlank() || _isProcessing.value) return

        viewModelScope.launch {
            // 1. Aggiungi messaggio utente
            val intent = classifier.classify(text)
            val userMsg = ChatMessage(
                text = text.trim(),
                isFromUser = true,
                intent = intent,
                profileId = profileId,
                sessionId = sessionId
            )
            addMessage(userMsg)
            saveMessage(userMsg)

            // 2. Mostra indicatore di caricamento
            val loadingMsg = ChatMessage.loadingMessage().copy(
                profileId = profileId,
                sessionId = sessionId
            )
            addMessage(loadingMsg)
            _isProcessing.value = true

            // 3. Processa la domanda
            val result = if (_gemmaAvailable.value) {
                chatHelper.processQuestion(text, profileId)
            } else {
                Result.success(chatHelper.processQuestionWithoutGemma(text, profileId))
            }

            // 4. Rimuovi indicatore di caricamento
            removeLoadingMessage()

            // 5. Aggiungi risposta
            result.fold(
                onSuccess = { response ->
                    val botMsg = ChatMessage(
                        text = response,
                        isFromUser = false,
                        intent = intent,
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(botMsg)
                    saveMessage(botMsg)
                },
                onFailure = { error ->
                    Log.e(TAG, "Error generating response", error)
                    val errorMsg = ChatMessage.errorMessage(
                        "Mi dispiace, c'e stato un errore: ${error.localizedMessage}"
                    ).copy(
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(errorMsg)
                }
            )

            _isProcessing.value = false

            // Aggiorna stato Gemma
            _gemmaReady.value = chatHelper.isGemmaReady()
        }
    }

    /**
     * Invia una domanda suggerita.
     */
    fun sendSuggestion(suggestion: String) {
        sendMessage(suggestion)
    }

    /**
     * Pulisce la cronologia chat.
     */
    fun clearHistory() {
        viewModelScope.launch {
            try {
                db.chatDao().clearAll()
                _messages.value = listOf(ChatMessage.welcomeMessage().copy(
                    profileId = profileId,
                    sessionId = sessionId
                ))
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing history", e)
            }
        }
    }

    /**
     * Retry dell'ultimo messaggio in caso di errore.
     */
    fun retryLastMessage() {
        val lastUserMessage = _messages.value
            .lastOrNull { it.isFromUser && !it.isLoading }
            ?.text

        if (lastUserMessage != null) {
            // Rimuovi l'ultimo messaggio di errore
            _messages.value = _messages.value.dropLastWhile { !it.isFromUser }
            sendMessage(lastUserMessage)
        }
    }

    // ==================== HELPERS ====================

    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }

    private fun removeLoadingMessage() {
        _messages.value = _messages.value.filter { !it.isLoading }
    }

    private suspend fun saveMessage(message: ChatMessage) {
        if (message.isLoading || message.isError) return

        try {
            db.chatDao().insert(message.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "Error saving message", e)
        }
    }

    // ==================== CLEANUP ====================

    override fun onCleared() {
        super.onCleared()
        // Pulizia se necessaria
    }
}
