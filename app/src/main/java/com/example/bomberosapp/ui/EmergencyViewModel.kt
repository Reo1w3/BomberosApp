package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.repository.ConfigRepository
import com.example.bomberosapp.data.repository.EmergencyRepository
import kotlinx.coroutines.launch

class EmergencyViewModel(
    private val repository: EmergencyRepository = EmergencyRepository(),
    private val configRepository: ConfigRepository = ConfigRepository()
) : ViewModel() {

    var emergencyState by mutableStateOf<EmergencyUIState>(EmergencyUIState.Idle)
        private set

    // Form data
    var formData = mutableStateMapOf<String, String>()
        private set

    var unidades by mutableStateOf<List<String>>(emptyList())
        private set

    var tiposServicio by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadConfig() {
        viewModelScope.launch {
            unidades = configRepository.getCatalogo("unidad", "placa")
            tiposServicio = configRepository.getCatalogo("tipo_servicio", "nombre")
        }
    }

    fun saveEmergency(
        unidad: String, piloto: String, personal: String, kmS: String, kmE: String,
        hA: String, hS: String, hL: String, hR: String, hLT: String,
        nomS: String, apeS: String, telS: String, solTel: Boolean, dirE: String, tipS: String,
        nomP: String, nomCP: String, edaP: String, genP: String, sexP: String, dpiP: String, dirP: String, domP: String,
        tieAco: Boolean, nomA: String, apeA: String, telA: String,
        pa: String, fc: String, fr: String, sat: String, tem: String, glu: String,
        diag: String, tieTra: Boolean, trasA: String, hosp: String, fall: Boolean, obs: String,
        perD: String, repF: String, vobo: String, confP: Boolean,
        firmaBase64: String, firmaPiloto: String, firmaJefe: String, firmaPers: String,
        onSuccess: () -> Unit
    ) {
        if (unidad.isBlank() || hS.isBlank() || nomS.isBlank() || tipS.isBlank() || dirE.isBlank()) {
            emergencyState = EmergencyUIState.Error("Por favor, complete los campos obligatorios")
            return
        }

        val emergency = Emergency(
            unidad = unidad, piloto = piloto, personal = personal, kilometrajeSalida = kmS, kilometrajeEntrada = kmE,
            horaAviso = hA, horaSalida = hS, horaLlegada = hL, horaRegreso = hR, horaLlegadaTraslado = hLT,
            nombreSolicitante = nomS, apellidoSolicitante = apeS, telefonoSolicitante = telS, solicitudPorTelefono = solTel, direccionEmergencia = dirE, tipoServicio = tipS,
            nombrePaciente = nomP, nombreCompletoPacientes = nomCP, edadPaciente = edaP, generoPaciente = genP, sexoPaciente = sexP,
            dpiPaciente = dpiP, direccionPaciente = dirP, domicilioPaciente = domP,
            tieneAcompanante = tieAco, nombreAcompanante = nomA, apellidoAcompanante = apeA, telefonoAcompanante = telA,
            presionArterial = pa, frecuenciaCardiaca = fc, frecuenciaRespiratoria = fr, saturacionOxigeno = sat, temperatura = tem, glucosa = glu,
            diagnosticoPreliminar = diag, tieneTraslado = tieTra, trasladoA = trasA, hospitalTraslado = hosp, fallecidos = fall, observaciones = obs,
            personalDestacado = perD, reporteFormuladoPor = repF, voBoJefeServicio = vobo, esConformePiloto = confP,
            firmaBase64 = firmaBase64, firmaPiloto = firmaPiloto, firmaJefeServicio = firmaJefe, firmaPersonalDestacado = firmaPers
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
        formData.clear()
    }
}

sealed class EmergencyUIState {
    object Idle : EmergencyUIState()
    object Loading : EmergencyUIState()
    object Success : EmergencyUIState()
    data class Error(val message: String) : EmergencyUIState()
}
