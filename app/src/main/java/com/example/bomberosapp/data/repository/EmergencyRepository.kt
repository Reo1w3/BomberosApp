package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.network.FirebaseClient
import kotlinx.coroutines.tasks.await

class EmergencyRepository {
    
    private val db = FirebaseClient.db

    suspend fun saveEmergency(emergency: Emergency): Boolean {
        FirebaseClient.connect()
        return try {
            db.collection("emergencias").add(emergency).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("EmergencyRepository", "Error saving emergency: ${e.message}")
            false
        }
    }
}
