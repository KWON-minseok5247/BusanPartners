package com.kwonminseok.busanpartners.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.kwonminseok.busanpartners.R

class ImageZoomAdapter : RecyclerView.Adapter<ImageZoomAdapter.ZoomViewHolder>() {

    private val images = mutableListOf<String>()

    fun submitList(newImages: List<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.zoom_image_item, parent, false)
        return ZoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ZoomViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.photoView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ZoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: PhotoView = itemView.findViewById(R.id.photoViewZoom)
    }
}
