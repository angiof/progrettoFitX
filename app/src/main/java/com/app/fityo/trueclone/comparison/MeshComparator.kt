package com.app.fityo.trueclone.comparison

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import com.app.fityo.trueclone.mesh.Mesh3D
import com.app.fityo.trueclone.mesh.MeshVertex
import com.app.fityo.trueclone.mesh.HumanBodyMeshGenerator
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Confronta due mesh 3D e genera heatmap per visualizzare i progressi.
 * Verde = crescita muscolare, Giallo = invariato, Rosso/Arancio = perdita.
 */
class MeshComparator {

    companion object {
        // Soglie per valutare cambiamento significativo (in unità normalizzate)
        private const val MIN_CHANGE_THRESHOLD = 0.005f  // 0.5% cambiamento minimo
        private const val MAX_CHANGE_DISPLAY = 0.1f     // 10% cambiamento per saturazione colore

    }

    /**
     * Risultato del confronto tra due mesh.
     */
    data class ComparisonResult(
        val vertexChanges: FloatArray,          // Cambiamento per vertice (-1 a +1)
        val zoneChanges: Map<Int, ZoneChange>,  // Cambiamento per zona muscolare
        val overallChange: Float,               // Cambiamento medio complessivo
        val gainedZones: List<String>,          // Zone con crescita significativa
        val lostZones: List<String>,            // Zone con perdita significativa
        val unchangedZones: List<String>,       // Zone invariate
        val processingTimeMs: Long
    )

    /**
     * Cambiamento in una zona muscolare.
     */
    data class ZoneChange(
        val zoneId: Int,
        val zoneName: String,
        val averageChange: Float,      // -1 a +1
        val percentChange: Float,      // -100% a +100%
        val volumeChange: Float,       // Cambiamento volume stimato
        val isSignificant: Boolean,    // Se il cambiamento è significativo
        val evaluation: ChangeEvaluation
    )

    enum class ChangeEvaluation {
        SIGNIFICANT_GAIN,    // Crescita significativa (verde intenso)
        MODERATE_GAIN,       // Crescita moderata (verde chiaro)
        SLIGHT_GAIN,         // Leggera crescita (verde pallido)
        NO_CHANGE,           // Nessun cambiamento (giallo/neutro)
        SLIGHT_LOSS,         // Leggera perdita (arancio pallido)
        MODERATE_LOSS,       // Perdita moderata (arancio)
        SIGNIFICANT_LOSS     // Perdita significativa (rosso)
    }

    /**
     * Confronta due mesh e calcola i cambiamenti per ogni vertice e zona.
     */
    fun compareMeshes(
        currentMesh: Mesh3D,
        previousMesh: Mesh3D,
        zones: Map<Int, HumanBodyMeshGenerator.MuscleZone>
    ): ComparisonResult {
        val startTime = System.currentTimeMillis()

        require(currentMesh.vertices.size == previousMesh.vertices.size) {
            "Le mesh devono avere lo stesso numero di vertici"
        }

        val vertexChanges = FloatArray(currentMesh.vertices.size)
        var totalChange = 0f

        // Calcola cambiamento per ogni vertice
        for (i in currentMesh.vertices.indices) {
            val change = calculateVertexChange(
                currentMesh.vertices[i],
                previousMesh.vertices[i]
            )
            vertexChanges[i] = change
            totalChange += change
        }

        // Normalizza cambiamenti
        val maxChange = vertexChanges.maxOfOrNull { abs(it) } ?: 1f
        if (maxChange > 0) {
            for (i in vertexChanges.indices) {
                vertexChanges[i] = (vertexChanges[i] / maxChange).coerceIn(-1f, 1f)
            }
        }

        // Calcola cambiamenti per zona
        val zoneChanges = mutableMapOf<Int, ZoneChange>()
        val gainedZones = mutableListOf<String>()
        val lostZones = mutableListOf<String>()
        val unchangedZones = mutableListOf<String>()

        for ((zoneId, zone) in zones) {
            if (zone.vertexIndices.isEmpty()) continue

            val zoneVertexChanges = zone.vertexIndices
                .filter { it < vertexChanges.size }
                .map { vertexChanges[it] }

            if (zoneVertexChanges.isEmpty()) continue

            val avgChange = zoneVertexChanges.average().toFloat()
            val percentChange = avgChange * 100f
            val evaluation = evaluateChange(avgChange)

            val zoneChange = ZoneChange(
                zoneId = zoneId,
                zoneName = zone.name,
                averageChange = avgChange,
                percentChange = percentChange,
                volumeChange = calculateVolumeChange(
                    currentMesh.vertices,
                    previousMesh.vertices,
                    zone.vertexIndices
                ),
                isSignificant = abs(avgChange) > MIN_CHANGE_THRESHOLD,
                evaluation = evaluation
            )
            zoneChanges[zoneId] = zoneChange

            // Categorizza zone
            when (evaluation) {
                ChangeEvaluation.SIGNIFICANT_GAIN,
                ChangeEvaluation.MODERATE_GAIN -> gainedZones.add(zone.name)
                ChangeEvaluation.SIGNIFICANT_LOSS,
                ChangeEvaluation.MODERATE_LOSS -> lostZones.add(zone.name)
                else -> unchangedZones.add(zone.name)
            }
        }

        return ComparisonResult(
            vertexChanges = vertexChanges,
            zoneChanges = zoneChanges,
            overallChange = totalChange / currentMesh.vertices.size,
            gainedZones = gainedZones,
            lostZones = lostZones,
            unchangedZones = unchangedZones,
            processingTimeMs = System.currentTimeMillis() - startTime
        )
    }

    /**
     * Calcola il cambiamento tra due vertici (distanza radiale dall'asse Y).
     */
    private fun calculateVertexChange(current: MeshVertex, previous: MeshVertex): Float {
        // Calcola raggio (distanza dall'asse centrale Y)
        val currentRadius = sqrt(current.x * current.x + current.z * current.z)
        val previousRadius = sqrt(previous.x * previous.x + previous.z * previous.z)

        return currentRadius - previousRadius
    }

    /**
     * Calcola il cambiamento di volume approssimato per una zona.
     */
    private fun calculateVolumeChange(
        currentVertices: List<MeshVertex>,
        previousVertices: List<MeshVertex>,
        vertexIndices: List<Int>
    ): Float {
        var currentVolume = 0f
        var previousVolume = 0f

        for (idx in vertexIndices) {
            if (idx >= currentVertices.size) continue

            // Stima volume usando raggio^2 (proporzionale all'area della sezione)
            val currR = sqrt(
                currentVertices[idx].x * currentVertices[idx].x +
                currentVertices[idx].z * currentVertices[idx].z
            )
            val prevR = sqrt(
                previousVertices[idx].x * previousVertices[idx].x +
                previousVertices[idx].z * previousVertices[idx].z
            )

            currentVolume += currR * currR
            previousVolume += prevR * prevR
        }

        return if (previousVolume > 0) {
            (currentVolume - previousVolume) / previousVolume
        } else {
            0f
        }
    }

    /**
     * Valuta il tipo di cambiamento basato sulla magnitude.
     */
    private fun evaluateChange(change: Float): ChangeEvaluation {
        return when {
            change > 0.15f -> ChangeEvaluation.SIGNIFICANT_GAIN
            change > 0.08f -> ChangeEvaluation.MODERATE_GAIN
            change > 0.02f -> ChangeEvaluation.SLIGHT_GAIN
            change > -0.02f -> ChangeEvaluation.NO_CHANGE
            change > -0.08f -> ChangeEvaluation.SLIGHT_LOSS
            change > -0.15f -> ChangeEvaluation.MODERATE_LOSS
            else -> ChangeEvaluation.SIGNIFICANT_LOSS
        }
    }

    /**
     * Applica la heatmap dei cambiamenti ai colori dei vertici della mesh.
     */
    fun applyHeatmapToMesh(mesh: Mesh3D, vertexChanges: FloatArray) {
        for (i in mesh.vertices.indices) {
            if (i >= vertexChanges.size) continue

            val change = vertexChanges[i]
            val color = changeToColor(change)

            mesh.vertices[i].r = color[0]
            mesh.vertices[i].g = color[1]
            mesh.vertices[i].b = color[2]
        }
    }

    /**
     * Converte un valore di cambiamento in colore RGB.
     * Rosso (-1) → Giallo (0) → Verde (+1)
     */
    private fun changeToColor(change: Float): FloatArray {
        val t = (change + 1f) / 2f  // Normalizza a 0-1

        return when {
            t < 0.4f -> {
                // Rosso → Arancio
                val s = t / 0.4f
                floatArrayOf(
                    0.9f + 0.1f * s,       // R: 0.9 → 1.0
                    0.2f + 0.4f * s,       // G: 0.2 → 0.6
                    0.1f + 0.1f * s        // B: 0.1 → 0.2
                )
            }
            t < 0.5f -> {
                // Arancio → Giallo
                val s = (t - 0.4f) / 0.1f
                floatArrayOf(
                    1f,                    // R: 1.0
                    0.6f + 0.3f * s,       // G: 0.6 → 0.9
                    0.2f + 0.1f * s        // B: 0.2 → 0.3
                )
            }
            t < 0.6f -> {
                // Giallo → Verde chiaro
                val s = (t - 0.5f) / 0.1f
                floatArrayOf(
                    1f - 0.4f * s,         // R: 1.0 → 0.6
                    0.9f + 0.1f * s,       // G: 0.9 → 1.0
                    0.3f                   // B: 0.3
                )
            }
            else -> {
                // Verde chiaro → Verde intenso
                val s = (t - 0.6f) / 0.4f
                floatArrayOf(
                    0.6f - 0.4f * s,       // R: 0.6 → 0.2
                    1f - 0.2f * s,         // G: 1.0 → 0.8
                    0.3f + 0.2f * s        // B: 0.3 → 0.5
                )
            }
        }
    }

    /**
     * Genera una legenda della heatmap come bitmap.
     */
    fun generateHeatmapLegend(width: Int = 300, height: Int = 50): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Sfondo
        canvas.drawColor(Color.TRANSPARENT)

        // Gradiente
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), 0f,
                intArrayOf(
                    Color.rgb(230, 51, 26),    // Rosso (perdita)
                    Color.rgb(255, 153, 51),   // Arancio
                    Color.rgb(255, 230, 77),   // Giallo (invariato)
                    Color.rgb(153, 255, 102),  // Verde chiaro
                    Color.rgb(51, 204, 128)    // Verde (crescita)
                ),
                floatArrayOf(0f, 0.3f, 0.5f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
        }

        // Disegna barra gradiente
        canvas.drawRoundRect(
            10f, 10f, width - 10f, height - 25f,
            8f, 8f, gradientPaint
        )

        // Etichette
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            isAntiAlias = true
        }

        canvas.drawText("Perdita", 10f, height - 5f, textPaint)
        canvas.drawText("Invariato", width / 2f - 25f, height - 5f, textPaint)
        canvas.drawText("Crescita", width - 55f, height - 5f, textPaint)

        return bitmap
    }

    /**
     * Genera un report testuale dei progressi.
     */
    fun generateProgressReport(result: ComparisonResult): String {
        val sb = StringBuilder()

        sb.appendLine("📊 REPORT PROGRESSI TRUECLONE")
        sb.appendLine("═══════════════════════════════")
        sb.appendLine()

        // Sommario
        val overallPercent = result.overallChange * 100
        val overallEmoji = when {
            overallPercent > 5 -> "🚀"
            overallPercent > 2 -> "💪"
            overallPercent > 0 -> "📈"
            overallPercent > -2 -> "➖"
            else -> "📉"
        }

        sb.appendLine("$overallEmoji Cambiamento complessivo: ${String.format("%+.1f%%", overallPercent)}")
        sb.appendLine()

        // Zone in crescita
        if (result.gainedZones.isNotEmpty()) {
            sb.appendLine("✅ ZONE IN CRESCITA:")
            result.gainedZones.forEach { zone ->
                val change = result.zoneChanges.values.find { it.zoneName == zone }
                sb.appendLine("   • $zone: ${String.format("%+.1f%%", change?.percentChange ?: 0f)}")
            }
            sb.appendLine()
        }

        // Zone in perdita
        if (result.lostZones.isNotEmpty()) {
            sb.appendLine("⚠️ ZONE IN PERDITA:")
            result.lostZones.forEach { zone ->
                val change = result.zoneChanges.values.find { it.zoneName == zone }
                sb.appendLine("   • $zone: ${String.format("%+.1f%%", change?.percentChange ?: 0f)}")
            }
            sb.appendLine()
        }

        // Zone invariate
        if (result.unchangedZones.isNotEmpty()) {
            sb.appendLine("➖ ZONE STABILI:")
            sb.appendLine("   ${result.unchangedZones.joinToString(", ")}")
            sb.appendLine()
        }

        // Consigli
        sb.appendLine("💡 SUGGERIMENTI:")
        if (result.lostZones.isNotEmpty()) {
            sb.appendLine("   • Aumenta il volume di allenamento per: ${result.lostZones.joinToString(", ")}")
        }
        if (result.gainedZones.isEmpty()) {
            sb.appendLine("   • Considera di aumentare l'apporto proteico")
            sb.appendLine("   • Verifica il recupero tra gli allenamenti")
        } else {
            sb.appendLine("   • Continua così! I tuoi allenamenti stanno dando risultati")
        }

        return sb.toString()
    }
}
