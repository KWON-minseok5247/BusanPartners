package com.kwonminseok.busanpartners.adapter

import com.kwonminseok.busanpartners.databinding.ItemPlaceBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

data class Place(val imageResId: Int, val title: String, val category: String, val time: String)

class PlaceAdapter(private val places: List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.binding.ivPlaceImage.setImageResource(place.imageResId)
        holder.binding.tvPlaceTitle.text = place.title
        holder.binding.tvPlaceCategory.text = place.category
        holder.binding.tvPlaceTime.text = place.time
    }

    override fun getItemCount(): Int {
        return places.size
    }
}
