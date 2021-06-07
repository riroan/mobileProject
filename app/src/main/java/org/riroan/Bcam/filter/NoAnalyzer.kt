package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic


class NoAnalyzer :
    BaseAnalyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {

        val bitmap = BitmapUtils.getBitmap(imageProxy)
        graphicOverlay.clear()
        graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))
        graphicOverlay.postInvalidate()

        imageProxy.close()
    }
}