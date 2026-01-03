package com.app.fityo.data_layer.db;

/**
 * Entity Room per salvare i confronti muscolari.
 * Le foto sono salvate criptate localmente e qui si memorizzano i path.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b,\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u0083\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\n\u0012\u0006\u0010\r\u001a\u00020\n\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\u0011\u001a\u00020\n\u0012\b\b\u0002\u0010\u0012\u001a\u00020\n\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\'\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0016J\t\u0010(\u001a\u00020\u0005H\u00c6\u0003J\t\u0010)\u001a\u00020\u0007H\u00c6\u0003J\t\u0010*\u001a\u00020\u0007H\u00c6\u0003J\t\u0010+\u001a\u00020\nH\u00c6\u0003J\t\u0010,\u001a\u00020\nH\u00c6\u0003J\t\u0010-\u001a\u00020\nH\u00c6\u0003J\t\u0010.\u001a\u00020\nH\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u00102\u001a\u00020\nH\u00c6\u0003J\t\u00103\u001a\u00020\nH\u00c6\u0003J\u0098\u0001\u00104\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\n2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0011\u001a\u00020\n2\b\b\u0002\u0010\u0012\u001a\u00020\nH\u00c6\u0001\u00a2\u0006\u0002\u00105J\u0013\u00106\u001a\u0002072\b\u00108\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00109\u001a\u00020\u0003H\u00d6\u0001J\t\u0010:\u001a\u00020\u0007H\u00d6\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0017\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0011\u0010\f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001eR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001eR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0011\u0010\u0011\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001eR\u0011\u0010\u0012\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001e\u00a8\u0006;"}, d2 = {"Lcom/app/fityo/data_layer/db/MuscleCompareEntity;", "", "id", "", "createdAt", "", "photoAPath", "", "photoBPath", "armsVariation", "", "absVariation", "legsVariation", "glutesVariation", "notes", "photoADate", "photoBDate", "scaleFactorA", "scaleFactorB", "<init>", "(Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;FFFFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;FF)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getCreatedAt", "()J", "getPhotoAPath", "()Ljava/lang/String;", "getPhotoBPath", "getArmsVariation", "()F", "getAbsVariation", "getLegsVariation", "getGlutesVariation", "getNotes", "getPhotoADate", "getPhotoBDate", "getScaleFactorA", "getScaleFactorB", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "copy", "(Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;FFFFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;FF)Lcom/app/fityo/data_layer/db/MuscleCompareEntity;", "equals", "", "other", "hashCode", "toString", "wear_debug"})
@androidx.room.Entity(tableName = "muscle_compare")
public final class MuscleCompareEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer id = null;
    
    /**
     * Timestamp di creazione del confronto
     */
    private final long createdAt = 0L;
    
    /**
     * Path della prima foto (criptata)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String photoAPath = null;
    
    /**
     * Path della seconda foto (criptata)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String photoBPath = null;
    
    /**
     * Variazione percentuale braccia (-100 a +100)
     */
    private final float armsVariation = 0.0F;
    
    /**
     * Variazione percentuale addominali (-100 a +100)
     */
    private final float absVariation = 0.0F;
    
    /**
     * Variazione percentuale gambe (-100 a +100)
     */
    private final float legsVariation = 0.0F;
    
    /**
     * Variazione percentuale glutei (-100 a +100)
     */
    private final float glutesVariation = 0.0F;
    
    /**
     * Note opzionali
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String notes = null;
    
    /**
     * Data originale della foto A (opzionale, per storico)
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String photoADate = null;
    
    /**
     * Data originale della foto B (opzionale, per storico)
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String photoBDate = null;
    
    /**
     * Fattore di scala usato per foto A
     */
    private final float scaleFactorA = 0.0F;
    
    /**
     * Fattore di scala usato per foto B
     */
    private final float scaleFactorB = 0.0F;
    
    public MuscleCompareEntity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String photoAPath, @org.jetbrains.annotations.NotNull()
    java.lang.String photoBPath, float armsVariation, float absVariation, float legsVariation, float glutesVariation, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String photoADate, @org.jetbrains.annotations.Nullable()
    java.lang.String photoBDate, float scaleFactorA, float scaleFactorB) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    /**
     * Timestamp di creazione del confronto
     */
    public final long getCreatedAt() {
        return 0L;
    }
    
    /**
     * Path della prima foto (criptata)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPhotoAPath() {
        return null;
    }
    
    /**
     * Path della seconda foto (criptata)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPhotoBPath() {
        return null;
    }
    
    /**
     * Variazione percentuale braccia (-100 a +100)
     */
    public final float getArmsVariation() {
        return 0.0F;
    }
    
    /**
     * Variazione percentuale addominali (-100 a +100)
     */
    public final float getAbsVariation() {
        return 0.0F;
    }
    
    /**
     * Variazione percentuale gambe (-100 a +100)
     */
    public final float getLegsVariation() {
        return 0.0F;
    }
    
    /**
     * Variazione percentuale glutei (-100 a +100)
     */
    public final float getGlutesVariation() {
        return 0.0F;
    }
    
    /**
     * Note opzionali
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotes() {
        return null;
    }
    
    /**
     * Data originale della foto A (opzionale, per storico)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPhotoADate() {
        return null;
    }
    
    /**
     * Data originale della foto B (opzionale, per storico)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPhotoBDate() {
        return null;
    }
    
    /**
     * Fattore di scala usato per foto A
     */
    public final float getScaleFactorA() {
        return 0.0F;
    }
    
    /**
     * Fattore di scala usato per foto B
     */
    public final float getScaleFactorB() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component11() {
        return null;
    }
    
    public final float component12() {
        return 0.0F;
    }
    
    public final float component13() {
        return 0.0F;
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
    
    public final float component5() {
        return 0.0F;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    public final float component7() {
        return 0.0F;
    }
    
    public final float component8() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.data_layer.db.MuscleCompareEntity copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, long createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String photoAPath, @org.jetbrains.annotations.NotNull()
    java.lang.String photoBPath, float armsVariation, float absVariation, float legsVariation, float glutesVariation, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String photoADate, @org.jetbrains.annotations.Nullable()
    java.lang.String photoBDate, float scaleFactorA, float scaleFactorB) {
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