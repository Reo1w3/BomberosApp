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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.ui.components.*
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos
import java.io.InputStream

@Composable
fun NuevoElementoScreen(
    tipo: String = "Piloto",
    onContinuarClick: (
        nombres: String,
        apellidos: String,
        numeroIdentificacion: String,
        codigoElemento: String,
        telefono: String,
        direccion: String,
        contrasena: String,
        fotoBase64: String,
        extraFields: Map<String, String>
    ) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var numeroIdentificacion by remember { mutableStateOf("") }
    var codigoElemento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var fotoBase64 by remember { mutableStateOf("") }

    // Piloto Fields
    var tipoLicencia by remember { mutableStateOf("") }
    var numeroLicencia by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }

    // Paramedico Fields
    var especialidad by remember { mutableStateOf("") }
    var certificacion by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") } 

    // Common
    var turno by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val tiposLicencia = listOf("TIPO A", "TIPO B", "TIPO C", "TIPO M")
    val turnos = listOf("GUARDIA A", "GUARDIA B", "GUARDIA C", "CAMBIO DE TURNO (8 HORAS)", "PERSONAL PERMANENTE")
    val especialidadesParamedico = listOf(
        "TECNICO EN ATENCION PREHOSPITALARIA",
        "COMBATE DE INCENDIOS AVANZADOS",
        "FUERZA DE RESCATE ESPECIALIZADO",
        "AREAS TECNICAS DE PREVENCION"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            fotoBase64 = encodeImageToBase64(bitmap)
        }
    }

    fun generarContrasena() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        contrasena = (1..8)
            .map { chars.random() }
            .joinToString("")
    }

    val isFormValid = nombres.isNotBlank() && 
                     apellidos.isNotBlank() && 
                     numeroIdentificacion.isNotBlank() && 
                     codigoElemento.isNotBlank() && 
                     contrasena.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(
            title = "REGISTRO DE NUEVO ELEMENTO",
            onAction = onVolverClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            
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
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoBase64.isNotEmpty()) {
                            val bitmap = decodeBase64ToBitmap(fotoBase64)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
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
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, null, tint = Blanco, modifier = Modifier.size(18.dp))
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

            // SECCIÓN 1: DATOS PERSONALES
            ExpandableSection(
                title = "1. DATOS PERSONALES",
                isCompleted = nombres.isNotBlank() && apellidos.isNotBlank()
            ) {
                CampoTextoMicrofono(
                    label = "Nombres",
                    value = nombres,
                    onValueChange = { nombres = it }
                )
                CampoTextoMicrofono(
                    label = "Apellidos",
                    value = apellidos,
                    onValueChange = { apellidos = it }
                )
                CampoTextoEmergencia(
                    label = "DPI / Identificación",
                    value = numeroIdentificacion,
                    onValueChange = { if (it.all { char -> char.isDigit() }) numeroIdentificacion = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // SECCIÓN 2: DATOS DE CONTACTO
            ExpandableSection(
                title = "2. DATOS DE CONTACTO",
                isCompleted = telefono.isNotBlank() && direccion.isNotBlank()
            ) {
                CampoTextoEmergencia(
                    label = "Teléfono",
                    value = telefono,
                    onValueChange = { if (it.all { char -> char.isDigit() }) telefono = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                CampoTextoGPS(
                    label = "Dirección",
                    value = direccion,
                    onValueChange = { direccion = it }
                )
            }

            // SECCIÓN 3: DATOS INSTITUCIONALES
            ExpandableSection(
                title = "3. DATOS INSTITUCIONALES",
                isCompleted = codigoElemento.isNotBlank() && contrasena.isNotBlank()
            ) {
                CampoTextoEmergencia(
                    label = "Código de Elemento",
                    value = codigoElemento,
                    onValueChange = { if (it.all { char -> char.isDigit() }) codigoElemento = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                DropdownFieldSimple(
                    label = "Turno Asignado",
                    options = turnos,
                    selectedOption = turno,
                    onOptionSelected = { turno = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CampoTextoEmergencia(
                            label = "Contraseña de Acceso",
                            value = contrasena,
                            onValueChange = { contrasena = it }
                        )
                    }
                    Button(
                        onClick = { generarContrasena() },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp).padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (tipo == "Piloto") {
                ExpandableSection(
                    title = "4. INFORMACIÓN DE LICENCIA",
                    isCompleted = numeroLicencia.isNotBlank()
                ) {
                    DropdownFieldSimple(
                        label = "Tipo de Licencia",
                        options = tiposLicencia,
                        selectedOption = tipoLicencia,
                        onOptionSelected = { tipoLicencia = it }
                    )
                    CampoTextoEmergencia(
                        label = "Número de Licencia",
                        value = numeroLicencia,
                        onValueChange = { if (it.all { char -> char.isDigit() }) numeroLicencia = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    CampoFechaPicker(
                        label = "Fecha de Vencimiento",
                        value = fechaVencimiento,
                        onValueChange = { fechaVencimiento = it }
                    )
                }
            } else if (tipo == "Paramedico") {
                ExpandableSection(
                    title = "4. ESPECIALIDAD Y CERTIFICACIÓN",
                    isCompleted = especialidad.isNotBlank()
                ) {
                    DropdownFieldSimple(
                        label = "Especialidad",
                        options = especialidadesParamedico,
                        selectedOption = especialidad,
                        onOptionSelected = { especialidad = it }
                    )
                    CampoTextoEmergencia(
                        label = "Certificación",
                        value = certificacion,
                        placeholder = "INGRESAR EL NUMERO DE CERTIFICACION",
                        onValueChange = { if (it.all { char -> char.isDigit() }) certificacion = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    CampoTextoEmergencia(
                        label = "Años de Experiencia",
                        value = experiencia,
                        onValueChange = { if (it.all { char -> char.isDigit() }) experiencia = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTONES DE ACCIÓN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onVolverClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    border = BorderStroke(1.dp, RojoBomberos),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("VOLVER", color = RojoBomberos, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (isFormValid) {
                            val extraFieldsMap = mutableMapOf<String, String>()
                            extraFieldsMap["turno"] = turno
                            if (tipo == "Piloto") {
                                extraFieldsMap["tipoLicencia"] = tipoLicencia
                                extraFieldsMap["numeroLicencia"] = numeroLicencia
                                extraFieldsMap["fechaVencimiento"] = fechaVencimiento
                            } else {
                                extraFieldsMap["especialidad"] = especialidad
                                extraFieldsMap["certificacion"] = certificacion
                                extraFieldsMap["experiencia"] = experiencia
                            }

                            onContinuarClick(
                                nombres, apellidos, numeroIdentificacion,
                                codigoElemento, telefono, direccion, contrasena, fotoBase64,
                                extraFieldsMap
                            )
                        } else {
                            Toast.makeText(context, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                    shape = RoundedCornerShape(25.dp),
                    enabled = isFormValid
                ) {
                    Text("CONTINUAR", color = Blanco, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
