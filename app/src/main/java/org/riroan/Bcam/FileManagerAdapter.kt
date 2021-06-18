package org.riroan.Bcam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class FileManagerAdapter (val itemsData: ArrayList<ItemData>) : RecyclerView.Adapter<FileManagerAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(holder: ViewHolder, view: View, data: ItemData, position: Int)
    }
    //        interface OnItemLongClickListener{
//            fun OnItemLongClick(holder: ViewHolder, view: View, data: ItemData, position: Int)
//        }
    var itemClickListener:OnItemClickListener?=null
//        var itemLongClickListener: AdapterView.OnItemLongClickListener?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val thumbnail_imageView: ImageView = itemView.findViewById(R.id.thumbnail_imageView)

        init {
            itemView.setOnClickListener{
                itemClickListener?.onItemClick(this, it, itemsData[adapterPosition], adapterPosition)
            }
//                itemView.setOnLongClickListener {
//                    itemLongClickListener?.OnItemLongClick(this, it, itemsData[adapterPosition], adapterPosition)
//                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thumbnail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.thumbnail_imageView.setImageBitmap(itemsData[position].bitmap)
    }
}