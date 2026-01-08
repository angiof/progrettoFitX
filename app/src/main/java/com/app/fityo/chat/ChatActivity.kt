package com.app.fityo.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.chat.compose.ChatScreen
import com.app.fityo.data_layer.db.DB.DbFit

/**
 * Activity per la chat AI fitness.
 * Permette all'utente di fare domande sui propri allenamenti.
 */
class ChatActivity : ComponentActivity() {

    private lateinit var viewModel: ChatViewModel

    companion object {
        const val EXTRA_PROFILE_ID = "profile_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Recupera il profileId se passato (per filtrare dati coach)
        val profileId = intent.getIntExtra(EXTRA_PROFILE_ID, -1).takeIf { it >= 0 }

        // Inizializza database e ViewModel
        val db = DbFit.getDatabase(this)
        val factory = ChatViewModelFactory(application, db, profileId)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        setContent {
            ChatScreen(
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}
