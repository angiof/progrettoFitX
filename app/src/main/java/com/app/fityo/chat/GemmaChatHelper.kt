package com.app.fityo.chat

import android.content.Context
import android.util.Log
import com.app.fityo.chat.data.PredefinedQuestion
import com.app.fityo.chat.data.PredefinedQueries
import com.app.fityo.chat.engine.QueryExecutor
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.import_scheda.GemmaLlmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper per gestire le conversazioni del chatbot fitness.
 *
 * ARCHITETTURA SEMPLIFICATA:
 * 1. L'utente seleziona SOLO domande predefinite (niente input libero)
 * 2. QueryExecutor esegue query dirette sul DB
 * 3. Gemma NON viene usato per generare risposte (solo frasi motivazionali predefinite)
 *
 * Questo approccio garantisce che le risposte siano SEMPRE basate sui dati reali.
 */
class GemmaChatHelper(
    private val context: Context,
    private val db: DbFit
) {
    private val gemma = GemmaLlmHelper.getInstance(context)
    private val queryExecutor = QueryExecutor(db)

    companion object {
        private const val TAG = "GemmaChatHelper"

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
     * Genera una risposta senza Gemma (per retrocompatibilita).
     */
    suspend fun processQuestionWithoutGemma(
        questionText: String,
        profileId: Int?
    ): String = withContext(Dispatchers.IO) {
        val result = processQuestion(questionText, profileId)
        result.getOrElse { "Si e verificato un errore. Riprova!" }
    }

    /**
     * Verifica se Gemma e disponibile.
     */
    fun isGemmaAvailable(): Boolean = gemma.isModelAvailable()

    /**
     * Verifica se Gemma e pronto (gia caricato).
     */
    fun isGemmaReady(): Boolean = gemma.isReady()

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
     * Aggiunge una frase motivazionale alla risposta.
     * USA SOLO frasi predefinite, niente generazione AI.
     */
    private fun addMotivationalPhrase(response: String): String {
        // 40% di probabilita di aggiungere frase motivazionale
        if (Math.random() > 0.4) {
            return response
        }

        val emoji = ENCOURAGEMENT_EMOJIS.random()
        val phrase = MOTIVATIONAL_PHRASES.random()

        return "$response\n\n$emoji $phrase"
    }
}
