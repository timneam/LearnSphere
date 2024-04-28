package com.example.learnsphere.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.learnsphere.navGraph.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Composable
fun UpdatePFPScreen(
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val user = auth.currentUser
    var profilePic by remember { mutableStateOf("") }
//    var userId by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val getImageContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    LaunchedEffect(user) {
        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        profilePic = document.getString("profilePic").toString()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        //Display the current profile picture

        selectedImageUri?.let { uri ->
            // Display the selected image using Image()
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
                    .clip(shape = CircleShape)
            )
        }

        Button(onClick = { getImageContent.launch("image/*") }) {
            Text(text = "Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to upload the selected image to Firebase Storage
        Button(
            onClick = {
                selectedImageUri?.let { uri ->
                    // Perform the upload operation outside the composable
                    uploadImageToFirebaseStorage(uri, navController)
                }
            },
            enabled = selectedImageUri != null
        ) {
            Text(text = "Update Profile Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
        ) {
            Text(text = "Return")
        }
//        Text(text = "File name: ${profilePic}")

    }
}

fun uploadImageToFirebaseStorage(imageUri: Uri, navController: NavController) {
    val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val user = auth.currentUser
    val userId = user!!.uid

    val imageName = "${userId}_profile_image"
    val imageRef = storageReference.child("profile_images/$imageName")
    Log.d("UpdatePFP-", imageRef.downloadUrl.toString())
    // Use a coroutine to perform the asynchronous operation

    imageRef.putFile(imageUri)
        .addOnFailureListener { exception ->
            // Handle failure when uploading the image
            Log.e("UpdatePFP", "Image upload failed: ${exception.message}")
        }
        .addOnSuccessListener { taskSnapshot ->
            // Image upload successful
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Get the download URL for the uploaded image
                Log.d("UpdatePFP", "Image upload successful. Download URL: $uri")

                // Update the user's profilePic in Firestore
//                user?.let {
                user.let {
                    db.collection("users").document(user.uid)
                        .update("profilePic", imageName)
                        .addOnSuccessListener {
                            // Handle success of updating the profilePic
                            Log.d("UpdatePFP", "ProfilePic updated successfully")

                            // Navigate to the desired destination
                             navController.navigate(Screen.ProfileScreen.route)
                        }
                        .addOnFailureListener { updateFailureException ->
                            // Handle failure of updating the profilePic
                            Log.e("UpdatePFP", "Failed to update profilePic: ${updateFailureException.message}")
                        }
                }
            }.addOnFailureListener { uriFailureException ->
                // Handle failure when retrieving the download URL
                Log.e("UpdatePFP", "Failed to get download URL: ${uriFailureException.message}")
            }
        }
}

@Preview(showBackground = true)
@Composable
fun UpdatePFPScreenPreview() {
    val navController = rememberNavController()
    UpdatePFPScreen(navController = navController)
}
