package com.app.progrettofitx.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.progrettofitx.data_layer.db.dao.DaoSchede
import com.app.progrettofitx.ui.shedeForms.SchedeRepository

class DashViewModelFactory(
    private val daoSchede: DaoSchede,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashViewModel(SchedeRepository(daoSchede), application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
