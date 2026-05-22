package com.example.bomberosapp.ui.components

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.LocationManager
import android.speech.RecognizerIntent
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoEmergencia(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, color = Color.Gray) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RojoBomberos,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = RojoBomberos,
            disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Gray
        )
    )
}

@Composable
fun CampoTextoMicrofono(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        data?.firstOrNull()?.let { onValueChange(it) }
    }

    CampoTextoEmergencia(
        label = label,
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                }
                speechLauncher.launch(intent)
            }) {
                Icon(Icons.Default.Mic, contentDescription = "Voz", tint = RojoBomberos)
            }
        }
    )
}

@Composable
fun CampoTextoDireccionMapa(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onLocationSelected: (Double, Double) -> Unit,
    onGpsClick: (() -> Unit)? = null
) {
    var showMapDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }

    // Intentar extraer coordenadas del string actual para centrar el mapa
    val currentGeoPoint = remember(value) {
        try {
            val coords = Regex("""(-?\d+\.\d+),\s*(-?\d+\.\d+)""").find(value)
            if (coords != null) {
                val lat = coords.groupValues[1].toDouble()
                val lon = coords.groupValues[2].toDouble()
                GeoPoint(lat, lon)
            } else null
        } catch (e: Exception) { null }
    }

    // mapCenter solo se usa para "forzar" la cámara (Inicio, GPS, Búsqueda)
    var mapCenter by remember { mutableStateOf(currentGeoPoint ?: GeoPoint(14.6349, -90.5069)) }

    // Al abrir el diálogo, intentamos obtener GPS una sola vez si está vacío o si se solicita explícitamente
    LaunchedEffect(showMapDialog) {
        if (showMapDialog && value.isBlank()) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val location = if (isGpsEnabled) {
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    } else if (isNetworkEnabled) {
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    } else null

                    location?.let {
                        val point = GeoPoint(it.latitude, it.longitude)
                        mapCenter = point
                        // También actualizamos la dirección si estaba vacía
                        scope.launch(Dispatchers.IO) {
                            val address = fetchAddress(context, it.latitude, it.longitude)
                            withContext(Dispatchers.Main) {
                                onValueChange(address)
                            }
                        }
                    }
                }
            } catch (e: Exception) { }
        } else if (showMapDialog && currentGeoPoint != null) {
            mapCenter = currentGeoPoint
        }
    }

    if (showMapDialog) {
        Dialog(onDismissRequest = { showMapDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "UBICACIÓN DEL INCIDENTE",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = RojoBomberos
                    )
                    
                    Spacer(Modifier.height(12.dp))

                    // BARRA DE BÚSQUEDA
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar dirección o lugar...") },
                        trailingIcon = {
                            IconButton(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        @Suppress("DEPRECATION")
                                        val results = geocoder.getFromLocationName(searchText, 1)
                                        if (!results.isNullOrEmpty()) {
                                            val loc = results[0]
                                            withContext(Dispatchers.Main) {
                                                mapCenter = GeoPoint(loc.latitude, loc.longitude)
                                                searchText = "" // Limpiar búsqueda tras encontrar
                                            }
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "No se encontró el lugar", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) { }
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = RojoBomberos)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(Modifier.weight(1f).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))) {
                        OsmMapView(
                            center = mapCenter,
                            onLocationSelected = { point ->
                                // IMPORTANTE: No actualizamos mapCenter aquí para evitar el "snap"
                                scope.launch(Dispatchers.IO) {
                                    val address = fetchAddress(context, point.latitude, point.longitude)
                                    withContext(Dispatchers.Main) {
                                        onValueChange(address)
                                    }
                                }
                                onLocationSelected(point.latitude, point.longitude)
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showMapDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CONFIRMAR UBICACIÓN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    CampoTextoEmergencia(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = "Ingrese dirección o use el mapa",
        trailingIcon = {
            Row {
                if (onGpsClick != null) {
                    IconButton(onClick = onGpsClick) {
                        Icon(Icons.Default.MyLocation, contentDescription = "GPS Actual", tint = RojoBomberos)
                    }
                }
                IconButton(onClick = { showMapDialog = true }) {
                    Icon(Icons.Default.Map, contentDescription = "Mapa", tint = RojoBomberos)
                }
            }
        }
    )
}

@Composable
fun CampoTextoGPS(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    CampoTextoEmergencia(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = "Latitud, Longitud",
        trailingIcon = {
            Row {
                IconButton(onClick = { 
                    onValueChange("14.6349, -90.5069")
                    Toast.makeText(context, "Coordenadas capturadas con éxito", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.MyLocation, contentDescription = "GPS", tint = RojoBomberos)
                }
            }
        }
    )
}

@Composable
fun CampoFechaPicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            onValueChange(sdf.format(selectedDate.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    CampoTextoEmergencia(
        label = label,
        value = value,
        onValueChange = onValueChange,
        readOnly = true,
        placeholder = "DD/MM/AA",
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "Fecha", tint = RojoBomberos)
            }
        },
        modifier = Modifier.clickable { datePickerDialog.show() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFieldSimple(
    options: List<String>,
    selectedOption: String,
    label: String = "",
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = if (label.isNotEmpty()) { { Text(label) } } else null,
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RojoBomberos,
                unfocusedBorderColor = Color.LightGray
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(Blanco)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun encodeImageToBase64(bitmap: Bitmap): String {
    val maxWidth = 800
    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val finalWidth = if (bitmap.width > maxWidth) maxWidth else bitmap.width
    val finalHeight = if (bitmap.width > maxWidth) (maxWidth / ratio).toInt() else bitmap.height
    
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun fetchAddress(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val street = address.thoroughfare ?: ""
            val feature = address.featureName ?: ""
            val subLocality = address.subLocality ?: ""
            
            val mainText = if (street.isNotBlank()) street else if (feature != "$latitude" && feature != "$longitude") feature else ""
            
            if (mainText.isNotBlank()) {
                val fullAddr = listOf(mainText, subLocality).filter { it.isNotBlank() }.joinToString(", ")
                "$fullAddr ($latitude, $longitude)"
            } else {
                "$latitude, $longitude"
            }
        } else {
            "$latitude, $longitude"
        }
    } catch (e: Exception) {
        "$latitude, $longitude"
    }
}

fun decodeBase64ToBitmap(base64: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        null
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
