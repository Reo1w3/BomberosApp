package com.example.bomberosapp

import android.app.Application
import com.google.firebase.FirebaseApp
import org.osmdroid.config.Configuration
import java.io.File

class BomberosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Configuración de OpenStreetMap (osmdroid)
        Configuration.getInstance().userAgentValue = packageName
        val osmConfig = Configuration.getInstance()
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        osmConfig.osmdroidTileCache = File(basePath, "tile")
    }
}
