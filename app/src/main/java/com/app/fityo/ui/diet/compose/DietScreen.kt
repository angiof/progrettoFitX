package com.app.fityo.ui.diet.compose

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fityo.dominio.MacroTotals
import com.app.fityo.dominio.NutritionItem
import com.app.fityo.dominio.NutritionProduct
import com.app.fityo.dominio.NutritionSource
import com.app.fityo.ui.diet.DietSessionViewModel

/**
 * Schermata principale Dieta Intelligente in Compose.
 * Gestisce scanner barcode, ricerca, OCR, inserimento manuale e consiglio Gemma.
 */
@Composable
fun DietScreen(
    viewModel: DietSessionViewModel,
    onBack: () -> Unit,
    onScanBarcode: () -> Unit,
    onScanOcr: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Stati locali per input
    var searchQuery by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("100") }
    var manualGrams by remember { mutableStateOf("100") }

    // Snackbar per errori
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostra errori come snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    DietTheme {
        Scaffold(
            topBar = {
                DietTopBar(
                    workoutSummary = uiState.workoutSummary,
                    onBack = onBack
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = DarkBackground
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Totali giornalieri
                    DailyTotalsCard(totals = uiState.dailyTotals)

                    // Sezione scanner e ricerca
                    ScanSearchSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onScanBarcode = onScanBarcode,
                        onSearch = {
                            viewModel.searchProduct(searchQuery)
                            searchQuery = ""
                        },
                        onOcrScan = onScanOcr,
                        isLoading = uiState.isLoading
                    )

                    // Card prodotto trovato
                    uiState.product?.let { product ->
                        ProductCard(
                            product = product,
                            quantity = quantity,
                            onQuantityChange = { quantity = it },
                            onAddToDaily = {
                                val qty = quantity.replace(',', '.').toFloatOrNull() ?: 100f
                                viewModel.addProductToDaily(qty)
                                quantity = "100"
                            },
                            isLoading = uiState.isLoading
                        )
                    }

                    // Sezione inserimento manuale
                    ManualInputSection(
                        macros = uiState.manualMacros,
                        grams = manualGrams,
                        onMacrosChange = { viewModel.updateManualMacros(it) },
                        onGramsChange = { manualGrams = it },
                        onSave = {
                            val grams = manualGrams.replace(',', '.').toFloatOrNull() ?: 100f
                            viewModel.saveManualMacros(uiState.manualMacros, grams)
                            manualGrams = "100"
                        },
                        isOcrMode = uiState.manualSource == NutritionSource.OCR,
                        isLoading = uiState.isLoading
                    )

                    // Consiglio Gemma
                    AdviceCard(
                        advice = uiState.advice,
                        isLoading = uiState.isAdviceLoading,
                        onGenerateAdvice = { viewModel.generateAdvice() }
                    )

                    // Storico pasti
                    HistorySection(items = uiState.items, onDelete = { item -> item.id?.let { viewModel.deleteItem(it) } })

                    // Spazio extra per scroll
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Loading overlay
                LoadingOverlay(isVisible = uiState.isLoading)

                if (uiState.showProductSelection) {
                    ProductSelectionDialog(
                        results = uiState.searchResults,
                        onSelect = { viewModel.selectProduct(it) },
                        onDismiss = { viewModel.dismissProductSelection() }
                    )
                }
            }
        }
    }
}

/**
 * Versione con stato esplicito per preview e testing.
 */
@Composable
fun DietScreenContent(
    dailyTotals: MacroTotals,
    workoutSummary: String,
    product: NutritionProduct?,
    manualMacros: MacroTotals,
    manualSource: NutritionSource,
    advice: String,
    items: List<NutritionItem>,
    searchResults: List<NutritionProduct> = emptyList(),
    showProductSelection: Boolean = false,
    onSelectProduct: (NutritionProduct) -> Unit = {},
    onDismissProductSelection: () -> Unit = {},
    onDeleteItem: (NutritionItem) -> Unit = {},
    isLoading: Boolean,
    isAdviceLoading: Boolean,
    searchQuery: String,
    quantity: String,
    manualGrams: String,
    onSearchQueryChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onManualGramsChange: (String) -> Unit,
    onManualMacrosChange: (MacroTotals) -> Unit,
    onBack: () -> Unit,
    onScanBarcode: () -> Unit,
    onSearch: () -> Unit,
    onScanOcr: () -> Unit,
    onAddProductToDaily: () -> Unit,
    onSaveManual: () -> Unit,
    onGenerateAdvice: () -> Unit,
    modifier: Modifier = Modifier
) {
    DietTheme {
        Scaffold(
            topBar = {
                DietTopBar(
                    workoutSummary = workoutSummary,
                    onBack = onBack
                )
            },
            containerColor = DarkBackground,
            modifier = modifier
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DailyTotalsCard(totals = dailyTotals)

                    ScanSearchSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onScanBarcode = onScanBarcode,
                        onSearch = onSearch,
                        onOcrScan = onScanOcr,
                        isLoading = isLoading
                    )

                    product?.let {
                        ProductCard(
                            product = it,
                            quantity = quantity,
                            onQuantityChange = onQuantityChange,
                            onAddToDaily = onAddProductToDaily,
                            isLoading = isLoading
                        )
                    }

                    ManualInputSection(
                        macros = manualMacros,
                        grams = manualGrams,
                        onMacrosChange = onManualMacrosChange,
                        onGramsChange = onManualGramsChange,
                        onSave = onSaveManual,
                        isOcrMode = manualSource == NutritionSource.OCR,
                        isLoading = isLoading
                    )

//                    AdviceCard(
//                        advice = advice,
//                        isLoading = isAdviceLoading,
//                        onGenerateAdvice = onGenerateAdvice
//                    )

                    HistorySection(items = items, onDelete = onDeleteItem)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                LoadingOverlay(isVisible = isLoading)

                if (showProductSelection) {
                    ProductSelectionDialog(
                        results = searchResults,
                        onSelect = onSelectProduct,
                        onDismiss = onDismissProductSelection
                    )
                }
            }
        }
    }
}
