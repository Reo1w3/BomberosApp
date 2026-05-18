package com.example.bomberosapp.ui

import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.MainButton


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FuerzaActivaScreen(
    onAgregarNuevoElemento: () -> Unit,
    onVolver: () -> Unit
) {
    val elementos = listOf(
        "001 - Juan Pérez",
        "002 - María López",
        "003 - Carlos Ramírez"
    )

    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "FUERZA ACTIVA", onLogout = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            MainButton(
                "AGREGAR NUEVO\nELEMENTO",
                Icons.Default.PersonAdd,
                onAgregarNuevoElemento
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ELEMENTOS REGISTRADOS",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            elementos.forEach { elemento ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = elemento,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}