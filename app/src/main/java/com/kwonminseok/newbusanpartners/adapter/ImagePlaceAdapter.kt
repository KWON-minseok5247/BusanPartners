package com.kwonminseok.newbusanpartners.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kwonminseok.newbusanpartners.R

class ImagePlaceAdapter(private val onImageClick: (Int) -> Unit) :
    RecyclerView.Adapter<ImagePlaceAdapter.ImageViewHolder>() {

    private val images = mutableListOf<String>()

    fun submitList(newImages: List<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_for_viewpager, parent, false)
        return ImageViewHolder(view, onImageClick)
    }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageViewHolder(itemView: View, private val onImageClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewForPlace)

        init {
            itemView.setOnClickListener {
                onImageClick(adapterPosition)
            }
        }

    }
}
