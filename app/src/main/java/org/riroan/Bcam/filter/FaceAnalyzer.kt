package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.riroan.Bcam.FaceGraphic
import org.riroan.Bcam.GraphicOverlay

class FaceAnalyzer(
    val context: Context,
    val graphicOverlay: GraphicOverlay?,
    val listener: bmpListener
) : MLBaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val t = System.currentTimeMillis()

        println("image size : ${image.width}, ${image.height}, ${image.imageInfo.rotationDegrees}")
        val mediaImage = image.image
        img = mediaImage?.toBitmap(100)
        var bitmapToFloating = img?.rotateWithReverse(270f)

        var mat = Mat()

        Utils.bitmapToMat(bitmapToFloating, mat)

        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient(highAccuracyOpts)

            val result = detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    graphicOverlay?.clear()
                    println("이미지는 있지만 얼굴은 없다")
                    for (face in faces) {
                        val bounds = face.boundingBox
                        println("${bounds.top},${bounds.left},${bounds.bottom},${bounds.right}")
                        var faceGraphic = FaceGraphic(graphicOverlay!!, null, face)
                        graphicOverlay.add(faceGraphic)
                    }
                    graphicOverlay?.postInvalidate()

                    //Utils.matToBitmap(mat, img)
                    println("있어요")
                    bitmapToFloating?.let {
                        println("${it.width}, ${it.height}")
                        listener(it)
                    }
                }
                .addOnFailureListener { e ->
                    println(e)
                }
        }else{
            println("이미지조차 없다")
        }
        image.close()
        println("Elapsed time : ${System.currentTimeMillis() - t}")
    }
}