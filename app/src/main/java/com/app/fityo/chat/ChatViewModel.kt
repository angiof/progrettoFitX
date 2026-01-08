package com.app.fityo.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.chat.data.ChatMessage
import com.app.fityo.chat.data.PredefinedQuestion
import com.app.fityo.data_layer.db.DB.DbFit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Profile state
    private val _profileName = MutableStateFlow<String?>(null)
    val profileName: StateFlow<String?> = _profileName.asStateFlow()

    val isCoachMode: Boolean get() = profileId != null

    // ==================== INIT ====================

    init {
        checkGemmaAvailability()
        loadProfileName()
        loadChatHistory()
    }

    private fun checkGemmaAvailability() {
        _gemmaAvailable.value = chatHelper.isGemmaAvailable()
        _gemmaReady.value = chatHelper.isGemmaReady()
        Log.d(TAG, "Gemma available: ${_gemmaAvailable.value}, ready: ${_gemmaReady.value}")
    }

    private fun loadProfileName() {
        if (profileId != null) {
            viewModelScope.launch {
                try {
                    val profile = db.coachProfileDao().getById(profileId)
                    _profileName.value = profile?.name
                    Log.d(TAG, "Loaded profile: ${profile?.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading profile name", e)
                }
            }
        }
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
     * Invia una domanda predefinita selezionata dall'utente.
     * Questo e il metodo principale per interagire con la chat.
     */
    fun sendPredefinedQuestion(question: PredefinedQuestion) {
        if (_isProcessing.value) return

        viewModelScope.launch {
            // 1. Aggiungi messaggio utente (mostra il testo della domanda)
            val userMsg = ChatMessage(
                text = question.displayText,
                isFromUser = true,
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

            // 3. Processa la domanda predefinita (query diretta al DB)
            val result = chatHelper.processPredefinedQuestion(question, profileId)

            // 4. Rimuovi indicatore di caricamento
            removeLoadingMessage()

            // 5. Aggiungi risposta
            result.fold(
                onSuccess = { response ->
                    val botMsg = ChatMessage(
                        text = response,
                        isFromUser = false,
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(botMsg)
                    saveMessage(botMsg)
                },
                onFailure = { error ->
                    Log.e(TAG, "Error generating response", error)
                    val errorMsg = ChatMessage.errorMessage(
                        "Mi dispiace, c'e stato un errore. Riprova!"
                    ).copy(
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(errorMsg)
                }
            )

            _isProcessing.value = false
        }
    }

    /**
     * Metodo legacy per retrocompatibilita.
     * Cerca di mappare il testo a una domanda predefinita.
     */
    fun sendMessage(text: String) {
        if (text.isBlank() || _isProcessing.value) return

        viewModelScope.launch {
            // 1. Aggiungi messaggio utente
            val userMsg = ChatMessage(
                text = text.trim(),
                isFromUser = true,
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

            // 3. Processa la domanda (cerca corrispondenza con domande predefinite)
            val result = chatHelper.processQuestion(text, profileId)

            // 4. Rimuovi indicatore di caricamento
            removeLoadingMessage()

            // 5. Aggiungi risposta
            result.fold(
                onSuccess = { response ->
                    val botMsg = ChatMessage(
                        text = response,
                        isFromUser = false,
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(botMsg)
                    saveMessage(botMsg)
                },
                onFailure = { error ->
                    Log.e(TAG, "Error generating response", error)
                    val errorMsg = ChatMessage.errorMessage(
                        "Mi dispiace, c'e stato un errore. Riprova!"
                    ).copy(
                        profileId = profileId,
                        sessionId = sessionId
                    )
                    addMessage(errorMsg)
                }
            )

            _isProcessing.value = false
        }
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
