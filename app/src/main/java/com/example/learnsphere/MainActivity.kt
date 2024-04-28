package com.example.learnsphere

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.learnsphere.navGraph.NavGraph
import com.example.learnsphere.ui.theme.LearnSphereTheme

class MainActivity : ComponentActivity() {

  /*private lateinit var nfcAdapter: NfcAdapter
  private val attendanceViewModel: AttendanceViewModel by viewModels()

  var myTag: Tag? = null*/

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()

    // Get the default NFC adapter
    /*nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    readFromIntent(intent)

    val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    tagDetected.addCategory(Intent.CATEGORY_DEFAULT)*/

    setContent {
      window.decorView.apply {
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        MainApp()
      }
    }
  }

  /*override fun onResume() {
    super.onResume()
    val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    var pendingIntent = PendingIntent.getActivity(
      this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    )

    val intentFilters = arrayOf<IntentFilter>(
      IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
      IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
      IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
    )
    nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
  }


  override fun onPause() {
    super.onPause()
*//*    val nfcAdapter = NfcAdapter.getDefaultAdapter(this)*//*
    nfcAdapter.disableForegroundDispatch(this)
  }

  //https://stackoverflow.com/questions/7552339/how-to-read-nfc-tags-in-android
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    readFromIntent(intent)
  }

  private fun readFromIntent(intent: Intent) {
    val action = intent.action
    if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
      myTag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?
      val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
      var msgs = mutableListOf<NdefMessage>()
      if (rawMsgs != null) {
        for (i in rawMsgs.indices) {
          msgs.add(i, rawMsgs[i] as NdefMessage)
        }
        buildTagViews(msgs.toTypedArray())
      }
    }
  }
  private fun buildTagViews(msgs: Array<NdefMessage>) {
    if (msgs.isEmpty()) return
    var text = ""
    val payload = msgs[0].records[0].payload
    val textEncoding: Charset = if ((payload[0] and 128.toByte()).toInt() == 0) Charsets.UTF_8 else Charsets.UTF_16 // Get the Text Encoding
    val languageCodeLength: Int = (payload[0] and 51).toInt() // Get the Language Code, e.g. "en"
    try {
      // Get the Text
      text = String(
        payload,
        languageCodeLength + 1,
        payload.size - languageCodeLength - 1,
        textEncoding
      )
    } catch (e: UnsupportedEncodingException) {
      Log.e("UnsupportedEncoding", e.toString())
    }
    Toast.makeText(this, "NFC tag detected: $text", Toast.LENGTH_LONG).show()
    attendanceViewModel.updateNfcContent(text)
  }*/

}

  @Preview(showBackground = true)
  @Composable
  fun MainApp() {
    var navController = rememberNavController()
    LearnSphereTheme {
      // A surface container using the 'background' color from the theme
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
      ) {
        NavGraph(
          navController = navController,
        )
      }
    }
  }