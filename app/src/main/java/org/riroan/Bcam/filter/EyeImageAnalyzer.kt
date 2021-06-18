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
import org.riroan.Bcam.R
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
                        val leftEyePoint = face.getLandmark(FaceLandmark.RIGHT_EYE).position
                        val rightEyePoint = face.getLandmark(FaceLandmark.LEFT_EYE).position
                        // 카메라상에서는 좌우 반전이기때문에 왼쪽이 오른쪽으로 나옴
                        val rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)?.points
                        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                        if (leftEyeContour != null) {

                            val leftEyeSize = getPoint(leftEyeContour, graphicOverlay)
                            println(leftEyeSize)
                            if (leftEyeSize > 67) {
                                graphicOverlay.add(
                                    ImageGraphic(
                                        graphicOverlay,
                                        leftEyePoint.x,
                                        leftEyePoint.y,
                                        leftEyeSize.toInt(),
                                        leftEyeSize.toInt() - 5,
                                        imageSource
                                    )
                                )
                            }
                        }

                        if (rightEyeContour != null) {
                            val rightEyeSize = getPoint(rightEyeContour, graphicOverlay)
                            if (rightEyeSize > 67) {
                                graphicOverlay.add(
                                    ImageGraphic(
                                        graphicOverlay,
                                        rightEyePoint.x,
                                        rightEyePoint.y,
                                        rightEyeSize.toInt(),
                                        rightEyeSize.toInt() - 5,
                                        imageSource
                                    )
                                )
                            }
                        }
                    }

                    graphicOverlay.postInvalidate()

                }
                .addOnFailureListener { e ->
                    println(e)
                }
                .addOnCompleteListener { imageProxy.close() }
        }
    }

    fun getPoint(points: List<PointF>, graphicOverlay: GraphicOverlay): Float {
        var miny = points[0].y
        var maxy = points[0].y
        for (pt in points) {
            if (miny > pt.y) {
                miny = pt.y
            }
            if (maxy < pt.y) {
                maxy = pt.y
            }
        }
        println("$miny  $maxy")

        miny = graphicOverlay.translateY(miny)
        maxy = graphicOverlay.translateY(maxy)
        return maxy - miny + 5
    }
}