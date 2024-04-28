package com.example.learnsphere.attendance

import AttendanceViewModel
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.learnsphere.R
import java.io.UnsupportedEncodingException
import kotlin.experimental.and

class ScanNFCActivity : ComponentActivity() {
  private var nfcAdapter: NfcAdapter? = null

  // Starting the app via NFC tag scan? onCreate() will be called.
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Check if NFC is supported and enabled
    nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    if (intent != null) {
      Log.d("onCreateIntent", "Found intent in onCreate")
      processIntent(intent)
    }

    setContentView( R.layout.scan_activity )

  }

  override fun onResume() {
    super.onResume()
    var nfcPendingIntent = PendingIntent.getActivity(
      this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    )
    nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
  }

  override fun onPause() {
    super.onPause()
    nfcAdapter?.disableForegroundDispatch(this)
  }

  // App already running in the foreground? onNewIntent() will be called.
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)

    processIntent(intent)
  }

  private fun processIntent(checkIntent: Intent) {
    if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
      val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
      val messages = mutableListOf<NdefMessage>()
      if (rawMessages != null) {
        for (i in rawMessages.indices) {
          messages.add(rawMessages[i] as NdefMessage)
        }
        processNdefMessages(messages.toTypedArray())
      }
    }
  }

  private fun processNdefMessages(ndefMessages: Array<NdefMessage>) {
    if (ndefMessages.isEmpty()) return
    var nfcContent = ""
    val payload = ndefMessages[0].records[0].payload
    val textEncoding = if ((payload[0] and 128.toByte()).toInt() == 0) Charsets.UTF_8 else Charsets.UTF_16
    val languageCodeLength: Int = (payload[0] and 51.toByte()).toInt()
    try {
      nfcContent = String(
        payload,
        languageCodeLength + 1,
        payload.size - languageCodeLength - 1,
        textEncoding
      )
    } catch (e: UnsupportedEncodingException) {
      Log.e("UnsupportedEncoding", e.toString())
    }
    Toast.makeText(this, "NFC tag detected: $nfcContent", Toast.LENGTH_LONG).show()
    val scanTextView = findViewById<TextView>(R.id.scanText)
    scanTextView.text = nfcContent

    val resultIntent = Intent().apply {
      putExtra("nfcTag", nfcContent)
    }
    setResult(RESULT_OK, resultIntent)

    finish()
  }
}
