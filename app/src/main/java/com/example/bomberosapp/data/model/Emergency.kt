package com.example.bomberosapp.data.model

data class Emergency(
    val horaSalida: String = "",
    val telefonoSolicitante: String = "",
    val nombreSolicitante: String = "",
    val tipoServicio: String = "",
    val direccionEmergencia: String = "",
    val nombrePaciente: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
