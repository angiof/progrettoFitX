package com.app.fityo.data_layer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.fityo.data_layer.db.NotificationEntity

@Dao
interface DaoNotifications {

    @Insert
    suspend fun insert(notification: NotificationEntity): Long

    @Update
    suspend fun update(notification: NotificationEntity)

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): LiveData<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE read = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifications(): LiveData<List<NotificationEntity>>

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM notifications WHERE read = 0")
    fun getUnreadCount(): LiveData<Int>
}

