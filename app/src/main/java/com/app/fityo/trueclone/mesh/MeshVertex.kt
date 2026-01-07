package com.app.fityo.trueclone.mesh

/**
 * Rappresenta un vertice 3D della mesh con tutte le sue proprietà.
 */
data class MeshVertex(
    val index: Int,
    var x: Float,
    var y: Float,
    var z: Float,
    var nx: Float = 0f,  // Normal X
    var ny: Float = 0f,  // Normal Y
    var nz: Float = 0f,  // Normal Z
    var u: Float = 0f,   // UV texture coordinate U
    var v: Float = 0f,   // UV texture coordinate V
    var r: Float = 1f,   // Vertex color R
    var g: Float = 1f,   // Vertex color G
    var b: Float = 1f,   // Vertex color B
    var a: Float = 1f    // Vertex color Alpha
) {
    /**
     * Posizione come array [x, y, z].
     */
    val position: FloatArray
        get() = floatArrayOf(x, y, z)

    /**
     * Normale come array [nx, ny, nz].
     */
    val normal: FloatArray
        get() = floatArrayOf(nx, ny, nz)

    /**
     * Colore come array [r, g, b, a].
     */
    val color: FloatArray
        get() = floatArrayOf(r, g, b, a)

    /**
     * Calcola la distanza da un altro vertice.
     */
    fun distanceTo(other: MeshVertex): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
    }

    /**
     * Interpola linearmente verso un altro vertice.
     */
    fun lerp(other: MeshVertex, t: Float): MeshVertex {
        return MeshVertex(
            index = index,
            x = x + (other.x - x) * t,
            y = y + (other.y - y) * t,
            z = z + (other.z - z) * t,
            nx = nx + (other.nx - nx) * t,
            ny = ny + (other.ny - ny) * t,
            nz = nz + (other.nz - nz) * t,
            u = u,
            v = v,
            r = r + (other.r - r) * t,
            g = g + (other.g - g) * t,
            b = b + (other.b - b) * t,
            a = a + (other.a - a) * t
        )
    }

    /**
     * Crea una copia del vertice.
     */
    fun copy(): MeshVertex = MeshVertex(
        index, x, y, z, nx, ny, nz, u, v, r, g, b, a
    )

    /**
     * Applica una trasformazione di scala.
     */
    fun scale(sx: Float, sy: Float, sz: Float): MeshVertex {
        return copy().apply {
            x *= sx
            y *= sy
            z *= sz
        }
    }

    /**
     * Applica una traslazione.
     */
    fun translate(tx: Float, ty: Float, tz: Float): MeshVertex {
        return copy().apply {
            x += tx
            y += ty
            z += tz
        }
    }
}

/**
 * Rappresenta una faccia triangolare della mesh.
 */
data class MeshFace(
    val v1: Int,  // Indice vertice 1
    val v2: Int,  // Indice vertice 2
    val v3: Int   // Indice vertice 3
) {
    /**
     * Calcola la normale della faccia.
     */
    fun calculateNormal(vertices: List<MeshVertex>): FloatArray {
        val p1 = vertices[v1]
        val p2 = vertices[v2]
        val p3 = vertices[v3]

        // Edge vectors
        val e1x = p2.x - p1.x
        val e1y = p2.y - p1.y
        val e1z = p2.z - p1.z

        val e2x = p3.x - p1.x
        val e2y = p3.y - p1.y
        val e2z = p3.z - p1.z

        // Cross product
        var nx = e1y * e2z - e1z * e2y
        var ny = e1z * e2x - e1x * e2z
        var nz = e1x * e2y - e1y * e2x

        // Normalize
        val length = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
        if (length > 0) {
            nx /= length
            ny /= length
            nz /= length
        }

        return floatArrayOf(nx, ny, nz)
    }
}

/**
 * Rappresenta una mesh 3D completa.
 */
data class Mesh3D(
    val vertices: MutableList<MeshVertex>,
    val faces: MutableList<MeshFace>,
    var name: String = "mesh"
) {
    /**
     * Numero di vertici.
     */
    val vertexCount: Int get() = vertices.size

    /**
     * Numero di facce.
     */
    val faceCount: Int get() = faces.size

    /**
     * Calcola il bounding box della mesh.
     */
    fun getBoundingBox(): BoundingBox {
        if (vertices.isEmpty()) return BoundingBox(0f, 0f, 0f, 0f, 0f, 0f)

        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var maxZ = Float.MIN_VALUE

        for (v in vertices) {
            if (v.x < minX) minX = v.x
            if (v.y < minY) minY = v.y
            if (v.z < minZ) minZ = v.z
            if (v.x > maxX) maxX = v.x
            if (v.y > maxY) maxY = v.y
            if (v.z > maxZ) maxZ = v.z
        }

        return BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
    }

    /**
     * Ricalcola le normali per tutti i vertici.
     */
    fun recalculateNormals() {
        // Reset normals
        for (v in vertices) {
            v.nx = 0f
            v.ny = 0f
            v.nz = 0f
        }

        // Accumulate face normals
        for (face in faces) {
            val normal = face.calculateNormal(vertices)
            vertices[face.v1].nx += normal[0]
            vertices[face.v1].ny += normal[1]
            vertices[face.v1].nz += normal[2]
            vertices[face.v2].nx += normal[0]
            vertices[face.v2].ny += normal[1]
            vertices[face.v2].nz += normal[2]
            vertices[face.v3].nx += normal[0]
            vertices[face.v3].ny += normal[1]
            vertices[face.v3].nz += normal[2]
        }

        // Normalize
        for (v in vertices) {
            val length = kotlin.math.sqrt(v.nx * v.nx + v.ny * v.ny + v.nz * v.nz)
            if (length > 0) {
                v.nx /= length
                v.ny /= length
                v.nz /= length
            }
        }
    }

    /**
     * Crea una copia profonda della mesh.
     */
    fun deepCopy(): Mesh3D {
        return Mesh3D(
            vertices = vertices.map { it.copy() }.toMutableList(),
            faces = faces.toMutableList(),
            name = name
        )
    }

    /**
     * Centra la mesh sull'origine.
     */
    fun centerOnOrigin() {
        val bbox = getBoundingBox()
        val cx = (bbox.minX + bbox.maxX) / 2
        val cy = (bbox.minY + bbox.maxY) / 2
        val cz = (bbox.minZ + bbox.maxZ) / 2

        for (v in vertices) {
            v.x -= cx
            v.y -= cy
            v.z -= cz
        }
    }

    /**
     * Normalizza la mesh per adattarla in un cubo unitario.
     */
    fun normalizeScale() {
        val bbox = getBoundingBox()
        val sizeX = bbox.maxX - bbox.minX
        val sizeY = bbox.maxY - bbox.minY
        val sizeZ = bbox.maxZ - bbox.minZ
        val maxSize = maxOf(sizeX, sizeY, sizeZ)

        if (maxSize > 0) {
            val scale = 1f / maxSize
            for (v in vertices) {
                v.x *= scale
                v.y *= scale
                v.z *= scale
            }
        }
    }
}

/**
 * Bounding box 3D.
 */
data class BoundingBox(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
    val depth: Float get() = maxZ - minZ
    val centerX: Float get() = (minX + maxX) / 2
    val centerY: Float get() = (minY + maxY) / 2
    val centerZ: Float get() = (minZ + maxZ) / 2
}
