package com.example.bomberosapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bomberosapp.R
import com.example.bomberosapp.ui.components.OsmMapView
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NuevaEmergenciaScreen(
    viewModel: EmergencyViewModel,
    onVolverClick: () -> Unit,
    onSiguienteClick: () -> Unit
) {
    val f = viewModel.formData
    val rojoBomberos = Color(0xFFE30613)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var showMapDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(GeoPoint(14.6349, -90.5069)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Toast.makeText(context, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permisos denegados", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadConfig()
    }

    val unidades = viewModel.unidades
    val tiposServicio = viewModel.tiposServicio

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(rojoBomberos)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Reporte de ambulancia",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = rojoBomberos),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Unidad", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            DropdownFieldSimple(unidades, f["unidad"] ?: "") { f["unidad"] = it }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            CampoTextoEmergencia(
                                label = "Hora de salida",
                                value = f["hS"] ?: "",
                                onValueChange = { f["hS"] = it },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        f["hS"] = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                    }) {
                                        Icon(Icons.Default.AccessTime, "Hora actual")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTelefonoEmergencia(
                                label = "Teléfono del solicitante",
                                value = f["telS"] ?: "",
                                onValueChange = { f["telS"] = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoEmergencia(
                                label = "Nombre del solicitante",
                                value = f["nomS"] ?: "",
                                onValueChange = { f["nomS"] = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Tipo de servicio", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            DropdownFieldSimple(tiposServicio, f["tipS"] ?: "") { f["tipS"] = it }

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoEmergencia(
                                label = "Dirección de la emergencia",
                                value = f["dirE"] ?: "",
                                onValueChange = { f["dirE"] = it },
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = {
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                val lm = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
                                                try {
                                                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) 
                                                        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                                    
                                                    if (location != null) {
                                                        f["dirE"] = "${location.latitude}, ${location.longitude}"
                                                        selectedLocation = GeoPoint(location.latitude, location.longitude)
                                                        Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "No se pudo obtener la ubicación. Active el GPS.", Toast.LENGTH_LONG).show()
                                                    }
                                                } catch (e: SecurityException) {
                                                    Toast.makeText(context, "Error de permisos", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                                            }
                                        }) {
                                            Icon(Icons.Default.LocationOn, "Ubicación actual")
                                        }
                                        IconButton(onClick = { showMapDialog = true }) {
                                            Icon(Icons.Default.Map, "Abrir mapa")
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTextoEmergencia(
                                label = "Nombre del paciente",
                                value = f["nomP"] ?: "",
                                onValueChange = { f["nomP"] = it }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "VOLVER",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSiguienteClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = rojoBomberos),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "SIGUIENTE",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            confirmButton = {
                Button(onClick = {
                    f["dirE"] = "${selectedLocation.latitude}, ${selectedLocation.longitude}"
                    showMapDialog = false
                }) {
                    Text("SELECCIONAR UBICACIÓN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMapDialog = false }) {
                    Text("CANCELAR")
                }
            },
            text = {
                Box(Modifier.fillMaxWidth().height(400.dp)) {
                    OsmMapView(
                        center = selectedLocation,
                        onMapReady = { map ->
                            map.addMapListener(object : org.osmdroid.events.MapListener {
                                override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                                    val center = map.mapCenter as GeoPoint
                                    selectedLocation = center
                                    return true
                                }
                                override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean = true
                            })
                        }
                    )
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        tint = Color.Red
                    )
                }
            },
            title = { Text("Mueve el mapa para ubicar") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFieldSimple(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Seleccionar") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CampoTextoEmergencia(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun CampoTelefonoEmergencia(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE30613),
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}
