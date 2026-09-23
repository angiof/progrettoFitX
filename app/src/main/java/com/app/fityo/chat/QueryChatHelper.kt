package com.app.fityo.chat

import android.content.Context
import android.util.Log
import com.app.fityo.chat.data.PredefinedQuestion
import com.app.fityo.chat.data.PredefinedQueries
import com.app.fityo.chat.engine.QueryExecutor
import com.app.fityo.data_layer.db.DB.DbFit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper per le conversazioni del chatbot fitness.
 *
 * ARCHITETTURA:
 * 1. L'utente seleziona SOLO domande predefinite (niente input libero)
 * 2. QueryExecutor esegue query dirette sul DB
 * 3. Nessun modello linguistico: le frasi motivazionali sono una lista fissa
 *
 * Cosi' ogni risposta e' sempre un dato reale letto dal database.
 */
class QueryChatHelper(
    private val context: Context,
    private val db: DbFit
) {
    private val queryExecutor = QueryExecutor(db)

    companion object {
        private const val TAG = "QueryChatHelper"

        // Frasi motivazionali predefinite (niente generazione AI)
        private val MOTIVATIONAL_PHRASES = listOf(
            "Continua cosi!",
            "Ottimo lavoro!",
            "Grande impegno!",
            "Forza e costanza!",
            "Sempre al top!",
            "Vai forte!",
            "Che grinta!",
            "Bravo/a!",
            "Stai andando alla grande!",
            "Non mollare!"
        )

        private val ENCOURAGEMENT_EMOJIS = listOf(
            "💪", "🔥", "⭐", "🏆", "👊", "✨", "🎯", "💯"
        )
    }

    /**
     * Processa una domanda predefinita e genera una risposta.
     *
     * @param question La domanda predefinita selezionata dall'utente
     * @param profileId ID del profilo (opzionale)
     * @return Risposta basata sui dati reali del DB + frase motivazionale opzionale
     */
    suspend fun processPredefinedQuestion(
        question: PredefinedQuestion,
        profileId: Int?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Processing predefined question: ${question.displayText}, type: ${question.queryType}")

            // 1. Esegui la query diretta sul DB
            val dataResponse = queryExecutor.execute(question, profileId)
            Log.d(TAG, "Query result: $dataResponse")

            // 2. Aggiungi frase motivazionale (semplice, predefinita)
            val finalResponse = addMotivationalPhrase(dataResponse)

            Result.success(finalResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing predefined question", e)
            Result.failure(e)
        }
    }

    /**
     * Processa una domanda testuale (per retrocompatibilita).
     * Cerca di mapparla a una domanda predefinita.
     */
    suspend fun processQuestion(
        questionText: String,
        profileId: Int?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Cerca la domanda predefinita corrispondente
            val predefinedQuestion = PredefinedQueries.findByDisplayText(questionText)

            if (predefinedQuestion != null) {
                return@withContext processPredefinedQuestion(predefinedQuestion, profileId)
            }

            // Fallback: cerca corrispondenza parziale
            val matchedQuestion = findClosestQuestion(questionText)
            if (matchedQuestion != null) {
                return@withContext processPredefinedQuestion(matchedQuestion, profileId)
            }

            // Se non trova corrispondenza, mostra messaggio di guida
            Result.success("Seleziona una domanda dalle categorie disponibili per ottenere informazioni precise sui tuoi allenamenti.")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing question", e)
            Result.failure(e)
        }
    }

    /**
     * Aggiorna la cache dei profili nel QueryExecutor.
     */
    suspend fun refreshProfilesCache() {
        queryExecutor.refreshProfilesCache()
    }

    // ==================== PRIVATE METHODS ====================

    /**
     * Cerca la domanda predefinita piu simile al testo inserito.
     */
    private fun findClosestQuestion(text: String): PredefinedQuestion? {
        val normalizedText = text.lowercase().trim()

        // Keywords per ogni tipo di domanda
        val keywordMap = mapOf(
            "gambe" to PredefinedQueries.allQuestions.find { it.displayText.contains("gambe", ignoreCase = true) && it.displayText.contains("Quando", ignoreCase = true) },
            "petto" to PredefinedQueries.allQuestions.find { it.displayText.contains("petto", ignoreCase = true) && it.displayText.contains("Quando", ignoreCase = true) },
            "schiena" to PredefinedQueries.allQuestions.find { it.displayText.contains("schiena", ignoreCase = true) },
            "spalle" to PredefinedQueries.allQuestions.find { it.displayText.contains("spalle", ignoreCase = true) },
            "braccia" to PredefinedQueries.allQuestions.find { it.displayText.contains("braccia", ignoreCase = true) },
            "panca" to PredefinedQueries.allQuestions.find { it.displayText.contains("panca", ignoreCase = true) },
            "squat" to PredefinedQueries.allQuestions.find { it.displayText.contains("squat", ignoreCase = true) },
            "stacco" to PredefinedQueries.allQuestions.find { it.displayText.contains("stacco", ignoreCase = true) },
            "ultimo" to PredefinedQueries.allQuestions.find { it.displayText == "Ultimo allenamento?" },
            "settimana" to PredefinedQueries.allQuestions.find { it.displayText.contains("settimana", ignoreCase = true) },
            "mese" to PredefinedQueries.allQuestions.find { it.displayText.contains("mese", ignoreCase = true) },
            "suggerisci" to PredefinedQueries.allQuestions.find { it.displayText.contains("Suggeriscimi", ignoreCase = true) },
            "oggi" to PredefinedQueries.allQuestions.find { it.displayText.contains("oggi", ignoreCase = true) },
            "profili" to PredefinedQueries.allQuestions.find { it.displayText.contains("profili", ignoreCase = true) },
            "attrezzi" to PredefinedQueries.allQuestions.find { it.displayText.contains("Attrezzi", ignoreCase = true) },
            "confronta" to PredefinedQueries.allQuestions.find { it.displayText.contains("Confronta", ignoreCase = true) },
            "media" to PredefinedQueries.allQuestions.find { it.displayText.contains("Media", ignoreCase = true) },
            "muscolo" to PredefinedQueries.allQuestions.find { it.displayText.contains("Muscolo", ignoreCase = true) }
        )

        for ((keyword, question) in keywordMap) {
            if (normalizedText.contains(keyword) && question != null) {
                return question
            }
        }

        return null
    }

    /**
     * Aggiunge una frase motivazionale alla risposta, presa da una lista fissa.
     *
     * NON aggiunge la frase quando la risposta segnala assenza di dati:
     * incoraggiare l'utente su "0 allenamenti" sembra fuori luogo.
     */
    private fun addMotivationalPhrase(response: String): String {
        if (isEmptyDataResponse(response)) return response

        // 40% di probabilita di aggiungere frase motivazionale
        if (Math.random() > 0.4) {
            return response
        }

        val emoji = ENCOURAGEMENT_EMOJIS.random()
        val phrase = MOTIVATIONAL_PHRASES.random()

        return "$response\n\n$emoji $phrase"
    }

    /**
     * Riconosce le risposte che indicano assenza di dati o dati insufficienti.
     * Serve a evitare frasi come "0 allenamenti - Continua cosi!".
     */
    private fun isEmptyDataResponse(response: String): Boolean {
        val lower = response.lowercase()
        val emptyMarkers = listOf(
            "non ci sono ancora",
            "non ho trovato",
            "non ho dati",
            "non ho abbastanza dati",
            "nessun dato",
            "nessun allenamento",
            "nessun appuntamento",
            "nessuna",
            "seleziona un profilo",
            "servono almeno",
            "inizia a tracciare",
            "inizia oggi",
            "inizia con calma"
        )
        return emptyMarkers.any { lower.contains(it) }
    }
}
