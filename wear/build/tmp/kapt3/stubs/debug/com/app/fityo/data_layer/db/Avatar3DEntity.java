package com.app.fityo.data_layer.db;

/**
 * Entity Room per gli avatar 3D generati da Body Intelligence.
 * Memorizza mesh 3D, parametri shape e colori zone muscolari.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b!\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001Bg\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\r\u001a\u00020\u0006\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010$\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\'\u001a\u00020\bH\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010)\u001a\u00020\bH\u00c6\u0003J\t\u0010*\u001a\u00020\bH\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010,\u001a\u00020\u0006H\u00c6\u0003J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0010H\u00c6\u0003J\u0082\u0001\u0010/\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\r\u001a\u00020\u00062\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u00c6\u0001\u00a2\u0006\u0002\u00100J\u0013\u00101\u001a\u0002022\b\u00103\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00104\u001a\u00020\u0003H\u00d6\u0001J\t\u00105\u001a\u00020\bH\u00d6\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\t\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\u000b\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001bR\u0013\u0010\f\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001bR\u0011\u0010\r\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0019R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0017R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#\u00a8\u00066"}, d2 = {"Lcom/app/fityo/data_layer/db/Avatar3DEntity;", "", "id", "", "userId", "createdAt", "", "meshDataPath", "", "thumbnailPath", "shapeParametersJson", "zoneColorsJson", "videoSourcePath", "processingDurationMs", "framesAnalyzed", "confidence", "", "<init>", "(Ljava/lang/Integer;IJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JIF)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getUserId", "()I", "getCreatedAt", "()J", "getMeshDataPath", "()Ljava/lang/String;", "getThumbnailPath", "getShapeParametersJson", "getZoneColorsJson", "getVideoSourcePath", "getProcessingDurationMs", "getFramesAnalyzed", "getConfidence", "()F", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "copy", "(Ljava/lang/Integer;IJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JIF)Lcom/app/fityo/data_layer/db/Avatar3DEntity;", "equals", "", "other", "hashCode", "toString", "wear_debug"})
@androidx.room.Entity(tableName = "avatar_3d", foreignKeys = {@androidx.room.ForeignKey(entity = com.app.fityo.data_layer.db.UserProfileEntity.class, parentColumns = {"id"}, childColumns = {"userId"}, onDelete = 5)}, indices = {@androidx.room.Index(value = {"userId"})})
public final class Avatar3DEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer id = null;
    private final int userId = 0;
    private final long createdAt = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String meshDataPath = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailPath = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String shapeParametersJson = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String zoneColorsJson = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String videoSourcePath = null;
    private final long processingDurationMs = 0L;
    private final int framesAnalyzed = 0;
    private final float confidence = 0.0F;
    
    public Avatar3DEntity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, int userId, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String meshDataPath, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailPath, @org.jetbrains.annotations.NotNull()
    java.lang.String shapeParametersJson, @org.jetbrains.annotations.NotNull()
    java.lang.String zoneColorsJson, @org.jetbrains.annotations.Nullable()
    java.lang.String videoSourcePath, long processingDurationMs, int framesAnalyzed, float confidence) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    public final int getUserId() {
        return 0;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMeshDataPath() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getThumbnailPath() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getShapeParametersJson() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getZoneColorsJson() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getVideoSourcePath() {
        return null;
    }
    
    public final long getProcessingDurationMs() {
        return 0L;
    }
    
    public final int getFramesAnalyzed() {
        return 0;
    }
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    public final int component10() {
        return 0;
    }
    
    public final float component11() {
        return 0.0F;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final long component3() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.data_layer.db.Avatar3DEntity copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, int userId, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String meshDataPath, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailPath, @org.jetbrains.annotations.NotNull()
    java.lang.String shapeParametersJson, @org.jetbrains.annotations.NotNull()
    java.lang.String zoneColorsJson, @org.jetbrains.annotations.Nullable()
    java.lang.String videoSourcePath, long processingDurationMs, int framesAnalyzed, float confidence) {
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