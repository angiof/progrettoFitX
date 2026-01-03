package com.app.fityo.data_layer.db.dao;

/**
 * DAO per la gestione degli avatar 3D.
 * Supporta operazioni CRUD e query per storico.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00120\u00112\u0006\u0010\u000f\u001a\u00020\u000bH\'J\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00120\u0011H\'J\u0016\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u0015\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\u0016\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoAvatar3D;", "", "insert", "", "avatar", "Lcom/app/fityo/data_layer/db/Avatar3DEntity;", "(Lcom/app/fityo/data_layer/db/Avatar3DEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "getLatestByUserId", "userId", "getAvatarsByUserId", "Lkotlinx/coroutines/flow/Flow;", "", "getAllAvatars", "countByUserId", "deleteAllByUserId", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoAvatar3D {
    
    /**
     * Inserisce un nuovo avatar 3D.
     * @return ID dell'avatar inserito
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.Avatar3DEntity avatar, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * Elimina un avatar.
     */
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.Avatar3DEntity avatar, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina un avatar per ID.
     */
    @androidx.room.Query(value = "DELETE FROM avatar_3d WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene un avatar per ID.
     */
    @androidx.room.Query(value = "SELECT * FROM avatar_3d WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.Avatar3DEntity> $completion);
    
    /**
     * Ottiene l'ultimo avatar creato per un utente.
     */
    @androidx.room.Query(value = "SELECT * FROM avatar_3d WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestByUserId(int userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.Avatar3DEntity> $completion);
    
    /**
     * Ottiene tutti gli avatar di un utente ordinati per data (più recente prima).
     */
    @androidx.room.Query(value = "SELECT * FROM avatar_3d WHERE userId = :userId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.Avatar3DEntity>> getAvatarsByUserId(int userId);
    
    /**
     * Ottiene tutti gli avatar ordinati per data.
     */
    @androidx.room.Query(value = "SELECT * FROM avatar_3d ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.Avatar3DEntity>> getAllAvatars();
    
    /**
     * Conta gli avatar per un utente.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM avatar_3d WHERE userId = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countByUserId(int userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Elimina tutti gli avatar di un utente.
     */
    @androidx.room.Query(value = "DELETE FROM avatar_3d WHERE userId = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllByUserId(int userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina tutti gli avatar.
     */
    @androidx.room.Query(value = "DELETE FROM avatar_3d")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}