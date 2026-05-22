package com.example.bomberosapp.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.UserRole
import com.example.bomberosapp.ui.components.ExpandableSection
import com.example.bomberosapp.ui.components.SignatureDialog
import com.example.bomberosapp.ui.components.decodeBase64ToBitmap
import com.example.bomberosapp.ui.components.encodeImageToBase64
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun ProfileScreen(
    userName: String,
    userRole: UserRole,
    onLogout: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var fotoBase64 by remember { mutableStateOf("") }
    var firmaBase64 by remember { mutableStateOf("") }
    var showSignatureDialog by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar datos de perfil desde Firestore
    LaunchedEffect(userName, userRole) {
        val collection = when (userRole) {
            UserRole.PILOTO -> "piloto"
            UserRole.PARAMEDICO -> "paramedico"
            UserRole.PERSONAL, UserRole.ADMIN -> "personal"
            else -> ""
        }
        if (collection.isNotEmpty()) {
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = if (userRole == UserRole.ADMIN || userRole == UserRole.PERSONAL) {
                    val codeInt = userName.toIntOrNull()
                    val firstTry = if (codeInt != null) {
                        db.collection(collection).whereEqualTo("codigo_personal", codeInt).get().await()
                    } else null
                    
                    if (firstTry == null || firstTry.isEmpty) {
                        db.collection(collection).whereEqualTo("codigo_personal", userName).get().await()
                    } else {
                        firstTry
                    }
                } else {
                    db.collection(collection).whereEqualTo("codigoElemento", userName).get().await()
                }

                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    fotoBase64 = doc.getString("fotoBase64") ?: ""
                    firmaBase64 = doc.getString("firmaBase64") ?: ""
                }
            } catch (e: Exception) {
                // Silently fail or handle error
            } finally {
                isLoadingData = false
            }
        } else {
            isLoadingData = false
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val encoded = encodeImageToBase64(bitmap)
                    
                    val collection = when (userRole) {
                        UserRole.PILOTO -> "piloto"
                        UserRole.PARAMEDICO -> "paramedico"
                        UserRole.PERSONAL -> "personal"
                        else -> ""
                    }
                    
                    if (collection.isNotEmpty()) {
                        val db = FirebaseFirestore.getInstance()
                        val snapshot = db.collection(collection)
                            .whereEqualTo("codigoElemento", userName)
                            .get()
                            .await()
                        
                        if (!snapshot.isEmpty) {
                            val docId = snapshot.documents.first().id
                            db.collection(collection).document(docId)
                                .update("fotoBase64", encoded)
                                .await()
                            fotoBase64 = encoded
                            Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al subir foto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showSignatureDialog) {
        SignatureDialog(
            onDismiss = { showSignatureDialog = false },
            onConfirm = { base64 ->
                scope.launch {
                    try {
                        val collection = when (userRole) {
                            UserRole.PILOTO -> "piloto"
                            UserRole.PARAMEDICO -> "paramedico"
                            UserRole.PERSONAL, UserRole.ADMIN -> "personal"
                            else -> ""
                        }
                        if (collection.isNotEmpty()) {
                            val db = FirebaseFirestore.getInstance()
                            val snapshot = if (userRole == UserRole.ADMIN || userRole == UserRole.PERSONAL) {
                                val codeInt = userName.toIntOrNull()
                                val firstTry = if (codeInt != null) {
                                    db.collection(collection).whereEqualTo("codigo_personal", codeInt).get().await()
                                } else null
                                
                                if (firstTry == null || firstTry.isEmpty) {
                                    db.collection(collection).whereEqualTo("codigo_personal", userName).get().await()
                                } else {
                                    firstTry
                                }
                            } else {
                                db.collection(collection).whereEqualTo("codigoElemento", userName).get().await()
                            }

                            if (!snapshot.isEmpty) {
                                val docId = snapshot.documents.first().id
                                db.collection(collection).document(docId)
                                    .update("firmaBase64", base64)
                                    .await()
                                firmaBase64 = base64
                                showSignatureDialog = false
                                Toast.makeText(context, "Firma actualizada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al guardar firma: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            userName = userName,
            userRole = userRole,
            onDismiss = { showPasswordDialog = false },
            onSuccess = {
                showPasswordDialog = false
                Toast.makeText(context, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderApp(title = "PERFIL DE USUARIO", onAction = onLogout)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .border(4.dp, RojoBomberos, CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingData) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp), color = RojoBomberos)
                } else if (fotoBase64.isNotEmpty()) {
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
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(90.dp),
                        tint = Color.Gray
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(30.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp).size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Text(
                text = userRole.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RojoBomberos
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExpandableSection(
                title = "INFORMACIÓN PERSONAL",
                isCompleted = true
            ) {
                ProfileInfoItem(label = "Cargo", value = when(userRole) {
                    UserRole.ADMIN -> "Administrador / Oficial"
                    UserRole.PILOTO -> "Piloto de Unidad"
                    UserRole.PARAMEDICO -> "Paramédico / Técnico"
                    UserRole.PERSONAL -> "Personal Voluntario"
                    else -> "N/A"
                })
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                ProfileInfoItem(label = "Estado", value = "Activo")
                
                if (userRole != UserRole.ADMIN) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showPasswordDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoBomberos),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RojoBomberos),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("CAMBIAR CONTRASEÑA", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExpandableSection(
                title = "FIRMA DIGITAL",
                isCompleted = firmaBase64.isNotEmpty()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { showSignatureDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (firmaBase64.isNotEmpty()) {
                        val bitmap = decodeBase64ToBitmap(firmaBase64)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Signature",
                                modifier = Modifier.fillMaxSize().padding(10.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Gesture, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Text("Toque para registrar firma", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
                
                if (firmaBase64.isNotEmpty()) {
                    TextButton(
                        onClick = { showSignatureDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CAMBIAR FIRMA", color = RojoBomberos, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("CERRAR SESIÓN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChangeCodeDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    var newCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Código Administrativo", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Código Actual: $currentCode", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newCode,
                    onValueChange = { if (it.all { c -> c.isDigit() }) newCode = it },
                    label = { Text("Nuevo Código") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true
                )
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newCode.isEmpty()) {
                        errorMsg = "El código no puede estar vacío"
                        return@Button
                    }
                    if (newCode == currentCode) {
                        errorMsg = "El nuevo código debe ser diferente"
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        try {
                            val db = FirebaseFirestore.getInstance()
                            
                            // 1. Comprobación de que el usuario no se repita
                            val newCodeInt = newCode.toIntOrNull()
                            val checkPersonal = if (newCodeInt != null) {
                                db.collection("personal").whereEqualTo("codigo_personal", newCodeInt).get().await()
                            } else {
                                db.collection("personal").whereEqualTo("codigo_personal", newCode).get().await()
                            }
                            
                            val checkPiloto = db.collection("piloto").whereEqualTo("codigoElemento", newCode).get().await()
                            val checkParamedico = db.collection("paramedico").whereEqualTo("codigoElemento", newCode).get().await()

                            if (!checkPersonal.isEmpty || !checkPiloto.isEmpty || !checkParamedico.isEmpty) {
                                errorMsg = "El código ya está en uso por otro elemento"
                                isLoading = false
                                return@launch
                            }

                            // 2. Buscar documento actual de admin para actualizar
                            val currentCodeInt = currentCode.toIntOrNull()
                            var snapshot = if (currentCodeInt != null) {
                                db.collection("personal").whereEqualTo("codigo_personal", currentCodeInt).get().await()
                            } else {
                                db.collection("personal").whereEqualTo("codigo_personal", currentCode).get().await()
                            }
                            
                            // Si no se encuentra como Int, intentar como String (o viceversa)
                            if (snapshot.isEmpty && currentCodeInt != null) {
                                snapshot = db.collection("personal").whereEqualTo("codigo_personal", currentCode).get().await()
                            }

                            if (!snapshot.isEmpty) {
                                val docId = snapshot.documents.first().id
                                // Intentar actualizar respetando el tipo del nuevo código (preferir Int si es posible)
                                val finalNewCode = newCodeInt ?: newCode
                                db.collection("personal").document(docId)
                                    .update("codigo_personal", finalNewCode)
                                    .await()
                                onSuccess(newCode)
                            } else {
                                errorMsg = "No se encontró el registro administrativo actual"
                            }
                        } catch (e: Exception) {
                            errorMsg = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("ACTUALIZAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    userName: String,
    userRole: UserRole,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true
                )
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isEmpty()) {
                        errorMsg = "La contraseña no puede estar vacía"
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        errorMsg = "Las contraseñas no coinciden"
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        try {
                            val collection = when (userRole) {
                                UserRole.PILOTO -> "piloto"
                                UserRole.PARAMEDICO -> "paramedico"
                                UserRole.PERSONAL, UserRole.ADMIN -> "personal"
                                else -> ""
                            }
                            if (collection.isNotEmpty()) {
                                val db = FirebaseFirestore.getInstance()
                                
                                val snapshot = if (userRole == UserRole.ADMIN || userRole == UserRole.PERSONAL) {
                                    val codeInt = userName.toIntOrNull()
                                    val firstTry = if (codeInt != null) {
                                        db.collection(collection).whereEqualTo("codigo_personal", codeInt).get().await()
                                    } else null
                                    
                                    if (firstTry == null || firstTry.isEmpty) {
                                        db.collection(collection).whereEqualTo("codigo_personal", userName).get().await()
                                    } else {
                                        firstTry
                                    }
                                } else {
                                    db.collection(collection).whereEqualTo("codigoElemento", userName).get().await()
                                }
                                
                                if (!snapshot.isEmpty) {
                                    val docId = snapshot.documents.first().id
                                    val passwordField = if (userRole == UserRole.ADMIN || userRole == UserRole.PERSONAL) "numero_identificacion" else "contrasena"
                                    
                                    db.collection(collection).document(docId)
                                        .update(passwordField, newPassword)
                                        .await()
                                    onSuccess()
                                } else {
                                    errorMsg = "No se encontró el usuario"
                                }
                            }
                        } catch (e: Exception) {
                            errorMsg = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}

@Composable
fun CircularProgressIndicator(modifier: Modifier = Modifier, color: Color) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = 2.dp
    )
}
