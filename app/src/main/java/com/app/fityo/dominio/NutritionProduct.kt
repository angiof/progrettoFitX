package com.app.fityo.dominio

data class NutritionProduct(
    val name: String,
    val imageUrl: String?,
    val macrosPer100g: MacroTotals
)
