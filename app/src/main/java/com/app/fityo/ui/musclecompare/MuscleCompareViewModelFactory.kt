package com.app.fityo.ui.musclecompare

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.repository.MuscleCompareRepository
import com.app.fityo.data_layer.repository.UserProfileRepository

class MuscleCompareViewModelFactory(
    private val application: Application,
    private val repository: MuscleCompareRepository,
    private val schedeDao: DaoSchede,
    private val userProfileRepository: UserProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MuscleCompareViewModel::class.java)) {
            return MuscleCompareViewModel(application, repository, schedeDao, userProfileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
