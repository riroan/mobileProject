package org.riroan.Bcam.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import org.riroan.Bcam.MainActivity
import org.riroan.Bcam.R
import org.riroan.Bcam.filter.*


class ColorFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = Adapter(requireContext())


    }

    class Adapter(context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        var itemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (items[position]) {
                    -1 -> MainActivity.imageProcessor = NoAnalyzer()
                    0 -> MainActivity.imageProcessor = Color_Gray()
                    1 -> MainActivity.imageProcessor = Cartoon()
                }
            }
        }


        override fun getItemCount(): Int {
            return ITEM_COUNT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.filter_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            @SuppressLint("SetTextI18n")
            holder.itemView.findViewById<TextView>(R.id.filter_name).text = "Color ${position + 1}"
            when (position) {
                1 -> holder.itemView.findViewById<ImageView>(R.id.filter_thumbnail)
                    .setImageResource(R.drawable.ic_baseline_person_24)
                2 -> {
                    holder.itemView.findViewById<TextView>(R.id.filter_name).text = "Line"
                    holder.itemView.findViewById<ImageView>(R.id.filter_thumbnail)
                        .setImageResource(R.drawable.ic_baseline_perm_identity_24)
                }
            }
        }

        inner class ViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {
            init {
                containerView.setOnClickListener {
                    itemClickListener?.onItemClick(it, adapterPosition)
                }
            }

        }
    }

    companion object {
        val items = arrayOf(-1, 0, 1)
        private val ITEM_COUNT = items.size
    }


}