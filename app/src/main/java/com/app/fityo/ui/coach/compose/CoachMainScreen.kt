package com.app.fityo.ui.coach.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class CoachMainTab(val icon: ImageVector, val titleRes: Int) {
    PROFILI(Icons.Default.Groups, R.string.coach_tab_profiles),
    CALENDARIO(Icons.Default.CalendarMonth, R.string.coach_calendar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachMainScreen(
    profiles: List<CoachProfileEntity>,
    allAppointments: List<CoachAppointmentEntity>,
    appointmentDates: Set<String>,
    selectedDate: String?,
    onBack: () -> Unit,
    onProfileClick: (CoachProfileEntity) -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (CoachProfileEntity) -> Unit,
    onDeleteProfile: (CoachProfileEntity) -> Unit,
    onDateSelected: (String) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onAppointmentClick: (CoachAppointmentEntity) -> Unit,
    onToggleAppointmentComplete: (CoachAppointmentEntity) -> Unit,
    onDeleteAppointment: (CoachAppointmentEntity) -> Unit,
    getProfileName: (Int) -> String?
) {
    val pagerState = rememberPagerState(pageCount = { CoachMainTab.entries.size })
    val coroutineScope = rememberCoroutineScope()
    var deleteDialogProfile by remember { mutableStateOf<CoachProfileEntity?>(null) }

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
                        Text(
                            "Coach Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
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
                    CoachMainTab.entries.forEachIndexed { index, tab ->
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
            if (pagerState.currentPage == 0) {
                FloatingActionButton(
                    onClick = onAddProfile,
                    containerColor = AccentPurple,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Aggiungi profilo")
                }
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
                0 -> ProfilesTabContent(
                    profiles = profiles,
                    onProfileClick = onProfileClick,
                    onEditProfile = onEditProfile,
                    onDeleteProfile = { deleteDialogProfile = it },
                    onAddProfile = onAddProfile
                )
                1 -> GlobalCalendarTabContent(
                    appointments = allAppointments,
                    appointmentDates = appointmentDates,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    onMonthChanged = onMonthChanged,
                    onAppointmentClick = onAppointmentClick,
                    onToggleComplete = onToggleAppointmentComplete,
                    onDelete = onDeleteAppointment,
                    getProfileName = getProfileName
                )
            }
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
private fun ProfilesTabContent(
    profiles: List<CoachProfileEntity>,
    onProfileClick: (CoachProfileEntity) -> Unit,
    onEditProfile: (CoachProfileEntity) -> Unit,
    onDeleteProfile: (CoachProfileEntity) -> Unit,
    onAddProfile: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_select_profile_hint),
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
                    onDelete = { onDeleteProfile(profile) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
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

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifica", tint = TextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = AccentRed.copy(alpha = 0.7f))
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
                text = stringResource(R.string.coach_no_athletes),
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_add_first_athlete),
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
                Text(stringResource(R.string.coach_add_athlete))
            }
        }
    }
}

@Composable
private fun GlobalCalendarTabContent(
    appointments: List<CoachAppointmentEntity>,
    appointmentDates: Set<String>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onAppointmentClick: (CoachAppointmentEntity) -> Unit,
    onToggleComplete: (CoachAppointmentEntity) -> Unit,
    onDelete: (CoachAppointmentEntity) -> Unit,
    getProfileName: (Int) -> String?
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Filtra appuntamenti per la data selezionata
    val filteredAppointments = remember(selectedDate, appointments) {
        if (selectedDate != null) {
            appointments.filter { it.date == selectedDate }
        } else {
            appointments.filter { it.date == today.format(dateFormatter) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Calendario header con navigazione
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                        onMonthChanged(currentMonth)
                    }) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Mese precedente",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            .replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                        onMonthChanged(currentMonth)
                    }) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Mese successivo",
                            tint = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Weekday headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("L", "M", "M", "G", "V", "S", "D").forEach { day ->
                        Text(
                            text = day,
                            color = AccentPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                GlobalCalendarGrid(
                    yearMonth = currentMonth,
                    appointmentDates = appointmentDates,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appuntamenti del giorno selezionato
        Text(
            text = if (selectedDate != null) {
                stringResource(R.string.coach_appointments_for_date, formatDisplayDate(selectedDate))
            } else {
                stringResource(R.string.coach_today_appointments)
            },
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredAppointments.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.coach_no_appointments_day),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredAppointments, key = { it.id ?: 0 }) { appointment ->
                    GlobalAppointmentCard(
                        appointment = appointment,
                        profileName = getProfileName(appointment.profileId),
                        onClick = { onAppointmentClick(appointment) },
                        onToggleComplete = { onToggleComplete(appointment) },
                        onDelete = { onDelete(appointment) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlobalCalendarGrid(
    yearMonth: YearMonth,
    appointmentDates: Set<String>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Calcola il giorno della settimana del primo giorno (1 = Lunedì, 7 = Domenica)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val daysInMonth = lastDayOfMonth.dayOfMonth
    val totalCells = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(((totalCells / 7) * 40).dp),
        userScrollEnabled = false
    ) {
        items(totalCells) { index ->
            val dayOfMonth = index - (firstDayOfWeek - 1) + 1

            if (dayOfMonth in 1..daysInMonth) {
                val date = yearMonth.atDay(dayOfMonth)
                val dateString = date.format(dateFormatter)
                val isToday = date == today
                val isSelected = dateString == selectedDate
                val hasAppointments = appointmentDates.contains(dateString)

                GlobalDayCell(
                    day = dayOfMonth,
                    isToday = isToday,
                    isSelected = isSelected,
                    hasAppointments = hasAppointments,
                    onClick = { onDateSelected(dateString) }
                )
            } else {
                Box(modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
private fun GlobalDayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasAppointments: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> AccentPurple
                    isToday -> AccentPurple.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                color = when {
                    isSelected -> Color.White
                    isToday -> AccentPurple
                    else -> TextPrimary
                },
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (hasAppointments && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(AccentPurple)
                )
            }
        }
    }
}

@Composable
private fun GlobalAppointmentCard(
    appointment: CoachAppointmentEntity,
    profileName: String?,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (appointment.isCompleted) DarkCard.copy(alpha = 0.6f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = appointment.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentPurple,
                    uncheckedColor = TextSecondary
                )
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.title,
                    color = if (appointment.isCompleted) TextSecondary else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nome profilo con badge colorato
                    profileName?.let { name ->
                        Surface(
                            color = AccentPurple.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = name,
                                color = AccentPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    appointment.time?.let { time ->
                        Text(
                            text = time,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
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
    }
}

private fun formatDisplayDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}
