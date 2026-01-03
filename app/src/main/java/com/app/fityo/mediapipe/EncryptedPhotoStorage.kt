package com.app.fityo.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.io.FileOutputStream

/**
 * Gestisce il salvataggio e il caricamento di foto criptate.
 * Usa AndroidX Security Crypto per crittografia AES-256-GCM.
 */
class EncryptedPhotoStorage(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val storageDir: File by lazy {
        File(context.getExternalFilesDir(null), STORAGE_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Salva una bitmap come foto criptata.
     * @param bitmap La bitmap da salvare
     * @param filename Nome del file (senza estensione)
     * @return Il path completo del file salvato
     */
    fun saveEncryptedPhoto(bitmap: Bitmap, filename: String): Result<String> {
        return try {
            val file = File(storageDir, "$filename.enc")

            // Elimina file esistente se presente
            if (file.exists()) {
                file.delete()
            }

            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileOutput().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            }

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Carica una foto criptata come bitmap.
     * @param path Il path completo del file criptato
     * @return La bitmap decrittata
     */
    fun loadEncryptedPhoto(path: String): Result<Bitmap> {
        return try {
            val file = File(path)
            if (!file.exists()) {
                return Result.failure(IllegalArgumentException("File not found: $path"))
            }

            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val bitmap = encryptedFile.openFileInput().use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }

            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(IllegalStateException("Failed to decode bitmap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una foto criptata.
     * @param path Il path completo del file da eliminare
     */
    fun deletePhoto(path: String): Boolean {
        return try {
            File(path).delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Elimina tutte le foto criptate.
     */
    fun deleteAllPhotos(): Boolean {
        return try {
            storageDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Genera un nome file univoco per una nuova foto.
     */
    fun generateFilename(prefix: String = "compare"): String {
        return "${prefix}_${System.currentTimeMillis()}"
    }

    /**
     * Ottiene la dimensione totale dello storage usato.
     */
    fun getStorageSize(): Long {
        return storageDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    /**
     * Verifica se un file esiste.
     */
    fun fileExists(path: String): Boolean {
        return File(path).exists()
    }

    companion object {
        private const val STORAGE_DIR_NAME = "muscle_compare"
        private const val JPEG_QUALITY = 90
    }
}
