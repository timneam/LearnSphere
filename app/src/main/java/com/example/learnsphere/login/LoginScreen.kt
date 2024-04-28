package com.example.learnsphere

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.components.LoadingIndicator
import com.example.learnsphere.navGraph.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.learnsphere.ui.theme.*

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope to handle the login process

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(70.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "LearnSphere Logo",
            modifier = Modifier.padding(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            enabled = !loading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier.
            fillMaxWidth(),
//            padding(40.dp, 0.dp, 40.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                onClick = {
                    loading = true
                    val trimmedEmail = email.trim()
                    loginUser(auth, trimmedEmail, password) { success, message ->
                        loginMessage = message
                        coroutineScope.launch {
                            if (success) {
                                navController.navigate(Screen.HomeScreen.route) {
                                    popUpTo(Screen.LoginScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.weight(1f) // Use weight to fill the available space
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.SignUpScreen.route)
                },
                enabled = !loading,
                modifier = Modifier.weight(1f) // Use weight to fill the available space
            )
            {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
            navController.navigate(Screen.ResetPasswordScreen.route)
        },
            enabled = !loading)
        {
            Text("Reset Password")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            LoadingIndicator(chosenIndicator = "Custom")
        }

        Text(loginMessage)
    }
}

private fun loginUser(auth: FirebaseAuth, email: String, password: String, callback: (Boolean, String) -> Unit) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Login successful")
            } else {
                callback(false, "Login failed: ${task.exception?.message ?: "Unknown error"}")
            }
        }
    } else {
        callback(false, "Email and password cannot be empty")
    }
}
