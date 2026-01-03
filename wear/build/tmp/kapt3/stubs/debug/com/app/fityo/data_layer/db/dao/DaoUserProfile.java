package com.app.fityo.data_layer.db.dao;

/**
 * DAO per la gestione del profilo utente.
 * Supporta operazioni CRUD complete.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0011H\'J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00140\u0011H\'J\u000e\u0010\u0015\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0018\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0019"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoUserProfile;", "", "insert", "", "profile", "Lcom/app/fityo/data_layer/db/UserProfileEntity;", "(Lcom/app/fityo/data_layer/db/UserProfileEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveProfileFlow", "Lkotlinx/coroutines/flow/Flow;", "getById", "getAllProfiles", "", "count", "hasProfile", "", "deleteAll", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoUserProfile {
    
    /**
     * Inserisce un nuovo profilo.
     * @return ID del profilo inserito
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.UserProfileEntity profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * Aggiorna un profilo esistente.
     */
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.UserProfileEntity profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina un profilo.
     */
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.UserProfileEntity profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina un profilo per ID.
     */
    @androidx.room.Query(value = "DELETE FROM user_profile WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene il profilo attivo (il più recente).
     * Assumiamo un solo profilo attivo alla volta.
     */
    @androidx.room.Query(value = "SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getActiveProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.UserProfileEntity> $completion);
    
    /**
     * Ottiene il profilo attivo come Flow per osservare i cambiamenti.
     */
    @androidx.room.Query(value = "SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.app.fityo.data_layer.db.UserProfileEntity> getActiveProfileFlow();
    
    /**
     * Ottiene un profilo per ID.
     */
    @androidx.room.Query(value = "SELECT * FROM user_profile WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.UserProfileEntity> $completion);
    
    /**
     * Ottiene tutti i profili (per supporto multi-profilo futuro).
     */
    @androidx.room.Query(value = "SELECT * FROM user_profile ORDER BY updatedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.UserProfileEntity>> getAllProfiles();
    
    /**
     * Conta i profili esistenti.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM user_profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Verifica se esiste almeno un profilo.
     */
    @androidx.room.Query(value = "SELECT EXISTS(SELECT 1 FROM user_profile LIMIT 1)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object hasProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    /**
     * Elimina tutti i profili.
     */
    @androidx.room.Query(value = "DELETE FROM user_profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}