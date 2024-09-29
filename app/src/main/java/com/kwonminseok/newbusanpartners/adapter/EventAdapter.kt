package com.kwonminseok.newbusanpartners.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kwonminseok.newbusanpartners.databinding.ItemEventBinding

data class Event(val imageResId: Int, val title: String, val location: String)

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.binding.ivFestivalImage.setImageResource(event.imageResId)
        holder.binding.tvFestivalTitle.text = event.title
        holder.binding.tvFestivalLocation.text = event.location
    }

    override fun getItemCount(): Int {
        return events.size
    }
}

