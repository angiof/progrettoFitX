package com.app.fityo.ui.coach.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.SchedeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachSessionScreen(
    profile: CoachProfileEntity,
    schede: List<SchedeEntity>,
    stats: CoachProfileStats,
    onBack: () -> Unit,
    onCreateScheda: () -> Unit,
    onSchedaClick: (SchedeEntity) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                // Barra viola indicatore coach mode
                CoachModeIndicator(profile = profile)

                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(profile.avatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(profile.name, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Esci",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateScheda,
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crea scheda")
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Stats card
            item {
                CoachStatsCard(stats = stats)
            }

            // Header schede
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schede di ${profile.name}",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${schede.size} totali",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            if (schede.isEmpty()) {
                item {
                    EmptySchedeCard(profileName = profile.name, onCreate = onCreateScheda)
                }
            } else {
                items(schede, key = { it.id ?: 0 }) { scheda ->
                    SchedaCard(scheda = scheda, onClick = { onSchedaClick(scheda) })
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CoachModeIndicator(profile: CoachProfileEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = AccentPurple
    ) {}
}

@Composable
private fun CoachStatsCard(stats: CoachProfileStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = stats.totalSchede.toString(), label = "Schede")
            StatItem(value = stats.favoriteSchede.toString(), label = "Preferite")
            StatItem(
                value = stats.lastWorkout ?: "-",
                label = "Ultimo"
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AccentPurple,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SchedaCard(
    scheda: SchedeEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheda.titolo,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${scheda.gruppoMuscolare} - ${scheda.intesita}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = scheda.data,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            if (scheda.favorite) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Preferita",
                    tint = AccentOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptySchedeCard(profileName: String, onCreate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = AccentPurple.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nessuna scheda",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crea la prima scheda per $profileName",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crea Scheda")
            }
        }
    }
}

data class CoachProfileStats(
    val totalSchede: Int = 0,
    val favoriteSchede: Int = 0,
    val lastWorkout: String? = null,
    val mostTrainedMuscle: String? = null
)
