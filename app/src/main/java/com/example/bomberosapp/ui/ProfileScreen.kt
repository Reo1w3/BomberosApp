package com.example.bomberosapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.R
import com.example.bomberosapp.data.model.UserRole
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.HeaderApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.widget.Toast
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userName: String,
    userRole: UserRole,
    onLogout: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(3.dp, RojoBomberos, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Text(
                text = userRole.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = RojoBomberos
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileInfoItem(label = "Cargo", value = when(userRole) {
                        UserRole.ADMIN -> "Administrador / Oficial"
                        UserRole.PILOTO -> "Piloto de Unidad"
                        UserRole.PARAMEDICO -> "Paramédico / Técnico"
                        UserRole.PERSONAL -> "Personal Voluntario"
                        else -> "N/A"
                    })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    ProfileInfoItem(label = "Estado", value = "Activo")
                    
                    if (userRole != UserRole.ADMIN) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoBomberos),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RojoBomberos),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("CAMBIAR CONTRASEÑA")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("CERRAR SESIÓN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
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
                                        .update("contrasena", newPassword)
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
                if (isLoading) CircularProgressIndicator(size = 20.dp, color = Color.White)
                else Text("GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}

@Composable
fun CircularProgressIndicator(size: androidx.compose.ui.unit.Dp, color: Color) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color,
        strokeWidth = 2.dp
    )
}
