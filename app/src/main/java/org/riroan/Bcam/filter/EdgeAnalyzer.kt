package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic


class EdgeAnalyzer :
    BaseAnalyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {
        var bitmap = BitmapUtils.getBitmap(imageProxy)
        var mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // ====================== 필터마다 수정할 부분 =======================//

        Imgproc.Sobel(mat, mat, -1, 1, 1)
        bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, bitmap)

        // ====================== 필터마다 수정할 부분 =======================//

        graphicOverlay.clear()
        graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))
        graphicOverlay.postInvalidate()

        imageProxy.close()
    }
}