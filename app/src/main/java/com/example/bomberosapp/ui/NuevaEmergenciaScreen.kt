package com.example.bomberosapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.ui.components.CampoTextoEmergencia
import com.example.bomberosapp.ui.components.DropdownFieldSimple
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos

@Composable
fun NuevaEmergenciaScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onFinalizarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.emergencyState

    LaunchedEffect(Unit) {
        viewModel.loadConfig()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "FORMULARIO DE EMERGENCIA", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SECCIÓN 1: INFORMACIÓN GENERAL
            ExpandableSection(
                title = "INFORMACIÓN GENERAL",
                isCompleted = viewModel.isGeneralInfoComplete
            ) {
                CampoTextoEmergencia(label = "Número de Emergencia", value = viewModel.numeroEmergencia, onValueChange = { viewModel.numeroEmergencia = it })
                CampoTextoEmergencia(label = "Teléfono Solicitante", value = viewModel.telefonoSolicitante, onValueChange = { viewModel.telefonoSolicitante = it })
                CampoTextoEmergencia(label = "Nombres Solicitante", value = viewModel.nombresSolicitante, onValueChange = { viewModel.nombresSolicitante = it })
                CampoTextoEmergencia(label = "Apellidos Solicitante", value = viewModel.apellidosSolicitante, onValueChange = { viewModel.apellidosSolicitante = it })
                
                Text("Tipo de Servicio", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(viewModel.tiposServicioList, viewModel.tipoServicio) { viewModel.tipoServicio = it }

                Text("Unidad", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(viewModel.unidades, viewModel.numeroUnidad) { viewModel.numeroUnidad = it }

                CampoTextoEmergencia(label = "Código Personal (Encargado)", value = viewModel.codigoPersonal, onValueChange = { viewModel.codigoPersonal = it })
                CampoTextoEmergencia(label = "Campo Extra", value = viewModel.campoExtra, onValueChange = { viewModel.campoExtra = it })
            }

            // SECCIÓN 2: UBICACIÓN
            ExpandableSection(
                title = "UBICACIÓN Y DIRECCIÓN",
                isCompleted = viewModel.isLocationComplete
            ) {
                CampoTextoEmergencia(label = "Ubicación Mapa (Lat, Log)", value = viewModel.ubicacionMapa, onValueChange = { viewModel.ubicacionMapa = it })
                CampoTextoEmergencia(label = "Referencias", value = viewModel.referenciasDireccion, onValueChange = { viewModel.referenciasDireccion = it })
                CampoTextoEmergencia(label = "Observaciones de Dirección", value = viewModel.observacionesDireccion, onValueChange = { viewModel.observacionesDireccion = it })
            }

            // SECCIÓN 3: PACIENTE
            ExpandableSection(
                title = "DATOS DEL PACIENTE",
                isCompleted = viewModel.isPatientComplete
            ) {
                CampoTextoEmergencia(label = "Nombre Completo", value = viewModel.nombrePaciente, onValueChange = { viewModel.nombrePaciente = it })
                CampoTextoEmergencia(label = "Domicilio", value = viewModel.domicilioPaciente, onValueChange = { viewModel.domicilioPaciente = it })
                CampoTextoEmergencia(label = "Edad", value = viewModel.edadPaciente, onValueChange = { viewModel.edadPaciente = it })
                
                Text("Sexo", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(listOf("Masculino", "Femenino"), viewModel.sexoPaciente) { viewModel.sexoPaciente = it }
                
                CampoTextoEmergencia(label = "Estado del Paciente", value = viewModel.estadoPaciente, onValueChange = { viewModel.estadoPaciente = it })
            }

            // SECCIÓN 4: ACOMPAÑANTE
            ExpandableSection(
                title = "DATOS DEL ACOMPAÑANTE",
                isCompleted = viewModel.isAcompananteComplete
            ) {
                CampoTextoEmergencia(label = "Nombre(s)", value = viewModel.nombreAcompanante, onValueChange = { viewModel.nombreAcompanante = it })
                CampoTextoEmergencia(label = "Apellido(s)", value = viewModel.apellidoAcompanante, onValueChange = { viewModel.apellidoAcompanante = it })
                CampoTextoEmergencia(label = "Teléfono", value = viewModel.telefonoAcompanante, onValueChange = { viewModel.telefonoAcompanante = it })
            }

            // SECCIÓN 5: TRASLADO
            ExpandableSection(
                title = "INFORMACIÓN DE TRASLADO",
                isCompleted = viewModel.isTrasladoComplete
            ) {
                CampoTextoEmergencia(label = "Dirección Origen", value = viewModel.direccionOrigenTraslado, onValueChange = { viewModel.direccionOrigenTraslado = it })
                CampoTextoEmergencia(label = "Dirección Destino", value = viewModel.direccionDestinoTraslado, onValueChange = { viewModel.direccionDestinoTraslado = it })
                CampoTextoEmergencia(label = "Hora de Llegada", value = viewModel.horaLlegadaTraslado, onValueChange = { viewModel.horaLlegadaTraslado = it })
            }

            if (uiState is EmergencyUIState.Error) {
                Text(text = uiState.message, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    viewModel.saveFullEmergency {
                        onFinalizarClick()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(25.dp),
                enabled = uiState !is EmergencyUIState.Loading
            ) {
                if (uiState is EmergencyUIState.Loading) {
                    CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                } else {
                    Text("FINALIZAR Y GUARDAR", color = Blanco, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    isCompleted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = if (isCompleted) Color(0xFF4CAF50) else RojoBomberos
    val titleColor = if (isCompleted) Color(0xFF2E7D32) else RojoBomberos

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Black,
                        color = titleColor,
                        fontSize = 16.sp
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completado",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = titleColor
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
        }
    }
}
