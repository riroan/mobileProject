package org.riroan.Bcam.filter

import android.content.Context
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.riroan.Bcam.GraphicOverlay
import java.io.ByteArrayOutputStream

interface BaseAnalyzer{
    fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay)
}