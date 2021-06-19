package org.riroan.Bcam.filter

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.utils.SegmentationGraphic

class SegmentationAnalyzer(val source: Int) : MLBaseAnalyzer() {

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay) {

        val mediaImage = imageProxy.image
        val segmenter = Segmentation.getClient(segmentationOption)
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            segmenter.process(image)
                .addOnSuccessListener { segmentationMask ->
                    graphicOverlay.clear()
                    graphicOverlay.add(SegmentationGraphic(graphicOverlay, segmentationMask, source))
                    graphicOverlay.postInvalidate()
                }
                .addOnFailureListener { e ->
                    println(e)
                }
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}