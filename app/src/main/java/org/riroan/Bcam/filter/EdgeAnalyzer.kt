package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

typealias EdgeListener = (luma: Bitmap) -> Unit

class EdgeAnalyzer(val context: Context, val listener: EdgeListener) :
    BaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        var bitmap = image.image!!.toBitmap()
        var mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // ====================== 필터마다 수정할 부분 =======================//

        Imgproc.Sobel(mat, mat, -1, 1, 1)
        var dst = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, dst)

        // ====================== 필터마다 수정할 부분 =======================//

        dst = rotatedBitmap(dst, 90f)

        listener(dst)

        image.close()
    }
}