package com.example.bomberosapp.data.model

data class Piloto(
    val nombres: String = "",
    val apellidos: String = "",
    val numeroIdentificacion: String = "",
    val codigoElemento: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val tipoLicencia: String = "",
    val numeroLicencia: String = "",
    val fechaVencimiento: String = "",
    val turno: String = "",
    val tipoElemento: String = "Piloto",
    val timestamp: Long = System.currentTimeMillis()
)