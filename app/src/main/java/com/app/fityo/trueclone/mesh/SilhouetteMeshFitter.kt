package com.app.fityo.trueclone.mesh

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Adatta la mesh 3D alle silhouette estratte dalle foto dell'utente.
 * Usa un approccio iterativo per deformare i vertici finché la proiezione
 * della mesh corrisponde alla silhouette.
 */
class SilhouetteMeshFitter {

    companion object {
        private const val MAX_ITERATIONS = 50
        private const val CONVERGENCE_THRESHOLD = 0.001f
        private const val DEFORMATION_RATE = 0.3f
        private const val SMOOTHING_WEIGHT = 0.5f
    }

    /**
     * Silhouette estratta da una foto.
     */
    data class Silhouette(
        val mask: Array<BooleanArray>,  // true = corpo, false = sfondo
        val width: Int,
        val height: Int,
        val viewAngle: Float,  // 0 = frontale, 90 = laterale, 180 = posteriore
        val boundingBox: BoundingBox2D
    ) {
        data class BoundingBox2D(
            val minX: Int,
            val maxX: Int,
            val minY: Int,
            val maxY: Int
        ) {
            val width: Int get() = maxX - minX
            val height: Int get() = maxY - minY
            val centerX: Int get() = (minX + maxX) / 2
            val centerY: Int get() = (minY + maxY) / 2
        }

        /**
         * Ottiene la larghezza della silhouette a una data altezza normalizzata (0=bottom, 1=top).
         */
        fun getWidthAtHeight(normalizedY: Float): Float {
            val y = (boundingBox.minY + (1f - normalizedY) * boundingBox.height).toInt()
                .coerceIn(0, height - 1)

            var minX = width
            var maxX = 0

            for (x in 0 until width) {
                if (mask[y][x]) {
                    minX = min(minX, x)
                    maxX = max(maxX, x)
                }
            }

            return if (maxX > minX) {
                (maxX - minX).toFloat() / boundingBox.width
            } else {
                0f
            }
        }

        /**
         * Verifica se un punto proiettato è dentro la silhouette.
         */
        fun containsPoint(normalizedX: Float, normalizedY: Float): Boolean {
            val x = (boundingBox.minX + normalizedX * boundingBox.width).toInt()
                .coerceIn(0, width - 1)
            val y = (boundingBox.minY + (1f - normalizedY) * boundingBox.height).toInt()
                .coerceIn(0, height - 1)
            return mask[y][x]
        }
    }

    /**
     * Risultato del fitting.
     */
    data class FittingResult(
        val mesh: Mesh3D,
        val iterations: Int,
        val finalError: Float,
        val converged: Boolean,
        val processingTimeMs: Long
    )

    /**
     * Estrae la silhouette da un'immagine segmentata (maschera binaria).
     */
    fun extractSilhouette(
        segmentationMask: Bitmap,
        viewAngle: Float
    ): Silhouette {
        val width = segmentationMask.width
        val height = segmentationMask.height
        val mask = Array(height) { BooleanArray(width) }

        var minX = width
        var maxX = 0
        var minY = height
        var maxY = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = segmentationMask.getPixel(x, y)
                // Considera bianco (o quasi) come corpo
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val isForeground = brightness > 128

                mask[y][x] = isForeground

                if (isForeground) {
                    minX = min(minX, x)
                    maxX = max(maxX, x)
                    minY = min(minY, y)
                    maxY = max(maxY, y)
                }
            }
        }

        // Assicura bounding box valido
        if (minX >= maxX || minY >= maxY) {
            minX = 0
            maxX = width - 1
            minY = 0
            maxY = height - 1
        }

        return Silhouette(
            mask = mask,
            width = width,
            height = height,
            viewAngle = viewAngle,
            boundingBox = Silhouette.BoundingBox2D(minX, maxX, minY, maxY)
        )
    }

    /**
     * Adatta la mesh alle silhouette fornite.
     *
     * @param baseMesh Mesh base da deformare
     * @param frontSilhouette Silhouette frontale (opzionale)
     * @param sideSilhouette Silhouette laterale (opzionale)
     * @param backSilhouette Silhouette posteriore (opzionale)
     */
    fun fitMeshToSilhouettes(
        baseMesh: Mesh3D,
        frontSilhouette: Silhouette?,
        sideSilhouette: Silhouette?,
        backSilhouette: Silhouette?
    ): FittingResult {
        val startTime = System.currentTimeMillis()

        if (frontSilhouette == null && sideSilhouette == null && backSilhouette == null) {
            return FittingResult(
                mesh = baseMesh,
                iterations = 0,
                finalError = 0f,
                converged = true,
                processingTimeMs = 0
            )
        }

        val mesh = baseMesh.deepCopy()
        val bbox = mesh.getBoundingBox()

        var iteration = 0
        var error = Float.MAX_VALUE
        var prevError = Float.MAX_VALUE

        while (iteration < MAX_ITERATIONS && error > CONVERGENCE_THRESHOLD) {
            prevError = error
            error = 0f

            // Calcola deformazioni per ogni vertice
            val deformations = Array(mesh.vertices.size) { floatArrayOf(0f, 0f, 0f) }
            val weights = FloatArray(mesh.vertices.size)

            // Fit frontale (proiezione X-Y)
            if (frontSilhouette != null) {
                error += fitToView(mesh, frontSilhouette, bbox, deformations, weights, ViewDirection.FRONT)
            }

            // Fit laterale (proiezione Z-Y)
            if (sideSilhouette != null) {
                error += fitToView(mesh, sideSilhouette, bbox, deformations, weights, ViewDirection.SIDE)
            }

            // Fit posteriore (proiezione X-Y, invertita)
            if (backSilhouette != null) {
                error += fitToView(mesh, backSilhouette, bbox, deformations, weights, ViewDirection.BACK)
            }

            // Applica deformazioni
            for (i in mesh.vertices.indices) {
                if (weights[i] > 0) {
                    val v = mesh.vertices[i]
                    v.x += deformations[i][0] / weights[i] * DEFORMATION_RATE
                    v.y += deformations[i][1] / weights[i] * DEFORMATION_RATE
                    v.z += deformations[i][2] / weights[i] * DEFORMATION_RATE
                }
            }

            // Smoothing laplaciano per evitare artefatti
            applyLaplacianSmoothing(mesh)

            iteration++

            // Check convergenza
            if (abs(error - prevError) < CONVERGENCE_THRESHOLD * 0.1f) {
                break
            }
        }

        mesh.recalculateNormals()

        return FittingResult(
            mesh = mesh,
            iterations = iteration,
            finalError = error,
            converged = error <= CONVERGENCE_THRESHOLD || abs(error - prevError) < CONVERGENCE_THRESHOLD * 0.1f,
            processingTimeMs = System.currentTimeMillis() - startTime
        )
    }

    private enum class ViewDirection { FRONT, SIDE, BACK }

    /**
     * Calcola le deformazioni necessarie per una vista.
     */
    private fun fitToView(
        mesh: Mesh3D,
        silhouette: Silhouette,
        bbox: BoundingBox,
        deformations: Array<FloatArray>,
        weights: FloatArray,
        direction: ViewDirection
    ): Float {
        var totalError = 0f
        val viewCount = mesh.vertices.count()

        for (i in mesh.vertices.indices) {
            val v = mesh.vertices[i]

            // Proietta vertice sulla vista
            val (projX, projY) = when (direction) {
                ViewDirection.FRONT -> Pair(
                    (v.x - bbox.minX) / bbox.width,
                    (v.y - bbox.minY) / bbox.height
                )
                ViewDirection.SIDE -> Pair(
                    (v.z - bbox.minZ) / bbox.depth,
                    (v.y - bbox.minY) / bbox.height
                )
                ViewDirection.BACK -> Pair(
                    1f - (v.x - bbox.minX) / bbox.width,
                    (v.y - bbox.minY) / bbox.height
                )
            }

            // Calcola larghezza attesa dalla silhouette a questa altezza
            val expectedWidth = silhouette.getWidthAtHeight(projY)
            if (expectedWidth <= 0) continue

            // Calcola posizione radiale del vertice rispetto all'asse centrale
            val actualRadius = when (direction) {
                ViewDirection.FRONT, ViewDirection.BACK -> abs(v.x)
                ViewDirection.SIDE -> abs(v.z)
            }

            // Raggio atteso basato sulla silhouette
            val expectedRadius = expectedWidth * bbox.width / 2

            // Calcola errore e deformazione
            val radiusError = expectedRadius - actualRadius
            totalError += abs(radiusError)

            // Applica deformazione nella direzione corretta
            val deformAmount = radiusError * 0.5f
            when (direction) {
                ViewDirection.FRONT, ViewDirection.BACK -> {
                    val sign = if (v.x >= 0) 1f else -1f
                    deformations[i][0] += deformAmount * sign
                }
                ViewDirection.SIDE -> {
                    val sign = if (v.z >= 0) 1f else -1f
                    deformations[i][2] += deformAmount * sign
                }
            }
            weights[i] += 1f
        }

        return totalError / viewCount
    }

    /**
     * Applica smoothing laplaciano per mantenere la mesh smooth.
     */
    private fun applyLaplacianSmoothing(mesh: Mesh3D) {
        // Costruisci adiacenza vertici (semplificata: vertici vicini per indice)
        val smoothed = mesh.vertices.map { it.copy() }

        for (face in mesh.faces) {
            val indices = listOf(face.v1, face.v2, face.v3)

            for (i in indices.indices) {
                val curr = indices[i]
                val neighbors = indices.filter { it != curr }

                var avgX = mesh.vertices[curr].x
                var avgY = mesh.vertices[curr].y
                var avgZ = mesh.vertices[curr].z

                for (n in neighbors) {
                    avgX += mesh.vertices[n].x
                    avgY += mesh.vertices[n].y
                    avgZ += mesh.vertices[n].z
                }

                avgX /= (neighbors.size + 1)
                avgY /= (neighbors.size + 1)
                avgZ /= (neighbors.size + 1)

                // Interpola verso la media
                smoothed[curr].x = mesh.vertices[curr].x * (1 - SMOOTHING_WEIGHT) + avgX * SMOOTHING_WEIGHT
                smoothed[curr].y = mesh.vertices[curr].y * (1 - SMOOTHING_WEIGHT) + avgY * SMOOTHING_WEIGHT
                smoothed[curr].z = mesh.vertices[curr].z * (1 - SMOOTHING_WEIGHT) + avgZ * SMOOTHING_WEIGHT
            }
        }

        // Applica risultati
        for (i in mesh.vertices.indices) {
            mesh.vertices[i].x = smoothed[i].x
            mesh.vertices[i].y = smoothed[i].y
            mesh.vertices[i].z = smoothed[i].z
        }
    }

    /**
     * Calcola metriche dalla silhouette per inizializzare ShapeParameters.
     */
    fun calculateShapeFromSilhouettes(
        frontSilhouette: Silhouette?,
        sideSilhouette: Silhouette?,
        backSilhouette: Silhouette?,
        userHeightCm: Float
    ): ShapeParameters {
        // Usa frontale come riferimento principale
        val front = frontSilhouette ?: return ShapeParameters.defaultMale()

        // Calcola proporzioni dalla silhouette frontale
        val shoulderY = 0.85f  // ~85% dell'altezza
        val hipY = 0.55f
        val waistY = 0.65f

        val shoulderWidth = front.getWidthAtHeight(shoulderY)
        val hipWidth = front.getWidthAtHeight(hipY)
        val waistWidth = front.getWidthAtHeight(waistY)

        // Stima profondità dalla vista laterale
        var chestDepth = shoulderWidth * 0.6f
        var hipDepth = hipWidth * 0.7f

        if (sideSilhouette != null) {
            chestDepth = sideSilhouette.getWidthAtHeight(shoulderY)
            hipDepth = sideSilhouette.getWidthAtHeight(hipY)
        }

        // Converti in cm usando l'altezza nota
        val pixelToCm = userHeightCm / front.boundingBox.height

        return ShapeParameters(
            heightCm = userHeightCm,
            shoulderWidthCm = shoulderWidth * front.boundingBox.width * pixelToCm,
            hipWidthCm = hipWidth * front.boundingBox.width * pixelToCm,
            waistCircumferenceCm = waistWidth * front.boundingBox.width * pixelToCm * 3.14f,
            chestCircumferenceCm = shoulderWidth * front.boundingBox.width * pixelToCm * 3.14f,
            shoulderToHipRatio = if (hipWidth > 0) shoulderWidth / hipWidth else 1.3f,
            waistToHipRatio = if (hipWidth > 0) waistWidth / hipWidth else 0.85f
        )
    }

    /**
     * Confronta due mesh e calcola le differenze per vertice.
     * Ritorna un array di differenze normalizzate (-1 a +1).
     */
    fun compareMeshes(
        currentMesh: Mesh3D,
        previousMesh: Mesh3D
    ): FloatArray {
        if (currentMesh.vertices.size != previousMesh.vertices.size) {
            return FloatArray(currentMesh.vertices.size) { 0f }
        }

        val differences = FloatArray(currentMesh.vertices.size)
        var maxDiff = 0.001f  // Evita divisione per zero

        // Calcola differenze
        for (i in currentMesh.vertices.indices) {
            val curr = currentMesh.vertices[i]
            val prev = previousMesh.vertices[i]

            // Differenza di "volume" radiale
            val currR = sqrt(curr.x * curr.x + curr.z * curr.z)
            val prevR = sqrt(prev.x * prev.x + prev.z * prev.z)
            differences[i] = currR - prevR

            maxDiff = max(maxDiff, abs(differences[i]))
        }

        // Normalizza
        for (i in differences.indices) {
            differences[i] = (differences[i] / maxDiff).coerceIn(-1f, 1f)
        }

        return differences
    }
}
