package com.example.bomberosapp.data.model

data class Paramedico(
    val id: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val numeroIdentificacion: String = "",
    val codigoElemento: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val especialidad: String = "",
    val certificacion: String = "",
    val experiencia: String = "",
    val turno: String = "",
    val contrasena: String = "",
    val fotoUrl: String = "",
    val fotoBase64: String = "",
    val tipoElemento: String = "Paramédico",
    val timestamp: Long = System.currentTimeMillis()
)