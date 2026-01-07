package com.app.fityo.ui.coach.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachProfileListScreen(
    profiles: List<CoachProfileEntity>,
    onProfileClick: (CoachProfileEntity) -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (CoachProfileEntity) -> Unit,
    onDeleteProfile: (CoachProfileEntity) -> Unit,
    onBack: () -> Unit
) {
    var deleteDialogProfile by remember { mutableStateOf<CoachProfileEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coach Mode", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProfile,
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Aggiungi profilo")
            }
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
                    text = "Seleziona un profilo per gestire le schede dell'atleta",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (profiles.isEmpty()) {
                item {
                    EmptyProfilesCard(onAddProfile = onAddProfile)
                }
            } else {
                items(profiles, key = { it.id ?: 0 }) { profile ->
                    CoachProfileCard(
                        profile = profile,
                        onClick = { onProfileClick(profile) },
                        onEdit = { onEditProfile(profile) },
                        onDelete = { deleteDialogProfile = profile }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Dialog conferma eliminazione
    deleteDialogProfile?.let { profile ->
        AlertDialog(
            onDismissRequest = { deleteDialogProfile = null },
            title = { Text("Elimina profilo", color = TextPrimary) },
            text = {
                Text(
                    "Eliminando '${profile.name}' perderai tutte le schede associate. Continuare?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteProfile(profile)
                    deleteDialogProfile = null
                }) {
                    Text("Elimina", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogProfile = null }) {
                    Text("Annulla", color = TextSecondary)
                }
            },
            containerColor = DarkCard
        )
    }
}

@Composable
private fun CoachProfileCard(
    profile: CoachProfileEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con colore profilo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(profile.avatarColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(2).uppercase(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                profile.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            // Menu azioni
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifica",
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
    }
}

@Composable
private fun EmptyProfilesCard(onAddProfile: () -> Unit) {
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
                Icons.Default.Groups,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nessun atleta",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aggiungi il tuo primo atleta per iniziare a gestire le sue schede di allenamento",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddProfile,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aggiungi Atleta")
            }
        }
    }
}
