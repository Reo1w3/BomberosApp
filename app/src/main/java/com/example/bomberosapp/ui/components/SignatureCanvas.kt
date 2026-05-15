package com.example.bomberosapp.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.ByteArrayOutputStream

@Composable
fun SignatureDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var path by remember { mutableStateOf(Path()) }
    val paths = remember { mutableStateListOf<Path>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dibuje su firma", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    path = Path().apply { moveTo(offset.x, offset.y) }
                                    paths.add(path)
                                },
                                onDrag = { change, dragAmount ->
                                    path.lineTo(change.position.x, change.position.y)
                                    // Hack to force recomposition
                                    val lastPath = paths.last()
                                    paths.removeAt(paths.size - 1)
                                    paths.add(lastPath)
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        paths.forEach { p ->
                            drawPath(
                                path = p,
                                color = Color.Black,
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    TextButton(onClick = { paths.clear() }) {
                        Text("Limpiar")
                    }
                    Button(onClick = {
                        val bitmap = pathsToBitmap(paths, 800, 400)
                        val base64 = bitmapToBase64(bitmap)
                        onConfirm(base64)
                    }) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

fun pathsToBitmap(paths: List<Path>, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    paths.forEach { path ->
        canvas.drawPath(path.asAndroidPath(), paint)
    }
    return bitmap
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
