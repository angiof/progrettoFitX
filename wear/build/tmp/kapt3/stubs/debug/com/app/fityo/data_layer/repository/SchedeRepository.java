package com.app.fityo.data_layer.repository;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000e\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u00102\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001e\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\t0\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J$\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001f0\u00102\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010#J$\u0010$\u001a\b\u0012\u0004\u0012\u00020%0\u00102\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010#J$\u0010&\u001a\b\u0012\u0004\u0012\u00020\t0\u00102\u0006\u0010\'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010)J\u001e\u0010*\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010+\u001a\u00020,H\u0086@\u00a2\u0006\u0002\u0010-J(\u0010.\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010/\u001a\u00020,2\b\u00100\u001a\u0004\u0018\u00010\u0018H\u0086@\u00a2\u0006\u0002\u00101J\u000e\u00102\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u000e\u00103\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u000e\u00104\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0010\u00105\u001a\u0004\u0018\u00010\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0014\u00106\u001a\b\u0012\u0004\u0012\u00020%0\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0014\u00107\u001a\b\u0012\u0004\u0012\u0002080\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J$\u00109\u001a\b\u0012\u0004\u0012\u0002080\u00102\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010#J\u0010\u0010:\u001a\u0004\u0018\u00010\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0010\u0010;\u001a\u0004\u0018\u00010\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0010\u0010<\u001a\u0004\u0018\u00010=H\u0086@\u00a2\u0006\u0002\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lcom/app/fityo/data_layer/repository/SchedeRepository;", "", "daoSchede", "Lcom/app/fityo/data_layer/db/dao/DaoSchede;", "<init>", "(Lcom/app/fityo/data_layer/db/dao/DaoSchede;)V", "insert", "", "schede", "Lcom/app/fityo/data_layer/db/SchedeEntity;", "(Lcom/app/fityo/data_layer/db/SchedeEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "scheda", "delete", "getAllSchede", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeByGruppoMuscolare", "gruppoMuscolare", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTime", "time", "(ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeWithTime", "getPercentualePerGruppoMuscolare", "Lcom/app/fityo/dominio/GruppoMuscolarePercentuale;", "getPercentualePerGruppoMuscolareInDateRange", "startDate", "endDate", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMediaIntensitaPerGruppoMuscolareDateRange", "Lcom/app/fityo/data_layer/db/dao/GruppoMuscolareIntensitaMedia;", "getSchedeInDateRange", "startMillis", "endMillis", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setFavorite", "isFav", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCompleted", "isCompleted", "completedDate", "(IZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "countSchede", "countFavoriteSchede", "countTotalExercises", "getLastWorkoutDate", "getMediaIntensitaAll", "getWorkoutCountByWeekdayAll", "Lcom/app/fityo/dominio/WeekdayWorkoutCount;", "getWorkoutCountByWeekday", "getDaysSinceLastWorkout", "getMostTrainedMuscleGroup", "getAverageWorkoutsPerWeek", "", "wear_debug"})
public final class SchedeRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.data_layer.db.dao.DaoSchede daoSchede = null;
    
    public SchedeRepository(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.dao.DaoSchede daoSchede) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity schede, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity scheda, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity scheda, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSchedeById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.SchedeEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSchedeByGruppoMuscolare(@org.jetbrains.annotations.NotNull()
    java.lang.String gruppoMuscolare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateTime(int id, @org.jetbrains.annotations.NotNull()
    java.lang.String time, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSchedeWithTime(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getPercentualePerGruppoMuscolare(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.GruppoMuscolarePercentuale>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getPercentualePerGruppoMuscolareInDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.GruppoMuscolarePercentuale>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMediaIntensitaPerGruppoMuscolareDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSchedeInDateRange(long startMillis, long endMillis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setFavorite(int id, boolean isFav, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCompleted(int id, boolean isCompleted, @org.jetbrains.annotations.Nullable()
    java.lang.String completedDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object countSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object countFavoriteSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object countTotalExercises(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLastWorkoutDate(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMediaIntensitaAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWorkoutCountByWeekdayAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.WeekdayWorkoutCount>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWorkoutCountByWeekday(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.WeekdayWorkoutCount>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDaysSinceLastWorkout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMostTrainedMuscleGroup(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAverageWorkoutsPerWeek(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion) {
        return null;
    }
}