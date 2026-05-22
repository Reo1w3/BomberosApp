package com.example.bomberosapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.theme.RojoBomberos
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.material.icons.filled.Person
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoClaro

@Composable
fun DetalleParamedicoScreen(
    paramedico: Paramedico,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Blanco)) {
        HeaderApp(title = "DETALLE DEL ELEMENTO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RojoBomberos)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "DETALLE DE ELEMENTO",
                            color = Blanco,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, RojoBomberos, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (paramedico.fotoBase64.isNotEmpty()) {
                                val bitmap = decodeBase64ToBitmap(paramedico.fotoBase64)
                                
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Foto de Perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                            } else {
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        CampoDetalle("NOMBRES", paramedico.nombres)
                        CampoDetalle("APELLIDOS", paramedico.apellidos)
                        if (paramedico.alias.isNotBlank()) {
                            CampoDetalle("ALIAS", paramedico.alias)
                        }
                        CampoDetalle("NÚMERO DE IDENTIFICACIÓN", paramedico.numeroIdentificacion)
                        CampoDetalle("CÓDIGO DE ELEMENTO", paramedico.codigoElemento)
                        CampoDetalle("TELÉFONO", paramedico.telefono)
                        CampoDetalle("DIRECCIÓN", paramedico.direccion)
                        CampoDetalle("TIPO DE ELEMENTO", paramedico.tipoElemento)
                        CampoDetalle("ESPECIALIDAD", paramedico.especialidad)
                        CampoDetalle("CERTIFICACIÓN", paramedico.certificacion)
                        CampoDetalle("AÑOS DE EXPERIENCIA", paramedico.experiencia)
                        CampoDetalle("TURNO", paramedico.turno)

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onEditarClick,
                                modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90)),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("EDITAR", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                            Button(
                                onClick = { mostrarDialogo = true },
                                modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = RojoClaro),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("ELIMINAR", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onVolverClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("VOLVER", color = Blanco, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    }
                }
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
