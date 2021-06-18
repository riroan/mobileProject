package org.riroan.Bcam

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.riroan.Bcam.databinding.ActivityEditPhotoBinding
import java.io.File


class EditPhotoActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        binding.apply {
            editPhotoBackBtn.setOnClickListener {
                super.onBackPressed()
            }

            val uri = intent.getStringExtra("uri")
            var bitmap = BitmapFactory.decodeFile(uri)
            imageView.setImageBitmap(bitmap)

            editPhotoDeleteBtn.setOnClickListener {
                val file = File(uri)
                //imageView.setColorFilter(Color.WHITE)
                if (file.exists()) { // test
                    if (file.delete()) {
                        println("test success")
                    } else {
                        println("test fail")
                    }
                }

//                if (file.exists()) {
//                   file.delete()
//                }
                finish()
            }
        }
    }
}