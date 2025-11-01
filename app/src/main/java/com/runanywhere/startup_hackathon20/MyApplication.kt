package com.runanywhere.startup_hackathon20

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.i("MyApp", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("MyApp", "Firebase initialization failed: ${e.message}")
        }

        Log.i("MyApp", "App using Google Gemini API - no local model downloads needed")
    }
}
