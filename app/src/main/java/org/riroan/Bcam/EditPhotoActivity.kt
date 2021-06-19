package org.riroan.Bcam

import android.graphics.BitmapFactory
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
            val uri = intent.getStringExtra("uri")
            var bitmap = BitmapFactory.decodeFile(uri)
            imageView.setImageBitmap(bitmap)

            editPhotoBackBtn.setOnClickListener {
                super.onBackPressed()
            }

            editPhotoDeleteBtn.setOnClickListener {
                val file = File(uri)
                if (file.exists()) {
                    file.delete()
                }
                finish()
            }
        }
    }
}