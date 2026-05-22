package com.example.bomberosapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico

@Composable
fun EditarParamedicoScreen(
    paramedico: Paramedico,
    onGuardarClick: (Paramedico) -> Unit,
    onVolverClick: () -> Unit
) {
    var especialidad by remember { mutableStateOf(paramedico.especialidad) }
    var certificacion by remember { mutableStateOf(paramedico.certificacion) }
    var numeroColegiado by remember { mutableStateOf(paramedico.numeroColegiado) }
    var aniosExperiencia by remember { mutableStateOf(paramedico.aniosExperiencia) }
    var turno by remember { mutableStateOf(paramedico.turno) }

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

    val experiencias = listOf(
        "0 a 1 año",
        "1 a 3 años",
        "3 a 5 años",
        "5 a 10 años",
        "Más de 10 años"
    )

    val turnos = listOf(
        "Matutino",
        "Vespertino",
        "Nocturno",
        "24x24"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "EDITAR ELEMENTO", onLogout = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            CampoDropdownEditarParamedico(
                label = "ESPECIALIDAD",
                valorSeleccionado = especialidad,
                opciones = especialidades,
                onSeleccionar = { especialidad = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoDropdownEditarParamedico(
                label = "CERTIFICACIÓN",
                valorSeleccionado = certificacion,
                opciones = certificaciones,
                onSeleccionar = { certificacion = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoNumeroEditarParamedico(
                label = "NÚMERO DE COLEGIADO",
                value = numeroColegiado,
                onValueChange = { numeroColegiado = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoDropdownEditarParamedico(
                label = "AÑOS DE EXPERIENCIA",
                valorSeleccionado = aniosExperiencia,
                opciones = experiencias,
                onSeleccionar = { aniosExperiencia = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoDropdownEditarParamedico(
                label = "TURNO",
                valorSeleccionado = turno,
                opciones = turnos,
                onSeleccionar = { turno = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onGuardarClick(
                        paramedico.copy(
                            especialidad = especialidad,
                            certificacion = certificacion,
                            numeroColegiado = numeroColegiado,
                            aniosExperiencia = aniosExperiencia,
                            turno = turno
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                Text(
                    text = "GUARDAR CAMBIOS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CampoDropdownEditarParamedico(
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
fun CampoNumeroEditarParamedico(
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