package com.app.fityo.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.app.fityo.R
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.utils.ExportMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object PdfExporter {

    suspend fun exportScheda(
        context: Context,
        scheda: SchedeEntity,
        esercizi: List<EsserciziEntity>,
        meta: ExportMetadata? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val appName = context.getString(R.string.app_name)
            val pageWidth = 595
            val pageHeight = 842
            val pdf = PdfDocument()

            // Definizione colori professionali
            val primaryColor = Color.parseColor("#455A64")
            val accentColor = Color.parseColor("#4DD0E1")
            val textDark = Color.parseColor("#212121")
            val textLight = Color.parseColor("#757575")

            // Paint per diversi stili
            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = primaryColor
            }
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = textDark
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = textLight
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = textDark
            }
            val bodyBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = textDark
            }
            val linePaint = Paint().apply {
                color = accentColor
                strokeWidth = 2f
            }
            val boxPaint = Paint().apply {
                color = Color.parseColor("#F5F5F5")
                style = Paint.Style.FILL
            }

            var pageNumber = 1
            var currentPage = pdf.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            var cursorY = 120f
            val margin = 48f
            val contentWidth = pageWidth - (margin * 2)

            fun drawHeader() {
                // Header background
                currentPage.canvas.drawRect(
                    0f, 0f, pageWidth.toFloat(), 60f,
                    Paint().apply { color = primaryColor }
                )
                // App name
                currentPage.canvas.drawText(
                    appName,
                    margin,
                    42f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 24f
                        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                        color = Color.WHITE
                    }
                )
                // Accent line
                currentPage.canvas.drawLine(
                    margin, 65f,
                    pageWidth - margin, 65f,
                    Paint().apply {
                        color = accentColor
                        strokeWidth = 3f
                    }
                )
            }

            fun drawFooter() {
                val footerY = pageHeight - 30f
                currentPage.canvas.drawText(
                    context.getString(R.string.pdf_page_number, pageNumber),
                    pageWidth / 2f - 30f,
                    footerY,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 10f
                        color = textLight
                    }
                )
            }

            fun finishPage() {
                drawFooter()
                pdf.finishPage(currentPage)
            }

            fun newPage() {
                finishPage()
                pageNumber++
                currentPage = pdf.startPage(
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                )
                drawHeader()
                cursorY = 120f
            }

            fun writeLine(
                text: String,
                paint: Paint = bodyPaint,
                spacing: Float = 20f,
                startX: Float = margin
            ) {
                if (cursorY > pageHeight - 100) {
                    newPage()
                }
                currentPage.canvas.drawText(text, startX, cursorY, paint)
                cursorY += spacing
            }

            fun drawHorizontalLine() {
                currentPage.canvas.drawLine(
                    margin, cursorY,
                    pageWidth - margin, cursorY,
                    linePaint
                )
                cursorY += 12f
            }

            fun writeSection(title: String) {
                cursorY += 8f
                writeLine(title, titlePaint, 30f)
                drawHorizontalLine()
            }

            fun writeInfoRow(label: String, value: String?) {
                if (cursorY > pageHeight - 100) newPage()
                val displayValue = value?.takeIf { it.isNotBlank() } ?: "-"
                currentPage.canvas.drawText(label, margin, cursorY, bodyBoldPaint)
                currentPage.canvas.drawText(displayValue, margin + 150f, cursorY, bodyPaint)
                cursorY += 20f
            }

            fun drawExerciseBox(index: Int, esercizio: EsserciziEntity) {
                if (cursorY > pageHeight - 150) newPage()

                val boxTop = cursorY - 10f
                val boxHeight = 110f

                // Background box
                currentPage.canvas.drawRect(
                    margin, boxTop,
                    pageWidth - margin, boxTop + boxHeight,
                    boxPaint
                )
                // Left accent bar
                currentPage.canvas.drawRect(
                    margin, boxTop,
                    margin + 4f, boxTop + boxHeight,
                    Paint().apply { color = accentColor }
                )

                // Esercizio numero e nome
                currentPage.canvas.drawText(
                    context.getString(R.string.pdf_label_exercise_title, index + 1, esercizio.nome),
                    margin + 12f, cursorY,
                    titlePaint
                )
                cursorY += 25f

                // Dettagli
                val detailsY = cursorY
                currentPage.canvas.drawText(context.getString(R.string.pdf_label_series, esercizio.nSerie), margin + 12f, cursorY, bodyBoldPaint)
                currentPage.canvas.drawText(context.getString(R.string.pdf_label_reps, esercizio.nRipetizione), margin + 150f, cursorY, bodyBoldPaint)
                cursorY += 20f

                if (!esercizio.attrezzo.isNullOrBlank()) {
                    currentPage.canvas.drawText(context.getString(R.string.pdf_label_attrezzo, esercizio.attrezzo), margin + 12f, cursorY, bodyPaint)
                    cursorY += 18f
                }

                val extras = mutableListOf<String>()
                esercizio.peso?.let { extras.add(context.getString(R.string.pdf_label_peso, it)) }
                esercizio.intervallo?.let { extras.add(context.getString(R.string.pdf_label_recupero, it)) }
                esercizio.insometria?.let { extras.add(context.getString(R.string.pdf_label_isometria, it)) }

                if (extras.isNotEmpty()) {
                    currentPage.canvas.drawText(extras.joinToString(" - "), margin + 12f, cursorY, bodyPaint)
                    cursorY += 18f
                }

                cursorY = boxTop + boxHeight + 8f
            }

            val exportTitle = meta?.customTitle?.takeIf { it.isNotBlank() } ?: scheda.titolo
            val rawStartDate = meta?.startDate?.takeIf { it.isNotBlank() } ?: scheda.data
            val rawEndDate = meta?.endDate?.takeIf { it.isNotBlank() } ?: rawStartDate
            val displayStartDate = formatDateForPdf(rawStartDate)
            val displayEndDate = formatDateForPdf(rawEndDate)

            // Disegna header della prima pagina
            drawHeader()

            // Titolo scheda centrato
            writeLine(exportTitle, headerPaint, 40f, margin)

            // Sezione Informazioni Generali
            writeSection(context.getString(R.string.pdf_section_info))
            writeInfoRow(context.getString(R.string.pdf_label_muscle_group), scheda.getGruppiMuscolariDisplay())
            writeInfoRow(context.getString(R.string.pdf_label_intensity), scheda.intesita)
            writeInfoRow(context.getString(R.string.pdf_label_period), "${displayStartDate ?: rawStartDate} - ${displayEndDate ?: rawEndDate}")
            meta?.coachName?.takeIf { it.isNotBlank() }?.let {
                writeInfoRow(context.getString(R.string.pdf_label_coach), it)
            }
            meta?.athleteName?.takeIf { it.isNotBlank() }?.let {
                writeInfoRow(context.getString(R.string.pdf_label_athlete), it)
            }

            // Note se presenti
            if (!scheda.notes.isNullOrBlank()) {
                cursorY += 10f
                writeSection(context.getString(R.string.pdf_section_notes))
                scheda.notes.lines().forEach { line ->
                    writeLine(line.trim(), bodyPaint, 18f)
                }
            }

            // Sezione Esercizi
            cursorY += 20f
            writeSection(context.getString(R.string.pdf_section_exercises, esercizi.size))
            cursorY += 5f

            if (esercizi.isEmpty()) {
                writeLine(context.getString(R.string.pdf_no_exercises), subtitlePaint, 24f)
            } else {
                esercizi.forEachIndexed { index, esercizio ->
                    drawExerciseBox(index, esercizio)
                }
            }

            finishPage()

            val safeName = exportTitle.replace(Regex("[^A-Za-z0-9_-]"), "_")
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val sanitizedPeriod = rawStartDate.replace(Regex("[^0-9A-Za-z]"), "")
            val fileName = "${appName}_${safeName}_${sanitizedPeriod}_$timeStamp.pdf"

            val appDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            val pdfFile = File(appDir, fileName)
            FileOutputStream(pdfFile).use { pdf.writeTo(it) }
            copyToPublicDocuments(context, pdfFile, fileName)
            pdf.close()

            pdfFile
        }
    }

    private fun copyToPublicDocuments(context: Context, source: File, fileName: String) {
        if (!source.exists()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val appName = context.getString(com.app.fityo.R.string.app_name)
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/$appName"
                )
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    source.inputStream().use { input -> input.copyTo(output) }
                }
            }
        } else {
            val appName = context.getString(com.app.fityo.R.string.app_name)
            val docsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (docsDir != null) {
                val targetDir = File(docsDir, appName)
                if (!targetDir.exists()) targetDir.mkdirs()
                val destFile = File(targetDir, fileName)
                source.copyTo(destFile, overwrite = true)
            }
        }
    }

    private fun formatDateForPdf(value: String?): String? {
        if (value.isNullOrBlank()) return null
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val patterns = listOf("yyyy-MM-dd", "dd/MM/yyyy")
        patterns.forEach { pattern ->
            try {
                val parser = SimpleDateFormat(pattern, Locale.getDefault())
                val parsed = parser.parse(value)
                if (parsed != null) {
                    return outputFormat.format(parsed)
                }
            } catch (ignored: Exception) {
            }
        }
        return value
    }
}

