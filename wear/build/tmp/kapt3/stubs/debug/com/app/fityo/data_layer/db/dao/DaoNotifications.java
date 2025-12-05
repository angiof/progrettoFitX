package com.app.fityo.data_layer.db.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\f0\u000bH\'J\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\f0\u000bH\'J\u0016\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000bH\'\u00a8\u0006\u0016"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoNotifications;", "", "insert", "", "notification", "Lcom/app/fityo/data_layer/db/NotificationEntity;", "(Lcom/app/fityo/data_layer/db/NotificationEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "getAllNotifications", "Landroidx/lifecycle/LiveData;", "", "getUnreadNotifications", "markAsRead", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOlderThan", "timestamp", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUnreadCount", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoNotifications {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.NotificationEntity notification, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.NotificationEntity notification, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.NotificationEntity notification, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM notifications ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.app.fityo.data_layer.db.NotificationEntity>> getAllNotifications();
    
    @androidx.room.Query(value = "SELECT * FROM notifications WHERE read = 0 ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.app.fityo.data_layer.db.NotificationEntity>> getUnreadNotifications();
    
    @androidx.room.Query(value = "UPDATE notifications SET read = 1 WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markAsRead(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM notifications WHERE timestamp < :timestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOlderThan(long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications WHERE read = 0")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Integer> getUnreadCount();
}