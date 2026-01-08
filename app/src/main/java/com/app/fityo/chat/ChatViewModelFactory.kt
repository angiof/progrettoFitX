package com.app.fityo.chat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.data_layer.db.DB.DbFit

/**
 * Factory per creare istanze di ChatViewModel con le dipendenze necessarie.
 */
class ChatViewModelFactory(
    private val application: Application,
    private val db: DbFit,
    private val profileId: Int?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(application, db, profileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
