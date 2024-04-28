import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttendanceViewModelInstrumentedTest {

    @Test
    fun generateQRCode_ReturnsNonNullBitmap() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Create an instance of the AttendanceViewModel
        val attendanceViewModel = AttendanceViewModel(application = appContext.applicationContext as Application)

        // Mock data
        val text = "Sample Text"

        // Call the generateQRCode function
        val bitmap = attendanceViewModel.generateQRCode(text)

        // Assert that the returned bitmap is not null
        assertNotNull(bitmap)
    }
}
