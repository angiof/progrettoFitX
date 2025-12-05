package com.app.fityo.wear.sync

import android.app.Application
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.nio.charset.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class WearSyncClient(application: Application) {

    private val app = application
    private val messageClient: MessageClient = Wearable.getMessageClient(application)
    private val capabilityClient: CapabilityClient = Wearable.getCapabilityClient(application)
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    init {
        capabilityClient.addLocalCapability(CAPABILITY_SYNC)
    }

    fun sendExerciseCompletion(id: Int, completed: Boolean) {
        val payload = JSONObject()
            .put("type", "exercise")
            .put("id", id)
            .put("completed", completed)
            .toString()
        sendToAllNodes(payload)
    }

    fun sendSchedaCompletion(
        id: Int,
        completed: Boolean,
        completedDate: String?,
        totalSteps: Int? = null,
        avgHeartRate: Int? = null,
        maxHeartRate: Int? = null
    ) {
        val payload = JSONObject()
            .put("type", "scheda")
            .put("id", id)
            .put("completed", completed)
            .put("completedDate", completedDate)
            .apply {
                totalSteps?.let { put("totalSteps", it) }
                avgHeartRate?.let { put("avgHeartRate", it) }
                maxHeartRate?.let { put("maxHeartRate", it) }
            }
            .toString()
        sendToAllNodes(payload)
    }

    private fun sendToAllNodes(payload: String) {
        scope.launch {
            val nodes = getConnectedNodes()
            val data = payload.toByteArray(Charset.defaultCharset())
            nodes.forEach { node ->
                try {
                    messageClient.sendMessage(node.id, SYNC_PATH, data)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to send to node ${node.displayName}", e)
                }
            }
        }
    }

    private suspend fun getConnectedNodes(): Set<Node> {
        return capabilityClient.getCapability(CAPABILITY_SYNC, CapabilityClient.FILTER_REACHABLE)
            .awaitSafe()
            .nodes
    }

    companion object {
        const val SYNC_PATH = "/fityo/sync"
        const val CAPABILITY_SYNC = "fityo_sync"
        const val TAG = "WearSyncClient"
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitSafe(): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> cont.resume(result) {} }
        addOnFailureListener { ex -> cont.resumeWith(Result.failure(ex)) }
        addOnCanceledListener { cont.cancel() }
    }
}
