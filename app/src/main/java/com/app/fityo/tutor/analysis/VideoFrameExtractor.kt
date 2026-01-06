package com.app.fityo.tutor.analysis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Estrae frame da un video per l'analisi.
 * Utilizza MediaMetadataRetriever per estrarre bitmap a intervalli regolari.
 */
class VideoFrameExtractor(private val context: Context) {

    companion object {
        const val DEFAULT_FRAME_RATE = 10 // Frame per secondo da analizzare
        const val MIN_FRAME_INTERVAL_MS = 100L // Minimo 100ms tra frame
    }

    /**
     * Informazioni sul video.
     */
    data class VideoInfo(
        val durationMs: Long,
        val width: Int,
        val height: Int,
        val frameRate: Float,
        val rotation: Int
    )

    /**
     * Frame estratto con timestamp.
     */
    data class ExtractedFrame(
        val bitmap: Bitmap,
        val timestampMs: Long,
        val frameIndex: Int
    )

    /**
     * Callback per il progresso dell'estrazione.
     */
    interface ExtractionListener {
        fun onProgress(currentFrame: Int, totalFrames: Int)
        fun onFrameExtracted(frame: ExtractedFrame)
        fun onComplete(frames: List<ExtractedFrame>)
        fun onError(error: String)
    }

    /**
     * Ottiene informazioni sul video.
     */
    suspend fun getVideoInfo(videoPath: String): VideoInfo? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

            // Frame rate non è sempre disponibile
            val frameCount = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
            val duration = durationStr?.toLongOrNull() ?: 0L
            val calculatedFrameRate = if (frameCount != null && duration > 0) {
                (frameCount.toFloat() / duration * 1000f)
            } else {
                30f // Default
            }

            VideoInfo(
                durationMs = duration,
                width = widthStr?.toIntOrNull() ?: 0,
                height = heightStr?.toIntOrNull() ?: 0,
                frameRate = calculatedFrameRate,
                rotation = rotationStr?.toIntOrNull() ?: 0
            )
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    /**
     * Estrae frame dal video a intervalli regolari.
     *
     * @param videoPath Percorso del file video
     * @param frameRateTarget Frame per secondo da estrarre (default 10)
     * @param listener Callback per progresso e risultati
     */
    suspend fun extractFrames(
        videoPath: String,
        frameRateTarget: Int = DEFAULT_FRAME_RATE,
        listener: ExtractionListener
    ) = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        val frames = mutableListOf<ExtractedFrame>()

        try {
            retriever.setDataSource(videoPath)

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val rotationDegrees = getRotationDegrees(retriever)

            if (durationMs <= 0) {
                withContext(Dispatchers.Main) {
                    listener.onError("Impossibile determinare la durata del video")
                }
                return@withContext
            }

            // Calcola intervallo tra frame
            val intervalMs = (1000L / frameRateTarget).coerceAtLeast(MIN_FRAME_INTERVAL_MS)
            val totalFrames = (durationMs / intervalMs).toInt()

            if (totalFrames <= 0) {
                withContext(Dispatchers.Main) {
                    listener.onError("Video troppo corto per l'analisi")
                }
                return@withContext
            }

            var frameIndex = 0
            var currentTimeMs = 0L

            while (currentTimeMs < durationMs) {
                // Estrai frame
                val bitmap = retriever.getFrameAtTime(
                    currentTimeMs * 1000, // Converti in microsecondi
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                if (bitmap != null) {
                    val rotatedBitmap = rotateBitmapIfNeeded(bitmap, rotationDegrees)
                    val frame = ExtractedFrame(
                        bitmap = rotatedBitmap,
                        timestampMs = currentTimeMs,
                        frameIndex = frameIndex
                    )
                    frames.add(frame)

                    withContext(Dispatchers.Main) {
                        listener.onProgress(frameIndex + 1, totalFrames)
                        listener.onFrameExtracted(frame)
                    }
                }

                frameIndex++
                currentTimeMs += intervalMs
            }

            withContext(Dispatchers.Main) {
                listener.onComplete(frames)
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                listener.onError("Errore nell'estrazione dei frame: ${e.message}")
            }
        } finally {
            retriever.release()
        }
    }

    /**
     * Estrae un singolo frame a un timestamp specifico.
     */
    suspend fun extractFrameAt(videoPath: String, timestampMs: Long): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            val rotationDegrees = getRotationDegrees(retriever)
            val bitmap = retriever.getFrameAtTime(
                timestampMs * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            bitmap?.let { rotateBitmapIfNeeded(it, rotationDegrees) }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    /**
     * Estrae una thumbnail dal video.
     */
    suspend fun extractThumbnail(videoPath: String): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            val rotationDegrees = getRotationDegrees(retriever)

            // Estrai frame al 10% del video per una thumbnail rappresentativa
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val thumbnailTimeMs = (durationMs * 0.1).toLong()

            val bitmap = retriever.getFrameAtTime(
                thumbnailTimeMs * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            bitmap?.let { rotateBitmapIfNeeded(it, rotationDegrees) }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        val normalized = ((rotationDegrees % 360) + 360) % 360
        if (normalized == 0) return bitmap
        return try {
            val matrix = Matrix().apply { postRotate(normalized.toFloat()) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
            }
            rotated
        } catch (e: Exception) {
            bitmap
        }
    }

    private fun getRotationDegrees(retriever: MediaMetadataRetriever): Int {
        val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        return rotationStr?.toIntOrNull() ?: 0
    }

    /**
     * Salva una thumbnail come file.
     */
    suspend fun saveThumbnail(bitmap: Bitmap, outputPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(outputPath)
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
