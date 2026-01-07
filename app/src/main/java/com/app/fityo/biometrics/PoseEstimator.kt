package com.app.fityo.biometrics

import android.graphics.Bitmap

data class PoseKeypoint(
    val x: Float,
    val y: Float,
    val score: Float
)

data class PoseEstimate(
    val keypoints: List<PoseKeypoint>
)

interface PoseEstimator {
    fun estimatePose(bitmap: Bitmap): PoseEstimate?
    fun close()
}
