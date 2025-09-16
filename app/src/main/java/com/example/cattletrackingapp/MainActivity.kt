package com.example.cattletrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cattletrackingapp.ui.screens.FarmersScreen
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CattleTrackingAppTheme {
                FarmersScreen()
            }
        }
    }
}