package com.app.fityo.dominio;

/**
 * Stati della modalità Tutor per la gestione del flusso UI.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\b\u0004\u0005\u0006\u0007\b\t\n\u000bB\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/app/fityo/dominio/TutorState;", "", "<init>", "()V", "Idle", "SelectingExercise", "SelectingVideoSource", "Recording", "Analyzing", "ResultReady", "Playback", "Error", "Lcom/app/fityo/dominio/TutorState$Analyzing;", "Lcom/app/fityo/dominio/TutorState$Error;", "Lcom/app/fityo/dominio/TutorState$Idle;", "Lcom/app/fityo/dominio/TutorState$Playback;", "Lcom/app/fityo/dominio/TutorState$Recording;", "Lcom/app/fityo/dominio/TutorState$ResultReady;", "Lcom/app/fityo/dominio/TutorState$SelectingExercise;", "Lcom/app/fityo/dominio/TutorState$SelectingVideoSource;", "wear_debug"})
public abstract class TutorState {
    
    private TutorState() {
        super();
    }
    
    /**
     * Analisi video in corso
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001a\u001a\u00020\tH\u00c6\u0003J;\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u00d6\u0003J\t\u0010 \u001a\u00020\tH\u00d6\u0001J\t\u0010!\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014\u00a8\u0006\""}, d2 = {"Lcom/app/fityo/dominio/TutorState$Analyzing;", "Lcom/app/fityo/dominio/TutorState;", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "videoPath", "", "progress", "", "currentFrame", "", "totalFrames", "<init>", "(Lcom/app/fityo/dominio/ExerciseType;Ljava/lang/String;FII)V", "getExerciseType", "()Lcom/app/fityo/dominio/ExerciseType;", "getVideoPath", "()Ljava/lang/String;", "getProgress", "()F", "getCurrentFrame", "()I", "getTotalFrames", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "toString", "wear_debug"})
    public static final class Analyzing extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ExerciseType exerciseType = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String videoPath = null;
        private final float progress = 0.0F;
        private final int currentFrame = 0;
        private final int totalFrames = 0;
        
        public Analyzing(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, float progress, int currentFrame, int totalFrames) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType getExerciseType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getVideoPath() {
            return null;
        }
        
        public final float getProgress() {
            return 0.0F;
        }
        
        public final int getCurrentFrame() {
            return 0;
        }
        
        public final int getTotalFrames() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.Analyzing copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, float progress, int currentFrame, int totalFrames) {
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
    
    /**
     * Errore durante l'elaborazione
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/app/fityo/dominio/TutorState$Error;", "Lcom/app/fityo/dominio/TutorState;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "wear_debug"})
    public static final class Error extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public Error(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.Error copy(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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
    
    /**
     * Stato iniziale - visualizza storico sessioni
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0003J\t\u0010\b\u001a\u00020\tH\u00d6\u0001J\t\u0010\n\u001a\u00020\u000bH\u00d6\u0001\u00a8\u0006\f"}, d2 = {"Lcom/app/fityo/dominio/TutorState$Idle;", "Lcom/app/fityo/dominio/TutorState;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class Idle extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        public static final com.app.fityo.dominio.TutorState.Idle INSTANCE = null;
        
        private Idle() {
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
    
    /**
     * Playback del video con errori evidenziati
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\fH\u00c6\u0003JA\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u00d6\u0003J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001J\t\u0010$\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006%"}, d2 = {"Lcom/app/fityo/dominio/TutorState$Playback;", "Lcom/app/fityo/dominio/TutorState;", "sessionId", "", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "videoPath", "", "errors", "", "Lcom/app/fityo/dominio/ExerciseError;", "overallScore", "", "<init>", "(ILcom/app/fityo/dominio/ExerciseType;Ljava/lang/String;Ljava/util/List;F)V", "getSessionId", "()I", "getExerciseType", "()Lcom/app/fityo/dominio/ExerciseType;", "getVideoPath", "()Ljava/lang/String;", "getErrors", "()Ljava/util/List;", "getOverallScore", "()F", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "toString", "wear_debug"})
    public static final class Playback extends com.app.fityo.dominio.TutorState {
        private final int sessionId = 0;
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ExerciseType exerciseType = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String videoPath = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.app.fityo.dominio.ExerciseError> errors = null;
        private final float overallScore = 0.0F;
        
        public Playback(int sessionId, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, @org.jetbrains.annotations.NotNull()
        java.util.List<com.app.fityo.dominio.ExerciseError> errors, float overallScore) {
        }
        
        public final int getSessionId() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType getExerciseType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getVideoPath() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.app.fityo.dominio.ExerciseError> getErrors() {
            return null;
        }
        
        public final float getOverallScore() {
            return 0.0F;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.app.fityo.dominio.ExerciseError> component4() {
            return null;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.Playback copy(int sessionId, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, @org.jetbrains.annotations.NotNull()
        java.util.List<com.app.fityo.dominio.ExerciseError> errors, float overallScore) {
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
    
    /**
     * Registrazione video in corso
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/TutorState$Recording;", "Lcom/app/fityo/dominio/TutorState;", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "<init>", "(Lcom/app/fityo/dominio/ExerciseType;)V", "getExerciseType", "()Lcom/app/fityo/dominio/ExerciseType;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class Recording extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ExerciseType exerciseType = null;
        
        public Recording(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType getExerciseType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.Recording copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType) {
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
    
    /**
     * Risultati pronti per la visualizzazione
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\fH\u00c6\u0003JA\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u00d6\u0003J\t\u0010#\u001a\u00020$H\u00d6\u0001J\t\u0010%\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lcom/app/fityo/dominio/TutorState$ResultReady;", "Lcom/app/fityo/dominio/TutorState;", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "videoPath", "", "errors", "", "Lcom/app/fityo/dominio/ExerciseError;", "overallScore", "", "duration", "", "<init>", "(Lcom/app/fityo/dominio/ExerciseType;Ljava/lang/String;Ljava/util/List;FJ)V", "getExerciseType", "()Lcom/app/fityo/dominio/ExerciseType;", "getVideoPath", "()Ljava/lang/String;", "getErrors", "()Ljava/util/List;", "getOverallScore", "()F", "getDuration", "()J", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "", "toString", "wear_debug"})
    public static final class ResultReady extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ExerciseType exerciseType = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String videoPath = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.app.fityo.dominio.ExerciseError> errors = null;
        private final float overallScore = 0.0F;
        private final long duration = 0L;
        
        public ResultReady(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, @org.jetbrains.annotations.NotNull()
        java.util.List<com.app.fityo.dominio.ExerciseError> errors, float overallScore, long duration) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType getExerciseType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getVideoPath() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.app.fityo.dominio.ExerciseError> getErrors() {
            return null;
        }
        
        public final float getOverallScore() {
            return 0.0F;
        }
        
        public final long getDuration() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.app.fityo.dominio.ExerciseError> component3() {
            return null;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final long component5() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.ResultReady copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType, @org.jetbrains.annotations.NotNull()
        java.lang.String videoPath, @org.jetbrains.annotations.NotNull()
        java.util.List<com.app.fityo.dominio.ExerciseError> errors, float overallScore, long duration) {
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
    
    /**
     * Selezione del tipo di esercizio
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0003J\t\u0010\b\u001a\u00020\tH\u00d6\u0001J\t\u0010\n\u001a\u00020\u000bH\u00d6\u0001\u00a8\u0006\f"}, d2 = {"Lcom/app/fityo/dominio/TutorState$SelectingExercise;", "Lcom/app/fityo/dominio/TutorState;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class SelectingExercise extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        public static final com.app.fityo.dominio.TutorState.SelectingExercise INSTANCE = null;
        
        private SelectingExercise() {
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
    
    /**
     * Selezione della sorgente video (registra o carica)
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/TutorState$SelectingVideoSource;", "Lcom/app/fityo/dominio/TutorState;", "exerciseType", "Lcom/app/fityo/dominio/ExerciseType;", "<init>", "(Lcom/app/fityo/dominio/ExerciseType;)V", "getExerciseType", "()Lcom/app/fityo/dominio/ExerciseType;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class SelectingVideoSource extends com.app.fityo.dominio.TutorState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ExerciseType exerciseType = null;
        
        public SelectingVideoSource(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType getExerciseType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ExerciseType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.TutorState.SelectingVideoSource copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ExerciseType exerciseType) {
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
}