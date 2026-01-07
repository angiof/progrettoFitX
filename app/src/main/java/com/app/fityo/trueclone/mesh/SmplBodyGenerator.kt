package com.app.fityo.trueclone.mesh

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Generatore di mesh corpo umano parametrico basato su SMPL.
 * Genera una mesh 3D del corpo umano che può essere deformata
 * in base ai parametri di forma (shape parameters).
 *
 * La mesh è composta da:
 * - Torso (petto, addome, schiena)
 * - Braccia (spalle, bicipiti, avambracci, mani)
 * - Gambe (glutei, cosce, polpacci, piedi)
 * - Testa/collo (semplificato)
 */
class SmplBodyGenerator {

    companion object {
        // Risoluzione mesh (numero di segmenti per le forme cilindriche)
        private const val RADIAL_SEGMENTS = 16
        private const val HEIGHT_SEGMENTS = 8

        // Indici zone muscolari per vertex coloring
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
    }

    /**
     * Zona muscolare con informazioni per vertex coloring.
     */
    data class MuscleZone(
        val id: Int,
        val name: String,
        val vertexIndices: MutableList<Int> = mutableListOf(),
        var developmentScore: Float = 0.5f  // 0=sottosviluppato, 1=molto sviluppato
    )

    /**
     * Risultato della generazione mesh.
     */
    data class GeneratedBody(
        val mesh: Mesh3D,
        val zones: Map<Int, MuscleZone>,
        val shapeParams: ShapeParameters
    )

    private val zones = mutableMapOf<Int, MuscleZone>()

    init {
        // Inizializza zone muscolari
        zones[ZONE_CHEST] = MuscleZone(ZONE_CHEST, "Petto")
        zones[ZONE_ABS] = MuscleZone(ZONE_ABS, "Addominali")
        zones[ZONE_BACK] = MuscleZone(ZONE_BACK, "Schiena")
        zones[ZONE_SHOULDERS] = MuscleZone(ZONE_SHOULDERS, "Spalle")
        zones[ZONE_BICEPS] = MuscleZone(ZONE_BICEPS, "Bicipiti")
        zones[ZONE_FOREARMS] = MuscleZone(ZONE_FOREARMS, "Avambracci")
        zones[ZONE_GLUTES] = MuscleZone(ZONE_GLUTES, "Glutei")
        zones[ZONE_QUADS] = MuscleZone(ZONE_QUADS, "Quadricipiti")
        zones[ZONE_HAMSTRINGS] = MuscleZone(ZONE_HAMSTRINGS, "Femorali")
        zones[ZONE_CALVES] = MuscleZone(ZONE_CALVES, "Polpacci")
    }

    /**
     * Genera una mesh corpo completo basata sui parametri di forma.
     */
    fun generateBody(params: ShapeParameters): GeneratedBody {
        // Reset zone indices
        zones.values.forEach { it.vertexIndices.clear() }

        val vertices = mutableListOf<MeshVertex>()
        val faces = mutableListOf<MeshFace>()

        // Calcola dimensioni base dai parametri
        val scale = params.heightCm / 175f  // Scala rispetto a altezza media
        val muscleScale = 1f + (params.beta8 * 0.1f)  // Scala muscolare
        val fatScale = 1f + (params.beta9 * 0.08f)  // Scala grasso

        // Dimensioni normalizzate (in unità, ~1 unità = 1m per default)
        val torsoHeight = 0.55f * scale
        val torsoWidth = (params.shoulderWidthCm / 100f) * scale * muscleScale
        val hipWidth = (params.hipWidthCm / 100f) * scale * fatScale
        val legLength = (params.legLengthCm / 100f) * scale
        val armLength = (params.armLengthCm / 100f) * scale

        // Genera torso
        val torsoOffset = generateTorso(
            vertices, faces,
            torsoHeight, torsoWidth, hipWidth,
            params.chestMuscle, params.absMuscle, params.backMuscle
        )

        // Genera braccia (simmetriche)
        generateArm(
            vertices, faces,
            torsoOffset, armLength, torsoWidth * 0.15f,
            params.shoulderMuscle, params.armMuscle,
            isLeft = true
        )
        generateArm(
            vertices, faces,
            torsoOffset, armLength, torsoWidth * 0.15f,
            params.shoulderMuscle, params.armMuscle,
            isLeft = false
        )

        // Genera gambe (simmetriche)
        generateLeg(
            vertices, faces,
            torsoOffset - torsoHeight, legLength, hipWidth * 0.2f,
            params.glutesMuscle, params.quadsMuscle, params.hamstringsMuscle, params.calvesMuscle,
            isLeft = true
        )
        generateLeg(
            vertices, faces,
            torsoOffset - torsoHeight, legLength, hipWidth * 0.2f,
            params.glutesMuscle, params.quadsMuscle, params.hamstringsMuscle, params.calvesMuscle,
            isLeft = false
        )

        // Genera testa/collo (semplificato)
        generateHead(vertices, faces, torsoOffset + 0.05f, scale * 0.1f)

        // Crea mesh
        val mesh = Mesh3D(vertices, faces, "trueclone_body")
        mesh.recalculateNormals()

        // Aggiorna punteggi zone dai parametri
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

    /**
     * Genera il torso (petto, addome, schiena).
     * Ritorna l'offset Y della parte superiore.
     */
    private fun generateTorso(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        height: Float,
        shoulderWidth: Float,
        hipWidth: Float,
        chestMuscle: Float,
        absMuscle: Float,
        backMuscle: Float
    ): Float {
        val startIndex = vertices.size
        val segments = HEIGHT_SEGMENTS
        val radialSegs = RADIAL_SEGMENTS

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val y = height * (1f - t) - height / 2  // Da top a bottom

            // Interpola larghezza spalle→fianchi
            val width = shoulderWidth * (1f - t) + hipWidth * t

            // Profondità variabile (petto più profondo)
            val chestDepth = width * 0.6f * (1f + chestMuscle * 0.3f)
            val backDepth = width * 0.5f * (1f + backMuscle * 0.2f)

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()

                // Forma ellittica con variazioni muscolari
                var x = cos(angle) * width / 2
                var z: Float

                // Distingui fronte/retro per muscoli
                if (angle < PI) {
                    // Fronte (petto/addome)
                    z = sin(angle) * chestDepth / 2
                    val muscleEffect = if (t < 0.4f) chestMuscle else absMuscle
                    z *= (1f + muscleEffect * 0.15f)
                } else {
                    // Retro (schiena)
                    z = sin(angle) * backDepth / 2
                    z *= (1f + backMuscle * 0.1f)
                }

                val vertex = MeshVertex(
                    index = vertices.size,
                    x = x,
                    y = y,
                    z = z,
                    u = i.toFloat() / radialSegs,
                    v = t
                )
                vertices.add(vertex)

                // Assegna a zona muscolare
                val zone = when {
                    angle < PI * 0.4f || angle > PI * 1.6f -> {
                        if (t < 0.4f) ZONE_CHEST else ZONE_ABS
                    }
                    angle > PI * 0.6f && angle < PI * 1.4f -> ZONE_BACK
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
                val currDown = curr + radialSegs
                val nextDown = next + radialSegs

                faces.add(MeshFace(curr, currDown, next))
                faces.add(MeshFace(next, currDown, nextDown))
            }
        }

        return height / 2
    }

    /**
     * Genera un braccio completo.
     */
    private fun generateArm(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        shoulderY: Float,
        length: Float,
        baseRadius: Float,
        shoulderMuscle: Float,
        armMuscle: Float,
        isLeft: Boolean
    ) {
        val startIndex = vertices.size
        val segments = 6  // Segmenti lungo il braccio
        val radialSegs = 12

        val xOffset = if (isLeft) -0.25f else 0.25f  // Posizione laterale

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val y = shoulderY - t * length

            // Raggio variabile lungo il braccio
            val radius = when {
                t < 0.15f -> baseRadius * (1f + shoulderMuscle * 0.4f)  // Spalla
                t < 0.5f -> baseRadius * (1f + armMuscle * 0.3f)  // Bicipite
                t < 0.8f -> baseRadius * 0.8f  // Avambraccio
                else -> baseRadius * 0.4f  // Polso/mano
            }

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()
                val x = xOffset + cos(angle) * radius
                val z = sin(angle) * radius

                val vertex = MeshVertex(
                    index = vertices.size,
                    x = x,
                    y = y,
                    z = z,
                    u = i.toFloat() / radialSegs,
                    v = t
                )
                vertices.add(vertex)

                // Assegna zona
                val zone = when {
                    t < 0.15f -> ZONE_SHOULDERS
                    t < 0.5f -> ZONE_BICEPS
                    else -> ZONE_FOREARMS
                }
                zones[zone]?.vertexIndices?.add(vertex.index)
            }
        }

        // Genera facce
        for (j in 0 until segments) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                val currDown = curr + radialSegs
                val nextDown = next + radialSegs

                faces.add(MeshFace(curr, currDown, next))
                faces.add(MeshFace(next, currDown, nextDown))
            }
        }
    }

    /**
     * Genera una gamba completa.
     */
    private fun generateLeg(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        hipY: Float,
        length: Float,
        baseRadius: Float,
        glutesMuscle: Float,
        quadsMuscle: Float,
        hamstringsMuscle: Float,
        calvesMuscle: Float,
        isLeft: Boolean
    ) {
        val startIndex = vertices.size
        val segments = 8
        val radialSegs = 12

        val xOffset = if (isLeft) -0.08f else 0.08f

        for (j in 0..segments) {
            val t = j.toFloat() / segments
            val y = hipY - t * length

            // Raggio variabile lungo la gamba
            val frontRadius: Float
            val backRadius: Float

            when {
                t < 0.1f -> {
                    // Glutei/anca
                    frontRadius = baseRadius * (1f + quadsMuscle * 0.2f)
                    backRadius = baseRadius * (1.2f + glutesMuscle * 0.4f)
                }
                t < 0.5f -> {
                    // Coscia
                    frontRadius = baseRadius * (1.1f + quadsMuscle * 0.3f)
                    backRadius = baseRadius * (1f + hamstringsMuscle * 0.25f)
                }
                t < 0.55f -> {
                    // Ginocchio
                    frontRadius = baseRadius * 0.7f
                    backRadius = baseRadius * 0.7f
                }
                t < 0.8f -> {
                    // Polpaccio
                    frontRadius = baseRadius * 0.6f
                    backRadius = baseRadius * (0.8f + calvesMuscle * 0.3f)
                }
                else -> {
                    // Caviglia/piede
                    frontRadius = baseRadius * 0.35f
                    backRadius = baseRadius * 0.4f
                }
            }

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()

                // Usa raggio diverso per fronte/retro
                val radius = if (angle < PI) frontRadius else backRadius
                val x = xOffset + cos(angle) * radius
                val z = sin(angle) * radius

                val vertex = MeshVertex(
                    index = vertices.size,
                    x = x,
                    y = y,
                    z = z,
                    u = i.toFloat() / radialSegs,
                    v = t
                )
                vertices.add(vertex)

                // Assegna zona
                val zone = when {
                    t < 0.1f -> ZONE_GLUTES
                    t < 0.5f -> if (angle < PI) ZONE_QUADS else ZONE_HAMSTRINGS
                    t < 0.8f -> ZONE_CALVES
                    else -> ZONE_CALVES  // Piede = polpaccio per semplicità
                }
                zones[zone]?.vertexIndices?.add(vertex.index)
            }
        }

        // Genera facce
        for (j in 0 until segments) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                val currDown = curr + radialSegs
                val nextDown = next + radialSegs

                faces.add(MeshFace(curr, currDown, next))
                faces.add(MeshFace(next, currDown, nextDown))
            }
        }
    }

    /**
     * Genera testa e collo semplificati.
     */
    private fun generateHead(
        vertices: MutableList<MeshVertex>,
        faces: MutableList<MeshFace>,
        neckY: Float,
        radius: Float
    ) {
        val startIndex = vertices.size

        // Collo (cilindro)
        val neckHeight = radius * 0.8f
        val neckRadius = radius * 0.5f
        val radialSegs = 10

        for (j in 0..2) {
            val t = j.toFloat() / 2
            val y = neckY + t * neckHeight

            for (i in 0 until radialSegs) {
                val angle = (i.toFloat() / radialSegs) * 2f * PI.toFloat()
                val x = cos(angle) * neckRadius
                val z = sin(angle) * neckRadius

                vertices.add(MeshVertex(
                    index = vertices.size,
                    x = x,
                    y = y,
                    z = z
                ))
            }
        }

        // Facce collo
        for (j in 0..1) {
            for (i in 0 until radialSegs) {
                val curr = startIndex + j * radialSegs + i
                val next = startIndex + j * radialSegs + (i + 1) % radialSegs
                val currUp = curr + radialSegs
                val nextUp = next + radialSegs

                faces.add(MeshFace(curr, next, currUp))
                faces.add(MeshFace(next, nextUp, currUp))
            }
        }

        // Testa (sfera semplificata - ellissoide)
        val headStartIndex = vertices.size
        val headY = neckY + neckHeight + radius * 0.6f
        val headRadiusX = radius * 0.7f
        val headRadiusY = radius * 0.9f
        val headRadiusZ = radius * 0.8f
        val latSegs = 6
        val lonSegs = 10

        for (lat in 0..latSegs) {
            val theta = (lat.toFloat() / latSegs) * PI.toFloat()
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)

            for (lon in 0 until lonSegs) {
                val phi = (lon.toFloat() / lonSegs) * 2f * PI.toFloat()
                val x = headRadiusX * sinTheta * cos(phi)
                val y = headY + headRadiusY * cosTheta
                val z = headRadiusZ * sinTheta * sin(phi)

                vertices.add(MeshVertex(
                    index = vertices.size,
                    x = x,
                    y = y,
                    z = z
                ))
            }
        }

        // Facce testa
        for (lat in 0 until latSegs) {
            for (lon in 0 until lonSegs) {
                val curr = headStartIndex + lat * lonSegs + lon
                val next = headStartIndex + lat * lonSegs + (lon + 1) % lonSegs
                val currUp = curr + lonSegs
                val nextUp = next + lonSegs

                if (lat > 0) {
                    faces.add(MeshFace(curr, next, currUp))
                }
                if (lat < latSegs - 1) {
                    faces.add(MeshFace(next, nextUp, currUp))
                }
            }
        }
    }

    /**
     * Applica colori alle zone muscolari basati sul punteggio di sviluppo.
     * Verde = sviluppato, Giallo = medio, Rosso = da lavorare
     */
    fun applyZoneColors(mesh: Mesh3D, zones: Map<Int, MuscleZone>) {
        for ((_, zone) in zones) {
            val color = scoreToColor(zone.developmentScore)
            for (vertexIndex in zone.vertexIndices) {
                if (vertexIndex < mesh.vertices.size) {
                    mesh.vertices[vertexIndex].apply {
                        r = color[0]
                        g = color[1]
                        b = color[2]
                    }
                }
            }
        }
    }

    /**
     * Converte un punteggio (0-1) in colore RGB.
     * 0 = Rosso (da lavorare), 0.5 = Giallo (medio), 1 = Verde (sviluppato)
     */
    private fun scoreToColor(score: Float): FloatArray {
        return when {
            score < 0.3f -> {
                // Rosso → Arancione
                val t = score / 0.3f
                floatArrayOf(1f, t * 0.5f, 0f)
            }
            score < 0.6f -> {
                // Arancione → Giallo
                val t = (score - 0.3f) / 0.3f
                floatArrayOf(1f, 0.5f + t * 0.5f, 0f)
            }
            score < 0.8f -> {
                // Giallo → Verde chiaro
                val t = (score - 0.6f) / 0.2f
                floatArrayOf(1f - t * 0.5f, 1f, t * 0.3f)
            }
            else -> {
                // Verde chiaro → Verde
                val t = (score - 0.8f) / 0.2f
                floatArrayOf(0.5f - t * 0.3f, 1f - t * 0.2f, 0.3f + t * 0.2f)
            }
        }
    }

    /**
     * Calcola la differenza tra due mesh e genera colori heatmap.
     * Verde = crescita, Giallo = invariato, Rosso = perdita
     */
    fun applyComparisonHeatmap(
        currentMesh: Mesh3D,
        previousMesh: Mesh3D,
        maxDifference: Float = 0.05f  // 5cm massimo per saturare il colore
    ) {
        if (currentMesh.vertices.size != previousMesh.vertices.size) {
            android.util.Log.w("SmplBodyGenerator", "Mesh size mismatch for comparison")
            return
        }

        for (i in currentMesh.vertices.indices) {
            val curr = currentMesh.vertices[i]
            val prev = previousMesh.vertices[i]

            // Calcola differenza di "volume" (distanza dall'asse Y)
            val currRadius = sqrt(curr.x * curr.x + curr.z * curr.z)
            val prevRadius = sqrt(prev.x * prev.x + prev.z * prev.z)
            val diff = currRadius - prevRadius

            // Normalizza differenza
            val normalizedDiff = (diff / maxDifference).coerceIn(-1f, 1f)

            // Colore: Verde (crescita) → Giallo (invariato) → Rosso (perdita)
            val color = when {
                normalizedDiff > 0.1f -> {
                    // Crescita → Verde
                    val t = ((normalizedDiff - 0.1f) / 0.9f).coerceIn(0f, 1f)
                    floatArrayOf(0.2f - t * 0.2f, 0.8f + t * 0.2f, 0.2f)
                }
                normalizedDiff < -0.1f -> {
                    // Perdita → Rosso/Arancione
                    val t = ((-normalizedDiff - 0.1f) / 0.9f).coerceIn(0f, 1f)
                    floatArrayOf(0.8f + t * 0.2f, 0.6f - t * 0.4f, 0.2f)
                }
                else -> {
                    // Invariato → Giallo/Neutro
                    floatArrayOf(0.9f, 0.9f, 0.3f)
                }
            }

            curr.r = color[0]
            curr.g = color[1]
            curr.b = color[2]
        }
    }
}
