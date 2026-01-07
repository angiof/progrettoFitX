package com.app.fityo.ui.coach.compose

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarTabContent(
    appointments: List<CoachAppointmentEntity>,
    appointmentDates: Set<String>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    onAppointmentClick: (CoachAppointmentEntity) -> Unit,
    onToggleComplete: (CoachAppointmentEntity) -> Unit,
    onDelete: (CoachAppointmentEntity) -> Unit,
    onAddAppointment: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    val appointmentsForSelectedDate = remember(appointments, selectedDate) {
        if (selectedDate != null) {
            appointments.filter { it.date == selectedDate }
        } else {
            emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Calendar header con navigazione mese
        item {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                onToday = { currentMonth = YearMonth.now() }
            )
        }

        // Calendar grid
        item {
            CalendarGrid(
                yearMonth = currentMonth,
                today = today,
                selectedDate = selectedDate?.let { LocalDate.parse(it) },
                appointmentDates = appointmentDates,
                onDateSelected = { date ->
                    onDateSelected(date.format(dateFormatter))
                }
            )
        }

        // Appuntamenti del giorno selezionato
        if (selectedDate != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayDate = try {
                        LocalDate.parse(selectedDate).format(
                            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
                        )
                    } catch (e: Exception) {
                        selectedDate
                    }
                    Text(
                        text = displayDate,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onAddAppointment) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.coach_add_appointment),
                            color = AccentPurple
                        )
                    }
                }
            }

            if (appointmentsForSelectedDate.isEmpty()) {
                item {
                    EmptyDayCard()
                }
            } else {
                items(appointmentsForSelectedDate, key = { it.id ?: 0 }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) },
                        onToggleComplete = { onToggleComplete(appointment) },
                        onDelete = { onDelete(appointment) }
                    )
                }
            }
        } else {
            // Prossimi appuntamenti
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.coach_upcoming_appointments),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val upcoming = appointments.filter {
                !it.isCompleted && it.date >= today.format(dateFormatter)
            }.take(5)

            if (upcoming.isEmpty()) {
                item {
                    EmptyUpcomingCard()
                }
            } else {
                items(upcoming, key = { it.id ?: 0 }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) },
                        onToggleComplete = { onToggleComplete(appointment) },
                        onDelete = { onDelete(appointment) },
                        showDate = true
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Mese precedente",
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() },
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentMonth.year.toString(),
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Row {
                TextButton(onClick = onToday) {
                    Text(
                        text = stringResource(R.string.coach_today),
                        color = AccentPurple,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mese successivo",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate?,
    appointmentDates: Set<String>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("L", "M", "M", "G", "V", "S", "D")
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Calcola offset per il primo giorno (0 = Lunedi)
    val firstDayOffset = (firstDayOfMonth.dayOfWeek.value - 1) % 7

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header giorni settimana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Griglia giorni
            var dayCounter = 1
            val totalDays = lastDayOfMonth.dayOfMonth
            val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

            for (week in 0..5) {
                if (dayCounter > totalDays) break

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 0..6) {
                        if (week == 0 && dayOfWeek < firstDayOffset) {
                            // Giorni vuoti prima dell'inizio del mese
                            Box(modifier = Modifier.size(40.dp))
                        } else if (dayCounter <= totalDays) {
                            val date = yearMonth.atDay(dayCounter)
                            val dateString = date.format(dateFormatter)
                            val isToday = date == today
                            val isSelected = date == selectedDate
                            val hasAppointment = appointmentDates.contains(dateString)

                            DayCell(
                                day = dayCounter,
                                isToday = isToday,
                                isSelected = isSelected,
                                hasAppointment = hasAppointment,
                                onClick = { onDateSelected(date) }
                            )
                            dayCounter++
                        } else {
                            Box(modifier = Modifier.size(40.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasAppointment: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> AccentPurple
        isToday -> AccentPurple.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> AccentPurple
        else -> TextPrimary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (hasAppointment && !isSelected) {
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
private fun AppointmentCard(
    appointment: CoachAppointmentEntity,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    showDate: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (appointment.isCompleted) DarkCard.copy(alpha = 0.5f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox completamento
            Checkbox(
                checked = appointment.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentGreen,
                    uncheckedColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.title,
                    color = if (appointment.isCompleted) TextSecondary else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (appointment.isCompleted) TextDecoration.LineThrough else null
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showDate) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val displayDate = try {
                                LocalDate.parse(appointment.date).format(
                                    DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                                )
                            } catch (e: Exception) {
                                appointment.date
                            }
                            Text(
                                text = displayDate,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    appointment.time?.let { time ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = time,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                appointment.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notes,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = DarkSurface
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.content_desc_delete), color = AccentRed) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = AccentRed)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDayCard() {
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
                Icons.Default.EventAvailable,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_no_appointments_day),
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyUpcomingCard() {
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
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = AccentPurple.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.coach_no_upcoming),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.coach_no_upcoming_desc),
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
