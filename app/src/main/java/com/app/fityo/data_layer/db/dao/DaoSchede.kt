package com.app.fityo.data_layer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.WeekdayWorkoutCount

@Dao
interface DaoSchede {

    @Insert
    suspend fun insert(schede: SchedeEntity): Long

    @Update
    suspend fun update(scheda: SchedeEntity)

    @Delete
    suspend fun delete(scheda: SchedeEntity)

    @Query("SELECT * FROM schede")
    suspend fun getAllSchede(): List<SchedeEntity>

    @Query("SELECT * FROM schede WHERE id = :id")
    suspend fun getSchedeById(id: Int): SchedeEntity?

    @Query("SELECT * FROM schede WHERE data = :date")
    suspend fun getSchedeByDate(date: String): List<SchedeEntity>

    @Query("SELECT * FROM schede WHERE gruppoMuscolare = :gruppoMuscolare")
    suspend fun getSchedeByGruppoMuscolare(gruppoMuscolare: String): List<SchedeEntity>

    // Aggiungere altre query come necessario

    @Query("UPDATE schede SET ora = :time WHERE id = :id")
    suspend fun updateTime(id: Int, time: String)


    @Query("SELECT * FROM schede WHERE ora IS NOT NULL")
    suspend fun getSchedeWithTime(): List<SchedeEntity>


    @Query("""
        SELECT gruppoMuscolare, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM schede) as percentuale 
        FROM schede 
        GROUP BY gruppoMuscolare
    """)
    suspend fun getPercentualePerGruppoMuscolare(): List<GruppoMuscolarePercentuale>



    @Query("""
    SELECT gruppoMuscolare, COUNT(*) * 100.0 / (
        SELECT COUNT(*) 
        FROM schede 
        WHERE data BETWEEN :startDate AND :endDate
    ) as percentuale 
    FROM schede 
    WHERE data BETWEEN :startDate AND :endDate
    GROUP BY gruppoMuscolare
""")
    suspend fun getPercentualePerGruppoMuscolareInDateRange(
        startDate: String,
        endDate: String
    ): List<GruppoMuscolarePercentuale>


    @Query("""
    SELECT gruppoMuscolare, AVG(
        CASE intesita
            WHEN 'Bassa' THEN 5
            WHEN 'Media' THEN 10
            WHEN 'Alta' THEN 15
            ELSE 0 
        END
    ) AS mediaIntensita
    FROM schede
    WHERE data BETWEEN :startDate AND :endDate
    GROUP BY gruppoMuscolare
""")
    suspend fun getMediaIntensitaPerGruppoMuscolareDateRange(
        startDate: String,
        endDate: String
    ): List<GruppoMuscolareIntensitaMedia>



    @Query("""
    SELECT * FROM schede
    WHERE date(data) BETWEEN date(:start) AND date(:end)
    ORDER BY date(data) DESC""")
    suspend fun getSchedeInDateRange(start: String, end: String): List<SchedeEntity>



    @Query("UPDATE schede SET favorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Int, isFav: Boolean)

    @Query("UPDATE schede SET completed = :isCompleted, completedDate = :completedDate WHERE id = :id")
    suspend fun setCompleted(id: Int, isCompleted: Boolean, completedDate: String?)


    @Query("SELECT COUNT(*) FROM schede")
    suspend fun countSchede(): Int

    @Query("SELECT COUNT(*) FROM schede WHERE favorite = 1")
    suspend fun countFavoriteSchede(): Int

    @Query("SELECT COUNT(*) FROM essercissi")
    suspend fun countTotalExercises(): Int

    @Query("SELECT data FROM schede ORDER BY date(data) DESC LIMIT 1")
    suspend fun getLastWorkoutDate(): String?

    @Query("""
        SELECT gruppoMuscolare, AVG(
            CASE intesita
                WHEN 'Bassa' THEN 5
                WHEN 'Media' THEN 10
                WHEN 'Alta' THEN 15
                ELSE 0 
            END
        ) AS mediaIntensita
        FROM schede
        GROUP BY gruppoMuscolare
    """)
    suspend fun getMediaIntensitaPerGruppoMuscolareAll(): List<GruppoMuscolareIntensitaMedia>

    @Query("""
        SELECT strftime('%w', data) AS dayOfWeek, COUNT(*) AS count
        FROM schede
        GROUP BY dayOfWeek
    """)
    suspend fun getWorkoutCountByWeekdayAll(): List<WeekdayWorkoutCount>

    @Query("""
        SELECT strftime('%w', data) AS dayOfWeek, COUNT(*) AS count
        FROM schede
        WHERE date(data) BETWEEN date(:startDate) AND date(:endDate)
        GROUP BY dayOfWeek
    """)
    suspend fun getWorkoutCountByWeekday(
        startDate: String,
        endDate: String
    ): List<WeekdayWorkoutCount>

    @Query("""
        SELECT CAST(JULIANDAY('now') - JULIANDAY(MAX(data)) AS INTEGER) as daysSinceLastWorkout
        FROM schede
    """)
    suspend fun getDaysSinceLastWorkout(): Int?

    @Query("""
        SELECT gruppoMuscolare
        FROM schede
        GROUP BY gruppoMuscolare
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getMostTrainedMuscleGroup(): String?

    @Query("""
        SELECT COUNT(*) * 1.0 /
        (SELECT (JULIANDAY(MAX(data)) - JULIANDAY(MIN(data))) / 7.0 FROM schede)
        as avgPerWeek
        FROM schede
        WHERE (SELECT COUNT(*) FROM schede) > 1
    """)
    suspend fun getAverageWorkoutsPerWeek(): Double?

    // ==================== QUERY PER COACH MODE ====================

    /**
     * Ottiene tutte le schede di un profilo coach specifico.
     */
    @Query("SELECT * FROM schede WHERE coachProfileId = :profileId ORDER BY date(data) DESC")
    suspend fun getSchedeByCoachProfile(profileId: Int): List<SchedeEntity>

    /**
     * Ottiene le schede personali (senza coach profile).
     */
    @Query("SELECT * FROM schede WHERE coachProfileId IS NULL ORDER BY date(data) DESC")
    suspend fun getPersonalSchede(): List<SchedeEntity>

    /**
     * Conta le schede di un profilo coach.
     */
    @Query("SELECT COUNT(*) FROM schede WHERE coachProfileId = :profileId")
    suspend fun countSchedeByCoachProfile(profileId: Int): Int

    /**
     * Percentuale gruppi muscolari per coach profile.
     */
    @Query("""
        SELECT gruppoMuscolare, COUNT(*) * 100.0 / (
            SELECT COUNT(*) FROM schede WHERE coachProfileId = :profileId
        ) as percentuale
        FROM schede
        WHERE coachProfileId = :profileId
        GROUP BY gruppoMuscolare
    """)
    suspend fun getPercentualeByCoachProfile(profileId: Int): List<GruppoMuscolarePercentuale>

    /**
     * Media intensita per gruppo muscolare per coach profile.
     */
    @Query("""
        SELECT gruppoMuscolare, AVG(
            CASE intesita
                WHEN 'Bassa' THEN 5
                WHEN 'Media' THEN 10
                WHEN 'Alta' THEN 15
                ELSE 0
            END
        ) AS mediaIntensita
        FROM schede
        WHERE coachProfileId = :profileId
        GROUP BY gruppoMuscolare
    """)
    suspend fun getMediaIntensitaByCoachProfile(profileId: Int): List<GruppoMuscolareIntensitaMedia>

    /**
     * Frequenza settimanale per coach profile.
     */
    @Query("""
        SELECT strftime('%w', data) AS dayOfWeek, COUNT(*) AS count
        FROM schede
        WHERE coachProfileId = :profileId
        GROUP BY dayOfWeek
    """)
    suspend fun getWorkoutCountByWeekdayForCoach(profileId: Int): List<WeekdayWorkoutCount>

    /**
     * Ultimo allenamento per coach profile.
     */
    @Query("SELECT data FROM schede WHERE coachProfileId = :profileId ORDER BY date(data) DESC LIMIT 1")
    suspend fun getLastWorkoutDateByCoach(profileId: Int): String?

    /**
     * Gruppo muscolare piu allenato per coach profile.
     */
    @Query("""
        SELECT gruppoMuscolare
        FROM schede
        WHERE coachProfileId = :profileId
        GROUP BY gruppoMuscolare
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getMostTrainedMuscleGroupByCoach(profileId: Int): String?

    /**
     * Conta schede favorite per coach profile.
     */
    @Query("SELECT COUNT(*) FROM schede WHERE coachProfileId = :profileId AND favorite = 1")
    suspend fun countFavoriteSchedeByCoach(profileId: Int): Int

    /**
     * Aggiorna il profilo coach per piu schede contemporaneamente.
     * Usato per l'assegnazione batch.
     */
    @Query("UPDATE schede SET coachProfileId = :profileId WHERE id IN (:schedeIds)")
    suspend fun updateProfileForSchede(schedeIds: List<Int>, profileId: Int?)
}

