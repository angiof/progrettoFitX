package com.app.fityo.dominio;

/**
 * Tipo di correlazione tra allenamento e risultati visivi.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/app/fityo/dominio/CorrelationType;", "", "<init>", "(Ljava/lang/String;I)V", "POSITIVE_STRONG", "POSITIVE_WEAK", "NEUTRAL", "NEEDS_ATTENTION", "UNEXPECTED_DECREASE", "wear_debug"})
public enum CorrelationType {
    /*public static final*/ POSITIVE_STRONG /* = new POSITIVE_STRONG() */,
    /*public static final*/ POSITIVE_WEAK /* = new POSITIVE_WEAK() */,
    /*public static final*/ NEUTRAL /* = new NEUTRAL() */,
    /*public static final*/ NEEDS_ATTENTION /* = new NEEDS_ATTENTION() */,
    /*public static final*/ UNEXPECTED_DECREASE /* = new UNEXPECTED_DECREASE() */;
    
    CorrelationType() {
    }
    
    /**
     * Tipo di correlazione tra allenamento e risultati visivi.
     */
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.app.fityo.dominio.CorrelationType> getEntries() {
        return null;
    }
}