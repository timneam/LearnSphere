package com.example.learnsphere.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.components.LoadingIndicator
import com.example.learnsphere.navGraph.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navController: NavController, modifier: Modifier = Modifier
) {
    val auth = FirebaseAuth.getInstance()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var profilePic by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var signUpMessage by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope to handle the signup process

    val db = Firebase.firestore

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Role") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            loading = true
            if (password == confirmPassword) {
                createUserWithEmailAndPassword(
                    auth,
                    db,
                    email,
                    password,
                    firstName,
                    lastName,
                    userName,
                    course,
                    profilePic,
                    role
                ) { success, message ->
                    signUpMessage = message
                    coroutineScope.launch {
                        if (success) {
                            // Navigate to login screen or home screen as needed
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(Screen.LoginScreen.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    loading = false
                }
            } else {
                signUpMessage = "Passwords do not match."
            }
        },
            enabled = !loading
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            LoadingIndicator()
        }

        Text(signUpMessage)
    }
}

private fun createUserWithEmailAndPassword(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    userName: String,
    course: String,
    profilePic: String,
    role: String,
    callback: (Boolean, String) -> Unit
) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                val userMap = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "userName" to userName,
                    "course" to course,
                    "profilePic" to profilePic,
                    "role" to role
                )

                db.collection("users").document(userId).set(userMap).addOnSuccessListener {
                    callback(true, "Sign Up successful and user data saved")
                }.addOnFailureListener { e ->
                    callback(false, "Sign Up successful but failed to save user data: ${e.message}")
                }
            } else {
                callback(false, "Sign Up failed: ${task.exception?.message ?: "Unknown error"}")
            }
        }
    } else {
        callback(false, "Email and password cannot be empty")
    }
}

