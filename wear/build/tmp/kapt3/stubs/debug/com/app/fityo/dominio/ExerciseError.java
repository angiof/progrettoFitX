package com.app.fityo.dominio;

/**
 * Rappresenta un errore rilevato durante l'analisi di un esercizio.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0006\u0010\u000e\u001a\u00020\n\u00a2\u0006\u0004\b\u000f\u0010\u0010J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0006H\u00c6\u0003J\t\u0010 \u001a\u00020\bH\u00c6\u0003J\t\u0010!\u001a\u00020\nH\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u00c6\u0003J\t\u0010#\u001a\u00020\nH\u00c6\u0003JU\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\b\b\u0002\u0010\u000e\u001a\u00020\nH\u00c6\u0001J\u0013\u0010%\u001a\u00020&2\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020\rH\u00d6\u0001J\t\u0010)\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0019\u00a8\u0006*"}, d2 = {"Lcom/app/fityo/dominio/ExerciseError;", "", "timestampMs", "", "endTimestampMs", "errorType", "Lcom/app/fityo/dominio/ExerciseErrorType;", "severity", "Lcom/app/fityo/dominio/ErrorSeverity;", "message", "", "affectedLandmarks", "", "", "correctionHint", "<init>", "(JJLcom/app/fityo/dominio/ExerciseErrorType;Lcom/app/fityo/dominio/ErrorSeverity;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;)V", "getTimestampMs", "()J", "getEndTimestampMs", "getErrorType", "()Lcom/app/fityo/dominio/ExerciseErrorType;", "getSeverity", "()Lcom/app/fityo/dominio/ErrorSeverity;", "getMessage", "()Ljava/lang/String;", "getAffectedLandmarks", "()Ljava/util/List;", "getCorrectionHint", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "wear_debug"})
public final class ExerciseError {
    
    /**
     * Timestamp in millisecondi dall'inizio del video
     */
    private final long timestampMs = 0L;
    
    /**
     * Timestamp di fine dell'errore (se persistente)
     */
    private final long endTimestampMs = 0L;
    
    /**
     * Tipo di errore specifico
     */
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.dominio.ExerciseErrorType errorType = null;
    
    /**
     * Severità dell'errore
     */
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.dominio.ErrorSeverity severity = null;
    
    /**
     * Messaggio descrittivo dell'errore
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    
    /**
     * Indici dei landmark MediaPipe coinvolti
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.Integer> affectedLandmarks = null;
    
    /**
     * Suggerimento per correggere l'errore
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String correctionHint = null;
    
    public ExerciseError(long timestampMs, long endTimestampMs, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.ExerciseErrorType errorType, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.ErrorSeverity severity, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> affectedLandmarks, @org.jetbrains.annotations.NotNull()
    java.lang.String correctionHint) {
        super();
    }
    
    /**
     * Timestamp in millisecondi dall'inizio del video
     */
    public final long getTimestampMs() {
        return 0L;
    }
    
    /**
     * Timestamp di fine dell'errore (se persistente)
     */
    public final long getEndTimestampMs() {
        return 0L;
    }
    
    /**
     * Tipo di errore specifico
     */
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.ExerciseErrorType getErrorType() {
        return null;
    }
    
    /**
     * Severità dell'errore
     */
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.ErrorSeverity getSeverity() {
        return null;
    }
    
    /**
     * Messaggio descrittivo dell'errore
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMessage() {
        return null;
    }
    
    /**
     * Indici dei landmark MediaPipe coinvolti
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getAffectedLandmarks() {
        return null;
    }
    
    /**
     * Suggerimento per correggere l'errore
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCorrectionHint() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.ExerciseErrorType component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.ErrorSeverity component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.ExerciseError copy(long timestampMs, long endTimestampMs, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.ExerciseErrorType errorType, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.ErrorSeverity severity, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> affectedLandmarks, @org.jetbrains.annotations.NotNull()
    java.lang.String correctionHint) {
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