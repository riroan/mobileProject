package org.riroan.Bcam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*
import org.riroan.Bcam.databinding.ActivitySecondBinding
import java.io.File
import java.io.FileNotFoundException


class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding
    var imgPath: String? = null
    lateinit var file: File
    val dir: String = "/storage/emulated/0/Android/media/org.riroan.Bcam/Bcam"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        if (intent.hasExtra("imagePath")) {
            val img = binding.imageView
            val imgPath = intent.getStringExtra("imagePath")
            val isFront = intent.getBooleanExtra("isFront", false)

            var bitmap = BitmapFactory.decodeFile(imgPath)
            var exif = ExifInterface(imgPath!!)
            val exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            var exifDegree = exifOrientationToDegrees(exifOrientation)
            if (isFront) {
                val matrix = Matrix()
                matrix.preScale(-1f, 1f)
                bitmap =
                    Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        false
                    )
                exifDegree = 360 - exifDegree
            }
            img.setImageBitmap(rotate(bitmap, exifDegree.toFloat()))
        }

        binding.apply {

            deleteButton.setOnClickListener {
                try {
                    file = File(dir)
                    var fList = file.listFiles()
                    for (i in 0..fList.size - 1) {
                        var fName = fList[i].name
                        if ((dir + "/" + fName) == imgPath) {
                            fList[i].delete()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SecondActivity, "파일 삭제 실패", Toast.LENGTH_SHORT).show()
                }
                finish()

            }

            saveButton.setOnClickListener {
                finish()
            }

        }
    }

    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    private fun rotate(bitmap: Bitmap, degree: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}