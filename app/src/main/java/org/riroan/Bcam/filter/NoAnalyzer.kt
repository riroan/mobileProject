package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy


typealias NoListener = (luma: Bitmap) -> Unit

class NoAnalyzer(val context: Context, val listener: NoListener) :
    BaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val img = image.image?.toBitmap(100)
        var bitmapToFloating = img?.rotateWithReverse(270f)
        bitmapToFloating?.let{listener(it)}

        image.close()
    }
}