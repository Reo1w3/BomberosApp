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
import com.example.bomberosapp.data.model.Paramedico

@Composable
fun EditarParamedicoScreen(
    paramedico: Paramedico,
    onGuardarClick: (Paramedico) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf(paramedico.nombres) }
    var apellidos by remember { mutableStateOf(paramedico.apellidos) }
    var numeroIdentificacion by remember { mutableStateOf(paramedico.numeroIdentificacion) }
    var codigoElemento by remember { mutableStateOf(paramedico.codigoElemento) }
    var telefono by remember { mutableStateOf(paramedico.telefono) }
    var direccion by remember { mutableStateOf(paramedico.direccion) }
    var especialidad by remember { mutableStateOf(paramedico.especialidad) }
    var certificacion by remember { mutableStateOf(paramedico.certificacion) }
    var experiencia by remember { mutableStateOf(paramedico.experiencia) }
    var turno by remember { mutableStateOf(paramedico.turno) }

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
                text = "Editar datos del paramédico",
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
            CampoEditar("Especialidad", especialidad) { especialidad = it }
            CampoEditar("Certificación", certificacion) { certificacion = it }
            CampoEditar("Años de experiencia", experiencia) { experiencia = it }
            CampoEditar("Turno", turno) { turno = it }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val paramedicoActualizado = paramedico.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        numeroIdentificacion = numeroIdentificacion,
                        codigoElemento = codigoElemento,
                        telefono = telefono,
                        direccion = direccion,
                        especialidad = especialidad,
                        certificacion = certificacion,
                        experiencia = experiencia,
                        turno = turno
                    )

                    onGuardarClick(paramedicoActualizado)
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