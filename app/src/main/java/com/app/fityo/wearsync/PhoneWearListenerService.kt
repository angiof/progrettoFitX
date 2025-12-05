package com.app.fityo.wearsync

import android.net.Uri
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import java.nio.charset.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PhoneWearListenerService : WearableListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path
        val payload = messageEvent.data?.toString(Charset.defaultCharset()) ?: return

        if (path == SYNC_PATH) {
            scope.launch {
                handleSync(payload)
            }
        } else {
            super.onMessageReceived(messageEvent)
        }
    }

    private suspend fun handleSync(payload: String) {
        val json = JSONObject(payload)
        val type = json.optString("type")
        val db = DbFit.getDatabase(applicationContext)
        when (type) {
            "exercise" -> {
                val id = json.optInt("id", -1)
                if (id != -1) {
                    val completed = json.optBoolean("completed", false)
                    db.essercissiDao().getEssercissiById(id).firstOrNull()?.let { current ->
                        db.essercissiDao().update(current.copy(completed = completed))
                    }
                }
            }
            "scheda" -> {
                val id = json.optInt("id", -1)
                val completed = json.optBoolean("completed", false)
                val completedDate = json.optString("completedDate", null)
                val totalSteps = if (json.has("totalSteps")) json.optInt("totalSteps") else null
                val avgHeartRate = if (json.has("avgHeartRate")) json.optInt("avgHeartRate") else null
                val maxHeartRate = if (json.has("maxHeartRate")) json.optInt("maxHeartRate") else null

                if (id != -1) {
                    // Aggiorna scheda con dati completi
                    db.schedeDao().getSchedeById(id)?.let { scheda ->
                        val updated = scheda.copy(
                            completed = completed,
                            completedDate = completedDate,
                            totalSteps = totalSteps,
                            avgHeartRate = avgHeartRate,
                            maxHeartRate = maxHeartRate
                        )
                        db.schedeDao().update(updated)
                    }
                }
            }
        }
    }

    companion object {
        const val SYNC_PATH = "/fityo/sync"
    }
}
