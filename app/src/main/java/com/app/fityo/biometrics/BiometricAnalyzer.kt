package com.app.fityo.biometrics

import android.graphics.Bitmap
import com.app.fityo.dominio.UserProfile
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class BiometricAnalyzer(
    private val poseEstimator: PoseEstimator,
    private val depthEstimator: DepthEstimator,
    private val minScore: Float = 0.3f
) {
    fun analyze(
        bitmap: Bitmap,
        segmentationMask: Bitmap?,
        profile: UserProfile
    ): BiometricMetrics? {
        val pose = poseEstimator.estimatePose(bitmap)
        val depth = depthEstimator.estimateDepth(bitmap)

        val shoulders = pose?.let {
            it.point(MoveNetKeypoints.LEFT_SHOULDER, minScore) to
                it.point(MoveNetKeypoints.RIGHT_SHOULDER, minScore)
        }

        val hips = pose?.let {
            it.point(MoveNetKeypoints.LEFT_HIP, minScore) to
                it.point(MoveNetKeypoints.RIGHT_HIP, minScore)
        }

        val (leftShoulder, rightShoulder) = shoulders ?: (null to null)
        val (leftHip, rightHip) = hips ?: (null to null)

        val centerX = averageOrNull(listOfNotNull(
            leftShoulder?.x,
            rightShoulder?.x,
            leftHip?.x,
            rightHip?.x
        ))

        val shoulderWidthPx = if (leftShoulder != null && rightShoulder != null) {
            abs(rightShoulder.x - leftShoulder.x) * bitmap.width
        } else null

        val (waistWidthPx, depthScale, depthSkew) = if (
            segmentationMask != null && leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null
        ) {
            val shoulderY = ((leftShoulder.y + rightShoulder.y) / 2f * segmentationMask.height)
                .toInt()
                .coerceIn(0, segmentationMask.height - 1)
            val hipY = ((leftHip.y + rightHip.y) / 2f * segmentationMask.height)
                .toInt()
                .coerceIn(0, segmentationMask.height - 1)
            val waistY = ((shoulderY + hipY) / 2f)
                .coerceIn(0f, (segmentationMask.height - 1).toFloat())
                .roundToInt()
            val band = (segmentationMask.height * 0.01f).toInt().coerceAtLeast(2)
            val widthPx = MaskMeasurements.measureMaskWidth(segmentationMask, waistY, band)

            val (scale, skew) = depth?.let { depthResult ->
                val avgDepth = MaskMeasurements.averageDepthInMask(segmentationMask, depthResult)
                val centerDepth = centerX?.let { cx ->
                    val cy = ((leftShoulder.y + rightShoulder.y + leftHip.y + rightHip.y) / 4f)
                    depthResult.getDepthAt(cx, cy)
                }
                val scaleValue = if (avgDepth != null && centerDepth != null && centerDepth > 0f) {
                    (avgDepth / centerDepth).coerceIn(0.7f, 1.3f)
                } else {
                    null
                }

                val leftDepth = depthResult.getDepthAt(leftShoulder.x, leftShoulder.y)
                val rightDepth = depthResult.getDepthAt(rightShoulder.x, rightShoulder.y)
                val skewValue = abs(leftDepth - rightDepth)
                scaleValue to skewValue
            } ?: (null to null)

            Triple(widthPx, scale, skew)
        } else {
            Triple(null, null, null)
        }

        val adonisRatio = if (shoulderWidthPx != null && waistWidthPx != null && waistWidthPx > 0f) {
            val correctedWaist = waistWidthPx * (depthScale ?: 1f)
            shoulderWidthPx / correctedWaist
        } else {
            null
        }

        val symmetryIndex = if (segmentationMask != null) {
            val splitX = ((centerX ?: 0.5f) * segmentationMask.width)
                .toInt()
                .coerceIn(1, segmentationMask.width - 1)
            val (leftArea, rightArea) = MaskMeasurements.countMaskArea(segmentationMask, splitX)
            val maxSide = max(leftArea, rightArea)
            if (maxSide > 0) 1f - abs(leftArea - rightArea).toFloat() / maxSide.toFloat() else null
        } else {
            null
        }

        val postureScore = pose?.let { estimatePostureScore(it, minScore) }

        val shoulderTiltPercent = if (leftShoulder != null && rightShoulder != null) {
            abs(leftShoulder.y - rightShoulder.y) * 100f
        } else {
            null
        }

        val (shoulderWidthCm, waistWidthCm) = if (pose != null && shoulderWidthPx != null && waistWidthPx != null) {
            val heightPx = estimateHeightPx(pose, bitmap.height, minScore)
            if (heightPx > 0f) {
                val cmPerPx = profile.heightCm / heightPx
                val scale = depthScale ?: 1f
                (shoulderWidthPx * scale * cmPerPx) to (waistWidthPx * scale * cmPerPx)
            } else {
                null to null
            }
        } else {
            null to null
        }

        return BiometricMetrics(
            adonisRatio = adonisRatio,
            symmetryIndex = symmetryIndex,
            postureScore = postureScore,
            shoulderTiltPercent = shoulderTiltPercent,
            shoulderWidthCm = shoulderWidthCm,
            waistWidthCm = waistWidthCm,
            depthScale = depthScale,
            depthSkew = depthSkew
        )
    }

    private fun estimatePostureScore(pose: PoseEstimate, minScore: Float): Float? {
        val leftEar = pose.point(MoveNetKeypoints.LEFT_EAR, minScore)
        val rightEar = pose.point(MoveNetKeypoints.RIGHT_EAR, minScore)
        val leftShoulder = pose.point(MoveNetKeypoints.LEFT_SHOULDER, minScore)
        val rightShoulder = pose.point(MoveNetKeypoints.RIGHT_SHOULDER, minScore)
        val leftHip = pose.point(MoveNetKeypoints.LEFT_HIP, minScore)
        val rightHip = pose.point(MoveNetKeypoints.RIGHT_HIP, minScore)

        val earCenter = centerX(leftEar, rightEar) ?: return null
        val shoulderCenter = centerX(leftShoulder, rightShoulder) ?: return null
        val hipCenter = centerX(leftHip, rightHip) ?: return null
        val reference = listOf(earCenter, shoulderCenter, hipCenter).average().toFloat()

        val deviation = (abs(earCenter - reference) +
            abs(shoulderCenter - reference) +
            abs(hipCenter - reference)) / 3f

        return (1f - deviation).coerceIn(0f, 1f)
    }

    private fun estimateHeightPx(pose: PoseEstimate, imageHeight: Int, minScore: Float): Float {
        val nose = pose.point(MoveNetKeypoints.NOSE, minScore)
        val leftEar = pose.point(MoveNetKeypoints.LEFT_EAR, minScore)
        val rightEar = pose.point(MoveNetKeypoints.RIGHT_EAR, minScore)
        val leftAnkle = pose.point(MoveNetKeypoints.LEFT_ANKLE, minScore)
        val rightAnkle = pose.point(MoveNetKeypoints.RIGHT_ANKLE, minScore)

        val top = listOfNotNull(nose?.y, leftEar?.y, rightEar?.y).minOrNull() ?: return 0f
        val bottom = listOfNotNull(leftAnkle?.y, rightAnkle?.y).maxOrNull() ?: return 0f
        val heightNorm = max(0f, bottom - top)
        return heightNorm * imageHeight
    }

    private fun PoseEstimate.point(index: Int, minScore: Float): PoseKeypoint? {
        val keypoint = keypoints.getOrNull(index) ?: return null
        return if (keypoint.score >= minScore) keypoint else null
    }

    private fun centerX(left: PoseKeypoint?, right: PoseKeypoint?): Float? {
        return when {
            left != null && right != null -> (left.x + right.x) / 2f
            left != null -> left.x
            right != null -> right.x
            else -> null
        }
    }

    private fun averageOrNull(values: List<Float>): Float? {
        return if (values.isNotEmpty()) values.sum() / values.size.toFloat() else null
    }

    object MoveNetKeypoints {
        const val NOSE = 0
        const val LEFT_EAR = 3
        const val RIGHT_EAR = 4
        const val LEFT_SHOULDER = 5
        const val RIGHT_SHOULDER = 6
        const val LEFT_HIP = 11
        const val RIGHT_HIP = 12
        const val LEFT_ANKLE = 15
        const val RIGHT_ANKLE = 16
    }
}
