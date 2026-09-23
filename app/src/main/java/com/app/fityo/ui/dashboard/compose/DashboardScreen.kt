package com.app.fityo.ui.dashboard.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.ui.dashboard.DashViewModel
import com.app.fityo.ui.dashboard.DashboardSummary
import io.grafima.charts.bar.AxisConfig
import io.grafima.charts.bar.BarChart
import io.grafima.charts.bar.BarDataSet
import io.grafima.charts.bar.BarEntry
import io.grafima.charts.bar.BarOrientation
import io.grafima.charts.bar.ChartStyle
import io.grafima.charts.bar.TooltipSelectionRenderer
import io.grafima.charts.gauge.GaugeChart
import io.grafima.charts.gauge.GaugeChartStyle
import io.grafima.charts.gauge.GaugeNeedleConfig
import io.grafima.charts.gauge.GaugeNeedleStyle
import io.grafima.charts.gauge.GaugeTickConfig
import io.grafima.charts.gauge.GaugeZone
import io.grafima.charts.line.LineAxisConfig
import io.grafima.charts.line.LineChart
import io.grafima.charts.line.LineChartStyle
import io.grafima.charts.line.LineCrosshairConfig
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineDataSet
import io.grafima.charts.line.LineSeries
import io.grafima.charts.pie.PieChart
import io.grafima.charts.pie.PieChartStyle
import io.grafima.charts.pie.PieDataSet
import io.grafima.charts.pie.PieEntry
import io.grafima.charts.pie.SliceBrush
import io.grafima.charts.pie.TooltipPieSelectionRenderer
import io.grafima.charts.radar.RadarAxis
import io.grafima.charts.radar.RadarChart
import io.grafima.charts.radar.RadarChartStyle
import io.grafima.charts.radar.RadarDataSet
import io.grafima.charts.radar.RadarGridStyle
import io.grafima.charts.radar.RadarSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashViewModel,
    onOpenAnalytics: () -> Unit,
    onSelectDateRange: () -> Unit,
    onManageProfiles: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val summary by viewModel.summary.observeAsState(DashboardSummary())
    val loading by viewModel.loading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val periodLabel by viewModel.periodLabel.observeAsState("")
    val selectedDays by viewModel.selectedDays.observeAsState(30)
    val coachProfiles by viewModel.coachProfiles.observeAsState(emptyList())
    val selectedProfileName by viewModel.selectedProfileName.observeAsState(null)

    var showProfileDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alla dashboard si arriva anche dalla home: senza freccia l'unico modo di
            // tornare indietro era la barra di sistema.
            DashboardTopBar(
                onBack = onBack,
                selectedProfileName = selectedProfileName,
                onSelectProfile = { showProfileDialog = true },
                onOpenAnalytics = onOpenAnalytics
            )

            HeroCard(summary = summary, periodLabel = periodLabel, loading = loading)

            PeriodSelector(
                selectedDays = selectedDays,
                onSelectPeriod = viewModel::selectPeriod,
                onCustomRange = onSelectDateRange
            )

            error?.let { message -> ErrorCard(message = message) }

            ConsistencyGaugeCard(weeklyAverage = summary.weeklyAverage)

            MuscleDonutCard(muscles = summary.muscles)

            // Il radar ha bisogno di almeno tre assi per disegnare una forma.
            if (summary.muscles.size >= 3) {
                MuscleRadarCard(muscles = summary.muscles)
            }

            WeekdayBarCard(weekdays = summary.weekdays)

            TrendLineCard(trend = summary.trend)

            IntensityBarCard(intensities = summary.intensities)

            StatsCard(summary = summary)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

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
private fun DashboardTopBar(
    onBack: (() -> Unit)?,
    selectedProfileName: String?,
    onSelectProfile: () -> Unit,
    onOpenAnalytics: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        onBack?.let { back ->
            IconButton(onClick = back) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = DashboardTextPrimary
                )
            }
        }
        Text(
            text = stringResource(R.string.title_dashboard),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(
                color = DashboardTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        IconButton(onClick = onOpenAnalytics) {
            Icon(
                Icons.Default.Analytics,
                contentDescription = stringResource(R.string.analytics_advanced),
                tint = DashboardAccentBlue
            )
        }
        IconButton(onClick = onSelectProfile) {
            Icon(
                Icons.Default.Person,
                contentDescription = selectedProfileName
                    ?: stringResource(R.string.dashboard_profile_all),
                tint = DashboardAccentGreen
            )
        }
    }
}

@Composable
private fun HeroCard(
    summary: DashboardSummary,
    periodLabel: String,
    loading: Boolean
) {
    // Il numero sale invece di comparire: si vede che il periodo e' cambiato.
    val completed by animateIntAsState(
        targetValue = summary.completed,
        animationSpec = tween(durationMillis = 700),
        label = "completed"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(DashboardHeroBrush)
            .border(1.dp, DashboardCardBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = periodLabel,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = DashboardAccentBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = completed.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = stringResource(R.string.dashboard_hero_completed),
                style = MaterialTheme.typography.titleSmall.copy(color = DashboardTextSecondary)
            )
            Text(
                text = stringResource(R.string.dashboard_hero_of, summary.total),
                style = MaterialTheme.typography.bodySmall.copy(color = DashboardTextMuted)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeroMetric(
                    value = summary.activeDays.toString(),
                    label = stringResource(R.string.dashboard_active_days)
                )
                HeroMetric(
                    value = String.format(Locale.getDefault(), "%.1f", summary.weeklyAverage),
                    label = stringResource(R.string.dashboard_avg_per_week)
                )
                HeroMetric(
                    value = summary.favorites.toString(),
                    label = stringResource(R.string.dashboard_favorite_schede)
                )
            }

            if (loading) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape),
                    color = DashboardAccentBlue,
                    trackColor = DashboardCardBorder
                )
            }
        }
    }
}

@Composable
private fun HeroMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = DashboardTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = DashboardTextMuted)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PeriodSelector(
    selectedDays: Int?,
    onSelectPeriod: (Int?) -> Unit,
    onCustomRange: () -> Unit
) {
    val periods = listOf(
        7 to stringResource(R.string.dashboard_period_7),
        30 to stringResource(R.string.dashboard_period_30),
        90 to stringResource(R.string.dashboard_period_90),
        null to stringResource(R.string.dashboard_period_all)
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { (days, label) ->
            PeriodChip(
                label = label,
                selected = selectedDays == days,
                onClick = { onSelectPeriod(days) }
            )
        }
        // -1 e' il periodo scelto a mano dal date picker: nessun chip lo rappresenta.
        PeriodChip(
            label = stringResource(R.string.seleziona_un_periodo),
            selected = selectedDays == -1,
            onClick = onCustomRange,
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
private fun PeriodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val background by animateColorAsState(
        targetValue = if (selected) DashboardAccentBlue.copy(alpha = 0.22f) else DashboardCard,
        label = "chipBackground"
    )
    val content = if (selected) DashboardAccentBlue else DashboardTextSecondary

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) DashboardAccentBlue.copy(alpha = 0.6f) else DashboardCardBorder,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        leadingIcon?.let {
            CompositionLocalProvider(LocalContentColor provides content) { it() }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = content,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DashboardError.copy(alpha = 0.14f))
            .border(1.dp, DashboardError.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = DashboardTextPrimary
        )
    }
}

@Composable
private fun ConsistencyGaugeCard(weeklyAverage: Double) {
    val zones = listOf(
        GaugeZone(
            id = "low",
            label = stringResource(R.string.dashboard_zone_low),
            range = 0f..2f,
            color = DashboardError
        ),
        GaugeZone(
            id = "steady",
            label = stringResource(R.string.dashboard_zone_steady),
            range = 2f..4f,
            color = DashboardAccentBlue
        ),
        GaugeZone(
            id = "high",
            label = stringResource(R.string.dashboard_zone_high),
            range = 4f..7f,
            color = DashboardAccentGreen
        )
    )

    ChartCard(
        title = stringResource(R.string.dashboard_consistency_title),
        subtitle = stringResource(R.string.dashboard_consistency_subtitle),
        accent = DashboardAccentGreen
    ) {
        GaugeChart(
            value = weeklyAverage.toFloat(),
            minValue = 0f,
            maxValue = 7f,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            zones = zones,
            style = GaugeChartStyle(
                startAngle = 135f,
                sweepAngle = 270f,
                arcWidth = 18.dp,
                trackColor = DashboardGrid,
                fillFraction = 0.9f,
                centerContentOffset = 64.dp
            ),
            tickConfig = GaugeTickConfig(
                majorTickCount = 7,
                minorTicksPerMajor = 1,
                majorTickColor = DashboardTextMuted,
                minorTickColor = DashboardGrid,
                labelColor = DashboardTextMuted,
                labelFontSize = 10.sp
            ),
            needleConfig = GaugeNeedleConfig(
                style = GaugeNeedleStyle.Tapered,
                color = DashboardTextPrimary,
                baseColor = DashboardTextMuted,
                lengthFraction = 0.78f
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", weeklyAverage),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = DashboardTextPrimary,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    text = stringResource(R.string.dashboard_consistency_unit),
                    style = MaterialTheme.typography.labelSmall.copy(color = DashboardTextMuted)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MuscleDonutCard(muscles: List<Pair<String, Int>>) {
    val description = stringResource(R.string.dashboard_chart_muscle_a11y)
    val completedLabel = stringResource(R.string.dashboard_completed_schede)
    val top = remember(muscles) { muscles.take(DashboardChartGradients.size) }
    var selected by remember { mutableStateOf<PieEntry?>(null) }

    val dataSet = remember(top, description) {
        PieDataSet(
            entries = top.mapIndexed { index, (gruppo, count) ->
                PieEntry(
                    id = gruppo,
                    label = gruppo,
                    value = count.toFloat(),
                    brush = SliceBrush.Linear(
                        colors = DashboardChartGradients[index % DashboardChartGradients.size],
                        angleDegrees = 45f
                    )
                )
            },
            contentDescription = description
        )
    }
    val total = remember(top) { top.sumOf { it.second } }

    ChartCard(
        title = stringResource(R.string.dashboard_muscle_distribution),
        subtitle = stringResource(R.string.dashboard_muscle_subtitle)
    ) {
        if (top.isEmpty()) {
            EmptyChart(height = 220.dp)
            return@ChartCard
        }

        PieChart(
            dataSet = dataSet,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            style = PieChartStyle(
                donutRatio = 0.58f,
                selectedScale = 1.06f,
                fillFraction = 0.78f,
                sliceSpacingAngle = 1.5f,
                minSliceAngle = 6f
            ),
            selectionRenderer = TooltipPieSelectionRenderer(
                backgroundColor = DashboardTooltip,
                textStyle = TextStyle(color = DashboardTextPrimary, fontSize = 12.sp)
            ),
            selectedEntry = selected,
            onSliceSelected = { selected = it }
        ) {
            val slice = selected
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (slice == null) total.toString()
                    else "${(slice.value * 100 / total.coerceAtLeast(1)).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = DashboardTextPrimary,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    text = slice?.label ?: completedLabel,
                    style = MaterialTheme.typography.labelSmall.copy(color = DashboardTextMuted),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            top.forEachIndexed { index, (gruppo, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    DashboardChartGradients[index % DashboardChartGradients.size]
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$gruppo ($count)",
                        color = DashboardTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MuscleRadarCard(muscles: List<Pair<String, Int>>) {
    val description = stringResource(R.string.dashboard_chart_radar_a11y)
    val seriesLabel = stringResource(R.string.dashboard_radar_title)
    var selected by remember { mutableStateOf<RadarSeries?>(null) }

    // Sei assi sono il massimo leggibile: oltre, le etichette si accavallano.
    val top = remember(muscles) { muscles.take(6) }
    val dataSet = remember(top, description, seriesLabel) {
        val scale = top.maxOf { it.second }.toFloat()
        RadarDataSet(
            axes = top.map { (gruppo, _) ->
                RadarAxis(id = gruppo, label = gruppo, maxValue = scale)
            },
            series = listOf(
                RadarSeries(
                    id = "volume",
                    label = seriesLabel,
                    values = top.associate { (gruppo, count) -> gruppo to count.toFloat() },
                    color = DashboardAccentBlue,
                    fillAlpha = 0.28f
                )
            ),
            contentDescription = description
        )
    }

    ChartCard(
        title = stringResource(R.string.dashboard_radar_title),
        subtitle = stringResource(R.string.dashboard_radar_subtitle),
        accent = DashboardAccentPurple
    ) {
        RadarChart(
            dataSet = dataSet,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            style = RadarChartStyle(
                gridColor = DashboardGrid,
                axisColor = DashboardGrid,
                labelColor = DashboardTextSecondary,
                gridStyle = RadarGridStyle.Polygon,
                gridLevels = 4,
                fillFraction = 0.72f,
                dotRadius = 4.dp
            ),
            selectedSeries = selected,
            onSeriesSelected = { selected = it }
        )
    }
}

@Composable
private fun WeekdayBarCard(weekdays: List<Int>) {
    val description = stringResource(R.string.dashboard_chart_week_a11y)
    // summary.weekdays parte dal lunedi (DayOfWeek.value 1..7).
    val dayLabels = listOf(
        stringResource(R.string.day_mon_short),
        stringResource(R.string.day_tue_short),
        stringResource(R.string.day_wed_short),
        stringResource(R.string.day_thu_short),
        stringResource(R.string.day_fri_short),
        stringResource(R.string.day_sat_short),
        stringResource(R.string.day_sun_short)
    )
    var selected by remember { mutableStateOf<BarEntry?>(null) }

    val dataSet = remember(weekdays, dayLabels, description) {
        val best = weekdays.withIndex().filter { it.value > 0 }.maxByOrNull { it.value }?.index
        BarDataSet(
            entries = dayLabels.mapIndexed { index, label ->
                BarEntry(
                    id = "day$index",
                    xLabel = label,
                    y = (weekdays.getOrNull(index) ?: 0).toFloat(),
                    // Il giorno migliore si stacca senza bisogno di leggere l'asse.
                    gradientColors = if (index == best) DashboardChartGradients[1]
                    else DashboardChartGradients[0]
                )
            },
            contentDescription = description
        )
    }

    ChartCard(
        title = stringResource(R.string.week_chart_title),
        subtitle = stringResource(R.string.dashboard_week_subtitle)
    ) {
        if (weekdays.none { it > 0 }) {
            EmptyChart(height = 200.dp)
            return@ChartCard
        }
        BarChart(
            dataSet = dataSet,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            style = dashboardBarStyle(),
            axisConfig = dashboardBarAxis(),
            selectionRenderer = dashboardBarTooltip(),
            selectedEntry = selected,
            onBarSelected = { selected = it }
        )
    }
}

@Composable
private fun IntensityBarCard(intensities: List<Pair<String, Int>>) {
    val description = stringResource(R.string.dashboard_chart_intensity_a11y)
    var selected by remember { mutableStateOf<BarEntry?>(null) }

    val dataSet = remember(intensities, description) {
        BarDataSet(
            entries = intensities.mapIndexed { index, (label, count) ->
                BarEntry(
                    id = "intensity$index",
                    xLabel = label,
                    y = count.toFloat(),
                    gradientColors = IntensityGradients.getOrElse(index) {
                        DashboardChartGradients[index % DashboardChartGradients.size]
                    }
                )
            },
            contentDescription = description
        )
    }

    ChartCard(
        title = stringResource(R.string.dashboard_intensity_distribution),
        subtitle = stringResource(R.string.dashboard_intensity_subtitle),
        accent = DashboardChartColors[3]
    ) {
        if (intensities.none { it.second > 0 }) {
            EmptyChart(height = 180.dp)
            return@ChartCard
        }
        BarChart(
            dataSet = dataSet,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            orientation = BarOrientation.Horizontal,
            style = dashboardBarStyle(),
            axisConfig = dashboardBarAxis(steps = 3),
            selectionRenderer = dashboardBarTooltip(),
            selectedEntry = selected,
            onBarSelected = { selected = it }
        )
    }
}

@Composable
private fun TrendLineCard(trend: List<Pair<LocalDate, Int>>) {
    val description = stringResource(R.string.dashboard_chart_trend_a11y)
    val seriesLabel = stringResource(R.string.dashboard_trend_title)
    var selectedPoint by remember { mutableStateOf<Int?>(null) }

    val dataSet = remember(trend, description, seriesLabel) {
        val format = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
        LineDataSet(
            series = listOf(
                LineSeries(
                    id = "trend",
                    label = seriesLabel,
                    points = trend.mapIndexed { index, (week, count) ->
                        LineDataPoint(
                            x = index.toFloat(),
                            y = count.toFloat(),
                            label = week.format(format)
                        )
                    },
                    color = DashboardAccentBlue,
                    fillAlpha = 0.28f,
                    fillGradientColors = listOf(DashboardAccentBlue, Color.Transparent),
                    dotRadius = 5.dp
                )
            ),
            contentDescription = description
        )
    }

    ChartCard(
        title = stringResource(R.string.dashboard_trend_title),
        subtitle = stringResource(R.string.dashboard_trend_subtitle),
        accent = DashboardChartColors[5]
    ) {
        // Una settimana sola non e' un andamento, e' un punto.
        if (trend.size < 2) {
            EmptyChart(height = 200.dp)
            return@ChartCard
        }
        LineChart(
            dataSet = dataSet,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            style = LineChartStyle(
                curveType = LineCurveType.MonotoneCubic,
                showDots = true
            ),
            axisConfig = LineAxisConfig(
                yTickCount = 4,
                includeZeroInYRange = true,
                gridColor = DashboardGrid,
                axisColor = DashboardGrid,
                labelColor = DashboardTextMuted
            ),
            crosshairConfig = LineCrosshairConfig(
                lineColor = DashboardTextMuted,
                dotBorderColor = DashboardCard,
                tooltipBackground = DashboardTooltip,
                tooltipTextColor = DashboardTextPrimary
            ),
            selectedPointIndex = selectedPoint,
            onPointSelected = { selectedPoint = it }
        )
    }
}

/** Bassa, Media, Alta, Non indicata: la scala di colore segue lo sforzo. */
private val IntensityGradients = listOf(
    DashboardChartGradients[1],
    DashboardChartGradients[0],
    DashboardChartGradients[4],
    listOf(Color(0xFF7A8CA0), Color(0xFF5A6C80))
)

@Composable
private fun dashboardBarStyle(): ChartStyle = remember {
    ChartStyle(
        labelTextStyle = TextStyle(
            color = DashboardTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        ),
        valueTextStyle = TextStyle(
            color = DashboardTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun dashboardBarAxis(steps: Int = 4): AxisConfig = remember(steps) {
    AxisConfig(
        yAxisSteps = steps,
        axisColor = DashboardGrid,
        axisLabelTextStyle = TextStyle(
            color = DashboardTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
private fun dashboardBarTooltip(): TooltipSelectionRenderer = remember {
    TooltipSelectionRenderer(
        backgroundColor = DashboardTooltip,
        textStyle = TextStyle(color = DashboardTextPrimary, fontSize = 12.sp)
    )
}

@Composable
private fun StatsCard(
    summary: DashboardSummary
) {
    val lastWorkoutDate = remember(summary.lastDate) {
        summary.lastDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }
    val daysSinceLastWorkout = remember(summary.lastDate) {
        summary.lastDate?.let { ChronoUnit.DAYS.between(it, LocalDate.now()).toInt() }
    }
    val mostTrainedMuscle = summary.muscles.firstOrNull()?.first

    ChartCard(
        title = stringResource(R.string.dashboard_stats_title),
        accent = DashboardChartColors[2]
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_total_schede),
                    value = summary.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_completed_schede),
                    value = summary.completed.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_last_session),
                    value = lastWorkoutDate ?: stringResource(R.string.dashboard_last_session_none),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_days_since),
                    value = daysSinceLastWorkout?.let {
                        stringResource(R.string.dashboard_days_format, it)
                    } ?: stringResource(R.string.dashboard_no_data),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = stringResource(R.string.dashboard_most_trained),
                    value = mostTrainedMuscle ?: stringResource(R.string.dashboard_no_data),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = stringResource(R.string.dashboard_favorite_schede),
                    value = summary.favorites.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Passi e battito arrivano dal wear: mostrati solo quando ci sono davvero.
            if (summary.steps != null || summary.heartRate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = stringResource(R.string.dashboard_total_steps),
                        value = summary.steps?.toString()
                            ?: stringResource(R.string.dashboard_no_data),
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = stringResource(R.string.dashboard_avg_heart_rate),
                        value = summary.heartRate?.let {
                            String.format(Locale.getDefault(), "%.0f", it)
                        } ?: stringResource(R.string.dashboard_no_data),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (summary.undated > 0) {
                Text(
                    text = stringResource(R.string.dashboard_undated_note, summary.undated),
                    style = MaterialTheme.typography.bodySmall,
                    color = DashboardTextMuted
                )
            }
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
                                color = Color(profile.avatarColor)
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

                // Senza questa voce un profilo scelto per sbaglio non si potrebbe piu togliere.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAll() },
                    colors = CardDefaults.cardColors(containerColor = DashboardCard)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_profile_all),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DashboardTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
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
