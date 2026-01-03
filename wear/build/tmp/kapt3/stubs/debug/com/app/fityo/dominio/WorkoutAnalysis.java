package com.app.fityo.dominio;

/**
 * Risultato dell'analisi che correla i cambiamenti visivi con la scheda di allenamento.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\rH\u00c6\u0003JQ\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010 \u001a\u00020\u00032\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006%"}, d2 = {"Lcom/app/fityo/dominio/WorkoutAnalysis;", "", "isWorkoutEffective", "", "overallMessage", "", "encouragementMessage", "muscleGroupCorrelations", "", "Lcom/app/fityo/dominio/MuscleGroupCorrelation;", "suggestions", "Lcom/app/fityo/dominio/WorkoutSuggestion;", "workoutStats", "Lcom/app/fityo/dominio/WorkoutStats;", "<init>", "(ZLjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Lcom/app/fityo/dominio/WorkoutStats;)V", "()Z", "getOverallMessage", "()Ljava/lang/String;", "getEncouragementMessage", "getMuscleGroupCorrelations", "()Ljava/util/List;", "getSuggestions", "getWorkoutStats", "()Lcom/app/fityo/dominio/WorkoutStats;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "wear_debug"})
public final class WorkoutAnalysis {
    private final boolean isWorkoutEffective = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String overallMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String encouragementMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.app.fityo.dominio.MuscleGroupCorrelation> muscleGroupCorrelations = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.app.fityo.dominio.WorkoutSuggestion> suggestions = null;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.dominio.WorkoutStats workoutStats = null;
    
    public WorkoutAnalysis(boolean isWorkoutEffective, @org.jetbrains.annotations.NotNull()
    java.lang.String overallMessage, @org.jetbrains.annotations.NotNull()
    java.lang.String encouragementMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<com.app.fityo.dominio.MuscleGroupCorrelation> muscleGroupCorrelations, @org.jetbrains.annotations.NotNull()
    java.util.List<com.app.fityo.dominio.WorkoutSuggestion> suggestions, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.WorkoutStats workoutStats) {
        super();
    }
    
    public final boolean isWorkoutEffective() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOverallMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEncouragementMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.app.fityo.dominio.MuscleGroupCorrelation> getMuscleGroupCorrelations() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.app.fityo.dominio.WorkoutSuggestion> getSuggestions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.WorkoutStats getWorkoutStats() {
        return null;
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.app.fityo.dominio.MuscleGroupCorrelation> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.app.fityo.dominio.WorkoutSuggestion> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.WorkoutStats component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.WorkoutAnalysis copy(boolean isWorkoutEffective, @org.jetbrains.annotations.NotNull()
    java.lang.String overallMessage, @org.jetbrains.annotations.NotNull()
    java.lang.String encouragementMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<com.app.fityo.dominio.MuscleGroupCorrelation> muscleGroupCorrelations, @org.jetbrains.annotations.NotNull()
    java.util.List<com.app.fityo.dominio.WorkoutSuggestion> suggestions, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.WorkoutStats workoutStats) {
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