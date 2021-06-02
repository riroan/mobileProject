package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc


class NewMLAnalyzer(val context: Context, val listener: bmpListener) : MLBaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient(highAccuracyOpts)
            val result = detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        val bounds = face.boundingBox
                        val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        leftEar?.let {
                            val leftEarPos = leftEar.position
                        }

                        // If contour detection was enabled:
                        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                        val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                        // If classification was enabled:
                        if (face.smilingProbability != null) {
                            val smileProb = face.smilingProbability
                        }
                        if (face.rightEyeOpenProbability != null) {
                            val rightEyeOpenProb = face.rightEyeOpenProbability
                        }

                        // If face tracking was enabled:
                        if (face.trackingId != null) {
                            val id = face.trackingId
                        }
                        println("성공 !")
                    }
                }
                .addOnFailureListener { e ->
                    println("error : ${e.toString()}")
                }
        }

        image.close()
    }

    fun point(x: Int, y: Int, mat: Mat, color: Scalar): Mat {
        Imgproc.line(
            mat,
            Point(x.toDouble(), y.toDouble()),
            Point(x.toDouble(), y.toDouble()),
            color,
            5
        )
        return mat
    }
}