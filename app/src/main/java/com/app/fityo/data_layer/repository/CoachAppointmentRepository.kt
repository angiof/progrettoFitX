package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.dao.DaoCoachAppointment
import kotlinx.coroutines.flow.Flow

class CoachAppointmentRepository(private val dao: DaoCoachAppointment) {

    suspend fun insert(appointment: CoachAppointmentEntity): Long = dao.insert(appointment)

    suspend fun update(appointment: CoachAppointmentEntity) = dao.update(appointment)

    suspend fun delete(appointment: CoachAppointmentEntity) = dao.delete(appointment)

    suspend fun deleteById(id: Int) = dao.deleteById(id)

    suspend fun getById(id: Int): CoachAppointmentEntity? = dao.getById(id)

    fun getAppointmentsByProfile(profileId: Int): Flow<List<CoachAppointmentEntity>> =
        dao.getAppointmentsByProfile(profileId)

    suspend fun getAppointmentsByProfileSync(profileId: Int): List<CoachAppointmentEntity> =
        dao.getAppointmentsByProfileSync(profileId)

    suspend fun getAppointmentsByProfileAndDate(profileId: Int, date: String): List<CoachAppointmentEntity> =
        dao.getAppointmentsByProfileAndDate(profileId, date)

    suspend fun getAppointmentsInRange(profileId: Int, startDate: String, endDate: String): List<CoachAppointmentEntity> =
        dao.getAppointmentsInRange(profileId, startDate, endDate)

    suspend fun getUpcomingAppointments(profileId: Int, today: String, limit: Int = 5): List<CoachAppointmentEntity> =
        dao.getUpcomingAppointments(profileId, today, limit)

    suspend fun getAppointmentDatesInRange(profileId: Int, startDate: String, endDate: String): List<String> =
        dao.getAppointmentDatesInRange(profileId, startDate, endDate)

    suspend fun setCompleted(id: Int, completed: Boolean) = dao.setCompleted(id, completed)

    suspend fun countByProfile(profileId: Int): Int = dao.countByProfile(profileId)

    suspend fun countUpcomingByProfile(profileId: Int, today: String): Int =
        dao.countUpcomingByProfile(profileId, today)

    suspend fun deleteAllByProfile(profileId: Int) = dao.deleteAllByProfile(profileId)
}
