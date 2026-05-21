package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.model.PacienteData
import com.example.bomberosapp.data.repository.ConfigRepository
import com.example.bomberosapp.data.repository.EmergencyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class EmergencyUIState {
    object Idle : EmergencyUIState()
    object Loading : EmergencyUIState()
    object Success : EmergencyUIState()
    data class Error(val message: String) : EmergencyUIState()
}

class EmergencyViewModel(
    private val repository: EmergencyRepository = EmergencyRepository(),
    private val configRepository: ConfigRepository = ConfigRepository()
) : ViewModel() {

    var emergencyState by mutableStateOf<EmergencyUIState>(EmergencyUIState.Idle)
        private set

    // --- SECCIÓN 1: DATOS DE SALIDA ---
    var unidad by mutableStateOf("")
    var horaSalida by mutableStateOf("")
    var telefonoSolicitante by mutableStateOf("")
    var nombreSolicitante by mutableStateOf("")
    var apellidoSolicitante by mutableStateOf("")
    var tipoServicio by mutableStateOf("")
    var direccionEmergencia by mutableStateOf("")

    // --- SECCIÓN 2: UBICACIÓN Y DIRECCIÓN ---
    var referencias by mutableStateOf("")
    var observacionesDireccion by mutableStateOf("")

    // --- SECCIÓN 3: DATOS DEL PACIENTE (DINÁMICA) ---
    var existenMasPacientes by mutableStateOf(false)
    val pacientesList = mutableStateListOf<PacienteData>(PacienteData()) 
    var hayFallecidos by mutableStateOf(false)
    
    // Campos para paciente único
    var nombrePaciente by mutableStateOf("")
    var apellidoPaciente by mutableStateOf("")
    var edadPaciente by mutableStateOf("")
    var sexoPaciente by mutableStateOf("")
    var domicilioPaciente by mutableStateOf("")
    var dpiPaciente by mutableStateOf("")
    var estadoPaciente by mutableStateOf("")
    var paPaciente by mutableStateOf("")
    var fcPaciente by mutableStateOf("")
    var frPaciente by mutableStateOf("")
    var satPaciente by mutableStateOf("")
    var tempPaciente by mutableStateOf("")
    var glucosaPaciente by mutableStateOf("")
    var esFallecidoPaciente by mutableStateOf(false)

    // --- SECCIÓN 4: ACOMPAÑANTE ---
    var tieneAcompanante by mutableStateOf(false)
    var nombreAcompanante by mutableStateOf("")
    var apellidoAcompanante by mutableStateOf("")
    var telefonoAcompanante by mutableStateOf("")

    // --- SECCIÓN 5: TRASLADO ---
    var tieneTraslado by mutableStateOf(false)
    var trasladoA by mutableStateOf("")
    var hospitalTraslado by mutableStateOf("")
    var horaLlegadaTraslado by mutableStateOf("")
    var direccionOrigenTraslado by mutableStateOf("")
    var direccionDestinoTraslado by mutableStateOf("")

    // --- SECCIÓN 6: PERSONAL DESTACADO ---
    var pilotoSeleccionado by mutableStateOf("")
    var existenMasParamedicos by mutableStateOf(false)
    val paramedicosSeleccionados = mutableStateListOf<String>("") // Al menos uno

    // --- SECCIÓN 7: CONTROL Y FIRMAS ---
    var horaLlegadaIncidente by mutableStateOf("")
    var observacionesFinales by mutableStateOf("")
    var reporteFormuladoPor by mutableStateOf("")
    var conformeJefeServicio by mutableStateOf(false)
    var jefeServicioNombre by mutableStateOf("Joel Perez")
    var conformePiloto by mutableStateOf(false)
    
    // Firmas (Base64)
    var firmaPilotoBase64 by mutableStateOf("")
    var firmaJefeServicioBase64 by mutableStateOf("")
    val firmasParamedicosBase64 = mutableStateListOf<String>()

    // --- CATÁLOGOS ---
    var unidadesList by mutableStateOf<List<String>>(emptyList())
        private set
    var pilotosCatalogList by mutableStateOf<List<String>>(emptyList())
        private set
    var paramedicosCatalogList by mutableStateOf<List<String>>(emptyList())
        private set

    val tiposServicioList = listOf(
        "ATENCION ENFERMEDAD COMUN", "PARTOS DE EMERGENCIA", "INTOXICACIONES", 
        "ENVENEMAMIENTO", "TRASLADOS PROGRAMADOS", "ACCIDENTE DE TRANSITO", 
        "RESCATE EN ESTRUCTURAS", "RESCATE EN ESPACIOS CONFINADOS", 
        "ACCIDENTE LABORAL", "ACCIDENTE DOMESTICO", "INCENDIO FORESTAL", 
        "INCENDIO ESTRUCTURAL", "INCENDIOS VEHICULARES", "RESCATE ACUATICO", 
        "RESCATE DE MONTAÑA", "RESCATE EN VERTICAL", "CONTROL DE MATERIALES PELIGROSOS", 
        "CAPTURA Y REUBICACION DE FAUNA PELIGROSA", "ABASTECIMIENTO DE AGUA DE EMERGENCIA"
    )

    // Listas para Dropdowns de Paciente
    val estadosPacienteList = listOf("CONCIENTE Y ORIENTADO", "CONCIENTE DESORIENTEADO", "SOMNOLIENTO", "INCONCIENTE CON RESPUESTA AL DOLOR", "INCONCIENTE TOTAL", "ESTADO EN SHOCK", "ESTDO DE DELIRIO", "EN PARO RESPIRATORIO", "EN PARO CARDIORESPIRATORIO", "FALLECIDO")
    val paList = listOf("NORMOTENSION (90/60 mmHg - 120/80)", "HIPOTENSION (<90/60)", "HIPERTENSION (>130/180)")
    val fcList = listOf("NORMOCARDIA (60-100)", "BRADICARDIA (<60)", "HIPERTENSION (>100)")
    val frList = listOf("EUPNEA (12-10 rpm)", "BRADIPNEA (<12 rpm)", "TAQUIPNEA (>20 rpm)")
    val satList = listOf("NORMOXIA (95%-100%)", "HIPOXIA LEVE (91%-94%)", "HIPOXIA SEVERA (<90%)")
    val tempList = listOf("NORMOTERMIA (36.5°C - 37.5°C)", "HIPOTERMIA (<35°C)", "FIEBRE/HIPERTERMIA (>38°C)")
    val glucosaList = listOf("NORMAL (70-100 mg/dL)", "HIPOGLUCEMIA (<70 mg/dL)", "HIPERGLUCEMIA (>140 mg/dL)")

    val hospitalesList = listOf(
        "HOSPITAL REGIONAL DE ZACAPA", 
        "EMERGENCIAS IGSS", 
        "CLININA IGSS", 
        "CENTRO DE SALUD ZACAPA", 
        "CENTRO DE SALUD TECULUTAN", 
        "CENTRO DE SALUD RIO HONDO"
    )

    fun addPaciente() {
        pacientesList.add(PacienteData())
    }

    fun removePaciente(index: Int) {
        if (pacientesList.size > 1) {
            pacientesList.removeAt(index)
        }
    }

    fun updatePaciente(index: Int, updated: PacienteData) {
        if (index in pacientesList.indices) {
            pacientesList[index] = updated
        }
    }

    // Funciones para Paramédicos
    fun addParamedico() {
        paramedicosSeleccionados.add("")
        firmasParamedicosBase64.add("")
    }

    fun removeParamedico(index: Int) {
        if (paramedicosSeleccionados.size > 1) {
            paramedicosSeleccionados.removeAt(index)
            if (index in firmasParamedicosBase64.indices) firmasParamedicosBase64.removeAt(index)
        }
    }

    fun updateParamedico(index: Int, nombre: String) {
        if (index in paramedicosSeleccionados.indices) {
            paramedicosSeleccionados[index] = nombre
            // Sincronizar tamaño de lista de firmas
            while (firmasParamedicosBase64.size < paramedicosSeleccionados.size) {
                firmasParamedicosBase64.add("")
            }
        }
    }

    fun updateFirmaParamedico(index: Int, base64: String) {
        if (index in firmasParamedicosBase64.indices) {
            firmasParamedicosBase64[index] = base64
        }
    }

    val isGeneralInfoComplete get() = unidad.isNotBlank() && 
            horaSalida.isNotBlank() && 
            nombreSolicitante.isNotBlank() && 
            apellidoSolicitante.isNotBlank() && 
            tipoServicio.isNotBlank() &&
            direccionEmergencia.isNotBlank()

    fun loadConfig() {
        viewModelScope.launch {
            unidadesList = configRepository.getCatalogo("unidad", "numero")
            pilotosCatalogList = configRepository.getPersonalCatalogo("piloto") 
            paramedicosCatalogList = configRepository.getPersonalCatalogo("paramedico")
            
            // Inicializar firma si no hay ninguna
            if (firmasParamedicosBase64.isEmpty()) firmasParamedicosBase64.add("")
        }
    }

    fun saveFullEmergency(onSuccess: () -> Unit) {
        if (!isGeneralInfoComplete) {
            emergencyState = EmergencyUIState.Error("Complete los campos obligatorios (Unidad, Hora, Solicitante, Servicio y Dirección)")
            return
        }

        emergencyState = EmergencyUIState.Loading
        
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val batch = db.batch()

                val emergencyRef = db.collection("emergencia").document()
                val emergencyId = emergencyRef.id
                
                val mainNombrePaciente = if (!existenMasPacientes) {
                    nombrePaciente
                } else if (pacientesList.isNotEmpty()) {
                    "${pacientesList[0].nombre} (+${pacientesList.size - 1})"
                } else {
                    "Varios"
                }

                val personalTexto = paramedicosSeleccionados.filter { it.isNotBlank() }.joinToString(", ")
                val firmasParamTexto = firmasParamedicosBase64.joinToString("|")

                val emergencyData = Emergency(
                    id = emergencyId,
                    unidad = unidad,
                    piloto = pilotoSeleccionado,
                    personalDestacado = personalTexto,
                    horaSalida = horaSalida,
                    telefonoSolicitante = telefonoSolicitante,
                    nombreSolicitante = nombreSolicitante,
                    apellidoSolicitante = apellidoSolicitante,
                    tipoServicio = tipoServicio,
                    direccionEmergencia = direccionEmergencia,
                    nombrePaciente = mainNombrePaciente,
                    observaciones = observacionesFinales,
                    hayFallecidos = hayFallecidos,
                    tieneAcompanante = tieneAcompanante,
                    nombreAcompanante = nombreAcompanante,
                    apellidoAcompanante = apellidoAcompanante,
                    telefonoAcompanante = telefonoAcompanante,
                    tieneTraslado = tieneTraslado,
                    trasladoA = trasladoA,
                    hospitalTraslado = hospitalTraslado,
                    direccionOrigenTraslado = direccionOrigenTraslado,
                    direccionDestinoTraslado = direccionDestinoTraslado,
                    horaLlegadaTraslado = horaLlegadaTraslado,
                    horaLlegada = horaLlegadaIncidente,
                    reporteFormuladoPor = reporteFormuladoPor,
                    voBoJefeServicio = jefeServicioNombre,
                    esConformePiloto = conformePiloto,
                    firmaPiloto = firmaPilotoBase64,
                    firmaJefeServicio = firmaJefeServicioBase64,
                    firmaPersonalDestacado = firmasParamTexto,
                    timestamp = System.currentTimeMillis()
                )
                
                batch.set(emergencyRef, emergencyData)

                if (!existenMasPacientes) {
                    val pacienteRef = db.collection("paciente").document()
                    val pData = PacienteData(
                        id = pacienteRef.id,
                        nombre = nombrePaciente,
                        apellidos = apellidoPaciente,
                        edad = edadPaciente,
                        sexo = sexoPaciente,
                        dpi = dpiPaciente,
                        domicilio = domicilioPaciente,
                        estado = estadoPaciente,
                        presionArterial = paPaciente,
                        frecuenciaCardiaca = fcPaciente,
                        frecuenciaRespiratoria = frPaciente,
                        saturacionOxigeno = satPaciente,
                        temperatura = tempPaciente,
                        glucosa = glucosaPaciente,
                        esFallecido = esFallecidoPaciente,
                        numeroEmergenciaRelacionado = emergencyId
                    )
                    batch.set(pacienteRef, pData)
                } else {
                    pacientesList.forEach { p ->
                        val pacienteRef = db.collection("paciente").document()
                        p.id = pacienteRef.id
                        p.numeroEmergenciaRelacionado = emergencyId
                        batch.set(pacienteRef, p)
                    }
                }

                batch.commit().await()
                emergencyState = EmergencyUIState.Success
                resetFields()
                onSuccess()
            } catch (e: Exception) {
                emergencyState = EmergencyUIState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun resetFields() {
        unidad = ""; horaSalida = ""; telefonoSolicitante = ""; nombreSolicitante = ""; apellidoSolicitante = ""
        tipoServicio = ""; direccionEmergencia = ""; referencias = ""; observacionesDireccion = ""
        nombrePaciente = ""; apellidoPaciente = ""; edadPaciente = ""; sexoPaciente = ""; domicilioPaciente = ""
        dpiPaciente = ""; estadoPaciente = ""; paPaciente = ""; fcPaciente = ""; frPaciente = ""; satPaciente = ""
        tempPaciente = ""; glucosaPaciente = ""; esFallecidoPaciente = false; existenMasPacientes = false
        pacientesList.clear(); pacientesList.add(PacienteData()); hayFallecidos = false
        tieneAcompanante = false; nombreAcompanante = ""; apellidoAcompanante = ""; telefonoAcompanante = ""
        tieneTraslado = false; trasladoA = ""; hospitalTraslado = ""; direccionOrigenTraslado = ""
        direccionDestinoTraslado = ""; horaLlegadaTraslado = ""; pilotoSeleccionado = ""
        existenMasParamedicos = false; paramedicosSeleccionados.clear(); paramedicosSeleccionados.add("")
        horaLlegadaIncidente = ""; observacionesFinales = ""; reporteFormuladoPor = ""; conformeJefeServicio = false
        conformePiloto = false; firmaPilotoBase64 = ""; firmaJefeServicioBase64 = ""; firmasParamedicosBase64.clear()
        firmasParamedicosBase64.add("")
    }

    fun resetState() { emergencyState = EmergencyUIState.Idle }
}
