package com.example.bomberosapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun PacienteAcompananteScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onSiguienteClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "DATOS DEL PACIENTE", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Paciente
            Card(
                modifier = Modifier.fillMaxWidth().border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("DATOS DEL PACIENTE", fontWeight = FontWeight.Black, color = RojoBomberos)
                    
                    CampoTextoEmergencia(label = "Nombre Completo", value = viewModel.nombrePaciente, onValueChange = { viewModel.nombrePaciente = it })
                    CampoTextoEmergencia(label = "Domicilio", value = viewModel.domicilioPaciente, onValueChange = { viewModel.domicilioPaciente = it })
                    CampoTextoEmergencia(label = "Edad", value = viewModel.edadPaciente, onValueChange = { viewModel.edadPaciente = it })
                    
                    Text("Sexo", fontWeight = FontWeight.Bold)
                    DropdownFieldSimple(listOf("Masculino", "Femenino"), viewModel.sexoPaciente) { viewModel.sexoPaciente = it }
                    
                    CampoTextoEmergencia(label = "Estado del Paciente", value = viewModel.estadoPaciente, onValueChange = { viewModel.estadoPaciente = it })
                }
            }

            // Sección Acompañante
            Card(
                modifier = Modifier.fillMaxWidth().border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("DATOS DEL ACOMPAÑANTE", fontWeight = FontWeight.Black, color = RojoBomberos)
                    
                    CampoTextoEmergencia(label = "Nombre(s)", value = viewModel.nombreAcompanante, onValueChange = { viewModel.nombreAcompanante = it })
                    CampoTextoEmergencia(label = "Apellido(s)", value = viewModel.apellidoAcompanante, onValueChange = { viewModel.apellidoAcompanante = it })
                    CampoTextoEmergencia(label = "Teléfono", value = viewModel.telefonoAcompanante, onValueChange = { viewModel.telefonoAcompanante = it })
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("VOLVER", color = Blanco)
                }
                Button(
                    onClick = onSiguienteClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("SIGUIENTE (TRASLADO)", color = Blanco)
                }
            }
        }
    }
}
