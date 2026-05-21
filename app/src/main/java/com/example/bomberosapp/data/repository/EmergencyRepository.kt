package com.example.bomberosapp.data.repository

import com.example.bomberosapp.data.model.*
import com.example.bomberosapp.data.network.FirebaseClient
import kotlinx.coroutines.tasks.await

class EmergencyRepository {
    
    private val db = FirebaseClient.db

    suspend fun saveCompleteEmergency(
        emergencia: EmergenciaPrincipal,
        paciente: Paciente,
        acompanante: Acompanante,
        direccion: DireccionEmergenciaDetalle,
        traslado: Traslado
    ): Boolean {
        return try {
            FirebaseClient.connect()
            
            // 1. Guardar Paciente y obtener ID si es necesario (o usar el proporcionado)
            val pacienteRef = db.collection("paciente").add(paciente).await()
            val pacienteId = pacienteRef.id

            // 2. Guardar Acompañante
            db.collection("acompanante").add(acompanante).await()

            // 3. Guardar Dirección Detalle
            val direccionRef = db.collection("direccionEmergencia").add(direccion).await()
            val direccionId = direccionRef.id

            // 4. Guardar Traslado
            val trasladoRef = db.collection("traslado").add(traslado).await()
            val trasladoId = trasladoRef.id

            // 5. Guardar Emergencia Principal vinculando los IDs
            val emergenciaFinal = emergencia.copy(
                direccionEmergenciaId = direccionId,
                idTraslado = trasladoId,
                nombrePaciente = pacienteId // O guardar el nombre y el ID aparte
            )
            
            db.collection("emergencia").add(emergenciaFinal).await()
            
            true
        } catch (e: Exception) {
            android.util.Log.e("EmergencyRepository", "Error saving complete emergency: ${e.message}")
            false
        }
    }

    suspend fun saveEmergency(emergency: Emergency): Boolean {
        FirebaseClient.connect()
        return try {
            db.collection("emergencia").add(emergency).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("EmergencyRepository", "Error saving emergency: ${e.message}")
            false
        }
    }
}
