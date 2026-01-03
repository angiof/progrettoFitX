package com.app.fityo.avatar3d.model

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generatore di mesh procedurale per il corpo umano.
 * Crea una rappresentazione 3D stilizzata basata sui parametri shape.
 */
class BodyMeshGenerator {

    data class Vertex(
        val x: Float,
        val y: Float,
        val z: Float,
        val nx: Float = 0f,  // Normal X
        val ny: Float = 0f,  // Normal Y
        val nz: Float = 1f,  // Normal Z
        val r: Float = 0.7f, // Color R
        val g: Float = 0.6f, // Color G
        val b: Float = 0.5f  // Color B (skin tone default)
    )

    data class BodyMesh(
        val vertices: List<Vertex>,
        val indices: List<Int>,
        val zoneVertexMap: Map<BodyZone, List<Int>> // Maps zones to vertex indices
    )

    enum class BodyZone {
        HEAD, NECK, SHOULDERS, CHEST, CORE, WAIST, HIPS,
        LEFT_ARM, RIGHT_ARM, LEFT_FOREARM, RIGHT_FOREARM,
        LEFT_THIGH, RIGHT_THIGH, LEFT_CALF, RIGHT_CALF
    }

    companion object {
        private const val SEGMENTS_AROUND = 12  // Segments around body circumference
        private const val BODY_HEIGHT = 1.8f    // Total height in units
    }

    /**
     * Genera una mesh del corpo basata sui parametri shape.
     */
    fun generateMesh(params: ShapeParameters, zoneColors: ZoneColors? = null): BodyMesh {
        val vertices = mutableListOf<Vertex>()
        val indices = mutableListOf<Int>()
        val zoneVertexMap = mutableMapOf<BodyZone, MutableList<Int>>()

        // Initialize zone lists
        BodyZone.entries.forEach { zoneVertexMap[it] = mutableListOf() }

        // Generate body parts
        var currentVertexOffset = 0

        // Head
        currentVertexOffset = generateSphere(
            vertices, indices, zoneVertexMap,
            centerY = BODY_HEIGHT * 0.92f,
            radius = 0.12f,
            zone = BodyZone.HEAD,
            color = getSkinColor(),
            vertexOffset = currentVertexOffset
        )

        // Neck
        currentVertexOffset = generateCylinder(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.82f,
            topY = BODY_HEIGHT * 0.87f,
            bottomRadius = 0.06f * (0.8f + params.muscle * 0.4f),
            topRadius = 0.05f,
            zone = BodyZone.NECK,
            color = getSkinColor(),
            vertexOffset = currentVertexOffset
        )

        // Shoulders (trapezius area)
        val shoulderWidth = 0.22f + params.shoulderWidth * 0.12f
        currentVertexOffset = generateTrapezoid(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.75f,
            topY = BODY_HEIGHT * 0.82f,
            bottomWidth = shoulderWidth,
            topWidth = 0.08f,
            depth = 0.08f + params.chestDepth * 0.04f,
            zone = BodyZone.SHOULDERS,
            color = getZoneColor(zoneColors?.shoulders),
            vertexOffset = currentVertexOffset
        )

        // Chest
        val chestWidth = shoulderWidth * 0.95f
        val chestDepth = 0.10f + params.chestDepth * 0.06f
        currentVertexOffset = generateTrapezoid(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.62f,
            topY = BODY_HEIGHT * 0.75f,
            bottomWidth = chestWidth * 0.9f,
            topWidth = chestWidth,
            depth = chestDepth,
            zone = BodyZone.CHEST,
            color = getZoneColor(zoneColors?.chest),
            vertexOffset = currentVertexOffset
        )

        // Core/Abs
        val coreWidth = chestWidth * 0.85f
        currentVertexOffset = generateTrapezoid(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.52f,
            topY = BODY_HEIGHT * 0.62f,
            bottomWidth = coreWidth * (0.9f + params.waistWidth * 0.15f),
            topWidth = coreWidth,
            depth = chestDepth * 0.85f,
            zone = BodyZone.CORE,
            color = getZoneColor(zoneColors?.core),
            vertexOffset = currentVertexOffset
        )

        // Waist
        val waistWidth = coreWidth * (0.85f + params.waistWidth * 0.2f)
        currentVertexOffset = generateTrapezoid(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.48f,
            topY = BODY_HEIGHT * 0.52f,
            bottomWidth = waistWidth,
            topWidth = waistWidth * 0.95f,
            depth = chestDepth * 0.8f,
            zone = BodyZone.WAIST,
            color = getZoneColor(zoneColors?.waist),
            vertexOffset = currentVertexOffset
        )

        // Hips
        val hipWidth = 0.18f + params.hipWidth * 0.08f
        currentVertexOffset = generateTrapezoid(
            vertices, indices, zoneVertexMap,
            bottomY = BODY_HEIGHT * 0.42f,
            topY = BODY_HEIGHT * 0.48f,
            bottomWidth = hipWidth,
            topWidth = waistWidth,
            depth = chestDepth * 0.85f,
            zone = BodyZone.HIPS,
            color = getZoneColor(zoneColors?.hips),
            vertexOffset = currentVertexOffset
        )

        // Arms
        val armLength = 0.28f + params.armLength * 0.08f
        val armThickness = 0.035f + params.muscle * 0.02f

        // Left arm
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = -shoulderWidth,
            startY = BODY_HEIGHT * 0.78f,
            length = armLength * 0.5f,
            angleFromVertical = 10f,
            topRadius = armThickness * 1.2f,
            bottomRadius = armThickness,
            zone = BodyZone.LEFT_ARM,
            color = getZoneColor(zoneColors?.arms),
            vertexOffset = currentVertexOffset
        )

        // Left forearm
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = -shoulderWidth - 0.03f,
            startY = BODY_HEIGHT * 0.78f - armLength * 0.5f,
            length = armLength * 0.5f,
            angleFromVertical = 5f,
            topRadius = armThickness,
            bottomRadius = armThickness * 0.7f,
            zone = BodyZone.LEFT_FOREARM,
            color = getSkinColor(),
            vertexOffset = currentVertexOffset
        )

        // Right arm
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = shoulderWidth,
            startY = BODY_HEIGHT * 0.78f,
            length = armLength * 0.5f,
            angleFromVertical = -10f,
            topRadius = armThickness * 1.2f,
            bottomRadius = armThickness,
            zone = BodyZone.RIGHT_ARM,
            color = getZoneColor(zoneColors?.arms),
            vertexOffset = currentVertexOffset
        )

        // Right forearm
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = shoulderWidth + 0.03f,
            startY = BODY_HEIGHT * 0.78f - armLength * 0.5f,
            length = armLength * 0.5f,
            angleFromVertical = -5f,
            topRadius = armThickness,
            bottomRadius = armThickness * 0.7f,
            zone = BodyZone.RIGHT_FOREARM,
            color = getSkinColor(),
            vertexOffset = currentVertexOffset
        )

        // Legs
        val legLength = 0.38f + params.legLength * 0.1f
        val thighThickness = 0.06f + params.muscle * 0.03f

        // Left thigh
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = -hipWidth * 0.4f,
            startY = BODY_HEIGHT * 0.42f,
            length = legLength * 0.55f,
            angleFromVertical = 2f,
            topRadius = thighThickness * 1.1f,
            bottomRadius = thighThickness * 0.8f,
            zone = BodyZone.LEFT_THIGH,
            color = getZoneColor(zoneColors?.thighs),
            vertexOffset = currentVertexOffset
        )

        // Left calf
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = -hipWidth * 0.4f - 0.01f,
            startY = BODY_HEIGHT * 0.42f - legLength * 0.55f,
            length = legLength * 0.45f,
            angleFromVertical = 0f,
            topRadius = thighThickness * 0.7f,
            bottomRadius = thighThickness * 0.4f,
            zone = BodyZone.LEFT_CALF,
            color = getZoneColor(zoneColors?.calves),
            vertexOffset = currentVertexOffset
        )

        // Right thigh
        currentVertexOffset = generateLimb(
            vertices, indices, zoneVertexMap,
            startX = hipWidth * 0.4f,
            startY = BODY_HEIGHT * 0.42f,
            length = legLength * 0.55f,
            angleFromVertical = -2f,
            topRadius = thighThickness * 1.1f,
            bottomRadius = thighThickness * 0.8f,
            zone = BodyZone.RIGHT_THIGH,
            color = getZoneColor(zoneColors?.thighs),
            vertexOffset = currentVertexOffset
        )

        // Right calf
        generateLimb(
            vertices, indices, zoneVertexMap,
            startX = hipWidth * 0.4f + 0.01f,
            startY = BODY_HEIGHT * 0.42f - legLength * 0.55f,
            length = legLength * 0.45f,
            angleFromVertical = 0f,
            topRadius = thighThickness * 0.7f,
            bottomRadius = thighThickness * 0.4f,
            zone = BodyZone.RIGHT_CALF,
            color = getZoneColor(zoneColors?.calves),
            vertexOffset = currentVertexOffset
        )

        return BodyMesh(
            vertices = vertices,
            indices = indices,
            zoneVertexMap = zoneVertexMap
        )
    }

    private fun generateSphere(
        vertices: MutableList<Vertex>,
        indices: MutableList<Int>,
        zoneMap: MutableMap<BodyZone, MutableList<Int>>,
        centerY: Float,
        radius: Float,
        zone: BodyZone,
        color: Triple<Float, Float, Float>,
        vertexOffset: Int
    ): Int {
        val stacks = 8
        val slices = SEGMENTS_AROUND
        var currentOffset = vertexOffset

        for (i in 0..stacks) {
            val phi = PI * i / stacks
            val y = centerY + radius * cos(phi).toFloat()
            val ringRadius = radius * sin(phi).toFloat()

            for (j in 0..slices) {
                val theta = 2 * PI * j / slices
                val x = ringRadius * cos(theta).toFloat()
                val z = ringRadius * sin(theta).toFloat()

                // Normal
                val nx = x / radius
                val ny = (y - centerY) / radius
                val nz = z / radius

                vertices.add(Vertex(x, y, z, nx, ny, nz, color.first, color.second, color.third))
                zoneMap[zone]?.add(vertices.size - 1)
            }
        }

        // Indices for sphere
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = currentOffset + i * (slices + 1) + j
                val second = first + slices + 1

                indices.addAll(listOf(first, second, first + 1))
                indices.addAll(listOf(second, second + 1, first + 1))
            }
        }

        return currentOffset + (stacks + 1) * (slices + 1)
    }

    private fun generateCylinder(
        vertices: MutableList<Vertex>,
        indices: MutableList<Int>,
        zoneMap: MutableMap<BodyZone, MutableList<Int>>,
        bottomY: Float,
        topY: Float,
        bottomRadius: Float,
        topRadius: Float,
        zone: BodyZone,
        color: Triple<Float, Float, Float>,
        vertexOffset: Int
    ): Int {
        var currentOffset = vertexOffset
        val slices = SEGMENTS_AROUND

        // Bottom ring
        for (j in 0..slices) {
            val theta = 2 * PI * j / slices
            val x = bottomRadius * cos(theta).toFloat()
            val z = bottomRadius * sin(theta).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()

            vertices.add(Vertex(x, bottomY, z, nx, 0f, nz, color.first, color.second, color.third))
            zoneMap[zone]?.add(vertices.size - 1)
        }

        // Top ring
        for (j in 0..slices) {
            val theta = 2 * PI * j / slices
            val x = topRadius * cos(theta).toFloat()
            val z = topRadius * sin(theta).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()

            vertices.add(Vertex(x, topY, z, nx, 0f, nz, color.first, color.second, color.third))
            zoneMap[zone]?.add(vertices.size - 1)
        }

        // Indices
        for (j in 0 until slices) {
            val bottomStart = currentOffset + j
            val topStart = currentOffset + slices + 1 + j

            indices.addAll(listOf(bottomStart, topStart, bottomStart + 1))
            indices.addAll(listOf(topStart, topStart + 1, bottomStart + 1))
        }

        return currentOffset + 2 * (slices + 1)
    }

    private fun generateTrapezoid(
        vertices: MutableList<Vertex>,
        indices: MutableList<Int>,
        zoneMap: MutableMap<BodyZone, MutableList<Int>>,
        bottomY: Float,
        topY: Float,
        bottomWidth: Float,
        topWidth: Float,
        depth: Float,
        zone: BodyZone,
        color: Triple<Float, Float, Float>,
        vertexOffset: Int
    ): Int {
        var currentOffset = vertexOffset
        val slices = SEGMENTS_AROUND

        // Bottom ellipse
        for (j in 0..slices) {
            val theta = 2 * PI * j / slices
            val x = bottomWidth * cos(theta).toFloat()
            val z = depth * sin(theta).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()

            vertices.add(Vertex(x, bottomY, z, nx, 0f, nz, color.first, color.second, color.third))
            zoneMap[zone]?.add(vertices.size - 1)
        }

        // Top ellipse
        for (j in 0..slices) {
            val theta = 2 * PI * j / slices
            val x = topWidth * cos(theta).toFloat()
            val z = depth * sin(theta).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()

            vertices.add(Vertex(x, topY, z, nx, 0f, nz, color.first, color.second, color.third))
            zoneMap[zone]?.add(vertices.size - 1)
        }

        // Indices
        for (j in 0 until slices) {
            val bottomStart = currentOffset + j
            val topStart = currentOffset + slices + 1 + j

            indices.addAll(listOf(bottomStart, topStart, bottomStart + 1))
            indices.addAll(listOf(topStart, topStart + 1, bottomStart + 1))
        }

        return currentOffset + 2 * (slices + 1)
    }

    private fun generateLimb(
        vertices: MutableList<Vertex>,
        indices: MutableList<Int>,
        zoneMap: MutableMap<BodyZone, MutableList<Int>>,
        startX: Float,
        startY: Float,
        length: Float,
        angleFromVertical: Float,
        topRadius: Float,
        bottomRadius: Float,
        zone: BodyZone,
        color: Triple<Float, Float, Float>,
        vertexOffset: Int
    ): Int {
        val angleRad = angleFromVertical * PI.toFloat() / 180f
        val endX = startX + length * sin(angleRad)
        val endY = startY - length * cos(angleRad)

        return generateCylinder(
            vertices, indices, zoneMap,
            bottomY = endY,
            topY = startY,
            bottomRadius = bottomRadius,
            topRadius = topRadius,
            zone = zone,
            color = color,
            vertexOffset = vertexOffset
        )
    }

    private fun getSkinColor(): Triple<Float, Float, Float> {
        return Triple(0.85f, 0.72f, 0.62f) // Light skin tone
    }

    private fun getZoneColor(colorHex: Long?): Triple<Float, Float, Float> {
        if (colorHex == null) return getSkinColor()

        val r = ((colorHex shr 16) and 0xFF) / 255f
        val g = ((colorHex shr 8) and 0xFF) / 255f
        val b = (colorHex and 0xFF) / 255f

        // Blend with skin tone (70% zone color, 30% skin)
        val skin = getSkinColor()
        return Triple(
            r * 0.7f + skin.first * 0.3f,
            g * 0.7f + skin.second * 0.3f,
            b * 0.7f + skin.third * 0.3f
        )
    }
}
