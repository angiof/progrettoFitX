package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.dominio.GruppoMuscolarePercentuale


import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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


    suspend fun getSchedeInDateRange(startMillis: Long, endMillis: Long): List<SchedeEntity> {
        // Converte i millisecondi in stringhe ISO-8601 compatibili con la tua colonna `data`
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        val startDate = Instant.ofEpochMilli(startMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(fmt)
        val endDate = Instant.ofEpochMilli(endMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(fmt)

        return daoSchede.getSchedeInDateRange(startDate, endDate)
    }


    suspend fun setFavorite(id: Int, isFav: Boolean) {
        daoSchede.setFavorite(id, isFav)
    }

    suspend fun setCompleted(id: Int, isCompleted: Boolean, completedDate: String?) {
        daoSchede.setCompleted(id, isCompleted, completedDate)
    }


    suspend fun countSchede(): Int = daoSchede.countSchede()

    suspend fun countFavoriteSchede(): Int = daoSchede.countFavoriteSchede()

    suspend fun countTotalExercises(): Int = daoSchede.countTotalExercises()

    suspend fun getLastWorkoutDate(): String? = daoSchede.getLastWorkoutDate()

    suspend fun getMediaIntensitaAll(): List<GruppoMuscolareIntensitaMedia> =
        daoSchede.getMediaIntensitaPerGruppoMuscolareAll()

    suspend fun getWorkoutCountByWeekdayAll() = daoSchede.getWorkoutCountByWeekdayAll()

    suspend fun getWorkoutCountByWeekday(startDate: String, endDate: String) =
        daoSchede.getWorkoutCountByWeekday(startDate, endDate)

    suspend fun getDaysSinceLastWorkout(): Int? = daoSchede.getDaysSinceLastWorkout()

    suspend fun getMostTrainedMuscleGroup(): String? = daoSchede.getMostTrainedMuscleGroup()

    suspend fun getAverageWorkoutsPerWeek(): Double? = daoSchede.getAverageWorkoutsPerWeek()

    // ==================== METODI PER COACH MODE ====================

    suspend fun getSchedeByCoachProfile(profileId: Int): List<SchedeEntity> =
        daoSchede.getSchedeByCoachProfile(profileId)

    suspend fun getPersonalSchede(): List<SchedeEntity> =
        daoSchede.getPersonalSchede()

    suspend fun countSchedeByCoachProfile(profileId: Int): Int =
        daoSchede.countSchedeByCoachProfile(profileId)

    suspend fun getPercentualeByCoachProfile(profileId: Int): List<GruppoMuscolarePercentuale> =
        daoSchede.getPercentualeByCoachProfile(profileId)

    suspend fun getMediaIntensitaByCoachProfile(profileId: Int): List<GruppoMuscolareIntensitaMedia> =
        daoSchede.getMediaIntensitaByCoachProfile(profileId)

    suspend fun getWorkoutCountByWeekdayForCoach(profileId: Int) =
        daoSchede.getWorkoutCountByWeekdayForCoach(profileId)

    suspend fun getLastWorkoutDateByCoach(profileId: Int): String? =
        daoSchede.getLastWorkoutDateByCoach(profileId)

    suspend fun getMostTrainedMuscleGroupByCoach(profileId: Int): String? =
        daoSchede.getMostTrainedMuscleGroupByCoach(profileId)

    suspend fun countFavoriteSchedeByCoach(profileId: Int): Int =
        daoSchede.countFavoriteSchedeByCoach(profileId)

    suspend fun updateProfileForSchede(schedeIds: List<Int>, profileId: Int?) =
        daoSchede.updateProfileForSchede(schedeIds, profileId)
}
