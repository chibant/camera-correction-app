package com.example.cameracorrection

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageProcessor {

    /**
     * Image を Bitmap に変換
     */
    fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes
        val buffer = planes[0].buffer
        buffer.rewind()
        val pixelStride = planes[0].pixelStride
        val rowPadding = planes[0].rowPadding
        val rowSize = pixelStride * image.width + rowPadding

        val bitmap = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )

        val bitmapBuffer = IntArray(image.width * image.height)
        for (y in 0 until image.height) {
            val offset = y * rowSize
            buffer.position(offset)
            for (x in 0 until image.width) {
                val uvPixelStride = planes[1].pixelStride
                val u = planes[1].buffer.get(planes[1].pixelStride * (image.width / 2) + x / 2)
                val v = planes[2].buffer.get(planes[2].pixelStride * (image.width / 2) + x / 2)
                val y_val = buffer.get(x * pixelStride).toInt() and 0xff
                val u_val = u.toInt() and 0xff
                val v_val = v.toInt() and 0xff

                val nv21 = 0
                bitmapBuffer[y * image.width + x] = convertYUV420ToARGB8888(y_val, u_val, v_val)
            }
        }

        bitmap.setPixels(
            bitmapBuffer,
            0,
            image.width,
            0,
            0,
            image.width,
            image.height
        )
        return bitmap
    }

    /**
     * ノイズ除去処理
     */
    suspend fun denoise(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val mat = Mat()
        org.opencv.imgcodecs.Imgcodecs.imread(bitmap.toString())
        
        // バイラテラルフィルターでノイズ除去
        val denoised = Mat()
        Imgproc.bilateralFilter(mat, denoised, 9, 75.0, 75.0)

        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(denoised, result)
        
        mat.release()
        denoised.release()
        
        result
    }

    /**
     * 画面の破損部分を検出・補正
     */
    suspend fun correctScreenDamage(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        // ここに AI モデル（TensorFlow Lite）の処理を追加予定
        // 現在は元の画像を返す
        bitmap
    }

    /**
     * コントラスト・明度調整
     */
    suspend fun enhanceContrast(bitmap: Bitmap, alpha: Float = 1.5f, beta: Float = 0f): Bitmap = withContext(Dispatchers.Default) {
        val mat = Mat()
        org.opencv.android.Utils.bitmapToMat(bitmap, mat)
        
        val enhanced = Mat()
        mat.convertTo(enhanced, -1, alpha.toDouble(), beta.toDouble())

        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(enhanced, result)
        
        mat.release()
        enhanced.release()
        
        result
    }

    /**
     * YUV420 を ARGB8888 に変換
     */
    private fun convertYUV420ToARGB8888(y: Int, u: Int, v: Int): Int {
        val r = (y + 1.402 * (v - 128)).toInt().coerceIn(0, 255)
        val g = (y - 0.344136 * (u - 128) - 0.714136 * (v - 128)).toInt().coerceIn(0, 255)
        val b = (y + 1.772 * (u - 128)).toInt().coerceIn(0, 255)

        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
}
