package com.example.bomberosapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Piloto

@Composable
fun DetallePilotoScreen(
    piloto: Piloto,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "DETALLE DEL ELEMENTO", onLogout = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMACIÓN COMPLETA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE30613)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoDetalle("Nombres", piloto.nombres)
                    CampoDetalle("Apellidos", piloto.apellidos)
                    CampoDetalle("Número de identificación", piloto.numeroIdentificacion)
                    CampoDetalle("Código de elemento", piloto.codigoElemento)
                    CampoDetalle("Teléfono", piloto.telefono)
                    CampoDetalle("Dirección", piloto.direccion)
                    CampoDetalle("Tipo de elemento", piloto.tipoElemento)
                    CampoDetalle("Tipo de licencia", piloto.tipoLicencia)
                    CampoDetalle("Número de licencia", piloto.numeroLicencia)
                    CampoDetalle("Fecha de vencimiento", piloto.fechaVencimiento)
                    CampoDetalle("Turno", piloto.turno)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEditarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = " EDITAR DATOS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEliminarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = " ELIMINAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CampoDetalle(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (valor.isBlank()) "Sin dato" else valor,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}