package com.app.fityo.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.data_layer.db.dao.DaoCoachProfile
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository

class DashViewModelFactory(
    private val daoSchede: DaoSchede,
    private val application: Application,
    private val coachProfileDao: DaoCoachProfile? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashViewModel::class.java)) {
            val coachRepo = coachProfileDao?.let { CoachProfileRepository(it) }
            @Suppress("UNCHECKED_CAST")
            return DashViewModel(SchedeRepository(daoSchede), coachRepo, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

