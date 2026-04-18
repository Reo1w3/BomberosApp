package com.example.bomberosapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "personal")
data class Personal(
    @PrimaryKey
    @ColumnInfo(name = "codigo_personal")
    val codigoPersonal: Int,
    val nombres: String,
    val apellidos: String,
    val telefono: String?,
    val direccion: String?,
    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimiento: String?,
    @ColumnInfo(name = "fecha_ingreso")
    val fechaIngreso: String?,
    val sexo: String?, // M o F
    @ColumnInfo(name = "rango_institucional")
    val rangoInstitucional: String?,
    @ColumnInfo(name = "id_tipo_personal")
    val idTipoPersonal: Int,
    @ColumnInfo(name = "estado_personal")
    val estadoPersonal: String?,
    @ColumnInfo(name = "numero_identificacion")
    val numeroIdentificacion: String? // Esta servirá como contraseña temporal o ID único
)
