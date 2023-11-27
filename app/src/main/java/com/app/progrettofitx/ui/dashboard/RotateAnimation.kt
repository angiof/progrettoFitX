package com.app.progrettofitx.ui.dashboard

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.github.mikephil.charting.charts.PieChart

class RotateAnimationx(private val chart: PieChart, private val newAngle: Float) : Animation() {
    private val oldAngle = chart.rotationAngle

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime)
        chart.rotationAngle = angle
        chart.invalidate()
    }
}