package com.app.fityo.ui.coach

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.data_layer.db.dao.DaoCoachAppointment
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.repository.CoachProfileRepository

class CoachViewModelFactory(
    private val application: Application,
    private val coachRepository: CoachProfileRepository,
    private val schedeDao: DaoSchede,
    private val appointmentDao: DaoCoachAppointment? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoachViewModel::class.java)) {
            return CoachViewModel(application, coachRepository, schedeDao, appointmentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
