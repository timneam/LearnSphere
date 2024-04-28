import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import android.Manifest
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableDoubleStateOf
import com.example.learnsphere.navGraph.Screen
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun ScanAttendanceScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel
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
                text = "Scan Attendance Screen",
                modifier = Modifier.padding(bottom = 24.dp)
            )
            val qrcodeContent by attendanceViewModel.qrcode.collectAsState()
            val qnaId by attendanceViewModel.qnaId.collectAsState()
            val userLatitude by attendanceViewModel.lat.collectAsState()
            val userLongitude by attendanceViewModel.long.collectAsState()
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid


            // Permission launcher
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val granted = permissions.entries.all { it.value }
                    if (granted) {
                        // Permissions are granted, proceed to get location
                        attendanceViewModel.getCurrentLocation { latitude, longitude ->
                            // Use the location here
                            attendanceViewModel.updateLatLong(latitude, longitude)
                        }
                    } else {
                        // Handle the case where permissions are not granted
                    }
                }
            )

            // Request permissions when needed
            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
                val text = when (result) {
                    is QRResult.QRSuccess -> {
                        result.content.rawValue
                        // decoding with default UTF-8 charset when rawValue is null will not result in meaningful output, demo purpose
                            ?: result.content.rawBytes?.let { String(it) }.orEmpty()
                    }

                    QRResult.QRUserCanceled -> "User canceled"
                    QRResult.QRMissingPermission -> "Missing permission"
                    is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
                }
                Log.d("qrcodecontent", text)

                attendanceViewModel.updateQrContent(text)
            }

            ElevatedButton(
                onClick = { scanQrCodeLauncher.launch(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            {
                Text("Scan QR Code")
            }

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(qrcodeContent) {
                if (uid != null) {
                    Log.d("qr launch", "Latitude: $userLatitude, Longitude: $userLongitude")
                    qrcodeContent?.let {
                        attendanceViewModel.markAttendance(
                            it,
                            uid,
                            userLatitude,
                            userLongitude
                        )
                    }
                }
            }

            LaunchedEffect(qnaId) {
                delay(2000)
                Log.d("qna launch", "qna id: $qnaId")
                Log.d("qna launch", "Latitude: $userLatitude, Longitude: $userLongitude")
                if (qnaId != null) {
                    if (qnaId != "User not in vicinity") {
                        navController.navigate(Screen.QAScreen.route + "?qnaId=" + qnaId)
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "User not in vicinity",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                }
            }
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}