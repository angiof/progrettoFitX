package com.app.fityo.data_layer.db;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b8\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B\u00a1\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0015J\f\u00103\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007J\u0006\u00104\u001a\u00020\u0005J\u0010\u00105\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\t\u00106\u001a\u00020\u0005H\u00c6\u0003J\u0011\u00107\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0007H\u00c6\u0003J\t\u00108\u001a\u00020\u0005H\u00c6\u0003J\t\u00109\u001a\u00020\u0005H\u00c6\u0003J\t\u0010:\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010=\u001a\u00020\u000eH\u00c6\u0003J\t\u0010>\u001a\u00020\u000eH\u00c6\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010@\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u0010\u0010A\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u0010\u0010B\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u00b0\u0001\u0010C\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010DJ\u0013\u0010E\u001a\u00020\u000e2\b\u0010F\u001a\u0004\u0018\u00010GH\u00d6\u0003J\t\u0010H\u001a\u00020\u0003H\u00d6\u0001J\t\u0010I\u001a\u00020\u0005H\u00d6\u0001R\"\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001a\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0019\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001cR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001cR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001cR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001cR\u001a\u0010\r\u001a\u00020\u000eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R\u001a\u0010\u000f\u001a\u00020\u000eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b(\u0010%\"\u0004\b)\u0010\'R\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010\u001c\"\u0004\b+\u0010,R\u001e\u0010\u0011\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001a\u001a\u0004\b-\u0010\u0017\"\u0004\b.\u0010\u0019R\u001e\u0010\u0012\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001a\u001a\u0004\b/\u0010\u0017\"\u0004\b0\u0010\u0019R\u001e\u0010\u0013\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001a\u001a\u0004\b1\u0010\u0017\"\u0004\b2\u0010\u0019\u00a8\u0006J"}, d2 = {"Lcom/app/fityo/data_layer/db/SchedeEntity;", "Ljava/io/Serializable;", "id", "", "gruppoMuscolare", "", "gruppiMuscolari", "", "intesita", "titolo", "data", "notes", "ora", "favorite", "", "completed", "completedDate", "totalSteps", "avgHeartRate", "maxHeartRate", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V", "getId", "()Ljava/lang/Integer;", "setId", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "getGruppoMuscolare", "()Ljava/lang/String;", "getGruppiMuscolari", "()Ljava/util/List;", "getIntesita", "getTitolo", "getData", "getNotes", "getOra", "getFavorite", "()Z", "setFavorite", "(Z)V", "getCompleted", "setCompleted", "getCompletedDate", "setCompletedDate", "(Ljava/lang/String;)V", "getTotalSteps", "setTotalSteps", "getAvgHeartRate", "setAvgHeartRate", "getMaxHeartRate", "setMaxHeartRate", "getAllGruppiMuscolari", "getGruppiMuscolariDisplay", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)Lcom/app/fityo/data_layer/db/SchedeEntity;", "equals", "other", "", "hashCode", "toString", "wear_debug"})
@androidx.room.Entity(tableName = "schede")
@androidx.room.TypeConverters(value = {com.app.fityo.data_layer.db.converters.Converters.class})
public final class SchedeEntity implements java.io.Serializable {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer id;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String gruppoMuscolare = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> gruppiMuscolari = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String intesita = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String titolo = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String data = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String notes = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String ora = null;
    private boolean favorite;
    private boolean completed;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String completedDate;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer totalSteps;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer avgHeartRate;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer maxHeartRate;
    
    public SchedeEntity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String gruppoMuscolare, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> gruppiMuscolari, @org.jetbrains.annotations.NotNull()
    java.lang.String intesita, @org.jetbrains.annotations.NotNull()
    java.lang.String titolo, @org.jetbrains.annotations.NotNull()
    java.lang.String data, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String ora, boolean favorite, boolean completed, @org.jetbrains.annotations.Nullable()
    java.lang.String completedDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer totalSteps, @org.jetbrains.annotations.Nullable()
    java.lang.Integer avgHeartRate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer maxHeartRate) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    public final void setId(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getGruppoMuscolare() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getGruppiMuscolari() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIntesita() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitolo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getData() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getOra() {
        return null;
    }
    
    public final boolean getFavorite() {
        return false;
    }
    
    public final void setFavorite(boolean p0) {
    }
    
    public final boolean getCompleted() {
        return false;
    }
    
    public final void setCompleted(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCompletedDate() {
        return null;
    }
    
    public final void setCompletedDate(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getTotalSteps() {
        return null;
    }
    
    public final void setTotalSteps(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getAvgHeartRate() {
        return null;
    }
    
    public final void setAvgHeartRate(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getMaxHeartRate() {
        return null;
    }
    
    public final void setMaxHeartRate(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAllGruppiMuscolari() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getGruppiMuscolariDisplay() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.data_layer.db.SchedeEntity copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String gruppoMuscolare, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> gruppiMuscolari, @org.jetbrains.annotations.NotNull()
    java.lang.String intesita, @org.jetbrains.annotations.NotNull()
    java.lang.String titolo, @org.jetbrains.annotations.NotNull()
    java.lang.String data, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String ora, boolean favorite, boolean completed, @org.jetbrains.annotations.Nullable()
    java.lang.String completedDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer totalSteps, @org.jetbrains.annotations.Nullable()
    java.lang.Integer avgHeartRate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer maxHeartRate) {
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