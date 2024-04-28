import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphere.attendance.AttendanceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.Manifest


class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    // Direct instantiation of the repository
    private val db = FirebaseFirestore.getInstance()
    private val repository = AttendanceRepository(db)

    private val _qnaId = MutableStateFlow<String?>(null)
    val qnaId: StateFlow<String?> = _qnaId.asStateFlow()

    private val _attendanceId = MutableStateFlow<String?>(null)
    val attendanceId: StateFlow<String?> = _attendanceId.asStateFlow()

    private val _nfcTag = MutableStateFlow<String?>(null)
    val nfcTag: StateFlow<String?> = _nfcTag.asStateFlow()

    private val _qrcode = MutableStateFlow<String?>(null)
    val qrcode: StateFlow<String?> = _qrcode.asStateFlow()

    private val _lat = MutableStateFlow<Double>(0.0)
    val lat: StateFlow<Double> = _lat.asStateFlow()

    private val _long = MutableStateFlow<Double>(0.0)
    val long: StateFlow<Double> = _long.asStateFlow()

    fun updateNfcContent(content: String) {
        // Update the state within the ViewModel
        Log.d("AttendanceViewModel", "NFC content: $content")
        _nfcTag.value = content
    }

    fun updateQrContent(content: String) {
        // Update the state within the ViewModel
        _qrcode.value = content
    }

    fun updateLatLong(lat: Double, long: Double) {
        // Update the state within the ViewModel
        _lat.value = lat
        _long.value = long
    }

    fun markAttendance(attendanceId: String, userId: String, latitude: Double, longitude: Double) {
        repository.addToHeadCount(attendanceId, userId, latitude, longitude) { success, qnaId, message ->
            if (success) {
                // Use the qnaId here if needed
                // For example, save it in a state or navigate to another screen with it
                _qnaId.value = qnaId
            } else {
                // Handle error, such as showing an error message
            }
        }
    }

    fun startAttendance(modCode: String, courseId: String) {
        // Using coroutine to handle asynchronous operation
        viewModelScope.launch {
            repository.startAttendance(modCode = modCode, courseId = courseId) { success, message, id ->
                if (success) {
                    _qnaId.value = id
                    // Handle any additional logic here such as navigation or showing a success message
                } else {
                    // Handle the error case, e.g., show an error message
                }
            }
        }
    }

    fun nfcScanStore(qnaId: String, instructorId: String, venueId: String) {
        viewModelScope.launch {
            repository.nfcScanStore(qnaId, instructorId, venueId) { success, result ->
                if (success) {
                    // Handle success, e.g., by updating UI state or navigatingqnaId
                    _attendanceId.value = result
                    Log.d("AttendanceViewModel", "NFC scan stored successfully with ID: $result")
                } else {
                    // Handle failure, e.g., by showing an error message
                }
            }
        }
    }

    fun generateQRCode(text: String): Bitmap? {
        if (text.isEmpty()) return null
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted, handle accordingly
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                }
            }
            .addOnFailureListener {
                // Handle failure to get location
            }
    }

}
