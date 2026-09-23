package com.app.fityo.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.UserProfileEntity
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class AnalyticsUiState {
    object Loading : AnalyticsUiState()
    object Empty : AnalyticsUiState()
    data class Error(val message: String) : AnalyticsUiState()
    data class Success(
        val insights: WorkoutInsights,
        val volumes: List<Float>,
        val intensities: List<Float>,
        val sessionStats: SessionStats,
        val muscleDistribution: List<GruppoMuscolarePercentuale> = emptyList(),
        val profileName: String? = null
    ) : AnalyticsUiState()
}

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DbFit.getDatabase(application)
    private val analyzer: WorkoutAnalyticsEngine = AnalyticsEngineProvider.create(application)

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    // Profilo selezionato per filtro
    private var selectedProfileId: Int? = null
    private var selectedProfileName: String? = null

    init {
        loadAnalytics()
    }

    fun setSelectedProfile(profileId: Int?, profileName: String?) {
        selectedProfileId = profileId
        selectedProfileName = profileName
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = AnalyticsUiState.Loading

            try {
                withContext(Dispatchers.IO) {
                    // Carica tutte le schede
                    val allSchede = db.schedeDao().getAllSchede()

                    // Filtra per profilo se selezionato
                    val schede = if (selectedProfileId != null) {
                        allSchede.filter { it.coachProfileId == selectedProfileId }
                    } else {
                        allSchede
                    }

                    val esercizi = mutableListOf<EsserciziEntity>()
                    schede.forEach { scheda ->
                        scheda.id?.let { id ->
                            esercizi.addAll(db.essercissiDao().getEserciziByschedaIdSync(id))
                        }
                    }

                    if (schede.isEmpty()) {
                        _uiState.value = AnalyticsUiState.Empty
                        return@withContext
                    }

                    val orderedSchede = sortSchedeByDate(schede)
                    val profile = db.userProfileDao().getActiveProfile()

                    // Calcola distribuzione muscolare per profilo
                    val muscleDistribution = if (selectedProfileId != null) {
                        db.schedeDao().getPercentualeByCoachProfile(selectedProfileId!!)
                    } else {
                        db.schedeDao().getPercentualePerGruppoMuscolare()
                    }

                    val insights = analyzer.analyze(orderedSchede, esercizi, profile, muscleDistribution)
                    val volumes = calculateVolumes(orderedSchede, esercizi)
                    val intensities = calculateIntensities(orderedSchede)
                    val sessionStats = calculateSessionStats(orderedSchede, esercizi, muscleDistribution)

                    _uiState.value = AnalyticsUiState.Success(
                        insights = insights,
                        volumes = volumes,
                        intensities = intensities,
                        sessionStats = sessionStats,
                        muscleDistribution = muscleDistribution,
                        profileName = selectedProfileName
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    private fun calculateVolumes(schede: List<SchedeEntity>, esercizi: List<EsserciziEntity>): List<Float> {
        return schede.takeLast(15).map { scheda ->
            esercizi.filter { it.schedaId == scheda.id }
                .sumOf { (it.nSerie * it.nRipetizione * (it.peso ?: 1f)).toDouble() }
                .toFloat()
        }
    }

    private fun calculateIntensities(schede: List<SchedeEntity>): List<Float> {
        return schede.takeLast(15).map {
            when (it.intesita.lowercase()) {
                "alta" -> 1f
                "media" -> 0.66f
                else -> 0.33f
            }
        }
    }

    private fun calculateSessionStats(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        distribution: List<GruppoMuscolarePercentuale>
    ): SessionStats {
        return try {
            val totalVolume = esercizi.sumOf {
                ((it.nSerie.coerceAtLeast(0) * it.nRipetizione.coerceAtLeast(0) * (it.peso ?: 1f).coerceAtLeast(0f))).toDouble()
            }.toFloat()

            val avgIntensity = if (schede.isNotEmpty()) {
                schede.map {
                    when (it.intesita.lowercase()) { "alta" -> 1f; "media" -> 0.66f; else -> 0.33f }
                }.average().toFloat()
            } else 0.5f

            val weeklyFrequency = if (schede.isNotEmpty()) {
                val days = schede.size.toFloat() / 4f
                days.coerceIn(0f, 7f)
            } else 0f

            val completed = schede.count { it.completed }
            val consistencyScore = if (schede.isNotEmpty()) {
                (completed.toFloat() / schede.size * 100).coerceIn(0f, 100f)
            } else 0f

            val strongestMuscle = distribution.maxByOrNull { it.percentuale }?.gruppoMuscolare
            val weakestMuscle = distribution.filter { it.percentuale > 0 }.minByOrNull { it.percentuale }?.gruppoMuscolare

            SessionStats(
                totalWorkouts = schede.size,
                totalVolume = totalVolume,
                avgIntensity = avgIntensity.takeIf { !it.isNaN() } ?: 0.5f,
                weeklyFrequency = weeklyFrequency,
                consistencyScore = consistencyScore,
                streakDays = calculateStreak(schede),
                strongestMuscle = strongestMuscle,
                weakestMuscle = weakestMuscle
            )
        } catch (e: Exception) {
            // Fallback sicuro
            SessionStats(
                totalWorkouts = schede.size,
                totalVolume = 0f,
                avgIntensity = 0.5f,
                weeklyFrequency = 0f,
                consistencyScore = 0f,
                streakDays = 0,
                strongestMuscle = null,
                weakestMuscle = null
            )
        }
    }

    private fun calculateStreak(schede: List<SchedeEntity>): Int {
        // Semplificato: conta allenamenti negli ultimi 7 giorni
        return schede.takeLast(7).count { it.completed }
    }

    private fun sortSchedeByDate(schede: List<SchedeEntity>): List<SchedeEntity> {
        if (schede.isEmpty()) return schede
        return schede.mapIndexed { index, scheda -> index to scheda }
            .sortedWith(
                compareBy<Pair<Int, SchedeEntity>>(
                    { parseLocalDate(it.second.data) ?: LocalDate.MIN },
                    { it.first }
                )
            )
            .map { it.second }
    }

    private fun parseLocalDate(value: String): LocalDate? {
        if (value.isBlank()) return null
        val datePart = value.trim().let { if (it.length >= 10) it.substring(0, 10) else it }
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        )
        for (formatter in formats) {
            try {
                return LocalDate.parse(datePart, formatter)
            } catch (_: Exception) {
            }
        }
        return null
    }
}
