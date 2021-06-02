package org.riroan.Bcam

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.riroan.Bcam.databinding.ActivityMainBinding
import org.riroan.Bcam.filter.EdgeAnalyzer
import org.riroan.Bcam.filter.FaceAnalyzer
import org.riroan.Bcam.filter.NewMLAnalyzer
import org.riroan.Bcam.filter.NoAnalyzer
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private lateinit var viewFinder: PreviewView
    private lateinit var overlay: ImageView

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null

    private var filterMode = FilterMode.TESTML
    private var useFrontCamera = true

    private lateinit var binding: ActivityMainBinding

    val rotation = Surface.ROTATION_270

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // 권한 획득
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // 권한 획득 실패
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //imageView = findViewById(R.id.overlay)
        viewFinder = binding.viewFinder
        overlay = binding.overlay

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }
        registerForContextMenu(option_button)

        //필터버튼
//        filter_button.setOnClickListener {
//
//        }


//
//        //화면 뒤집기 버튼
//        flip_button.setOnClickListener {
//
//        }
//
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //더보기버튼
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu1->{//16:9

            }
            R.id.menu2->{//4:3

            }
            R.id.menu3->{//1:1

            }
        }
        return super.onContextItemSelected(item)
    }



    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        // null일경우 리턴
        // 아닌경우 사진 찍은 경우
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        // 파일생성
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

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
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun startCamera() {
        // 카메라를 열고 닫는 작업
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //preview = setPreview()
            imageCapture = setImageCapture()
            imageAnalysis = setImageAnalysis()
            if (useFrontCamera)
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            else
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            //viewFinder.overlay.add(overlay)

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                // 카메라가 앱의 라이프사이클을 따라감
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, imageAnalysis, imageCapture//, preview
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

            //preview!!.setSurfaceProvider(viewFinder.surfaceProvider)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setPreview(): Preview {
        // 사진 찍기전 보이는 화면
        val preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()

        // 후면 카메라


        return preview
    }

    private fun setImageAnalysis(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(rotation)
            .build()

        // 만든 필터 실행하고 싶을 때 EdgeAnalyzer 대신에 넣으면 됩니다.

        if (filterMode == FilterMode.NO) {
            imageAnalysis.setAnalyzer(cameraExecutor, NoAnalyzer(this) { img ->
                runOnUiThread {
                    println("${img.width},${img.height}")
                    overlay.setImageBitmap(img)
                }
            })
        } else if (filterMode == FilterMode.SOBEL) {

            imageAnalysis.setAnalyzer(cameraExecutor, EdgeAnalyzer(this) { img ->
                runOnUiThread {
                    overlay.setImageBitmap(img)
                }
            })

        } else if (filterMode == FilterMode.TESTML) {
            imageAnalysis.setAnalyzer(cameraExecutor, NewMLAnalyzer(this) { img ->
                runOnUiThread {
                    overlay.setImageBitmap(img)
                    println("${img.width},${img.height}")
                }
            })
        } else if (filterMode == FilterMode.FACE) {
            imageAnalysis.setAnalyzer(cameraExecutor, FaceAnalyzer(this, binding.graphicOverlay) {
                runOnUiThread {
                    overlay.setImageBitmap(it)
                }
            })

        }

        return imageAnalysis
    }

    fun setGraphicOverlay(overlay: GraphicOverlay?) {
        val min = Math.min(720, 1280)
        val max = Math.max(720, 1280)
        overlay?.setCameraInfo(min, max, CameraSelector.DEFAULT_FRONT_CAMERA)
        overlay?.clear()
    }

    private fun setImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setTargetRotation(rotation)
            .build()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis!!.targetRotation = rotation
                imageCapture!!.targetRotation = rotation
            }
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onStart() {
        super.onStart()
        //orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        //orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        init {
            if (!OpenCVLoader.initDebug()) Log.d(
                "ERROR",
                "Unable to load OpenCV"
            ) else Log.d("SUCCESS", "OpenCV loaded")
        }

        enum class FilterMode {
            NO, SOBEL, TESTML, FACE
        }
    }
}
