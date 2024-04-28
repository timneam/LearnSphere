package com.example.learnsphere.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AttendanceUserCheck(
    navController: NavController,
) {
    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {
        it
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Attendance User Check Screen",
                modifier = Modifier.padding(bottom = 24.dp)
            )
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid
            var userRole by remember { mutableStateOf<String?>(null) }
            val db = FirebaseFirestore.getInstance()

            // Check the user role when the composable enters the composition
            LaunchedEffect(uid) {
                uid?.let {
                    val docRef = db.collection("users").document(it)
                    docRef.get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            userRole = document.getString("role")
                        } else {
                            // Handle the case where the user document doesn't exist
                        }
                    }.addOnFailureListener { exception ->
                        // Handle failure
                    }
                }
            }
            // Display buttons based on user role
            when (userRole) {
                "student" -> {
                    navController.navigate("scan_attendance_screen")
                }
                "instructor" -> {
                    navController.navigate("attendance_start_screen")
                }
                else -> {
                    // Optional: Handle unexpected user role or when userRole is null
                }
            }
        }
    }
}