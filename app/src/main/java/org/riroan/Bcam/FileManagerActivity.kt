package org.riroan.Bcam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import org.riroan.Bcam.databinding.ActivityFileManagerBinding


class FileManagerActivity : AppCompatActivity() {
    lateinit var binding : ActivityFileManagerBinding
    var data:ArrayList<ItemData> = ArrayList()
    lateinit var adapter: FileManagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun initRecyclerView() {
        binding.apply {
            fileManagerRecyclerview.layoutManager = GridLayoutManager(parent, 3)

            val cursor = contentResolver.query( // 기기의 모든 사진 가져옴
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            )

            if (cursor != null) { // 필터링
                while(cursor.moveToNext()) {
//                for (i in 1..10) {
//                    cursor.moveToNext()

                    var uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    println(uri)
                    var dir = uri.split("/")

//                    for (elem in dir) { // test
//                        println(elem)
//                    }

                    if(dir[1] != "storage"){
                        continue
                    }
                    if(dir[2] != "emulated"){
                        continue
                    }
                    if(dir[3] != "0"){
                        continue
                    }
                    if(dir[4] != "DCIM"){
                        continue
                    }
                    if(dir[5] != "Screenshots"){
                        continue
                    }
//                    "/storage/emulated/0/Android/data/org.riroan.Bcam" 이걸로 바꿔야됨

                    var bitmap = BitmapFactory.decodeFile(uri)
                    data.add(ItemData(uri.toUri(), bitmap))
                }
                cursor.close()
            }


            adapter = FileManagerAdapter(data)
            adapter.itemClickListener = object : FileManagerAdapter.OnItemClickListener{
                override fun onItemClick(
                    holder: FileManagerAdapter.ViewHolder,
                    view: View,
                    data: ItemData,
                    position: Int
                ) {
                    holder.thumbnail_imageView.setColorFilter(Color.WHITE)
//                    adapter.itemsData[position].uri = "".toUri()
                    adapter.itemsData[position].bitmap = null
                    val intent = Intent(view.context, EditPhotoActivity::class.java)
                    intent.putExtra("uri", adapter.itemsData[position].uri.toString())
                    startActivity(intent)
                }
            }
//            adapter.itemLongClickListener = object  : FileManagerAdapter.OnItemLongClickListener{
//                override fun OnItemLongClick(
//                    holder: FileManagerAdapter.ViewHolder,
//                    view: View,
//                    data: ItemData,
//                    position: Int
//                ) {
//                    // 꾹누른 후 화면
//                }
//            }
            fileManagerRecyclerview.adapter = adapter
        }
    }

    private fun init() {
        initRecyclerView()
        binding.apply {
            fileManagerCloseBtn.setOnClickListener {
                super.onBackPressed()
            }
        }
    }
}