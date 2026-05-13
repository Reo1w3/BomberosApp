package com.example.bomberosapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuevaEmergenciaScreen(
    onVolverClick: () -> Unit,
    onSiguienteClick: () -> Unit
) {
    var horaSalida by remember { mutableStateOf("") }
    var telefonoSolicitante by remember { mutableStateOf("") }
    var nombreSolicitante by remember { mutableStateOf("") }
    var tipoServicio by remember { mutableStateOf("") }
    var direccionEmergencia by remember { mutableStateOf("") }
    var nombrePaciente by remember { mutableStateOf("") }

    val rojoBomberos = Color(0xFFE30613)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(rojoBomberos)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Text(
            text = "NUEVA EMERGENCIA",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Complete la información del reporte",
                    color = rojoBomberos,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                CampoEmergencia(
                    label = "Hora de salida",
                    value = horaSalida,
                    onValueChange = { horaSalida = it }
                )

                CampoEmergencia(
                    label = "Teléfono del solicitante",
                    value = telefonoSolicitante,
                    onValueChange = { telefonoSolicitante = it }
                )

                CampoEmergencia(
                    label = "Nombre del solicitante",
                    value = nombreSolicitante,
                    onValueChange = { nombreSolicitante = it }
                )

                CampoEmergencia(
                    label = "Tipo de servicio",
                    value = tipoServicio,
                    onValueChange = { tipoServicio = it }
                )

                CampoEmergencia(
                    label = "Dirección de la emergencia",
                    value = direccionEmergencia,
                    onValueChange = { direccionEmergencia = it }
                )

                CampoEmergencia(
                    label = "Nombre del paciente",
                    value = nombrePaciente,
                    onValueChange = { nombrePaciente = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onVolverClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "VOLVER",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onSiguienteClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = rojoBomberos),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "SIGUIENTE",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }



                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun CampoEmergencia(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black,
                focusedContainerColor = Color(0xFFFFF4F4),
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}