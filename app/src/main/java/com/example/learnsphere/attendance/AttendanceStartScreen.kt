package com.example.learnsphere.attendance

import AttendanceViewModel
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.home.CoursesList
import com.example.learnsphere.home.ModuleClass
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Module(
    val courseModId: String,
    val moduleName: String,
    val modCode: String,
    val modLead: String,
    val courseId: String
)

data class Course(
    val courseId: String,
    val courseName: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceStartScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel
) {
    val db = Firebase.firestore
    var message by remember { mutableStateOf("") }
    val qnaId by attendanceViewModel.qnaId.collectAsState()
    val coursesList = remember { mutableListOf<Course>() }
    val moduleList = remember { mutableListOf<Module>() }

    LaunchedEffect(Firebase.auth.currentUser) {
        val courseCollection = db.collection("courses").get().await()
        val moduleCollection =
            courseCollection.documents[0].reference.collection("modules").get().await()

        for (course in courseCollection) {
            val courseId = course.id
            val courseName = course.data["course_code"].toString()
            coursesList.add(Course(courseId, courseName))
            Log.d("Add Announcement Screen", coursesList.toString())
        }

        for (module in moduleCollection) {
            val courseModId = module.id
            val moduleName = module.data["mod_name"].toString()
            val modCode = module.data["mod_code"].toString()
            val moduleLead = module.data["mod_lead"].toString()
            val courseId = module.reference.parent.parent?.id
            moduleList.add(Module(courseModId, moduleName, modCode, moduleLead, courseId.toString()))
            Log.d("Add Announcement Screen", moduleList.toString())
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {
        it

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // This will navigate to the ScanNFCScreen once qnaId is not null
            var isModuleExpanded by remember { mutableStateOf(false) }
            var selectedModule by remember { mutableStateOf("") }
            var selectedModuleCourse by remember { mutableStateOf("") }
            var selectedModuleCode by remember { mutableStateOf("") }
            val errorCheck by remember { mutableStateOf(false) }
            LaunchedEffect(qnaId) {
                if (qnaId != null) {
                    navController.navigate("scan_nfc_screen")
                }
            }

            if (errorCheck) {
                Text("Select a module")
            }

            Text(text = "Select Module")
            
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                ExposedDropdownMenuBox(
                    expanded = isModuleExpanded,
                    onExpandedChange = { isModuleExpanded = it }
                ) {
                    TextField(
                        value = selectedModule,
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
                                selectedModule = module.moduleName
                                selectedModuleCourse = module.courseId
                                selectedModuleCode = module.modCode
                            })
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                attendanceViewModel.startAttendance(modCode = selectedModuleCode, courseId = selectedModuleCourse)
            }) {
                Text("Start Attendance")
            }

            // Display the message below the button
            if (message.isNotBlank()) {
                Text(message)
                Text(text = "QNA ID: $qnaId")
            }
        }
    }
}



