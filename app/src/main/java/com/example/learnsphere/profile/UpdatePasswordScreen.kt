package com.example.learnsphere.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navGraph.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UpdatePasswordScreen(
//    modifier: Modifier = Modifier,
    navController: NavController
) {
    var newPassword by remember { mutableStateOf("") }
    var cfmPassword by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Lock,
            contentDescription = "Password Icon",
            modifier = Modifier
                .size(200.dp) // Adjust the size of the icon as needed
                .padding(bottom = 16.dp)
        )
        // New Password input field
        TextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                isError = false
            },
            label = { Text("New Password") },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.background)
        )

        // Confirm Password input field
        TextField(
            value = cfmPassword,
            onValueChange = {
                cfmPassword = it
                isError = false
            },
            label = { Text("Confirm Password") },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.background)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Button to trigger password update
        Button(
            enabled = validatePasswords(newPassword, cfmPassword),
            onClick = {
                updatePassword(newPassword, navController)
            },
        ) {
            Text("Update Password")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate back to previous screen
        Button(
            onClick = {
                navController.popBackStack()
            },
        ) {
            Text("Return")
        }

        // Handle error state
        if (isError) {
            // Show error message
            Text("Password fields cannot be empty and must match.")
        }
    }
}

fun updatePassword(newPassword: String, navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        try {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UpdatePassword", "Password updated successfully")
                        // Password updated successfully
                        // Handle success
                    } else {
                        // Handle failure to update password
                        val exception = task.exception
                        Log.e("UpdatePassword", "Error: ${exception?.message}")

                        // Password updated successfully
                        // Handle success, navigate to appropriate screen
                        navController.navigate(Screen.HomeScreen.route)
                    }
                }
        } catch (e: Exception) {
            // Handle failure to update password
            // Show appropriate error messages or handle errors
            Log.e("UpdatePassword", "Error: ${e.message}")
        }
    } else {
        // User is not authenticated
        // Handle accordingly
        Log.e("UpdatePassword", "User not authenticated")
    }
}

@Composable
fun validatePasswords(
    newPassword: String, confirmPassword: String
): Boolean {
    return newPassword.isNotEmpty() && newPassword == confirmPassword
}
