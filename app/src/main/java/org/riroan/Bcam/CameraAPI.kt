package org.riroan.Bcam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import java.io.File
import java.io.FileOutputStream


class CameraAPI(private val activity: MainActivity, private val textureView: TextureView) {
    private var camera: Camera? = null
    fun init() {
        try {
            // 카메라를 연다.
            camera = Camera.open()
            //camera.setPreviewTexture(textureView.getSurfaceTexture());
            //camera.startPreview();

            // 카메라 회전을 조절
            val windowManager = activity.windowManager
            val display = windowManager.defaultDisplay
            val rotation = display.rotation
            var degree = 0
            when (rotation) {
                Surface.ROTATION_0 -> degree = 90
                Surface.ROTATION_90 -> degree = 0
                Surface.ROTATION_180 -> degree = 270
                Surface.ROTATION_270 -> degree = 180
            }
            camera!!.setDisplayOrientation(degree)
            val chk = textureView.isAvailable
            if (chk == true) {
                startPreview()
            } else {
                val listener = TextureLinstener()
                textureView.surfaceTextureListener = listener
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // TextureView의 상태가 변화하면 반응하는 리스너
    private inner class TextureLinstener : SurfaceTextureListener {
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            startPreview()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    // 미리보기 처리
    private fun startPreview() {
        try {
            camera!!.setPreviewTexture(textureView.surfaceTexture)
            camera!!.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 사진 촬영
    fun imageCapture(filePath: String) {
        val callback: CaptureCallback = CaptureCallback(filePath)
        camera!!.takePicture(null, null, callback)
    }

    // 사진 촬영이 성공하면 반응하는 콜백
    private inner class CaptureCallback(var filePath: String) :
        PictureCallback {
        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            try {
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                val file = File(filePath)
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}