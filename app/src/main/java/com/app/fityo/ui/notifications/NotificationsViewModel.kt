package com.app.fityo.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.DB.DbFit
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationsDao = DbFit.getDatabase(application).notificationsDao()
    private val appointmentDao = DbFit.getDatabase(application).coachAppointmentDao()
    private val coachProfileDao = DbFit.getDatabase(application).coachProfileDao()
    private val formatter = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())

    val notifications: LiveData<List<NotificationItem>> =
        notificationsDao.getAllNotifications().map { entities ->
            entities.map { entity ->
                NotificationItem(
                    id = entity.id ?: 0,
                    title = entity.title,
                    date = formatter.format(Date(entity.timestamp))
                )
            }
        }

    val unreadCount: LiveData<Int> = notificationsDao.getUnreadCount()

    // Appuntamenti di oggi
    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val todayAppointments: LiveData<List<TodayAppointmentItem>> =
        appointmentDao.observeTodayAppointments(today).map { appointments ->
            appointments.map { appointment ->
                val profileName = coachProfileDao.getById(appointment.profileId)?.name
                TodayAppointmentItem(
                    id = appointment.id ?: 0,
                    title = appointment.title,
                    time = appointment.time,
                    profileName = profileName,
                    profileId = appointment.profileId,
                    isCompleted = appointment.isCompleted,
                    notes = appointment.notes
                )
            }
        }.asLiveData()
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val date: String
)

data class TodayAppointmentItem(
    val id: Int,
    val title: String,
    val time: String?,
    val profileName: String?,
    val profileId: Int,
    val isCompleted: Boolean,
    val notes: String?
)
