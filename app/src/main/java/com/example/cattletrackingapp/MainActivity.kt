package com.example.cattletrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme
import com.example.cattletrackingapp.ui.navigation.MainNavHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CattleTrackingAppTheme {
                MainNavHost()

            }
        }
    }
}