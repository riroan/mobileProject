package org.riroan.Bcam.filter

import com.google.mlkit.vision.face.FaceDetectorOptions

abstract class MLBaseAnalyzer {

    val option = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()
}