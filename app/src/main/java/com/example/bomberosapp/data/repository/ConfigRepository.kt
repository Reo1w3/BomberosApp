package com.example.bomberosapp.data.repository

import android.util.Log
import com.example.bomberosapp.data.network.FirebaseClient
import kotlinx.coroutines.tasks.await

class ConfigRepository {
    private val db = FirebaseClient.db

    suspend fun getCatalogo(coleccion: String, campoEtiqueta: String): List<String> {
        FirebaseClient.connect()
        return try {
            val snapshot = db.collection(coleccion).get().await()
            val list = snapshot.documents.mapNotNull { it.getString(campoEtiqueta) ?: it.id }
            Log.d("ConfigRepository", "Fetched ${list.size} items from $coleccion")
            list
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error fetching $coleccion: ${e.message}")
            emptyList()
        }
    }
}
