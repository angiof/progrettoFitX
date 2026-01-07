package com.app.fityo.trueclone.depth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.sqrt

/**
 * Stimatore di profondità semplificato.
 * Usa un algoritmo basato su luminosità e gradiente invece di MiDaS TFLite
 * per evitare dipendenze pesanti.
 *
 * Per una stima più accurata si potrebbe integrare MiDaS TFLite in futuro.
 */
class MidasDepthEstimator(private val context: Context) {

    companion object {
        private const val OUTPUT_SIZE = 256  // Dimensione output depth map
    }

    /**
     * Risultato della stima di profondità.
     */
    data class DepthResult(
        val depthMap: FloatArray,       // Depth values normalized 0-1
        val width: Int,
        val height: Int,
        val minDepth: Float,
        val maxDepth: Float,
        val processingTimeMs: Long
    ) {
        /**
         * Ottiene il valore di profondità a una coordinata normalizzata (0-1).
         */
        fun getDepthAt(normalizedX: Float, normalizedY: Float): Float {
            val x = (normalizedX * width).toInt().coerceIn(0, width - 1)
            val y = (normalizedY * height).toInt().coerceIn(0, height - 1)
            return depthMap[y * width + x]
        }

        /**
         * Calcola la profondità media in una regione.
         */
        fun getAverageDepthInRegion(
            startX: Float, startY: Float,
            endX: Float, endY: Float
        ): Float {
            val x1 = (startX * width).toInt().coerceIn(0, width - 1)
            val y1 = (startY * height).toInt().coerceIn(0, height - 1)
            val x2 = (endX * width).toInt().coerceIn(0, width - 1)
            val y2 = (endY * height).toInt().coerceIn(0, height - 1)

            var sum = 0f
            var count = 0
            for (y in y1..y2) {
                for (x in x1..x2) {
                    sum += depthMap[y * width + x]
                    count++
                }
            }
            return if (count > 0) sum / count else 0f
        }
    }

    private var isInitialized = false

    /**
     * Inizializza lo stimatore (no-op per versione semplificata).
     */
    fun initialize(): Boolean {
        isInitialized = true
        android.util.Log.d("MidasDepth", "Initialized with simplified depth estimation")
        return true
    }

    /**
     * Stima la profondità da un'immagine Bitmap usando un algoritmo semplificato.
     * Usa gradiente e luminosità come proxy per la profondità.
     */
    fun estimateDepth(bitmap: Bitmap): DepthResult? {
        if (!isInitialized) {
            android.util.Log.w("MidasDepth", "Not initialized, auto-initializing...")
            initialize()
        }

        val startTime = System.currentTimeMillis()

        try {
            // Ridimensiona l'immagine
            val resized = Bitmap.createScaledBitmap(bitmap, OUTPUT_SIZE, OUTPUT_SIZE, true)

            // Calcola depth map basata su luminosità e gradiente
            val depthMap = FloatArray(OUTPUT_SIZE * OUTPUT_SIZE)
            var minDepth = Float.MAX_VALUE
            var maxDepth = Float.MIN_VALUE

            // Prima passata: calcola luminosità
            val luminance = FloatArray(OUTPUT_SIZE * OUTPUT_SIZE)
            for (y in 0 until OUTPUT_SIZE) {
                for (x in 0 until OUTPUT_SIZE) {
                    val pixel = resized.getPixel(x, y)
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)
                    // Luminanza percettiva
                    luminance[y * OUTPUT_SIZE + x] = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
                }
            }

            // Seconda passata: calcola gradiente e stima profondità
            for (y in 1 until OUTPUT_SIZE - 1) {
                for (x in 1 until OUTPUT_SIZE - 1) {
                    val idx = y * OUTPUT_SIZE + x

                    // Gradiente Sobel
                    val gx = luminance[(y - 1) * OUTPUT_SIZE + (x + 1)] +
                            2 * luminance[y * OUTPUT_SIZE + (x + 1)] +
                            luminance[(y + 1) * OUTPUT_SIZE + (x + 1)] -
                            luminance[(y - 1) * OUTPUT_SIZE + (x - 1)] -
                            2 * luminance[y * OUTPUT_SIZE + (x - 1)] -
                            luminance[(y + 1) * OUTPUT_SIZE + (x - 1)]

                    val gy = luminance[(y + 1) * OUTPUT_SIZE + (x - 1)] +
                            2 * luminance[(y + 1) * OUTPUT_SIZE + x] +
                            luminance[(y + 1) * OUTPUT_SIZE + (x + 1)] -
                            luminance[(y - 1) * OUTPUT_SIZE + (x - 1)] -
                            2 * luminance[(y - 1) * OUTPUT_SIZE + x] -
                            luminance[(y - 1) * OUTPUT_SIZE + (x + 1)]

                    val gradient = sqrt(gx * gx + gy * gy)

                    // Stima profondità: zone più scure e con meno gradiente = più lontane
                    // Zone più chiare e con più dettaglio = più vicine
                    val lum = luminance[idx]
                    val depth = 1f - (lum * 0.7f + gradient * 0.3f)

                    depthMap[idx] = depth

                    if (depth < minDepth) minDepth = depth
                    if (depth > maxDepth) maxDepth = depth
                }
            }

            // Copia bordi
            for (x in 0 until OUTPUT_SIZE) {
                depthMap[x] = depthMap[OUTPUT_SIZE + x]
                depthMap[(OUTPUT_SIZE - 1) * OUTPUT_SIZE + x] = depthMap[(OUTPUT_SIZE - 2) * OUTPUT_SIZE + x]
            }
            for (y in 0 until OUTPUT_SIZE) {
                depthMap[y * OUTPUT_SIZE] = depthMap[y * OUTPUT_SIZE + 1]
                depthMap[y * OUTPUT_SIZE + OUTPUT_SIZE - 1] = depthMap[y * OUTPUT_SIZE + OUTPUT_SIZE - 2]
            }

            // Normalizza a 0-1
            val range = maxDepth - minDepth
            if (range > 0) {
                for (i in depthMap.indices) {
                    depthMap[i] = (depthMap[i] - minDepth) / range
                }
            }

            // Smoothing semplice (media 3x3)
            val smoothed = FloatArray(OUTPUT_SIZE * OUTPUT_SIZE)
            for (y in 1 until OUTPUT_SIZE - 1) {
                for (x in 1 until OUTPUT_SIZE - 1) {
                    var sum = 0f
                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            sum += depthMap[(y + dy) * OUTPUT_SIZE + (x + dx)]
                        }
                    }
                    smoothed[y * OUTPUT_SIZE + x] = sum / 9f
                }
            }

            // Copia smoothed nei bordi
            for (x in 0 until OUTPUT_SIZE) {
                smoothed[x] = smoothed[OUTPUT_SIZE + x]
                smoothed[(OUTPUT_SIZE - 1) * OUTPUT_SIZE + x] = smoothed[(OUTPUT_SIZE - 2) * OUTPUT_SIZE + x]
            }
            for (y in 0 until OUTPUT_SIZE) {
                smoothed[y * OUTPUT_SIZE] = smoothed[y * OUTPUT_SIZE + 1]
                smoothed[y * OUTPUT_SIZE + OUTPUT_SIZE - 1] = smoothed[y * OUTPUT_SIZE + OUTPUT_SIZE - 2]
            }

            resized.recycle()

            val processingTime = System.currentTimeMillis() - startTime
            android.util.Log.d("MidasDepth", "Depth estimation completed in ${processingTime}ms")

            return DepthResult(
                depthMap = smoothed,
                width = OUTPUT_SIZE,
                height = OUTPUT_SIZE,
                minDepth = 0f,
                maxDepth = 1f,
                processingTimeMs = processingTime
            )

        } catch (e: Exception) {
            android.util.Log.e("MidasDepth", "Depth estimation failed: ${e.message}", e)
            return null
        }
    }

    /**
     * Converte la depth map in un Bitmap visualizzabile.
     */
    fun depthMapToBitmap(result: DepthResult): Bitmap {
        val bitmap = Bitmap.createBitmap(result.width, result.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(result.width * result.height)

        for (i in result.depthMap.indices) {
            val depth = result.depthMap[i]
            // Convert to grayscale (closer = brighter)
            val gray = ((1 - depth) * 255).toInt().coerceIn(0, 255)
            pixels[i] = (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
        }

        bitmap.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
        return bitmap
    }

    fun close() {
        isInitialized = false
    }
}
