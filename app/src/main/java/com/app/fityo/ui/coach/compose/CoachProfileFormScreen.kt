package com.app.fityo.ui.coach.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.data_layer.db.CoachProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachProfileFormScreen(
    existingProfile: CoachProfileEntity?,
    onSave: (CoachProfileEntity) -> Unit,
    onBack: () -> Unit
) {
    val isEditing = existingProfile != null

    var name by remember { mutableStateOf(existingProfile?.name ?: "") }
    var notes by remember { mutableStateOf(existingProfile?.notes ?: "") }
    var selectedColor by remember {
        mutableIntStateOf(existingProfile?.avatarColor ?: AvatarColors.first().toArgb())
    }

    val isValid = name.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Modifica Atleta" else "Nuovo Atleta",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (isValid) {
                                val now = System.currentTimeMillis()
                                val profile = CoachProfileEntity(
                                    id = existingProfile?.id,
                                    name = name.trim(),
                                    avatarColor = selectedColor,
                                    notes = notes.takeIf { it.isNotBlank() },
                                    createdAt = existingProfile?.createdAt ?: now,
                                    updatedAt = now
                                )
                                onSave(profile)
                            }
                        },
                        enabled = isValid
                    ) {
                        Text(
                            "Salva",
                            color = if (isValid) AccentPurple else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Preview Avatar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (name.isNotBlank()) name.take(2).uppercase() else "?",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Nome
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome Atleta") },
                placeholder = { Text("Es. Mario Rossi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = AccentPurple,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = AccentPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Note
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Note (opzionale)") },
                placeholder = { Text("Es. Principiante, obiettivo massa...") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = AccentPurple,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = AccentPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Selezione colore
            Column {
                Text(
                    text = "Colore Avatar",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(AvatarColors) { color ->
                        ColorOption(
                            color = color,
                            isSelected = selectedColor == color.toArgb(),
                            onClick = { selectedColor = color.toArgb() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottone salva
            Button(
                onClick = {
                    if (isValid) {
                        val now = System.currentTimeMillis()
                        val profile = CoachProfileEntity(
                            id = existingProfile?.id,
                            name = name.trim(),
                            avatarColor = selectedColor,
                            notes = notes.takeIf { it.isNotBlank() },
                            createdAt = existingProfile?.createdAt ?: now,
                            updatedAt = now
                        )
                        onSave(profile)
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    disabledContainerColor = AccentPurple.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (isEditing) "Salva Modifiche" else "Crea Atleta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.White, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selezionato",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
