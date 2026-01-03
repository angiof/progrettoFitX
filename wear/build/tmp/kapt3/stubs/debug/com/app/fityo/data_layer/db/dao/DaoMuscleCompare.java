package com.app.fityo.data_layer.db.dao;

/**
 * DAO per operazioni CRUD sui confronti muscolari.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00100\u000fH\'J\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0013J$\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00100\u000f2\u0006\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0003H\'J\u000e\u0010\u0018\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u0013\u00a8\u0006\u001e"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoMuscleCompare;", "", "insert", "", "compare", "Lcom/app/fityo/data_layer/db/MuscleCompareEntity;", "(Lcom/app/fityo/data_layer/db/MuscleCompareEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllCompares", "Lkotlinx/coroutines/flow/Flow;", "", "getById", "getLatest", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCount", "getComparesByDateRange", "startDate", "endDate", "deleteAll", "getAverageArmsVariation", "", "getAverageAbsVariation", "getAverageLegsVariation", "getAverageGlutesVariation", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoMuscleCompare {
    
    /**
     * Inserisce un nuovo confronto.
     * @return L'ID del confronto inserito
     */
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * Aggiorna un confronto esistente.
     */
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina un confronto.
     */
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina un confronto per ID.
     */
    @androidx.room.Query(value = "DELETE FROM muscle_compare WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene tutti i confronti ordinati per data (più recenti prima).
     */
    @androidx.room.Query(value = "SELECT * FROM muscle_compare ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.MuscleCompareEntity>> getAllCompares();
    
    /**
     * Ottiene un confronto per ID.
     */
    @androidx.room.Query(value = "SELECT * FROM muscle_compare WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.MuscleCompareEntity> $completion);
    
    /**
     * Ottiene l'ultimo confronto effettuato.
     */
    @androidx.room.Query(value = "SELECT * FROM muscle_compare ORDER BY createdAt DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatest(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.MuscleCompareEntity> $completion);
    
    /**
     * Conta il numero totale di confronti.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Ottiene confronti in un range di date.
     */
    @androidx.room.Query(value = "SELECT * FROM muscle_compare WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.MuscleCompareEntity>> getComparesByDateRange(long startDate, long endDate);
    
    /**
     * Elimina tutti i confronti (per pulizia completa).
     */
    @androidx.room.Query(value = "DELETE FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene la media delle variazioni per le braccia.
     */
    @androidx.room.Query(value = "SELECT AVG(armsVariation) FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageArmsVariation(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
    
    /**
     * Ottiene la media delle variazioni per gli addominali.
     */
    @androidx.room.Query(value = "SELECT AVG(absVariation) FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageAbsVariation(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
    
    /**
     * Ottiene la media delle variazioni per le gambe.
     */
    @androidx.room.Query(value = "SELECT AVG(legsVariation) FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageLegsVariation(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
    
    /**
     * Ottiene la media delle variazioni per i glutei.
     */
    @androidx.room.Query(value = "SELECT AVG(glutesVariation) FROM muscle_compare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageGlutesVariation(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
}