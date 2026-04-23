package com.example.bomberosapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bomberosapp.ui.LoginUIState
import com.example.bomberosapp.ui.LoginViewModel
import com.example.bomberosapp.ui.theme.BomberosAppTheme

enum class Screen { Login, Home, AdminHome }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización manual de Firebase (Cargando la API Key de forma segura desde BuildConfig)
        val options = FirebaseOptions.Builder()
            .setProjectId("bomberosapp-74af6")
            .setApplicationId("1:762131751015:android:f78cdbab6aee7ed1c874c2")
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setStorageBucket("bomberosapp-74af6.firebasestorage.app")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }

        enableEdgeToEdge()
        setContent {
            BomberosAppTheme {
                val viewModel: LoginViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(Screen.Login) }
                val loginState = viewModel.loginState

                LaunchedEffect(loginState) {
                    if (loginState is LoginUIState.Success) {
                        currentScreen = Screen.Home
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    if (currentScreen == Screen.Home || currentScreen == Screen.AdminHome) {
                        BackHandler {
                            currentScreen = Screen.Login
                            viewModel.resetState()
                        }
                    }

                    when (currentScreen) {
                        Screen.Login -> {
                            Box {
                                BomberosLoginScreen(
                                    onLoginClick = { usuario, pass ->
                                        viewModel.login(usuario, pass) {
                                            if (usuario == "0") {
                                                currentScreen = Screen.AdminHome
                                            } else {
                                                currentScreen = Screen.Home
                                            }
                                        }
                                    }
                                )
                                
                                if (loginState is LoginUIState.Error) {
                                    Toast.makeText(LocalContext.current, loginState.message, Toast.LENGTH_SHORT).show()
                                    viewModel.resetState()
                                }
                                
                                if (loginState is LoginUIState.Loading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                }
                            }
                        }
                        Screen.Home -> BomberosHomeScreen(
                            onHomeClick = {
                                currentScreen = Screen.Login
                                viewModel.resetState()
                            }
                        )
                        Screen.AdminHome -> BomberosAdminHomeScreen(
                            onHomeClick = {
                                currentScreen = Screen.Login
                                viewModel.resetState()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BomberosLoginScreen(onLoginClick: (String, String) -> Unit) {
    var voluntario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val condensedTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.38f).background(Color.White))
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFE30613)))
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.38f)) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp).size(190.dp),
                    contentScale = ContentScale.None
                )
                Text(
                    text = "INICIO DE SESIÓN",
                    color = Color(0xFFE30613),
                    fontSize = 32.sp,
                    style = condensedTextStyle,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 55.dp)
                )
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
                Card(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .fillMaxWidth(0.82f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginInputField("VOLUNTARIO", Icons.Default.Person, voluntario, { voluntario = it }, textStyle = condensedTextStyle)
                        Spacer(modifier = Modifier.height(20.dp))
                        LoginInputField("CONTRASEÑA", Icons.Default.Lock, contrasena, { contrasena = it }, true, passwordVisible, condensedTextStyle)
                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(24.dp)) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, Modifier.size(16.dp))
                            }
                            Text("MOSTRAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(
                            onClick = { onLoginClick(voluntario, contrasena) },
                            modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0C0)),
                            shape = RoundedCornerShape(27.5.dp)
                        ) {
                            Text("INGRESAR", color = Color.Black, fontSize = 24.sp, style = condensedTextStyle)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                            Icon(Icons.Outlined.Lightbulb, null, Modifier.size(18.dp))
                            Text(buildAnnotatedString {
                                append("OLVIDE MI CONTRASEÑA ")
                                withStyle(SpanStyle(color = Color(0xFF007AFF))) { append("AQUI") }
                            }, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BomberosHomeScreen(onHomeClick: () -> Unit) {
    val condensedTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.2).sp
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(110.dp).background(Color(0xFFE30613)).padding(top = 25.dp, start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "BENEMÉRITO CUERPO DE\nBOMBEROS VOLUNTARIOS DE\nGUATEMALA",
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                style = condensedTextStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.82f).height(85.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.LocalShipping, null, Modifier.size(40.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "NUEVA\nEMERGENCIA",
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        style = condensedTextStyle,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Card(
                modifier = Modifier.fillMaxWidth(0.82f).height(100.dp),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ÚLTIMOS CONTROLES",
                        color = Color.White,
                        fontSize = 18.sp,
                        style = condensedTextStyle
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No existen controles recientes.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(90.dp).background(Color(0xFFE30613)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Default.Home, null, Modifier.size(45.dp), tint = Color.Black)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.Person, null, Modifier.size(45.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
fun BomberosAdminHomeScreen(onHomeClick: () -> Unit) {
    val condensedTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.2).sp
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFE30613))
                .padding(top = 35.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(65.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "BENEMÉRITO CUERPO DE\nBOMBEROS VOLUNTARIOS DE\nGUATEMALA",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                style = condensedTextStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE30613))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "BIENVENIDO OFICIAL",
                color = Color.White,
                fontSize = 32.sp,
                style = condensedTextStyle
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight()
                    .offset(y = (-5).dp),
                shape = RoundedCornerShape(35.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "¿QUE DESEA REALIZAR?",
                        color = Color(0xFFE30613),
                        fontSize = 20.sp,
                        style = condensedTextStyle
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    AdminMenuOption(Icons.Default.Shield, "VER TODOS LOS\nCONTROLES", condensedTextStyle)
                    Spacer(modifier = Modifier.height(20.dp))
                    AdminMenuOption(Icons.Default.Person, "VER FUERZA ACTIVA", condensedTextStyle)
                    Spacer(modifier = Modifier.height(20.dp))
                    AdminMenuOption(Icons.Default.LocalShipping, "VER UNIDADES", condensedTextStyle)
                    
                    Spacer(modifier = Modifier.weight(1f))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "ÚLTIMOS CONTROLES",
                                color = Color.White,
                                fontSize = 22.sp,
                                style = condensedTextStyle
                            )
                            Text(
                                "No existen controles recientes.",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color(0xFFE30613)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Default.Home, null, Modifier.size(50.dp), tint = Color.Black)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.Person, null, Modifier.size(50.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
fun AdminMenuOption(icon: ImageVector, text: String, style: TextStyle) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE30613))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(45.dp), tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            style = style,
            lineHeight = 22.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoginInputField(label: String, icon: ImageVector, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false, passwordVisible: Boolean = false, textStyle: TextStyle) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 19.sp, style = textStyle)
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(0.95f).height(56.dp),
            textStyle = textStyle.copy(fontSize = 18.sp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black, 
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true
        )
    }
}
