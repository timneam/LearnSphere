package com.example.learnsphere.profile

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.learnsphere.R
import com.example.learnsphere.components.ShimmerEffect
import com.example.learnsphere.navGraph.Screen
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

suspend fun getProfilePic(): String? {
    val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userId = user!!.uid

    try {
        val imageName = db.collection("users").document(user.uid).get().await()
            .getString("profilePic")

        if (!imageName.isNullOrBlank()) {
            val imageRef = storageReference.child("profile_images/$imageName").downloadUrl.await()
            return imageRef.toString()
        }
    } catch (e: Exception) {
        // Handle exceptions, e.g., document not found, storage URL retrieval failure
        Log.e("GetProfilePic", "Error: ${e.message}")
    }

    return null
}

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val showShimmer = remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {
        it
        Column(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture(showShimmer = showShimmer.value, navController = navController)

            ProfileInformation(showShimmer = showShimmer.value)

            // Edit setting button - to go to setting screen
//            ElevatedButton(
//                onClick = {
//                    navController.navigate(Screen.SettingScreen.route)
//                },
//            ) {
//                Text(text = "Edit Settings")
//            }


//             Edit setting button - to go to setting screen

            Spacer(modifier = Modifier.height(20.dp))

            ElevatedButton(
                onClick = {
                    navController.navigate(Screen.UpdatePasswordScreen.route)
                },
            ) {
                Text(text = "Update Password")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sign out button - to sign out and go to login screen
            SignOutButton(navController = navController)

        }
    }
}

@Composable
fun ProfilePicture(
    showShimmer: Boolean = true,
    navController: NavController
) {
    var profilePicUrl by remember { mutableStateOf<String?>(null) }
    var isContentLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val url = getProfilePic()
        if (!url.isNullOrBlank()) {
            // Set the profile picture URL
            profilePicUrl = url
            isContentLoaded = true
        }
    }
    Image(
        painter = if (profilePicUrl.isNullOrBlank()) {
            painterResource(id = R.drawable.profile_pic_icon)
        } else {
            rememberAsyncImagePainter(profilePicUrl)
        },
        contentDescription = "Profile Picture",
        modifier = Modifier
            .clickable {
                // Navigate to the desired screen when clicked
                navController.navigate(Screen.UpdatePFPScreen.route)
            }
            .padding(30.dp)
            .size(200.dp)
            .clip(shape = CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.inverseSurface, CircleShape)
            .background(ShimmerEffect(targetValue = 1000f, showShimmer = showShimmer && !isContentLoaded)),
        contentScale = ContentScale.Crop,
    )
//  Text(text = "${profilePicUrl}", modifier = Modifier.padding(30.dp))
}

@Composable
fun ProfileInformation(
    showShimmer: Boolean = true
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
//    val userId = user!!.uid
    var isContentLoaded by remember { mutableStateOf(false) }

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
            .addOnCompleteListener {
                isContentLoaded = true
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Username: ",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )
            Text(
                text = "${firstName} ${lastName}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                modifier = Modifier
                    .background(ShimmerEffect(targetValue = 1000f, showShimmer = showShimmer && !isContentLoaded))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Email: ",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )
            Text(
                text = "${email}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier
                    .background(ShimmerEffect(targetValue = 1000f, showShimmer = showShimmer && !isContentLoaded))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Course: ",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )
            Text(
                text = "${course}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                modifier = Modifier
                    .background(ShimmerEffect(targetValue = 1000f, showShimmer = showShimmer && !isContentLoaded))
            )
        }

    }
}

@Composable
fun SignOutButton(navController: NavController) {
    Button(onClick = {
        FirebaseAuth.getInstance().signOut()
        // After sign out, redirect back to the login screen
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(0) { inclusive = true } // Clear the back stack correctly
        }
    }) {
        Text("Sign Out")
    }
}

