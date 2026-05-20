package com.example.bomberosapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.example.bomberosapp.HeaderApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamedicoScreen(
    onGuardarClick: (
        especialidad: String,
        certificacion: String,
        experiencia: String,
        turno: String
    ) -> Unit,
    onVolverClick: () -> Unit
) {
    var especialidad by remember { mutableStateOf("") }
    var certificacion by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var turno by remember { mutableStateOf("") }

    var especialidadExpandida by remember { mutableStateOf(false) }
    var turnoExpandido by remember { mutableStateOf(false) }

    val opcionesEspecialidad = listOf(
        "Primeros auxilios",
        "Emergencias médicas",
        "Trauma",
        "Rescate",
        "Enfermería",
        "Soporte vital básico"
    )

    val opcionesTurno = listOf(
        "Matutino",
        "Vespertino",
        "Nocturno",
        "24 horas"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderApp(title = "DATOS DEL PARAMÉDICO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Información del paramédico",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE30613)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = especialidadExpandida,
                onExpandedChange = { especialidadExpandida = !especialidadExpandida }
            ) {
                OutlinedTextField(
                    value = especialidad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidad") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = especialidadExpandida,
                    onDismissRequest = { especialidadExpandida = false }
                ) {
                    opcionesEspecialidad.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                especialidad = opcion
                                especialidadExpandida = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = certificacion,
                onValueChange = { certificacion = it },
                label = { Text("Certificación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = experiencia,
                onValueChange = { experiencia = it },
                label = { Text("Años de experiencia") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = turnoExpandido,
                onExpandedChange = { turnoExpandido = !turnoExpandido }
            ) {
                OutlinedTextField(
                    value = turno,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Turno") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = turnoExpandido,
                    onDismissRequest = { turnoExpandido = false }
                ) {
                    opcionesTurno.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                turno = opcion
                                turnoExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onGuardarClick(
                        especialidad,
                        certificacion,
                        experiencia,
                        turno
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                Text(
                    text = "GUARDAR PARAMÉDICO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}