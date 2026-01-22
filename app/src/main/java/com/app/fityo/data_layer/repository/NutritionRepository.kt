package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.DailyNutritionEntity
import com.app.fityo.data_layer.db.DailyNutritionItemEntity
import com.app.fityo.data_layer.db.dao.DaoDailyNutrition
import com.app.fityo.data_layer.db.dao.DaoDailyNutritionItem
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.network.OpenFoodFactsProduct
import com.app.fityo.data_layer.network.OpenFoodFactsService
import com.app.fityo.dominio.MacroTotals
import com.app.fityo.dominio.NutritionItem
import com.app.fityo.dominio.NutritionProduct
import com.app.fityo.dominio.NutritionSource
import java.util.Locale

class NutritionRepository(
    private val api: OpenFoodFactsService,
    private val dailyDao: DaoDailyNutrition,
    private val itemsDao: DaoDailyNutritionItem,
    private val schedeDao: DaoSchede
) {

    /**
     * Cerca prodotto per barcode.
     */
    suspend fun fetchProductByBarcode(barcode: String): Result<NutritionProduct> {
        return runCatching {
            val response = api.getProduct(barcode = barcode.trim())
            val product = response.product
            if (response.status != 1 || product?.nutriments == null) {
                throw IllegalStateException("Prodotto non trovato")
            }
            mapProduct(product)
        }
    }

    /**
     * Cerca prodotti per nome - ritorna lista multipla.
     */
    suspend fun searchProducts(query: String): Result<List<NutritionProduct>> {
        return runCatching {
            val response = api.searchProducts(query = query.trim())
            val products = response.products
                ?.filter { it.nutriments != null }
                ?.map { mapProduct(it) }
                ?: emptyList()

            if (products.isEmpty()) {
                throw IllegalStateException("Nessun prodotto trovato")
            }
            products
        }
    }

    /**
     * Cerca prodotto singolo (primo risultato) - retrocompatibilità.
     */
    suspend fun searchProduct(query: String): Result<NutritionProduct> {
        return searchProducts(query).map { it.first() }
    }

    /**
     * Aggiunge un item nutrizionale alla giornata.
     */
    suspend fun addNutritionItem(
        date: String,
        name: String,
        grams: Float,
        macros: MacroTotals,
        source: NutritionSource
    ): MacroTotals {
        itemsDao.insert(
            DailyNutritionItemEntity(
                date = date,
                name = name,
                grams = grams,
                proteins = macros.proteins,
                carbs = macros.carbs,
                fats = macros.fats,
                kcal = macros.kcal,
                fibers = macros.fibers,
                sugars = macros.sugars,
                saturatedFats = macros.saturatedFats,
                salt = macros.salt,
                source = source.name,
                createdAt = System.currentTimeMillis()
            )
        )
        return recalculateDailyTotals(date)
    }

    /**
     * Elimina un item e ricalcola i totali.
     */
    suspend fun deleteNutritionItem(itemId: Int, date: String): MacroTotals {
        itemsDao.deleteById(itemId)
        return recalculateDailyTotals(date)
    }

    /**
     * Ricalcola i totali giornalieri sommando tutti gli items.
     */
    private suspend fun recalculateDailyTotals(date: String): MacroTotals {
        val items = itemsDao.getByDate(date)
        val totals = items.fold(MacroTotals()) { acc, item ->
            acc + MacroTotals(
                proteins = item.proteins,
                carbs = item.carbs,
                fats = item.fats,
                kcal = item.kcal,
                fibers = item.fibers,
                sugars = item.sugars,
                saturatedFats = item.saturatedFats,
                salt = item.salt
            )
        }

        val existing = dailyDao.getByDate(date)
        dailyDao.upsert(
            DailyNutritionEntity(
                id = existing?.id,
                date = date,
                totalProteins = totals.proteins,
                totalCarbs = totals.carbs,
                totalFats = totals.fats,
                totalKcal = totals.kcal,
                totalFibers = totals.fibers,
                totalSugars = totals.sugars,
                totalSaturatedFats = totals.saturatedFats,
                totalSalt = totals.salt,
                updatedAt = System.currentTimeMillis()
            )
        )
        return totals
    }

    /**
     * Ottiene gli items per una data.
     */
    suspend fun getItemsForDate(date: String): List<NutritionItem> {
        return itemsDao.getByDate(date).map { it.toNutritionItem() }
    }

    /**
     * Ottiene i totali giornalieri.
     */
    suspend fun getDailyTotals(date: String): MacroTotals {
        return dailyDao.getByDate(date)?.toMacroTotals() ?: MacroTotals()
    }

    /**
     * Ottiene il riepilogo allenamento del giorno.
     */
    suspend fun getWorkoutSummary(date: String): String? {
        val schede = schedeDao.getSchedeByDate(date)
        if (schede.isEmpty()) return null
        val titles = schede.map {
            val title = it.titolo.trim()
            if (title.isNotBlank()) title else it.getGruppiMuscolariDisplay()
        }
        return titles.distinct().joinToString(" / ")
    }

    /**
     * Mappa prodotto OpenFoodFacts a NutritionProduct.
     */
    private fun mapProduct(product: OpenFoodFactsProduct): NutritionProduct {
        val nutriments = product.nutriments

        // Calcola kcal (preferisci kcal, altrimenti converti da kJ)
        val kcal = nutriments?.energyKcal ?: nutriments?.energy?.let { energyKj ->
            (energyKj / 4.184f)
        } ?: 0f

        // Calcola sale (se non presente, converti da sodio: sale = sodio * 2.5)
        val salt = nutriments?.salt ?: nutriments?.sodium?.let { it * 2.5f } ?: 0f

        val macros = MacroTotals(
            proteins = nutriments?.proteins ?: 0f,
            carbs = nutriments?.carbs ?: 0f,
            fats = nutriments?.fats ?: 0f,
            kcal = kcal,
            fibers = nutriments?.fibers ?: 0f,
            sugars = nutriments?.sugars ?: 0f,
            saturatedFats = nutriments?.saturatedFats ?: 0f,
            salt = salt
        )

        // Preferisci nome italiano, altrimenti nome generico
        val name = product.productNameIt?.takeIf { it.isNotBlank() }
            ?: product.productName?.takeIf { it.isNotBlank() }
            ?: "Prodotto"

        // Aggiungi marca se disponibile
        val displayName = if (!product.brands.isNullOrBlank()) {
            "$name (${product.brands})"
        } else {
            name
        }.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        return NutritionProduct(
            name = displayName,
            imageUrl = product.imageUrl ?: product.imageSmallUrl,
            macrosPer100g = macros
        )
    }

    private fun DailyNutritionEntity.toMacroTotals(): MacroTotals {
        return MacroTotals(
            proteins = totalProteins,
            carbs = totalCarbs,
            fats = totalFats,
            kcal = totalKcal,
            fibers = totalFibers,
            sugars = totalSugars,
            saturatedFats = totalSaturatedFats,
            salt = totalSalt
        )
    }

    private fun DailyNutritionItemEntity.toNutritionItem(): NutritionItem {
        val sourceEnum = runCatching { NutritionSource.valueOf(source) }
            .getOrElse { NutritionSource.MANUAL }
        return NutritionItem(
            id = id,
            name = name,
            grams = grams,
            macros = MacroTotals(
                proteins = proteins,
                carbs = carbs,
                fats = fats,
                kcal = kcal,
                fibers = fibers,
                sugars = sugars,
                saturatedFats = saturatedFats,
                salt = salt
            ),
            source = sourceEnum,
            createdAt = createdAt
        )
    }
}
