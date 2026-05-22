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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.ui.components.CampoFechaPicker
import com.example.bomberosapp.ui.components.CampoTextoEmergencia
import com.example.bomberosapp.ui.components.DropdownFieldSimple
import com.example.bomberosapp.ui.components.ExpandableSection
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.components.encodeImageToBase64
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.Blanco
import java.io.InputStream

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
    var contrasena by remember { mutableStateOf(piloto.contrasena) }
    var fotoBase64 by remember { mutableStateOf(piloto.fotoBase64) }
    
    var showPassword by remember { mutableStateOf(false) }

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

    fun generarContrasena() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        contrasena = (1..8)
            .map { chars.random() }
            .joinToString("")
    }

    Column(modifier = Modifier.fillMaxSize().background(Blanco)) {
        HeaderApp(title = "EDITAR PILOTO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "DATOS ACTUALES DEL PILOTO",
                fontWeight = FontWeight.ExtraBold,
                color = RojoBomberos,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .border(3.dp, RojoBomberos, CircleShape)
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
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(65.dp), tint = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(bottom = 8.dp).size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            ExpandableSection(title = "INFORMACIÓN PERSONAL") {
                CampoTextoEmergencia("Nombres", nombres, { nombres = it })
                CampoTextoEmergencia("Apellidos", apellidos, { apellidos = it })
                CampoTextoEmergencia("DPI / Identificación", numeroIdentificacion, { numeroIdentificacion = it })
                CampoTextoEmergencia("Código de elemento", codigoElemento, { codigoElemento = it })
                CampoTextoEmergencia("Teléfono", telefono, { telefono = it })
                CampoTextoEmergencia("Dirección", direccion, { direccion = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableSection(title = "LICENCIA Y TURNO") {
                DropdownFieldSimple(
                    options = listOf("Tipo A", "Tipo B", "Tipo C"),
                    selectedOption = tipoLicencia,
                    label = "Tipo de licencia",
                    onOptionSelected = { tipoLicencia = it }
                )
                CampoTextoEmergencia("Número de licencia", numeroLicencia, { numeroLicencia = it })
                CampoFechaPicker("Fecha de vencimiento", fechaVencimiento, { fechaVencimiento = it })
                DropdownFieldSimple(
                    options = listOf("Mañana", "Tarde", "Noche", "24 Horas"),
                    selectedOption = turno,
                    label = "Turno",
                    onOptionSelected = { turno = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableSection(title = "SEGURIDAD DE ACCESO") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña de Acceso") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = RojoBomberos
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    
                    Button(
                        onClick = { generarContrasena() },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

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
                        turno = turno,
                        contrasena = contrasena,
                        fotoBase64 = fotoBase64
                    )
                    onGuardarClick(pilotoActualizado)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "GUARDAR CAMBIOS",
                    color = Blanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

