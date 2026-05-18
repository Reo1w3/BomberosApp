package com.example.bomberosapp
import com.example.bomberosapp.ui.NuevoElementoScreen
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.ui.AdminViewModel
import com.example.bomberosapp.ui.EmergencyUIState
import com.example.bomberosapp.ui.EmergencyViewModel
import com.example.bomberosapp.ui.FuerzaActivaScreen
import com.example.bomberosapp.ui.LoginUIState
import com.example.bomberosapp.ui.LoginViewModel
import com.example.bomberosapp.ui.PilotoScreen
import com.example.bomberosapp.ui.SeleccionTipoElementoScreen
import com.example.bomberosapp.ui.components.SignatureDialog
import java.text.SimpleDateFormat
import java.util.*

import androidx.lifecycle.lifecycleScope
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.data.repository.PilotoRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.bomberosapp.ui.PilotoViewModel
import com.example.bomberosapp.ui.DetallePilotoScreen
import com.example.bomberosapp.data.model.NuevoElementoTemp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pilotoRepository = PilotoRepository()
        setContent {
            val pilotoViewModel: PilotoViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()
            val emergencyViewModel: EmergencyViewModel = viewModel()
            val adminViewModel: AdminViewModel = viewModel()
            
            var currentScreen by remember { mutableStateOf("login") }
            var pilotoSeleccionado by remember { mutableStateOf<com.example.bomberosapp.data.model.Piloto?>(null) }
            var nuevoElementoTemp by remember { mutableStateOf(NuevoElementoTemp()) }
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    BackHandler(enabled = currentScreen != "login") {
                        currentScreen = when (currentScreen) {
                            "admin_list" -> "admin_home"
                            "form" -> "home"
                            else -> "login"
                        }
                    }

                    Column(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) {
                            when (currentScreen) {
                                "login" -> LoginScreen(loginViewModel) { user ->
                                    currentScreen = if (user == "0" || user == "123") "admin_home" else "home"
                                }
                                "home" -> HomeScreen(
                                    onNewEmergency = {
                                        emergencyViewModel.resetState()
                                        currentScreen = "form"
                                    },
                                    onLogout = {
                                        loginViewModel.resetState()
                                        currentScreen = "login"
                                    }
                                )
                                "admin_home" -> AdminHomeScreen(
                                    onList = { currentScreen = "admin_list" },
                                    onVerFuerzaActiva = { currentScreen = "fuerza_activa" },
                                    onLogout = {
                                        loginViewModel.resetState()
                                        currentScreen = "login"
                                    }
                                )

                                "fuerza_activa" -> {
                                    LaunchedEffect(Unit) { pilotoViewModel.startObserving() }

                                    FuerzaActivaScreen(
                                        pilotos = pilotoViewModel.pilotos,
                                        isLoading = pilotoViewModel.isLoading,
                                        onAgregarNuevoElemento = { currentScreen = "nuevo_elemento" },
                                        onVerDetalle = { piloto ->
                                            pilotoSeleccionado = piloto
                                            currentScreen = "detalle_piloto"
                                        },
                                        onVolver = { currentScreen = "admin_home" }
                                    )
                                }
                                "detalle_piloto" -> {
                                    pilotoSeleccionado?.let { piloto ->
                                        DetallePilotoScreen(
                                            piloto = piloto,
                                            onEditarClick = {
                                            },
                                            onEliminarClick = {
                                            },
                                            onVolverClick = { currentScreen = "fuerza_activa" }
                                        )
                                    }
                                }
                                "nuevo_elemento" -> NuevoElementoScreen(
                                    onContinuarClick = { nombres, apellidos, numeroIdentificacion, codigoElemento, telefono, direccion ->
                                        nuevoElementoTemp = NuevoElementoTemp(
                                            nombres = nombres,
                                            apellidos = apellidos,
                                            numeroIdentificacion = numeroIdentificacion,
                                            codigoElemento = codigoElemento,
                                            telefono = telefono,
                                            direccion = direccion
                                        )
                                        currentScreen = "seleccion_tipo_elemento"
                                    },
                                    onVolverClick = { currentScreen = "fuerza_activa" }
                                )
                                "seleccion_tipo_elemento" -> SeleccionTipoElementoScreen(
                                    onParamedicoClick = { currentScreen = "paramedico" },
                                    onPilotoClick = { currentScreen = "piloto" },
                                    onVolverClick = { currentScreen = "nuevo_elemento" }
                                )
                                "piloto" -> PilotoScreen(
                                    onGuardarClick = { tipoLicencia, numeroLicencia, fechaVencimiento, turno ->
                                        lifecycleScope.launch {
                                            val guardado = pilotoRepository.guardarPiloto(
                                                Piloto(
                                                    nombres = nuevoElementoTemp.nombres,
                                                    apellidos = nuevoElementoTemp.apellidos,
                                                    numeroIdentificacion = nuevoElementoTemp.numeroIdentificacion,
                                                    codigoElemento = nuevoElementoTemp.codigoElemento,
                                                    telefono = nuevoElementoTemp.telefono,
                                                    direccion = nuevoElementoTemp.direccion,
                                                    tipoLicencia = tipoLicencia,
                                                    numeroLicencia = numeroLicencia,
                                                    fechaVencimiento = fechaVencimiento,
                                                    turno = turno,
                                                    tipoElemento = "Piloto"
                                                )
                                            )

                                            if (guardado) {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Piloto guardado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                currentScreen = "fuerza_activa"
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Error al guardar piloto",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onVolverClick = { currentScreen = "seleccion_tipo_elemento" }
                                )
                                "piloto" -> {
                                    Text("Pantalla de Piloto")
                                }
                                "admin_list" -> {
                                    LaunchedEffect(Unit) { adminViewModel.startObserving() }
                                    AdminListScreen(adminViewModel) { currentScreen = "admin_home" }
                                }
                                "form" -> NuevaEmergenciaScreen(emergencyViewModel) { currentScreen = "home" }
                            }
                        }
                        
                        if (currentScreen != "login") {
                            BottomNavBar(
                                currentScreen = currentScreen,
                                onHome = { if(currentScreen.contains("admin")) currentScreen = "admin_home" else currentScreen = "home" },
                                onProfile = { /* Perfil */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: (String) -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val state = viewModel.loginState

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(0.4f).background(Color(0xFFE30613)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(120.dp))
            Spacer(Modifier.height(8.dp))
            Text("INICIO DE SESIÓN", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }

        Box(modifier = Modifier.fillMaxWidth().weight(0.6f).padding(horizontal = 24.dp).offset(y = (-30).dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    LabelWithIcon("VOLUNTARIO", Icons.Default.Person)
                    OutlinedTextField(value = user, onValueChange = { user = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    
                    Spacer(Modifier.height(12.dp))
                    LabelWithIcon("CONTRASEÑA", Icons.Default.Lock)
                    OutlinedTextField(
                        value = pass, onValueChange = { pass = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    TextButton(onClick = { showPass = !showPass }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if(showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("MOSTRAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    if (state is LoginUIState.Error) {
                        Text(state.message, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    Button(
                        onClick = { viewModel.login(user, pass) { onLoginSuccess(user) } },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        enabled = state !is LoginUIState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBABA)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        if (state is LoginUIState.Loading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INGRESAR", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    TextButton(onClick = { }) {
                        Text("OLVIDE MI CONTRASEÑA AQUI", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LabelWithIcon(text: String, icon: ImageVector) {
    Row(Modifier.fillMaxWidth().padding(bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
fun HomeScreen(onNewEmergency: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        HeaderApp(onLogout = onLogout)
        Column(Modifier.padding(20.dp)) {
            MainButton("NUEVA\nEMERGENCIA", Icons.Default.LocalShipping, onNewEmergency)
            Spacer(Modifier.height(16.dp))
            Card(
                Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("ÚLTIMOS CONTROLES", color = Color.White, fontWeight = FontWeight.Black)
                    Text("No existen controles recientes.", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MainButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(90.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(40.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaEmergenciaScreen(viewModel: EmergencyViewModel, onBack: () -> Unit) {
    val f = remember { mutableStateMapOf<String, String>() }
    var signatureBase64 by remember { mutableStateOf("") }
    var signaturePiloto by remember { mutableStateOf("") }
    var signatureJefe by remember { mutableStateOf("") }
    var signaturePers by remember { mutableStateOf("") }
    
    var activeSignatureTarget by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    val unidades = viewModel.unidades
    val tiposServicio = viewModel.tiposServicio

    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "Reporte de ambulancia", onLogout = onBack)

        LazyColumn(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            item {
                RetractableSection("DATOS DE SALIDA") {
                    FieldLabel("Unidad:")
                    DropdownField("Seleccione Unidad", unidades, f["unidad"] ?: "") { f["unidad"] = it }
                    
                    FieldLabel("Hora de salida:")
                    FieldInput(
                        value = f["hS"] ?: "",
                        onValue = { f["hS"] = it },
                        trailingIcon = {
                            IconButton(onClick = {
                                f["hS"] = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            }) {
                                Icon(Icons.Default.AccessTime, "Hora actual")
                            }
                        }
                    )
                    
                    FieldLabel("Teléfono del Solicitante:")
                    FieldInput(f["telS"] ?: "") { f["telS"] = it }
                    
                    FieldLabel("Nombre del Solicitante:")
                    FieldInput(f["nomS"] ?: "") { f["nomS"] = it }
                    
                    FieldLabel("Apellido del Solicitante:")
                    FieldInput(f["apeS"] ?: "") { f["apeS"] = it }
                    
                    FieldLabel("Tipo de servicio:")
                    DropdownField("Seleccione Servicio", tiposServicio, f["tipS"] ?: "") { f["tipS"] = it }
                    
                    FieldLabel("Dirección:")
                    FieldInput(
                        value = f["dirE"] ?: "",
                        isLong = true,
                        placeholder = "Toca acá para escribir o el ícono de ubicación.",
                        onValue = { f["dirE"] = it },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    val lm = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
                                    try {
                                        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) 
                                            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                        
                                        if (location != null) {
                                            f["dirE"] = "${location.latitude}, ${location.longitude}"
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
                        }
                    )
                }

                RetractableSection("DATOS DEL PACIENTE") {
                    FieldLabel("Nombre del paciente:")
                    FieldInput(f["nomP"] ?: "") { f["nomP"] = it }
                    FieldLabel("Apellido del paciente:")
                    FieldInput(f["apeP"] ?: "") { f["apeP"] = it }
                    
                    FieldLabel("Fallecidos:")
                    YesNoToggle(f["fall"] == "true") { f["fall"] = it.toString() }
                    FieldLabel("Sexo del paciente:")
                    FieldInput(f["sexP"] ?: "") { f["sexP"] = it }
                    FieldLabel("Domicilio del paciente:")
                    FieldInput(f["domP"] ?: "") { f["domP"] = it }
                    FieldLabel("Acompañante:")
                    YesNoToggle(f["tieAco"] == "true") { f["tieAco"] = it.toString() }
                    FieldLabel("Nombre del acompañante:")
                    FieldInput(f["nomA"] ?: "") { f["nomA"] = it }
                    FieldLabel("Apellido del acompañante:")
                    FieldInput(f["apeA"] ?: "") { f["apeA"] = it }
                    FieldLabel("Telefono del acompañante:")
                    FieldInput(f["telA"] ?: "") { f["telA"] = it }
                }

                RetractableSection("TRASLADO Y PERSONAL") {
                    FieldLabel("Traslado a")
                    YesNoToggle(f["tieTra"] == "true") { f["tieTra"] = it.toString() }
                    FieldInput(f["trasA"] ?: "") { f["trasA"] = it }
                    FieldLabel("Hora de llegada traslado:")
                    FieldInput(f["hLT"] ?: "") { f["hLT"] = it }
                    FieldLabel("Piloto:")
                    FieldInput(f["piloto"] ?: "") { f["piloto"] = it }
                    FieldLabel("Personal destacado:")
                    FieldInput(f["perD"] ?: "") { f["perD"] = it }
                    FieldLabel("Reporte formulado por:")
                    FieldInput(f["repF"] ?: "") { f["repF"] = it }
                    FieldLabel("Firma del Personal Destacado:")
                    SignatureBox(signaturePers) { activeSignatureTarget = "pers" }
                }

                RetractableSection("OBSERVACIONES") {
                    FieldLabel("Observaciones:")
                    FieldInput(f["obs"] ?: "", isLong = true, placeholder = "Toca acá para escribir o el microfono para hablar.") { f["obs"] = it }
                }

                RetractableSection("FIRMAS FINALES") {
                    FieldLabel("Es conforme el piloto:")
                    YesNoToggle(f["confP"] == "true") { f["confP"] = it.toString() }
                    FieldLabel("Firma del piloto:")
                    SignatureBox(signaturePiloto) { activeSignatureTarget = "piloto" }
                    FieldLabel("Vo. Bo jefe de servicio")
                    FieldInput(f["vobo"] ?: "") { f["vobo"] = it }
                    FieldLabel("Firma del Vo. Bo jefe de servicio:")
                    SignatureBox(signatureJefe) { activeSignatureTarget = "jefe" }
                }

                Spacer(Modifier.height(16.dp))

                if (viewModel.emergencyState is EmergencyUIState.Error) {
                    Text(
                        (viewModel.emergencyState as EmergencyUIState.Error).message,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        viewModel.saveEmergency(
                            f["unidad"] ?: "", f["piloto"] ?: "", f["perD"] ?: "", f["kmS"] ?: "", f["kmE"] ?: "",
                            f["hA"] ?: "", f["hS"] ?: "", f["hL"] ?: "", f["hR"] ?: "", f["hLT"] ?: "",
                            f["nomS"] ?: "", f["apeS"] ?: "", f["telS"] ?: "", f["solTel"] == "true", f["dirE"] ?: "", f["tipS"] ?: "",
                            f["nomP"] ?: "", f["nomCP"] ?: "", f["edaP"] ?: "", f["genP"] ?: "", f["sexP"] ?: "", f["dpiP"] ?: "", f["dirP"] ?: "", f["domP"] ?: "",
                            f["tieAco"] == "true", f["nomA"] ?: "", f["apeA"] ?: "", f["telA"] ?: "",
                            f["pa"] ?: "", f["fc"] ?: "", "", f["sat"] ?: "", f["tem"] ?: "", "",
                            f["diag"] ?: "", f["tieTra"] == "true", f["trasA"] ?: "", f["hosp"] ?: "", f["fall"] == "true", f["obs"] ?: "",
                            f["perD"] ?: "", f["repF"] ?: "", f["vobo"] ?: "", f["confP"] == "true",
                            signatureBase64, signaturePiloto, signatureJefe, signaturePers
                        ) {
                            Toast.makeText(context, "GUARDADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
                    enabled = viewModel.emergencyState !is EmergencyUIState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (viewModel.emergencyState is EmergencyUIState.Loading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text("GUARDAR", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Save, null, tint = Color.Black)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }

        activeSignatureTarget?.let { target ->
            SignatureDialog(onDismiss = { activeSignatureTarget = null }) { base64 ->
                when (target) {
                    "pers" -> signaturePers = base64
                    "piloto" -> signaturePiloto = base64
                    "jefe" -> signatureJefe = base64
                    "main" -> signatureBase64 = base64
                }
                activeSignatureTarget = null
            }
        }
    }
}

@Composable
fun RetractableSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.Red),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth().clickable { expanded = !expanded }, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 2.dp))
}

@Composable
fun FieldInput(
    value: String,
    placeholder: String = "",
    isLong: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValue: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value, onValueChange = onValue,
        modifier = Modifier.fillMaxWidth().then(if(isLong) Modifier.height(120.dp) else Modifier),
        shape = RoundedCornerShape(8.dp),
        placeholder = { if(placeholder.isNotEmpty()) Text(placeholder, fontSize = 11.sp, color = Color.Gray) },
        trailingIcon = trailingIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
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
            placeholder = { Text(label, fontSize = 14.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
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
fun YesNoToggle(selected: Boolean, onSelect: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.End) {
        Row(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFFFDADA)).padding(2.dp)) {
            Box(Modifier.width(55.dp).height(30.dp).clip(RoundedCornerShape(20.dp)).background(if (selected) Color.White else Color.Transparent).clickable { onSelect(true) }, contentAlignment = Alignment.Center) {
                Text("Si", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Box(Modifier.width(55.dp).height(30.dp).clip(RoundedCornerShape(20.dp)).background(if (!selected) Color.White else Color.Transparent).clickable { onSelect(false) }, contentAlignment = Alignment.Center) {
                Text("No", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun SignatureBox(base64: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 4.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, if(base64.isNotEmpty()) Color.Green else Color.Gray), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (base64.isEmpty()) {
            Text("Presione aquí para firmar", color = Color.Gray, fontSize = 12.sp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, "Firmado", tint = Color(0xFF006400))
                Spacer(Modifier.width(8.dp))
                Text("DOCUMENTO FIRMADO", color = Color(0xFF006400), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HeaderApp(title: String = "BENEMÉRITO CUERPO DE BOMBEROS VOLUNTARIOS DE GUATEMALA", onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFE30613)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(45.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.weight(1f))
        IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White) }
    }
}

@Composable
fun BottomNavBar(currentScreen: String, onHome: () -> Unit, onProfile: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color(0xFFE30613)), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).fillMaxHeight().clickable { onHome() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Home, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Box(Modifier.weight(1f).fillMaxHeight().clickable { onProfile() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun AdminHomeScreen(
    onList: () -> Unit,
    onVerFuerzaActiva: () -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "PANEL DE OFICIAL", onLogout = onLogout)
        Column(Modifier.padding(24.dp)) {
            MainButton("VER TODOS\nLOS REPORTES", Icons.Default.Assignment, onList)
            Spacer(modifier = Modifier.height(12.dp))
            MainButton("VER FUERZA\nACTIVA", Icons.Default.Group, onVerFuerzaActiva)
        }
    }

}

@Composable
fun AdminListScreen(viewModel: AdminViewModel, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFE30613)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            Text("HISTORIAL GENERAL", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Red) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                items(viewModel.emergencies) { em ->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(2.dp, Color.Red),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Unidad: ${em.unidad}", fontWeight = FontWeight.Black)
                            Text("Servicio: ${em.tipoServicio}")
                            Text("Paciente: ${em.nombrePaciente}")
                        }
                    }
                }
            }
        }
    }
}
