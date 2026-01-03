package com.app.fityo.dominio;

/**
 * Stato UI per il processo di confronto.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\t\u0004\u0005\u0006\u0007\b\t\n\u000b\fB\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\t\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/app/fityo/dominio/CompareState;", "", "<init>", "()V", "Idle", "SelectingViewType", "CapturingPhotoA", "PhotoACaptured", "CapturingPhotoB", "PhotoBCaptured", "Processing", "ResultReady", "Error", "Lcom/app/fityo/dominio/CompareState$CapturingPhotoA;", "Lcom/app/fityo/dominio/CompareState$CapturingPhotoB;", "Lcom/app/fityo/dominio/CompareState$Error;", "Lcom/app/fityo/dominio/CompareState$Idle;", "Lcom/app/fityo/dominio/CompareState$PhotoACaptured;", "Lcom/app/fityo/dominio/CompareState$PhotoBCaptured;", "Lcom/app/fityo/dominio/CompareState$Processing;", "Lcom/app/fityo/dominio/CompareState$ResultReady;", "Lcom/app/fityo/dominio/CompareState$SelectingViewType;", "wear_debug"})
public abstract class CompareState {
    
    private CompareState() {
        super();
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/CompareState$CapturingPhotoA;", "Lcom/app/fityo/dominio/CompareState;", "viewType", "Lcom/app/fityo/dominio/ViewType;", "<init>", "(Lcom/app/fityo/dominio/ViewType;)V", "getViewType", "()Lcom/app/fityo/dominio/ViewType;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class CapturingPhotoA extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ViewType viewType = null;
        
        public CapturingPhotoA(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType getViewType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.CapturingPhotoA copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/CompareState$CapturingPhotoB;", "Lcom/app/fityo/dominio/CompareState;", "viewType", "Lcom/app/fityo/dominio/ViewType;", "<init>", "(Lcom/app/fityo/dominio/ViewType;)V", "getViewType", "()Lcom/app/fityo/dominio/ViewType;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class CapturingPhotoB extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ViewType viewType = null;
        
        public CapturingPhotoB(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType getViewType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.CapturingPhotoB copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/app/fityo/dominio/CompareState$Error;", "Lcom/app/fityo/dominio/CompareState;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "wear_debug"})
    public static final class Error extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public Error(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.Error copy(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/app/fityo/dominio/CompareState$Idle;", "Lcom/app/fityo/dominio/CompareState;", "<init>", "()V", "wear_debug"})
    public static final class Idle extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        public static final com.app.fityo.dominio.CompareState.Idle INSTANCE = null;
        
        private Idle() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0016"}, d2 = {"Lcom/app/fityo/dominio/CompareState$PhotoACaptured;", "Lcom/app/fityo/dominio/CompareState;", "photoPath", "", "viewType", "Lcom/app/fityo/dominio/ViewType;", "<init>", "(Ljava/lang/String;Lcom/app/fityo/dominio/ViewType;)V", "getPhotoPath", "()Ljava/lang/String;", "getViewType", "()Lcom/app/fityo/dominio/ViewType;", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "wear_debug"})
    public static final class PhotoACaptured extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String photoPath = null;
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ViewType viewType = null;
        
        public PhotoACaptured(@org.jetbrains.annotations.NotNull()
        java.lang.String photoPath, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhotoPath() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType getViewType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.PhotoACaptured copy(@org.jetbrains.annotations.NotNull()
        java.lang.String photoPath, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0019"}, d2 = {"Lcom/app/fityo/dominio/CompareState$PhotoBCaptured;", "Lcom/app/fityo/dominio/CompareState;", "photoAPath", "", "photoBPath", "viewType", "Lcom/app/fityo/dominio/ViewType;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcom/app/fityo/dominio/ViewType;)V", "getPhotoAPath", "()Ljava/lang/String;", "getPhotoBPath", "getViewType", "()Lcom/app/fityo/dominio/ViewType;", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "wear_debug"})
    public static final class PhotoBCaptured extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String photoAPath = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String photoBPath = null;
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.ViewType viewType = null;
        
        public PhotoBCaptured(@org.jetbrains.annotations.NotNull()
        java.lang.String photoAPath, @org.jetbrains.annotations.NotNull()
        java.lang.String photoBPath, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhotoAPath() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhotoBPath() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType getViewType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.ViewType component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.PhotoBCaptured copy(@org.jetbrains.annotations.NotNull()
        java.lang.String photoAPath, @org.jetbrains.annotations.NotNull()
        java.lang.String photoBPath, @org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.ViewType viewType) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/app/fityo/dominio/CompareState$Processing;", "Lcom/app/fityo/dominio/CompareState;", "<init>", "()V", "wear_debug"})
    public static final class Processing extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        public static final com.app.fityo.dominio.CompareState.Processing INSTANCE = null;
        
        private Processing() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/app/fityo/dominio/CompareState$ResultReady;", "Lcom/app/fityo/dominio/CompareState;", "result", "Lcom/app/fityo/dominio/CompareResult;", "<init>", "(Lcom/app/fityo/dominio/CompareResult;)V", "getResult", "()Lcom/app/fityo/dominio/CompareResult;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "wear_debug"})
    public static final class ResultReady extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        private final com.app.fityo.dominio.CompareResult result = null;
        
        public ResultReady(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.CompareResult result) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareResult getResult() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareResult component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.dominio.CompareState.ResultReady copy(@org.jetbrains.annotations.NotNull()
        com.app.fityo.dominio.CompareResult result) {
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
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/app/fityo/dominio/CompareState$SelectingViewType;", "Lcom/app/fityo/dominio/CompareState;", "<init>", "()V", "wear_debug"})
    public static final class SelectingViewType extends com.app.fityo.dominio.CompareState {
        @org.jetbrains.annotations.NotNull()
        public static final com.app.fityo.dominio.CompareState.SelectingViewType INSTANCE = null;
        
        private SelectingViewType() {
        }
    }
}