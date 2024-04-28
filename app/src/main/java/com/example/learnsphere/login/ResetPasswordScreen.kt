package com.example.learnsphere.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun ResetPasswordScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to trigger password reset
        Button(
            onClick = {
                // Assuming resetPassword is a suspend function
                isLoading = true
                resetPassword(email) { success ->
                    isLoading = false
                    isError = !success
                    showMessage = success
                }
            }) {
            Text("Reset Password")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(text = "Return")
        }

        // Handle error state
        if (isError) {
            // Show error message
            Text("Failed to reset password. Please try again.")
        }

        // Show success message using a Snackbar
        if (showMessage) {
            LaunchedEffect(showMessage) {
                // Delay for a short period to show the Snackbar
                delay(300)
                showMessage = false
            }

            // Display Snackbar with success message
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(
                        onClick = {
                            // Handle any action upon clicking the Snackbar action
                            showMessage = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                Text("Password reset email sent successfully. Check your email for instructions.")
            }
        }
    }
}

private fun resetPassword(email: String, onComplete: (Boolean) -> Unit) {
    Firebase.auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
            if (task.isSuccessful) {
                Log.d("ResetPassword", "Email sent successfully")
            } else {
                Log.e("ResetPassword", "Error: ${task.exception?.message}")
            }
        }
}
