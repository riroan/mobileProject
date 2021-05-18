package org.riroan.Bcam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.TextureView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.riroan.Bcam.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    var permission_list = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var dirPath: String? = null
    var cameraAPI: CameraAPI? = null
    var textureView: TextureView? = null
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textureView = binding.textureView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0)
        } else {
            init()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return
            }
        }
        init()
    }

    private fun init() {
        try {
            val tempPath = Environment.getExternalStorageDirectory().absolutePath
            dirPath = "$tempPath/Android/data/$packageName"
            val file = File(dirPath)
            if (file.exists() == false) {
                file.mkdir()
            }
            cameraAPI = CameraAPI(this, textureView!!)
            cameraAPI!!.init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun imageCaptureBtn(view: View?) {
        val filePath = dirPath + "/temp_" + System.currentTimeMillis() + ".jpg"
        cameraAPI!!.imageCapture(filePath)
    }
}