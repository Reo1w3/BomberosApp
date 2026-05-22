package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.UserRole
import com.example.bomberosapp.data.network.FirebaseClient
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseClient.db

    suspend fun login(codigo: String, pass: String): UserRole {
        val userTrim = codigo.trim()
        val passTrim = pass.trim()

        return try {
            FirebaseClient.connect()

            // 1. Check in 'personal' (Admin / General Personnel)
            val codigoInt = userTrim.toIntOrNull()
            var result = if (codigoInt != null) {
                db.collection("personal")
                    .whereEqualTo("codigo_personal", codigoInt)
                    .whereEqualTo("numero_identificacion", passTrim)
                    .get().await()
            } else {
                db.collection("personal")
                    .whereEqualTo("codigo_personal", userTrim)
                    .whereEqualTo("numero_identificacion", passTrim)
                    .get().await()
            }

            // Búsqueda exhaustiva si no se encontró con el primer tipo
            if (result.isEmpty && codigoInt != null) {
                result = db.collection("personal")
                    .whereEqualTo("codigo_personal", userTrim)
                    .whereEqualTo("numero_identificacion", passTrim)
                    .get().await()
            }

            if (!result.isEmpty) {
                val doc = result.documents[0]
                val idTipoPersonal = doc.getLong("id_tipo_personal")?.toInt() ?: 0
                
                // Verificación de Admin robusta: por rol en DB o códigos maestros
                val isAdmin = idTipoPersonal == 1 || 
                              userTrim == "0" || 
                              userTrim == "0001" || 
                              userTrim == "123" ||
                              (codigoInt != null && (codigoInt == 0 || codigoInt == 1))

                return if (isAdmin) UserRole.ADMIN else UserRole.PERSONAL
            }

            // 2. Check in 'piloto'
            result = db.collection("piloto")
                .whereEqualTo("codigoElemento", userTrim)
                .get().await()
            
            if (!result.isEmpty) {
                val doc = result.documents[0]
                val dbPass = doc.getString("contrasena") ?: doc.getString("numeroIdentificacion") ?: ""
                if (dbPass == passTrim) return UserRole.PILOTO
            }

            // 3. Check in 'paramedico'
            result = db.collection("paramedico")
                .whereEqualTo("codigoElemento", userTrim)
                .get().await()

            if (!result.isEmpty) {
                val doc = result.documents[0]
                val dbPass = doc.getString("contrasena") ?: doc.getString("numeroIdentificacion") ?: ""
                if (dbPass == passTrim) return UserRole.PARAMEDICO
            }

            UserRole.NONE

        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error Firestore: ${e.message}")
            UserRole.NONE
        }
    }
}
