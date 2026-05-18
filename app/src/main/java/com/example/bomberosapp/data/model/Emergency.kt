package com.example.bomberosapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Emergency(
    val id: String = "",
    // Información del Servicio
    val unidad: String = "",
    val piloto: String = "",
    val personal: String = "",
    val kilometrajeSalida: String = "",
    val kilometrajeEntrada: String = "",
    
    // Control de Tiempos
    val horaAviso: String = "",
    val horaSalida: String = "",
    val horaLlegada: String = "",
    val horaRegreso: String = "",
    val horaLlegadaTraslado: String = "",
    
    // Datos del Solicitante
    val nombreSolicitante: String = "",
    val apellidoSolicitante: String = "",
    val telefonoSolicitante: String = "",
    val solicitudPorTelefono: Boolean = false,
    val direccionEmergencia: String = "",
    val direccion: String = "",
    val tipoServicio: String = "",
    
    // Datos del Paciente
    val nombrePaciente: String = "",
    val nombreCompletoPacientes: String = "",
    val edadPaciente: String = "",
    val generoPaciente: String = "",
    val sexoPaciente: String = "",
    val dpiPaciente: String = "",
    val direccionPaciente: String = "",
    val domicilioPaciente: String = "",
    
    // Acompañante
    val tieneAcompanante: Boolean = false,
    val nombreAcompanante: String = "",
    val apellidoAcompanante: String = "",
    val telefonoAcompanante: String = "",
    
    // Evaluación Médica (Signos Vitales)
    val presionArterial: String = "",
    val frecuenciaCardiaca: String = "",
    val frecuenciaRespiratoria: String = "",
    val saturacionOxigeno: String = "",
    val temperatura: String = "",
    val glucosa: String = "",
    
    // Traslado y Diagnóstico
    val diagnosticoPreliminar: String = "",
    val tieneTraslado: Boolean = false,
    val trasladoA: String = "",
    val hospitalTraslado: String = "",
    val fallecidos: Boolean = false,
    val observaciones: String = "",
    
    // Personal y Firmas
    val personalDestacado: String = "",
    val reporteFormuladoPor: String = "",
    val voBoJefeServicio: String = "",
    val esConformePiloto: Boolean = false,
    val firmaBase64: String = "",
    val firmaPiloto: String = "",
    val firmaJefeServicio: String = "",
    val firmaPersonalDestacado: String = "",
    
    // Validación y Metadatos
    val timestamp: Long = System.currentTimeMillis()
)
