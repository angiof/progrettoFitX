package com.app.fityo.dominio

enum class NutritionSource {
    BARCODE,
    SEARCH,
    OCR,
    MANUAL
}

data class NutritionItem(
    val id: Int?,
    val name: String,
    val grams: Float,
    val macros: MacroTotals,
    val source: NutritionSource,
    val createdAt: Long
)
