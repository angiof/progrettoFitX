package com.app.fityo.data_layer.db.dao;

/**
 * DAO per operazioni CRUD sulle sessioni Tutor.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00100\u000fH\'J\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u001c\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00100\u000f2\u0006\u0010\u0016\u001a\u00020\u0017H\'J$\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00100\u000f2\u0006\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u0003H\'J\u000e\u0010\u001b\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0018\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u001dH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010 \u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u001e\u00a8\u0006!"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoTutorSession;", "", "insert", "", "session", "Lcom/app/fityo/data_layer/db/TutorSessionEntity;", "(Lcom/app/fityo/data_layer/db/TutorSessionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllSessions", "Lkotlinx/coroutines/flow/Flow;", "", "getById", "getLatest", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCount", "getSessionsByExerciseType", "exerciseType", "", "getSessionsByDateRange", "startDate", "endDate", "deleteAll", "getAverageScoreByExercise", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOverallAverageScore", "getCountByExercise", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoTutorSession {
    
    /**
     * Inserisce una nuova sessione.
     * @return L'ID della sessione inserita
     */
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.TutorSessionEntity session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * Aggiorna una sessione esistente.
     */
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.TutorSessionEntity session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina una sessione.
     */
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.TutorSessionEntity session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Elimina una sessione per ID.
     */
    @androidx.room.Query(value = "DELETE FROM tutor_sessions WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene tutte le sessioni ordinate per data (più recenti prima).
     */
    @androidx.room.Query(value = "SELECT * FROM tutor_sessions ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.TutorSessionEntity>> getAllSessions();
    
    /**
     * Ottiene una sessione per ID.
     */
    @androidx.room.Query(value = "SELECT * FROM tutor_sessions WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.TutorSessionEntity> $completion);
    
    /**
     * Ottiene l'ultima sessione effettuata.
     */
    @androidx.room.Query(value = "SELECT * FROM tutor_sessions ORDER BY createdAt DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatest(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.TutorSessionEntity> $completion);
    
    /**
     * Conta il numero totale di sessioni.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM tutor_sessions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Ottiene sessioni filtrate per tipo di esercizio.
     */
    @androidx.room.Query(value = "SELECT * FROM tutor_sessions WHERE exerciseType = :exerciseType ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.TutorSessionEntity>> getSessionsByExerciseType(@org.jetbrains.annotations.NotNull()
    java.lang.String exerciseType);
    
    /**
     * Ottiene sessioni in un range di date.
     */
    @androidx.room.Query(value = "SELECT * FROM tutor_sessions WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.TutorSessionEntity>> getSessionsByDateRange(long startDate, long endDate);
    
    /**
     * Elimina tutte le sessioni (per pulizia completa).
     */
    @androidx.room.Query(value = "DELETE FROM tutor_sessions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Ottiene il punteggio medio per un tipo di esercizio.
     */
    @androidx.room.Query(value = "SELECT AVG(overallScore) FROM tutor_sessions WHERE exerciseType = :exerciseType")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageScoreByExercise(@org.jetbrains.annotations.NotNull()
    java.lang.String exerciseType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
    
    /**
     * Ottiene il punteggio medio complessivo.
     */
    @androidx.room.Query(value = "SELECT AVG(overallScore) FROM tutor_sessions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getOverallAverageScore(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion);
    
    /**
     * Ottiene il conteggio delle sessioni per tipo di esercizio.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM tutor_sessions WHERE exerciseType = :exerciseType")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCountByExercise(@org.jetbrains.annotations.NotNull()
    java.lang.String exerciseType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}