package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.Paramedico
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ParamedicoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun guardarParamedico(paramedico: Paramedico): Boolean {
        return try {
            db.collection("paramedico")
                .add(paramedico)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarParamedico(id: String): Boolean {
        return try {
            db.collection("paramedicos")
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}