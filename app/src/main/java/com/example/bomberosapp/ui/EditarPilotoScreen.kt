package com.example.bomberosapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Piloto

@Composable
fun EditarPilotoScreen(
    piloto: Piloto,
    onGuardarClick: (Piloto) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf(piloto.nombres) }
    var apellidos by remember { mutableStateOf(piloto.apellidos) }
    var numeroIdentificacion by remember { mutableStateOf(piloto.numeroIdentificacion) }
    var codigoElemento by remember { mutableStateOf(piloto.codigoElemento) }
    var telefono by remember { mutableStateOf(piloto.telefono) }
    var direccion by remember { mutableStateOf(piloto.direccion) }
    var tipoLicencia by remember { mutableStateOf(piloto.tipoLicencia) }
    var numeroLicencia by remember { mutableStateOf(piloto.numeroLicencia) }
    var fechaVencimiento by remember { mutableStateOf(piloto.fechaVencimiento) }
    var turno by remember { mutableStateOf(piloto.turno) }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderApp(title = "EDITAR ELEMENTO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Editar datos del piloto",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE30613)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoEditar("Nombres", nombres) { nombres = it }
            CampoEditar("Apellidos", apellidos) { apellidos = it }
            CampoEditar("Número de identificación", numeroIdentificacion) { numeroIdentificacion = it }
            CampoEditar("Código de elemento", codigoElemento) { codigoElemento = it }
            CampoEditar("Teléfono", telefono) { telefono = it }
            CampoEditar("Dirección", direccion) { direccion = it }
            CampoEditar("Tipo de licencia", tipoLicencia) { tipoLicencia = it }
            CampoEditar("Número de licencia", numeroLicencia) { numeroLicencia = it }
            CampoEditar("Fecha de vencimiento", fechaVencimiento) { fechaVencimiento = it }
            CampoEditar("Turno", turno) { turno = it }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val pilotoActualizado = piloto.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        numeroIdentificacion = numeroIdentificacion,
                        codigoElemento = codigoElemento,
                        telefono = telefono,
                        direccion = direccion,
                        tipoLicencia = tipoLicencia,
                        numeroLicencia = numeroLicencia,
                        fechaVencimiento = fechaVencimiento,
                        turno = turno
                    )

                    onGuardarClick(pilotoActualizado)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                Text(
                    text = "GUARDAR CAMBIOS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CampoEditar(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))
}