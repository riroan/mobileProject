package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.graphics.PointF
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark
import org.riroan.Bcam.FaceGraphic
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic
import org.riroan.Bcam.utils.ImageGraphic

class EyeImageAnalyzer(source: Int) :
    MLBaseAnalyzer() {
    private var imageSource = source

    fun setImage(source: Int) {
        imageSource = source
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {

        val mediaImage = imageProxy.image
        val detector = FaceDetection.getClient(detectionOption)
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val bitmap = BitmapUtils.getBitmap(imageProxy)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    graphicOverlay.clear()
                    graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))

                    for (face in faces) {
                        // 얼굴에 점같은거 표시
                        graphicOverlay.add(FaceGraphic(graphicOverlay, face))

                        // 카메라상에서는 좌우 반전이기때문에 왼쪽이 오른쪽으로 나옴
                        val rightEye = face.getContour(FaceContour.LEFT_EYE).points
                        val leftEye = face.getContour(FaceContour.RIGHT_EYE)
                    }

                    graphicOverlay.postInvalidate()
                }
                .addOnFailureListener { e ->
                    println(e)
                }
                .addOnCompleteListener { imageProxy.close() }
        }
    }

    fun getPoint(points: List<PointF>): Array<PointF> {
        var minx = points[0].x
        var maxx = points[0].x
        var miny = points[0].y
        var maxy = points[0].y
        for (pt in points) {
            if (minx > pt.x) {
                minx = pt.x
            }
            if (miny > pt.y) {
                miny = pt.y
            }
            if (maxx < pt.x) {
                maxx = pt.x
            }
            if (maxy < pt.y) {
                maxy = pt.y
            }
        }
        return arrayOf(PointF(minx, miny), PointF(maxx, maxy))
    }
}