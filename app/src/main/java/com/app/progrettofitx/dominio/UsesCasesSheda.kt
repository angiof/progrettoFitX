package com.app.progrettofitx.dominio

import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.ui.shedeForms.SchedeRepository

class UsesCasesSheda(private val repository: SchedeRepository) {


    suspend fun insert(schede: SchedeEntity): Long {
        return repository.insert(schede)
    }

    suspend fun update(scheda: SchedeEntity) {
        repository.update(scheda)
    }

    // Elimina una scheda dal database
    suspend fun delete(scheda: SchedeEntity) {
        repository.delete(scheda)
    }

    // Ottiene tutte le schede dal database
    suspend fun getAllSchede(): List<SchedeEntity> {
        return repository.getAllSchede()
    }

    // Ottiene una scheda specifica per ID dal database
    suspend fun getSchedeById(id: Int): SchedeEntity? {
        return repository.getSchedeById(id)
    }

    // Ottiene tutte le schede di un determinato gruppo muscolare
    suspend fun getSchedeByGruppoMuscolare(gruppoMuscolare: String): List<SchedeEntity> {
        return repository.getSchedeByGruppoMuscolare(gruppoMuscolare)
    }

}