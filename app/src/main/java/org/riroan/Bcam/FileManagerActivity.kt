package org.riroan.Bcam

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import org.riroan.Bcam.databinding.ActivityFileManagerBinding
import java.io.File


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

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.apply {
            data.clear()
            fileManagerRecyclerview.layoutManager = GridLayoutManager(parent, 3)

//            var path = File("/storage/emulated/0/DCIM/Screenshots") // test
            var path = File("/storage/emulated/0/Android/media/org.riroan.Bcam/Bcam")
            val listAllFiles = path.listFiles()
            for(uri in listAllFiles) {
                var bitmap = BitmapFactory.decodeFile(uri.toString())
                data.add(ItemData(uri.toString().toUri(), bitmap))
            }

            adapter = FileManagerAdapter(data)
            adapter.itemClickListener = object : FileManagerAdapter.OnItemClickListener{
                override fun onItemClick(
                    holder: FileManagerAdapter.ViewHolder,
                    view: View,
                    data: ItemData,
                    position: Int
                ) {
                    val intent = Intent(view.context, EditPhotoActivity::class.java)
                    intent.putExtra("uri", adapter.itemsData[position].uri.toString())
                    startActivity(intent)
                }
            }
            fileManagerRecyclerview.adapter = adapter
        }
    }

    private fun init() {
        binding.apply {
            fileManagerCloseBtn.setOnClickListener {
                super.onBackPressed()
            }
        }
    }
}