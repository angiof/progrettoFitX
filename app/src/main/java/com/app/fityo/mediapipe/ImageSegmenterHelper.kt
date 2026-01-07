package com.app.fityo.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenterResult

/**
 * Helper per MediaPipe Image Segmenter.
 * Segmenta l'immagine per isolare la figura umana dallo sfondo.
 * Usa il modello SelfieSegmenter quadrato (256x256) per foto portrait/fitness.
 */
class ImageSegmenterHelper(
    private val context: Context,
    private val currentDelegate: Delegate = Delegate.CPU,
    private val segmenterListener: SegmenterListener? = null
) {
    private var imageSegmenter: ImageSegmenter? = null

    init {
        setupImageSegmenter()
    }

    private fun setupImageSegmenter() {
        try {
            val baseOptions = BaseOptions.builder()
                .setDelegate(currentDelegate)
                .setModelAssetPath(MODEL_SELFIE_SEGMENTER)
                .build()

            val options = ImageSegmenter.ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setOutputCategoryMask(true)
                .setOutputConfidenceMasks(false)
                .build()

            imageSegmenter = ImageSegmenter.createFromOptions(context, options)
        } catch (e: Exception) {
            segmenterListener?.onError("Image segmenter initialization failed: ${e.message}")
        }
    }

    /**
     * Segmenta un'immagine statica.
     */
    fun segmentImage(bitmap: Bitmap): ResultBundle? {
        val startTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(bitmap).build()

        return try {
            val result = imageSegmenter?.segment(mpImage)
            val inferenceTime = SystemClock.uptimeMillis() - startTime

            result?.let {
                ResultBundle(it, inferenceTime)
            }
        } catch (e: Exception) {
            segmenterListener?.onError("Segmentation failed: ${e.message}")
            null
        }
    }

    /**
     * Crea una maschera bitmap dalla category mask.
     * La maschera ha pixel bianchi dove c'è la persona e trasparenti altrove.
     * NOTA: Il modello SelfieSegmenter produce una maschera 256x256, quindi
     * dobbiamo scalare i valori alle dimensioni dell'immagine originale.
     */
    fun createMaskBitmap(result: ImageSegmenterResult, width: Int, height: Int): Bitmap? {
        val categoryMask = result.categoryMask().orElse(null) ?: return null

        val byteBuffer = ByteBufferExtractor.extract(categoryMask)

        // Dimensioni della maschera del modello (256x256 per SelfieSegmenter)
        val maskWidth = categoryMask.width
        val maskHeight = categoryMask.height

        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        // Scala dalla dimensione della maschera alle dimensioni dell'immagine
        val scaleX = maskWidth.toFloat() / width.toFloat()
        val scaleY = maskHeight.toFloat() / height.toFloat()

        for (y in 0 until height) {
            for (x in 0 until width) {
                // Mappa le coordinate dell'immagine alle coordinate della maschera
                val maskX = (x * scaleX).toInt().coerceIn(0, maskWidth - 1)
                val maskY = (y * scaleY).toInt().coerceIn(0, maskHeight - 1)
                val maskIndex = maskY * maskWidth + maskX

                val category = if (maskIndex < byteBuffer.capacity()) {
                    byteBuffer.get(maskIndex).toInt() and 0xFF
                } else {
                    0
                }

                val pixelIndex = y * width + x
                // Category 1 = persona (foreground)
                pixels[pixelIndex] = if (category == PERSON_CATEGORY) {
                    Color.WHITE
                } else {
                    Color.TRANSPARENT
                }
            }
        }

        maskBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return maskBitmap
    }

    /**
     * Crea una bitmap con la sagoma semi-trasparente per il Ghost Overlay.
     */
    fun createGhostOverlay(
        originalBitmap: Bitmap,
        result: ImageSegmenterResult,
        alpha: Int = 80
    ): Bitmap? {
        val categoryMask = result.categoryMask().orElse(null) ?: return null

        val width = originalBitmap.width
        val height = originalBitmap.height
        val byteBuffer = ByteBufferExtractor.extract(categoryMask)

        // Dimensioni della maschera del modello
        val maskWidth = categoryMask.width
        val maskHeight = categoryMask.height
        val scaleX = maskWidth.toFloat() / width.toFloat()
        val scaleY = maskHeight.toFloat() / height.toFloat()

        val ghostBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalPixels = IntArray(width * height)
        val ghostPixels = IntArray(width * height)

        originalBitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val maskX = (x * scaleX).toInt().coerceIn(0, maskWidth - 1)
                val maskY = (y * scaleY).toInt().coerceIn(0, maskHeight - 1)
                val maskIndex = maskY * maskWidth + maskX

                val category = if (maskIndex < byteBuffer.capacity()) {
                    byteBuffer.get(maskIndex).toInt() and 0xFF
                } else {
                    0
                }

                val pixelIndex = y * width + x
                if (category == PERSON_CATEGORY) {
                    val originalColor = originalPixels[pixelIndex]
                    // Applica alpha per semi-trasparenza
                    ghostPixels[pixelIndex] = Color.argb(
                        alpha,
                        Color.red(originalColor),
                        Color.green(originalColor),
                        Color.blue(originalColor)
                    )
                } else {
                    ghostPixels[pixelIndex] = Color.TRANSPARENT
                }
            }
        }

        ghostBitmap.setPixels(ghostPixels, 0, width, 0, 0, width, height)
        return ghostBitmap
    }

    /**
     * Applica la maschera a una bitmap per isolare la figura.
     */
    fun applyMaskToBitmap(bitmap: Bitmap, result: ImageSegmenterResult): Bitmap? {
        val categoryMask = result.categoryMask().orElse(null) ?: return null

        val width = bitmap.width
        val height = bitmap.height
        val byteBuffer = ByteBufferExtractor.extract(categoryMask)

        // Dimensioni della maschera del modello
        val maskWidth = categoryMask.width
        val maskHeight = categoryMask.height
        val scaleX = maskWidth.toFloat() / width.toFloat()
        val scaleY = maskHeight.toFloat() / height.toFloat()

        val maskedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalPixels = IntArray(width * height)
        val maskedPixels = IntArray(width * height)

        bitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val maskX = (x * scaleX).toInt().coerceIn(0, maskWidth - 1)
                val maskY = (y * scaleY).toInt().coerceIn(0, maskHeight - 1)
                val maskIndex = maskY * maskWidth + maskX

                val category = if (maskIndex < byteBuffer.capacity()) {
                    byteBuffer.get(maskIndex).toInt() and 0xFF
                } else {
                    0
                }

                val pixelIndex = y * width + x
                maskedPixels[pixelIndex] = if (category == PERSON_CATEGORY) {
                    originalPixels[pixelIndex]
                } else {
                    Color.TRANSPARENT
                }
            }
        }

        maskedBitmap.setPixels(maskedPixels, 0, width, 0, 0, width, height)
        return maskedBitmap
    }

    fun close() {
        imageSegmenter?.close()
        imageSegmenter = null
    }

    /**
     * Bundle per i risultati della segmentazione.
     */
    data class ResultBundle(
        val result: ImageSegmenterResult,
        val inferenceTime: Long
    )

    interface SegmenterListener {
        fun onResults(resultBundle: ResultBundle)
        fun onError(error: String)
    }

    companion object {
        // SelfieSegmenter quadrato (256x256) - migliore per foto portrait/fitness
        // Nome file: selfie_segmenter.tflite (scaricabile da MediaPipe)
        private const val MODEL_SELFIE_SEGMENTER = "selfie_segmenter.tflite"
        private const val PERSON_CATEGORY = 1
    }
}
