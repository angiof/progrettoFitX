package com.app.fityo.wear.sync

import android.app.Application
import android.util.Log
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Gestisce la sincronizzazione bidirezionale dei dati tra Wear e Phone
 * Include polling periodico e sync on-demand
 */
class WearDataSyncManager(private val application: Application) : WearableListenerService() {

    private val messageClient: MessageClient by lazy { Wearable.getMessageClient(application) }
    private val capabilityClient: CapabilityClient by lazy { Wearable.getCapabilityClient(application) }
    private val db by lazy { DbFit.getDatabase(application) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var pollingJob: Job? = null

    companion object {
        const val TAG = "WearDataSync"
        const val REQUEST_SYNC_PATH = "/fityo/request_sync"
        const val RESPONSE_SYNC_PATH = "/fityo/sync_data"
        const val CAPABILITY_SYNC = "fityo_sync"
        const val POLLING_INTERVAL_MS = 30_000L // 30 secondi
    }

    init {
        capabilityClient.addLocalCapability(CAPABILITY_SYNC)
    }

    /**
     * Avvia polling periodico per sincronizzazione automatica
     */
    fun startPeriodicSync() {
        stopPeriodicSync()
        pollingJob = scope.launch {
            while (isActive) {
                try {
                    requestFullSync()
                    delay(POLLING_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in periodic sync", e)
                    delay(POLLING_INTERVAL_MS)
                }
            }
        }
        Log.d(TAG, "Periodic sync started")
    }

    /**
     * Ferma polling periodico
     */
    fun stopPeriodicSync() {
        pollingJob?.cancel()
        pollingJob = null
        Log.d(TAG, "Periodic sync stopped")
    }

    /**
     * Richiede sincronizzazione completa dal telefono
     */
    suspend fun requestFullSync() {
        val nodes = getConnectedNodes()
        if (nodes.isEmpty()) {
            Log.w(TAG, "No connected nodes found")
            return
        }

        val requestPayload = JSONObject()
            .put("type", "request_sync")
            .put("timestamp", System.currentTimeMillis())
            .toString()
            .toByteArray(Charset.defaultCharset())

        nodes.forEach { node ->
            try {
                messageClient.sendMessage(node.id, REQUEST_SYNC_PATH, requestPayload).await()
                Log.d(TAG, "Sync request sent to ${node.displayName}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send sync request to ${node.displayName}", e)
            }
        }
    }

    /**
     * Riceve dati sincronizzati dal telefono
     */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            RESPONSE_SYNC_PATH -> {
                scope.launch {
                    try {
                        val payload = messageEvent.data?.toString(Charset.defaultCharset()) ?: return@launch
                        processSyncData(payload)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing sync data", e)
                    }
                }
            }
        }
    }

    /**
     * Processa i dati ricevuti e aggiorna il database locale
     */
    private suspend fun processSyncData(payload: String) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(payload)
            val schedeArray = json.optJSONArray("schede") ?: return@withContext
            val eserciziArray = json.optJSONArray("esercizi") ?: return@withContext

            Log.d(TAG, "Received ${schedeArray.length()} schede and ${eserciziArray.length()} esercizi")

            // Cancella tutti i dati esistenti per evitare duplicati
            db.schedeDao().getAllSchede().forEach { scheda ->
                scheda.id?.let { id ->
                    db.essercissiDao().getEssercissiById(id).forEach { esercizio ->
                        db.essercissiDao().delete(esercizio)
                    }
                    db.schedeDao().delete(scheda)
                }
            }

            // Inserisci schede
            val schedeMap = mutableMapOf<Int, Int>() // oldId -> newId
            for (i in 0 until schedeArray.length()) {
                val schedaJson = schedeArray.getJSONObject(i)
                val oldId = schedaJson.optInt("id")

                val scheda = SchedeEntity(
                    id = null, // Auto-generate new ID
                    gruppoMuscolare = schedaJson.optString("gruppoMuscolare", ""),
                    gruppiMuscolari = jsonArrayToList(schedaJson.optJSONArray("gruppiMuscolari")),
                    intesita = schedaJson.optString("intesita", ""),
                    titolo = schedaJson.optString("titolo", ""),
                    data = schedaJson.optString("data", ""),
                    notes = schedaJson.optString("notes").takeIf { it.isNotEmpty() },
                    ora = schedaJson.optString("ora").takeIf { it.isNotEmpty() },
                    favorite = schedaJson.optBoolean("favorite", false),
                    completed = schedaJson.optBoolean("completed", false),
                    completedDate = schedaJson.optString("completedDate").takeIf { it.isNotEmpty() },
                    totalSteps = if (schedaJson.has("totalSteps")) schedaJson.optInt("totalSteps") else null,
                    avgHeartRate = if (schedaJson.has("avgHeartRate")) schedaJson.optInt("avgHeartRate") else null,
                    maxHeartRate = if (schedaJson.has("maxHeartRate")) schedaJson.optInt("maxHeartRate") else null
                )

                val newId = db.schedeDao().insert(scheda).toInt()
                schedeMap[oldId] = newId
                Log.d(TAG, "Inserted scheda: ${scheda.titolo} (oldId=$oldId, newId=$newId)")
            }

            // Inserisci esercizi con ID scheda aggiornati
            for (i in 0 until eserciziArray.length()) {
                val esercizioJson = eserciziArray.getJSONObject(i)
                val oldSchedaId = esercizioJson.optInt("schedaId")
                val newSchedaId = schedeMap[oldSchedaId] ?: continue

                val esercizio = EsserciziEntity(
                    id = null,
                    schedaId = newSchedaId,
                    nome = esercizioJson.optString("nome", ""),
                    nSerie = esercizioJson.optInt("nSerie", 0),
                    nRipetizione = esercizioJson.optInt("nRipetizione", 0),
                    attrezzo = esercizioJson.optString("attrezzo", ""),
                    peso = if (esercizioJson.has("peso")) esercizioJson.optDouble("peso").toFloat() else null,
                    intervallo = if (esercizioJson.has("intervallo")) esercizioJson.optInt("intervallo") else null,
                    insometria = if (esercizioJson.has("insometria")) esercizioJson.optInt("insometria") else null,
                    completed = esercizioJson.optBoolean("completed", false)
                )

                db.essercissiDao().insert(esercizio)
            }

            Log.d(TAG, "Sync completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing sync data", e)
        }
    }

    private fun jsonArrayToList(jsonArray: JSONArray?): List<String>? {
        if (jsonArray == null) return null
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list.takeIf { it.isNotEmpty() }
    }

    private suspend fun getConnectedNodes(): Set<Node> {
        return try {
            capabilityClient.getCapability(CAPABILITY_SYNC, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get connected nodes", e)
            emptySet()
        }
    }

    fun release() {
        stopPeriodicSync()
        scope.cancel()
    }
}
