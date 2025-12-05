package com.app.fityo.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.content.ContentValues
import com.app.fityo.R
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object FitxImportExport {

    /**
     * Esporta una scheda in formato .fitx (JSON)
     */
    suspend fun exportScheda(
        context: Context,
        scheda: SchedeEntity,
        esercizi: List<EsserciziEntity>
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val fitxFormat = FitxFormat.fromScheda(scheda, esercizi)
            val jsonString = FitxFormat.toJson(fitxFormat)

            val appName = context.getString(R.string.app_name)
            val safeName = scheda.titolo.replace(Regex("[^A-Za-z0-9_-]"), "_")
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val fileName = "${appName}_${safeName}_${timeStamp}.fitx"

            // Salva nella directory app-specific
            val appDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            val fitxFile = File(appDir, fileName)
            FileOutputStream(fitxFile).use {
                it.write(jsonString.toByteArray(Charsets.UTF_8))
            }

            // Copia anche nella cartella Download pubblica
            copyToPublicDocuments(context, fitxFile, fileName)

            fitxFile
        }
    }

    /**
     * Importa una scheda da file .fitx (JSON)
     * Restituisce FitxFormat parsed
     */
    suspend fun importScheda(
        context: Context,
        uri: Uri
    ): Result<FitxFormat> = withContext(Dispatchers.IO) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Impossibile aprire il file")

            val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            inputStream.close()

            val fitxFormat = FitxFormat.fromJson(jsonString)
                ?: throw Exception("Formato file non valido")

            fitxFormat
        }
    }

    /**
     * Copia file nella cartella Download pubblica
     */
    private fun copyToPublicDocuments(context: Context, source: File, fileName: String) {
        if (!source.exists()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val appName = context.getString(R.string.app_name)
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
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
            val appName = context.getString(R.string.app_name)
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
}

