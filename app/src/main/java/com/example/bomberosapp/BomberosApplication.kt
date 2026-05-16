package com.example.bomberosapp

import android.app.Application
import com.google.firebase.FirebaseApp

class BomberosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
