package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import org.riroan.Bcam.FaceGraphic
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.BitmapUtils
import org.riroan.Bcam.utils.CameraImageGraphic

class MLAnalyzer2(val context: Context, val graphicOverlay: GraphicOverlay) :
    MLBaseAnalyzer(context) {
    override fun analyze(image: ImageProxy) {}

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            println("이미지 있음")
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            println("image size : ${image.width}, ${image.height}")
            bitmap = BitmapUtils.getBitmap(imageProxy)
            val detector = FaceDetection.getClient(highAccuracyOpts)
            println("디텍터 생성")
            detector.process(image)
                .addOnSuccessListener { faces ->
                    graphicOverlay.clear()
                    graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap!!))

                    for (face in faces) {
                        graphicOverlay.add(FaceGraphic(graphicOverlay, face))
                    }
                    graphicOverlay.postInvalidate()
                    println("성공")
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    println(e)
                    println("실패")
                    imageProxy.close()
                }
        }

    }
}