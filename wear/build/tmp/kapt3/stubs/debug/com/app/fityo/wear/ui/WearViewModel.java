package com.app.fityo.wear.ui;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001f\u001a\u00020 J\u000e\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#J\u0016\u0010$\u001a\u00020 2\u0006\u0010%\u001a\u00020#2\u0006\u0010&\u001a\u00020\'J\u000e\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020#J\u0019\u0010*\u001a\u00020 2\n\b\u0002\u0010+\u001a\u0004\u0018\u00010#H\u0002\u00a2\u0006\u0002\u0010,J\b\u0010-\u001a\u00020 H\u0016J\u0006\u0010.\u001a\u00020 R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018\u00a8\u0006/"}, d2 = {"Lcom/app/fityo/wear/ui/WearViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "db", "Lcom/app/fityo/data_layer/db/DB/DbFit;", "getDb", "()Lcom/app/fityo/data_layer/db/DB/DbFit;", "db$delegate", "Lkotlin/Lazy;", "syncClient", "Lcom/app/fityo/wear/sync/WearSyncClient;", "sensorManager", "Lcom/app/fityo/wear/sensors/FitnessSensorManager;", "dataSyncManager", "Lcom/app/fityo/wear/sync/WearDataSyncManager;", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/app/fityo/wear/ui/WearUiState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "stepsData", "Lcom/app/fityo/wear/sensors/StepsData;", "getStepsData", "heartRateData", "Lcom/app/fityo/wear/sensors/HeartRateData;", "getHeartRateData", "onPermissionsGranted", "", "selectScheda", "id", "", "toggleExercise", "exerciseId", "completed", "", "completeScheda", "schedaId", "refreshSchede", "selectedId", "(Ljava/lang/Integer;)V", "onCleared", "forceSync", "wear_debug"})
public final class WearViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy db$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.wear.sync.WearSyncClient syncClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.wear.sensors.FitnessSensorManager sensorManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.app.fityo.wear.sync.WearDataSyncManager dataSyncManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.app.fityo.wear.ui.WearUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.ui.WearUiState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.StepsData> stepsData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.HeartRateData> heartRateData = null;
    
    public WearViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    private final com.app.fityo.data_layer.db.DB.DbFit getDb() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.ui.WearUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.StepsData> getStepsData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.HeartRateData> getHeartRateData() {
        return null;
    }
    
    public final void onPermissionsGranted() {
    }
    
    public final void selectScheda(int id) {
    }
    
    public final void toggleExercise(int exerciseId, boolean completed) {
    }
    
    public final void completeScheda(int schedaId) {
    }
    
    private final void refreshSchede(java.lang.Integer selectedId) {
    }
    
    @java.lang.Override()
    public void onCleared() {
    }
    
    public final void forceSync() {
    }
}