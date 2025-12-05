package com.app.fityo.wear.sensors;

/**
 * Gestisce i sensori fitness su Wear OS:
 * - Contapassi (step counter)
 * - Frequenza cardiaca (heart rate)
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001b\u001a\u00020\u001cJ\u0006\u0010\u001d\u001a\u00020\u001cJ\u0010\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 H\u0016J\u0018\u0010!\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\t2\u0006\u0010#\u001a\u00020\u0017H\u0016J\u0006\u0010$\u001a\u00020%J\u0006\u0010&\u001a\u00020\u001cR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/app/fityo/wear/sensors/FitnessSensorManager;", "Landroid/hardware/SensorEventListener;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "sensorManager", "Landroid/hardware/SensorManager;", "stepCounterSensor", "Landroid/hardware/Sensor;", "heartRateSensor", "_stepsData", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/app/fityo/wear/sensors/StepsData;", "stepsData", "Lkotlinx/coroutines/flow/StateFlow;", "getStepsData", "()Lkotlinx/coroutines/flow/StateFlow;", "_heartRateData", "Lcom/app/fityo/wear/sensors/HeartRateData;", "heartRateData", "getHeartRateData", "initialStepCount", "", "Ljava/lang/Integer;", "isTracking", "", "startTracking", "", "stopTracking", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "onAccuracyChanged", "sensor", "accuracy", "areSensorsAvailable", "Lcom/app/fityo/wear/sensors/SensorAvailability;", "release", "wear_debug"})
public final class FitnessSensorManager implements android.hardware.SensorEventListener {
    @org.jetbrains.annotations.NotNull()
    private final android.hardware.SensorManager sensorManager = null;
    @org.jetbrains.annotations.Nullable()
    private final android.hardware.Sensor stepCounterSensor = null;
    @org.jetbrains.annotations.Nullable()
    private final android.hardware.Sensor heartRateSensor = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.app.fityo.wear.sensors.StepsData> _stepsData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.StepsData> stepsData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.app.fityo.wear.sensors.HeartRateData> _heartRateData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.HeartRateData> heartRateData = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer initialStepCount;
    private boolean isTracking = false;
    
    public FitnessSensorManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.StepsData> getStepsData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.app.fityo.wear.sensors.HeartRateData> getHeartRateData() {
        return null;
    }
    
    /**
     * Avvia il monitoraggio dei sensori
     */
    public final void startTracking() {
    }
    
    /**
     * Ferma il monitoraggio dei sensori
     */
    public final void stopTracking() {
    }
    
    @java.lang.Override()
    public void onSensorChanged(@org.jetbrains.annotations.NotNull()
    android.hardware.SensorEvent event) {
    }
    
    @java.lang.Override()
    public void onAccuracyChanged(@org.jetbrains.annotations.NotNull()
    android.hardware.Sensor sensor, int accuracy) {
    }
    
    /**
     * Verifica se i sensori sono disponibili
     */
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.wear.sensors.SensorAvailability areSensorsAvailable() {
        return null;
    }
    
    /**
     * Rilascia le risorse
     */
    public final void release() {
    }
}