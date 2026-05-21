package com.example.bomberosapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.R
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.ui.theme.RojoClaro

@Composable
fun NuevoElementoScreen(
    onContinuarClick: (
        nombres: String,
        apellidos: String,
        numeroIdentificacion: String,
        codigoElemento: String,
        telefono: String,
        direccion: String,
        contrasena: String
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

    val scrollState = rememberScrollState()

    fun generarContrasena() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        contrasena = (1..8)
            .map { chars.random() }
            .joinToString("")
    }

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
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(8.dp, RojoBomberos, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "INGRESE LOS DATOS",
                        color = RojoBomberos,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CampoNuevoElemento(
                        label = "NOMBRES",
                        value = nombres,
                        onValueChange = { nombres = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoNuevoElemento(
                        label = "APELLIDOS",
                        value = apellidos,
                        onValueChange = { apellidos = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoNuevoElementoNumero(
                        label = "NÚMERO DE IDENTIFICACIÓN PERSONAL",
                        value = numeroIdentificacion,
                        onValueChange = { numeroIdentificacion = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoNuevoElementoNumero(
                        label = "INGRESE CÓDIGO DE ELEMENTO",
                        value = codigoElemento,
                        onValueChange = { codigoElemento = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoNuevoElementoNumero(
                        label = "TELÉFONO",
                        value = telefono,
                        onValueChange = { telefono = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoNuevoElemento(
                        label = "DIRECCIÓN",
                        value = direccion,
                        onValueChange = { direccion = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            CampoNuevoElemento(
                                label = "CONTRASEÑA",
                                value = contrasena,
                                onValueChange = { contrasena = it }
                            )
                        }
                        Button(
                            onClick = { generarContrasena() },
                            colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("GENERAR", fontSize = 10.sp)
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
                        .height(56.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "VOLVER",
                        color = Blanco,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        onContinuarClick(
                            nombres,
                            apellidos,
                            numeroIdentificacion,
                            codigoElemento,
                            telefono,
                            direccion,
                            contrasena
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "CONTINUAR",
                        color = Blanco,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CampoNuevoElemento(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,

            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RojoBomberos,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun CampoNuevoElementoNumero(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0BABA),
                unfocusedBorderColor = Color(0xFFE0BABA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}