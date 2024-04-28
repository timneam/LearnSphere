package com.example.learnsphere.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navGraph.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SettingScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // screen title
        Text(
            text = "Settings",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        // Switch area
        SwitchSetting()

        // Information area
        InformationSetting()

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(onClick = {
                navController.navigate(Screen.UpdatePasswordScreen.route)
            }) {
                Text("Update password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(0.dp, 40.dp)
            ) {
                Text("Save Settings")
            }
        }
    }
}

@Composable
fun SwitchSetting() {
    Column(
        modifier = Modifier
            .padding(50.dp, 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GPS",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )

            Switch(
                checked = false,
                onCheckedChange = { /*TODO*/ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NFC",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )

            Switch(
                checked = false,
                onCheckedChange = { /*TODO*/ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Camera",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )

            Switch(
                checked = false,
                onCheckedChange = { /*TODO*/ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            )
        }

    }
}

@Composable
fun InformationSetting() {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    if (user != null) {
        db.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    firstName = document.getString("firstName")?: ""
                    lastName = document.getString("lastName")?: ""
                    course = document.getString("course")?: ""
                    email = user.email.toString()
                }
            }
    }

    Column(
        modifier = Modifier
            .padding(20.dp, 10.dp)
    ) {
        TextField(
            value = "Joseph Wong",
            onValueChange = {/*TODO*/ },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 20.dp),
            readOnly = true
        )

        TextField(
            value = "2201291@sit.singaporetech.edu.sg",
            maxLines = 1,
            onValueChange = {/*TODO*/ },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 20.dp),
            readOnly = true
        )

        TextField(
            value = "Degree in Computing Science",
            maxLines = 1,
            onValueChange = {/*TODO*/ },
            label = { Text("Course") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 20.dp),
            readOnly = true
        )

    }
}
