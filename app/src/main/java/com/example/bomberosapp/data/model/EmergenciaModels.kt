package com.example.bomberosapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Traslado(
    val idTraslado: String = "",
    val direccionOrigen: String = "",
    val direccionDestino: String = "",
    val horaLlegada: String = ""
)

@IgnoreExtraProperties
data class Paciente(
    val idPaciente: String = "",
    val nombrePaciente: String = "",
    val domicilio: String = "",
    val edadPaciente: String = "",
    val sexo: String = "",
    val estadoPaciente: String = ""
)

@IgnoreExtraProperties
data class Acompanante(
    val nombreAcompanante: String = "",
    val apellidoAcompanante: String = "",
    val telefonoAcompanante: String = ""
)

@IgnoreExtraProperties
data class DireccionEmergenciaDetalle(
    val idDireccion: String = "",
    val ubicacionMapa: String = "", // Lat,Long o URL
    val referencias: String = "",
    val observaciones: String = ""
)

@IgnoreExtraProperties
data class EmergenciaPrincipal(
    val id: String = "",
    val numeroEmergencia: String = "",
    val fechaHoraLlamada: Long = System.currentTimeMillis(),
    val telefonoSolicitante: String = "",
    val nombresSolicitante: String = "",
    val apellidosSolicitante: String = "",
    val tipoServicio: String = "",
    val direccionEmergenciaId: String = "",
    val numeroUnidad: String = "",
    val nombrePaciente: String = "", // Referencia o nombre denormalizado
    val nombreAcompanante: String = "",
    val idTraslado: String = "",
    val codigoPersonal: String = "",
    val campo: String = "" // Campo extra del diagrama
)
