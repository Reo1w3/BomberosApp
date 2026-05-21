package com.example.bomberosapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bomberosapp.HeaderApp
import com.example.bomberosapp.ui.theme.Blanco
import com.example.bomberosapp.ui.theme.RojoBomberos

data class CompañiaApoyo(val nombre: String, val telefono: String)

@Composable
fun SolicitarApoyoScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val listaCompañias = listOf(
        CompañiaApoyo("LINEA NACIONAL DEPARTAMENTALES", "1554"),
        CompañiaApoyo("LINEA NACIONAL VOLUNTARIOS", "122"),
        CompañiaApoyo("79ª COMPAÑIA ESTANZUELA", "79335481"),
        CompañiaApoyo("75ª COMPAÑIA TECULUTAN", "79348218"),
        CompañiaApoyo("23ª COMPAÑIA ZACAPA", "79410122"),
        CompañiaApoyo("52ª COMPAÑIA GUALAN", "79419228"),
        CompañiaApoyo("48ª COMPAÑIA CABAÑAS", "79419228"),
        CompañiaApoyo("86ª COMPAÑIA HUITE", "53673527"),
        CompañiaApoyo("#38 MUNICIPALES USUMATLAN", "40300497"),
        CompañiaApoyo("#11 MUNICIPALES RIO HONDO", "79340443")
    )

    Column(Modifier.fillMaxSize().background(Blanco).statusBarsPadding().navigationBarsPadding()) {
        HeaderApp(title = "SOLICITAR APOYO", icon = Icons.AutoMirrored.Filled.ArrowBack, onAction = onVolverClick)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "DIRECTORIO DE EMERGENCIAS",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = RojoBomberos,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(listaCompañias) { compañia ->
                ApoyoCard(compañia) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${compañia.telefono}"))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ApoyoCard(compañia: CompañiaApoyo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, RojoBomberos, RoundedCornerShape(15.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = compañia.nombre,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = compañia.telefono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = RojoBomberos
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(RojoBomberos, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Call, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}
