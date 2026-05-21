package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.Piloto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PilotoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun guardarPiloto(piloto: Piloto): Boolean {
        return try {
            db.collection("piloto")
                .add(piloto)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}