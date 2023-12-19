package com.app.progrettofitx.ui.shedeForms

import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.data_layer.db.dao.DaoSchede
import com.app.progrettofitx.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale

class SchedeRepository(private val daoSchede: DaoSchede) {

    // Inserisce una nuova scheda nel database
    suspend fun insert(schede: SchedeEntity): Long {
        return daoSchede.insert(schede)
    }

    // Aggiorna una scheda esistente nel database
    suspend fun update(scheda: SchedeEntity) {
        daoSchede.update(scheda)
    }

    // Elimina una scheda dal database
    suspend fun delete(scheda: SchedeEntity) {
        daoSchede.delete(scheda)
    }

    // Ottiene tutte le schede dal database
    suspend fun getAllSchede(): List<SchedeEntity> {
        return daoSchede.getAllSchede()
    }

    // Ottiene una scheda specifica per ID dal database
    suspend fun getSchedeById(id: Int): SchedeEntity? {
        return daoSchede.getSchedeById(id)
    }

    // Ottiene tutte le schede di un determinato gruppo muscolare
    suspend fun getSchedeByGruppoMuscolare(gruppoMuscolare: String): List<SchedeEntity> {
        return daoSchede.getSchedeByGruppoMuscolare(gruppoMuscolare)
    }

    suspend fun updateTime(id: Int, time: String) = daoSchede.updateTime(id, time)

    suspend fun getSchedeWithTime(): List<SchedeEntity> {
        return daoSchede.getSchedeWithTime()
    }


    suspend fun getPercentualePerGruppoMuscolare(): List<GruppoMuscolarePercentuale> {
        return daoSchede.getPercentualePerGruppoMuscolare()
    }

    suspend fun getPercentualePerGruppoMuscolareInDateRange(startDate: String, endDate: String): List<GruppoMuscolarePercentuale> {
        return daoSchede.getPercentualePerGruppoMuscolareInDateRange(startDate, endDate)
    }

    suspend fun getMediaIntensitaPerGruppoMuscolareDateRange(startDate: String, endDate: String): List<GruppoMuscolareIntensitaMedia> {
        return daoSchede.getMediaIntensitaPerGruppoMuscolareDateRange(startDate, endDate)
    }

}
