package org.riroan.Bcam

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.filter_button
import kotlinx.android.synthetic.main.activity_second.*
import org.opencv.android.OpenCVLoader
import org.riroan.Bcam.databinding.ActivityMainBinding
import org.riroan.Bcam.filter.*
import org.riroan.Bcam.utils.*
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraSelector: CameraSelector
    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var binding: ActivityMainBinding

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private lateinit var second_intent: Intent

    var size = Size(480, 640)
    var filePath: String = ""

    //필터메뉴
    val bottomDialogFragment = FilterMenuFragment()

    init {
        if (!OpenCVLoader.initDebug()) Log.d(
            "ERROR",
            "Unable to load OpenCV"
        ) else Log.d("SUCCESS", "OpenCV loaded")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "Permission granted!")
        if (allPermissionsGranted()) {
            bindAllCameraUseCases()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        second_intent = Intent(this, SecondActivity::class.java)
        graphicOverlay = binding.graphicOverlay

        setContentView(binding.root)

        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(this, { provider: ProcessCameraProvider ->
                cameraProvider = provider
                if (allPermissionsGranted()) {
                    bindAllCameraUseCases()
                }
            })

        if (!allPermissionsGranted()) {
            runtimePermissions
        }


    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
            bindCaptureUseCase()
        }
    }

    private fun bindCaptureUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (imageCapture == null) {
            cameraProvider!!.unbind(imageCapture)
        }
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(size)
            .build()

        cameraProvider!!.bindToLifecycle(this, cameraSelector, imageCapture)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (imageAnalysis == null) {
            cameraProvider!!.unbind(imageAnalysis)
        }

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(size)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        needUpdateGraphicOverlayImageSourceInfo = true

        imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(this), { imageProxy ->

            if (needUpdateGraphicOverlayImageSourceInfo) {
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val isImageFlipped =
                    lensFacing == CameraSelector.LENS_FACING_FRONT
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicOverlay.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    graphicOverlay.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
        })
        cameraProvider!!.bindToLifecycle(this, cameraSelector, imageAnalysis)
    }

    // 전면 후면 카메라 바꾸는 코드
    fun changeLensFacing() {
        if (cameraProvider == null) {
            return
        }
        val newLensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        val newCameraSelector =
            CameraSelector.Builder().requireLensFacing(newLensFacing).build()

        if (cameraProvider!!.hasCamera(newCameraSelector)) {
            Log.d(TAG, "Set facing to $newLensFacing")
            lensFacing = newLensFacing
            cameraSelector = newCameraSelector
            bindAllCameraUseCases()
            return
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (preview != null) {
            cameraProvider!!.unbind(preview)
        }

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener {
            takePhoto()
        }

        album_button.setOnClickListener {
            val intent = Intent(this, FileManagerActivity::class.java)
            startActivity(intent)
        }

        //필터버튼
        filter_button.setOnClickListener {
            bottomDialogFragment.show(supportFragmentManager, bottomDialogFragment.tag)
        }


//        화면 뒤집기 버튼
        flip_button.setOnClickListener {
            changeLensFacing()
        }

        outputDirectory = getOutputDirectory()

        preview = Preview.Builder()
            .setTargetResolution(size)
            .build()
            .also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
        cameraProvider!!.bindToLifecycle(this, cameraSelector, preview)
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        // 파일생성
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        filePath = photoFile.path

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile).toString()
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    second_intent.putExtra("imagePath", filePath)
                    val f = File(filePath)
                    if (f.exists()) {
                        println("$filePath 존재")
                    } else {
                        println("$filePath 미존재")
                    }
                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        second_intent.putExtra("isFront", true)
                    } else {
                        second_intent.putExtra("isFront", false)
                    }
                    startActivity(second_intent)
                }
            })
    }


    private val requiredPermissions: Array<String?>
        get() = try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }

    private val runtimePermissions: Unit
        get() {
            val allNeededPermissions: MutableList<String?> = ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(this, permission)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (allNeededPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    allNeededPermissions.toTypedArray(),
                    PERMISSION_REQUESTS
                )
            }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    companion object {
        var imageProcessor: BaseAnalyzer = NoAnalyzer()
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PERMISSION_REQUESTS = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private fun isPermissionGranted(
            context: Context,
            permission: String?
        ): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission!!)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }
    }
}
