package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.*
import com.example.bomberosapp.data.repository.ConfigRepository
import com.example.bomberosapp.data.repository.EmergencyRepository
import kotlinx.coroutines.launch

class EmergencyViewModel(
    private val repository: EmergencyRepository = EmergencyRepository(),
    private val configRepository: ConfigRepository = ConfigRepository()
) : ViewModel() {

    var emergencyState by mutableStateOf<EmergencyUIState>(EmergencyUIState.Idle)
        private set

    // --- CAMPOS DEL FORMULARIO (Según Diagramas) ---

    // Emergencia (General)
    var numeroEmergencia by mutableStateOf("")
    var fechaHoraLlamada by mutableStateOf(System.currentTimeMillis())
    var telefonoSolicitante by mutableStateOf("")
    var nombresSolicitante by mutableStateOf("")
    var apellidosSolicitante by mutableStateOf("")
    var tipoServicio by mutableStateOf("")
    var numeroUnidad by mutableStateOf("")
    var codigoPersonal by mutableStateOf("")
    var campoExtra by mutableStateOf("")

    // Paciente
    var nombrePaciente by mutableStateOf("")
    var domicilioPaciente by mutableStateOf("")
    var edadPaciente by mutableStateOf("")
    var sexoPaciente by mutableStateOf("")
    var estadoPaciente by mutableStateOf("")

    // Acompañante
    var nombreAcompanante by mutableStateOf("")
    var apellidoAcompanante by mutableStateOf("")
    var telefonoAcompanante by mutableStateOf("")

    // Dirección Emergencia
    var ubicacionMapa by mutableStateOf("")
    var referenciasDireccion by mutableStateOf("")
    var observacionesDireccion by mutableStateOf("")

    // Traslado
    var direccionOrigenTraslado by mutableStateOf("")
    var direccionDestinoTraslado by mutableStateOf("")
    var horaLlegadaTraslado by mutableStateOf("")

    // --- VALIDACIONES DE SECCIÓN ---
    val isGeneralInfoComplete get() = numeroEmergencia.isNotBlank() && nombresSolicitante.isNotBlank() && tipoServicio.isNotBlank() && numeroUnidad.isNotBlank()
    val isLocationComplete get() = ubicacionMapa.isNotBlank() || referenciasDireccion.isNotBlank()
    val isPatientComplete get() = nombrePaciente.isNotBlank() && edadPaciente.isNotBlank() && sexoPaciente.isNotBlank()
    val isAcompananteComplete get() = nombreAcompanante.isNotBlank() || apellidoAcompanante.isNotBlank()
    val isTrasladoComplete get() = direccionOrigenTraslado.isNotBlank() && direccionDestinoTraslado.isNotBlank()

    // --- CATÁLOGOS ---
    var unidades by mutableStateOf<List<String>>(emptyList())
        private set
    var tiposServicioList by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadConfig() {
        viewModelScope.launch {
            unidades = configRepository.getCatalogo("unidad", "numeroUnidad")
            tiposServicioList = configRepository.getCatalogo("tipo_servicio", "nombre")
        }
    }

    fun saveFullEmergency(onSuccess: () -> Unit) {
        // Validación de datos vacíos (Requerimiento del usuario)
        if (numeroEmergencia.isBlank() || nombresSolicitante.isBlank() || tipoServicio.isBlank() || numeroUnidad.isBlank()) {
            emergencyState = EmergencyUIState.Error("Complete los campos básicos de la emergencia")
            return
        }

        emergencyState = EmergencyUIState.Loading

        val paciente = Paciente(
            nombrePaciente = nombrePaciente,
            domicilio = domicilioPaciente,
            edadPaciente = edadPaciente,
            sexo = sexoPaciente,
            estadoPaciente = estadoPaciente
        )

        val acompanante = Acompanante(
            nombreAcompanante = nombreAcompanante,
            apellidoAcompanante = apellidoAcompanante,
            telefonoAcompanante = telefonoAcompanante
        )

        val direccion = DireccionEmergenciaDetalle(
            ubicacionMapa = ubicacionMapa,
            referencias = referenciasDireccion,
            observaciones = observacionesDireccion
        )

        val traslado = Traslado(
            direccionOrigen = direccionOrigenTraslado,
            direccionDestino = direccionDestinoTraslado,
            horaLlegada = horaLlegadaTraslado
        )

        val emergencia = EmergenciaPrincipal(
            numeroEmergencia = numeroEmergencia,
            fechaHoraLlamada = fechaHoraLlamada,
            telefonoSolicitante = telefonoSolicitante,
            nombresSolicitante = nombresSolicitante,
            apellidosSolicitante = apellidosSolicitante,
            tipoServicio = tipoServicio,
            numeroUnidad = numeroUnidad,
            nombrePaciente = nombrePaciente,
            nombreAcompanante = nombreAcompanante,
            codigoPersonal = codigoPersonal,
            campo = campoExtra
        )

        viewModelScope.launch {
            val success = repository.saveCompleteEmergency(
                emergencia, paciente, acompanante, direccion, traslado
            )
            if (success) {
                emergencyState = EmergencyUIState.Success
                resetFields()
                onSuccess()
            } else {
                emergencyState = EmergencyUIState.Error("Error al guardar en la base de datos")
            }
        }
    }

    private fun resetFields() {
        numeroEmergencia = ""
        telefonoSolicitante = ""
        nombresSolicitante = ""
        apellidosSolicitante = ""
        tipoServicio = ""
        numeroUnidad = ""
        codigoPersonal = ""
        campoExtra = ""
        nombrePaciente = ""
        domicilioPaciente = ""
        edadPaciente = ""
        sexoPaciente = ""
        estadoPaciente = ""
        nombreAcompanante = ""
        apellidoAcompanante = ""
        telefonoAcompanante = ""
        ubicacionMapa = ""
        referenciasDireccion = ""
        observacionesDireccion = ""
        direccionOrigenTraslado = ""
        direccionDestinoTraslado = ""
        horaLlegadaTraslado = ""
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
