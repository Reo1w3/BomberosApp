package com.example.bomberosapp.data.repository

import android.util.Log
import com.example.bomberosapp.data.network.FirebaseClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConfigRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getCatalogo(coleccion: String, campoEtiqueta: String): List<String> {
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

    /**
     * Obtiene el personal (pilotos o paramédicos) en formato "APELLIDO - CODIGO"
     * Busca los campos: apellidos/apellido y codigoPersonal/codigoElemento/numeroIdentificacion
     * Si tiene alias, se incluye como (ALIAS)
     */
    suspend fun getPersonalCatalogo(coleccion: String): List<String> {
        return try {
            val snapshot = db.collection(coleccion).get().await()
            val list = snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                
                // Buscamos el APELLIDO (probando variantes plural/singular)
                val apellido = (data["apellidos"] ?: data["apellido"])?.toString()?.trim() ?: ""
                
                // Buscamos el CÓDIGO (probando codigoPersonal, luego codigoElemento, etc.)
                val codigo = (data["codigoPersonal"] ?: 
                             data["codigoElemento"] ?: 
                             data["numeroIdentificacion"] ?: 
                             data["codigo"])?.toString()?.trim() ?: doc.id

                val alias = data["alias"]?.toString()?.trim() ?: ""
                val aliasSuffix = if (alias.isNotBlank()) " \"$alias\"" else ""
                
                if (apellido.isNotEmpty()) {
                    "$apellido - $codigo$aliasSuffix"
                } else {
                    // Respaldo si no hay apellido: usar el nombre
                    val nombre = (data["nombres"] ?: data["nombre"])?.toString()?.trim() ?: ""
                    if (nombre.isNotEmpty()) "$nombre - $codigo$aliasSuffix" else "$codigo$aliasSuffix"
                }
            }
            Log.d("ConfigRepository", "ÉXITO: Cargados ${list.size} elementos de $coleccion con formato Apellido-Código")
            list.sorted() 
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error cargando personal de $coleccion: ${e.message}")
            emptyList()
        }
    }
}
