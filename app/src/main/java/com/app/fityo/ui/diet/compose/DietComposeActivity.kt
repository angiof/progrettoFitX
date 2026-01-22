package com.app.fityo.ui.diet.compose

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.network.OpenFoodFactsClient
import com.app.fityo.data_layer.repository.NutritionRepository
import com.app.fityo.ui.diet.DietSessionViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/**
 * Activity Compose per la Sessione Dieta Intelligente.
 * Gestisce scanner barcode, OCR camera e hosting della UI Compose.
 */
class DietComposeActivity : ComponentActivity() {

    private val viewModel: DietSessionViewModel by viewModels {
        val db = DbFit.getDatabase(application)
        val repository = NutritionRepository(
            api = OpenFoodFactsClient.service,
            dailyDao = db.dailyNutritionDao(),
            itemsDao = db.dailyNutritionItemDao(),
            schedeDao = db.schedeDao()
        )
        DietViewModelFactory(application, repository)
    }

    private val barcodeScanner by lazy {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
            .build()
        GmsBarcodeScanning.getClient(this, options)
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ocrLauncher.launch(null)
        } else {
            Toast.makeText(this, "Permesso camera necessario per OCR", Toast.LENGTH_SHORT).show()
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

        setContent {
            DietScreen(
                viewModel = viewModel,
                onBack = { finish() },
                onScanBarcode = { startBarcodeScanner() },
                onScanOcr = { startOcrScanner() }
            )
        }
    }

    private fun startBarcodeScanner() {
        barcodeScanner.startScan()
            .addOnSuccessListener { barcode ->
                val value = barcode.rawValue
                if (value.isNullOrBlank()) {
                    Toast.makeText(this, "Barcode non valido", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.fetchProductByBarcode(value)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Scanner fallito: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun startOcrScanner() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

/**
 * Factory per DietSessionViewModel.
 */
class DietViewModelFactory(
    private val application: android.app.Application,
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
