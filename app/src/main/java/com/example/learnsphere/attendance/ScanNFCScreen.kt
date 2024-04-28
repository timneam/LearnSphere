import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.attendance.ScanNFCActivity
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ScanNFCScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel
) {
    val qnaId by attendanceViewModel.qnaId.collectAsState()
    val nfcTag by attendanceViewModel.nfcTag.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    var context = LocalContext.current

    val scanNFCResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == -1) {
                val data: Intent? = result.data
                val nfcContent = data?.getStringExtra("nfcTag")
                if (nfcContent != null) {
                    Log.d("ScanNFCScreen", "NFC Tag: $nfcContent")
                    attendanceViewModel.updateNfcContent(nfcContent)
                } else {
                    Log.d("ScanNFCScreen", "No NFC Tag returned from ScanNFCActivity")
                }
            }
        }
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
        ) {
            Text("QnA ID: ${qnaId ?: "Not Set"}")

            Spacer(modifier = Modifier.height(20.dp)) // Add some space between the text and the button

            Text("NFC Tag: ${nfcTag ?: "Not Scanned"}")

            Spacer(modifier = Modifier.height(20.dp)) // Add some space between the text and the button

            Text(text = "Please scan the NFC to start the attendance")

            // When the button is clicked, the NFC tag will be scanned
            Button(onClick = {
                val intent = Intent(context, ScanNFCActivity::class.java)
                scanNFCResultLauncher.launch(intent)

            }) {
                Text("Scan NFC")
            }

            Spacer(modifier = Modifier.height(20.dp)) // Add some space between the button and the next text

            // When venueId gets populated, this block will run
            LaunchedEffect(nfcTag) {
                if (nfcTag != null && qnaId != null && uid != null) {
                    // Trigger NFC tag scan and store the result
                    attendanceViewModel.nfcScanStore(
                        qnaId = qnaId!!,
                        instructorId = uid,
                        venueId = nfcTag!!
                    )
                    // After the scan, navigate to the next screen
                    navController.navigate("generate_qr_screen")
                }
            }

            BackHandler {
                navController.popBackStack("home_screen", false)
            }
        }
    }
}
