package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc


class TestMLAnalyzer(val context: Context, val listener: bmpListener) : MLBaseAnalyzer(context) {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        println("image size : ${image.image!!.width}, ${image.image?.height}, ${image.imageInfo.rotationDegrees}")
        val mediaImage = image.image
        img = mediaImage?.toBitmap(100)
        var bitmapToFloating = img?.rotateWithReverse(270f)


        var mat = Mat()

        Utils.bitmapToMat(bitmapToFloating, mat)
        println("여기까지 잘됨")

        if (mediaImage != null) {
            println("이미지는 있음")
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient(highAccuracyOpts)
            val result = detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    println("얼굴 발견안됨")
                    for (face in faces) {
                        println("얼굴 발견됨")
                        val bounds = face.boundingBox
                        println("${bounds.top},${bounds.left},${bounds.bottom},${bounds.right}")

                        Imgproc.rectangle(
                            mat,
                            Point(bounds.left.toDouble(), bounds.top.toDouble()),
                            Point(bounds.right.toDouble(), bounds.bottom.toDouble()),
                            Scalar(0.0, 255.0, 0.0),
                            3
                        )

                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        leftEar?.let {
                            val leftEarPos = leftEar.position
                        }

                        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
                        leftEye?.let {
                            val leftEyePos = leftEye.position
                            Imgproc.line(
                                mat,
                                Point(leftEyePos.x.toDouble(), leftEyePos.y.toDouble()),
                                Point(leftEyePos.x.toDouble(), leftEyePos.y.toDouble()),
                                Scalar(255.0, 0.0, 0.0),
                                5
                            )
                            println("왼쪽눈")
                        }

                        // If contour detection was enabled:
                        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                        val rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)?.points
                        val faceContour = face.getContour(FaceContour.FACE)?.points
                        val leftCheekContour = face.getContour(FaceContour.LEFT_CHEEK)?.points
                        val rightCheekContour = face.getContour(FaceContour.RIGHT_CHEEK)?.points

                        if (leftEyeContour != null) {
                            for (point in leftEyeContour) {
                                mat = point(
                                    point.x.toInt(),
                                    point.y.toInt(),
                                    mat,
                                    Scalar(0.0, 0.0, 255.0)
                                )
                            }
                        }
                        if (rightEyeContour != null) {
                            for (point in rightEyeContour) {
                                mat = point(
                                    point.x.toInt(),
                                    point.y.toInt(),
                                    mat,
                                    Scalar(0.0, 255.0, 0.0)
                                )
                            }
                        }
                        if (faceContour != null){
                            for(point in faceContour){
                                mat = point(
                                    point.x.toInt(),
                                    point.y.toInt(),
                                    mat,
                                    Scalar(255.0, 0.0, 0.0)
                                )
                            }
                        }
                        if (leftCheekContour != null){
                            for(point in leftCheekContour){
                                mat = point(
                                    point.x.toInt(),
                                    point.y.toInt(),
                                    mat,
                                    Scalar(255.0, 255.0, 0.0)
                                )
                            }
                        }
                        if (rightCheekContour != null){
                            for(point in rightCheekContour){
                                mat = point(
                                    point.x.toInt(),
                                    point.y.toInt(),
                                    mat,
                                    Scalar(0.0, 255.0, 255.0)
                                )
                            }
                        }

                    }

                    Utils.matToBitmap(mat, bitmapToFloating)

                    //img = rotatedBitmap(img!!, image.imageInfo.rotationDegrees.toFloat())
                    bitmapToFloating?.let{listener(it)}
                }
                .addOnFailureListener { e ->
                    println("Error : ${e.toString()}")
                }
        }else{
            println("이미지가 널임")
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