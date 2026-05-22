package com.example.bomberosapp.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.ui.components.*
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.Blanco
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditarParamedicoScreen(
    paramedico: Paramedico,
    onGuardarClick: (Paramedico) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf(paramedico.nombres) }
    var apellidos by remember { mutableStateOf(paramedico.apellidos) }
    var alias by remember { mutableStateOf(paramedico.alias) }
    var numeroIdentificacion by remember { mutableStateOf(paramedico.numeroIdentificacion) }
    var codigoElemento by remember { mutableStateOf(paramedico.codigoElemento) }
    var telefono by remember { mutableStateOf(paramedico.telefono) }
    var direccion by remember { mutableStateOf(paramedico.direccion) }
    var especialidad by remember { mutableStateOf(paramedico.especialidad) }
    var certificacion by remember { mutableStateOf(paramedico.certificacion) }
    var experiencia by remember { mutableStateOf(paramedico.experiencia) }
    var turno by remember { mutableStateOf(paramedico.turno) }
    var contrasena by remember { mutableStateOf(paramedico.contrasena) }
    var fotoBase64 by remember { mutableStateOf(paramedico.fotoBase64) }

    var isCapturingGps by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    val isFormValid = nombres.isNotBlank() && 
                     apellidos.isNotBlank() && 
                     numeroIdentificacion.isNotBlank() && 
                     codigoElemento.isNotBlank() && 
                     telefono.isNotBlank() &&
                     direccion.isNotBlank() &&
                     contrasena.isNotBlank() &&
                     !isProcessingImage &&
                     !isCapturingGps

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        }
    }

    fun obtenerUbicacionGps() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location: Location? = try {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (_: SecurityException) { null }
            
            location?.let {
                scope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) { isCapturingGps = true }
                    val address = fetchAddress(context, it.latitude, it.longitude)
                    withContext(Dispatchers.Main) {
                        direccion = address
                        isCapturingGps = false
                    }
                }
            }
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) { isProcessingImage = true }
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val base64 = encodeImageToBase64(bitmap)
                    withContext(Dispatchers.Main) {
                        fotoBase64 = base64
                        isProcessingImage = false
                    }
                } else {
                    withContext(Dispatchers.Main) { isProcessingImage = false }
                }
            }
        }
    }

    fun generarContrasena() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        contrasena = (1..8).map { chars.random() }.joinToString("")
    }

    Column(modifier = Modifier.fillMaxSize().background(Blanco)) {
        HeaderApp(title = "EDITAR PARAMÉDICO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "DATOS ACTUALES DEL PARAMÉDICO",
                fontWeight = FontWeight.ExtraBold,
                color = RojoBomberos,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, RojoBomberos.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, RojoBomberos, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoBase64.isNotEmpty()) {
                            val bitmap = decodeBase64ToBitmap(fotoBase64)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Icon(Icons.Default.AddAPhoto, null, tint = RojoBomberos, modifier = Modifier.size(40.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("CAMBIAR FOTO", color = RojoBomberos, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ExpandableSection(
                title = "INFORMACIÓN PERSONAL",
                isCompleted = nombres.isNotBlank() && apellidos.isNotBlank() && numeroIdentificacion.isNotBlank() && codigoElemento.isNotBlank() && telefono.isNotBlank() && direccion.isNotBlank()
            ) {
                CampoTextoEmergencia("Nombres", nombres, { nombres = it })
                CampoTextoEmergencia("Apellidos", apellidos, { apellidos = it })
                CampoTextoEmergencia("Alias (Opcional)", alias, { alias = it })
                CampoTextoEmergencia("DPI / Identificación", numeroIdentificacion, { numeroIdentificacion = it })
                CampoTextoEmergencia("Código de elemento", codigoElemento, { codigoElemento = it })
                CampoTextoEmergencia("Teléfono", telefono, { telefono = it })
                CampoTextoDireccionMapa(
                    label = "Dirección",
                    value = if (isCapturingGps) "Capturando ubicación..." else direccion,
                    onValueChange = { direccion = it },
                    onLocationSelected = { _, _ -> },
                    onGpsClick = { obtenerUbicacionGps() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableSection(
                title = "PERFIL PROFESIONAL",
                isCompleted = especialidad.isNotBlank() && certificacion.isNotBlank() && experiencia.isNotBlank() && turno.isNotBlank()
            ) {
                CampoTextoEmergencia("Especialidad", especialidad, { especialidad = it })
                CampoTextoEmergencia("Certificación", certificacion, { certificacion = it })
                CampoTextoEmergencia("Años de experiencia", experiencia, { experiencia = it })
                DropdownFieldSimple(
                    options = listOf("Mañana", "Tarde", "Noche", "24 Horas"),
                    selectedOption = turno,
                    label = "Turno",
                    onOptionSelected = { turno = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableSection(
                title = "SEGURIDAD DE ACCESO",
                isCompleted = contrasena.isNotBlank() && contrasena.length >= 6
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña (mín. 6 caracteres)") },
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    IconButton(
                        onClick = { generarContrasena() },
                        modifier = Modifier.size(56.dp).background(RojoBomberos, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Refresh, null, tint = Blanco)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onGuardarClick(paramedico.copy(
                        nombres = nombres, apellidos = apellidos, alias = alias,
                        numeroIdentificacion = numeroIdentificacion, codigoElemento = codigoElemento,
                        telefono = telefono, direccion = direccion, especialidad = especialidad,
                        certificacion = certificacion, experiencia = experiencia, turno = turno,
                        contrasena = contrasena, fotoBase64 = fotoBase64
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoBomberos,
                    disabledContainerColor = Color.Gray
                ),
                enabled = isFormValid
            ) {
                if (isProcessingImage || isCapturingGps) {
                    CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
