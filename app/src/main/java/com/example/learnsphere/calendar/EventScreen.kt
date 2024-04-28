package com.example.learnsphere.calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

data class EventModule(
    val moduleId: String,
    val moduleName: String
)

data class EventCourse(
    val courseId: String,
    val courseName: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(navController: NavController, repository: EventRepository, eventId: String?) {
    val db = Firebase.firestore
    val viewModel = remember { CalendarViewModel(repository) }
    val coursesList = remember { mutableListOf<EventCourse>() }
    val moduleList = remember { mutableListOf<EventModule>() }
    Log.d("debug", "eventId: ${eventId}")
    LaunchedEffect(eventId) {
        if (!eventId.isNullOrBlank()) {
            viewModel.getEventById(eventId)
        }
        val courseCollection = db.collection("courses").get().await()
        val moduleCollection =
            courseCollection.documents[0].reference.collection("modules").get().await()

        for (course in courseCollection) {
            val courseId = course.id
            val courseName = course.data["course_code"].toString()
            coursesList.add(EventCourse(courseId, courseName))
            Log.d("Add Announcement Screen", coursesList.toString())
        }

        for (module in moduleCollection) {
            val moduleId = module.id
            val moduleName = module.data["mod_name"].toString()
            moduleList.add(EventModule(moduleId, moduleName))
            Log.d("Add Announcement Screen", moduleList.toString())
        }
    }
    val eventState = viewModel.eventState.collectAsState()
    Log.d("debug", "event: ${eventState.value}")
    var eventTitle by remember { mutableStateOf(TextFieldValue()) }
    var eventRemarks by remember { mutableStateOf(TextFieldValue()) }
    var datePickerState = rememberDatePickerState()
    var timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isModuleExpanded by remember { mutableStateOf(false) }
    var selectedModule by remember { mutableStateOf("") }
    var selectedModuleName by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf(0L) }
    var selectedTimeMillis by remember { mutableStateOf(0L) }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {it
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Event title field
            TextField(
                value = eventTitle,
                onValueChange = { eventTitle = it },
                label = { Text("Event Title") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event remarks field
            TextField(
                value = eventRemarks,
                onValueChange = { eventRemarks = it },
                label = { Text("Event Remarks") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = isModuleExpanded,
                onExpandedChange = { isModuleExpanded = it }
            ) {
                TextField(
                    value = selectedModuleName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModuleExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = isModuleExpanded,
                    onDismissRequest = { isModuleExpanded = false }
                ) {
                    // Populate the dropdown with the list of modules
                    for (module in moduleList) {
                        DropdownMenuItem(text = {
                            Text(module.moduleName)
                        }, onClick = {
                            isModuleExpanded = false
                            selectedModule = module.moduleId
                            selectedModuleName = module.moduleName
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Picker Button
            Button(
                onClick = { showDatePicker = true },
            ) {
                Text(text = "Date Picker")
            }

            Text(
                text = if (selectedDateMillis == 0L) {
                    "Date not selected"
                } else {
                    val selectedDate = LocalDateTime.ofEpochSecond(selectedDateMillis / 1000, 0, ZoneOffset.UTC)
                    val formatter = DateTimeFormatter.ofPattern("dd MMM") // Format for day and month
                    val formattedDate = selectedDate.format(formatter)
                    "Selected Date: $formattedDate"
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time Picker Button
            Button(
                onClick = { showTimePicker = true },
            ) {
                Text(text = "Time Picker")
            }

            Text(
                text = if (selectedTimeMillis == 0L) {
                    "Time not selected"
                } else {
                    val formattedTime = "${timePickerState.hour}:%02d".format(timePickerState.minute)
                    "Selected Time: $formattedTime"
                }
            )

            // Date Picker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedDateMillis = datePickerState.selectedDateMillis ?: 0
                                showDatePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) { Text("Cancel") }
                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }

            // Time Picker Dialog
            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedTimeMillis = ((timePickerState.hour.toLong() * 60 * 60 * 1000) + (timePickerState.minute.toLong() * 60 * 1000)) * 60
                                showTimePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showTimePicker = false
                            }
                        ) { Text("Cancel") }
                    }
                ) {
                    TimePicker(
                        state = timePickerState
                    )
                }
            }

            Button(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: 0
                    val eventDateTime = selectedDateMillis + selectedTimeMillis / 60
                    Log.d("Date", selectedDateMillis.toString())
                    Log.d("Time", selectedTimeMillis.toString())
                    Log.d("DateTime", eventDateTime.toString())
                    viewModel.addOrUpdateEvent(
                        Event(
                            id = eventId.orEmpty(), // Empty string if null
                            eventTitle = eventTitle.text,
                            eventRemarks = eventRemarks.text,
                            modId = selectedModule,
                            eventDateTime = eventDateTime
                        )
                    )
                    navController.popBackStack()
                }
            ) {
                Text("Add Event")
            }

            if (!eventId.isNullOrBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = {
                            viewModel.deleteEvent(eventId)
                            navController.popBackStack() // Optionally, navigate back after deletion
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Event"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

