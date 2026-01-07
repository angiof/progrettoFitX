package com.app.fityo.trueclone.mesh

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generatore di mesh corpo umano anatomicamente corretto.
 * Genera una mesh 3D realistica senza dipendere dalla segmentazione.
 */
class HumanBodyMeshGenerator {

    companion object {
        // Zone muscolari
        const val ZONE_CHEST = 0
        const val ZONE_ABS = 1
        const val ZONE_BACK = 2
        const val ZONE_SHOULDERS = 3
        const val ZONE_BICEPS = 4
        const val ZONE_FOREARMS = 5
        const val ZONE_GLUTES = 6
        const val ZONE_QUADS = 7
        const val ZONE_HAMSTRINGS = 8
        const val ZONE_CALVES = 9

        // Proporzioni anatomiche standard (normalizzate su altezza 1.0)
        private const val HEAD_HEIGHT = 0.13f
        private const val NECK_HEIGHT = 0.03f
        private const val TORSO_HEIGHT = 0.30f
        private const val LEG_HEIGHT = 0.47f
        private const val ARM_LENGTH = 0.38f

        private const val SHOULDER_WIDTH = 0.26f
        private const val CHEST_WIDTH = 0.24f
        private const val WAIST_WIDTH = 0.18f
        private const val HIP_WIDTH = 0.20f

        private const val HEAD_RADIUS = 0.055f
        private const val NECK_RADIUS = 0.035f
        private const val UPPER_ARM_RADIUS = 0.035f
        private const val FOREARM_RADIUS = 0.028f
        private const val THIGH_RADIUS = 0.055f
        private const val CALF_RADIUS = 0.035f
    }

    data class MuscleZone(
        val id: Int,
        val name: String,
        val vertexIndices: MutableList<Int> = mutableListOf(),
        var developmentScore: Float = 0.5f
    )

    data class GeneratedBody(
        val mesh: Mesh3D,
        val zones: Map<Int, MuscleZone>,
        val shapeParams: ShapeParameters
    )

    private val zones = mutableMapOf(
        ZONE_CHEST to MuscleZone(ZONE_CHEST, "Petto"),
        ZONE_ABS to MuscleZone(ZONE_ABS, "Addominali"),
        ZONE_BACK to MuscleZone(ZONE_BACK, "Schiena"),
        ZONE_SHOULDERS to MuscleZone(ZONE_SHOULDERS, "Spalle"),
        ZONE_BICEPS to MuscleZone(ZONE_BICEPS, "Bicipiti"),
        ZONE_FOREARMS to MuscleZone(ZONE_FOREARMS, "Avambracci"),
        ZONE_GLUTES to MuscleZone(ZONE_GLUTES, "Glutei"),
        ZONE_QUADS to MuscleZone(ZONE_QUADS, "Quadricipiti"),
        ZONE_HAMSTRINGS to MuscleZone(ZONE_HAMSTRINGS, "Femorali"),
        ZONE_CALVES to MuscleZone(ZONE_CALVES, "Polpacci")
    )

    /**
     * Genera mesh corpo umano con proporzioni basate sui parametri MISURATI.
     * Usa le misurazioni reali da MediaPipe Pose invece di valori default.
     */
    fun generateBody(params: ShapeParameters): GeneratedBody {
        zones.values.forEach { it.vertexIndices.clear() }

        val vertices = mutableListOf<MeshVertex>()
        val faces = mutableListOf<MeshFace>()

        // Scala basata su altezza (normalizzata a 1.0 = 175cm)
        val heightScale = params.heightCm / 175f

        // USA PROPORZIONI REALI DA MEDIAPIPE (normalizzate)
        // Converti cm in unità mesh (1 unità = ~175cm)
        val measuredShoulderWidth = (params.shoulderWidthCm / params.heightCm).coerceIn(0.20f, 0.35f)
        val measuredHipWidth = (params.hipWidthCm / params.heightCm).coerceIn(0.15f, 0.28f)
        val measuredArmLength = (params.armLengthCm / params.heightCm).coerceIn(0.30f, 0.45f)
        val measuredLegLength = (params.legLengthCm / params.heightCm).coerceIn(0.40f, 0.55f)

        // Fattori muscolari basati sui valori ANALIZZATI (non più default 50%)
        val avgMuscleFactor = (params.chestMuscle + params.armMuscle + params.quadsMuscle) / 3f
        val muscleBonus = 1f + avgMuscleFactor * 0.20f

        // Peso relativo per modificare spessore
        val bmi = params.weightKg / ((params.heightCm / 100f) * (params.heightCm / 100f))
        val weightFactor = (bmi / 22f).coerceIn(0.85f, 1.25f)

        // Usa rapporto spalle/fianchi MISURATO
        val shoulderToHip = params.shoulderToHipRatio.coerceIn(0.9f, 1.6f)

        android.util.Log.d("HumanBodyMeshGenerator",
            "Generating body with MEASURED proportions: " +
            "shoulderWidth=${measuredShoulderWidth}, hipWidth=${measuredHipWidth}, " +
            "S/H ratio=${shoulderToHip}, muscle=${avgMuscleFactor}"
        )

        // Posizioni Y chiave usando leg length MISURATA
        val footY = -0.5f * heightScale
        val kneeY = footY + measuredLegLength * 0.45f * heightScale
        val hipY = footY + measuredLegLength * heightScale
        val torsoHeight = (1f - measuredLegLength - HEAD_HEIGHT - NECK_HEIGHT)
        val shoulderY = hipY + torsoHeight * heightScale
        val neckTopY = shoulderY + NECK_HEIGHT * heightScale
        val headTopY = neckTopY + HEAD_HEIGHT * heightScale

        // ===== TORSO con proporzioni MISURATE =====
        generateTorsoSection(vertices, faces,
            bottomY = hipY, topY = shoulderY,
            bottomWidth = measuredHipWidth * heightScale * weightFactor,
            topWidth = measuredShoulderWidth * heightScale * muscleBonus,
            depth = measuredHipWidth * 0.6f * heightScale * weightFactor,
            chestDepth = measuredShoulderWidth * 0.55f * heightScale * muscleBonus,
            segments = 12, radialSegs = 16,
            chestMuscle = params.chestMuscle,
            absMuscle = params.absMuscle,
            backMuscle = params.backMuscle
        )

        // ===== TESTA e COLLO =====
        generateHead(vertices, faces,
            neckBottom = shoulderY,
            neckTop = neckTopY,
            headTop = headTopY,
            neckRadius = NECK_RADIUS * heightScale,
            headRadius = HEAD_RADIUS * heightScale
        )

        // ===== BRACCIA con lunghezza MISURATA =====
        val shoulderOffset = measuredShoulderWidth / 2 * heightScale * muscleBonus
        val armThickness = UPPER_ARM_RADIUS * (1f + params.armMuscle * 0.3f)

        // Braccio sinistro
        generateArm(vertices, faces,
            shoulderPos = Triple(-shoulderOffset, shoulderY - 0.02f * heightScale, 0f),
            length = measuredArmLength * heightScale,
            upperRadius = armThickness * heightScale * muscleBonus,
            lowerRadius = FOREARM_RADIUS * heightScale * (1f + params.armMuscle * 0.15f),
            armMuscle = params.armMuscle,
            isLeft = true
        )

        // Braccio destro
        generateArm(vertices, faces,
            shoulderPos = Triple(shoulderOffset, shoulderY - 0.02f * heightScale, 0f),
            length = measuredArmLength * heightScale,
            upperRadius = armThickness * heightScale * muscleBonus,
            lowerRadius = FOREARM_RADIUS * heightScale * (1f + params.armMuscle * 0.15f),
            armMuscle = params.armMuscle,
            isLeft = false
        )

        // ===== GAMBE con proporzioni MISURATE =====
        val hipOffset = measuredHipWidth / 4 * heightScale * weightFactor
        val thighThickness = THIGH_RADIUS * (1f + params.quadsMuscle * 0.25f)

        // Gamba sinistra
        generateLeg(vertices, faces,
            hipPos = Triple(-hipOffset, hipY, 0f),
            footY = footY,
            kneeY = kneeY,
            thighRadius = thighThickness * heightScale * weightFactor,
            calfRadius = CALF_RADIUS * heightScale * (1f + params.calvesMuscle * 0.2f),
            quadsMuscle = params.quadsMuscle,
            hamstringsMuscle = params.hamstringsMuscle,
            calvesMuscle = params.calvesMuscle,
            glutesMuscle = params.glutesMuscle,
            isLeft = true
        )

        // Gamba destra
        generateLeg(vertices, faces,
            hipPos = Triple(hipOffset, hipY, 0f),
            footY = footY,
            kneeY = kneeY,
            thighRadius = thighThickness * heightScale * weightFactor,
            calfRadius = CALF_RADIUS * heightScale * (1f + params.calvesMuscle * 0.2f),
            quadsMuscle = params.quadsMuscle,
            hamstringsMuscle = params.hamstringsMuscle,
            calvesMuscle = params.calvesMuscle,
            glutesMuscle = params.glutesMuscle,
            isLeft = false
        )

        val mesh = Mesh3D(vertices, faces, "human_body")
        mesh.recalculateNormals()

        // Aggiorna scores zone
        zones[ZONE_CHEST]?.developmentScore = params.chestMuscle
        zones[ZONE_ABS]?.developmentScore = params.absMuscle
        zones[ZONE_BACK]?.developmentScore = params.backMuscle
        zones[ZONE_SHOULDERS]?.developmentScore = params.shoulderMuscle
        zones[ZONE_BICEPS]?.developmentScore = params.armMuscle
        zones[ZONE_FOREARMS]?.developmentScore = params.armMuscle * 0.9f
        zones[ZONE_GLUTES]?.developmentScore = params.glutesMuscle
        zones[ZONE_QUADS]?.developmentScore = params.quadsMuscle
        zones[ZONE_HAMSTRINGS]?.developmentScore = params.hamstringsMuscle
        zones[ZONE_CALVES]?.developmentScore = params.calvesMuscle

        return GeneratedBody(mesh, zones.toMap(), params)
    }

    private fun generateTorsoSection(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        bottomY: Float, topY: Float,
        bottomWidth: Float, topWidth: Float,
        depth: Float, chestDepth: Float,
        segments: Int, radialSegs: Int,
        chestMuscle: Float, absMuscle: Float, backMuscle: Float
    ) {
        val startIndex = vertices.size
        val height = topY - bottomY

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val y = bottomY + t * height

            // Larghezza interpolata con curva anatomica (vita più stretta)
            val waistFactor = 1f - 0.15f * sin(t * PI.toFloat())  // Rientranza vita
            val width = (bottomWidth + (topWidth - bottomWidth) * t) * waistFactor

            // Profondità con rigonfiamento petto
            val chestBulge = if (t > 0.6f) (t - 0.6f) / 0.4f * chestMuscle * 0.08f else 0f
            val currentDepth = depth + chestBulge

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()

                // Forma ellittica con variazioni anatomiche
                val widthMod = if (angle in (PI * 0.3f)..(PI * 0.7f) ||
                                   angle in (PI * 1.3f)..(PI * 1.7f)) 0.9f else 1f

                val x = cos(angle) * width / 2 * widthMod
                val z = sin(angle) * currentDepth *
                       (if (angle < PI) (1f + chestMuscle * 0.1f) else (1f + backMuscle * 0.08f))

                val vertex = MeshVertex(
                    index = vertices.size,
                    x = x, y = y, z = z,
                    u = i.toFloat() / radialSegs,
                    v = t
                )
                vertices.add(vertex)

                // Assegna zona muscolare
                val zone = when {
                    angle < PI * 0.35f || angle > PI * 1.65f -> {
                        if (t > 0.5f) ZONE_CHEST else ZONE_ABS
                    }
                    angle > PI * 0.65f && angle < PI * 1.35f -> ZONE_BACK
                    else -> ZONE_SHOULDERS
                }
                zones[zone]?.vertexIndices?.add(vertex.index)
            }
        }

        // Genera facce
        for (j in 0 until segments) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                val currUp = curr + radialSegs
                val nextUp = next + radialSegs

                faces.add(MeshFace(curr, next, currUp))
                faces.add(MeshFace(next, nextUp, currUp))
            }
        }
    }

    private fun generateHead(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        neckBottom: Float, neckTop: Float, headTop: Float,
        neckRadius: Float, headRadius: Float
    ) {
        val startIndex = vertices.size
        val radialSegs = 12

        // Collo
        for (j in 0..3) {
            val t = j.toFloat() / 3
            val y = neckBottom + (neckTop - neckBottom) * t
            val radius = neckRadius * (1f - t * 0.1f)

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()
                vertices.add(MeshVertex(
                    index = vertices.size,
                    x = cos(angle) * radius,
                    y = y,
                    z = sin(angle) * radius
                ))
            }
        }

        // Facce collo
        for (j in 0 until 3) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                faces.add(MeshFace(curr, next, curr + radialSegs))
                faces.add(MeshFace(next, next + radialSegs, curr + radialSegs))
            }
        }

        // Testa (ellissoide)
        val headStart = vertices.size
        val headCenter = neckTop + (headTop - neckTop) * 0.45f
        val latSegs = 8
        val lonSegs = 12

        for (lat in 0..latSegs) {
            val theta = (lat.toFloat() / latSegs) * PI.toFloat()
            for (lon in 0 until lonSegs) {
                val phi = (lon.toFloat() / lonSegs) * 2f * PI.toFloat()
                val x = headRadius * 0.85f * sin(theta) * cos(phi)
                val y = headCenter + headRadius * 1.1f * cos(theta)
                val z = headRadius * 0.9f * sin(theta) * sin(phi)

                vertices.add(MeshVertex(vertices.size, x, y, z))
            }
        }

        // Facce testa
        for (lat in 0 until latSegs) {
            for (lon in 0 until lonSegs) {
                val curr = headStart + lat * lonSegs + lon
                val next = headStart + lat * lonSegs + (lon + 1) % lonSegs
                if (lat > 0) faces.add(MeshFace(curr, next, curr + lonSegs))
                if (lat < latSegs - 1) faces.add(MeshFace(next, next + lonSegs, curr + lonSegs))
            }
        }
    }

    private fun generateArm(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        shoulderPos: Triple<Float, Float, Float>,
        length: Float,
        upperRadius: Float,
        lowerRadius: Float,
        armMuscle: Float,
        isLeft: Boolean
    ) {
        val startIndex = vertices.size
        val segments = 8
        val radialSegs = 10

        val xDir = if (isLeft) -1f else 1f
        val armAngle = 0.15f * xDir  // Braccia leggermente divaricate

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val localY = -t * length

            // Posizione lungo il braccio
            val x = shoulderPos.first + sin(armAngle) * (-localY)
            val y = shoulderPos.second + cos(armAngle) * localY
            val z = shoulderPos.third

            // Raggio variabile (bicipite, gomito, avambraccio)
            val radius = when {
                t < 0.15f -> upperRadius * (1f + armMuscle * 0.2f)  // Deltoide
                t < 0.45f -> upperRadius * (0.95f + armMuscle * 0.15f)  // Bicipite
                t < 0.55f -> lowerRadius * 0.85f  // Gomito
                t < 0.85f -> lowerRadius * (1f + armMuscle * 0.08f)  // Avambraccio
                else -> lowerRadius * 0.6f  // Polso
            }

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()
                val vx = x + cos(angle) * radius
                val vz = z + sin(angle) * radius

                val vertex = MeshVertex(vertices.size, vx, y, vz)
                vertices.add(vertex)

                val zone = when {
                    t < 0.15f -> ZONE_SHOULDERS
                    t < 0.5f -> ZONE_BICEPS
                    else -> ZONE_FOREARMS
                }
                zones[zone]?.vertexIndices?.add(vertex.index)
            }
        }

        // Facce
        for (j in 0 until segments) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                faces.add(MeshFace(curr, next, curr + radialSegs))
                faces.add(MeshFace(next, next + radialSegs, curr + radialSegs))
            }
        }
    }

    private fun generateLeg(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        hipPos: Triple<Float, Float, Float>,
        footY: Float,
        kneeY: Float,
        thighRadius: Float,
        calfRadius: Float,
        quadsMuscle: Float,
        hamstringsMuscle: Float,
        calvesMuscle: Float,
        glutesMuscle: Float,
        isLeft: Boolean
    ) {
        val startIndex = vertices.size
        val segments = 10
        val radialSegs = 12

        val totalLength = hipPos.second - footY

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val y = hipPos.second - t * totalLength

            // Posizione X con leggera curvatura
            val x = hipPos.first

            // Raggio anatomico
            val radius: Float
            val frontBonus: Float
            val backBonus: Float

            when {
                t < 0.1f -> {  // Glutei/anca
                    radius = thighRadius * 1.1f
                    frontBonus = 0f
                    backBonus = glutesMuscle * 0.15f
                }
                t < 0.45f -> {  // Coscia
                    radius = thighRadius * (1f - (t - 0.1f) * 0.3f)
                    frontBonus = quadsMuscle * 0.12f
                    backBonus = hamstringsMuscle * 0.1f
                }
                t < 0.55f -> {  // Ginocchio
                    radius = thighRadius * 0.55f
                    frontBonus = 0f
                    backBonus = 0f
                }
                t < 0.85f -> {  // Polpaccio
                    val calfT = (t - 0.55f) / 0.3f
                    val calfBulge = sin(calfT * PI.toFloat()) * calvesMuscle * 0.1f
                    radius = calfRadius * (1f + calfBulge)
                    frontBonus = 0f
                    backBonus = calvesMuscle * 0.08f
                }
                else -> {  // Caviglia
                    radius = calfRadius * 0.5f
                    frontBonus = 0f
                    backBonus = 0f
                }
            }

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()

                // Forma con bonus muscolare fronte/retro
                val radMod = if (angle < PI) (1f + frontBonus) else (1f + backBonus)
                val vx = x + cos(angle) * radius * radMod
                val vz = sin(angle) * radius * radMod

                val vertex = MeshVertex(vertices.size, vx, y, vz)
                vertices.add(vertex)

                val zone = when {
                    t < 0.1f -> ZONE_GLUTES
                    t < 0.45f -> if (angle < PI) ZONE_QUADS else ZONE_HAMSTRINGS
                    else -> ZONE_CALVES
                }
                zones[zone]?.vertexIndices?.add(vertex.index)
            }
        }

        // Facce
        for (j in 0 until segments) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                faces.add(MeshFace(curr, next, curr + radialSegs))
                faces.add(MeshFace(next, next + radialSegs, curr + radialSegs))
            }
        }
    }

    /**
     * Applica colori zone basati sul development score.
     */
    fun applyZoneColors(mesh: Mesh3D, zones: Map<Int, MuscleZone>) {
        for ((_, zone) in zones) {
            val color = scoreToColor(zone.developmentScore)
            for (idx in zone.vertexIndices) {
                if (idx < mesh.vertices.size) {
                    mesh.vertices[idx].apply {
                        r = color[0]
                        g = color[1]
                        b = color[2]
                    }
                }
            }
        }
    }

    private fun scoreToColor(score: Float): FloatArray {
        return when {
            score < 0.35f -> floatArrayOf(0.9f, 0.3f, 0.2f)      // Rosso
            score < 0.5f -> floatArrayOf(1f, 0.6f, 0.2f)         // Arancione
            score < 0.65f -> floatArrayOf(1f, 0.9f, 0.3f)        // Giallo
            score < 0.8f -> floatArrayOf(0.6f, 0.9f, 0.4f)       // Verde chiaro
            else -> floatArrayOf(0.3f, 0.85f, 0.5f)             // Verde
        }
    }
}
