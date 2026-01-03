package com.app.fityo.data_layer.repository;

/**
 * Repository per i confronti muscolari.
 * Gestisce l'accesso ai dati e le trasformazioni.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001!B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0012\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007J\u0012\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\b0\u0007J\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000e\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000e\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0016J\u0018\u0010\u0017\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0016J\u0010\u0010\u0018\u001a\u0004\u0018\u00010\tH\u0086@\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010\u001a\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0019J\"\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\rJ\u000e\u0010\u001e\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/app/fityo/data_layer/repository/MuscleCompareRepository;", "", "dao", "Lcom/app/fityo/data_layer/db/dao/DaoMuscleCompare;", "<init>", "(Lcom/app/fityo/data_layer/db/dao/DaoMuscleCompare;)V", "getAllCompares", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/app/fityo/data_layer/db/MuscleCompareEntity;", "getAllComparesAsHistoryItems", "Lcom/app/fityo/dominio/CompareHistoryItem;", "insert", "", "compare", "(Lcom/app/fityo/data_layer/db/MuscleCompareEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "getLatest", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCount", "getComparesByDateRange", "startDate", "endDate", "deleteAll", "getAverageVariations", "Lcom/app/fityo/data_layer/repository/MuscleCompareRepository$AverageVariations;", "AverageVariations", "wear_debug"})
public final class MuscleCompareRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.data_layer.db.dao.DaoMuscleCompare dao = null;
    
    public MuscleCompareRepository(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.dao.DaoMuscleCompare dao) {
        super();
    }
    
    /**
     * Ottiene tutti i confronti come Flow per aggiornamenti reattivi.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.MuscleCompareEntity>> getAllCompares() {
        return null;
    }
    
    /**
     * Ottiene tutti i confronti come lista di CompareHistoryItem.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.dominio.CompareHistoryItem>> getAllComparesAsHistoryItems() {
        return null;
    }
    
    /**
     * Inserisce un nuovo confronto.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Aggiorna un confronto esistente.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina un confronto.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.MuscleCompareEntity compare, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina un confronto per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Ottiene un confronto per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.MuscleCompareEntity> $completion) {
        return null;
    }
    
    /**
     * Ottiene l'ultimo confronto.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLatest(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.db.MuscleCompareEntity> $completion) {
        return null;
    }
    
    /**
     * Conta il numero totale di confronti.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * Ottiene confronti in un range di date.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.data_layer.db.MuscleCompareEntity>> getComparesByDateRange(long startDate, long endDate) {
        return null;
    }
    
    /**
     * Elimina tutti i confronti.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Ottiene le medie delle variazioni per tutti i distretti.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAverageVariations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.data_layer.repository.MuscleCompareRepository.AverageVariations> $completion) {
        return null;
    }
    
    /**
     * Data class per le medie delle variazioni.
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n\u00a8\u0006\u001a"}, d2 = {"Lcom/app/fityo/data_layer/repository/MuscleCompareRepository$AverageVariations;", "", "arms", "", "abs", "legs", "glutes", "<init>", "(FFFF)V", "getArms", "()F", "getAbs", "getLegs", "getGlutes", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "wear_debug"})
    public static final class AverageVariations {
        private final float arms = 0.0F;
        private final float abs = 0.0F;
        private final float legs = 0.0F;
        private final float glutes = 0.0F;
        
        public AverageVariations(float arms, float abs, float legs, float glutes) {
            super();
        }
        
        public final float getArms() {
            return 0.0F;
        }
        
        public final float getAbs() {
            return 0.0F;
        }
        
        public final float getLegs() {
            return 0.0F;
        }
        
        public final float getGlutes() {
            return 0.0F;
        }
        
        public final float component1() {
            return 0.0F;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.data_layer.repository.MuscleCompareRepository.AverageVariations copy(float arms, float abs, float legs, float glutes) {
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
}