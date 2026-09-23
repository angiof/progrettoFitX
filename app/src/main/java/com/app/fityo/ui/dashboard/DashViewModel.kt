package com.app.fityo.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DashViewModel(
    private val repository: SchedeRepository,
    private val coachRepository: CoachProfileRepository?,
    application: Application
) : AndroidViewModel(application) {
    private val profileId = MutableStateFlow<Int?>(null)
    val selectedProfileId = profileId.asStateFlow()
    val selectedProfileName = MutableLiveData<String?>(null)
    val coachProfiles = MutableLiveData<List<CoachProfileEntity>>(emptyList())
    val summary = MutableLiveData(DashboardSummary())
    val loading = MutableLiveData(true)
    val error = MutableLiveData<String?>(null)
    val periodLabel = MutableLiveData("Ultimi 30 giorni")
    val selectedDays = MutableLiveData<Int?>(30)
    private var start: LocalDate? = LocalDate.now().minusDays(29)
    private var end: LocalDate? = LocalDate.now()
    private var refreshJob: Job? = null

    init { loadCoachProfiles(); refresh() }

    fun loadCoachProfiles() {
        viewModelScope.launch {
            try {
                coachProfiles.value = withContext(Dispatchers.IO) { coachRepository?.getAllProfilesSync().orEmpty() }
                if (profileId.value != null && coachProfiles.value.orEmpty().none { it.id == profileId.value }) {
                    setSelectedProfile(null, null)
                }
            } catch (ex: CancellationException) { throw ex }
            catch (_: Exception) { error.value = "Impossibile caricare i profili. Riprova." }
        }
    }

    fun setSelectedProfile(id: Int?, name: String?) {
        profileId.value = id
        selectedProfileName.value = name
        refresh()
    }

    fun selectPeriod(days: Int?) {
        selectedDays.value = days
        start = days?.let { LocalDate.now().minusDays(it.toLong() - 1) }
        end = days?.let { LocalDate.now() }
        periodLabel.value = days?.let { "Ultimi $it giorni" } ?: "Tutto il periodo"
        refresh()
    }

    fun refreshChartsForRange(start: String, end: String) {
        val from = parseDashboardDate(start) ?: return
        val to = parseDashboardDate(end) ?: return
        if (from > to) return
        this.start = from
        this.end = to
        selectedDays.value = -1
        val format = DateTimeFormatter.ofPattern("dd MMM yyyy")
        periodLabel.value = "${from.format(format)} – ${to.format(format)}"
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        val profile = profileId.value
        val from = start
        val to = end
        refreshJob = viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                summary.value = withContext(Dispatchers.IO) {
                    summarizeDashboard(repository.getAllSchede(), profile, from, to)
                }
            } catch (ex: CancellationException) { throw ex }
            catch (_: Exception) { error.value = "Impossibile caricare la dashboard. Riprova." }
            finally { loading.value = false }
        }
    }
}
