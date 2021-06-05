package org.riroan.Bcam.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.GraphicOverlay.Graphic


class CameraImageGraphic(overlay: GraphicOverlay?, private val bitmap: Bitmap) : Graphic(
    overlay!!
) {
    override fun draw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, getTransformationMatrix(), null)
    }
}