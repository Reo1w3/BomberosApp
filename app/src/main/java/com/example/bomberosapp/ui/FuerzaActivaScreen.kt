package com.example.bomberosapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.data.model.Piloto

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
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderApp(title = "FUERZA ACTIVA", onAction = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = onAgregarNuevoElemento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE30613)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Color.White
                )

                Text(
                    text = "  AGREGAR NUEVO\nELEMENTO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF4F4F4)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "ELEMENTOS REGISTRADOS",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    color = Color(0xFFE30613),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFE30613)
                )
            }

            if (pilotos.isEmpty() && paramedicos.isEmpty() && !isLoading) {
                Text(
                    text = "No hay elementos registrados",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            pilotos.forEach { piloto ->
                CardPilotoSimple(
                    piloto = piloto,
                    onClick = { onVerDetallePiloto(piloto) }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            paramedicos.forEach { paramedico ->
                CardParamedicoSimple(
                    paramedico = paramedico,
                    onClick = { onVerDetalleParamedico(paramedico) }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CardPilotoSimple(
    piloto: Piloto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDEAF0)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Color(0xFFE30613)
            )

            Text(
                text = "  ${piloto.nombres} ${piloto.apellidos}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun CardParamedicoSimple(
    paramedico: Paramedico,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDEAF0)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                tint = Color(0xFFE30613)
            )

            Text(
                text = "  ${paramedico.nombres} ${paramedico.apellidos}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 17.sp
            )
        }
    }
}