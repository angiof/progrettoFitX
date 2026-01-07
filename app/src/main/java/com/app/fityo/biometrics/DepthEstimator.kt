package com.app.fityo.biometrics

import android.graphics.Bitmap
import kotlin.math.roundToInt

data class DepthResult(
    val depthMap: FloatArray,
    val width: Int,
    val height: Int,
    val minDepth: Float,
    val maxDepth: Float
) {
    fun getDepthAt(normalizedX: Float, normalizedY: Float): Float {
        if (width == 0 || height == 0) return 0f
        val x = (normalizedX * (width - 1)).roundToInt().coerceIn(0, width - 1)
        val y = (normalizedY * (height - 1)).roundToInt().coerceIn(0, height - 1)
        return depthMap[y * width + x]
    }
}

interface DepthEstimator {
    fun estimateDepth(bitmap: Bitmap): DepthResult?
    fun close()
}
