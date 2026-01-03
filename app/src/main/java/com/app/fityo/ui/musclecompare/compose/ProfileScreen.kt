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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.AthleticDiscipline
import com.app.fityo.dominio.BiologicalSex
import com.app.fityo.dominio.UserProfile

/**
 * Schermata per visualizzare il profilo esistente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewScreen(
    profile: UserProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onStartAnalysis: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Il Tuo Profilo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifica", tint = AccentBlue)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = AccentRed)
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
            // Avatar/Icona profilo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (profile.sex == BiologicalSex.MALE) Icons.Default.Male else Icons.Default.Female,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                profile.name,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                profile.discipline.displayName,
                color = AccentBlue,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card dati biometrici
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Dati Biometrici",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DataItem(label = "Età", value = "${profile.age} anni")
                        DataItem(label = "Altezza", value = "${profile.heightCm.toInt()} cm")
                        DataItem(label = "Peso", value = "${profile.weightKg} kg")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DataItem(
                            label = "BMI",
                            value = String.format("%.1f", profile.bmi),
                            subtitle = profile.bmiCategory
                        )
                        DataItem(
                            label = "Sesso",
                            value = profile.sex.displayName
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card disciplina
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Disciplina",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        profile.discipline.description,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Aree prioritarie:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        profile.discipline.priorityAreas.joinToString(" • "),
                        color = AccentBlue,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Range FFMI ideale: ${profile.discipline.idealFfmiRange.start.toInt()}-${profile.discipline.idealFfmiRange.endInclusive.toInt()}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsante analisi
            Button(
                onClick = onStartAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inizia Analisi Corporea", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog conferma eliminazione
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina Profilo") },
            text = { Text("Sei sicuro di voler eliminare il tuo profilo? Questa azione non può essere annullata.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Elimina", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            },
            containerColor = DarkCard
        )
    }
}

@Composable
private fun DataItem(
    label: String,
    value: String,
    subtitle: String? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = TextSecondary,
            fontSize = 12.sp
        )
        subtitle?.let {
            Text(
                it,
                color = AccentBlue,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Schermata per creare o modificare un profilo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    existingProfile: UserProfile? = null,
    onSave: (UserProfile) -> Unit,
    onBack: () -> Unit
) {
    val isEditing = existingProfile != null

    var name by remember { mutableStateOf(existingProfile?.name ?: "") }
    var age by remember { mutableStateOf(existingProfile?.age?.toString() ?: "") }
    var heightCm by remember { mutableStateOf(existingProfile?.heightCm?.toInt()?.toString() ?: "") }
    var weightKg by remember { mutableStateOf(existingProfile?.weightKg?.toString() ?: "") }
    var sex by remember { mutableStateOf(existingProfile?.sex ?: BiologicalSex.MALE) }
    var discipline by remember { mutableStateOf(existingProfile?.discipline ?: AthleticDiscipline.WELLNESS) }

    val isValid = name.isNotBlank() &&
            age.toIntOrNull() != null && age.toInt() in 10..100 &&
            heightCm.toIntOrNull() != null && heightCm.toInt() in 100..250 &&
            weightKg.toFloatOrNull() != null && weightKg.toFloat() in 30f..300f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Modifica Profilo" else "Crea Profilo") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nome
            FormTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nome",
                placeholder = "Il tuo nome"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Età
            FormTextField(
                value = age,
                onValueChange = { if (it.length <= 3) age = it.filter { c -> c.isDigit() } },
                label = "Età",
                placeholder = "Es: 25",
                keyboardType = KeyboardType.Number,
                suffix = "anni"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Altezza e Peso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormTextField(
                    value = heightCm,
                    onValueChange = { if (it.length <= 3) heightCm = it.filter { c -> c.isDigit() } },
                    label = "Altezza",
                    placeholder = "Es: 175",
                    keyboardType = KeyboardType.Number,
                    suffix = "cm",
                    modifier = Modifier.weight(1f)
                )

                FormTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it.filter { c -> c.isDigit() || c == '.' } },
                    label = "Peso",
                    placeholder = "Es: 75.5",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "kg",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sesso
            Text(
                "Sesso Biologico",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableCard(
                    selected = sex == BiologicalSex.MALE,
                    onClick = { sex = BiologicalSex.MALE },
                    icon = Icons.Default.Male,
                    label = "Maschio",
                    modifier = Modifier.weight(1f)
                )
                SelectableCard(
                    selected = sex == BiologicalSex.FEMALE,
                    onClick = { sex = BiologicalSex.FEMALE },
                    icon = Icons.Default.Female,
                    label = "Femmina",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disciplina
            Text(
                "Disciplina Atletica",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Questo influenza come l'IA interpreta il tuo fisico",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            AthleticDiscipline.entries.forEach { d ->
                DisciplineCard(
                    discipline = d,
                    selected = discipline == d,
                    onClick = { discipline = d }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsante salva
            Button(
                onClick = {
                    val profile = UserProfile(
                        id = existingProfile?.id,
                        name = name,
                        age = age.toInt(),
                        heightCm = heightCm.toFloat(),
                        weightKg = weightKg.toFloat(),
                        sex = sex,
                        discipline = discipline,
                        createdAt = existingProfile?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(profile)
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    disabledContainerColor = AccentGreen.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isEditing) "Salva Modifiche" else "Crea Profilo", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        suffix = suffix?.let { { Text(it, color = TextSecondary) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
            focusedLabelColor = AccentBlue,
            unfocusedLabelColor = TextSecondary,
            cursorColor = AccentBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AccentBlue.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, AccentBlue) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) AccentBlue else TextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                color = if (selected) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DisciplineCard(
    discipline: AthleticDiscipline,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AccentBlue.copy(alpha = 0.15f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, AccentBlue) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selected) AccentBlue.copy(alpha = 0.3f) else DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = if (selected) AccentBlue else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    discipline.displayName,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    discipline.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
