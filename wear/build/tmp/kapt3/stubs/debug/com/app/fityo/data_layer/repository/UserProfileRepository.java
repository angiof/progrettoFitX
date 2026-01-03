package com.app.fityo.data_layer.repository;

/**
 * Repository per la gestione del profilo utente.
 * Fornisce un'interfaccia pulita per le operazioni CRUD.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0007J\u0010\u0010\t\u001a\u0004\u0018\u00010\bH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\u0004\u0018\u00010\b2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0012\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00100\u0007J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0018\u001a\u00020\u00162\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u0019\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u001c\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/app/fityo/data_layer/repository/UserProfileRepository;", "", "dao", "Lcom/app/fityo/data_layer/db/dao/DaoUserProfile;", "<init>", "(Lcom/app/fityo/data_layer/db/dao/DaoUserProfile;)V", "getActiveProfileFlow", "Lkotlinx/coroutines/flow/Flow;", "Lcom/app/fityo/dominio/UserProfile;", "getActiveProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllProfiles", "", "createProfile", "", "profile", "(Lcom/app/fityo/dominio/UserProfile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfile", "", "deleteProfile", "deleteById", "deleteAll", "hasProfile", "", "count", "wear_debug"})
public final class UserProfileRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.data_layer.db.dao.DaoUserProfile dao = null;
    
    public UserProfileRepository(@org.jetbrains.annotations.NotNull()
    com.app.fityo.data_layer.db.dao.DaoUserProfile dao) {
        super();
    }
    
    /**
     * Ottiene il profilo attivo come Flow.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.app.fityo.dominio.UserProfile> getActiveProfileFlow() {
        return null;
    }
    
    /**
     * Ottiene il profilo attivo.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getActiveProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.dominio.UserProfile> $completion) {
        return null;
    }
    
    /**
     * Ottiene un profilo per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.app.fityo.dominio.UserProfile> $completion) {
        return null;
    }
    
    /**
     * Ottiene tutti i profili.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.app.fityo.dominio.UserProfile>> getAllProfiles() {
        return null;
    }
    
    /**
     * Crea un nuovo profilo.
     * @return ID del profilo creato
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createProfile(@org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.UserProfile profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Aggiorna un profilo esistente.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateProfile(@org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.UserProfile profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina un profilo.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteProfile(@org.jetbrains.annotations.NotNull()
    com.app.fityo.dominio.UserProfile profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina un profilo per ID.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Elimina tutti i profili.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Verifica se esiste almeno un profilo.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object hasProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Conta i profili esistenti.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}