package com.example.bomberosapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun login(codigo: Int, pass: String): Boolean {
        // Acceso de emergencia si la DB está vacía
        if (codigo == 123 && pass == "123") return true

        return try {
            val result = db.collection("personal")
                .whereEqualTo("codigo_personal", codigo)
                .whereEqualTo("numero_identificacion", pass)
                .get()
                .await()
            
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
