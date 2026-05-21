package com.example.bomberosapp.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.model.PacienteData
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos
import com.example.bomberosapp.utils.PdfHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UltimosControlesScreen(
    viewModel: AdminViewModel,
    title: String = "ÚLTIMOS CONTROLES",
    onVolverClick: () -> Unit
) {
    val emergencies = viewModel.emergencies
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.startObserving()
    }

    Column(Modifier.fillMaxSize().background(Blanco).statusBarsPadding().navigationBarsPadding()) {
        HeaderApp(title = title, icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onVolverClick)

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoBomberos)
            }
        } else if (emergencies.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay registros de emergencias", fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                items(emergencies) { emergency ->
                    EmergencyCard(emergency)
                }
            }
        }
    }
}

@Composable
fun EmergencyCard(emergency: Emergency) {
    var expanded by remember { mutableStateOf(false) }
    var patients by remember { mutableStateOf<List<PacienteData>>(emptyList()) }
    var loadingPatients by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateText = sdf.format(Date(emergency.timestamp))

    LaunchedEffect(expanded) {
        if (expanded && patients.isEmpty()) {
            loadingPatients = true
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("paciente")
                    .whereEqualTo("numeroEmergenciaRelacionado", emergency.id)
                    .get()
                    .await()
                patients = snapshot.documents.mapNotNull { it.toObject(PacienteData::class.java) }
            } catch (e: Exception) {
                // Error silent
            } finally {
                loadingPatients = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, RojoBomberos, RoundedCornerShape(15.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "UNIDAD: ${emergency.unidad}",
                        fontWeight = FontWeight.Black,
                        color = RojoBomberos,
                        fontSize = 18.sp
                    )
                    Text(
                        text = emergency.tipoServicio,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = RojoBomberos,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = dateText, fontSize = 12.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(Icons.Default.Person, null, tint = RojoBomberos, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = emergency.nombrePaciente, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = RojoBomberos.copy(alpha = 0.3f), thickness = 2.dp)
                    Spacer(Modifier.height(12.dp))
                    
                    DetailSection("1. DATOS DE SALIDA", Icons.AutoMirrored.Filled.ExitToApp) {
                        DetailItem("Hora Salida", emergency.horaSalida)
                        DetailItem("Solicitante", "${emergency.nombreSolicitante} ${emergency.apellidoSolicitante}")
                        DetailItem("Teléfono", emergency.telefonoSolicitante)
                    }

                    DetailSection("2. UBICACIÓN", Icons.Default.LocationOn) {
                        DetailItem("Dirección", emergency.direccionEmergencia)
                        DetailItem("Observaciones", emergency.observaciones)
                    }

                    DetailSection("3. DATOS DEL PACIENTE", Icons.Default.MedicalServices) {
                        if (loadingPatients) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = RojoBomberos)
                        } else if (patients.isEmpty()) {
                            Text("No se encontraron detalles de pacientes", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            patients.forEachIndexed { index, p ->
                                Text("PACIENTE ${index + 1}", fontWeight = FontWeight.Bold, color = RojoBomberos, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                DetailItem("Nombre", "${p.nombre} ${p.apellidos}")
                                DetailItem("Edad", p.edad)
                                DetailItem("Sexo", p.sexo)
                                DetailItem("DPI", p.dpi)
                                DetailItem("Estado", p.estado)
                                Text("Signos Vitales:", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        DetailItem("P.A.", p.presionArterial)
                                        DetailItem("F.C.", p.frecuenciaCardiaca)
                                        DetailItem("F.R.", p.frecuenciaRespiratoria)
                                    }
                                    Column(Modifier.weight(1f)) {
                                        DetailItem("SatO2", p.saturacionOxigeno)
                                        DetailItem("Temp", p.temperatura)
                                        DetailItem("Glucosa", p.glucosa)
                                    }
                                }
                                if (p.esFallecido) Text("ESTADO: FALLECIDO", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                if (index < patients.size - 1) HorizontalDivider(Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                            }
                        }
                    }

                    DetailSection("4. ACOMPAÑANTE", Icons.Default.People) {
                        if (emergency.tieneAcompanante) {
                            DetailItem("Nombre", "${emergency.nombreAcompanante} ${emergency.apellidoAcompanante}")
                            DetailItem("Teléfono", emergency.telefonoAcompanante)
                        } else {
                            Text("Sin acompañante", fontSize = 13.sp, color = Color.Gray)
                        }
                    }

                    DetailSection("5. TRASLADO", Icons.Default.LocalHospital) {
                        if (emergency.tieneTraslado) {
                            DetailItem("Hospital", emergency.hospitalTraslado)
                            DetailItem("Destino", emergency.trasladoA)
                            DetailItem("Hora Llegada", emergency.horaLlegadaTraslado)
                        } else {
                            Text("No requirió traslado", fontSize = 13.sp, color = Color.Gray)
                        }
                    }

                    DetailSection("6. PERSONAL", Icons.Default.AssignmentInd) {
                        DetailItem("Piloto", emergency.piloto)
                        DetailItem("Personal Destacado", emergency.personalDestacado)
                    }

                    DetailSection("7. CONTROL Y FIRMAS", Icons.Default.Verified) {
                        DetailItem("Llegada Incidente", emergency.horaLlegada)
                        DetailItem("Formulado Por", emergency.reporteFormuladoPor)
                        DetailItem("Vo.Bo. Jefe", emergency.voBoJefeServicio)
                    }

                    Spacer(Modifier.height(16.dp))
                    
                    // BOTÓN DE EXPORTAR PDF
                    Button(
                        onClick = { PdfHelper.generarReportePdf(context, emergency, patients) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RojoBomberos),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, null)
                        Spacer(Modifier.width(8.dp))
                        Text("EXPORTAR REPORTE A PDF", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = RojoBomberos, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = RojoBomberos)
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(Modifier.padding(vertical = 1.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
        Text(text = if (value.isBlank()) "N/A" else value, fontSize = 12.sp, color = Color.DarkGray)
    }
}
