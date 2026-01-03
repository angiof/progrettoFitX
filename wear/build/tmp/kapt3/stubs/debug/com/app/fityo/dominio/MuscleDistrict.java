package com.app.fityo.dominio;

/**
 * Enum che rappresenta i distretti muscolari analizzabili.
 * Ogni distretto ha associati i punti MediaPipe Pose Landmarker necessari per l'analisi.
 *
 * Riferimento punti MediaPipe:
 * 11 = spalla sinistra, 12 = spalla destra
 * 13 = gomito sinistro, 14 = gomito destro
 * 15 = polso sinistro, 16 = polso destro
 * 23 = anca sinistra, 24 = anca destra
 * 25 = ginocchio sinistro, 26 = ginocchio destro
 * 27 = caviglia sinistra, 28 = caviglia destra
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u0000 \u00112\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0011B\u001f\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/MuscleDistrict;", "", "landmarks", "", "", "displayName", "", "<init>", "(Ljava/lang/String;ILjava/util/List;Ljava/lang/String;)V", "getLandmarks", "()Ljava/util/List;", "getDisplayName", "()Ljava/lang/String;", "ARMS", "ABS", "LEGS", "GLUTES", "Companion", "wear_debug"})
public enum MuscleDistrict {
    /*public static final*/ ARMS /* = new ARMS(null, null) */,
    /*public static final*/ ABS /* = new ABS(null, null) */,
    /*public static final*/ LEGS /* = new LEGS(null, null) */,
    /*public static final*/ GLUTES /* = new GLUTES(null, null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.Integer> landmarks = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    
    /**
     * Punti di riferimento per la normalizzazione della scala.
     * Usa la distanza tra le spalle come riferimento costante.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.Integer> NORMALIZATION_LANDMARKS = null;
    
    /**
     * Soglia minima di variazione per considerare un risultato "notevole"
     */
    public static final float NOTABLE_THRESHOLD = 3.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.app.fityo.dominio.MuscleDistrict.Companion Companion = null;
    
    MuscleDistrict(java.util.List<java.lang.Integer> landmarks, java.lang.String displayName) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getLandmarks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    /**
     * Enum che rappresenta i distretti muscolari analizzabili.
     * Ogni distretto ha associati i punti MediaPipe Pose Landmarker necessari per l'analisi.
     *
     * Riferimento punti MediaPipe:
     * 11 = spalla sinistra, 12 = spalla destra
     * 13 = gomito sinistro, 14 = gomito destro
     * 15 = polso sinistro, 16 = polso destro
     * 23 = anca sinistra, 24 = anca destra
     * 25 = ginocchio sinistro, 26 = ginocchio destro
     * 27 = caviglia sinistra, 28 = caviglia destra
     */
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.app.fityo.dominio.MuscleDistrict> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/app/fityo/dominio/MuscleDistrict$Companion;", "", "<init>", "()V", "NORMALIZATION_LANDMARKS", "", "", "getNORMALIZATION_LANDMARKS", "()Ljava/util/List;", "NOTABLE_THRESHOLD", "", "wear_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Punti di riferimento per la normalizzazione della scala.
         * Usa la distanza tra le spalle come riferimento costante.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.Integer> getNORMALIZATION_LANDMARKS() {
            return null;
        }
    }
}