package com.app.fityo.wearsync

import android.util.Log
import com.app.fityo.data_layer.db.DB.DbFit
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Servizio sul telefono che risponde alle richieste di sync dal Wear
 */
class PhoneSyncResponder : WearableListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val TAG = "PhoneSyncResponder"
        const val REQUEST_SYNC_PATH = "/fityo/request_sync"
        const val RESPONSE_SYNC_PATH = "/fityo/sync_data"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            REQUEST_SYNC_PATH -> {
                Log.d(TAG, "Sync request received from ${messageEvent.sourceNodeId}")
                scope.launch {
                    sendFullDatabaseToWear(messageEvent.sourceNodeId)
                }
            }
        }
    }

    private suspend fun sendFullDatabaseToWear(nodeId: String) {
        try {
            val db = DbFit.getDatabase(applicationContext)

            // Recupera tutte le schede
            val schede = db.schedeDao().getAllSchede()
            val schedeArray = JSONArray()

            schede.forEach { scheda ->
                val schedaJson = JSONObject().apply {
                    put("id", scheda.id)
                    put("gruppoMuscolare", scheda.gruppoMuscolare)
                    scheda.gruppiMuscolari?.let {
                        put("gruppiMuscolari", JSONArray(it))
                    }
                    put("intesita", scheda.intesita)
                    put("titolo", scheda.titolo)
                    put("data", scheda.data)
                    scheda.notes?.let { put("notes", it) }
                    scheda.ora?.let { put("ora", it) }
                    put("favorite", scheda.favorite)
                    put("completed", scheda.completed)
                    scheda.completedDate?.let { put("completedDate", it) }
                    scheda.totalSteps?.let { put("totalSteps", it) }
                    scheda.avgHeartRate?.let { put("avgHeartRate", it) }
                    scheda.maxHeartRate?.let { put("maxHeartRate", it) }
                }
                schedeArray.put(schedaJson)
            }

            // Recupera tutti gli esercizi
            val eserciziArray = JSONArray()
            schede.forEach { scheda ->
                scheda.id?.let { schedaId ->
                    val esercizi = db.essercissiDao().getEssercissiById(schedaId)
                    esercizi.forEach { esercizio ->
                        val esercizioJson = JSONObject().apply {
                            put("id", esercizio.id)
                            put("schedaId", esercizio.schedaId)
                            put("nome", esercizio.nome)
                            put("nSerie", esercizio.nSerie)
                            put("nRipetizione", esercizio.nRipetizione)
                            put("attrezzo", esercizio.attrezzo)
                            esercizio.peso?.let { put("peso", it) }
                            esercizio.intervallo?.let { put("intervallo", it) }
                            esercizio.insometria?.let { put("insometria", it) }
                            put("completed", esercizio.completed)
                        }
                        eserciziArray.put(esercizioJson)
                    }
                }
            }

            // Crea payload completo
            val payload = JSONObject().apply {
                put("schede", schedeArray)
                put("esercizi", eserciziArray)
                put("timestamp", System.currentTimeMillis())
            }

            val data = payload.toString().toByteArray(Charset.defaultCharset())

            // Invia dati al Wear
            val messageClient = Wearable.getMessageClient(applicationContext)
            messageClient.sendMessage(nodeId, RESPONSE_SYNC_PATH, data).await()

            Log.d(TAG, "Sent ${schedeArray.length()} schede and ${eserciziArray.length()} esercizi to Wear")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending data to Wear", e)
        }
    }
}
