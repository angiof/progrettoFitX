package com.app.fityo.data_layer.db.dao

import androidx.room.*
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoCoachAppointment {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: CoachAppointmentEntity): Long

    @Update
    suspend fun update(appointment: CoachAppointmentEntity)

    @Delete
    suspend fun delete(appointment: CoachAppointmentEntity)

    @Query("DELETE FROM coach_appointments WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM coach_appointments WHERE id = :id")
    suspend fun getById(id: Int): CoachAppointmentEntity?

    @Query("SELECT * FROM coach_appointments WHERE profileId = :profileId ORDER BY date ASC, time ASC")
    fun getAppointmentsByProfile(profileId: Int): Flow<List<CoachAppointmentEntity>>

    @Query("SELECT * FROM coach_appointments WHERE profileId = :profileId ORDER BY date ASC, time ASC")
    suspend fun getAppointmentsByProfileSync(profileId: Int): List<CoachAppointmentEntity>

    @Query("SELECT * FROM coach_appointments WHERE profileId = :profileId AND date = :date ORDER BY time ASC")
    suspend fun getAppointmentsByProfileAndDate(profileId: Int, date: String): List<CoachAppointmentEntity>

    @Query("SELECT * FROM coach_appointments WHERE profileId = :profileId AND date >= :startDate AND date <= :endDate ORDER BY date ASC, time ASC")
    suspend fun getAppointmentsInRange(profileId: Int, startDate: String, endDate: String): List<CoachAppointmentEntity>

    @Query("SELECT * FROM coach_appointments WHERE profileId = :profileId AND date >= :today AND isCompleted = 0 ORDER BY date ASC, time ASC LIMIT :limit")
    suspend fun getUpcomingAppointments(profileId: Int, today: String, limit: Int = 5): List<CoachAppointmentEntity>

    @Query("SELECT DISTINCT date FROM coach_appointments WHERE profileId = :profileId AND date >= :startDate AND date <= :endDate")
    suspend fun getAppointmentDatesInRange(profileId: Int, startDate: String, endDate: String): List<String>

    @Query("UPDATE coach_appointments SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Int, completed: Boolean)

    @Query("SELECT COUNT(*) FROM coach_appointments WHERE profileId = :profileId")
    suspend fun countByProfile(profileId: Int): Int

    @Query("SELECT COUNT(*) FROM coach_appointments WHERE profileId = :profileId AND isCompleted = 0 AND date >= :today")
    suspend fun countUpcomingByProfile(profileId: Int, today: String): Int

    @Query("DELETE FROM coach_appointments WHERE profileId = :profileId")
    suspend fun deleteAllByProfile(profileId: Int)

    // ==================== QUERY GLOBALI (TUTTI I PROFILI) ====================

    /**
     * Ottiene tutti gli appuntamenti di tutti i profili ordinati per data
     */
    @Query("SELECT * FROM coach_appointments ORDER BY date ASC, time ASC")
    suspend fun getAllAppointments(): List<CoachAppointmentEntity>

    /**
     * Ottiene tutti gli appuntamenti di oggi (per notifications)
     */
    @Query("SELECT * FROM coach_appointments WHERE date = :today ORDER BY time ASC")
    suspend fun getTodayAppointments(today: String): List<CoachAppointmentEntity>

    /**
     * Flow per osservare gli appuntamenti di oggi
     */
    @Query("SELECT * FROM coach_appointments WHERE date = :today ORDER BY time ASC")
    fun observeTodayAppointments(today: String): Flow<List<CoachAppointmentEntity>>

    /**
     * Ottiene le date con appuntamenti in un range (tutti i profili)
     */
    @Query("SELECT DISTINCT date FROM coach_appointments WHERE date >= :startDate AND date <= :endDate")
    suspend fun getAllAppointmentDatesInRange(startDate: String, endDate: String): List<String>

    /**
     * Ottiene tutti gli appuntamenti in un range di date (tutti i profili)
     */
    @Query("SELECT * FROM coach_appointments WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, time ASC")
    suspend fun getAllAppointmentsInRange(startDate: String, endDate: String): List<CoachAppointmentEntity>

    /**
     * Conta appuntamenti non completati di oggi
     */
    @Query("SELECT COUNT(*) FROM coach_appointments WHERE date = :today AND isCompleted = 0")
    suspend fun countTodayPendingAppointments(today: String): Int
}
