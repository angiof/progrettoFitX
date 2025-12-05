package com.app.fityo.wear.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestisce i sensori fitness su Wear OS:
 * - Contapassi (step counter)
 * - Frequenza cardiaca (heart rate)
 */
class FitnessSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    private val _stepsData = MutableStateFlow(StepsData())
    val stepsData: StateFlow<StepsData> = _stepsData.asStateFlow()

    private val _heartRateData = MutableStateFlow(HeartRateData())
    val heartRateData: StateFlow<HeartRateData> = _heartRateData.asStateFlow()

    private var initialStepCount: Int? = null
    private var isTracking = false

    /**
     * Avvia il monitoraggio dei sensori
     */
    fun startTracking() {
        if (isTracking) return
        isTracking = true
        initialStepCount = null

        // Registra listener per contapassi (aggiornamento ogni 0.5 secondi)
        stepCounterSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Registra listener per frequenza cardiaca (aggiornamento ogni 1 secondo)
        heartRateSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Reset dati
        _stepsData.value = StepsData()
        _heartRateData.value = HeartRateData()
    }

    /**
     * Ferma il monitoraggio dei sensori
     */
    fun stopTracking() {
        if (!isTracking) return
        isTracking = false
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSteps = event.values[0].toInt()

                // Prima lettura: salva il valore iniziale
                if (initialStepCount == null) {
                    initialStepCount = totalSteps
                }

                // Calcola i passi dall'inizio dell'allenamento
                val workoutSteps = totalSteps - (initialStepCount ?: 0)
                _stepsData.value = StepsData(
                    currentSteps = workoutSteps,
                    isAvailable = true
                )
            }

            Sensor.TYPE_HEART_RATE -> {
                val bpm = event.values[0].toInt()
                if (bpm > 0) { // Ignora valori zero (nessun contatto con sensore)
                    val current = _heartRateData.value
                    val newMax = maxOf(current.maxBpm, bpm)

                    // Calcola media progressiva
                    val newCount = current.sampleCount + 1
                    val newAvg = ((current.avgBpm * current.sampleCount) + bpm) / newCount

                    _heartRateData.value = HeartRateData(
                        currentBpm = bpm,
                        avgBpm = newAvg,
                        maxBpm = newMax,
                        sampleCount = newCount,
                        isAvailable = true
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Non utilizzato per questi sensori
    }

    /**
     * Verifica se i sensori sono disponibili
     */
    fun areSensorsAvailable(): SensorAvailability {
        return SensorAvailability(
            stepsAvailable = stepCounterSensor != null,
            heartRateAvailable = heartRateSensor != null
        )
    }

    /**
     * Rilascia le risorse
     */
    fun release() {
        stopTracking()
    }
}

data class StepsData(
    val currentSteps: Int = 0,
    val isAvailable: Boolean = false
)

data class HeartRateData(
    val currentBpm: Int = 0,
    val avgBpm: Int = 0,
    val maxBpm: Int = 0,
    val sampleCount: Int = 0,
    val isAvailable: Boolean = false
)

data class SensorAvailability(
    val stepsAvailable: Boolean,
    val heartRateAvailable: Boolean
)
