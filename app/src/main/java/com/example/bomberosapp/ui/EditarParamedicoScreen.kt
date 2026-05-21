package com.example.bomberosapp.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.components.encodeImageToBase64
import java.io.InputStream

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
    var fotoBase64 by remember { mutableStateOf(paramedico.fotoBase64) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                fotoBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

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

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(3.dp, Color(0xFFE30613), CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoBase64.isNotEmpty()) {
                    val bitmap = decodeBase64ToBitmap(fotoBase64)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(bottom = 8.dp).size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = numeroIdentificacion,
                onValueChange = { numeroIdentificacion = it },
                label = { Text("Número de identificación") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = codigoElemento,
                onValueChange = { codigoElemento = it },
                label = { Text("Código de elemento") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = certificacion,
                onValueChange = { certificacion = it },
                label = { Text("Certificación") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = experiencia,
                onValueChange = { experiencia = it },
                label = { Text("Años de experiencia") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = turno,
                onValueChange = { turno = it },
                label = { Text("Turno") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

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
                        turno = turno,
                        fotoBase64 = fotoBase64
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
