package com.app.fityo.dominio

/**
 * Dati nutrizionali completi per 100g o per porzione.
 */
data class MacroTotals(
    val proteins: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f,
    val kcal: Float = 0f,
    // Campi aggiuntivi
    val fibers: Float = 0f,
    val sugars: Float = 0f,
    val saturatedFats: Float = 0f,
    val salt: Float = 0f
) {
    operator fun plus(other: MacroTotals): MacroTotals {
        return MacroTotals(
            proteins = proteins + other.proteins,
            carbs = carbs + other.carbs,
            fats = fats + other.fats,
            kcal = kcal + other.kcal,
            fibers = fibers + other.fibers,
            sugars = sugars + other.sugars,
            saturatedFats = saturatedFats + other.saturatedFats,
            salt = salt + other.salt
        )
    }

    fun scaledBy(factor: Float): MacroTotals {
        return MacroTotals(
            proteins = proteins * factor,
            carbs = carbs * factor,
            fats = fats * factor,
            kcal = kcal * factor,
            fibers = fibers * factor,
            sugars = sugars * factor,
            saturatedFats = saturatedFats * factor,
            salt = salt * factor
        )
    }
}
