package com.app.fityo.ui.diet

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.repository.NutritionRepository
import com.app.fityo.dominio.MacroTotals
import com.app.fityo.dominio.NutritionItem
import com.app.fityo.dominio.NutritionProduct
import com.app.fityo.dominio.NutritionSource
import com.app.fityo.import_scheda.GemmaLlmHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DietSessionViewModel(
    application: Application,
    private val repository: NutritionRepository
) : AndroidViewModel(application) {

    data class DietUiState(
        val isLoading: Boolean = false,
        val isAdviceLoading: Boolean = false,
        val product: NutritionProduct? = null,
        val productSource: NutritionSource = NutritionSource.SEARCH,
        val dailyTotals: MacroTotals = MacroTotals(),
        val workoutSummary: String = "",
        val advice: String = "",
        val manualMacros: MacroTotals = MacroTotals(),
        val manualSource: NutritionSource = NutritionSource.MANUAL,
        val items: List<NutritionItem> = emptyList(),
        val errorMessage: String? = null,
        // Ricerca multipla
        val searchResults: List<NutritionProduct> = emptyList(),
        val showProductSelection: Boolean = false
    )

    private val gemma = GemmaLlmHelper.getInstance(application)

    private val _uiState = MutableStateFlow(DietUiState())
    val uiState: StateFlow<DietUiState> = _uiState.asStateFlow()

    init {
        refreshDailyTotals()
        refreshWorkoutSummary()
        refreshItems()
    }

    fun fetchProductByBarcode(barcode: String) {
        if (barcode.isBlank()) {
            emitError("Barcode non valido")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            val result = repository.fetchProductByBarcode(barcode)
            result.onSuccess { product ->
                _uiState.value = _uiState.value.copy(
                    product = product,
                    productSource = NutritionSource.BARCODE,
                    advice = ""
                )
            }.onFailure {
                emitError(it.message ?: "Prodotto non trovato")
            }
            setLoading(false)
        }
    }

    fun searchProduct(query: String) {
        if (query.isBlank()) {
            emitError("Inserisci un nome prodotto")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            val result = repository.searchProducts(query)
            result.onSuccess { products ->
                if (products.size == 1) {
                    // Se c'e solo un risultato, selezionalo direttamente
                    _uiState.value = _uiState.value.copy(
                        product = products.first(),
                        productSource = NutritionSource.SEARCH,
                        searchResults = emptyList(),
                        showProductSelection = false,
                        advice = ""
                    )
                } else {
                    // Mostra dialog di selezione
                    _uiState.value = _uiState.value.copy(
                        product = null,
                        productSource = NutritionSource.SEARCH,
                        searchResults = products,
                        showProductSelection = true,
                        advice = ""
                    )
                }
            }.onFailure {
                emitError(it.message ?: "Prodotto non trovato")
            }
            setLoading(false)
        }
    }

    fun selectProduct(product: NutritionProduct) {
        _uiState.value = _uiState.value.copy(
            product = product,
            productSource = NutritionSource.SEARCH,
            searchResults = emptyList(),
            showProductSelection = false,
            advice = ""
        )
    }

    fun dismissProductSelection() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            showProductSelection = false
        )
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            val updated = repository.deleteNutritionItem(itemId, today())
            _uiState.value = _uiState.value.copy(dailyTotals = updated, advice = "")
            refreshItems()
        }
    }

    fun addProductToDaily(quantityGrams: Float) {
        val product = _uiState.value.product
        if (product == null) {
            emitError("Nessun prodotto selezionato")
            return
        }
        val qty = if (quantityGrams > 0f) quantityGrams else 100f
        val factor = qty / 100f
        val macros = product.macrosPer100g.scaledBy(factor)
        val source = _uiState.value.productSource
        viewModelScope.launch {
            val updated = repository.addNutritionItem(
                date = today(),
                name = product.name,
                grams = qty,
                macros = macros,
                source = source
            )
            _uiState.value = _uiState.value.copy(dailyTotals = updated, advice = "")
            refreshItems()
        }
    }

    fun saveManualMacros(macros: MacroTotals, grams: Float) {
        val source = _uiState.value.manualSource
        val name = if (source == NutritionSource.OCR) "OCR" else "Manuale"
        val qty = if (grams > 0f) grams else 100f
        val factor = qty / 100f
        val scaledMacros = macros.scaledBy(factor)
        viewModelScope.launch {
            val updated = repository.addNutritionItem(
                date = today(),
                name = name,
                grams = qty,
                macros = scaledMacros,
                source = source
            )
            _uiState.value = _uiState.value.copy(
                dailyTotals = updated,
                manualMacros = macros,
                manualSource = NutritionSource.MANUAL,
                advice = ""
            )
            refreshItems()
        }
    }

    fun updateManualMacros(macros: MacroTotals) {
        _uiState.value = _uiState.value.copy(manualMacros = macros)
    }

    fun generateAdvice() {
        viewModelScope.launch {
            val totals = _uiState.value.dailyTotals
            val workout = _uiState.value.workoutSummary.ifBlank { "Riposo" }
            if (!gemma.isModelAvailable()) {
                _uiState.value = _uiState.value.copy(advice = "Gemma non disponibile")
                return@launch
            }
            setAdviceLoading(true)
            val prompt = buildAdvicePrompt(
                workoutType = workout,
                totalProteins = totals.proteins,
                totalCarbs = totals.carbs
            )
            val result = gemma.generateResponse(prompt, GemmaLlmHelper.GemmaProfile.DIET)
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(advice = response.trim())
            }.onFailure { error ->
                emitError(error.message ?: "Errore Gemma")
            }
            setAdviceLoading(false)
        }
    }

    fun processOcr(bitmap: Bitmap) {
        viewModelScope.launch {
            setLoading(true)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            val text = result.text
            val macros = parseMacrosFromText(text)
            if (macros != null) {
                _uiState.value = _uiState.value.copy(
                    manualMacros = macros,
                    manualSource = NutritionSource.OCR
                )
            } else {
                emitError("Valori nutrizionali non trovati")
            }
            setLoading(false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun refreshDailyTotals() {
        viewModelScope.launch {
            val totals = repository.getDailyTotals(today())
            _uiState.value = _uiState.value.copy(dailyTotals = totals)
        }
    }

    private fun refreshWorkoutSummary() {
        viewModelScope.launch {
            val summary = repository.getWorkoutSummary(today()) ?: ""
            _uiState.value = _uiState.value.copy(workoutSummary = summary)
        }
    }

    private fun refreshItems() {
        viewModelScope.launch {
            val items = repository.getItemsForDate(today())
            _uiState.value = _uiState.value.copy(items = items)
        }
    }

    private fun today(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    private fun setAdviceLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isAdviceLoading = isLoading)
    }

    private fun emitError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    private fun buildAdvicePrompt(
        workoutType: String,
        totalProteins: Float,
        totalCarbs: Float
    ): String {
        return """
            <start_of_turn>user
            RUOLO: Coach Nutrizionale Sportivo.
            DATI ODIERNI:
            - Allenamento: $workoutType
            - Proteine assunte: ${totalProteins}g
            - Carboidrati assunti: ${totalCarbs}g

            ANALISI:
            Valuta se questi macro sono sufficienti per sostenere l'allenamento e il recupero.
            Sii tecnico ma motivante. Massimo 30 parole.
            <end_of_turn>
            <start_of_turn>model
        """.trimIndent()
    }

    private fun parseMacrosFromText(text: String): MacroTotals? {
        val proteins = findMacroValue(text, listOf("proteine", "protein"))
        val carbs = findMacroValue(text, listOf("carboidrati", "carbs", "carbohydrate"))
        val fats = findMacroValue(text, listOf("grassi", "fat"))
        val kcal = findKcalValue(text)

        if (proteins == null && carbs == null && fats == null && kcal == null) return null

        return MacroTotals(
            proteins = proteins ?: 0f,
            carbs = carbs ?: 0f,
            fats = fats ?: 0f,
            kcal = kcal ?: 0f
        )
    }

    private fun findMacroValue(text: String, keywords: List<String>): Float? {
        val pattern = keywords.joinToString("|") { Regex.escape(it) }
        val regex = Regex("""(?i)(?:$pattern)[^0-9]*([0-9]+(?:[.,][0-9]+)?)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toFloatSafe()
    }

    private fun findKcalValue(text: String): Float? {
        val kcalRegex = Regex("""(?i)(?:kcal|calorie)[^0-9]*([0-9]+(?:[.,][0-9]+)?)""")
        val kcalMatch = kcalRegex.find(text)
        if (kcalMatch != null) {
            return kcalMatch.groupValues.getOrNull(1)?.toFloatSafe()
        }
        val energyRegex = Regex("""(?i)(?:energia|energy)[^0-9]*([0-9]+(?:[.,][0-9]+)?)\s*k?j""")
        val energyMatch = energyRegex.find(text)
        if (energyMatch != null) {
            val kj = energyMatch.groupValues.getOrNull(1)?.toFloatSafe() ?: return null
            return kj / 4.184f
        }
        return null
    }

    private fun String.toFloatSafe(): Float? {
        val normalized = trim().replace(',', '.')
        return normalized.toFloatOrNull()
    }
}

class DietSessionViewModelFactory(
    private val application: Application,
    private val repository: NutritionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietSessionViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
