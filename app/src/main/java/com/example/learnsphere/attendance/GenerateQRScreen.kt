import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GenerateQRScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel
) {
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val attendanceId by attendanceViewModel.attendanceId.collectAsState()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Generate QR Code")

        qrCodeBitmap = attendanceId?.let { attendanceViewModel.generateQRCode(it) }

        qrCodeBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.padding(16.dp)
            )
        }
        BackHandler {
            navController.popBackStack("home_screen", false)
        }
    }
}



