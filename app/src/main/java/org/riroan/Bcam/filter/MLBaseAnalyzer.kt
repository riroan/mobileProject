package org.riroan.Bcam.filter

import android.content.Context
import com.google.mlkit.vision.face.FaceDetectorOptions

abstract class MLBaseAnalyzer(context: Context) : BaseAnalyzer(context) {

    val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .build()

    // Real-time contour detection
    val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()
}