package com.example.learnsphere.calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.learnsphere.navGraph.Screen
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel
) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek(DayOfWeek.MONDAY) }
    val eventListState by viewModel.eventsListState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) { // using dialog for now
        EventListDialog(
            onClose = { showDialog = false },
            onAdd = {
                showDialog = false
                navController.navigate(Screen.EventScreen.route)
                },
            events = eventListState,
            navController = navController
        )
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Scaffold (
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ){it
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalCalendar(
                state = state,
                dayContent = { Day(it) },
                monthHeader = { month ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = month.yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        ListButton(
                            onClick = {
                                viewModel.getEventsForMonth(month.yearMonth); showDialog = true }
                        )
                        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                    }
                }
            )
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}
@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.tertiary else Color.Gray
        )
    }
}

@Composable
fun EventListDialog(
    onClose: () -> Unit,
    onAdd: () -> Unit,
    events: List<Event>,
    navController: NavController
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Row {
                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event"
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }
                LazyColumn {
                    items(events) { event ->
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Column(
                                Modifier.padding(10.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = formatEpochTimestamp(event.eventDateTime),
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = event.eventTitle,
                                        modifier = Modifier
                                            .weight(2f)
                                    )

                                    Text(
                                        text = event.eventRemarks,
                                        modifier = Modifier
                                            .weight(2f)
                                    )
                                }

    //                        Button(
    //                            onClick = { navController.navigate(Screen.EventScreen.route + "?eventId=" + event.id) },
    //                            modifier = Modifier.padding(start = 8.dp)
    //                        ) {
    //                            Text("Edit Event")
    //                        }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ListButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Outlined.List, contentDescription = "List")
    }
}

fun formatEpochTimestamp(epochMillis: Long): String {
    val instant = Instant.ofEpochMilli(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    return formatter.format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
}