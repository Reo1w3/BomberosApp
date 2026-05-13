package com.example.bomberosapp.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.R

@Composable
fun PacienteAcompananteScreen(
    onVolverClick: () -> Unit,
    onSiguienteClick: () -> Unit
) {
    var fallecido by remember { mutableStateOf(false) }
    var cantidadFallecidos by remember { mutableStateOf("") }
    var sexoPaciente by remember { mutableStateOf("") }
    var domicilioPaciente by remember { mutableStateOf("") }
    var tieneAcompanante by remember { mutableStateOf(false) }
    var nombreAcompanante by remember { mutableStateOf("") }
    var apellidoAcompanante by remember { mutableStateOf("") }
    var telefonoAcompanante by remember { mutableStateOf("") }

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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "PACIENTE Y ACOMPAÑANTE",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(52.dp))
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
                            Text(
                                text = "Fallecidos:",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BotonOpcion(
                                    texto = "Sí",
                                    seleccionado = fallecido,
                                    onClick = { fallecido = true },
                                    colorSeleccionado = rosadoBoton
                                )

                                BotonOpcion(
                                    texto = "No",
                                    seleccionado = !fallecido,
                                    onClick = {
                                        fallecido = false
                                        cantidadFallecidos = ""
                                    },
                                    colorSeleccionado = rosadoBoton
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoNumeroFormulario(
                                label = "¿Cuántos fallecidos?",
                                value = cantidadFallecidos,
                                onValueChange = { cantidadFallecidos = it },
                                enabled = fallecido
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoDropdownSexo(
                                label = "Sexo del paciente",
                                valorSeleccionado = sexoPaciente,
                                opciones = listOf("Masculino", "Femenino"),
                                onSeleccionar = { sexoPaciente = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Domicilio del paciente",
                                value = domicilioPaciente,
                                onValueChange = { domicilioPaciente = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Acompañante:",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BotonOpcion(
                                    texto = "Sí",
                                    seleccionado = tieneAcompanante,
                                    onClick = { tieneAcompanante = true },
                                    colorSeleccionado = rosadoBoton
                                )

                                BotonOpcion(
                                    texto = "No",
                                    seleccionado = !tieneAcompanante,
                                    onClick = {
                                        tieneAcompanante = false
                                        nombreAcompanante = ""
                                        apellidoAcompanante = ""
                                        telefonoAcompanante = ""
                                    },
                                    colorSeleccionado = rosadoBoton
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Nombre del acompañante",
                                value = nombreAcompanante,
                                onValueChange = { nombreAcompanante = it },
                                enabled = tieneAcompanante
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoFormulario(
                                label = "Apellido del acompañante",
                                value = apellidoAcompanante,
                                onValueChange = { apellidoAcompanante = it },
                                enabled = tieneAcompanante
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTelefonoFormulario(
                                label = "Teléfono del acompañante",
                                value = telefonoAcompanante,
                                onValueChange = { telefonoAcompanante = it },
                                enabled = tieneAcompanante
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
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

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
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

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
fun CampoNumeroFormulario(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

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
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

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