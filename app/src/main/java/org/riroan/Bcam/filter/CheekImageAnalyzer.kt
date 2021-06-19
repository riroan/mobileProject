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

class CheekImageAnalyzer(source:Int) :
    MLBaseAnalyzer() {
    private var imageSource = source
    private val srcSize = 70

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
                        // 카메라상에서는 좌우 반전이기때문에 왼쪽이 오른쪽으로 나옴
                        val rightCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
                        val leftCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
                        if (rightCheek != null) {
                            graphicOverlay.add(
                                ImageGraphic(
                                    graphicOverlay,
                                    rightCheek.position.x,
                                    rightCheek.position.y,
                                    srcSize, srcSize,
                                    imageSource
                                )
                            )
                        }
                        if(leftCheek != null){
                            graphicOverlay.add(
                                ImageGraphic(
                                    graphicOverlay,
                                    leftCheek.position.x,
                                    leftCheek.position.y,
                                    srcSize, srcSize,
                                    imageSource
                                )
                            )
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