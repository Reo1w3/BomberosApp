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
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap

import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.GrisCard
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoClaro
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.material.icons.filled.Person

@Composable
fun DetallePilotoScreen(
    piloto: Piloto,
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
                                if (piloto.fotoBase64.isNotEmpty()) {
                                    val bitmap = decodeBase64ToBitmap(piloto.fotoBase64)
                                    
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

                            CampoDetalle("NOMBRES", piloto.nombres)
                            CampoDetalle("APELLIDOS", piloto.apellidos)
                            CampoDetalle("NÚMERO DE IDENTIFICACIÓN", piloto.numeroIdentificacion)
                            CampoDetalle("CÓDIGO DE ELEMENTO", piloto.codigoElemento)
                            CampoDetalle("TELÉFONO", piloto.telefono)
                            CampoDetalle("DIRECCIÓN", piloto.direccion)
                            CampoDetalle("TIPO DE ELEMENTO", piloto.tipoElemento)
                            CampoDetalle("TIPO DE LICENCIA", piloto.tipoLicencia)
                            CampoDetalle("NÚMERO DE LICENCIA", piloto.numeroLicencia)
                            CampoDetalle("FECHA DE VENCIMIENTO", piloto.fechaVencimiento)
                            CampoDetalle("TURNO", piloto.turno)

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
            onDismissRequest = {
                mostrarDialogo = false
            },
            title = {
                Text("Confirmar eliminación")
            },
            text = {
                Text("¿Desea eliminar al elemento de fuerza activa?")
            },
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
                OutlinedButton(
                    onClick = {
                        mostrarDialogo = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CampoDetalle(label: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color.Gray
        )

        Text(
            text = if (valor.isBlank()) "Sin dato" else valor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 1.dp, color = Color.LightGray)
    }
}
