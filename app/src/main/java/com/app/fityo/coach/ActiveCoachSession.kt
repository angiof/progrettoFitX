package com.app.fityo.coach

import com.app.fityo.data_layer.db.CoachProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton per gestire la sessione coach attiva.
 * Usato per filtrare le schede quando l'utente e in "modalita coach".
 */
object ActiveCoachSession {
    private val _activeProfile = MutableStateFlow<CoachProfileEntity?>(null)
    val activeProfile: StateFlow<CoachProfileEntity?> = _activeProfile.asStateFlow()

    /**
     * Verifica se la modalita coach e attiva.
     */
    val isCoachModeActive: Boolean
        get() = _activeProfile.value != null

    /**
     * Ottiene l'ID del profilo attivo, se presente.
     */
    val activeProfileId: Int?
        get() = _activeProfile.value?.id

    /**
     * Ottiene il nome del profilo attivo, se presente.
     */
    val activeProfileName: String?
        get() = _activeProfile.value?.name

    /**
     * Entra in modalita coach con il profilo specificato.
     */
    fun enterCoachMode(profile: CoachProfileEntity) {
        _activeProfile.value = profile
    }

    /**
     * Esce dalla modalita coach.
     */
    fun exitCoachMode() {
        _activeProfile.value = null
    }

    /**
     * Aggiorna il profilo attivo (es. dopo una modifica).
     */
    fun updateActiveProfile(profile: CoachProfileEntity) {
        if (_activeProfile.value?.id == profile.id) {
            _activeProfile.value = profile
        }
    }
}
