package com.app.fityo.biometrics

data class BiometricMetrics(
    val adonisRatio: Float?,
    val symmetryIndex: Float?,
    val postureScore: Float?,
    val shoulderTiltPercent: Float?,
    val shoulderWidthCm: Float?,
    val waistWidthCm: Float?,
    val depthScale: Float?,
    val depthSkew: Float?
)
