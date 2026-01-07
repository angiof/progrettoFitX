package com.app.fityo.ui.dashboard.compose

import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.WeekdayWorkoutCount
import com.app.fityo.ui.dashboard.DashViewModel
import com.app.fityo.ui.dashboard.DashboardStats
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun DashboardScreen(
    viewModel: DashViewModel,
    onOpenAnalytics: () -> Unit,
    onSelectDateRange: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

    // Profile selection dialog
    if (showProfileDialog) {
        ProfileSelectionDialog(
            profiles = coachProfiles,
            onDismiss = { showProfileDialog = false },
            onSelectAll = {
                viewModel.setSelectedProfile(null, null)
                showProfileDialog = false
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

@Composable
private fun PieChartCard(
    percentuali: List<GruppoMuscolarePercentuale>
) {
    val context = LocalContext.current
    val noDataText = stringResource(R.string.no_data_chart)
    val centerText = stringResource(R.string.title_dashboard)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
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

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PieChart(ctx).apply {
                        setNoDataText(noDataText)
                        setNoDataTextColor(AndroidColor.WHITE)
                        description.isEnabled = false
                        setUsePercentValues(true)
                        isDrawHoleEnabled = true
                        holeRadius = 50f
                        transparentCircleRadius = 55f
                        setHoleColor(AndroidColor.TRANSPARENT)
                        setDrawCenterText(true)
                        setCenterTextSize(20f)
                        setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                        setCenterTextColor(AndroidColor.WHITE)
                        this.centerText = centerText
                        legend.apply {
                            textColor = AndroidColor.WHITE
                            isWordWrapEnabled = true
                        }
                        setExtraOffsets(20f, 0f, 20f, 0f)
                    }
                },
                update = { chart ->
                    if (percentuali.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    val entries = percentuali.map { p ->
                        PieEntry(p.percentuale, p.gruppoMuscolare)
                    }

                    val colors = listOf(
                        AndroidColor.parseColor("#4777c0"),
                        AndroidColor.parseColor("#a374c6"),
                        AndroidColor.parseColor("#4fb3e8"),
                        AndroidColor.parseColor("#99cf43"),
                        AndroidColor.parseColor("#fdc135"),
                        AndroidColor.parseColor("#fd9a47"),
                        AndroidColor.parseColor("#eb6e7a"),
                        AndroidColor.parseColor("#6785c2")
                    )

                    val dataSet = PieDataSet(entries, "").apply {
                        this.colors = colors
                        setDrawValues(true)
                        valueTextColor = AndroidColor.WHITE
                        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        valueTextSize = 14f
                        valueTypeface = Typeface.DEFAULT_BOLD
                        valueLinePart1Length = 0.6f
                        valueLinePart2Length = 0.3f
                        valueLineWidth = 2f
                        valueLinePart1OffsetPercentage = 115f
                        isUsingSliceColorAsValueLineColor = true
                    }

                    chart.data = PieData(dataSet)
                    chart.animateY(1200)
                    chart.invalidate()
                }
            )
        }
    }
}

@Composable
private fun IntensityBarChartCard(
    mediaIntensita: List<GruppoMuscolareIntensitaMedia>
) {
    val noDataText = stringResource(R.string.no_data_chart)
    val chartLabel = stringResource(R.string.bar_label_intensity)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
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
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    BarChart(ctx).apply {
                        setNoDataText(noDataText)
                        setNoDataTextColor(AndroidColor.WHITE)
                        description.isEnabled = false
                        setFitBars(true)
                        setDrawGridBackground(false)
                        legend.apply {
                            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                            orientation = Legend.LegendOrientation.HORIZONTAL
                            setDrawInside(false)
                            isWordWrapEnabled = true
                            textColor = AndroidColor.WHITE
                        }
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            textColor = AndroidColor.WHITE
                            setDrawGridLines(false)
                            granularity = 1f
                        }
                        axisLeft.apply {
                            textColor = AndroidColor.WHITE
                            axisMinimum = 0f
                            granularity = 1f
                            setDrawGridLines(true)
                        }
                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    if (mediaIntensita.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    val labels = ArrayList<String>()
                    val entries = ArrayList<BarEntry>()

                    mediaIntensita.forEachIndexed { index, item ->
                        entries.add(BarEntry(index.toFloat(), item.mediaIntensita))
                        labels.add(item.gruppoMuscolare)
                    }

                    val colors = listOf(
                        AndroidColor.parseColor("#FF6F61"),
                        AndroidColor.parseColor("#4DD0E1"),
                        AndroidColor.parseColor("#9575CD"),
                        AndroidColor.parseColor("#81C784"),
                        AndroidColor.parseColor("#FFD54F")
                    )

                    val dataSet = BarDataSet(entries, chartLabel).apply {
                        this.colors = colors
                        setDrawValues(true)
                        valueTextSize = 10f
                        valueTextColor = AndroidColor.WHITE
                        valueTypeface = Typeface.DEFAULT_BOLD
                    }

                    chart.data = BarData(dataSet)
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    chart.xAxis.labelCount = labels.size
                    chart.animateY(2000)
                    chart.invalidate()
                }
            )
        }
    }
}

@Composable
private fun WeeklyChartCard(
    weekFrequency: List<WeekdayWorkoutCount>
) {
    val context = LocalContext.current
    val noDataText = stringResource(R.string.no_data_chart)
    val chartLabel = stringResource(R.string.bar_label_week)

    val dayLabels = listOf(
        stringResource(R.string.day_sun_short),
        stringResource(R.string.day_mon_short),
        stringResource(R.string.day_tue_short),
        stringResource(R.string.day_wed_short),
        stringResource(R.string.day_thu_short),
        stringResource(R.string.day_fri_short),
        stringResource(R.string.day_sat_short)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
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
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    BarChart(ctx).apply {
                        setNoDataText(noDataText)
                        setNoDataTextColor(AndroidColor.WHITE)
                        description.isEnabled = false
                        legend.isEnabled = false
                        axisRight.isEnabled = false
                        axisLeft.apply {
                            textColor = AndroidColor.WHITE
                            axisMinimum = 0f
                        }
                        xAxis.apply {
                            textColor = AndroidColor.WHITE
                            granularity = 1f
                            position = XAxis.XAxisPosition.BOTTOM
                        }
                        setDrawGridBackground(false)
                    }
                },
                update = { chart ->
                    if (weekFrequency.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    val map = weekFrequency.associate { it.dayOfWeek to it.count }
                    val entries = dayLabels.indices.map { index ->
                        val value = map[index] ?: 0
                        BarEntry(index.toFloat(), value.toFloat())
                    }

                    val dataSet = BarDataSet(entries, chartLabel).apply {
                        color = AndroidColor.parseColor("#4DD0E1")
                        valueTextColor = AndroidColor.WHITE
                        valueTypeface = Typeface.DEFAULT_BOLD
                        valueTextSize = 10f
                    }

                    chart.data = BarData(dataSet)
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
                    chart.animateY(1500)
                    chart.invalidate()
                }
            )
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
    onSelectProfile: (CoachProfileEntity) -> Unit
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
