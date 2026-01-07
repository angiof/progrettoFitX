package com.app.fityo.import_scheda

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val DarkBackground = Color(0xFF10161B)
private val DarkSurface = Color(0xFF1A222A)
private val DarkCard = Color(0xFF222C35)
private val TextPrimary = Color(0xFFECF0F1)
private val TextSecondary = Color(0xFFB0BEC5)
private val AccentBlue = Color(0xFF40C4FF)
private val AccentGreen = Color(0xFF4CAF50)
private val AccentOrange = Color(0xFFFF9800)
private val AccentRed = Color(0xFFF44336)

/**
 * Dialog per selezionare il tipo di importazione
 */
@Composable
fun ImportOptionsDialog(
    onDismiss: () -> Unit,
    onSelectFityo: () -> Unit,
    onSelectPdf: () -> Unit,
    onSelectCamera: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                text = stringResource(R.string.import_options_title),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.import_options_subtitle),
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opzione 1: Formato FitYo
                ImportOptionCard(
                    icon = Icons.Default.Folder,
                    title = stringResource(R.string.import_option_fityo),
                    subtitle = stringResource(R.string.import_option_fityo_desc),
                    accentColor = AccentBlue,
                    onClick = onSelectFityo // NON chiamare onDismiss, il dialog si nasconde con showOptionsDialog=false
                )

                // Opzione 2: PDF
                ImportOptionCard(
                    icon = Icons.Default.PictureAsPdf,
                    title = stringResource(R.string.import_option_pdf),
                    subtitle = stringResource(R.string.import_option_pdf_desc),
                    accentColor = AccentOrange,
                    onClick = onSelectPdf
                )

                // Opzione 3: Camera OCR
                ImportOptionCard(
                icon = Icons.Default.CameraAlt,
                title = stringResource(R.string.import_option_camera),
                subtitle = stringResource(R.string.import_option_camera_desc),
                accentColor = AccentGreen,
                onClick = onSelectCamera
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel), color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ImportOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

/**
 * Schermata completa per l'importazione da Camera OCR
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraOcrImportScreen(
    onBack: () -> Unit,
    onImportComplete: (List<EditableExerciseRow>, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var ocrResult by remember { mutableStateOf<OcrResult?>(null) }
    var editableRows by remember { mutableStateOf<List<EditableExerciseRow>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf("") }
    var schedaTitle by remember { mutableStateOf("") }

    val ocrHelper = remember { WorkoutOcrHelper() }

    DisposableEffect(Unit) {
        onDispose { ocrHelper.close() }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            capturedBitmap = it
            scope.launch {
                processImage(it, ocrHelper) { result, step, processing ->
                    ocrResult = result
                    processingStep = step
                    isProcessing = processing
                    result?.let { r ->
                        editableRows = r.parsedRows.map { parsed ->
                            EditableExerciseRow.fromParsed(
                                parsed,
                                parsed.boundingBox?.let { box ->
                                    ocrHelper.cropImageToBoundingBox(it, box)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    loadBitmapFromUri(context, it)
                }
                bitmap?.let { bmp ->
                    capturedBitmap = bmp
                    processImage(bmp, ocrHelper) { result, step, processing ->
                        ocrResult = result
                        processingStep = step
                        isProcessing = processing
                        result?.let { r ->
                            editableRows = r.parsedRows.map { parsed ->
                                EditableExerciseRow.fromParsed(
                                    parsed,
                                    parsed.boundingBox?.let { box ->
                                        ocrHelper.cropImageToBoundingBox(bmp, box)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.import_camera_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                capturedBitmap == null -> {
                    // Schermata iniziale - scegli come catturare
                    CaptureSelectionContent(
                        onCameraClick = { cameraLauncher.launch(null) },
                        onGalleryClick = { galleryLauncher.launch("image/*") }
                    )
                }

                isProcessing -> {
                    // Processing
                    ProcessingContent(step = processingStep)
                }

                ocrResult != null && editableRows.isNotEmpty() -> {
                    // Schermata di revisione
                    ReviewContent(
                        originalBitmap = capturedBitmap,
                        rows = editableRows,
                        schedaTitle = schedaTitle,
                        onTitleChange = { schedaTitle = it },
                        onRowChange = { index, newRow ->
                            editableRows = editableRows.toMutableList().apply {
                                set(index, newRow)
                            }
                        },
                        onDeleteRow = { index ->
                            editableRows = editableRows.toMutableList().apply {
                                removeAt(index)
                            }
                        },
                        onAddRow = {
                            editableRows = editableRows + EditableExerciseRow(
                                id = java.util.UUID.randomUUID().toString(),
                                exerciseName = "",
                                sets = "",
                                reps = "",
                                weight = "",
                                rest = "",
                                notes = "",
                                confidence = 1f,
                                hasError = false,
                                errorMessage = null
                            )
                        },
                        onRetake = {
                            capturedBitmap = null
                            ocrResult = null
                            editableRows = emptyList()
                        },
                        onImport = {
                            if (schedaTitle.isNotBlank() && editableRows.any { it.isValid() }) {
                                onImportComplete(editableRows.filter { it.isValid() }, schedaTitle)
                            }
                        },
                        canImport = schedaTitle.isNotBlank() && editableRows.any { it.isValid() }
                    )
                }

                ocrResult?.success == false -> {
                    // Errore OCR
                    ErrorContent(
                        message = ocrResult?.errorMessage ?: "Errore sconosciuto",
                        onRetry = {
                            capturedBitmap = null
                            ocrResult = null
                        }
                    )
                }

                else -> {
                    // Nessun esercizio trovato
                    NoResultsContent(
                        onRetry = {
                            capturedBitmap = null
                            ocrResult = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptureSelectionContent(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.DocumentScanner,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.import_camera_hint_title),
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.import_camera_hint_subtitle),
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Camera button
        Button(
        onClick = onCameraClick,
        modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
        shape = RoundedCornerShape(16.dp)
        ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(stringResource(R.string.import_camera_take_photo), fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Gallery button
        Button(
            onClick = onGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.import_camera_from_gallery))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.import_camera_tips_title),
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TipItem(stringResource(R.string.import_camera_tip_1))
                TipItem(stringResource(R.string.import_camera_tip_2))
                TipItem(stringResource(R.string.import_camera_tip_3))
            }
        }
    }
}

@Composable
private fun TipItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = AccentGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ProcessingContent(step: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AccentBlue)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = step,
            color = TextPrimary,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ReviewContent(
    originalBitmap: Bitmap?,
    rows: List<EditableExerciseRow>,
    schedaTitle: String,
    onTitleChange: (String) -> Unit,
    onRowChange: (Int, EditableExerciseRow) -> Unit,
    onDeleteRow: (Int) -> Unit,
    onAddRow: () -> Unit,
    onRetake: () -> Unit,
    onImport: () -> Unit,
    canImport: Boolean
) {
    val validCount = rows.count { it.isValid() }
    val errorCount = rows.count { it.hasError }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con statistiche
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.import_review_title),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatChip(
                        icon = Icons.Default.CheckCircle,
                        value = validCount.toString(),
                        label = stringResource(R.string.import_review_valid),
                        color = AccentGreen
                    )
                    if (errorCount > 0) {
                        StatChip(
                            icon = Icons.Default.Warning,
                            value = errorCount.toString(),
                            label = stringResource(R.string.import_review_errors),
                            color = AccentOrange
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo titolo scheda
        OutlinedTextField(
            value = schedaTitle,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.import_review_scheda_title)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextSecondary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista esercizi editabili
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rows.size) { index ->
                EditableExerciseCard(
                    row = rows[index],
                    onRowChange = { onRowChange(index, it) },
                    onDelete = { onDeleteRow(index) }
                )
            }

            item {
                // Aggiungi esercizio manualmente
                Button(
                    onClick = onAddRow,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.import_review_add_exercise))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottoni azione
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetake,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.import_review_retake))
            }

            Button(
                onClick = onImport,
                modifier = Modifier.weight(1f),
                enabled = canImport,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    disabledContainerColor = AccentGreen.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.import_action))
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$value $label",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EditableExerciseCard(
    row: EditableExerciseRow,
    onRowChange: (EditableExerciseRow) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(row.hasError) }

    val borderColor by animateColorAsState(
        targetValue = when {
            row.hasError -> AccentOrange
            !row.isValid() -> AccentRed
            else -> Color.Transparent
        },
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicatore confidenza
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                row.confidence >= 0.9f -> AccentGreen
                                row.confidence >= 0.7f -> AccentOrange
                                else -> AccentRed
                            }
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Nome esercizio
                    OutlinedTextField(
                        value = row.exerciseName,
                        onValueChange = { onRowChange(row.copy(exerciseName = it)) },
                        label = { Text(stringResource(R.string.exercise_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        isError = row.exerciseName.isBlank()
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Elimina",
                        tint = AccentRed.copy(alpha = 0.7f)
                    )
                }
            }

            // Serie e Ripetizioni (sempre visibili)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = row.sets,
                    onValueChange = { onRowChange(row.copy(sets = it)) },
                    label = { Text(stringResource(R.string.series)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = row.sets.toIntOrNull() == null
                )

                OutlinedTextField(
                    value = row.reps,
                    onValueChange = { onRowChange(row.copy(reps = it)) },
                    label = { Text(stringResource(R.string.repetitions)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = row.reps.toIntOrNull() == null
                )
            }

            // Campi espandibili
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = row.weight,
                            onValueChange = { onRowChange(row.copy(weight = it)) },
                            label = { Text(stringResource(R.string.weight_kg)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = row.rest,
                            onValueChange = { onRowChange(row.copy(rest = it)) },
                            label = { Text(stringResource(R.string.rest_time)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                            ),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = row.notes,
                        onValueChange = { onRowChange(row.copy(notes = it)) },
                        label = { Text(stringResource(R.string.notes)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        maxLines = 2
                    )

                    // Messaggio errore
                    row.errorMessage?.let { msg ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    AccentOrange.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                color = AccentOrange,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = AccentRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.import_error_title),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(stringResource(R.string.import_review_retake))
        }
    }
}

@Composable
private fun NoResultsContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.import_no_results_title),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.import_no_results_subtitle),
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(stringResource(R.string.import_review_retake))
        }
    }
}

// Helper functions
private suspend fun processImage(
    bitmap: Bitmap,
    ocrHelper: WorkoutOcrHelper,
    onProgress: (OcrResult?, String, Boolean) -> Unit
) {
    Log.d("ImportSchedaScreen", "=== Starting image processing ===")
    Log.d("ImportSchedaScreen", "Bitmap size: ${bitmap.width}x${bitmap.height}")

    // Initialize Gemma if available but not ready
    if (ocrHelper.isGemmaAvailable() && !ocrHelper.isGemmaReady()) {
        Log.d("ImportSchedaScreen", "Gemma available but not ready, initializing...")
        onProgress(null, "Caricamento AI Gemma...", true)
        val gemmaResult = ocrHelper.initializeGemma()
        Log.d("ImportSchedaScreen", "Gemma init result: success=${gemmaResult.isSuccess}")
        if (gemmaResult.isFailure) {
            Log.w("ImportSchedaScreen", "Gemma init failed: ${gemmaResult.exceptionOrNull()?.message}")
        }
    } else {
        Log.d("ImportSchedaScreen", "Gemma available: ${ocrHelper.isGemmaAvailable()}, ready: ${ocrHelper.isGemmaReady()}")
    }

    onProgress(null, "Elaborazione immagine...", true)
    Log.d("ImportSchedaScreen", "Starting OCR processing...")

    val result = ocrHelper.processImage(bitmap)

    Log.d("ImportSchedaScreen", "=== OCR Results ===")
    Log.d("ImportSchedaScreen", "Exercises found: ${result.parsedRows.size}")
    Log.d("ImportSchedaScreen", "Used Gemma: ${result.usedGemma}")
    result.parsedRows.forEachIndexed { index, exercise ->
        Log.d("ImportSchedaScreen", "  [$index] ${exercise.exerciseName}: ${exercise.sets}x${exercise.reps}, rest=${exercise.rest}")
    }
    Log.d("ImportSchedaScreen", "=== End OCR Results ===")

    onProgress(result, "", false)
}

private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Dialog per assegnare la scheda importata a un profilo
 */
@Composable
fun ProfileAssignmentDialog(
    profiles: List<ProfileInfo>,
    onDismiss: () -> Unit,
    onSelectPersonal: () -> Unit,
    onSelectProfile: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                text = stringResource(R.string.import_assign_profile_title),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.import_assign_profile_subtitle),
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opzione: Scheda personale (no profilo)
                // NON chiamare onDismiss prima di onSelectPersonal - il salvataggio deve completarsi prima
                ProfileOptionCard(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.import_assign_personal),
                    subtitle = stringResource(R.string.import_assign_personal_desc),
                    accentColor = AccentBlue,
                    onClick = onSelectPersonal // Salva prima, poi finish() verrà chiamato in saveScheda
                )

                // Lista profili coach
                if (profiles.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.import_assign_coach_profiles),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    profiles.forEach { profile ->
                        ProfileOptionCard(
                            icon = Icons.Default.Group,
                            title = profile.name,
                            subtitle = profile.notes ?: "",
                            accentColor = Color(profile.avatarColor),
                            onClick = { onSelectProfile(profile.id) } // NON chiamare onDismiss, saveScheda chiamerà finish()
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel), color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Info minime per profilo (per evitare dipendenze circolari)
 */
data class ProfileInfo(
    val id: Int,
    val name: String,
    val avatarColor: Int,
    val notes: String?
)

/**
 * Schermata di processing PDF
 */
@Composable
fun PdfProcessingScreen(step: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AccentOrange)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = step,
            color = TextPrimary,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.import_pdf_processing_hint),
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Schermata di revisione risultati PDF
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReviewScreen(
    pageCount: Int,
    rows: List<EditableExerciseRow>,
    schedaTitle: String,
    onTitleChange: (String) -> Unit,
    onRowChange: (Int, EditableExerciseRow) -> Unit,
    onDeleteRow: (Int) -> Unit,
    onAddRow: () -> Unit,
    onBack: () -> Unit,
    onImport: () -> Unit,
    canImport: Boolean
) {
    val validCount = rows.count { it.isValid() }
    val errorCount = rows.count { it.hasError }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.import_pdf_review_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Info PDF
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.import_pdf_pages_found, pageCount),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "$validCount ${stringResource(R.string.import_review_valid)}",
                                    color = AccentGreen,
                                    fontSize = 13.sp
                                )
                                if (errorCount > 0) {
                                    Text(
                                        text = "$errorCount ${stringResource(R.string.import_review_errors)}",
                                        color = AccentOrange,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo titolo scheda
            OutlinedTextField(
                value = schedaTitle,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.import_review_scheda_title)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentOrange,
                    unfocusedBorderColor = TextSecondary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista esercizi editabili
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rows.size) { index ->
                    EditableExerciseCard(
                        row = rows[index],
                        onRowChange = { onRowChange(index, it) },
                        onDelete = { onDeleteRow(index) }
                    )
                }

                item {
                    Button(
                        onClick = onAddRow,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOrange)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.import_review_add_exercise))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottoni azione
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text(stringResource(R.string.button_cancel))
                }

                Button(
                    onClick = onImport,
                    modifier = Modifier.weight(1f),
                    enabled = canImport,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        disabledContainerColor = AccentGreen.copy(alpha = 0.3f)
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.import_action))
                }
            }
        }
    }
}

