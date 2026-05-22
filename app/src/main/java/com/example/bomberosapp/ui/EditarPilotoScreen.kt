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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
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
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.ui.components.*
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.Blanco
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

@Composable
fun EditarPilotoScreen(
    piloto: Piloto,
    onGuardarClick: (Piloto) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf(piloto.nombres) }
    var apellidos by remember { mutableStateOf(piloto.apellidos) }
    var alias by remember { mutableStateOf(piloto.alias) }
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
                        Toast.makeText(context, "Ubicación capturada", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: Toast.makeText(context, "No se pudo obtener GPS. Asegúrese de tener el GPS activado.", Toast.LENGTH_SHORT).show()
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

            // SECCIÓN FOTO DE PERFIL - CENTRADO PROFESIONAL MEJORADO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, RojoBomberos.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "FOTO DE PERFIL",
                        fontWeight = FontWeight.Black,
                        color = RojoBomberos,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(135.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .border(3.5.dp, RojoBomberos, CircleShape)
                            .clickable(enabled = !isProcessingImage) { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoBase64.isNotEmpty() && !isProcessingImage) {
                            val bitmap = decodeBase64ToBitmap(fotoBase64)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else if (isProcessingImage) {
                            CircularProgressIndicator(color = RojoBomberos)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto, 
                                contentDescription = null, 
                                tint = RojoBomberos.copy(alpha = 0.7f), 
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(14.dp))
                    
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        enabled = !isProcessingImage
                    ) {
                        if (isProcessingImage) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Blanco, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PhotoLibrary, null, tint = Blanco, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (fotoBase64.isEmpty()) "SELECCIONAR FOTO" else "CAMBIAR FOTO", 
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
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
                title = "LICENCIA Y TURNO",
                isCompleted = tipoLicencia.isNotBlank() && numeroLicencia.isNotBlank() && fechaVencimiento.isNotBlank() && turno.isNotBlank()
            ) {
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
                        alias = alias,
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoBomberos,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = isFormValid
            ) {
                if (isProcessingImage || isCapturingGps) {
                    CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                } else {
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
}

