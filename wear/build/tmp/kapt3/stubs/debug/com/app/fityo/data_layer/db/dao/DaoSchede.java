package com.app.fityo.data_layer.db.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00050\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ$\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001b0\f2\u0006\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u001e\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ$\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\f2\u0006\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u001e\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ$\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u0006\u0010#\u001a\u00020\u00142\u0006\u0010$\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u0010%\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010&\u001a\u00020\'H\u00a7@\u00a2\u0006\u0002\u0010(J(\u0010)\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010*\u001a\u00020\'2\b\u0010+\u001a\u0004\u0018\u00010\u0014H\u00a7@\u00a2\u0006\u0002\u0010,J\u000e\u0010-\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u000e\u0010.\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u000e\u0010/\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u00100\u001a\u0004\u0018\u00010\u0014H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u00101\u001a\b\u0012\u0004\u0012\u00020!0\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u00102\u001a\b\u0012\u0004\u0012\u0002030\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ$\u00104\u001a\b\u0012\u0004\u0012\u0002030\f2\u0006\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u001e\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u0010\u00105\u001a\u0004\u0018\u00010\u0010H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u00106\u001a\u0004\u0018\u00010\u0014H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0010\u00107\u001a\u0004\u0018\u000108H\u00a7@\u00a2\u0006\u0002\u0010\r\u00a8\u00069"}, d2 = {"Lcom/app/fityo/data_layer/db/dao/DaoSchede;", "", "insert", "", "schede", "Lcom/app/fityo/data_layer/db/SchedeEntity;", "(Lcom/app/fityo/data_layer/db/SchedeEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "scheda", "delete", "getAllSchede", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeByGruppoMuscolare", "gruppoMuscolare", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTime", "time", "(ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedeWithTime", "getPercentualePerGruppoMuscolare", "Lcom/app/fityo/dominio/GruppoMuscolarePercentuale;", "getPercentualePerGruppoMuscolareInDateRange", "startDate", "endDate", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMediaIntensitaPerGruppoMuscolareDateRange", "Lcom/app/fityo/data_layer/db/dao/GruppoMuscolareIntensitaMedia;", "getSchedeInDateRange", "start", "end", "setFavorite", "isFav", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCompleted", "isCompleted", "completedDate", "(IZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "countSchede", "countFavoriteSchede", "countTotalExercises", "getLastWorkoutDate", "getMediaIntensitaPerGruppoMuscolareAll", "getWorkoutCountByWeekdayAll", "Lcom/app/fityo/dominio/WeekdayWorkoutCount;", "getWorkoutCountByWeekday", "getDaysSinceLastWorkout", "getMostTrainedMuscleGroup", "getAverageWorkoutsPerWeek", "", "wear_debug"})
@androidx.room.Dao()
public abstract interface DaoSchede {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity schede, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity scheda, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.SchedeEntity scheda, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM schede")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM schede WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSchedeById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.SchedeEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM schede WHERE gruppoMuscolare = :gruppoMuscolare")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSchedeByGruppoMuscolare(@org.jetbrains.annotations.NotNull()
    java.lang.String gruppoMuscolare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE schede SET ora = :time WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTime(int id, @org.jetbrains.annotations.NotNull()
    java.lang.String time, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM schede WHERE ora IS NOT NULL")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSchedeWithTime(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT gruppoMuscolare, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM schede) as percentuale \n        FROM schede \n        GROUP BY gruppoMuscolare\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPercentualePerGruppoMuscolare(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.GruppoMuscolarePercentuale>> $completion);
    
    @androidx.room.Query(value = "\n    SELECT gruppoMuscolare, COUNT(*) * 100.0 / (\n        SELECT COUNT(*) \n        FROM schede \n        WHERE data BETWEEN :startDate AND :endDate\n    ) as percentuale \n    FROM schede \n    WHERE data BETWEEN :startDate AND :endDate\n    GROUP BY gruppoMuscolare\n")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPercentualePerGruppoMuscolareInDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.GruppoMuscolarePercentuale>> $completion);
    
    @androidx.room.Query(value = "\n    SELECT gruppoMuscolare, AVG(\n        CASE intesita\n            WHEN \'Bassa\' THEN 5\n            WHEN \'Media\' THEN 10\n            WHEN \'Alta\' THEN 15\n            ELSE 0 \n        END\n    ) AS mediaIntensita\n    FROM schede\n    WHERE data BETWEEN :startDate AND :endDate\n    GROUP BY gruppoMuscolare\n")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaIntensitaPerGruppoMuscolareDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia>> $completion);
    
    @androidx.room.Query(value = "\n    SELECT * FROM schede\n    WHERE date(data) BETWEEN date(:start) AND date(:end)\n    ORDER BY date(data) DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSchedeInDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String start, @org.jetbrains.annotations.NotNull()
    java.lang.String end, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.SchedeEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE schede SET favorite = :isFav WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setFavorite(int id, boolean isFav, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE schede SET completed = :isCompleted, completedDate = :completedDate WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setCompleted(int id, boolean isCompleted, @org.jetbrains.annotations.Nullable()
    java.lang.String completedDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM schede")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM schede WHERE favorite = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countFavoriteSchede(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM essercissi")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countTotalExercises(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "SELECT data FROM schede ORDER BY date(data) DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLastWorkoutDate(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @androidx.room.Query(value = "\n        SELECT gruppoMuscolare, AVG(\n            CASE intesita\n                WHEN \'Bassa\' THEN 5\n                WHEN \'Media\' THEN 10\n                WHEN \'Alta\' THEN 15\n                ELSE 0 \n            END\n        ) AS mediaIntensita\n        FROM schede\n        GROUP BY gruppoMuscolare\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaIntensitaPerGruppoMuscolareAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT strftime(\'%w\', data) AS dayOfWeek, COUNT(*) AS count\n        FROM schede\n        GROUP BY dayOfWeek\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWorkoutCountByWeekdayAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.WeekdayWorkoutCount>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT strftime(\'%w\', data) AS dayOfWeek, COUNT(*) AS count\n        FROM schede\n        WHERE date(data) BETWEEN date(:startDate) AND date(:endDate)\n        GROUP BY dayOfWeek\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWorkoutCountByWeekday(@org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    java.lang.String endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.app.fityo.dominio.WeekdayWorkoutCount>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT CAST(JULIANDAY(\'now\') - JULIANDAY(MAX(data)) AS INTEGER) as daysSinceLastWorkout\n        FROM schede\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDaysSinceLastWorkout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "\n        SELECT gruppoMuscolare\n        FROM schede\n        GROUP BY gruppoMuscolare\n        ORDER BY COUNT(*) DESC\n        LIMIT 1\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMostTrainedMuscleGroup(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @androidx.room.Query(value = "\n        SELECT COUNT(*) * 1.0 /\n        (SELECT (JULIANDAY(MAX(data)) - JULIANDAY(MIN(data))) / 7.0 FROM schede)\n        as avgPerWeek\n        FROM schede\n        WHERE (SELECT COUNT(*) FROM schede) > 1\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAverageWorkoutsPerWeek(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
}