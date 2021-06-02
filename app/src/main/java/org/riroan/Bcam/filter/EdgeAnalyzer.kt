package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


class EdgeAnalyzer(val context: Context, val listener: bmpListener) :
    BaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        img = image.image!!.toBitmap(100)
        var bitmapToFloating = img?.rotateWithReverse(270f)
        var mat = Mat()
        Utils.bitmapToMat(bitmapToFloating, mat)

        // ====================== 필터마다 수정할 부분 =======================//

        Imgproc.Sobel(mat, mat, -1, 1, 1)
        bitmapToFloating = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, bitmapToFloating)

        // ====================== 필터마다 수정할 부분 =======================//

        bitmapToFloating?.let{listener(it)}

        image.close()
    }
}