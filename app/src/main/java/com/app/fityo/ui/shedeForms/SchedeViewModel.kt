package com.app.fityo.ui.shedeForms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.dominio.UsesCasesSheda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedeViewModel(application: Application, private val useCase: UsesCasesSheda) :
    AndroidViewModel(application) {

    // Aggiungere altre variabili LiveData se necessario

    // Inserisce una nuova scheda nel database
    suspend fun insert(scheda: SchedeEntity): Long {
        return withContext(Dispatchers.IO) {
            useCase.insert(scheda)
        }
    }

    // Aggiorna una scheda esistente nel database
    fun update(scheda: SchedeEntity) = viewModelScope.launch {
        useCase.update(scheda)
    }

    // Elimina una scheda dal database
    fun delete(scheda: SchedeEntity) = viewModelScope.launch {
        useCase.delete(scheda)
    }

    // Altre funzioni per interagire con il database
    // (per esempio, potresti voler aggiungere funzioni che restituiscano dati come LiveData)


    suspend fun updateTime(id: Int, time: String) = useCase.updateTime(id, time)

}

