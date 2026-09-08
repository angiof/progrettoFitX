package com.app.fityo.ui.dashboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.WeekdayWorkoutCount
import com.app.fityo.ui.dashboard.DashViewModel
import com.app.fityo.ui.dashboard.DashboardStats
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun DashboardScreen(
    viewModel: DashViewModel,
    onOpenAnalytics: () -> Unit,
    onSelectDateRange: () -> Unit,
    onOpenChat: () -> Unit = {},
    onManageProfiles: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val percentuali by viewModel.percentualiGruppiMuscolari.observeAsState(emptyList())
    val mediaIntensita by viewModel.mediaIntensita.observeAsState(emptyList())
    val weekFrequency by viewModel.weekFrequency.observeAsState(emptyList())
    val dashboardStats by viewModel.dashboardStats.observeAsState(
        DashboardStats(0, 0, 0, null, null, null, null)
    )
    val coachProfiles by viewModel.coachProfiles.observeAsState(emptyList())
    val selectedProfileName by viewModel.selectedProfileName.observeAsState(null)

    var showProfileDialog by remember { mutableStateOf(false) }
    var startDateDisplay by remember { mutableStateOf("") }
    var endDateDisplay by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alla dashboard si arriva anche dalla home: senza freccia l'unico modo di
            // tornare indietro era la barra di sistema.
            onBack?.let { back ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = back) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = DashboardTextPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.title_dashboard),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = DashboardTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Top buttons row
            TopButtonsRow(
                onSelectDateRange = onSelectDateRange,
                onOpenAnalytics = onOpenAnalytics,
                selectedProfileName = selectedProfileName,
                onSelectProfile = { showProfileDialog = true }
            )

            // Date range card
            DateRangeCard(
                startDate = startDateDisplay,
                endDate = endDateDisplay
            )

            // Pie Chart - Muscle group distribution
            PieChartCard(percentuali = percentuali)

            // Bar Chart - Intensity by muscle group
            IntensityBarChartCard(mediaIntensita = mediaIntensita)

            // Weekly frequency chart
            WeeklyChartCard(weekFrequency = weekFrequency)

            // Stats card
            StatsCard(stats = dashboardStats)

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Chat FAB
        FloatingActionButton(
            onClick = onOpenChat,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = DashboardAccentPurple,
            contentColor = DashboardTextPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "AI Chat"
            )
        }
    }

    // Profile selection dialog
    if (showProfileDialog) {
        ProfileSelectionDialog(
            profiles = coachProfiles,
            onDismiss = { showProfileDialog = false },
            onSelectAll = {
                viewModel.setSelectedProfile(null, null)
                showProfileDialog = false
            },
            onManageProfiles = {
                showProfileDialog = false
                onManageProfiles()
            },
            onSelectProfile = { profile ->
                viewModel.setSelectedProfile(profile.id, profile.name)
                showProfileDialog = false
            }
        )
    }
}

@Composable
private fun TopButtonsRow(
    onSelectDateRange: () -> Unit,
    onOpenAnalytics: () -> Unit,
    selectedProfileName: String?,
    onSelectProfile: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Date range button
            Button(
                onClick = onSelectDateRange,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashboardCard,
                    contentColor = DashboardTextPrimary
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.seleziona_un_periodo))
            }

            // Analytics button
            Button(
                onClick = onOpenAnalytics,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DashboardAccentBlue,
                    contentColor = DashboardTextPrimary
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.analytics_advanced))
            }
        }

        // Profile button
        Button(
            onClick = onSelectProfile,
            colors = ButtonDefaults.buttonColors(
                containerColor = DashboardSurface,
                contentColor = DashboardTextPrimary
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedProfileName?.let {
                    stringResource(R.string.dashboard_showing_profile, it)
                } ?: stringResource(R.string.dashboard_profile_all)
            )
        }
    }
}

@Composable
private fun DateRangeCard(
    startDate: String,
    endDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = startDate.ifEmpty { "Data inizio" },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = endDate.ifEmpty { "Data fine" },
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DashboardTextSecondary
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PieChartCard(
    percentuali: List<GruppoMuscolarePercentuale>
) {
    val pieColors = listOf(
        Color(0xFF4777C0),
        Color(0xFFA374C6),
        Color(0xFF4FB3E8),
        Color(0xFF99CF43),
        Color(0xFFFDC135),
        Color(0xFFFD9A47),
        Color(0xFFEB6E7A),
        Color(0xFF6785C2)
    )

    val pieData = remember(percentuali) {
        percentuali.mapIndexed { index, p ->
            Pie(
                label = p.gruppoMuscolare,
                data = p.percentuale.toDouble(),
                color = pieColors[index % pieColors.size],
                selectedColor = pieColors[index % pieColors.size].copy(alpha = 0.8f)
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_total_esercizi),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (percentuali.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_chart),
                        color = DashboardTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        modifier = Modifier.size(200.dp),
                        data = pieData,
                        style = Pie.Style.Stroke(width = 60.dp),
                        scaleAnimEnterSpec = tween(durationMillis = 1200),
                        spaceDegreeAnimEnterSpec = tween(durationMillis = 1200)
                    )
                }

                // Legenda
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pieData.forEach { pie ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(pie.color)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${pie.label} (${pie.data.toInt()}%)",
                                color = DashboardTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntensityBarChartCard(
    mediaIntensita: List<GruppoMuscolareIntensitaMedia>
) {
    val chartLabel = stringResource(R.string.bar_label_intensity)

    val barColors = listOf(
        Color(0xFFFF6F61),
        Color(0xFF4DD0E1),
        Color(0xFF9575CD),
        Color(0xFF81C784),
        Color(0xFFFFD54F)
    )

    val barsData = remember(mediaIntensita) {
        mediaIntensita.mapIndexed { index, item ->
            Bars(
                label = item.gruppoMuscolare,
                values = listOf(
                    Bars.Data(
                        value = item.mediaIntensita.toDouble(),
                        color = SolidColor(barColors[index % barColors.size])
                    )
                )
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = chartLabel,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (mediaIntensita.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_chart),
                        color = DashboardTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                ColumnChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 8.dp),
                    data = barsData,
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                        spacing = 4.dp,
                        thickness = 24.dp
                    ),
                    animationSpec = tween(durationMillis = 2000)
                )

                // Legenda etichette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    mediaIntensita.forEachIndexed { index, item ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(barColors[index % barColors.size])
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.gruppoMuscolare.take(8),
                                color = DashboardTextSecondary,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyChartCard(
    weekFrequency: List<WeekdayWorkoutCount>
) {
    val dayLabels = listOf(
        stringResource(R.string.day_sun_short),
        stringResource(R.string.day_mon_short),
        stringResource(R.string.day_tue_short),
        stringResource(R.string.day_wed_short),
        stringResource(R.string.day_thu_short),
        stringResource(R.string.day_fri_short),
        stringResource(R.string.day_sat_short)
    )

    val weekColor = Color(0xFF4DD0E1)

    val barsData = remember(weekFrequency, dayLabels) {
        val map = weekFrequency.associate { it.dayOfWeek to it.count }
        dayLabels.mapIndexed { index, label ->
            Bars(
                label = label,
                values = listOf(
                    Bars.Data(
                        value = (map[index] ?: 0).toDouble(),
                        color = SolidColor(weekColor)
                    )
                )
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.week_chart_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.week_chart_description),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DashboardTextSecondary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (weekFrequency.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_chart),
                        color = DashboardTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                ColumnChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 8.dp),
                    data = barsData,
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(topRight = 4.dp, topLeft = 4.dp),
                        spacing = 2.dp,
                        thickness = 20.dp
                    ),
                    animationSpec = tween(durationMillis = 1500)
                )

                // Etichette giorni
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayLabels.forEach { label ->
                        Text(
                            text = label,
                            color = DashboardTextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    stats: DashboardStats
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_stats_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextPrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_total_schede),
                    value = stats.totalSchede.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_favorite_schede),
                    value = stats.favoriteSchede.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_total_esercizi),
                    value = stats.totalExercises.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_last_session),
                    value = stats.lastWorkoutDate ?: stringResource(R.string.dashboard_last_session_none),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_days_since),
                    value = stats.daysSinceLastWorkout?.let {
                        stringResource(R.string.dashboard_days_format, it)
                    } ?: stringResource(R.string.dashboard_no_data),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_most_trained),
                    value = stats.mostTrainedMuscle ?: stringResource(R.string.dashboard_no_data),
                    modifier = Modifier.weight(1f)
                )
            }

            StatItem(
                label = stringResource(R.string.dashboard_avg_per_week),
                value = stats.avgWorkoutsPerWeek?.let {
                    stringResource(R.string.dashboard_avg_format, it)
                } ?: stringResource(R.string.dashboard_no_data),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DashboardAccentBlue
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DashboardTextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileSelectionDialog(
    profiles: List<CoachProfileEntity>,
    onDismiss: () -> Unit,
    onSelectAll: () -> Unit,
    onSelectProfile: (CoachProfileEntity) -> Unit,
    onManageProfiles: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DashboardSurface,
        title = {
            Text(
                text = stringResource(R.string.select_profile_title),
                color = DashboardTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Individual profiles (rimosso "tutti i profili")
                profiles.forEach { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProfile(profile) },
                        colors = CardDefaults.cardColors(containerColor = DashboardCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = androidx.compose.ui.graphics.Color(profile.avatarColor)
                            ) {
                                Text(
                                    text = profile.name.take(2).uppercase(),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentHeight(Alignment.CenterVertically),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = DashboardTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = DashboardTextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                profile.notes?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DashboardTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                if (profiles.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_profiles_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DashboardTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Creare un profilo si poteva solo dalla home, che ora non ha piu la voce
                // Coach: senza questa riga non ci sarebbe piu modo di crearne uno.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onManageProfiles() },
                    colors = CardDefaults.cardColors(
                        containerColor = DashboardAccentBlue.copy(alpha = 0.18f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = DashboardAccentBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Crea e gestisci profili",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = DashboardTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "Aggiungi un atleta, modifica o elimina i profili",
                                style = MaterialTheme.typography.bodySmall,
                                color = DashboardTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.exercise_cancel),
                    color = DashboardTextSecondary
                )
            }
        }
    )
}
