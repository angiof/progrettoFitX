package com.app.fityo.trueclone.texture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.app.fityo.trueclone.capture.CapturedPhoto
import com.app.fityo.trueclone.capture.PhotoView
import com.app.fityo.trueclone.mesh.BoundingBox
import com.app.fityo.trueclone.mesh.Mesh3D
import com.app.fityo.trueclone.mesh.MeshVertex
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Proietta le texture dalle foto sulla mesh 3D.
 * Supporta blending da multiple viste per copertura completa.
 */
class TextureProjector {

    companion object {
        private const val TEXTURE_SIZE = 1024
        private const val BLEND_ANGLE_DEGREES = 45f  // Angolo di blending tra viste
    }

    /**
     * Risultato della proiezione texture.
     */
    data class TextureResult(
        val textureMap: Bitmap,
        val uvCoordinates: List<Pair<Float, Float>>,
        val coverage: Float,  // 0-1, percentuale copertura
        val processingTimeMs: Long
    )

    /**
     * Genera la texture map combinando le foto da multiple viste.
     */
    fun projectTextures(
        mesh: Mesh3D,
        photos: List<CapturedPhoto>,
        segmentationMasks: Map<PhotoView, Bitmap>? = null
    ): TextureResult {
        val startTime = System.currentTimeMillis()

        // Crea texture vuota
        val textureMap = Bitmap.createBitmap(
            TEXTURE_SIZE, TEXTURE_SIZE,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(textureMap)
        canvas.drawColor(Color.rgb(180, 140, 120))  // Colore pelle base

        val bbox = mesh.getBoundingBox()

        // Genera coordinate UV per ogni vertice
        val uvCoordinates = generateUVCoordinates(mesh, bbox)

        // Applica ogni foto alla sua zona
        var coveredPixels = 0
        val totalPixels = TEXTURE_SIZE * TEXTURE_SIZE

        for (photo in photos) {
            val mask = segmentationMasks?.get(photo.view)
            coveredPixels += projectPhotoToTexture(
                canvas, textureMap,
                photo, mesh, bbox, uvCoordinates, mask
            )
        }

        // Applica vertex colors dalla texture
        applyTextureToVertexColors(mesh, textureMap, uvCoordinates)

        return TextureResult(
            textureMap = textureMap,
            uvCoordinates = uvCoordinates,
            coverage = coveredPixels.toFloat() / totalPixels,
            processingTimeMs = System.currentTimeMillis() - startTime
        )
    }

    /**
     * Genera coordinate UV cilindriche per la mesh.
     */
    private fun generateUVCoordinates(
        mesh: Mesh3D,
        bbox: BoundingBox
    ): List<Pair<Float, Float>> {
        return mesh.vertices.map { vertex ->
            // UV cilindrico
            // U = angolo attorno all'asse Y (0-1)
            // V = altezza normalizzata (0-1)

            val angle = atan2(vertex.z, vertex.x)
            val u = ((angle + PI) / (2 * PI)).toFloat()

            val v = ((vertex.y - bbox.minY) / bbox.height).coerceIn(0f, 1f)

            Pair(u, v)
        }
    }

    /**
     * Proietta una singola foto sulla texture.
     */
    private fun projectPhotoToTexture(
        canvas: Canvas,
        textureMap: Bitmap,
        photo: CapturedPhoto,
        mesh: Mesh3D,
        bbox: BoundingBox,
        uvCoordinates: List<Pair<Float, Float>>,
        segmentationMask: Bitmap?
    ): Int {
        val photoBitmap = photo.bitmap
        val photoWidth = photoBitmap.width
        val photoHeight = photoBitmap.height

        // Determina range UV per questa vista
        // Nota: BACK usa wrap-around (0.75-1.0 e 0.0-0.25), gestito nel range check sotto
        val (uMin, uMax) = when (photo.view) {
            PhotoView.FRONT -> Pair(0.25f, 0.75f)   // Fronte
            PhotoView.BACK -> Pair(0.75f, 1f)       // Retro (wrap-around gestito nel range check)
            PhotoView.SIDE -> Pair(0f, 0.25f)       // Laterale sinistro
        }

        var pixelsWritten = 0
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        // Per ogni vertice nella zona di questa vista
        for (i in mesh.vertices.indices) {
            val (u, v) = uvCoordinates[i]

            // Verifica se questo vertice è nella zona di questa foto
            val inRange = when (photo.view) {
                PhotoView.FRONT -> u in 0.25f..0.75f
                PhotoView.BACK -> u < 0.25f || u > 0.75f
                PhotoView.SIDE -> u in 0f..0.25f || u in 0.75f..1f
            }

            if (!inRange) continue

            // Calcola posizione nel texture map
            val texX = (u * TEXTURE_SIZE).toInt().coerceIn(0, TEXTURE_SIZE - 1)
            val texY = ((1f - v) * TEXTURE_SIZE).toInt().coerceIn(0, TEXTURE_SIZE - 1)

            // Calcola posizione corrispondente nella foto
            val vertex = mesh.vertices[i]
            val (photoX, photoY) = projectVertexToPhoto(vertex, bbox, photo.view, photoWidth, photoHeight)

            if (photoX in 0 until photoWidth && photoY in 0 until photoHeight) {
                // Verifica segmentazione (se disponibile)
                val isForeground = segmentationMask?.let { mask ->
                    val maskX = (photoX * mask.width / photoWidth).coerceIn(0, mask.width - 1)
                    val maskY = (photoY * mask.height / photoHeight).coerceIn(0, mask.height - 1)
                    val pixel = mask.getPixel(maskX, maskY)
                    (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3 > 128
                } ?: true

                if (isForeground) {
                    val pixel = photoBitmap.getPixel(photoX, photoY)

                    // Calcola peso per blending (più forte al centro della vista)
                    val blendWeight = calculateBlendWeight(u, photo.view)

                    // Leggi pixel esistente e blenda
                    val existingPixel = textureMap.getPixel(texX, texY)
                    val blendedPixel = blendPixels(existingPixel, pixel, blendWeight)

                    textureMap.setPixel(texX, texY, blendedPixel)
                    pixelsWritten++
                }
            }
        }

        return pixelsWritten
    }

    /**
     * Proietta un vertice 3D sulla foto 2D.
     */
    private fun projectVertexToPhoto(
        vertex: MeshVertex,
        bbox: BoundingBox,
        view: PhotoView,
        photoWidth: Int,
        photoHeight: Int
    ): Pair<Int, Int> {
        return when (view) {
            PhotoView.FRONT -> {
                // Proiezione ortografica frontale (X-Y plane)
                val x = ((vertex.x - bbox.minX) / bbox.width * photoWidth).toInt()
                val y = ((bbox.maxY - vertex.y) / bbox.height * photoHeight).toInt()
                Pair(x, y)
            }
            PhotoView.BACK -> {
                // Proiezione ortografica posteriore (X invertita)
                val x = ((bbox.maxX - vertex.x) / bbox.width * photoWidth).toInt()
                val y = ((bbox.maxY - vertex.y) / bbox.height * photoHeight).toInt()
                Pair(x, y)
            }
            PhotoView.SIDE -> {
                // Proiezione ortografica laterale (Z-Y plane)
                val x = ((vertex.z - bbox.minZ) / bbox.depth * photoWidth).toInt()
                val y = ((bbox.maxY - vertex.y) / bbox.height * photoHeight).toInt()
                Pair(x, y)
            }
        }
    }

    /**
     * Calcola il peso di blending basato sulla posizione UV.
     */
    private fun calculateBlendWeight(u: Float, view: PhotoView): Float {
        // Centro della vista per questa foto
        val viewCenter = when (view) {
            PhotoView.FRONT -> 0.5f
            PhotoView.BACK -> 0f  // o 1f (wrap around)
            PhotoView.SIDE -> 0.25f
        }

        // Distanza dal centro (con wrap-around per BACK)
        val distance = if (view == PhotoView.BACK) {
            minOf(abs(u - 0f), abs(u - 1f))
        } else {
            abs(u - viewCenter)
        }

        // Peso gaussiano
        val sigma = BLEND_ANGLE_DEGREES / 360f
        return kotlin.math.exp(-(distance * distance) / (2 * sigma * sigma)).toFloat()
    }

    /**
     * Blenda due pixel con un peso.
     */
    private fun blendPixels(existing: Int, new: Int, weight: Float): Int {
        val w = weight.coerceIn(0f, 1f)
        val invW = 1f - w

        val r = (Color.red(existing) * invW + Color.red(new) * w).toInt()
        val g = (Color.green(existing) * invW + Color.green(new) * w).toInt()
        val b = (Color.blue(existing) * invW + Color.blue(new) * w).toInt()

        return Color.rgb(
            r.coerceIn(0, 255),
            g.coerceIn(0, 255),
            b.coerceIn(0, 255)
        )
    }

    /**
     * Applica i colori dalla texture ai vertici della mesh.
     */
    private fun applyTextureToVertexColors(
        mesh: Mesh3D,
        textureMap: Bitmap,
        uvCoordinates: List<Pair<Float, Float>>
    ) {
        for (i in mesh.vertices.indices) {
            val (u, v) = uvCoordinates[i]
            val texX = (u * (textureMap.width - 1)).toInt().coerceIn(0, textureMap.width - 1)
            val texY = ((1f - v) * (textureMap.height - 1)).toInt().coerceIn(0, textureMap.height - 1)

            val pixel = textureMap.getPixel(texX, texY)

            mesh.vertices[i].r = Color.red(pixel) / 255f
            mesh.vertices[i].g = Color.green(pixel) / 255f
            mesh.vertices[i].b = Color.blue(pixel) / 255f
        }
    }

    /**
     * Estrae le normali dalla foto (per dettagli superficie).
     * Usa gradiente di luminosità come approssimazione delle normali.
     */
    fun extractNormalMapFromPhoto(photo: Bitmap): Bitmap {
        val width = photo.width
        val height = photo.height
        val normalMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                // Calcola gradiente usando Sobel
                val left = getLuminance(photo.getPixel(x - 1, y))
                val right = getLuminance(photo.getPixel(x + 1, y))
                val top = getLuminance(photo.getPixel(x, y - 1))
                val bottom = getLuminance(photo.getPixel(x, y + 1))

                val dx = (right - left) / 2f
                val dy = (bottom - top) / 2f

                // Converti in normale (assumendo superficie rivolta verso camera)
                val nx = -dx
                val ny = -dy
                val nz = 1f

                val length = sqrt(nx * nx + ny * ny + nz * nz)

                // Normalizza e mappa a colore [0, 255]
                val r = ((nx / length + 1f) * 0.5f * 255).toInt().coerceIn(0, 255)
                val g = ((ny / length + 1f) * 0.5f * 255).toInt().coerceIn(0, 255)
                val b = ((nz / length + 1f) * 0.5f * 255).toInt().coerceIn(0, 255)

                normalMap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }

        return normalMap
    }

    private fun getLuminance(pixel: Int): Float {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        return (0.299f * r + 0.587f * g + 0.114f * b) / 255f
    }

    /**
     * Genera una texture heightmap dalla depth map per dettagli muscoli.
     */
    fun depthToHeightmap(
        depthMap: FloatArray,
        width: Int,
        height: Int,
        invertDepth: Boolean = true
    ): Bitmap {
        val heightmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val depth = depthMap[y * width + x]
                val value = if (invertDepth) 1f - depth else depth
                val gray = (value * 255).toInt().coerceIn(0, 255)
                heightmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }

        return heightmap
    }
}
