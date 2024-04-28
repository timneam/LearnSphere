package com.example.learnsphere

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navGraph.Screen
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.android.gms.maps.model.Circle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AnnouncementClass(
    val content: String,
    val dateTime: String,
    val modId: String
)

data class ModuleClass(
//    val modCode: String,
//    val modLead: String,
    val courseModId: String,
    val moduleName: String
)

data class UserClass(
    val course: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePic: String = "",
    val userName: String = "",
    val role: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val user = auth.currentUser
    val userDoc = db.collection("users")
    val courseCollection = db.collection("courses")
    val announcementCollection = db.collection("announcements").get()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var userCourse by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    var userClass by remember { mutableStateOf<UserClass>(UserClass()) }

    var announcementList = remember {
        mutableListOf<AnnouncementClass>()
    }

    var moduleList = remember {
        mutableListOf<ModuleClass>()
    }

    LaunchedEffect(user) {
        if (user != null) {
            GlobalScope.launch(Dispatchers.IO) {
                val userDoc = userDoc.document(user.uid).get().await()

                userClass = UserClass(
                    firstName = userDoc.getString("firstName") ?: "",
                    lastName = userDoc.getString("lastName") ?: "",
                    course = userDoc.getString("course") ?: "",
                    userName = userDoc.getString("userName") ?: "",
                    profilePic = userDoc.getString("profilePic") ?: "",
                    role = userDoc.getString("role") ?: ""
                )
                Log.d(
                    "HomeScreen->UserDoc",
                    "User document retrieved: ${userClass.toString()}"
                )


                Log.d("HomeScreen", "=== Getting course collection === \n $userClass")

                // Fetch courses based on userCourse
                val courseCol = courseCollection
                    .whereEqualTo("course_code", userClass.course)
                    .get().await()

                Log.d(
                    "HomeScreen->CourseCollection",
                    "Getting Course Success: ${courseCol.documents}"
                )
                val moduleCollection =
                    courseCol.documents[0].reference.collection("modules").get().await()
                Log.d("HomeScreen->ModColCheck", "modCol = $moduleCollection")

                for (eachMod in moduleCollection) {
                    moduleList.add(
                        ModuleClass(
                            courseModId = eachMod.id,
                            moduleName = eachMod.getString("mod_name") ?: ""
                        )
                    )
                }
                // Assuming courseDoc is the reference to a specific course document


                val announcementCol = announcementCollection.await()

                Log.d("AnnouncementChecker", "Getting announcements and adding to list")
                for (doc in announcementCol) {
                    // find module name from document
                    announcementList.add(
                        AnnouncementClass(
                            content = doc.getString("content") ?: "",
                            dateTime = doc.getString("date_time") ?: "",
                            modId = doc.getString("mod_id") ?: ""
                        )
                    )
                }
            }
        }
    }

    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        // handle QRResult
        Log.d("Home Screen", result.toString())
    }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        },
    ) {
        it

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
        ) {
            if (userClass.firstName.isNotEmpty() && userClass.lastName.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Welcome back, $firstName $lastName!",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            text = "Announcements",
                        )
                        // Map Icon
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Map",
                            tint = MaterialTheme.colorScheme.primary,// Adjust tint color if needed
                            modifier = Modifier
                                .shadow(1.dp, clip = true, shape = RoundedCornerShape(1.dp))
                                .padding(8.dp)
                                .size(40.dp)
                                .clickable { navController.navigate(Screen.MapScreen.route) }
                        )
                    }
                }

//                ElevatedButton(onClick = {
////                    navController.navigate(Screen.QAScreen.route)
//                    navController.navigate(Screen.QAScreen.route + "?qnaId=" + "TVQygrnItIqb2N4Ns2eI")
//                }) {
//                    Text("To Q&A")
//                }
                // Show button if user role is an instructor
                if (userClass.role == "instructor") {
                    ElevatedButton(onClick = {
                        navController.navigate(Screen.AddAnnouncementScreen.route)
                    }) {
                        Text("Add Announcements")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                ) {
                    Log.d("HomeScreen->LazyColCheckML", moduleList.toString())
                    Log.d("HomeScreen->LazyColCheckAL", announcementList.toString())

                    items(announcementList.size) { index ->
                        val announcementItem = announcementList[index]
                        val modName =
                            moduleList.filter { it.courseModId == announcementItem.modId }[0].moduleName
                        // Create UI for each AnnouncementClass instance
                        Card(
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(10.dp),
                        ) {
                            Column(
                                Modifier.padding(20.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
//                                    text = "Module: ${modName}",
                                    text = modName,
//                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                if (announcementItem.content.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        text = "${announcementItem.content}",
                                    )
                                } else {
                                    Text(text = "No Announcements")
                                }

                                Text(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    text = announcementItem.dateTime,
                                    softWrap = true,
                                    textAlign = TextAlign.Right,
                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.SemiBold,

                                    )
                            }
                        }
                    }
                }
            } else {
                Text(text = "This is the home-screen")
            }

            Spacer(modifier = Modifier.height(32.dp)) // Add some space before the sign out button

        }
    }

}
