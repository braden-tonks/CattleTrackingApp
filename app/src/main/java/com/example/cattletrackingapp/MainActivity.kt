package com.example.cattletrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cattletrackingapp.data.local.sync.SyncManager
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme
import com.example.cattletrackingapp.ui.navigation.MainNavHost
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            syncManager.syncAll()
        }

        setContent {
            CattleTrackingAppTheme {
                enableEdgeToEdge()
                MainNavHost()

            }
        }
    }
}