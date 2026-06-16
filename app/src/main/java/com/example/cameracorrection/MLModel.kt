package com.example.cameracorrection

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90TransformOp
import java.nio.MappedByteBuffer

class MLModel(context: Context) {

    private var tfliteInterpreter: Interpreter? = null
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var tfliteModel: MappedByteBuffer

    init {
        try {
            // モデルをメモリにロード
            tfliteModel = FileUtil.loadMappedFile(context, "model.tflite")
            
            // インタープリターを初期化
            val options = Interpreter.Options()
            options.numThreads = 4
            tfliteInterpreter = Interpreter(tfliteModel, options)
            
            // 画像プロセッサーを初期化
            imageProcessor = ImageProcessor.Builder()
                .add(Rot90TransformOp(0))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 画像補正の推論
     */
    fun correctImage(bitmap: Bitmap): Bitmap? {
        return try {
            // 入力画像を TensorFlow Lite 形式に変換
            val tensorImage = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            // 推論を実行
            val output = Array(1) { FloatArray(bitmap.width * bitmap.height * 3) }
            tfliteInterpreter?.run(processedImage.buffer, output)

            // 出力を Bitmap に変換
            outputToBitmap(output[0], bitmap.width, bitmap.height)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap // エラーの場合は元の画像を返す
        }
    }

    /**
     * モデルの出力を Bitmap に変換
     */
    private fun outputToBitmap(output: FloatArray, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        for (i in pixels.indices) {
            val idx = i * 3
            val r = (output[idx] * 255).toInt().coerceIn(0, 255)
            val g = (output[idx + 1] * 255).toInt().coerceIn(0, 255)
            val b = (output[idx + 2] * 255).toInt().coerceIn(0, 255)

            pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * リソースを解放
     */
    fun close() {
        tfliteInterpreter?.close()
    }
}
