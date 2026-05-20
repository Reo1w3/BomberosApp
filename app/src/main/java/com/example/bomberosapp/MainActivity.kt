package com.example.bomberosapp

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.model.Unidad
import com.example.bomberosapp.ui.AdminViewModel
import com.example.bomberosapp.ui.EmergencyUIState
import com.example.bomberosapp.ui.EmergencyViewModel
import com.example.bomberosapp.ui.LoginUIState
import com.example.bomberosapp.ui.LoginViewModel
import com.example.bomberosapp.ui.NuevaEmergenciaScreen as NuevaEmergenciaUI
import com.example.bomberosapp.ui.PacienteAcompananteScreen
import com.example.bomberosapp.ui.components.SignatureDialog
import com.example.bomberosapp.ui.components.OsmMapView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel: LoginViewModel = viewModel()
            val emergencyViewModel: EmergencyViewModel = viewModel()
            val adminViewModel: AdminViewModel = viewModel()
            
            var currentScreen by remember { mutableStateOf("login") }
            var selectedUnidad by remember { mutableStateOf<Unidad?>(null) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    BackHandler(enabled = currentScreen != "login") {
                        currentScreen = when (currentScreen) {
                            "admin_list" -> "admin_home"
                            "admin_unidades" -> "admin_home"
                            "admin_nueva_unidad" -> "admin_unidades"
                            "admin_detalle_unidad" -> "admin_unidades"
                            "admin_editar_unidad" -> "admin_detalle_unidad"
                            "form" -> "home"
                            "form_paciente" -> "form"
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
                                    onUnidades = { currentScreen = "admin_unidades" },
                                    onLogout = {
                                        loginViewModel.resetState()
                                        currentScreen = "login"
                                    }
                                )
                                "admin_list" -> {
                                    LaunchedEffect(Unit) { adminViewModel.startObserving() }
                                    AdminListScreen(adminViewModel) { currentScreen = "admin_home" }
                                }
                                "admin_unidades" -> {
                                    AdminUnidadesScreen(
                                        onBack = { currentScreen = "admin_home" },
                                        onAdd = { currentScreen = "admin_nueva_unidad" },
                                        onUnitClick = { unit ->
                                            selectedUnidad = unit
                                            currentScreen = "admin_detalle_unidad"
                                        }
                                    )
                                }
                                "admin_nueva_unidad" -> {
                                    AgregarUnidadScreen(
                                        onBack = { currentScreen = "admin_unidades" },
                                        onSuccess = { currentScreen = "admin_unidades" }
                                    )
                                }
                                "admin_detalle_unidad" -> {
                                    selectedUnidad?.let { unit ->
                                        DetalleUnidadScreen(
                                            unidad = unit,
                                            onBack = { currentScreen = "admin_unidades" },
                                            onEdit = { currentScreen = "admin_editar_unidad" },
                                            onDeleteSuccess = { currentScreen = "admin_unidades" }
                                        )
                                    }
                                }
                                "admin_editar_unidad" -> {
                                    selectedUnidad?.let { unit ->
                                        AgregarUnidadScreen(
                                            unidadAEditar = unit,
                                            onBack = { currentScreen = "admin_detalle_unidad" },
                                            onSuccess = {
                                                // Refrescar la unidad seleccionada después de editar
                                                FirebaseFirestore.getInstance().collection("unidad").document(unit.id).get()
                                                    .addOnSuccessListener { doc ->
                                                        selectedUnidad = doc.toObject(Unidad::class.java)?.copy(id = doc.id)
                                                        currentScreen = "admin_detalle_unidad"
                                                    }
                                            }
                                        )
                                    }
                                }
                                "form" -> NuevaEmergenciaUI(
                                    viewModel = emergencyViewModel,
                                    onVolverClick = { currentScreen = "home" },
                                    onSiguienteClick = { currentScreen = "form_paciente" }
                                )
                                "form_paciente" -> PacienteAcompananteScreen(
                                    viewModel = emergencyViewModel,
                                    onVolverClick = { currentScreen = "form" },
                                    onSiguienteClick = { /* Próximo paso: Signatures/Resumen */ }
                                )
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
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
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
        HeaderApp(onAction = onLogout)
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

@Composable
fun HeaderApp(
    title: String = "BENEMÉRITO CUERPO DE BOMBEROS VOLUNTARIOS DE GUATEMALA", 
    icon: ImageVector = Icons.AutoMirrored.Filled.Logout,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFE30613)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(45.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.weight(1f))
        IconButton(onClick = onAction) { Icon(icon, null, tint = Color.White) }
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
fun AdminHomeScreen(onList: () -> Unit, onUnidades: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFE30613))) {
        HeaderApp(onAction = onLogout)
        
        Text(
            "BIENVENIDO OFICIAL",
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    "¿QUE DESEA REALIZAR?",
                    color = Color(0xFFE30613),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(20.dp))

                AdminMenuButton("VER TODOS LOS\nCONTROLES", Icons.Default.Shield, onList)
                AdminMenuButton("VER FUERZA ACTIVA", Icons.Default.Person, {})
                AdminMenuButton("VER UNIDADES", Icons.Default.LocalShipping, onUnidades)

                Spacer(Modifier.weight(1f))

                // Últimos Controles Section
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "ÚLTIMOS CONTROLES",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color.White, RoundedCornerShape(20.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color.White, RoundedCornerShape(20.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .background(Color(0xFFE30613), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )
        }
        Spacer(Modifier.width(20.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 22.sp,
            color = Color.Black
        )
    }
}

@Composable
fun AdminListScreen(viewModel: AdminViewModel, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        HeaderApp(title = "HISTORIAL GENERAL", icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onBack)
        
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.Red) }
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

@Composable
fun AdminUnidadesScreen(onBack: () -> Unit, onAdd: () -> Unit, onUnitClick: (Unidad) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var unidades by remember { mutableStateOf<List<Unidad>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("unidad")
                .get()
                .await()
            unidades = snapshot.documents.mapNotNull { it.toObject(Unidad::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    val filteredUnidades = unidades.filter { it.numero.contains(searchText, ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(Color(0xFFE30613))) {
        HeaderApp(icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onBack)
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(25.dp)),
                placeholder = { 
                    Text(
                        "INGRESE EL NUMERO DE LA UNIDAD", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    ) 
                },
                trailingIcon = { Icon(Icons.Default.Search, null, tint = Color.Black) },
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.Red)
                } else {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        items(filteredUnidades) { unidad ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUnitClick(unidad) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(65.dp)
                                        .background(Color(0xFFE30613), RoundedCornerShape(15.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                                Spacer(Modifier.width(20.dp))
                                Text(
                                    "UNIDAD ${unidad.numero}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "AGREGAR NUEVA\nUNIDAD",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarUnidadScreen(unidadAEditar: Unidad? = null, onBack: () -> Unit, onSuccess: () -> Unit) {
    var numero by remember { mutableStateOf(unidadAEditar?.numero ?: "") }
    var tipo by remember { mutableStateOf(unidadAEditar?.tipo ?: "") }
    var placa by remember { mutableStateOf(unidadAEditar?.placa ?: "") }
    var marca by remember { mutableStateOf(unidadAEditar?.marca ?: "") }
    var modelo by remember { mutableStateOf(unidadAEditar?.modelo ?: "") }
    var fechaRegistro by remember { mutableStateOf(unidadAEditar?.fechaRegistro ?: "") }
    var colorU by remember { mutableStateOf(unidadAEditar?.color ?: "") }
    var estadoU by remember { mutableStateOf(unidadAEditar?.estado ?: "") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tiposUnidadList = listOf("AUTOBOMBA LIVIANA", "AUTOBOMBA PESADA", "CAMION NODRIZA-CISTERNA", "CAMION FORESTAL", "RESCATE TECNICO", "CAMION AUTOESCALA", "RESCATE ACUATICO", "AMBULANCIA SOPORTE BASICO", "AMBULANCIA SOPORTE AVANZADO", "MATERIALES PELIGROSOS", "COMANDO-COMUNICACIONES")
    val marcasList = listOf("ROSENBAUER", "MERCEDES-BENZ", "VOLVO", "FREIGHTLINER", "ISUZU", "HINO", "FORD", "CHEVROLET", "RAM", "TOYOTA")
    val coloresList = listOf("ROJO Y BLANCO", "AMARILLO LIMON Y VERDE FLUOR", "ROJO Y AMARILLO FLUOR", "BLANCO Y AZUL", "AMARILLO Y NEGRO", "ROJO Y AMARILLO")
    val estadosList = listOf("EXCELENTE", "BUENO", "REGULAR", "MALO", "CRITICO(DEFECTUOSO)")

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        fechaRegistro = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        HeaderApp(icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(8.dp, Color(0xFFE30613), RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE30613))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (unidadAEditar != null) "EDITAR DATOS" else "INGRESE LOS DATOS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            FieldLabelAdmin("NUMERO DE UNIDAD")
                            FieldInputAdmin(numero, "ESCRIBIR", KeyboardOptions(keyboardType = KeyboardType.Number)) { numero = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("TIPO DE UNIDAD")
                            DropdownFieldAdmin("SELECCIONAR", tiposUnidadList, tipo) { tipo = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("NUMERO DE PLACA")
                            FieldInputAdmin(placa, "ESCRIBIR") { placa = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("MARCA")
                            DropdownFieldAdmin("SELECCIONAR", marcasList, marca) { marca = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("MODELO")
                            FieldInputAdmin(modelo, "ESCRIBIR", KeyboardOptions(keyboardType = KeyboardType.Number)) { modelo = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("FECHA DE REGISTRO")
                            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                                FieldInputAdmin(fechaRegistro, "SELECCIONAR", enabled = false) { }
                            }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("COLOR")
                            DropdownFieldAdmin("SELECCIONAR", coloresList, colorU) { colorU = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("ESTADO DE LA UNIDAD")
                            DropdownFieldAdmin("SELECCIONAR", estadosList, estadoU) { estadoU = it }

                            Spacer(Modifier.height(40.dp))

                            Button(
                                onClick = {
                                    if (numero.isBlank() || placa.isBlank()) {
                                        Toast.makeText(context, "Complete campos obligatorios", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val nuevaUnidad = Unidad(
                                                numero = numero,
                                                tipo = tipo,
                                                placa = placa,
                                                marca = marca,
                                                modelo = modelo,
                                                fechaRegistro = fechaRegistro,
                                                color = colorU,
                                                estado = estadoU
                                            )
                                            val db = FirebaseFirestore.getInstance()
                                            if (unidadAEditar != null) {
                                                db.collection("unidad").document(unidadAEditar.id)
                                                    .set(nuevaUnidad)
                                                    .await()
                                                Toast.makeText(context, "UNIDAD ACTUALIZADA", Toast.LENGTH_SHORT).show()
                                            } else {
                                                db.collection("unidad")
                                                    .add(nuevaUnidad)
                                                    .await()
                                                Toast.makeText(context, "UNIDAD GUARDADA EXITOSAMENTE", Toast.LENGTH_SHORT).show()
                                            }
                                            onSuccess()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(60.dp)
                                    .border(1.dp, Color.Black, RoundedCornerShape(30.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613)),
                                shape = RoundedCornerShape(30.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Text("GUARDAR", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetalleUnidadScreen(unidad: Unidad, onBack: () -> Unit, onEdit: () -> Unit, onDeleteSuccess: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Unidad", fontWeight = FontWeight.Bold) },
            text = { Text("¿Está seguro que desea eliminar la unidad ${unidad.numero}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isDeleting = true
                        scope.launch {
                            try {
                                FirebaseFirestore.getInstance().collection("unidad").document(unidad.id).delete().await()
                                Toast.makeText(context, "Unidad eliminada", Toast.LENGTH_SHORT).show()
                                onDeleteSuccess()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isDeleting = false
                            }
                        }
                    }
                ) {
                    Text("ELIMINAR", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        HeaderApp(icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(8.dp, Color(0xFFE30613), RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE30613))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "DETALLE DE UNIDAD",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(24.dp)
                    ) {
                        item {
                            DetalleItem("NÚMERO DE UNIDAD", unidad.numero)
                            DetalleItem("TIPO DE UNIDAD", unidad.tipo)
                            DetalleItem("NÚMERO DE PLACA", unidad.placa)
                            DetalleItem("MARCA", unidad.marca)
                            DetalleItem("MODELO", unidad.modelo)
                            DetalleItem("FECHA DE REGISTRO", unidad.fechaRegistro)
                            DetalleItem("COLOR", unidad.color)
                            DetalleItem("ESTADO DE LA UNIDAD", unidad.estado)

                            Spacer(Modifier.height(30.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onEdit,
                                    modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90)),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("EDITAR", color = Color.Black, fontWeight = FontWeight.Black)
                                }
                                Button(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.weight(1f).height(50.dp).border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBABA)),
                                    shape = RoundedCornerShape(25.dp),
                                    enabled = !isDeleting
                                ) {
                                    if (isDeleting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                                    else Text("ELIMINAR", color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = onBack,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613)),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("VOLVER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetalleItem(label: String, value: String) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Gray)
        Text(text = if (value.isEmpty()) "N/A" else value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
fun FieldLabelAdmin(text: String) {
    Text(text, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
fun FieldInputAdmin(
    value: String, 
    placeholder: String, 
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    onValue: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        modifier = Modifier.fillMaxWidth().height(55.dp),
        shape = RoundedCornerShape(15.dp),
        placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 13.sp) },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black,
            disabledPlaceholderColor = Color.LightGray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFieldAdmin(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
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
            placeholder = { Text(label, color = Color.LightGray, fontSize = 13.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
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
