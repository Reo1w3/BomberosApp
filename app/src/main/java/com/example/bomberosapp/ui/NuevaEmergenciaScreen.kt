package com.example.bomberosapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.speech.RecognizerIntent
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.ui.components.CampoTextoEmergencia
import com.example.bomberosapp.ui.components.DropdownFieldSimple
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.components.OsmMapView
import com.example.bomberosapp.ui.components.SignatureDialog
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NuevaEmergenciaScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onFinalizarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.emergencyState
    val context = LocalContext.current
    
    var showMapDialog by remember { mutableStateOf(false) }
    var tempLatLng by remember { mutableStateOf(GeoPoint(14.6349, -90.5069)) }
    var mapTarget by remember { mutableStateOf("emergencia") }

    var showSignatureDialog by remember { mutableStateOf(false) }
    var signatureTarget by remember { mutableStateOf("") } 

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = RojoBomberos,
        checkedTrackColor = RojoBomberos.copy(alpha = 0.5f),
        uncheckedThumbColor = Color.Gray,
        uncheckedTrackColor = Color.LightGray
    )

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                viewModel.observacionesFinales += (if (viewModel.observacionesFinales.isEmpty()) "" else " ") + results[0]
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadConfig()
    }

    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = { Text("Mueva el mapa para ubicar") },
            text = {
                Box(Modifier.fillMaxWidth().height(400.dp)) {
                    OsmMapView(center = tempLatLng, onMapReady = { map ->
                        map.addMapListener(object : MapListener {
                            override fun onScroll(event: ScrollEvent?): Boolean { 
                                tempLatLng = map.mapCenter as GeoPoint
                                return true 
                            }
                            override fun onZoom(event: ZoomEvent?): Boolean = true
                        })
                    })
                    Icon(Icons.Default.Add, null, Modifier.align(Alignment.Center).size(32.dp), Color.Red)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (mapTarget == "emergencia") viewModel.direccionEmergencia = "${tempLatLng.latitude}, ${tempLatLng.longitude}"
                    else viewModel.trasladoA = "${tempLatLng.latitude}, ${tempLatLng.longitude}"
                    showMapDialog = false
                }) { Text("SELECCIONAR UBICACIÓN") }
            },
            dismissButton = { TextButton(onClick = { showMapDialog = false }) { Text("CANCELAR") } }
        )
    }

    if (showSignatureDialog) {
        SignatureDialog(
            onDismiss = { showSignatureDialog = false },
            onConfirm = { base64 ->
                when {
                    signatureTarget == "piloto" -> viewModel.firmaPilotoBase64 = base64
                    signatureTarget == "jefe" -> viewModel.firmaJefeServicioBase64 = base64
                    signatureTarget.startsWith("paramedico_") -> {
                        val index = signatureTarget.substringAfter("paramedico_").toInt()
                        viewModel.updateFirmaParamedico(index, base64)
                    }
                }
                showSignatureDialog = false
            }
        )
    }

    Column(Modifier.fillMaxSize().background(Blanco).statusBarsPadding().navigationBarsPadding()) {
        HeaderApp(title = "REPORTE DE AMBULANCIA", onAction = onVolverClick)

        Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            
            // SECCIÓN 1: DATOS DE SALIDA
            ExpandableSection(title = "DATOS DE SALIDA", isCompleted = viewModel.isGeneralInfoComplete) {
                Text("Unidad", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(options = viewModel.unidadesList, selectedOption = viewModel.unidad, label = "Seleccione Unidad") { viewModel.unidad = it }
                CampoTextoEmergencia(label = "Hora de salida", value = viewModel.horaSalida, onValueChange = { viewModel.horaSalida = it },
                    trailingIcon = { IconButton(onClick = { viewModel.horaSalida = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }) { Icon(Icons.Default.AccessTime, "Hora") } }
                )
                Text("Tipo de Servicio", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(options = viewModel.tiposServicioList, selectedOption = viewModel.tipoServicio, label = "Seleccione Servicio") { viewModel.tipoServicio = it }
                
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Text("Datos del Solicitante", fontWeight = FontWeight.Bold, color = RojoBomberos)
                CampoTextoEmergencia(label = "Nombre del Solicitante", value = viewModel.nombreSolicitante, onValueChange = { viewModel.nombreSolicitante = it })
                CampoTextoEmergencia(label = "Apellido del Solicitante", value = viewModel.apellidoSolicitante, onValueChange = { viewModel.apellidoSolicitante = it })
                CampoTextoEmergencia(label = "Teléfono del Solicitante", value = viewModel.telefonoSolicitante, onValueChange = { viewModel.telefonoSolicitante = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            }

            // SECCIÓN 2: UBICACIÓN
            ExpandableSection(title = "UBICACIÓN Y DIRECCIÓN", isCompleted = viewModel.direccionEmergencia.isNotBlank()) {
                CampoTextoEmergencia(label = "Dirección", value = viewModel.direccionEmergencia, onValueChange = { viewModel.direccionEmergencia = it },
                    trailingIcon = { IconButton(onClick = { mapTarget = "emergencia"; showMapDialog = true }) { Icon(Icons.Default.Map, "Mapa") } }
                )
                CampoTextoEmergencia(label = "Referencias", value = viewModel.referencias, onValueChange = { viewModel.referencias = it })
            }

            // SECCIÓN 3: DATOS DEL PACIENTE
            ExpandableSection(title = "DATOS DEL PACIENTE", isCompleted = viewModel.nombrePaciente.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("¿Varios pacientes?", fontWeight = FontWeight.Bold)
                    Switch(checked = viewModel.existenMasPacientes, onCheckedChange = { viewModel.existenMasPacientes = it }, colors = switchColors)
                }
                
                if (!viewModel.existenMasPacientes) {
                    CampoTextoEmergencia(label = "Nombre del Paciente", value = viewModel.nombrePaciente, onValueChange = { viewModel.nombrePaciente = it })
                    CampoTextoEmergencia(label = "Apellido", value = viewModel.apellidoPaciente, onValueChange = { viewModel.apellidoPaciente = it })
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) { CampoTextoEmergencia(label = "Edad", value = viewModel.edadPaciente, onValueChange = { viewModel.edadPaciente = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
                        Box(Modifier.weight(1f)) { 
                            Column {
                                Text("Sexo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                DropdownFieldSimple(options = listOf("MASCULINO", "FEMENINO"), selectedOption = viewModel.sexoPaciente, label = "Sexo") { viewModel.sexoPaciente = it }
                            }
                        }
                    }
                    CampoTextoEmergencia(label = "DPI / Identificación", value = viewModel.dpiPaciente, onValueChange = { viewModel.dpiPaciente = it })
                    
                    Text("Estado de Salud", fontWeight = FontWeight.Bold)
                    DropdownFieldSimple(options = viewModel.estadosPacienteList, selectedOption = viewModel.estadoPaciente, label = "Seleccione Estado") { viewModel.estadoPaciente = it }
                    
                    Text("Signos Vitales", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) { CampoTextoEmergencia(label = "P.A.", value = viewModel.paPaciente, onValueChange = { viewModel.paPaciente = it }) }
                        Box(Modifier.weight(1f)) { CampoTextoEmergencia(label = "F.C.", value = viewModel.fcPaciente, onValueChange = { viewModel.fcPaciente = it }) }
                        Box(Modifier.weight(1f)) { CampoTextoEmergencia(label = "SatO2", value = viewModel.satPaciente, onValueChange = { viewModel.satPaciente = it }) }
                    }
                } else {
                    viewModel.pacientesList.forEachIndexed { index, p ->
                        CampoTextoEmergencia(label = "Paciente ${index + 1}", value = p.nombre, onValueChange = { viewModel.updatePaciente(index, p.copy(nombre = it)) })
                    }
                    Button(onClick = { viewModel.addPaciente() }, colors = ButtonDefaults.buttonColors(RojoBomberos)) { Text("AGREGAR PACIENTE") }
                }
            }

            // SECCIÓN 5: TRASLADO
            ExpandableSection(title = "DATOS DE TRASLADO", isCompleted = viewModel.trasladoA.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.tieneTraslado, onCheckedChange = { viewModel.tieneTraslado = it }, colors = CheckboxDefaults.colors(RojoBomberos))
                    Text("¿Requiere Traslado?")
                }
                if (viewModel.tieneTraslado) {
                    Text("Hospital / Destino", fontWeight = FontWeight.Bold)
                    DropdownFieldSimple(options = viewModel.hospitalesList, selectedOption = viewModel.hospitalTraslado, label = "Seleccione Hospital") { viewModel.hospitalTraslado = it }
                    CampoTextoEmergencia(label = "Ubicación Destino", value = viewModel.trasladoA, onValueChange = { viewModel.trasladoA = it },
                        trailingIcon = { IconButton(onClick = { mapTarget = "traslado"; showMapDialog = true }) { Icon(Icons.Default.Map, "Mapa") } }
                    )
                    CampoTextoEmergencia(label = "Hora Llegada Hospital", value = viewModel.horaLlegadaTraslado, onValueChange = { viewModel.horaLlegadaTraslado = it },
                        trailingIcon = { IconButton(onClick = { viewModel.horaLlegadaTraslado = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }) { Icon(Icons.Default.AccessTime, "Hora") } }
                    )
                }
            }

            // SECCIÓN 6: PERSONAL DESTACADO
            ExpandableSection(title = "PERSONAL DESTACADO", isCompleted = viewModel.pilotoSeleccionado.isNotBlank()) {
                Text("Piloto", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(viewModel.pilotosCatalogList, viewModel.pilotoSeleccionado, "Seleccione Piloto") { viewModel.pilotoSeleccionado = it }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("¿Varios paramédicos?", fontWeight = FontWeight.Bold)
                    Switch(checked = viewModel.existenMasParamedicos, onCheckedChange = { viewModel.existenMasParamedicos = it }, colors = switchColors)
                }
                
                viewModel.paramedicosSeleccionados.forEachIndexed { index, param ->
                    DropdownFieldSimple(viewModel.paramedicosCatalogList, param, "Paramédico ${index + 1}") { viewModel.updateParamedico(index, it) }
                }
                if (viewModel.existenMasParamedicos) Button(onClick = { viewModel.addParamedico() }, colors = ButtonDefaults.buttonColors(RojoBomberos)) { Text("Agregar Paramédico") }
            }

            // --- SECCIÓN 7: CONTROL Y FIRMAS ---
            ExpandableSection(title = "CONTROL Y FIRMAS", isCompleted = viewModel.firmaJefeServicioBase64.isNotBlank()) {
                // Hora de Llegada
                CampoTextoEmergencia(label = "HORA DE LLEGADA AL INCIDENTE", value = viewModel.horaLlegadaIncidente, onValueChange = { viewModel.horaLlegadaIncidente = it },
                    trailingIcon = { IconButton(onClick = { viewModel.horaLlegadaIncidente = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }) { Icon(Icons.Default.AccessTime, "Reloj") } }
                )

                // Observaciones con Dictado
                CampoTextoEmergencia(
                    label = "OBSERVACIONES FINAL",
                    value = viewModel.observacionesFinales, 
                    onValueChange = { viewModel.observacionesFinales = it },
                    placeholder = "Pulse el micro para dictar...",
                    trailingIcon = {
                        IconButton(onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Hable para dictar observaciones...")
                            }
                            try { speechLauncher.launch(intent) } catch (e: Exception) { Toast.makeText(context, "Dictado no soportado", Toast.LENGTH_SHORT).show() }
                        }) {
                            Icon(Icons.Default.Mic, "Dictar", tint = RojoBomberos)
                        }
                    }
                )

                Text("Reporte Formulado Por", fontWeight = FontWeight.Bold)
                DropdownFieldSimple(viewModel.paramedicosCatalogList, viewModel.reporteFormuladoPor, "Seleccione Paramédico") { viewModel.reporteFormuladoPor = it }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Conformidades
                Text("VISTOS BUENOS", fontWeight = FontWeight.Black, color = Color.Gray, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.conformeJefeServicio, onCheckedChange = { viewModel.conformeJefeServicio = it }, colors = CheckboxDefaults.colors(RojoBomberos))
                    Text("CONFORME JEFE: ${viewModel.jefeServicioNombre}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.conformePiloto, onCheckedChange = { viewModel.conformePiloto = it }, colors = CheckboxDefaults.colors(RojoBomberos))
                    Text("CONFORME PILOTO: ${if(viewModel.pilotoSeleccionado.isNotBlank()) viewModel.pilotoSeleccionado else "SELECCIONE PILOTO"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))
                Text("ESPACIOS PARA FIRMAS DIGITALES", fontWeight = FontWeight.Black, color = RojoBomberos)

                // --- ESPACIOS DE FIRMA DE PARAMÉDICOS (DINÁMICOS) ---
                viewModel.paramedicosSeleccionados.forEachIndexed { index, param ->
                    SignatureField(
                        label = if (viewModel.paramedicosSeleccionados.size > 1) "FIRMA PARAMÉDICO ${index + 1}" else "FIRMA PARAMÉDICO ÚNICO",
                        personName = if (param.isNotBlank()) param else "Seleccione Paramédico",
                        signatureBase64 = viewModel.firmasParamedicosBase64.getOrElse(index) { "" },
                        onSignClick = { 
                            if (param.isNotBlank()) {
                                signatureTarget = "paramedico_$index"
                                showSignatureDialog = true 
                            } else {
                                Toast.makeText(context, "Seleccione al paramédico primero", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClearClick = { viewModel.updateFirmaParamedico(index, "") }
                    )
                }

                // --- ESPACIO DE FIRMA PILOTO ---
                SignatureField(
                    label = "FIRMA PILOTO",
                    personName = if(viewModel.pilotoSeleccionado.isNotBlank()) viewModel.pilotoSeleccionado else "Piloto No Seleccionado",
                    signatureBase64 = viewModel.firmaPilotoBase64,
                    onSignClick = { 
                        if (viewModel.pilotoSeleccionado.isNotBlank()) {
                            signatureTarget = "piloto"
                            showSignatureDialog = true 
                        } else {
                            Toast.makeText(context, "Seleccione al piloto primero", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClearClick = { viewModel.firmaPilotoBase64 = "" }
                )

                // --- ESPACIO DE FIRMA JEFE ---
                SignatureField(
                    label = "FIRMA JEFE DE SERVICIO",
                    personName = viewModel.jefeServicioNombre,
                    signatureBase64 = viewModel.firmaJefeServicioBase64,
                    onSignClick = { signatureTarget = "jefe"; showSignatureDialog = true },
                    onClearClick = { viewModel.firmaJefeServicioBase64 = "" }
                )
            }

            if (uiState is EmergencyUIState.Error) {
                Text(uiState.message, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = { viewModel.saveFullEmergency { onFinalizarClick() } },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(25.dp),
                enabled = uiState !is EmergencyUIState.Loading
            ) {
                if (uiState is EmergencyUIState.Loading) {
                    CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                } else {
                    Text("GUARDAR REPORTE COMPLETO", color = Blanco, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SignatureField(
    label: String, 
    personName: String, 
    signatureBase64: String, 
    onSignClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Black, color = RojoBomberos)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFFCFCFC), RoundedCornerShape(12.dp))
                .border(2.dp, if (signatureBase64.isNotBlank()) Color(0xFF4CAF50) else Color.LightGray, RoundedCornerShape(12.dp))
                .clickable { onSignClick() },
            contentAlignment = Alignment.Center
        ) {
            if (signatureBase64.isNotBlank()) {
                val bitmap = remember(signatureBase64) {
                    try {
                        val decodedString = Base64.decode(signatureBase64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    } catch (e: Exception) { null }
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(), 
                        contentDescription = "Firma", 
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Draw, "Firma", tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    Text("TOQUE PARA REALIZAR FIRMA", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.width(220.dp).height(2.dp).background(Color.LightGray))
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = personName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
            if (signatureBase64.isNotBlank()) {
                Button(
                    onClick = onClearClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Red),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("BORRAR", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    isCompleted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = if (isCompleted) Color(0xFF4CAF50) else RojoBomberos
    val titleColor = if (isCompleted) Color(0xFF2E7D32) else RojoBomberos

    Card(
        modifier = Modifier.fillMaxWidth().border(2.dp, borderColor, RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontWeight = FontWeight.Black, color = titleColor, fontSize = 16.sp)
                    if (isCompleted) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.CheckCircle, "Completado", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    }
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = titleColor)
            }
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
        }
    }
}
