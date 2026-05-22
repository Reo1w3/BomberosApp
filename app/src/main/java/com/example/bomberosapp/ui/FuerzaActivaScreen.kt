package com.example.bomberosapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.Blanco

@Composable
fun FuerzaActivaScreen(
    pilotos: List<Piloto>,
    paramedicos: List<Paramedico>,
    isLoading: Boolean,
    onAgregarNuevoElemento: () -> Unit,
    onVerDetallePiloto: (Piloto) -> Unit,
    onVerDetalleParamedico: (Paramedico) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "FUERZA ACTIVA", onAction = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ELEMENTOS REGISTRADOS",
                        color = RojoBomberos,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = RojoBomberos)
                        }
                    } else if (pilotos.isEmpty() && paramedicos.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No hay elementos registrados",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (pilotos.isNotEmpty()) {
                                item {
                                    Text(
                                        "PILOTOS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = RojoBomberos,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                items(pilotos) { piloto ->
                                    CardElementoSimple(
                                        nombre = "${piloto.nombres} ${piloto.apellidos}",
                                        alias = piloto.alias,
                                        codigo = piloto.codigoElemento,
                                        icon = Icons.Default.DirectionsCar,
                                        fotoBase64 = piloto.fotoBase64,
                                        onClick = { onVerDetallePiloto(piloto) }
                                    )
                                }
                            }

                            if (paramedicos.isNotEmpty()) {
                                item {
                                    Text(
                                        "PARAMÉDICOS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = RojoBomberos,
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                }
                                items(paramedicos) { paramedico ->
                                    CardElementoSimple(
                                        nombre = "${paramedico.nombres} ${paramedico.apellidos}",
                                        alias = paramedico.alias,
                                        codigo = paramedico.codigoElemento,
                                        icon = Icons.Default.LocalHospital,
                                        fotoBase64 = paramedico.fotoBase64,
                                        onClick = { onVerDetalleParamedico(paramedico) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAgregarNuevoElemento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Blanco
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AGREGAR NUEVO ELEMENTO",
                    color = Blanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CardElementoSimple(
    nombre: String,
    alias: String? = null,
    codigo: String,
    icon: ImageVector,
    fotoBase64: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, RojoBomberos, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(if (fotoBase64.isEmpty()) RojoBomberos else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (fotoBase64.isNotEmpty()) {
                    val bitmap = decodeBase64ToBitmap(fotoBase64)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Blanco,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Blanco,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                if (!alias.isNullOrBlank()) {
                    Text(
                        text = "\"$alias\"",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "Cód: $codigo",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}
