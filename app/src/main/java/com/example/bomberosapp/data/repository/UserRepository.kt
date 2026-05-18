package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.network.FirebaseClient
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseClient.db

    suspend fun login(codigo: String, pass: String): Boolean {
        val userTrim = codigo.trim()
        val passTrim = pass.trim()

        return try {
            FirebaseClient.connect()

            // Buscamos coincidencia exacta en la colección 'personal'
            val codigoInt = userTrim.toIntOrNull()
            
            // Intento 1: Buscar como Number
            var result = if (codigoInt != null) {
                db.collection("personal")
                    .whereEqualTo("codigo_personal", codigoInt)
                    .whereEqualTo("numero_identificacion", passTrim)
                    .get().await()
            } else null

            // Intento 2: Buscar como String (si el 1 falló o no es Int)
            if (result == null || result.isEmpty) {
                result = db.collection("personal")
                    .whereEqualTo("codigo_personal", userTrim)
                    .whereEqualTo("numero_identificacion", passTrim)
                    .get().await()
            }
            
            !result.isEmpty

        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error Firestore: ${e.message}")
            false
        }
    }
}
