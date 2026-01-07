package com.app.fityo.biometrics

import android.graphics.Bitmap
import com.app.fityo.dominio.UserProfile
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class RealBodyMeasurements(
    val shoulderWidthCm: Float?,
    val waistWidthCm: Float?,
    val hipWidthCm: Float?,
    val chestWidthCm: Float?,
    val armWidthCm: Float?,
    val thighWidthCm: Float?,
    val shoulderCircumferenceCm: Float?,
    val waistCircumferenceCm: Float?,
    val hipCircumferenceCm: Float?,
    val chestCircumferenceCm: Float?,
    val armCircumferenceCm: Float?,
    val thighCircumferenceCm: Float?
)

data class MeasurementViewInput(
    val bitmap: Bitmap,
    val pose: PoseLandmarkerResult,
    val mask: Bitmap
)

object MultiViewMeasurements {
    fun estimate(
        front: MeasurementViewInput,
        side: MeasurementViewInput,
        back: MeasurementViewInput?,
        profile: UserProfile,
        depthEstimator: DepthEstimator?
    ): RealBodyMeasurements {
        val frontData = analyzeView(front, profile, depthEstimator, preferMaxForLimbs = false)
        val sideData = analyzeView(side, profile, depthEstimator, preferMaxForLimbs = true)
        val backData = back?.let { analyzeView(it, profile, depthEstimator, preferMaxForLimbs = false) }

        val shoulderWidthCm = averageOrNull(listOfNotNull(frontData?.shoulderCm, backData?.shoulderCm))
        val waistWidthCm = averageOrNull(listOfNotNull(frontData?.waistCm, backData?.waistCm))
        val hipWidthCm = averageOrNull(listOfNotNull(frontData?.hipCm, backData?.hipCm))
        val chestWidthCm = averageOrNull(listOfNotNull(frontData?.chestCm, backData?.chestCm))
        val armWidthCm = averageOrNull(listOfNotNull(frontData?.armCm, backData?.armCm))
        val thighWidthCm = averageOrNull(listOfNotNull(frontData?.thighCm, backData?.thighCm))

        val shoulderCircumferenceCm = ellipseCircumference(shoulderWidthCm, sideData?.shoulderCm)
        val waistCircumferenceCm = ellipseCircumference(waistWidthCm, sideData?.waistCm)
        val hipCircumferenceCm = ellipseCircumference(hipWidthCm, sideData?.hipCm)
        val chestCircumferenceCm = ellipseCircumference(chestWidthCm, sideData?.chestCm)
        val armCircumferenceCm = ellipseCircumference(armWidthCm, sideData?.armCm)
        val thighCircumferenceCm = ellipseCircumference(thighWidthCm, sideData?.thighCm)

        return RealBodyMeasurements(
            shoulderWidthCm = shoulderWidthCm,
            waistWidthCm = waistWidthCm,
            hipWidthCm = hipWidthCm,
            chestWidthCm = chestWidthCm,
            armWidthCm = armWidthCm,
            thighWidthCm = thighWidthCm,
            shoulderCircumferenceCm = shoulderCircumferenceCm,
            waistCircumferenceCm = waistCircumferenceCm,
            hipCircumferenceCm = hipCircumferenceCm,
            chestCircumferenceCm = chestCircumferenceCm,
            armCircumferenceCm = armCircumferenceCm,
            thighCircumferenceCm = thighCircumferenceCm
        )
    }

    private data class ViewData(
        val shoulderCm: Float?,
        val waistCm: Float?,
        val hipCm: Float?,
        val chestCm: Float?,
        val armCm: Float?,
        val thighCm: Float?
    )

    private fun analyzeView(
        input: MeasurementViewInput,
        profile: UserProfile,
        depthEstimator: DepthEstimator?,
        preferMaxForLimbs: Boolean
    ): ViewData? {
        val landmarks = input.pose.landmarks().firstOrNull() ?: return null
        if (landmarks.size < 33) return null

        val shoulderY = averageY(landmarks, 11, 12)?.let { (it + 0.02f).coerceIn(0f, 1f) }
        val hipY = averageY(landmarks, 23, 24)
        val waistY = if (shoulderY != null && hipY != null) {
            ((shoulderY + hipY) / 2f).coerceIn(0f, 1f)
        } else {
            null
        }
        val chestY = if (shoulderY != null && hipY != null) {
            (shoulderY + (hipY - shoulderY) * 0.25f).coerceIn(0f, 1f)
        } else {
            null
        }

        val cmPerPx = estimateCmPerPx(input.pose, input.bitmap.height, profile.heightCm)
        val depthScale = estimateDepthScale(input, depthEstimator, shoulderY, hipY)
        val scale = depthScale ?: 1f

        val band = (input.mask.height * 0.01f).roundToInt().coerceAtLeast(2)

        val shoulderPx = shoulderY?.let { y ->
            MaskMeasurements.measureMaskWidth(
                input.mask,
                (y * input.mask.height).roundToInt().coerceIn(0, input.mask.height - 1),
                band
            )
        }
        val waistPx = waistY?.let { y ->
            MaskMeasurements.measureMaskWidth(
                input.mask,
                (y * input.mask.height).roundToInt().coerceIn(0, input.mask.height - 1),
                band
            )
        }
        val hipPx = hipY?.let { y ->
            MaskMeasurements.measureMaskWidth(
                input.mask,
                (y * input.mask.height).roundToInt().coerceIn(0, input.mask.height - 1),
                band
            )
        }
        val chestPx = chestY?.let { y ->
            MaskMeasurements.measureMaskWidth(
                input.mask,
                (y * input.mask.height).roundToInt().coerceIn(0, input.mask.height - 1),
                band
            )
        }

        val leftArmPx = limbWidthPx(landmarks, input.mask, 11, 13, band)
        val rightArmPx = limbWidthPx(landmarks, input.mask, 12, 14, band)
        val armPx = pickLimbWidth(leftArmPx, rightArmPx, preferMaxForLimbs)

        val leftThighPx = limbWidthPx(landmarks, input.mask, 23, 25, band)
        val rightThighPx = limbWidthPx(landmarks, input.mask, 24, 26, band)
        val thighPx = pickLimbWidth(leftThighPx, rightThighPx, preferMaxForLimbs)

        val shoulderCm = toCm(shoulderPx, cmPerPx, scale)
        val waistCm = toCm(waistPx, cmPerPx, scale)
        val hipCm = toCm(hipPx, cmPerPx, scale)
        val chestCm = toCm(chestPx, cmPerPx, scale)
        val armCm = toCm(armPx, cmPerPx, scale)
        val thighCm = toCm(thighPx, cmPerPx, scale)

        return ViewData(
            shoulderCm = shoulderCm,
            waistCm = waistCm,
            hipCm = hipCm,
            chestCm = chestCm,
            armCm = armCm,
            thighCm = thighCm
        )
    }

    private fun estimateCmPerPx(
        pose: PoseLandmarkerResult,
        imageHeight: Int,
        heightCm: Float
    ): Float? {
        val landmarks = pose.landmarks().firstOrNull() ?: return null
        if (landmarks.size < 33) return null
        val topY = listOf(0, 7, 8)
            .mapNotNull { idx -> landmarks.getOrNull(idx)?.y() }
            .minOrNull() ?: return null
        val bottomY = listOf(27, 28)
            .mapNotNull { idx -> landmarks.getOrNull(idx)?.y() }
            .maxOrNull() ?: return null
        val heightNorm = (bottomY - topY).coerceAtLeast(0f)
        if (heightNorm <= 0f) return null
        val heightPx = heightNorm * imageHeight.toFloat()
        return if (heightPx > 0f) heightCm / heightPx else null
    }

    private fun estimateDepthScale(
        input: MeasurementViewInput,
        depthEstimator: DepthEstimator?,
        shoulderY: Float?,
        hipY: Float?
    ): Float? {
        val depth = depthEstimator?.estimateDepth(input.bitmap) ?: return null
        val centerX = averageX(input.pose, 11, 12, 23, 24) ?: return null
        val centerY = averageOrNull(listOfNotNull(shoulderY, hipY)) ?: return null
        val avgDepth = MaskMeasurements.averageDepthInMask(input.mask, depth) ?: return null
        val centerDepth = depth.getDepthAt(centerX, centerY)
        if (centerDepth <= 0f) return null
        return (avgDepth / centerDepth).coerceIn(0.7f, 1.3f)
    }

    private fun ellipseCircumference(widthCm: Float?, depthCm: Float?): Float? {
        if (widthCm == null || depthCm == null || widthCm <= 0f || depthCm <= 0f) return null
        val a = widthCm / 2f
        val b = depthCm / 2f
        val term = sqrt((3f * a + b) * (a + 3f * b))
        return kotlin.math.PI.toFloat() * (3f * (a + b) - term)
    }

    private fun toCm(pixels: Float?, cmPerPx: Float?, scale: Float): Float? {
        if (pixels == null || cmPerPx == null) return null
        return pixels * cmPerPx * scale
    }

    private fun averageY(landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>, left: Int, right: Int): Float? {
        val leftY = landmarks.getOrNull(left)?.y()
        val rightY = landmarks.getOrNull(right)?.y()
        return averageOrNull(listOfNotNull(leftY, rightY))
    }

    private fun limbWidthPx(
        landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        mask: Bitmap,
        startIndex: Int,
        endIndex: Int,
        band: Int
    ): Float? {
        val start = landmarks.getOrNull(startIndex) ?: return null
        val end = landmarks.getOrNull(endIndex) ?: return null
        val centerX = ((start.x() + end.x()) / 2f).coerceIn(0f, 1f)
        val centerY = ((start.y() + end.y()) / 2f).coerceIn(0f, 1f)
        return MaskMeasurements.measureLocalWidth(
            mask,
            (centerX * mask.width).roundToInt().coerceIn(0, mask.width - 1),
            (centerY * mask.height).roundToInt().coerceIn(0, mask.height - 1),
            band
        )
    }

    private fun pickLimbWidth(left: Float?, right: Float?, preferMax: Boolean): Float? {
        val values = listOfNotNull(left, right).filter { it > 0f }
        if (values.isEmpty()) return null
        return if (preferMax) values.maxOrNull() else values.average().toFloat()
    }

    private fun averageX(pose: PoseLandmarkerResult, vararg indices: Int): Float? {
        val landmarks = pose.landmarks().firstOrNull() ?: return null
        val values = indices.map { idx -> landmarks.getOrNull(idx)?.x() }.filterNotNull()
        return averageOrNull(values)
    }

    private fun averageOrNull(values: List<Float>): Float? {
        return if (values.isNotEmpty()) values.sum() / values.size.toFloat() else null
    }
}
