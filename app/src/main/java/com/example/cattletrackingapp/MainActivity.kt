package com.example.cattletrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme
import com.example.cattletrackingapp.ui.navigation.MainNavHost
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CattleTrackingAppTheme {
                enableEdgeToEdge()
                MainNavHost()

            }
        }
    }
}