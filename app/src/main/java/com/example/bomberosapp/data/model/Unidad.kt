package com.example.bomberosapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Unidad(
    val id: String = "",
    val numero: String = "",
    val tipo: String = "",
    val placa: String = "",
    val marca: String = "",
    val modelo: String = "",
    val fechaRegistro: String = "",
    val color: String = "",
    val estado: String = ""
)
