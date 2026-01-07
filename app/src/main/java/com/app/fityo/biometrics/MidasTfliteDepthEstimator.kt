package com.app.fityo.biometrics

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import kotlin.math.max
import kotlin.math.min

class MidasTfliteDepthEstimator(
    context: Context,
    modelPath: String = MODEL_PATH,
    numThreads: Int = 2
) : DepthEstimator {
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

    override fun estimateDepth(bitmap: Bitmap): DepthResult? {
        if (bitmap.width == 0 || bitmap.height == 0) return null
        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = TfliteUtils.bitmapToBuffer(resized, inputType)

        val output = createOutputArray()
        if (output == null) {
            resized.recycle()
            return null
        }
        interpreter.run(inputBuffer, output)
        resized.recycle()

        val (values, width, height) = extractDepth(output) ?: return null
        val minMax = normalizeDepth(values)
        return DepthResult(
            depthMap = minMax.normalized,
            width = width,
            height = height,
            minDepth = minMax.min,
            maxDepth = minMax.max
        )
    }

    private fun createOutputArray(): Any? {
        return when {
            outputShape.contentEquals(intArrayOf(1, 1, inputHeight, inputWidth)) -> {
                Array(1) { Array(1) { Array(inputHeight) { FloatArray(inputWidth) } } }
            }
            outputShape.size == 4 && outputShape[0] == 1 && outputShape[3] == 1 -> {
                val height = outputShape[1]
                val width = outputShape[2]
                Array(1) { Array(height) { Array(width) { FloatArray(1) } } }
            }
            outputShape.size == 3 && outputShape[0] == 1 -> {
                val height = outputShape[1]
                val width = outputShape[2]
                Array(1) { Array(height) { FloatArray(width) } }
            }
            else -> null
        }
    }

    private fun extractDepth(output: Any): Triple<FloatArray, Int, Int>? {
        return when (output) {
            is Array<*> -> {
                when {
                    outputShape.contentEquals(intArrayOf(1, 1, inputHeight, inputWidth)) -> {
                        @Suppress("UNCHECKED_CAST")
                        val data = output as Array<Array<Array<FloatArray>>>
                        val values = FloatArray(inputWidth * inputHeight)
                        for (y in 0 until inputHeight) {
                            for (x in 0 until inputWidth) {
                                values[y * inputWidth + x] = data[0][0][y][x]
                            }
                        }
                        Triple(values, inputWidth, inputHeight)
                    }
                    outputShape.size == 4 && outputShape[0] == 1 && outputShape[3] == 1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val data = output as Array<Array<Array<FloatArray>>>
                        val height = outputShape[1]
                        val width = outputShape[2]
                        val values = FloatArray(width * height)
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                values[y * width + x] = data[0][y][x][0]
                            }
                        }
                        Triple(values, width, height)
                    }
                    outputShape.size == 3 && outputShape[0] == 1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val data = output as Array<Array<FloatArray>>
                        val height = outputShape[1]
                        val width = outputShape[2]
                        val values = FloatArray(width * height)
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                values[y * width + x] = data[0][y][x]
                            }
                        }
                        Triple(values, width, height)
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    private data class NormalizedDepth(
        val normalized: FloatArray,
        val min: Float,
        val max: Float
    )

    private fun normalizeDepth(values: FloatArray): NormalizedDepth {
        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE
        for (value in values) {
            minValue = min(minValue, value)
            maxValue = max(maxValue, value)
        }
        val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f
        val normalized = FloatArray(values.size)
        for (i in values.indices) {
            normalized[i] = (values[i] - minValue) / range
        }
        return NormalizedDepth(normalized, minValue, maxValue)
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        private const val MODEL_PATH = "models/midas/midas_v2.tflite"
    }
}
