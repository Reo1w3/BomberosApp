package com.example.bomberosapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.model.PacienteData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfHelper {

    fun generarReportePdf(context: Context, emergency: Emergency, patients: List<PacienteData>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamaño A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint()

        var y = 40f

        // Encabezado
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 14f
        titlePaint.color = Color.RED
        canvas.drawText("BENEMÉRITO CUERPO DE BOMBEROS VOLUNTARIOS DE GUATEMALA", 50f, y, titlePaint)
        
        y += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = Color.BLACK
        canvas.drawText("REPORTE DE SERVICIO DE AMBULANCIA", 50f, y, paint)
        
        y += 20f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f
        canvas.drawText("ID Reporte: ${emergency.id}", 50f, y, paint)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Fecha de Generación: ${sdf.format(Date())}", 350f, y, paint)

        y += 30f
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 20f

        // SECCIÓN 1
        drawSectionTitle(canvas, "1. DATOS DE SALIDA", y)
        y += 15f
        drawText(canvas, "Unidad: ${emergency.unidad}", 60f, y)
        drawText(canvas, "Hora Salida: ${emergency.horaSalida}", 300f, y)
        y += 15f
        drawText(canvas, "Servicio: ${emergency.tipoServicio}", 60f, y)
        y += 15f
        drawText(canvas, "Solicitante: ${emergency.nombreSolicitante} ${emergency.apellidoSolicitante}", 60f, y)
        drawText(canvas, "Teléfono: ${emergency.telefonoSolicitante}", 300f, y)

        // SECCIÓN 2
        y += 25f
        drawSectionTitle(canvas, "2. UBICACIÓN Y DIRECCIÓN", y)
        y += 15f
        drawText(canvas, "Dirección: ${emergency.direccionEmergencia}", 60f, y)
        y += 15f
        drawText(canvas, "Observaciones: ${emergency.observaciones}", 60f, y)

        // SECCIÓN 3: PACIENTES
        y += 25f
        drawSectionTitle(canvas, "3. DATOS DEL PACIENTE", y)
        patients.forEachIndexed { index, p ->
            y += 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("PACIENTE ${index + 1}: ${p.nombre} ${p.apellidos}", 60f, y, paint)
            y += 12f
            paint.typeface = Typeface.DEFAULT
            drawText(canvas, "Edad: ${p.edad}  |  Sexo: ${p.sexo}  |  DPI: ${p.dpi}", 70f, y)
            y += 12f
            drawText(canvas, "Estado: ${p.estado}", 70f, y)
            y += 12f
            drawText(canvas, "PA: ${p.presionArterial} | FC: ${p.frecuenciaCardiaca} | FR: ${p.frecuenciaRespiratoria}", 70f, y)
            y += 12f
            drawText(canvas, "O2: ${p.saturacionOxigeno} | Temp: ${p.temperatura} | Glu: ${p.glucosa}", 70f, y)
            if (p.esFallecido) {
                y += 12f
                val fatalPaint = Paint().apply { color = Color.RED; textSize = 10f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
                canvas.drawText("ESTADO: FALLECIDO", 70f, y, fatalPaint)
            }
            y += 10f
        }

        // SECCIÓN 4 & 5
        y += 15f
        drawSectionTitle(canvas, "4. ACOMPAÑANTE y 5. TRASLADO", y)
        y += 15f
        if (emergency.tieneAcompanante) drawText(canvas, "Acompañante: ${emergency.nombreAcompanante} (${emergency.telefonoAcompanante})", 60f, y)
        else drawText(canvas, "Acompañante: NO", 60f, y)
        y += 15f
        if (emergency.tieneTraslado) {
            drawText(canvas, "Traslado a: ${emergency.hospitalTraslado}", 60f, y)
            y += 12f
            drawText(canvas, "Destino: ${emergency.trasladoA} | Hora Llegada: ${emergency.horaLlegadaTraslado}", 60f, y)
        } else {
            drawText(canvas, "Traslado: NO REQUERIDO", 60f, y)
        }

        // SECCIÓN 6 & 7
        y += 25f
        drawSectionTitle(canvas, "6. PERSONAL y 7. CONTROL", y)
        y += 15f
        drawText(canvas, "Piloto: ${emergency.piloto}", 60f, y)
        y += 15f
        drawText(canvas, "Paramédicos: ${emergency.personalDestacado}", 60f, y)
        y += 15f
        drawText(canvas, "Reporte por: ${emergency.reporteFormuladoPor}", 60f, y)
        drawText(canvas, "Hora Llegada Incidente: ${emergency.horaLlegada}", 300f, y)
        y += 15f
        drawText(canvas, "Visto Bueno Jefe: ${emergency.voBoJefeServicio}", 60f, y)

        // FIRMAS DIGITALES
        y += 40f
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 20f
        drawSectionTitle(canvas, "FIRMAS DIGITALES", y)
        
        y += 20f
        drawSignature(canvas, emergency.firmaPiloto, 60f, y, "FIRMA PILOTO")
        drawSignature(canvas, emergency.firmaJefeServicio, 300f, y, "FIRMA JEFE DE SERVICIO")

        // Agregar firmas de paramédicos si existen
        if (emergency.firmaPersonalDestacado.isNotBlank()) {
            val signatures = emergency.firmaPersonalDestacado.split("|")
            val names = emergency.personalDestacado.split(", ")
            signatures.forEachIndexed { index, sig ->
                if (sig.isNotBlank()) {
                    y += 90f
                    val name = names.getOrNull(index) ?: "PARAMÉDICO ${index + 1}"
                    drawSignature(canvas, sig, 60f, y, "FIRMA $name")
                }
            }
        }

        pdfDocument.finishPage(page)

        // Guardar y Compartir
        val file = File(context.cacheDir, "Reporte_Bomberos_${emergency.id}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            compartirPdf(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawSectionTitle(canvas: Canvas, title: String, y: Float) {
        val p = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 11f
            color = Color.DKGRAY
        }
        canvas.drawText(title, 50f, y, p)
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float) {
        val p = Paint().apply {
            textSize = 10f
            color = Color.BLACK
        }
        canvas.drawText(text, x, y, p)
    }

    private fun drawSignature(canvas: Canvas, base64: String?, x: Float, y: Float, label: String) {
        if (base64.isNullOrBlank()) {
            val p = Paint().apply { textSize = 8f; color = Color.GRAY }
            canvas.drawText("$label: (No firmó)", x, y, p)
            return
        }
        try {
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            if (bitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 60, true)
                canvas.drawBitmap(scaledBitmap, x, y, null)
                val p = Paint().apply { textSize = 8f; color = Color.BLACK; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
                canvas.drawText(label, x, y + 75, p)
            }
        } catch (e: Exception) {
            // Error decodificando
        }
    }

    private fun compartirPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "com.example.bomberosapp.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir Reporte PDF"))
    }
}
