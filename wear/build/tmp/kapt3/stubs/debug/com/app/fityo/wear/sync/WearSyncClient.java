package com.app.fityo.wear.sync;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000  2\u00020\u0001:\u0001 B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012JI\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00102\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00102\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\u0002\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0015H\u0002J\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001dH\u0082@\u00a2\u0006\u0002\u0010\u001fR\u000e\u0010\u0006\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/app/fityo/wear/sync/WearSyncClient;", "", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "app", "messageClient", "Lcom/google/android/gms/wearable/MessageClient;", "capabilityClient", "Lcom/google/android/gms/wearable/CapabilityClient;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "sendExerciseCompletion", "", "id", "", "completed", "", "sendSchedaCompletion", "completedDate", "", "totalSteps", "avgHeartRate", "maxHeartRate", "(IZLjava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V", "sendToAllNodes", "payload", "getConnectedNodes", "", "Lcom/google/android/gms/wearable/Node;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "wear_debug"})
public final class WearSyncClient {
    @org.jetbrains.annotations.NotNull()
    private final android.app.Application app = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.android.gms.wearable.MessageClient messageClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.android.gms.wearable.CapabilityClient capabilityClient = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SYNC_PATH = "/fityo/sync";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CAPABILITY_SYNC = "fityo_sync";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG = "WearSyncClient";
    @org.jetbrains.annotations.NotNull()
    public static final com.app.fityo.wear.sync.WearSyncClient.Companion Companion = null;
    
    public WearSyncClient(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super();
    }
    
    public final void sendExerciseCompletion(int id, boolean completed) {
    }
    
    public final void sendSchedaCompletion(int id, boolean completed, @org.jetbrains.annotations.Nullable()
    java.lang.String completedDate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer totalSteps, @org.jetbrains.annotations.Nullable()
    java.lang.Integer avgHeartRate, @org.jetbrains.annotations.Nullable()
    java.lang.Integer maxHeartRate) {
    }
    
    private final void sendToAllNodes(java.lang.String payload) {
    }
    
    private final java.lang.Object getConnectedNodes(kotlin.coroutines.Continuation<? super java.util.Set<? extends com.google.android.gms.wearable.Node>> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/app/fityo/wear/sync/WearSyncClient$Companion;", "", "<init>", "()V", "SYNC_PATH", "", "CAPABILITY_SYNC", "TAG", "wear_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}