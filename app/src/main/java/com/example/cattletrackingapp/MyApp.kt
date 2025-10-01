package com.example.cattletrackingapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    // Usually empty for Hilt purposes
    // Optional: app-wide initialization can go here
}