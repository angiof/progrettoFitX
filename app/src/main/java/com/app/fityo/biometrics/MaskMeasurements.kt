package com.app.fityo.biometrics

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.max
import kotlin.math.roundToInt

object MaskMeasurements {
    private const val ALPHA_THRESHOLD = 128

    fun measureMaskWidth(mask: Bitmap, centerY: Int, band: Int): Float {
        val width = mask.width
        val height = mask.height
        if (width == 0 || height == 0) return 0f

        val startY = (centerY - band).coerceIn(0, height - 1)
        val endY = (centerY + band).coerceIn(0, height - 1)

        var totalWidth = 0
        var rows = 0

        for (y in startY..endY) {
            var minX = Int.MAX_VALUE
            var maxX = Int.MIN_VALUE
            for (x in 0 until width) {
                if (Color.alpha(mask.getPixel(x, y)) > ALPHA_THRESHOLD) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                }
            }
            if (maxX >= minX) {
                totalWidth += (maxX - minX)
                rows++
            }
        }

        return if (rows > 0) totalWidth.toFloat() / rows else 0f
    }

    fun countMaskArea(mask: Bitmap, splitX: Int): Pair<Int, Int> {
        val width = mask.width
        val height = mask.height
        if (width == 0 || height == 0) return 0 to 0
        val safeSplit = splitX.coerceIn(1, width - 1)

        var left = 0
        var right = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (Color.alpha(mask.getPixel(x, y)) > ALPHA_THRESHOLD) {
                    if (x < safeSplit) {
                        left++
                    } else {
                        right++
                    }
                }
            }
        }
        return left to right
    }

    fun averageDepthInMask(mask: Bitmap, depth: DepthResult): Float? {
        val width = mask.width
        val height = mask.height
        if (width == 0 || height == 0) return null

        val step = max(1, minOf(width, height) / 64)
        var sum = 0f
        var count = 0

        for (y in 0 until height step step) {
            val normalizedY = y.toFloat() / height.toFloat()
            for (x in 0 until width step step) {
                if (Color.alpha(mask.getPixel(x, y)) > ALPHA_THRESHOLD) {
                    val normalizedX = x.toFloat() / width.toFloat()
                    sum += depth.getDepthAt(normalizedX, normalizedY)
                    count++
                }
            }
        }

        return if (count > 0) sum / count else null
    }

    fun measureLocalWidth(mask: Bitmap, centerX: Int, centerY: Int, band: Int): Float {
        val width = mask.width
        val height = mask.height
        if (width == 0 || height == 0) return 0f

        val startY = (centerY - band).coerceIn(0, height - 1)
        val endY = (centerY + band).coerceIn(0, height - 1)
        val safeCenterX = centerX.coerceIn(0, width - 1)
        val searchRadius = (width * 0.1f).roundToInt().coerceAtLeast(10)

        var totalWidth = 0
        var rows = 0

        for (y in startY..endY) {
            var x0 = safeCenterX
            if (!isMaskPixel(mask, x0, y)) {
                val nearest = findNearestMaskX(mask, y, x0, searchRadius)
                if (nearest == null) continue
                x0 = nearest
            }

            var left = x0
            while (left > 0 && isMaskPixel(mask, left, y)) {
                left--
            }
            var right = x0
            while (right < width - 1 && isMaskPixel(mask, right, y)) {
                right++
            }
            val localWidth = (right - left - 1).coerceAtLeast(0)
            totalWidth += localWidth
            rows++
        }

        return if (rows > 0) totalWidth.toFloat() / rows else 0f
    }

    private fun isMaskPixel(mask: Bitmap, x: Int, y: Int): Boolean {
        return Color.alpha(mask.getPixel(x, y)) > ALPHA_THRESHOLD
    }

    private fun findNearestMaskX(mask: Bitmap, y: Int, startX: Int, radius: Int): Int? {
        val width = mask.width
        var offset = 1
        while (offset <= radius) {
            val left = startX - offset
            if (left >= 0 && isMaskPixel(mask, left, y)) return left
            val right = startX + offset
            if (right < width && isMaskPixel(mask, right, y)) return right
            offset++
        }
        return null
    }
}
