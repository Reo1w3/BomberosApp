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
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.ui.components.CampoTextoEmergencia
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos

@Composable
fun TrasladoScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onFinalizarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.emergencyState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "DATOS DE TRASLADO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("INFORMACIÓN DE TRASLADO", fontWeight = FontWeight.Black, color = RojoBomberos)
                    
                    CampoTextoEmergencia(label = "Dirección Origen", value = viewModel.direccionOrigenTraslado, onValueChange = { viewModel.direccionOrigenTraslado = it })
                    CampoTextoEmergencia(label = "Dirección Destino", value = viewModel.direccionDestinoTraslado, onValueChange = { viewModel.direccionDestinoTraslado = it })
                    CampoTextoEmergencia(label = "Hora de Llegada", value = viewModel.horaLlegadaTraslado, onValueChange = { viewModel.horaLlegadaTraslado = it })
                }
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
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

            Button(
                onClick = onVolverClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("VOLVER", color = Blanco)
            }
        }
    }
}
