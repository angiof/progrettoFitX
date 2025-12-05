package com.app.progrettofitx.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.app.progrettofitx.data_layer.db.DB.DbFit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationsDao = DbFit.getDatabase(application).notificationsDao()
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
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val date: String
)
