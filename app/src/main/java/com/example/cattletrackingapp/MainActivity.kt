package com.example.cattletrackingapp

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cattletrackingapp.ui.components.handleNfcIntent
import com.example.cattletrackingapp.ui.navigation.MainNavHost
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme

class MainActivity : ComponentActivity() {

    // Compose-friendly state
    var nfcTagData by mutableStateOf("No tag read yet")

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFilters: Array<IntentFilter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // PendingIntent for foreground dispatch
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        // Intent filters
        val tagFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        intentFilters = arrayOf(tagFilter)

        // Set up Compose
        setContent {
            CattleTrackingAppTheme {
                enableEdgeToEdge()
                MainNavHost()
            }
        }
    }

    // Enable foreground dispatch while app is visible
    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    // Called when NFC tag is detected
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent, { data ->
            nfcTagData = data
        }, this)
    }
}
