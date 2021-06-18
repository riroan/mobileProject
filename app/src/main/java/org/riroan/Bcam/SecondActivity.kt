package org.riroan.Bcam

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_second.*
import org.riroan.Bcam.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    lateinit var binding : ActivitySecondBinding
    var imgPath:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        if(intent.hasExtra("imagePath")){
            imgPath = intent.getStringExtra("imagePath")
        }
        var bitmap = BitmapFactory.decodeFile(imgPath)
        imageView.setImageBitmap(bitmap)
    }
}