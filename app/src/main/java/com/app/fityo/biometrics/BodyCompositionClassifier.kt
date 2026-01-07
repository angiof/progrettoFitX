package com.app.fityo.biometrics

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

enum class BodyCompositionLabel {
    FIT,
    FAT,
    POWERLIFTER
}

data class BodyCompositionResult(
    val label: BodyCompositionLabel,
    val fitProbability: Float,
    val fatProbability: Float,
    val powerlifterProbability: Float
) {
    val confidence: Float
        get() = when (label) {
            BodyCompositionLabel.FIT -> fitProbability
            BodyCompositionLabel.FAT -> fatProbability
            BodyCompositionLabel.POWERLIFTER -> powerlifterProbability
        }

    fun probabilityFor(label: BodyCompositionLabel): Float {
        return when (label) {
            BodyCompositionLabel.FIT -> fitProbability
            BodyCompositionLabel.FAT -> fatProbability
            BodyCompositionLabel.POWERLIFTER -> powerlifterProbability
        }
    }
}

class BodyCompositionClassifier(
    context: Context,
    modelPath: String = MODEL_PATH,
    numThreads: Int = 2
) : AutoCloseable {
    private val interpreter: Interpreter
    private val inputWidth: Int
    private val inputHeight: Int
    private val inputType: DataType
    private val outputType: DataType
    private val outputElements: Int

    init {
        try {
            val modelBuffer = TfliteUtils.loadModelFile(context, modelPath)
            val options = Interpreter.Options().setNumThreads(numThreads)
            interpreter = Interpreter(modelBuffer, options)
            val inputTensor = interpreter.getInputTensor(0)
            val inputShape = inputTensor.shape()
            inputHeight = inputShape[1]
            inputWidth = inputShape[2]
            inputType = inputTensor.dataType()
            val outputTensor = interpreter.getOutputTensor(0)
            outputType = outputTensor.dataType()
            outputElements = outputTensor.shape().fold(1) { acc, v -> acc * v }
            Log.d(
                TAG,
                "Loaded modelPath=$modelPath input=${inputShape.contentToString()} type=$inputType " +
                    "output=${outputTensor.shape().contentToString()} type=$outputType elements=$outputElements"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load TFLite model at $modelPath: ${e.message}", e)
            throw e
        }
    }

    fun classify(bitmap: Bitmap): BodyCompositionResult? {
        if (bitmap.width == 0 || bitmap.height == 0 || outputElements <= 0) {
            Log.d(
                TAG,
                "Invalid input or output elements (w=${bitmap.width} h=${bitmap.height} outputElements=$outputElements)"
            )
            return null
        }
        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = TfliteUtils.bitmapToBuffer(resized, inputType)
        val outputBuffer = allocateOutputBuffer()
        if (outputBuffer == null) {
            Log.d(TAG, "Output buffer allocation failed (outputType=$outputType elements=$outputElements)")
            resized.recycle()
            return null
        }
        try {
            interpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            Log.e(TAG, "Interpreter.run failed: ${e.message}", e)
            resized.recycle()
            return null
        }
        resized.recycle()

        val scores = parseOutput(outputBuffer) ?: return null
        Log.d(TAG, "Raw scores: ${scores.joinToString(prefix = "[", postfix = "]")}")
        val probabilities = toProbabilities(scores)
        Log.d(TAG, "Probabilities: ${probabilities.joinToString(prefix = "[", postfix = "]")}")
        val labelIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val label = when (labelIndex) {
            0 -> BodyCompositionLabel.FIT
            1 -> BodyCompositionLabel.FAT
            else -> BodyCompositionLabel.POWERLIFTER
        }
        val fitProb = probabilities.getOrElse(0) { 0f }
        val fatProb = probabilities.getOrElse(1) { 0f }
        val powerProb = probabilities.getOrElse(2) { 0f }
        return BodyCompositionResult(
            label = label,
            fitProbability = fitProb,
            fatProbability = fatProb,
            powerlifterProbability = powerProb
        )
    }

    private fun allocateOutputBuffer(): ByteBuffer? {
        val bytesPerElement = when (outputType) {
            DataType.FLOAT32 -> 4
            DataType.UINT8, DataType.INT8 -> 1
            else -> return null
        }
        return ByteBuffer.allocateDirect(outputElements * bytesPerElement).order(ByteOrder.nativeOrder())
    }

    private fun parseOutput(buffer: ByteBuffer): FloatArray? {
        buffer.rewind()
        return when (outputType) {
            DataType.FLOAT32 -> {
                val values = FloatArray(outputElements)
                for (i in 0 until outputElements) {
                    values[i] = buffer.float
                }
                values
            }
            DataType.UINT8, DataType.INT8 -> {
                val params = interpreter.getOutputTensor(0).quantizationParams()
                val scale = params.scale
                val zeroPoint = params.zeroPoint
                Log.d(TAG, "Quant output scale=$scale zeroPoint=$zeroPoint")
                val values = FloatArray(outputElements)
                for (i in 0 until outputElements) {
                    val raw = if (outputType == DataType.UINT8) {
                        buffer.get().toInt() and 0xFF
                    } else {
                        buffer.get().toInt()
                    }
                    values[i] = (raw - zeroPoint) * scale
                }
                values
            }
            else -> null
        }
    }

    private fun toProbabilities(scores: FloatArray): FloatArray {
        if (scores.isEmpty()) return floatArrayOf(0.5f, 0.5f, 0f)

        return when {
            scores.size >= 3 -> {
                val raw = scores.copyOfRange(0, 3)
                val sum = raw.sum()
                if (raw.all { it in 0f..1f } && sum in 0.9f..1.1f) {
                    raw
                } else {
                    softmax(raw)
                }
            }
            scores.size == 2 -> {
                val raw = scores.copyOfRange(0, 2)
                val sum = raw.sum()
                val two = if (raw.all { it in 0f..1f } && sum in 0.9f..1.1f) {
                    raw
                } else {
                    softmax(raw)
                }
                floatArrayOf(two[0], two[1], 0f)
            }
            else -> {
                val fat = scores[0].coerceIn(0f, 1f)
                floatArrayOf(1f - fat, fat, 0f)
            }
        }
    }

    private fun softmax(values: FloatArray): FloatArray {
        val maxValue = values.maxOrNull() ?: 0f
        val exps = FloatArray(values.size)
        var sum = 0f
        for (i in values.indices) {
            val expValue = exp((values[i] - maxValue).toDouble()).toFloat()
            exps[i] = expValue
            sum += expValue
        }
        if (sum <= 0f) return FloatArray(values.size) { 0f }
        return FloatArray(values.size) { index -> exps[index] / sum }
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        private const val TAG = "BodyComposition"
        private const val MODEL_PATH = "models/model_unquant.tflite"
    }
}
