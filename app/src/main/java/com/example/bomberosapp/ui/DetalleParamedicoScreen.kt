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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderApp(title = "DETALLE DEL ELEMENTO", onLogout = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEAF0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMACIÓN COMPLETA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE30613)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoDetalle("Nombres", paramedico.nombres)
                    CampoDetalle("Apellidos", paramedico.apellidos)
                    CampoDetalle("Número de identificación", paramedico.numeroIdentificacion)
                    CampoDetalle("Código de elemento", paramedico.codigoElemento)
                    CampoDetalle("Teléfono", paramedico.telefono)
                    CampoDetalle("Dirección", paramedico.direccion)
                    CampoDetalle("Tipo de elemento", paramedico.tipoElemento)
                    CampoDetalle("Especialidad", paramedico.especialidad)
                    CampoDetalle("Certificación", paramedico.certificacion)
                    CampoDetalle("Años de experiencia", paramedico.experiencia)
                    CampoDetalle("Turno", paramedico.turno)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEditarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                Text(" EDITAR DATOS", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                Text(" ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Desea eliminar al elemento de fuerza activa?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onEliminarClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sí, eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}