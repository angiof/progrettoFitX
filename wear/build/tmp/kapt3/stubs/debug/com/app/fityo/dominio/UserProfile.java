package com.app.fityo.dominio;

/**
 * Profilo utente con dati biometrici e preferenze.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0011\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u000e\u0010+\u001a\u00020\b2\u0006\u0010,\u001a\u00020\bJ\u000e\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\bJ\u0010\u00100\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u00101\u001a\u00020\u0005H\u00c6\u0003J\t\u00102\u001a\u00020\u0003H\u00c6\u0003J\t\u00103\u001a\u00020\bH\u00c6\u0003J\t\u00104\u001a\u00020\bH\u00c6\u0003J\t\u00105\u001a\u00020\u000bH\u00c6\u0003J\t\u00106\u001a\u00020\rH\u00c6\u0003J\t\u00107\u001a\u00020\u000fH\u00c6\u0003J\t\u00108\u001a\u00020\u000fH\u00c6\u0003Jj\u00109\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000fH\u00c6\u0001\u00a2\u0006\u0002\u0010:J\u0013\u0010;\u001a\u00020)2\b\u0010<\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010=\u001a\u00020\u0003H\u00d6\u0001J\t\u0010>\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\"R\u0011\u0010$\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b%\u0010\u001bR\u0011\u0010&\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\'\u0010\u0017R\u0011\u0010(\u001a\u00020)8F\u00a2\u0006\u0006\u001a\u0004\b(\u0010*\u00a8\u0006?"}, d2 = {"Lcom/app/fityo/dominio/UserProfile;", "", "id", "", "name", "", "age", "heightCm", "", "weightKg", "sex", "Lcom/app/fityo/dominio/BiologicalSex;", "discipline", "Lcom/app/fityo/dominio/AthleticDiscipline;", "createdAt", "", "updatedAt", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;IFFLcom/app/fityo/dominio/BiologicalSex;Lcom/app/fityo/dominio/AthleticDiscipline;JJ)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getName", "()Ljava/lang/String;", "getAge", "()I", "getHeightCm", "()F", "getWeightKg", "getSex", "()Lcom/app/fityo/dominio/BiologicalSex;", "getDiscipline", "()Lcom/app/fityo/dominio/AthleticDiscipline;", "getCreatedAt", "()J", "getUpdatedAt", "bmi", "getBmi", "bmiCategory", "getBmiCategory", "isBmiIdealForDiscipline", "", "()Z", "calculateFfmi", "bodyFatPercent", "evaluateFfmi", "Lcom/app/fityo/dominio/FfmiEvaluation;", "ffmi", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/Integer;Ljava/lang/String;IFFLcom/app/fityo/dominio/BiologicalSex;Lcom/app/fityo/dominio/AthleticDiscipline;JJ)Lcom/app/fityo/dominio/UserProfile;", "equals", "other", "hashCode", "toString", "wear_debug"})
public final class UserProfile {
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    private final int age = 0;
    private final float heightCm = 0.0F;
    private final float weightKg = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.dominio.BiologicalSex sex = null;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.dominio.AthleticDiscipline discipline = null;
    private final long createdAt = 0L;
    private final long updatedAt = 0L;
    
    public UserProfile(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, int age, float heightCm, float weightKg, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.BiologicalSex sex, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.AthleticDiscipline discipline, long createdAt, long updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    public final int getAge() {
        return 0;
    }
    
    public final float getHeightCm() {
        return 0.0F;
    }
    
    public final float getWeightKg() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.BiologicalSex getSex() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.AthleticDiscipline getDiscipline() {
        return null;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long getUpdatedAt() {
        return 0L;
    }
    
    public final float getBmi() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBmiCategory() {
        return null;
    }
    
    public final boolean isBmiIdealForDiscipline() {
        return false;
    }
    
    /**
     * Calcola il FFMI stimato (richiede body fat %).
     * FFMI = (Massa Magra / Altezza²) + 6.1 × (1.8 - Altezza)
     */
    public final float calculateFfmi(float bodyFatPercent) {
        return 0.0F;
    }
    
    /**
     * Valuta il FFMI rispetto alla disciplina.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.FfmiEvaluation evaluateFfmi(float ffmi) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final float component4() {
        return 0.0F;
    }
    
    public final float component5() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.BiologicalSex component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.AthleticDiscipline component7() {
        return null;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.dominio.UserProfile copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, int age, float heightCm, float weightKg, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.BiologicalSex sex, @org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.AthleticDiscipline discipline, long createdAt, long updatedAt) {
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