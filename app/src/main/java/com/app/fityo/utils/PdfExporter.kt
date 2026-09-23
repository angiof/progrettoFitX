package com.app.fityo.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.app.fityo.R
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object PdfExporter {

    private data class Theme(
        val primary: Int,
        val accent: Int,
        val textDark: Int,
        val textLight: Int,
        val boxBg: Int,
        val headerHeight: Float,
        val headerTextColor: Int
    )

    private val THEME_CLASSIC = Theme(
        primary = Color.parseColor("#455A64"),
        accent = Color.parseColor("#4DD0E1"),
        textDark = Color.parseColor("#212121"),
        textLight = Color.parseColor("#757575"),
        boxBg = Color.parseColor("#F5F5F5"),
        headerHeight = 60f,
        headerTextColor = Color.WHITE
    )

    private val THEME_MODERN = Theme(
        primary = Color.parseColor("#0F172A"),
        accent = Color.parseColor("#38BDF8"),
        textDark = Color.parseColor("#0F172A"),
        textLight = Color.parseColor("#64748B"),
        boxBg = Color.parseColor("#F1F5F9"),
        headerHeight = 90f,
        headerTextColor = Color.WHITE
    )

    suspend fun exportScheda(
        context: Context,
        scheda: SchedeEntity,
        esercizi: List<EsserciziEntity>,
        meta: ExportMetadata? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val isModern = meta?.pdfFormat == "MODERN"
            val theme = if (isModern) THEME_MODERN else THEME_CLASSIC
            val appName = context.getString(R.string.app_name)
            val logoBitmap = LogoStore.bitmap(context)
            val pageWidth = 595
            val pageHeight = 842
            val pdf = PdfDocument()

            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = if (isModern) 34f else 28f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = theme.primary
                textAlign = if (isModern) Paint.Align.CENTER else Paint.Align.LEFT
            }
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = theme.textDark
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = theme.textLight
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = theme.textDark
            }
            val bodyBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = theme.textDark
            }
            val linePaint = Paint().apply {
                color = theme.accent
                strokeWidth = 2f
            }
            val boxPaint = Paint().apply {
                color = theme.boxBg
                style = Paint.Style.FILL
            }

            var pageNumber = 1
            var currentPage = pdf.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            var cursorY = if (isModern) theme.headerHeight + 70f else 120f
            val margin = 48f

            val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = theme.accent
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                letterSpacing = 0.2f
            }
            val subBrandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 20f
                color = theme.headerTextColor
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            }
            val classicBrandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = theme.headerTextColor
            }
            val brandText = appName.uppercase(Locale.getDefault())
            val subBrandText = "Scheda di allenamento"
            val headerTextWidth = if (isModern) {
                maxOf(brandPaint.measureText(brandText), subBrandPaint.measureText(subBrandText))
            } else {
                classicBrandPaint.measureText(appName)
            }
            val headerTextX = margin

            fun drawHeader() {
                currentPage.canvas.drawRect(
                    0f, 0f, pageWidth.toFloat(), theme.headerHeight,
                    Paint().apply { color = theme.primary }
                )
                if (isModern) {
                    currentPage.canvas.drawText(brandText, headerTextX, 34f, brandPaint)
                    currentPage.canvas.drawText(subBrandText, headerTextX, 62f, subBrandPaint)
                } else {
                    currentPage.canvas.drawText(appName, headerTextX, 42f, classicBrandPaint)
                    currentPage.canvas.drawLine(
                        margin, 65f, pageWidth - margin, 65f,
                        Paint().apply {
                            color = theme.accent
                            strokeWidth = 3f
                        }
                    )
                }
            }

            fun drawFooter() {
                val footerY = pageHeight - 30f
                currentPage.canvas.drawText(
                    context.getString(R.string.pdf_page_number, pageNumber),
                    pageWidth / 2f - 30f, footerY,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 10f
                        color = theme.textLight
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
                cursorY = if (isModern) theme.headerHeight + 40f else 120f
            }

            fun writeLine(
                text: String,
                paint: Paint = bodyPaint,
                spacing: Float = 20f,
                startX: Float = margin
            ) {
                if (cursorY > pageHeight - 100) newPage()
                currentPage.canvas.drawText(text, startX, cursorY, paint)
                cursorY += spacing
            }

            fun drawHorizontalLine() {
                currentPage.canvas.drawLine(
                    margin, cursorY, pageWidth - margin, cursorY, linePaint
                )
                cursorY += 12f
            }

            fun writeSection(title: String) {
                cursorY += 8f
                if (isModern) {
                    // Modern section: chip a sinistra + testo
                    val chipPaint = Paint().apply { color = theme.accent }
                    currentPage.canvas.drawRect(
                        margin - 4f, cursorY - 12f,
                        margin, cursorY + 4f,
                        chipPaint
                    )
                    writeLine(title.uppercase(Locale.getDefault()), Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 13f
                        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                        color = theme.primary
                        letterSpacing = 0.15f
                    }, 24f, margin + 8f)
                } else {
                    writeLine(title, titlePaint, 30f)
                    drawHorizontalLine()
                }
            }

            fun writeInfoRow(label: String, value: String?) {
                if (cursorY > pageHeight - 100) newPage()
                val displayValue = value?.takeIf { it.isNotBlank() } ?: "-"
                currentPage.canvas.drawText(label, margin, cursorY, bodyBoldPaint)
                currentPage.canvas.drawText(displayValue, margin + 150f, cursorY, bodyPaint)
                cursorY += 20f
            }

            fun drawExerciseClassic(index: Int, esercizio: EsserciziEntity) {
                // Le righe sotto il titolo sono opzionali: il riquadro si adatta invece di
                // lasciare spazio vuoto o, con le note, tagliare l'ultima riga.
                val extras = mutableListOf<String>()
                esercizio.peso?.let { extras.add(context.getString(R.string.pdf_label_peso, formatPeso(it))) }
                esercizio.intervallo?.let { extras.add(context.getString(R.string.pdf_label_recupero, it)) }
                esercizio.insometria?.let { extras.add(context.getString(R.string.pdf_label_isometria, it)) }

                val optionalLines = mutableListOf<String>()
                if (!esercizio.attrezzo.isNullOrBlank()) {
                    optionalLines.add(context.getString(R.string.pdf_label_attrezzo, esercizio.attrezzo))
                }
                if (extras.isNotEmpty()) {
                    optionalLines.add(extras.joinToString(" - "))
                }
                val avanzate = mutableListOf<String>()
                esercizio.rpe?.takeIf { it.isNotBlank() }?.let { avanzate.add("RPE $it") }
                esercizio.tempo?.takeIf { it.isNotBlank() }?.let { avanzate.add("Tempo $it") }
                esercizio.percentuale?.let { avanzate.add("${formatPeso(it)}% 1RM") }
                if (avanzate.isNotEmpty()) {
                    optionalLines.add(avanzate.joinToString(" - "))
                }

                esercizio.notes?.takeIf { it.isNotBlank() }?.let {
                    optionalLines.add(context.getString(R.string.pdf_label_note, it.replace("\n", " ")))
                }

                val boxHeight = 55f + optionalLines.size * 18f
                if (cursorY > pageHeight - (boxHeight + 40f)) newPage()

                val boxTop = cursorY - 10f

                currentPage.canvas.drawRect(
                    margin, boxTop, pageWidth - margin, boxTop + boxHeight, boxPaint
                )
                currentPage.canvas.drawRect(
                    margin, boxTop, margin + 4f, boxTop + boxHeight,
                    Paint().apply { color = theme.accent }
                )

                // Barra laterale arancione per gli esercizi legati in superset.
                if (esercizio.supersetGroup != null) {
                    currentPage.canvas.drawRect(
                        margin, boxTop,
                        margin + 4f, boxTop + boxHeight,
                        Paint().apply { color = Color.parseColor("#FB8C00") }
                    )
                }

                // Esercizio numero e nome
                currentPage.canvas.drawText(
                    context.getString(R.string.pdf_label_exercise_title, index + 1, esercizio.nome),
                    margin + 12f, cursorY, titlePaint
                )
                cursorY += 25f

                // Dettagli
                currentPage.canvas.drawText(context.getString(R.string.pdf_label_series, esercizio.nSerie), margin + 12f, cursorY, bodyBoldPaint)
                currentPage.canvas.drawText(context.getString(R.string.pdf_label_reps, esercizio.nRipetizione), margin + 150f, cursorY, bodyBoldPaint)
                cursorY += 20f

                optionalLines.forEach { line ->
                    currentPage.canvas.drawText(line, margin + 12f, cursorY, bodyPaint)
                    cursorY += 18f
                }
                cursorY = boxTop + boxHeight + 8f
            }

            fun drawExerciseModern(index: Int, esercizio: EsserciziEntity) {
                if (cursorY > pageHeight - 130) newPage()
                val rowTop = cursorY - 12f
                val rowHeight = 78f

                // Card ombreggiata leggera
                currentPage.canvas.drawRect(
                    margin, rowTop, pageWidth - margin, rowTop + rowHeight,
                    Paint().apply { color = Color.WHITE }
                )
                currentPage.canvas.drawRect(
                    margin, rowTop, pageWidth - margin, rowTop + 2f,
                    Paint().apply { color = theme.accent }
                )

                // Numero esercizio in badge circolare
                val badgeCX = margin + 22f
                val badgeCY = rowTop + 26f
                currentPage.canvas.drawCircle(
                    badgeCX, badgeCY, 14f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.primary }
                )
                currentPage.canvas.drawText(
                    (index + 1).toString(),
                    badgeCX - 4f, badgeCY + 4f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 12f
                        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                        color = Color.WHITE
                    }
                )

                // Nome esercizio
                currentPage.canvas.drawText(
                    esercizio.nome,
                    margin + 46f, rowTop + 22f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 14f
                        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                        color = theme.textDark
                    }
                )

                // Serie x Reps in evidenza a destra
                val srText = "${esercizio.nSerie} × ${esercizio.nRipetizione}"
                val srPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 16f
                    typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                    color = theme.accent
                    textAlign = Paint.Align.RIGHT
                }
                currentPage.canvas.drawText(srText, pageWidth - margin - 8f, rowTop + 22f, srPaint)

                // Riga metadata: attrezzo, peso, recupero, isometria
                val meta = mutableListOf<String>()
                if (!esercizio.attrezzo.isNullOrBlank()) meta.add(esercizio.attrezzo)
                esercizio.peso?.let { meta.add("${it}kg") }
                esercizio.intervallo?.let { meta.add("Rec ${it}s") }
                esercizio.insometria?.let { meta.add("Iso ${it}s") }
                if (meta.isNotEmpty()) {
                    currentPage.canvas.drawText(
                        meta.joinToString(" • "),
                        margin + 46f, rowTop + 44f,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 11f
                            color = theme.textLight
                        }
                    )
                }

                // Linea di separazione sotto
                currentPage.canvas.drawLine(
                    margin, rowTop + rowHeight,
                    pageWidth - margin, rowTop + rowHeight,
                    Paint().apply {
                        color = theme.boxBg
                        strokeWidth = 1f
                    }
                )
                cursorY = rowTop + rowHeight + 12f
            }

            val exportTitle = meta?.customTitle?.takeIf { it.isNotBlank() } ?: scheda.titolo
            val rawStartDate = meta?.startDate?.takeIf { it.isNotBlank() } ?: scheda.data
            val rawEndDate = meta?.endDate?.takeIf { it.isNotBlank() } ?: rawStartDate
            val displayStartDate = formatDateForPdf(rawStartDate)
            val displayEndDate = formatDateForPdf(rawEndDate)

            drawHeader()

            // Logo dell'utente, nel punto scelto trascinandolo sull'anteprima. Solo sulla prima
            // pagina: e un'intestazione, non una filigrana. Il bitmap si libera a documento
            // chiuso, perche il canvas della pagina disegna davvero solo a finishPage().
            logoBitmap?.let { logo ->
                val placement = LogoStore.placement(context)
                val logoWidth = pageWidth * placement.widthFraction
                val logoHeight = logoWidth * logo.height / logo.width
                val left = pageWidth * placement.xFraction
                val top = pageHeight * placement.yFraction
                currentPage.canvas.drawBitmap(
                    logo,
                    null,
                    RectF(left, top, left + logoWidth, top + logoHeight),
                    Paint(Paint.FILTER_BITMAP_FLAG)
                )
            }

            // Titolo scheda
            if (isModern) {
                currentPage.canvas.drawText(
                    exportTitle,
                    pageWidth / 2f, cursorY,
                    headerPaint
                )
                cursorY += 30f
                // Sottotitolo con date centrate
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 12f
                    color = theme.textLight
                    textAlign = Paint.Align.CENTER
                }
                currentPage.canvas.drawText(
                    "${displayStartDate ?: rawStartDate}  —  ${displayEndDate ?: rawEndDate}",
                    pageWidth / 2f, cursorY, subPaint
                )
                cursorY += 30f
            } else {
                writeLine(exportTitle, headerPaint, 40f, margin)
            }

            writeSection(context.getString(R.string.pdf_section_info))
            writeInfoRow(context.getString(R.string.pdf_label_muscle_group), scheda.getGruppiMuscolariDisplay())
            writeInfoRow(context.getString(R.string.pdf_label_intensity), scheda.intesita)
            if (!isModern) {
                writeInfoRow(
                    context.getString(R.string.pdf_label_period),
                    "${displayStartDate ?: rawStartDate} - ${displayEndDate ?: rawEndDate}"
                )
            }
            meta?.coachName?.takeIf { it.isNotBlank() }?.let {
                writeInfoRow(context.getString(R.string.pdf_label_coach), it)
            }
            meta?.athleteName?.takeIf { it.isNotBlank() }?.let {
                writeInfoRow(context.getString(R.string.pdf_label_athlete), it)
            }

            if (!scheda.notes.isNullOrBlank()) {
                cursorY += 10f
                writeSection(context.getString(R.string.pdf_section_notes))
                scheda.notes.lines().forEach { line ->
                    writeLine(line.trim(), bodyPaint, 18f)
                }
            }

            cursorY += 20f
            writeSection(context.getString(R.string.pdf_section_exercises, esercizi.size))
            cursorY += 5f

            if (esercizi.isEmpty()) {
                writeLine(context.getString(R.string.pdf_no_exercises), subtitlePaint, 24f)
            } else {
                // Scheda complessa: un blocco per settimana/giorno con la sua intestazione,
                // e la numerazione degli esercizi che riparte da 1 dentro ogni giorno.
                val struttura = esercizi
                    .groupBy { it.settimana to it.giorno }
                    .toSortedMap(compareBy({ it.first }, { it.second }))
                val etichette = struttura.size > 1

                struttura.forEach { (chiave, gruppo) ->
                    val (settimana, giorno) = chiave
                    if (etichette) {
                        cursorY += 6f
                        writeLine("Settimana $settimana - Giorno $giorno", subtitlePaint, 24f)
                    }
                    gruppo.sortedBy { it.ordine }.forEachIndexed { index, esercizio ->
                        if (isModern) drawExerciseModern(index, esercizio)
                        else drawExerciseClassic(index, esercizio)
                    }
                }
            }

            finishPage()

            val safeName = exportTitle.replace(Regex("[^A-Za-z0-9_-]"), "_")
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault()
            ).format(System.currentTimeMillis())
            val sanitizedPeriod = rawStartDate.replace(Regex("[^0-9A-Za-z]"), "")
            val formatSuffix = if (isModern) "_modern" else ""
            val fileName = "${appName}_${safeName}_${sanitizedPeriod}_$timeStamp$formatSuffix.pdf"

            val appDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            val pdfFile = File(appDir, fileName)
            FileOutputStream(pdfFile).use { pdf.writeTo(it) }
            copyToPublicDocuments(context, pdfFile, fileName)
            pdf.close()
            logoBitmap?.recycle()

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

    /** Sul foglio stampato "20 kg" si legge meglio di "20.0 kg". */
    private fun formatPeso(value: Float): String =
        if (value % 1f == 0f) value.toInt().toString()
        else String.format(Locale.getDefault(), "%.1f", value)

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
