package com.kwonminseok.busanpartners.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kwonminseok.busanpartners.R
import java.util.*

class LanguageAdapter(
    private val context: Context,
    private val languages: List<Pair<String, Locale>>,
    private var selectedPosition: Int,
    private val onLanguageSelected: (Locale) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.language_item, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languages[position], position)
    }

    override fun getItemCount(): Int = languages.size

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.itemTextView)
        private val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)

        fun bind(language: Pair<String, Locale>, position: Int) {
            textView.text = language.first
            radioButton.isChecked = position == selectedPosition

            itemView.setOnClickListener {
                if (selectedPosition != position) {
                    updateSelectedPosition(position)
                }
            }

            radioButton.setOnClickListener {
                if (selectedPosition != position) {
                    updateSelectedPosition(position)
                }
            }
        }

        private fun updateSelectedPosition(position: Int) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onLanguageSelected(languages[selectedPosition].second)
        }
    }
}
