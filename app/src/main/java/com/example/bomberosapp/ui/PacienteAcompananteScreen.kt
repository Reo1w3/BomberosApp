package com.example.bomberosapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun PacienteAcompananteScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onSiguienteClick: () -> Unit
) {
    val f = viewModel.formData
    val rojoBomberos = Color(0xFFE30613)
    val rosadoBoton = Color(0xFFFFD9DC)
    val scrollState = rememberScrollState()

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
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Reporte de ambulancia",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Fallecidos:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                BotonOpcion(
                                    texto = "Sí",
                                    seleccionado = f["fall"] == "true",
                                    onClick = { f["fall"] = "true" },
                                    colorSeleccionado = rosadoBoton
                                )
                                BotonOpcion(
                                    texto = "No",
                                    seleccionado = f["fall"] != "true",
                                    onClick = { f["fall"] = "false" },
                                    colorSeleccionado = rosadoBoton
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoDropdownSexo(
                                label = "Sexo del paciente",
                                valorSeleccionado = f["sexP"] ?: "",
                                opciones = listOf("Masculino", "Femenino"),
                                onSeleccionar = { f["sexP"] = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Domicilio del paciente",
                                value = f["domP"] ?: "",
                                onValueChange = { f["domP"] = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Acompañante:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                BotonOpcion(
                                    texto = "Sí",
                                    seleccionado = f["tieAco"] == "true",
                                    onClick = { f["tieAco"] = "true" },
                                    colorSeleccionado = rosadoBoton
                                )
                                BotonOpcion(
                                    texto = "No",
                                    seleccionado = f["tieAco"] != "true",
                                    onClick = {
                                        f["tieAco"] = "false"
                                        f["nomA"] = ""
                                        f["apeA"] = ""
                                        f["telA"] = ""
                                    },
                                    colorSeleccionado = rosadoBoton
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Nombre del acompañante",
                                value = f["nomA"] ?: "",
                                onValueChange = { f["nomA"] = it },
                                enabled = f["tieAco"] == "true"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Apellido del acompañante",
                                value = f["apeA"] ?: "",
                                onValueChange = { f["apeA"] = it },
                                enabled = f["tieAco"] == "true"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTelefonoFormulario(
                                label = "Teléfono del acompañante",
                                value = f["telA"] ?: "",
                                onValueChange = { f["telA"] = it },
                                enabled = f["tieAco"] == "true"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "VOLVER", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSiguienteClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = rojoBomberos),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "SIGUIENTE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BotonOpcion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    colorSeleccionado: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) colorSeleccionado else Color(0xFFF4F4F4)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = texto,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CampoTextoFormulario(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF3F3F3)
            )
        )
    }
}

@Composable
fun CampoTelefonoFormulario(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF3F3F3)
            )
        )
    }
}

@Composable
fun CampoDropdownSexo(
    label: String,
    valorSeleccionado: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(14.dp))
                    .clickable { expandido = true }
                    .padding(16.dp)
            ) {
                Text(
                    text = if (valorSeleccionado.isEmpty()) "Seleccionar" else valorSeleccionado,
                    color = if (valorSeleccionado.isEmpty()) Color.Gray else Color.Black
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
