package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.repository.EmergencyRepository
import kotlinx.coroutines.launch

class EmergencyViewModel(private val repository: EmergencyRepository = EmergencyRepository()) : ViewModel() {

    var emergencyState by mutableStateOf<EmergencyUIState>(EmergencyUIState.Idle)
        private set

    fun saveEmergency(
        horaSalida: String,
        telefonoSolicitante: String,
        nombreSolicitante: String,
        tipoServicio: String,
        direccionEmergencia: String,
        nombrePaciente: String,
        onSuccess: () -> Unit
    ) {
        if (horaSalida.isBlank() || nombreSolicitante.isBlank() || tipoServicio.isBlank() || direccionEmergencia.isBlank()) {
            emergencyState = EmergencyUIState.Error("Por favor, complete los campos obligatorios")
            return
        }

        val emergency = Emergency(
            horaSalida = horaSalida,
            telefonoSolicitante = telefonoSolicitante,
            nombreSolicitante = nombreSolicitante,
            tipoServicio = tipoServicio,
            direccionEmergencia = direccionEmergencia,
            nombrePaciente = nombrePaciente
        )

        emergencyState = EmergencyUIState.Loading
        viewModelScope.launch {
            val success = repository.saveEmergency(emergency)
            if (success) {
                emergencyState = EmergencyUIState.Success
                onSuccess()
            } else {
                emergencyState = EmergencyUIState.Error("Error al guardar en Firestore")
            }
        }
    }

    fun resetState() {
        emergencyState = EmergencyUIState.Idle
    }
}

sealed class EmergencyUIState {
    object Idle : EmergencyUIState()
    object Loading : EmergencyUIState()
    object Success : EmergencyUIState()
    data class Error(val message: String) : EmergencyUIState()
}
