package org.riroan.Bcam

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

class FaceGraphic(overlay: GraphicOverlay, var overlayImg: Bitmap?, var face : Face) : GraphicOverlay.Graphic(overlay) {
    val idPaint = Paint().apply {
        color = Color.WHITE
    }

    override fun draw(canvas: Canvas?) {
        drawBitmapOverLandmarkPosition(canvas, null, face.getLandmark(FaceLandmark.LEFT_EYE))
        drawBitmapOverLandmarkPosition(canvas, null, face.getLandmark(FaceLandmark.RIGHT_EYE))
    }

    fun drawBitmapOverLandmarkPosition(canvas: Canvas?, overlayImg: Bitmap?, landmark: FaceLandmark) {
        val point = landmark.position
        if (point != null) {
            println("${point.x}, ${point.y}")
            canvas?.drawCircle(translateX(point.x), translateY(point.y), 5f, idPaint)
        }
    }
}