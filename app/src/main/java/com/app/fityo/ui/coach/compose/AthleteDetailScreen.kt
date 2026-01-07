package com.app.fityo.ui.coach.compose

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.SchedeEntity
import kotlinx.coroutines.launch

enum class AthleteTab(val icon: ImageVector, val titleRes: Int) {
    SCHEDE(Icons.Default.FitnessCenter, R.string.apri_schede),
    CALENDARIO(Icons.Default.CalendarMonth, R.string.coach_calendar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteDetailScreen(
    profile: CoachProfileEntity,
    schede: List<SchedeEntity>,
    appointments: List<CoachAppointmentEntity>,
    appointmentDates: Set<String>,
    stats: CoachProfileStats,
    selectedDate: String?,
    onBack: () -> Unit,
    onCreateScheda: () -> Unit,
    onOpenSchedeList: () -> Unit,
    onSchedaClick: (SchedeEntity) -> Unit,
    onAddAppointment: () -> Unit,
    onDateSelected: (String) -> Unit,
    onAppointmentClick: (CoachAppointmentEntity) -> Unit,
    onToggleAppointmentComplete: (CoachAppointmentEntity) -> Unit,
    onDeleteAppointment: (CoachAppointmentEntity) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { AthleteTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                // Barra viola indicatore coach mode
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = AccentPurple
                ) {}

                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(profile.avatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                profile.notes?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = it,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.button_back),
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = TextPrimary
                    )
                )

                // Tab Bar moderna
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = DarkSurface,
                    contentColor = AccentPurple
                ) {
                    AthleteTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(tab.titleRes),
                                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            selectedContentColor = AccentPurple,
                            unselectedContentColor = TextSecondary
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage == 0) onCreateScheda() else onAddAppointment()
                },
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = if (pagerState.currentPage == 0) "Crea scheda" else "Aggiungi appuntamento"
                )
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> SchedeTabContent(
                    schede = schede,
                    stats = stats,
                    profileName = profile.name,
                    onSchedaClick = onSchedaClick,
                    onOpenFullList = onOpenSchedeList,
                    onCreate = onCreateScheda
                )
                1 -> CalendarTabContent(
                    appointments = appointments,
                    appointmentDates = appointmentDates,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    onAppointmentClick = onAppointmentClick,
                    onToggleComplete = onToggleAppointmentComplete,
                    onDelete = onDeleteAppointment,
                    onAddAppointment = onAddAppointment
                )
            }
        }
    }
}

@Composable
private fun SchedeTabContent(
    schede: List<SchedeEntity>,
    stats: CoachProfileStats,
    profileName: String,
    onSchedaClick: (SchedeEntity) -> Unit,
    onOpenFullList: () -> Unit,
    onCreate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Quick stats
        item {
            QuickStatsRow(stats = stats)
        }

        // Header con bottone "Vedi tutte"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.coach_recent_schede),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenFullList) {
                    Text(
                        text = stringResource(R.string.coach_view_all),
                        color = AccentPurple
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (schede.isEmpty()) {
            item {
                EmptySchedePrompt(profileName = profileName, onCreate = onCreate)
            }
        } else {
            items(schede.take(5), key = { it.id ?: 0 }) { scheda ->
                SchedaCompactCard(scheda = scheda, onClick = { onSchedaClick(scheda) })
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun QuickStatsRow(stats: CoachProfileStats) {
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
            QuickStatItem(
                value = stats.totalSchede.toString(),
                label = stringResource(R.string.dashboard_total_schede),
                icon = Icons.Default.FitnessCenter
            )
            QuickStatItem(
                value = stats.favoriteSchede.toString(),
                label = stringResource(R.string.dashboard_favorite_schede),
                icon = Icons.Default.Star
            )
            QuickStatItem(
                value = stats.lastWorkout ?: "-",
                label = stringResource(R.string.coach_last_workout),
                icon = Icons.Default.Today
            )
        }
    }
}

@Composable
private fun QuickStatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentPurple.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SchedaCompactCard(
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
            // Icona gruppo muscolare
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheda.titolo,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${scheda.gruppoMuscolare} - ${scheda.intesita}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = scheda.data,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                if (scheda.favorite) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySchedePrompt(profileName: String, onCreate: () -> Unit) {
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
                text = stringResource(R.string.coach_no_schede),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_create_first_scheda, profileName),
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
                Text(stringResource(R.string.crea_scheda))
            }
        }
    }
}
