package com.example.bomberosapp.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Piloto
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.components.encodeImageToBase64
import java.io.InputStream

@Composable
fun EditarPilotoScreen(
    piloto: Piloto,
    onGuardarClick: (Piloto) -> Unit,
    onVolverClick: () -> Unit
) {
    var nombres by remember { mutableStateOf(piloto.nombres) }
    var apellidos by remember { mutableStateOf(piloto.apellidos) }
    var numeroIdentificacion by remember { mutableStateOf(piloto.numeroIdentificacion) }
    var codigoElemento by remember { mutableStateOf(piloto.codigoElemento) }
    var telefono by remember { mutableStateOf(piloto.telefono) }
    var direccion by remember { mutableStateOf(piloto.direccion) }
    var tipoLicencia by remember { mutableStateOf(piloto.tipoLicencia) }
    var numeroLicencia by remember { mutableStateOf(piloto.numeroLicencia) }
    var fechaVencimiento by remember { mutableStateOf(piloto.fechaVencimiento) }
    var turno by remember { mutableStateOf(piloto.turno) }
    var fotoBase64 by remember { mutableStateOf(piloto.fotoBase64) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                fotoBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderApp(title = "EDITAR ELEMENTO", onAction = onVolverClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Editar datos del piloto",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE30613)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(3.dp, Color(0xFFE30613), CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoBase64.isNotEmpty()) {
                    val bitmap = decodeBase64ToBitmap(fotoBase64)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(bottom = 8.dp).size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CampoEditar("Nombres", nombres) { nombres = it }
            CampoEditar("Apellidos", apellidos) { apellidos = it }
            CampoEditar("Número de identificación", numeroIdentificacion) { numeroIdentificacion = it }
            CampoEditar("Código de elemento", codigoElemento) { codigoElemento = it }
            CampoEditar("Teléfono", telefono) { telefono = it }
            CampoEditar("Dirección", direccion) { direccion = it }
            CampoEditar("Tipo de licencia", tipoLicencia) { tipoLicencia = it }
            CampoEditar("Número de licencia", numeroLicencia) { numeroLicencia = it }
            CampoEditar("Fecha de vencimiento", fechaVencimiento) { fechaVencimiento = it }
            CampoEditar("Turno", turno) { turno = it }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val pilotoActualizado = piloto.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        numeroIdentificacion = numeroIdentificacion,
                        codigoElemento = codigoElemento,
                        telefono = telefono,
                        direccion = direccion,
                        tipoLicencia = tipoLicencia,
                        numeroLicencia = numeroLicencia,
                        fechaVencimiento = fechaVencimiento,
                        turno = turno,
                        fotoBase64 = fotoBase64
                    )

                    onGuardarClick(pilotoActualizado)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE30613))
            ) {
                Text(
                    text = "GUARDAR CAMBIOS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CampoEditar(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))
}
