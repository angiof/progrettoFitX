package com.app.fityo.ui.diet.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.fityo.dominio.MacroTotals
import com.app.fityo.dominio.NutritionItem
import com.app.fityo.dominio.NutritionProduct
import com.app.fityo.dominio.NutritionSource
import java.util.Locale

/**
 * Top bar della schermata dieta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietTopBar(
    workoutSummary: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Dieta Intelligente",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (workoutSummary.isBlank()) "Oggi: Riposo" else "Oggi: $workoutSummary",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentGreen
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary
        ),
        modifier = modifier
    )
}

/**
 * Card con totali giornalieri dei macro.
 */
@Composable
fun DailyTotalsCard(
    totals: MacroTotals,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MacroCardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Macro Giornalieri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Riga principale: Proteine, Carbo, Grassi, Kcal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItem(
                    label = "Proteine",
                    value = totals.proteins,
                    unit = "g",
                    color = ProteinColor
                )
                MacroItem(
                    label = "Carbo",
                    value = totals.carbs,
                    unit = "g",
                    color = CarbsColor
                )
                MacroItem(
                    label = "Grassi",
                    value = totals.fats,
                    unit = "g",
                    color = FatsColor
                )
                MacroItem(
                    label = "Kcal",
                    value = totals.kcal,
                    unit = "",
                    color = KcalColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Riga secondaria: Fibre, Zuccheri, Grassi Sat., Sale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItemSmall(
                    label = "Fibre",
                    value = totals.fibers,
                    unit = "g",
                    color = FibersColor
                )
                MacroItemSmall(
                    label = "Zuccheri",
                    value = totals.sugars,
                    unit = "g",
                    color = SugarsColor
                )
                MacroItemSmall(
                    label = "Saturi",
                    value = totals.saturatedFats,
                    unit = "g",
                    color = SaturatedFatsColor
                )
                MacroItemSmall(
                    label = "Sale",
                    value = totals.salt,
                    unit = "g",
                    color = SaltColor
                )
            }
        }
    }
}

@Composable
private fun MacroItem(
    label: String,
    value: Float,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatMacroValue(value) + unit,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun MacroItemSmall(
    label: String,
    value: Float,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = formatMacroValue(value) + unit,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

/**
 * Sezione scanner e ricerca.
 */
@Composable
fun ScanSearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScanBarcode: () -> Unit,
    onSearch: () -> Unit,
    onOcrScan: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aggiungi Alimento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Riga pulsanti scan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Barcode",
                    icon = Icons.Default.QrCodeScanner,
                    color = ScanButtonColor,
                    onClick = onScanBarcode,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "OCR",
                    icon = Icons.Default.DocumentScanner,
                    color = OcrButtonColor,
                    onClick = onOcrScan,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ricerca testuale
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text("Cerca prodotto...", color = TextSecondary)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackgroundColor,
                        unfocusedContainerColor = InputBackgroundColor,
                        focusedBorderColor = InputFocusedBorderColor,
                        unfocusedBorderColor = InputBorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onSearch,
                    enabled = !isLoading && searchQuery.isNotBlank(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (searchQuery.isNotBlank()) SearchButtonColor else SecondaryButtonColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}

/**
 * Card prodotto trovato.
 */
@Composable
fun ProductCard(
    product: NutritionProduct?,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onAddToDaily: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (product == null) return

    Card(
        colors = CardDefaults.cardColors(containerColor = ProductCardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Immagine prodotto
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Valori per 100g",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ProductMacrosRow(macros = product.macrosPer100g)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input quantita e pulsante aggiungi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Grammi") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackgroundColor,
                        unfocusedContainerColor = InputBackgroundColor,
                        focusedBorderColor = InputFocusedBorderColor,
                        unfocusedBorderColor = InputBorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TextSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(100.dp)
                )

                Button(
                    onClick = onAddToDaily,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aggiungi")
                }
            }
        }
    }
}

@Composable
fun ProductSelectionDialog(
    results: List<NutritionProduct>,
    onSelect: (NutritionProduct) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = DarkCard,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seleziona prodotto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 360.dp)
                ) {
                    items(results) { product ->
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(product) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ProductMacrosRow(macros = product.macrosPer100g)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text(text = "Chiudi", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ProductMacrosRow(
    macros: MacroTotals,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacroChip("P", macros.proteins, ProteinColor)
        MacroChip("C", macros.carbs, CarbsColor)
        MacroChip("G", macros.fats, FatsColor)
        MacroChip("Kcal", macros.kcal, KcalColor)
    }
}

@Composable
private fun MacroChip(
    label: String,
    value: Float,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label: ${formatMacroValue(value)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

/**
 * Sezione inserimento manuale.
 */
@Composable
fun ManualInputSection(
    macros: MacroTotals,
    grams: String,
    onMacrosChange: (MacroTotals) -> Unit,
    onGramsChange: (String) -> Unit,
    onSave: () -> Unit,
    isOcrMode: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isOcrMode) "Valori da OCR" else "Inserimento Manuale",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (isOcrMode) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = OcrButtonColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "OCR",
                            style = MaterialTheme.typography.labelSmall,
                            color = OcrButtonColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input grammi
            OutlinedTextField(
                value = grams,
                onValueChange = onGramsChange,
                label = { Text("Quantita (g)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grid macro (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroInputField(
                    label = "Proteine (g)",
                    value = macros.proteins,
                    onValueChange = { onMacrosChange(macros.copy(proteins = it)) },
                    color = ProteinColor,
                    modifier = Modifier.weight(1f)
                )
                MacroInputField(
                    label = "Carbo (g)",
                    value = macros.carbs,
                    onValueChange = { onMacrosChange(macros.copy(carbs = it)) },
                    color = CarbsColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroInputField(
                    label = "Grassi (g)",
                    value = macros.fats,
                    onValueChange = { onMacrosChange(macros.copy(fats = it)) },
                    color = FatsColor,
                    modifier = Modifier.weight(1f)
                )
                MacroInputField(
                    label = "Kcal",
                    value = macros.kcal,
                    onValueChange = { onMacrosChange(macros.copy(kcal = it)) },
                    color = KcalColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva")
            }
        }
    }
}

@Composable
private fun MacroInputField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) {
        mutableStateOf(if (value == 0f) "" else formatMacroValue(value))
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newText ->
            textValue = newText
            val parsed = newText.replace(',', '.').toFloatOrNull() ?: 0f
            onValueChange(parsed)
        },
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = textFieldColors(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = InputBackgroundColor,
    unfocusedContainerColor = InputBackgroundColor,
    focusedBorderColor = InputFocusedBorderColor,
    unfocusedBorderColor = InputBorderColor,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = TextSecondary,
    unfocusedLabelColor = TextSecondary,
    cursorColor = AccentPurple
)

/**
 * Card consiglio Gemma.
 */
@Composable
fun AdviceCard(
    advice: String,
    isLoading: Boolean,
    onGenerateAdvice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AdviceCardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🤖",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Consiglio Gemma",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onGenerateAdvice,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Genera consiglio",
                        tint = if (isLoading) TextSecondary else AccentOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            } else {
                Text(
                    text = advice.ifBlank { "Premi il pulsante per ricevere un consiglio basato sui tuoi macro e allenamento di oggi." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (advice.isBlank()) TextSecondary else TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

/**
 * Effetto shimmer per loading.
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.horizontalGradient(
        colors = listOf(
            ShimmerBaseColor,
            ShimmerHighlightColor,
            ShimmerBaseColor
        ),
        startX = translateAnim - 500f,
        endX = translateAnim
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

/**
 * Lista storico pasti del giorno.
 */
@Composable
fun HistorySection(
    items: List<NutritionItem>,
    onDelete: (NutritionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pasti di Oggi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isEmpty()) {
                Text(
                    text = "Nessun pasto registrato oggi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        HistoryItemRow(item = item, onDelete = onDelete)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(
    item: NutritionItem,
    onDelete: (NutritionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HistoryItemColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icona sorgente
            val sourceIcon = when (item.source) {
                NutritionSource.BARCODE -> Icons.Default.QrCodeScanner
                NutritionSource.SEARCH -> Icons.Default.Search
                NutritionSource.OCR -> Icons.Default.DocumentScanner
                NutritionSource.MANUAL -> Icons.Default.Edit
            }
            val sourceColor = when (item.source) {
                NutritionSource.BARCODE -> ScanButtonColor
                NutritionSource.SEARCH -> SearchButtonColor
                NutritionSource.OCR -> OcrButtonColor
                NutritionSource.MANUAL -> AccentPurple
            }

            Icon(
                imageVector = sourceIcon,
                contentDescription = null,
                tint = sourceColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.name} (${formatMacroValue(item.grams)}g)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "P:${formatMacroValue(item.macros.proteins)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProteinColor
                    )
                    Text(
                        text = "C:${formatMacroValue(item.macros.carbs)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CarbsColor
                    )
                    Text(
                        text = "G:${formatMacroValue(item.macros.fats)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FatsColor
                    )
                    Text(
                        text = "${formatMacroValue(item.macros.kcal)}kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = KcalColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onDelete(item) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina",
                    tint = AccentRed
                )
            }
        }
    }
}

/**
 * Loading overlay.
 */
@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = AccentPurple,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Formatta un valore macro.
 */
private fun formatMacroValue(value: Float): String {
    return if (value == 0f) "0" else String.format(Locale.getDefault(), "%.1f", value)
}
