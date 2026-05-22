package com.example.bomberosapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.R

@Composable
fun ParamedicoScreen(
    onGuardarClick: (
        especialidad: String,
        certificacion: String,
        numeroColegiado: String,
        aniosExperiencia: String,
        turno: String
    ) -> Unit,
    onVolverClick: () -> Unit
) {
    var especialidad by remember { mutableStateOf("") }
    var certificacion by remember { mutableStateOf("") }
    var numeroColegiado by remember { mutableStateOf("") }
    var aniosExperiencia by remember { mutableStateOf("") }
    var turno by remember { mutableStateOf("") }

    val rojoBomberos = Color(0xFFE30613)
    val scrollState = rememberScrollState()

    val especialidades = listOf(
        "Atención prehospitalaria",
        "Trauma",
        "Soporte vital",
        "Rescate",
        "Urgencias"
    )

    val certificaciones = listOf(
        "BLS",
        "ACLS",
        "PHTLS",
        "PALS",
        "TUM"
    )

    val turnos = listOf(
        "Matutino",
        "Vespertino",
        "Nocturno",
        "24"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(rojoBomberos)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "PARAMÉDICO",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onVolverClick) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = rojoBomberos),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = rojoBomberos,
                                    modifier = Modifier.size(26.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "PARAMÉDICO",
                                    color = rojoBomberos,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            CampoDropdownParamedico(
                                label = "ESPECIALIDAD",
                                valorSeleccionado = especialidad,
                                opciones = especialidades,
                                onSeleccionar = { especialidad = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            CampoDropdownParamedico(
                                label = "CERTIFICACIÓN",
                                valorSeleccionado = certificacion,
                                opciones = certificaciones,
                                onSeleccionar = { certificacion = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            CampoNumeroParamedico(
                                label = "NÚMERO DE COLEGIADO",
                                value = numeroColegiado,
                                onValueChange = { numeroColegiado = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            CampoNumeroParamedico(
                                label = "AÑOS DE EXPERIENCIA",
                                value = aniosExperiencia,
                                onValueChange = { aniosExperiencia = it }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            CampoDropdownParamedico(
                                label = "TURNO",
                                valorSeleccionado = turno,
                                opciones = turnos,
                                onSeleccionar = { turno = it }
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            Button(
                                onClick = {
                                    onGuardarClick(
                                        especialidad,
                                        certificacion,
                                        numeroColegiado,
                                        aniosExperiencia,
                                        turno
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = rojoBomberos),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "GUARDAR",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoDropdownParamedico(
    label: String,
    valorSeleccionado: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE0BABA), RoundedCornerShape(12.dp))
                    .clickable { expandido = true }
                    .padding(16.dp)
            ) {
                Text(
                    text = if (valorSeleccionado.isEmpty()) "SELECCIONAR" else valorSeleccionado,
                    color = if (valorSeleccionado.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 13.sp
                )
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccionar(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CampoNumeroParamedico(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0BABA),
                unfocusedBorderColor = Color(0xFFE0BABA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}