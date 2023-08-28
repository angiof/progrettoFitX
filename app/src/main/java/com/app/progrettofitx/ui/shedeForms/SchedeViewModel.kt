package com.app.progrettofitx.ui.shedeForms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.db.DB.DbFit
import com.app.progrettofitx.db.SchedeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchedeRepository
    // Aggiungere altre variabili LiveData se necessario

    init {
        val daoSchede = DbFit.getDatabase(application).schedeDao()
        repository = SchedeRepository(daoSchede)
    }

    // Inserisce una nuova scheda nel database
    suspend fun insert(scheda: SchedeEntity): Long {
        return withContext(Dispatchers.IO) {
            repository.insert(scheda)
        }
    }

    // Aggiorna una scheda esistente nel database
    fun update(scheda: SchedeEntity) = viewModelScope.launch {
        repository.update(scheda)
    }

    // Elimina una scheda dal database
    fun delete(scheda: SchedeEntity) = viewModelScope.launch {
        repository.delete(scheda)
    }

    // Altre funzioni per interagire con il database
    // (per esempio, potresti voler aggiungere funzioni che restituiscano dati come LiveData)
}
