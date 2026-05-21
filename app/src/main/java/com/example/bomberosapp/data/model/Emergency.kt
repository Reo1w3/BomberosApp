package com.example.bomberosapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PacienteData(
    var id: String = "",
    var nombre: String = "",
    var apellidos: String = "",
    var edad: String = "",
    var sexo: String = "",
    var dpi: String = "",
    var domicilio: String = "",
    var estado: String = "",
    // Signos Vitales
    var presionArterial: String = "",
    var frecuenciaCardiaca: String = "",
    var frecuenciaRespiratoria: String = "",
    var saturacionOxigeno: String = "",
    var temperatura: String = "",
    var glucosa: String = "",
    var esFallecido: Boolean = false,
    var numeroEmergenciaRelacionado: String = ""
)

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
    val tipoServicio: String = "",
    
    // Metadatos de pacientes
    val nombrePaciente: String = "", // Added for quick reference in lists
    val numeroPacientes: Int = 0,
    val hayFallecidos: Boolean = false,
    
    // Acompañante
    val tieneAcompanante: Boolean = false,
    val nombreAcompanante: String = "",
    val apellidoAcompanante: String = "",
    val telefonoAcompanante: String = "",
    
    // Traslado y Diagnóstico
    val diagnosticoPreliminar: String = "",
    val tieneTraslado: Boolean = false,
    val trasladoA: String = "",
    val hospitalTraslado: String = "",
    val direccionOrigenTraslado: String = "",
    val direccionDestinoTraslado: String = "",
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
