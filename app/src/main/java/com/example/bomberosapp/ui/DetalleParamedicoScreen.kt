package com.example.bomberosapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico

@Composable
fun DetalleParamedicoScreen(
    paramedico: Paramedico,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text("Confirmar eliminación")
            },
            text = {
                Text("¿Deseas eliminar este paramédico?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminarClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "Sí, eliminar",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.White
                    )
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "DETALLE DEL ELEMENTO", onLogout = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMACIÓN COMPLETA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE30613)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoDetalleParamedico("Nombres", paramedico.nombres)
                    CampoDetalleParamedico("Apellidos", paramedico.apellidos)
                    CampoDetalleParamedico("Número de identificación", paramedico.numeroIdentificacion)
                    CampoDetalleParamedico("Código de elemento", paramedico.codigoElemento)
                    CampoDetalleParamedico("Teléfono", paramedico.telefono)
                    CampoDetalleParamedico("Dirección", paramedico.direccion)
                    CampoDetalleParamedico("Tipo de elemento", paramedico.tipoElemento)
                    CampoDetalleParamedico("Especialidad", paramedico.especialidad)
                    CampoDetalleParamedico("Certificación", paramedico.certificacion)
                    CampoDetalleParamedico("Número de colegiado", paramedico.numeroColegiado)
                    CampoDetalleParamedico("Años de experiencia", paramedico.aniosExperiencia)
                    CampoDetalleParamedico("Turno", paramedico.turno)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEditarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = " EDITAR DATOS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { mostrarDialogoEliminar = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = " ELIMINAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CampoDetalleParamedico(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (valor.isBlank()) "Sin dato" else valor,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}