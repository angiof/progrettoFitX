package com.app.fityo.biometrics

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

object TfliteUtils {
    fun loadModelFile(context: Context, assetPath: String): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(assetPath)
        FileInputStream(assetFileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    fun bitmapToBuffer(
        bitmap: Bitmap,
        dataType: DataType,
        mean: Float = 0f,
        std: Float = 255f
    ): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height
        val bytesPerChannel = if (dataType == DataType.FLOAT32) 4 else 1
        val buffer = ByteBuffer.allocateDirect(width * height * 3 * bytesPerChannel)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[i++]
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                if (dataType == DataType.FLOAT32) {
                    buffer.putFloat((r - mean) / std)
                    buffer.putFloat((g - mean) / std)
                    buffer.putFloat((b - mean) / std)
                } else {
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
        }

        buffer.rewind()
        return buffer
    }
}
