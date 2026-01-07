package com.app.fityo.biometrics

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter

class MoveNetPoseEstimator(
    context: Context,
    modelPath: String = MODEL_PATH,
    numThreads: Int = 2
) : PoseEstimator {
    private val interpreter: Interpreter
    private val inputWidth: Int
    private val inputHeight: Int
    private val inputType: DataType
    private val outputShape: IntArray

    init {
        val modelBuffer = TfliteUtils.loadModelFile(context, modelPath)
        val options = Interpreter.Options().setNumThreads(numThreads)
        interpreter = Interpreter(modelBuffer, options)
        val inputTensor = interpreter.getInputTensor(0)
        val inputShape = inputTensor.shape()
        inputHeight = inputShape[1]
        inputWidth = inputShape[2]
        inputType = inputTensor.dataType()
        outputShape = interpreter.getOutputTensor(0).shape()
    }

    override fun estimatePose(bitmap: Bitmap): PoseEstimate? {
        if (bitmap.width == 0 || bitmap.height == 0) return null
        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = TfliteUtils.bitmapToBuffer(resized, inputType)
        val keypoints = when {
            outputShape.contentEquals(intArrayOf(1, 1, KEYPOINT_COUNT, 3)) -> {
                val output = Array(1) { Array(1) { Array(KEYPOINT_COUNT) { FloatArray(3) } } }
                interpreter.run(inputBuffer, output)
                output[0][0].map { toKeypoint(it) }
            }
            outputShape.contentEquals(intArrayOf(1, KEYPOINT_COUNT, 3)) -> {
                val output = Array(1) { Array(KEYPOINT_COUNT) { FloatArray(3) } }
                interpreter.run(inputBuffer, output)
                output[0].map { toKeypoint(it) }
            }
            else -> null
        }
        resized.recycle()
        return keypoints?.let { PoseEstimate(it) }
    }

    private fun toKeypoint(values: FloatArray): PoseKeypoint {
        val y = values[0].coerceIn(0f, 1f)
        val x = values[1].coerceIn(0f, 1f)
        val score = values[2].coerceIn(0f, 1f)
        return PoseKeypoint(x = x, y = y, score = score)
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        private const val KEYPOINT_COUNT = 17
        private const val MODEL_PATH = "models/3.tflite"
    }
}
