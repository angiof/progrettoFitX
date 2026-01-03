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
     */
    fun createMaskBitmap(result: ImageSegmenterResult, width: Int, height: Int): Bitmap? {
        val categoryMask = result.categoryMask().orElse(null) ?: return null

        val byteBuffer = ByteBufferExtractor.extract(categoryMask)
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)

        for (i in 0 until width * height) {
            val category = byteBuffer.get(i).toInt() and 0xFF
            // Category 1 = persona (foreground)
            pixels[i] = if (category == PERSON_CATEGORY) {
                Color.WHITE
            } else {
                Color.TRANSPARENT
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

        val ghostBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalPixels = IntArray(width * height)
        val ghostPixels = IntArray(width * height)

        originalBitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            val category = byteBuffer.get(i).toInt() and 0xFF
            if (category == PERSON_CATEGORY) {
                val originalColor = originalPixels[i]
                // Applica alpha per semi-trasparenza
                ghostPixels[i] = Color.argb(
                    alpha,
                    Color.red(originalColor),
                    Color.green(originalColor),
                    Color.blue(originalColor)
                )
            } else {
                ghostPixels[i] = Color.TRANSPARENT
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

        val maskedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalPixels = IntArray(width * height)
        val maskedPixels = IntArray(width * height)

        bitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            val category = byteBuffer.get(i).toInt() and 0xFF
            maskedPixels[i] = if (category == PERSON_CATEGORY) {
                originalPixels[i]
            } else {
                Color.TRANSPARENT
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
