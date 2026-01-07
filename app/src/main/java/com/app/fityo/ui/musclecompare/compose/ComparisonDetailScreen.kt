package com.app.fityo.ui.musclecompare.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.CompareHistoryItem
import com.app.fityo.dominio.CompareResult
import com.app.fityo.dominio.DistrictResult
import com.app.fityo.dominio.MuscleDistrict
import com.app.fityo.mediapipe.EncryptedPhotoStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Schermata dettaglio confronto salvato.
 * Mostra le due foto e le variazioni muscolari.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonDetailScreen(
    comparison: CompareHistoryItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }
    val photoStorage = remember { EncryptedPhotoStorage(context) }

    // Load photos from encrypted storage
    val photoA = remember(comparison.photoAPath) {
        try {
            if (comparison.photoAPath.isNotEmpty()) {
                photoStorage.loadEncryptedPhoto(comparison.photoAPath).getOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    val photoB = remember(comparison.photoBPath) {
        try {
            if (comparison.photoBPath.isNotEmpty()) {
                photoStorage.loadEncryptedPhoto(comparison.photoBPath).getOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    val result = remember(comparison) {
        CompareResult(
            armsResult = DistrictResult(MuscleDistrict.ARMS, comparison.armsVariation, 0, 0),
            absResult = DistrictResult(MuscleDistrict.ABS, comparison.absVariation, 0, 0),
            legsResult = DistrictResult(MuscleDistrict.LEGS, comparison.legsVariation, 0, 0),
            glutesResult = DistrictResult(MuscleDistrict.GLUTES, comparison.glutesVariation, 0, 0),
            scaleFactorA = comparison.scaleFactorA,
            scaleFactorB = comparison.scaleFactorB,
            timestamp = comparison.createdAt
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Confronto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = dateFormatter.format(Date(comparison.createdAt)),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Photos comparison
            Text(
                text = "Foto Confrontate",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo A - Prima
                PhotoCard(
                    label = "Prima",
                    bitmap = photoA,
                    modifier = Modifier.weight(1f)
                )

                // Compare icon in middle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Photo B - Dopo
                PhotoCard(
                    label = "Dopo",
                    bitmap = photoB,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CompareResultContent(
                result = result,
                workoutAnalysis = null,
                showPixelDetails = false
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PhotoCard(
    label: String,
    bitmap: android.graphics.Bitmap?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Non disponibile",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

