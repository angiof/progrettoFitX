package com.app.fityo.dominio

import androidx.annotation.DrawableRes

data class ModelHomemenu(
    @DrawableRes val copertina: Int,
    val titolo: String,
    val action: HomeMenuAction
)

enum class HomeMenuAction {
    CREATE_SCHEDE,
    OPEN_SCHEDE,
    VIEW_STATS
}

