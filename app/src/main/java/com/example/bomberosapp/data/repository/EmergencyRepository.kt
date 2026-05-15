package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.Emergency
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmergencyRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    
    suspend fun saveEmergency(emergency: Emergency): Boolean {
        return try {
            db.collection("emergencias")
                .add(emergency)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
