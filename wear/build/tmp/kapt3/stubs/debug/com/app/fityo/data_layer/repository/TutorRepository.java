package com.app.fityo.data_layer.repository;

/**
 * Repository per la gestione delle sessioni Tutor.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0012\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u0015J\u0014\u0010\u0018\u001a\u00020\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0016J\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00162\u0006\u0010\u001d\u001a\u00020\u0019J\u0018\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020!H\u0086@\u00a2\u0006\u0002\u0010\"J\u000e\u0010#\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010$R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/app/fityo/data_layer/repository/TutorRepository;", "", "dao", "Lcom/app/fityo/data_layer/db/dao/DaoTutorSession;", "<init>", "(Lcom/app/fityo/data_layer/db/dao/DaoTutorSession;)V", "gson", "Lcom/google/gson/Gson;", "insert", "", "session", "Lcom/app/fityo/data_layer/db/TutorSessionEntity;", "(Lcom/app/fityo/data_layer/db/TutorSessionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "getAllSessionsAsHistoryItems", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/app/fityo/dominio/TutorHistoryItem;", "errorsToJson", "", "errors", "Lcom/app/fityo/dominio/ExerciseError;", "jsonToErrors", "json", "getAverageScoreByExercise", "", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "(Lcom/app/fityo/dominio/ExerciseType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCount", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "wear_debug"})
public final class TutorRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.data_layer.db.dao.DaoTutorSession dao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    
    public TutorRepository(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.dao.DaoTutorSession dao) {
        super();
    }
    
    /**
     * Inserisce una nuova sessione.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.TutorSessionEntity session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Aggiorna una sessione esistente.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.TutorSessionEntity session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina una sessione per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Ottiene una sessione per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.TutorSessionEntity> $completion) {
        return null;
    }
    
    /**
     * Ottiene tutte le sessioni come HistoryItems.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.dominio.TutorHistoryItem>> getAllSessionsAsHistoryItems() {
        return null;
    }
    
    /**
     * Converte una lista di errori in JSON per il salvataggio.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String errorsToJson(@org.jetbrains.annotations.NotNull()
    java.util.List<com.app.fityo.dominio.ExerciseError> errors) {
        return null;
    }
    
    /**
     * Converte JSON in lista di errori.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.app.fityo.dominio.ExerciseError> jsonToErrors(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    /**
     * Ottiene il punteggio medio per un tipo di esercizio.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAverageScoreByExercise(@org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Float> $completion) {
        return null;
    }
    
    /**
     * Ottiene il numero di sessioni.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}