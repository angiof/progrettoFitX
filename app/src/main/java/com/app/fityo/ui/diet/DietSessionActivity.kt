package com.app.fityo.ui.diet



import android.Manifest

import android.animation.ObjectAnimator

import android.animation.ValueAnimator

import android.os.Bundle

import android.view.View

import android.view.animation.LinearInterpolator

import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.isVisible

import androidx.lifecycle.Lifecycle

import androidx.lifecycle.lifecycleScope

import androidx.lifecycle.repeatOnLifecycle

import com.app.fityo.data_layer.db.DB.DbFit

import com.app.fityo.data_layer.network.OpenFoodFactsClient

import com.app.fityo.data_layer.repository.NutritionRepository

import com.app.fityo.databinding.ActivityDietSessionBinding

import com.app.fityo.dominio.MacroTotals

import com.app.fityo.dominio.NutritionItem

import com.app.fityo.dominio.NutritionSource

import com.bumptech.glide.Glide

import com.google.mlkit.vision.barcode.common.Barcode

import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions

import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

import kotlinx.coroutines.launch

import java.util.Locale



class DietSessionActivity : AppCompatActivity() {



    private lateinit var binding: ActivityDietSessionBinding

    private var shimmerAnimator: ObjectAnimator? = null



    private val viewModel: DietSessionViewModel by viewModels {

        val db = DbFit.getDatabase(application)

        val repository = NutritionRepository(

            api = OpenFoodFactsClient.service,

            dailyDao = db.dailyNutritionDao(),

            itemsDao = db.dailyNutritionItemDao(),

            schedeDao = db.schedeDao()

        )

        DietSessionViewModelFactory(application, repository)

    }



    private val barcodeScanner by lazy {

        val options = GmsBarcodeScannerOptions.Builder()

            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)

            .build()

        GmsBarcodeScanning.getClient(this, options)

    }



    private val ocrPermissionLauncher = registerForActivityResult(

        ActivityResultContracts.RequestPermission()

    ) { granted ->

        if (granted) {

            ocrLauncher.launch(null)

        } else {

            Toast.makeText(this, "Permesso camera necessario", Toast.LENGTH_SHORT).show()

        }

    }



    private val ocrLauncher = registerForActivityResult(

        ActivityResultContracts.TakePicturePreview()

    ) { bitmap ->

        if (bitmap != null) {

            viewModel.processOcr(bitmap)

        } else {

            Toast.makeText(this, "Nessuna immagine acquisita", Toast.LENGTH_SHORT).show()

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityDietSessionBinding.inflate(layoutInflater)

        setContentView(binding.root)



        setupActions()

        observeState()

    }



    override fun onDestroy() {

        super.onDestroy()

        stopShimmer()

    }



    private fun setupActions() {

        binding.btnScanBarcode.setOnClickListener {

            barcodeScanner.startScan()

                .addOnSuccessListener { barcode ->

                    val value = barcode.rawValue

                    if (value.isNullOrBlank()) {

                        Toast.makeText(this, "Barcode non valido", Toast.LENGTH_SHORT).show()

                    } else {

                        viewModel.fetchProductByBarcode(value)

                    }

                }

                .addOnFailureListener {

                    Toast.makeText(this, "Scanner fallito, usa ricerca o OCR", Toast.LENGTH_SHORT).show()

                }

        }



        binding.btnSearchProduct.setOnClickListener {

            val query = binding.etSearchProduct.text?.toString().orEmpty()

            viewModel.searchProduct(query)

        }



        binding.btnOcrScan.setOnClickListener {

            ocrPermissionLauncher.launch(Manifest.permission.CAMERA)

        }



        binding.btnAddToDaily.setOnClickListener {

            val quantity = binding.etQuantity.text?.toString().orEmpty().toFloatSafe()

            viewModel.addProductToDaily(quantity ?: 0f)

        }



        binding.btnSaveManual.setOnClickListener {

            val grams = binding.etManualGrams.text?.toString().orEmpty().toFloatSafe() ?: 0f

            val proteins = binding.etManualProteins.text?.toString().orEmpty().toFloatSafe() ?: 0f

            val carbs = binding.etManualCarbs.text?.toString().orEmpty().toFloatSafe() ?: 0f

            val fats = binding.etManualFats.text?.toString().orEmpty().toFloatSafe() ?: 0f

            val kcal = binding.etManualKcal.text?.toString().orEmpty().toFloatSafe() ?: 0f

            viewModel.saveManualMacros(MacroTotals(proteins, carbs, fats, kcal), grams)

        }



        binding.btnGenerateAdvice.setOnClickListener {

            viewModel.generateAdvice()

        }

    }



    private fun observeState() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressLoading.isVisible = state.isLoading

                    binding.tvWorkoutSummary.text = if (state.workoutSummary.isBlank()) {

                        "Riposo"

                    } else {

                        state.workoutSummary

                    }



                    val product = state.product

                    binding.tvProductName.text = product?.name ?: "-"

                    binding.tvMacroProteins.text = formatMacro("Proteine", product?.macrosPer100g?.proteins)

                    binding.tvMacroCarbs.text = formatMacro("Carboidrati", product?.macrosPer100g?.carbs)

                    binding.tvMacroFats.text = formatMacro("Grassi", product?.macrosPer100g?.fats)

                    binding.tvMacroKcal.text = formatMacro("Kcal", product?.macrosPer100g?.kcal)



                    if (product?.imageUrl.isNullOrBlank()) {

                        binding.ivProduct.setImageResource(android.R.color.transparent)

                    } else {

                        Glide.with(this@DietSessionActivity)

                            .load(product?.imageUrl)

                            .centerCrop()

                            .into(binding.ivProduct)

                    }



                    binding.tvDailyTotals.text = buildDailyTotalsText(state.dailyTotals)

                    binding.tvHistoryItems.text = buildHistoryText(state.items)



                    binding.etManualProteins.setText(state.manualMacros.proteins.toInput())

                    binding.etManualCarbs.setText(state.manualMacros.carbs.toInput())

                    binding.etManualFats.setText(state.manualMacros.fats.toInput())

                    binding.etManualKcal.setText(state.manualMacros.kcal.toInput())



                    if (state.isAdviceLoading) {

                        startShimmer()

                        binding.tvAdviceBody.text = ""

                    } else {

                        stopShimmer()

                        binding.tvAdviceBody.text = if (state.advice.isBlank()) "-" else state.advice

                    }



                    state.errorMessage?.let { message ->

                        Toast.makeText(this@DietSessionActivity, message, Toast.LENGTH_SHORT).show()

                        viewModel.clearError()

                    }

                }

            }

        }

    }



    private fun startShimmer() {

        val shimmerView = binding.viewAdviceShimmer

        shimmerView.isVisible = true

        if (shimmerAnimator != null) return



        shimmerView.post {

            val width = shimmerView.width.toFloat()

            shimmerAnimator?.cancel()

            shimmerAnimator = ObjectAnimator.ofFloat(shimmerView, View.TRANSLATION_X, -width, width).apply {

                duration = 1200

                repeatCount = ValueAnimator.INFINITE

                repeatMode = ValueAnimator.RESTART

                interpolator = LinearInterpolator()

                start()

            }

        }

    }



    private fun stopShimmer() {

        shimmerAnimator?.cancel()

        shimmerAnimator = null

        binding.viewAdviceShimmer.translationX = 0f

        binding.viewAdviceShimmer.isVisible = false

    }



    private fun buildHistoryText(items: List<NutritionItem>): String {

        if (items.isEmpty()) {

            return getString(com.app.fityo.R.string.diet_history_empty)

        }

        return items.joinToString("\n\n") { item ->

            val grams = String.format(Locale.getDefault(), "%.0f", item.grams)

            val source = sourceLabel(item.source)

            "${item.name} (${grams}g) - P ${item.macros.proteins.toInput()} C ${item.macros.carbs.toInput()} F ${item.macros.fats.toInput()} Kcal ${item.macros.kcal.toInput()} [$source]"

        }

    }



    private fun sourceLabel(source: NutritionSource): String {

        val res = resources

        return when (source) {

            NutritionSource.BARCODE -> res.getString(com.app.fityo.R.string.diet_source_barcode)

            NutritionSource.SEARCH -> res.getString(com.app.fityo.R.string.diet_source_search)

            NutritionSource.OCR -> res.getString(com.app.fityo.R.string.diet_source_ocr)

            NutritionSource.MANUAL -> res.getString(com.app.fityo.R.string.diet_source_manual)

        }

    }



    private fun formatMacro(label: String, value: Float?): String {

        val display = value?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "0.0"

        return "$label: $display"

    }



    private fun buildDailyTotalsText(totals: MacroTotals): String {

        return "Proteine: ${totals.proteins.toInput()} | Carboidrati: ${totals.carbs.toInput()} | Grassi: ${totals.fats.toInput()} | Kcal: ${totals.kcal.toInput()}"

    }



    private fun Float.toInput(): String {

        return if (this == 0f) "0" else String.format(Locale.getDefault(), "%.1f", this)

    }



    private fun String.toFloatSafe(): Float? {

        val normalized = trim().replace(',', '.')

        return normalized.toFloatOrNull()

    }

}

