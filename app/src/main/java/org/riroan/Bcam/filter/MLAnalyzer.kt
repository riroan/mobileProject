package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark
import org.riroan.Bcam.FaceGraphic
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic
import org.riroan.Bcam.utils.ImageGraphic

class MLAnalyzer :
    MLBaseAnalyzer() {
    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {

        println("${imageProxy.width}, ${imageProxy.height}")

        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val bitmap = BitmapUtils.getBitmap(imageProxy)
            val detector = FaceDetection.getClient(option)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    graphicOverlay.clear()
                    graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))

                    for (face in faces) {
                        graphicOverlay.add(FaceGraphic(graphicOverlay, face))

                        val rightCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
                        if (rightCheek != null) {
                            println("leftcheek is not null : ${rightCheek.position.x}, ${rightCheek.position.y}")
                            graphicOverlay.add(
                                ImageGraphic(
                                    graphicOverlay,
                                    rightCheek.position.x,
                                    rightCheek.position.y,
                                    50, 50
                                )
                            )
                        } else {
                            println("leftcheek is null")
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
}