package com.app.fityo.ui.musclecompare.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.CompareHistoryItem
import com.app.fityo.dominio.HistoryState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyState: HistoryState,
    onNewCompare: () -> Unit,
    onBodyIntelligence: () -> Unit,
    onAvatar3DHistory: () -> Unit = {},
    onHistoryItemClick: (Int) -> Unit = {},
    onDeleteCompare: (Int) -> Unit,
    onBack: () -> Unit
) {
    var deleteDialogItem by remember { mutableStateOf<CompareHistoryItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisi Corporea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Storico Confronti",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            // History section
            when (historyState) {
                is HistoryState.Loading -> {
                    item { LoadingContent() }
                }

                is HistoryState.Empty -> {
                    item {
                        EmptyHistoryHint()
                    }
                }

                is HistoryState.Error -> {
                    item {
                        ErrorContent(message = historyState.message)
                    }
                }

                is HistoryState.Success -> {
                    items(historyState.comparisons) { item ->
                        HistoryItemCard(
                            item = item,
                            onClick = { onHistoryItemClick(item.id) },
                            onDelete = { deleteDialogItem = item }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                ModeSelectionSection(
                    onNewCompare = onNewCompare,
                    onBodyIntelligence = onBodyIntelligence,
                    onAvatar3DHistory = onAvatar3DHistory
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Dialog conferma eliminazione
    deleteDialogItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteDialogItem = null },
            title = { Text("Elimina confronto") },
            text = { Text("Sei sicuro di voler eliminare questo confronto? L'azione non può essere annullata.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCompare(item.id)
                        deleteDialogItem = null
                    }
                ) {
                    Text("Elimina", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogItem = null }) {
                    Text("Annulla")
                }
            },
            containerColor = DarkCard
        )
    }
}

@Composable
private fun ModeSelectionSection(
    onNewCompare: () -> Unit,
    onBodyIntelligence: () -> Unit,
    onAvatar3DHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Scegli Modalità",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Prima riga: Confronto e Body Intelligence
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModeCard(
                title = "Confronto",
                subtitle = "Compara 2 foto",
                icon = Icons.Default.CompareArrows,
                gradientColors = listOf(
                    AccentBlue.copy(alpha = 0.3f),
                    AccentBlue.copy(alpha = 0.1f)
                ),
                accentColor = AccentBlue,
                onClick = onNewCompare,
                modifier = Modifier.weight(1f)
            )

            ModeCard(
                title = "Body Intelligence",
                subtitle = "Analisi completa",
                icon = Icons.Default.Analytics,
                gradientColors = listOf(
                    AccentGreen.copy(alpha = 0.3f),
                    AccentGreen.copy(alpha = 0.1f)
                ),
                accentColor = AccentGreen,
                onClick = onBodyIntelligence,
                modifier = Modifier.weight(1f)
            )
        }

        // Seconda riga: Avatar 3D
        ModeCard(
            title = "Avatar 3D",
            subtitle = "Visualizza i tuoi avatar salvati",
            icon = Icons.Default.ViewInAr,
            gradientColors = listOf(
                AccentOrange.copy(alpha = 0.3f),
                AccentOrange.copy(alpha = 0.1f)
            ),
            accentColor = AccentOrange,
            onClick = onAvatar3DHistory,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(gradientColors)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AccentBlue)
    }
}

@Composable
private fun EmptyHistoryHint() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Nessun confronto salvato",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "I tuoi confronti appariranno qui",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Errore: $message",
                color = AccentRed,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: CompareHistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con data e bottone elimina
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        dateFormatter.format(Date(item.createdAt)),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Elimina",
                        tint = AccentRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Griglia variazioni
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VariationChip("Braccia", item.armsVariation)
                VariationChip("Addome", item.absVariation)
                VariationChip("Gambe", item.legsVariation)
                VariationChip("Glutei", item.glutesVariation)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Media
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Media: ",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    String.format("%.1f%%", item.averageVariation),
                    color = if (item.averageVariation > 0) AccentGreen else AccentRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VariationChip(label: String, variation: Float) {
    val isPositive = variation >= 0
    val color = if (isPositive) AccentGreen else AccentRed

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                String.format("%.1f%%", abs(variation)),
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            label,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}
