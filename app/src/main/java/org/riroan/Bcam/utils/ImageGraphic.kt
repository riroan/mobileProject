package org.riroan.Bcam.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.R

class ImageGraphic(
    overlay: GraphicOverlay?, var x: Float, var y: Float, val w: Int, val h: Int
) :
    GraphicOverlay.Graphic(overlay!!) {
    override fun draw(canvas: Canvas?) {
        val context = applicationContext
        var bitmap =
            BitmapFactory.decodeResource(context.resources, R.raw.moon)
        x = translateX(x)
        y = translateY(y)
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)

        if (bitmap != null) {
            canvas?.drawBitmap(bitmap, x, y, null)
        }
    }
}