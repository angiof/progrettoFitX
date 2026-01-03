package com.app.fityo.dominio;

/**
 * Livelli di severità degli errori.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/app/fityo/dominio/ErrorSeverity;", "", "displayName", "", "colorWeight", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;F)V", "getDisplayName", "()Ljava/lang/String;", "getColorWeight", "()F", "WARNING", "ERROR", "CRITICAL", "wear_debug"})
public enum ErrorSeverity {
    /*public static final*/ WARNING /* = new WARNING(null, 0.0F) */,
    /*public static final*/ ERROR /* = new ERROR(null, 0.0F) */,
    /*public static final*/ CRITICAL /* = new CRITICAL(null, 0.0F) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    private final float colorWeight = 0.0F;
    
    ErrorSeverity(java.lang.String displayName, float colorWeight) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    public final float getColorWeight() {
        return 0.0F;
    }
    
    /**
     * Livelli di severità degli errori.
     */
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.app.fityo.dominio.ErrorSeverity> getEntries() {
        return null;
    }
}