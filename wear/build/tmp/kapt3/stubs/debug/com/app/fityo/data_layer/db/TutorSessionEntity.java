package com.app.fityo.data_layer.db;

/**
 * Entity Room per salvare le sessioni di analisi Tutor.
 * I video sono salvati localmente e qui si memorizzano i path e i risultati.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BW\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\n\u001a\u00020\u0005\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010 \u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0012J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0007H\u00c6\u0003J\t\u0010#\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\rH\u00c6\u0003J\t\u0010(\u001a\u00020\u0007H\u00c6\u0003Jl\u0010)\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010*J\u0013\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020\u0003H\u00d6\u0001J\t\u0010/\u001a\u00020\u0007H\u00d6\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0017\u00a8\u00060"}, d2 = {"Lcom/app/fityo/data_layer/db/TutorSessionEntity;", "", "id", "", "createdAt", "", "exerciseType", "", "videoPath", "thumbnailPath", "duration", "totalErrors", "overallScore", "", "errorsJson", "<init>", "(Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;JIFLjava/lang/String;)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getCreatedAt", "()J", "getExerciseType", "()Ljava/lang/String;", "getVideoPath", "getThumbnailPath", "getDuration", "getTotalErrors", "()I", "getOverallScore", "()F", "getErrorsJson", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;JIFLjava/lang/String;)Lcom/app/fityo/data_layer/db/TutorSessionEntity;", "equals", "", "other", "hashCode", "toString", "wear_debug"})
@androidx.room.Entity(tableName = "tutor_sessions")
public final class TutorSessionEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer id = null;
    
    /**
     * Timestamp di creazione della sessione
     */
    private final long createdAt = 0L;
    
    /**
     * Tipo di esercizio: "SQUAT", "LUNGES", "DEADLIFT"
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String exerciseType = null;
    
    /**
     * Path del video analizzato
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String videoPath = null;
    
    /**
     * Path della thumbnail (opzionale)
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailPath = null;
    
    /**
     * Durata del video in millisecondi
     */
    private final long duration = 0L;
    
    /**
     * Numero totale di errori rilevati
     */
    private final int totalErrors = 0;
    
    /**
     * Punteggio complessivo (0-100)
     */
    private final float overallScore = 0.0F;
    
    /**
     * JSON array degli errori rilevati
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String errorsJson = null;
    
    public TutorSessionEntity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String exerciseType, @org.jetbrains.annotations.NotNull()
    java.lang.String videoPath, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailPath, long duration, int totalErrors, float overallScore, @org.jetbrains.annotations.NotNull()
    java.lang.String errorsJson) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    /**
     * Timestamp di creazione della sessione
     */
    public final long getCreatedAt() {
        return 0L;
    }
    
    /**
     * Tipo di esercizio: "SQUAT", "LUNGES", "DEADLIFT"
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExerciseType() {
        return null;
    }
    
    /**
     * Path del video analizzato
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVideoPath() {
        return null;
    }
    
    /**
     * Path della thumbnail (opzionale)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getThumbnailPath() {
        return null;
    }
    
    /**
     * Durata del video in millisecondi
     */
    public final long getDuration() {
        return 0L;
    }
    
    /**
     * Numero totale di errori rilevati
     */
    public final int getTotalErrors() {
        return 0;
    }
    
    /**
     * Punteggio complessivo (0-100)
     */
    public final float getOverallScore() {
        return 0.0F;
    }
    
    /**
     * JSON array degli errori rilevati
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getErrorsJson() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    public final long component6() {
        return 0L;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final float component8() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.data_layer.db.TutorSessionEntity copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String exerciseType, @org.jetbrains.annotations.NotNull()
    java.lang.String videoPath, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailPath, long duration, int totalErrors, float overallScore, @org.jetbrains.annotations.NotNull()
    java.lang.String errorsJson) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}