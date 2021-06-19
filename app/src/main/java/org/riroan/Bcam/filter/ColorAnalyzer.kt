package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic


class ColorAnalyzer :
    BaseAnalyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {
        var bitmap = BitmapUtils.getBitmap(imageProxy)
        var mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // ====================== 필터마다 수정할 부분 =======================//

        //Imgproc.Sobel(mat, mat, -1, 1, 1)
        //Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY)
        //Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2Luv)

        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2Luv)
        Core.inRange(mat,Scalar(255.0,255.0,50.0), Scalar(30.0,30.0,0.0),mat)

        bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, bitmap)

        // ====================== 필터마다 수정할 부분 =======================//

        graphicOverlay.clear()

        graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))
        graphicOverlay.postInvalidate()

        imageProxy.close()
    }
}