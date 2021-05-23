package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat

typealias NoListener = (luma: Bitmap) -> Unit

class NoAnalyzer(val context: Context, val listener: EdgeListener) :
    BaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        var bitmap = image.image!!.toBitmap()
        var mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        var dst = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(mat, dst)

        dst = rotatedBitmap(dst, 90f)

        listener(dst)

        image.close()
    }
}