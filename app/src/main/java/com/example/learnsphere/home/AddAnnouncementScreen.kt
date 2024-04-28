@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.learnsphere.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class ModuleClass(
//    val modCode: String,
//    val modLead: String,
    val courseModId: String,
    val moduleName: String
)

data class CoursesList(
    val courseId: String,
    val courseName: String,
)

@Composable
fun AddAnnouncementScreen(
    navController: NavController,
) {
    val db = Firebase.firestore
    val coursesList = remember { mutableListOf<CoursesList>() }
    val moduleList = remember { mutableListOf<ModuleClass>() }

    LaunchedEffect(Firebase.auth.currentUser) {
        val courseCollection = db.collection("courses").get().await()
        val moduleCollection =
            courseCollection.documents[0].reference.collection("modules").get().await()

        for (course in courseCollection) {
            val courseId = course.id
            val courseName = course.data["course_code"].toString()
            coursesList.add(CoursesList(courseId, courseName))
            Log.d("Add Announcement Screen", coursesList.toString())
        }

        for (module in moduleCollection) {
            val courseModId = module.id
            val moduleName = module.data["mod_name"].toString()
            moduleList.add(ModuleClass(courseModId, moduleName))
            Log.d("Add Announcement Screen", moduleList.toString())
        }

    }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {
        it
        // Add Announcement Screen
        AddAnnouncementUI(coursesList, moduleList)
    }
}

@Composable
fun AddAnnouncementUI(
    courseList: MutableList<CoursesList>,
    moduleList: MutableList<ModuleClass>,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
//        var isCourseExpanded by remember { mutableStateOf(false) }
//        var selectedCourse by remember { mutableStateOf("") }

        var isModuleExpanded by remember { mutableStateOf(false) }
        var selectedModule by remember { mutableStateOf("") }
        val db = Firebase.firestore

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Announcement Screen",
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for selecting course
//            Text(text = "Select course to add announcement to")
//            Box(
//                modifier = Modifier,
//                contentAlignment = Alignment.Center
//            ) {
//                ExposedDropdownMenuBox(
//                    expanded = isCourseExpanded,
//                    onExpandedChange = { isCourseExpanded = it }
//                ) {
//                    TextField(
//                        value = selectedCourse,
//                        onValueChange = {},
//                        readOnly = true,
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCourseExpanded)
//                        },
//                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
//                        modifier = Modifier.menuAnchor(),
//                    )
//                    ExposedDropdownMenu(
//                        expanded = isCourseExpanded,
//                        onDismissRequest = { isCourseExpanded = false }
//                    ) {
//                        // Populate the dropdown with the list of courses
//                        for (course in courseList) {
//                            DropdownMenuItem(text = {
//                                Text(course.courseName)
//                            }, onClick = {
//                                isCourseExpanded = false
//                                selectedCourse = course.courseName
//                            })
//                        }
//                    }
//                }
//            }

            // Dropdown for selecting module
            Text(text = "Select module to add announcement to")
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
                                selectedModule = module.courseModId
                            })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            var announcementContent by remember { mutableStateOf("") }
            OutlinedTextField(
                value = announcementContent,
                onValueChange = { announcementContent = it},
                label = { Text("Announcement") },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                // Add announcement to the selected module in the selected course in the database
                // Ensure a module is selected
                if (selectedModule.isNotEmpty()) {
                    // Create a data object with the required fields
                    val announcementData = hashMapOf(
                        "mod_id" to selectedModule,
                        "date_time" to System.currentTimeMillis().toString(),
                        "content" to announcementContent
                    )

                    // Add the announcement data to the Firestore database
                    db.collection("announcements").add(announcementData)
                        .addOnSuccessListener {
                            Log.d("Add Announcement", "Announcement added successfully")
                            // navigate back to the home screen
                        }
                        .addOnFailureListener { e ->
                            Log.w("Add Announcement", "Error adding announcement", e)
                        }
                }
            }) {
                Text(text = "Add Announcement")
            }
        }
    }
}