package com.example.bomberosapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bomberosapp.data.model.Unidad
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.data.model.Paramedico
import com.example.bomberosapp.data.model.UserRole
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.ui.*
import com.example.bomberosapp.ui.components.*
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoClaro
import com.example.bomberosapp.ui.NuevaEmergenciaScreen as NuevaEmergenciaUI
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import android.graphics.BitmapFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("bomberos_prefs", Context.MODE_PRIVATE)

        setContent {
            val loginViewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return LoginViewModel(prefs = sharedPrefs) as T
                    }
                }
            )
            val emergencyViewModel: EmergencyViewModel = viewModel()
            val adminViewModel: AdminViewModel = viewModel()
            val pilotoViewModel: PilotoViewModel = viewModel()
            val paramedicoViewModel: ParamedicoViewModel = viewModel()
            
            var currentScreen by remember { mutableStateOf("login") }
            var currentUserCode by remember { mutableStateOf("") }
            var currentUserRole by remember { mutableStateOf(UserRole.NONE) }
            var selectedUnidad by remember { mutableStateOf<Unidad?>(null) }
            var selectedPiloto by remember { mutableStateOf<Piloto?>(null) }
            var selectedParamedico by remember { mutableStateOf<Paramedico?>(null) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    BackHandler(enabled = currentScreen != "login") {
                        currentScreen = when (currentScreen) {
                            "admin_todos_controles" -> "admin_home"
                            "admin_unidades" -> "admin_home"
                            "admin_nueva_unidad" -> "admin_unidades"
                            "admin_detalle_unidad" -> "admin_unidades"
                            "admin_editar_unidad" -> "admin_detalle_unidad"
                            "admin_fuerza_activa" -> "admin_home"
                            "admin_detalle_piloto" -> "admin_fuerza_activa"
                            "admin_detalle_paramedico" -> "admin_fuerza_activa"
                            "admin_editar_piloto" -> "admin_detalle_piloto"
                            "admin_editar_paramedico" -> "admin_detalle_paramedico"
                            "admin_seleccion_tipo" -> "admin_fuerza_activa"
                            "admin_nuevo_elemento_piloto" -> "admin_seleccion_tipo"
                            "admin_nuevo_elemento_paramedico" -> "admin_seleccion_tipo"
                            "form" -> "home"
                            "ultimos_controles" -> "home"
                            "solicitar_apoyo" -> "home"
                            "profile" -> if (currentUserRole == UserRole.ADMIN) "admin_home" else "home"
                            else -> "login"
                        }
                    }

                    Column(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) {
                            when (currentScreen) {
                                "login" -> LoginScreen(loginViewModel) { userCode, role ->
                                    currentUserCode = userCode
                                    currentUserRole = role
                                    currentScreen = if (role == UserRole.ADMIN) "admin_home" else "home"
                                }
                                "home" -> HomeScreen(
                                    onNewEmergency = {
                                        emergencyViewModel.resetState()
                                        currentScreen = "form"
                                    },
                                    onUltimosControles = {
                                        currentScreen = "ultimos_controles"
                                    },
                                    onSolicitarApoyo = {
                                        currentScreen = "solicitar_apoyo"
                                    },
                                    onLogout = {
                                        loginViewModel.resetState()
                                        currentScreen = "login"
                                    }
                                )
                                "ultimos_controles" -> UltimosControlesScreen(
                                    viewModel = adminViewModel,
                                    onVolverClick = { currentScreen = "home" }
                                )
                                "solicitar_apoyo" -> SolicitarApoyoScreen(
                                    onVolverClick = { currentScreen = "home" }
                                )
                                "admin_home" -> {
                                    LaunchedEffect(Unit) {
                                        adminViewModel.startObserving()
                                    }
                                    AdminHomeScreen(
                                        emergencies = adminViewModel.emergencies.take(3),
                                        onList = { currentScreen = "admin_todos_controles" },
                                        onUnidades = { currentScreen = "admin_unidades" },
                                        onFuerzaActiva = { currentScreen = "admin_fuerza_activa" },
                                        onLogout = {
                                            loginViewModel.resetState()
                                            currentScreen = "login"
                                        }
                                    )
                                }
                                "admin_todos_controles" -> UltimosControlesScreen(
                                    viewModel = adminViewModel,
                                    title = "HISTORIAL GENERAL",
                                    onVolverClick = { currentScreen = "admin_home" }
                                )
                                "admin_fuerza_activa" -> {
                                    LaunchedEffect(Unit) {
                                        pilotoViewModel.startObserving()
                                        paramedicoViewModel.startObserving()
                                    }
                                    FuerzaActivaScreen(
                                        pilotos = pilotoViewModel.pilotos,
                                        paramedicos = paramedicoViewModel.paramedicos,
                                        isLoading = pilotoViewModel.isLoading || paramedicoViewModel.isLoading,
                                        onAgregarNuevoElemento = { currentScreen = "admin_seleccion_tipo" },
                                        onVerDetallePiloto = { piloto ->
                                            selectedPiloto = piloto
                                            currentScreen = "admin_detalle_piloto"
                                        },
                                        onVerDetalleParamedico = { paramedico ->
                                            selectedParamedico = paramedico
                                            currentScreen = "admin_detalle_paramedico"
                                        },
                                        onVolver = { currentScreen = "admin_home" }
                                    )
                                }
                                "admin_detalle_piloto" -> {
                                    selectedPiloto?.let { piloto ->
                                        DetallePilotoScreen(
                                            piloto = piloto,
                                            onVolverClick = { currentScreen = "admin_fuerza_activa" },
                                            onEditarClick = { currentScreen = "admin_editar_piloto" },
                                            onEliminarClick = {
                                                pilotoViewModel.eliminarPiloto(piloto.id) {
                                                    currentScreen = "admin_fuerza_activa"
                                                }
                                            }
                                        )
                                    }
                                }
                                "admin_detalle_paramedico" -> {
                                    selectedParamedico?.let { para ->
                                        DetalleParamedicoScreen(
                                            paramedico = para,
                                            onVolverClick = { currentScreen = "admin_fuerza_activa" },
                                            onEditarClick = { currentScreen = "admin_editar_paramedico" },
                                            onEliminarClick = {
                                                paramedicoViewModel.eliminarParamedico(para.id) {
                                                    currentScreen = "admin_fuerza_activa"
                                                }
                                            }
                                        )
                                    }
                                }
                                "admin_editar_piloto" -> {
                                    selectedPiloto?.let { piloto ->
                                        EditarPilotoScreen(
                                            piloto = piloto,
                                            onVolverClick = { currentScreen = "admin_detalle_piloto" },
                                            onGuardarClick = { updated ->
                                                pilotoViewModel.actualizarPiloto(updated) {
                                                    selectedPiloto = updated
                                                    currentScreen = "admin_detalle_piloto"
                                                }
                                            }
                                        )
                                    }
                                }
                                "admin_editar_paramedico" -> {
                                    selectedParamedico?.let { para ->
                                        EditarParamedicoScreen(
                                            paramedico = para,
                                            onVolverClick = { currentScreen = "admin_detalle_paramedico" },
                                            onGuardarClick = { updated ->
                                                paramedicoViewModel.actualizarParamedico(updated) {
                                                    selectedParamedico = updated
                                                    currentScreen = "admin_detalle_paramedico"
                                                }
                                            }
                                        )
                                    }
                                }
                                "admin_seleccion_tipo" -> {
                                    SeleccionTipoElementoScreen(
                                        onVolverClick = { currentScreen = "admin_fuerza_activa" },
                                        onPilotoClick = {
                                            currentScreen = "admin_nuevo_elemento_piloto"
                                        },
                                        onParamedicoClick = {
                                            currentScreen = "admin_nuevo_elemento_paramedico"
                                        }
                                    )
                                }
                                "admin_nuevo_elemento_piloto" -> {
                                    NuevoElementoScreen(
                                        tipo = "Piloto",
                                        onVolverClick = { currentScreen = "admin_seleccion_tipo" },
                                        onContinuarClick = { n, a, i, c, t, d, psw, foto, extra ->
                                            if (n.isBlank() || a.isBlank() || i.isBlank() || c.isBlank() || psw.isBlank()) {
                                                return@NuevoElementoScreen
                                            }
                                            val p = Piloto(
                                                id = "",
                                                nombres = n,
                                                apellidos = a,
                                                numeroIdentificacion = i,
                                                codigoElemento = c,
                                                telefono = t,
                                                direccion = d,
                                                contrasena = psw,
                                                fotoBase64 = foto,
                                                tipoLicencia = extra["tipoLicencia"] ?: "",
                                                numeroLicencia = extra["numeroLicencia"] ?: "",
                                                fechaVencimiento = extra["fechaVencimiento"] ?: "",
                                                turno = extra["turno"] ?: ""
                                            )
                                            FirebaseFirestore.getInstance().collection("piloto").add(p).addOnSuccessListener {
                                                currentScreen = "admin_fuerza_activa"
                                            }
                                        }
                                    )
                                }
                                "admin_nuevo_elemento_paramedico" -> {
                                    NuevoElementoScreen(
                                        tipo = "Paramedico",
                                        onVolverClick = { currentScreen = "admin_seleccion_tipo" },
                                        onContinuarClick = { n, a, i, c, t, d, psw, foto, extra ->
                                            if (n.isBlank() || a.isBlank() || i.isBlank() || c.isBlank() || psw.isBlank()) {
                                                return@NuevoElementoScreen
                                            }
                                            val p = Paramedico(
                                                id = "",
                                                nombres = n,
                                                apellidos = a,
                                                numeroIdentificacion = i,
                                                codigoElemento = c,
                                                telefono = t,
                                                direccion = d,
                                                contrasena = psw,
                                                fotoBase64 = foto,
                                                especialidad = extra["especialidad"] ?: "",
                                                certificacion = extra["certificacion"] ?: "",
                                                experiencia = extra["experiencia"] ?: "",
                                                turno = extra["turno"] ?: ""
                                            )
                                            FirebaseFirestore.getInstance().collection("paramedico").add(p).addOnSuccessListener {
                                                currentScreen = "admin_fuerza_activa"
                                            }
                                        }
                                    )
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
                                    onFinalizarClick = { currentScreen = "home" }
                                )
                                "profile" -> ProfileScreen(
                                    userName = currentUserCode,
                                    userRole = currentUserRole,
                                    onLogout = {
                                        loginViewModel.resetState()
                                        currentScreen = "login"
                                    }
                                )
                            }
                        }
                        
                        if (currentScreen != "login") {
                            BottomNavBar(
                                onHome = { 
                                    currentScreen = if(currentScreen.contains("admin")) "admin_home" else "home" 
                                },
                                onProfile = { currentScreen = "profile" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: (String, UserRole) -> Unit) {
    val savedUser = viewModel.getSavedUser()
    var user by remember { mutableStateOf(savedUser) }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(savedUser.isNotEmpty()) }
    val state = viewModel.loginState

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(0.4f).background(RojoBomberos),
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
                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    LabelWithIcon("CONTRASEÑA", Icons.Default.Lock)
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = RojoBomberos)
                            )
                            Text("RECORDARME", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        TextButton(onClick = { showPass = !showPass }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if(showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(Modifier.width(4.dp))
                                Text("MOSTRAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }

                    if (state is LoginUIState.Error) {
                        Text(state.message, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    Button(
                        onClick = { viewModel.login(user, pass, rememberMe) { role -> onLoginSuccess(user, role) } },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        enabled = state !is LoginUIState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = RojoClaro),
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
fun HomeScreen(onNewEmergency: () -> Unit, onUltimosControles: () -> Unit, onSolicitarApoyo: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        HeaderApp(onAction = onLogout)
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MainButton("NUEVA\nEMERGENCIA", Icons.Default.LocalShipping, onNewEmergency)
            
            Card(
                Modifier.fillMaxWidth().height(80.dp).clickable { onUltimosControles() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("ÚLTIMOS CONTROLES", color = Color.White, fontWeight = FontWeight.Black)
                    Text("Toque para ver registros anteriores", color = Color.White, fontSize = 12.sp)
                }
            }

            Card(
                Modifier.fillMaxWidth().height(80.dp).clickable { onSolicitarApoyo() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
            ) {
                Row(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Phone, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SOLICITAR APOYO", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Directorio de compañías", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                border = BorderStroke(1.dp, RojoBomberos.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "\"CUANDO HAY PELIGRO, DIOS ES ACLAMADO Y EL BOMBERO BUSCADO, CUANDO PASA EL PELIGRO, DIOS ES OLVIDADO Y EL BOMBERO IGNORADO\"",
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 18.sp
                )
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
fun BottomNavBar(onHome: () -> Unit, onProfile: () -> Unit) {
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
    emergencies: List<Emergency>,
    onList: () -> Unit, 
    onUnidades: () -> Unit, 
    onFuerzaActiva: () -> Unit, 
    onLogout: () -> Unit
) {
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
                AdminMenuButton("VER FUERZA ACTIVA", Icons.Default.Person, onFuerzaActiva)
                AdminMenuButton("VER UNIDADES", Icons.Default.LocalShipping, onUnidades)

                Spacer(Modifier.height(24.dp))

                Text(
                    "ÚLTIMOS 3 CONTROLES",
                    color = Color(0xFFE30613),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                
                Spacer(Modifier.height(12.dp))

                if (emergencies.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No hay registros recientes", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        emergencies.forEach { emergency ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RojoBomberos.copy(alpha = 0.3f)),
                                colors = CardDefaults.cardColors(containerColor = Blanco)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.History, null, tint = RojoBomberos, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = emergency.tipoServicio,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Unidad: ${emergency.unidad} - Paciente: ${emergency.nombrePaciente}",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
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
            // Ignorar
        } finally {
            isLoading = false
        }
    }

    val filteredUnidades = unidades.filter { it.numero.contains(searchText, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderApp(title = "LISTA DE UNIDADES", icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onBack)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        placeholder = { 
                            Text(
                                "Buscar por número de unidad...", 
                                fontSize = 14.sp, 
                                color = Color.Gray
                            ) 
                        },
                        trailingIcon = { Icon(Icons.Default.Search, null, tint = RojoBomberos) },
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoBomberos,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = RojoBomberos)
                        }
                    } else if (filteredUnidades.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No se encontraron unidades",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredUnidades) { unidad ->
                                CardUnidadSimple(
                                    unidad = unidad,
                                    onClick = { onUnitClick(unidad) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Blanco)
                Spacer(Modifier.width(10.dp))
                Text(
                    "AGREGAR NUEVA UNIDAD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blanco
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CardUnidadSimple(unidad: Unidad, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, RojoBomberos, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (unidad.fotoBase64.isNotEmpty()) {
                    val bitmap = decodeBase64ToBitmap(unidad.fotoBase64)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(Icons.Default.LocalShipping, null, tint = RojoBomberos)
                } else {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = RojoBomberos,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "UNIDAD ${unidad.numero}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = unidad.tipo,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
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
    var fotoBase64 by remember { mutableStateOf(unidadAEditar?.fotoBase64 ?: "") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tiposUnidadList = listOf("AUTOBOMBA LIVIANA", "AUTOBOMBA PESADA", "CAMION NODRIZA-CISTERNA", "CAMION FORESTAL", "RESCATE TECNICO", "CAMION AUTOESCALA", "RESCATE ACUATICO", "AMBULANCIA SOPORTE BASICO", "AMBULANCIA SOPORTE AVANZADO", "MATERIALES PELIGROSOS", "COMANDO-COMUNICACIONES")
    val marcasList = listOf("ROSENBAUER", "MERCEDES-BENZ", "VOLVO", "FREIGHTLINER", "ISUZU", "HINO", "FORD", "CHEVROLET", "RAM", "TOYOTA")
    val coloresList = listOf("ROJO Y BLANCO", "AMARILLO LIMON Y VERDE FLUOR", "ROJO Y AMARILLO FLUOR", "BLANCO Y AZUL", "AMARILLO Y NEGRO", "ROJO Y AMARILLO")
    val estadosList = listOf("EXCELENTE", "BUENO", "REGULAR", "MALO", "CRITICO(DEFECTUOSO)")
    val modelosList = (1989..2027).map { it.toString() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            fotoBase64 = encodeImageToBase64(bitmap)
        }
    }

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
                            // SECCIÓN FOTO DE LA UNIDAD
                            Text(
                                text = "FOTO DE LA UNIDAD",
                                fontWeight = FontWeight.Black,
                                color = RojoBomberos,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(alpha = 0.2f))
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
                            TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                                Text(if (fotoBase64.isEmpty()) "SELECCIONAR FOTO" else "CAMBIAR FOTO", color = RojoBomberos)
                            }
                            
                            Spacer(Modifier.height(16.dp))

                            FieldLabelAdmin("NUMERO DE UNIDAD")
                            FieldInputAdmin(
                                value = numero, 
                                placeholder = "INGRESE NUMERO DE UNIDAD", 
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            ) { numero = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("TIPO DE UNIDAD")
                            DropdownFieldAdmin("SELECCIONAR", tiposUnidadList, tipo) { tipo = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("NUMERO DE PLACA")
                            FieldInputAdmin(
                                value = placa, 
                                placeholder = "INGRESE PLACA"
                            ) { placa = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("MARCA")
                            DropdownFieldAdmin("SELECCIONAR", marcasList, marca) { marca = it }

                            Spacer(Modifier.height(12.dp))
                            FieldLabelAdmin("MODELO")
                            DropdownFieldAdmin("SELECCIONAR", modelosList, modelo) { modelo = it }

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
                                                estado = estadoU,
                                                fotoBase64 = fotoBase64
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            // FOTO EN EL DETALLE
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                                    .border(2.dp, RojoBomberos, RoundedCornerShape(15.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (unidad.fotoBase64.isNotEmpty()) {
                                    val bitmap = decodeBase64ToBitmap(unidad.fotoBase64)
                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                } else {
                                    Icon(Icons.Default.LocalShipping, null, tint = RojoBomberos, modifier = Modifier.size(60.dp))
                                }
                            }

                            Spacer(Modifier.height(20.dp))

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
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Gray)
        Text(text = value.ifEmpty { "N/A" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
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
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(55.dp),
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
