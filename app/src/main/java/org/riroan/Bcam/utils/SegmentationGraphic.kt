package org.riroan.Bcam.utils

import android.graphics.*
import androidx.annotation.ColorInt
import com.google.mlkit.vision.segmentation.SegmentationMask
import org.riroan.Bcam.GraphicOverlay
import org.riroan.Bcam.R
import java.nio.ByteBuffer

class SegmentationGraphic(
    overlay: GraphicOverlay,
    segmentationMask: SegmentationMask,
    val source: Int
) :
    GraphicOverlay.Graphic(overlay) {
    private val mask: ByteBuffer
    private val maskWidth: Int
    private val maskHeight: Int
    private val isRawSizeMaskEnabled: Boolean
    private val scaleX: Float
    private val scaleY: Float

    /** Draws the segmented background on the supplied canvas.  */
    override fun draw(canvas: Canvas?) {
        val bitmap = Bitmap.createBitmap(
            maskColorsFromByteBuffer(mask), maskWidth, maskHeight, Bitmap.Config.ARGB_8888
        )
        if (isRawSizeMaskEnabled) {
            val matrix = Matrix(getTransformationMatrix())
            matrix.preScale(scaleX, scaleY)
            canvas?.drawBitmap(bitmap, matrix, null)
        } else {
            canvas?.drawBitmap(bitmap, getTransformationMatrix(), null)
        }
        bitmap.recycle()
        // Reset byteBuffer pointer to beginning, so that the mask can be redrawn if screen is refreshed
        mask.rewind()
    }

    /** Converts byteBuffer floats to ColorInt array that can be used as a mask.  */
    @ColorInt
    private fun maskColorsFromByteBuffer(byteBuffer: ByteBuffer): IntArray {
        @ColorInt val colors =
            IntArray(maskWidth * maskHeight)
        val context = applicationContext
        var bitmap =
            BitmapFactory.decodeResource(context.resources, source)
        println("$maskWidth, $maskHeight")
        bitmap = Bitmap.createScaledBitmap(bitmap, maskWidth, maskHeight, false)

        for (i in 0 until maskWidth * maskHeight) {
            val backgroundLikelihood = 1 - byteBuffer.float
            if (backgroundLikelihood > 0.3) {
                //colors[i] = Color.argb(128, 255, 0, 255)
                colors[i] = bitmap.getPixel(i % maskWidth, i / maskHeight)
            }
        }
        return colors
    }

    init {
        mask = segmentationMask.buffer
        maskWidth = segmentationMask.width
        maskHeight = segmentationMask.height
        isRawSizeMaskEnabled =
            maskWidth != overlay.getImageWidth() || maskHeight != overlay.getImageHeight()
        scaleX = overlay.getImageWidth() * 1f / maskWidth
        scaleY = overlay.getImageHeight() * 1f / maskHeight
    }
}