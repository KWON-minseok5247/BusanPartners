package com.kwonminseok.busanpartners.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.TranslatedList
import com.kwonminseok.busanpartners.data.TranslatedText
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.ItemStudentBinding
import com.kwonminseok.busanpartners.databinding.StudentCardFrontBinding
import com.kwonminseok.busanpartners.util.LanguageUtils

class StudentAdapter : RecyclerView.Adapter<StudentAdapter.StudentCardViewHolder>() {

    inner class StudentCardViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(itemView).load(user.imagePath).into(binding.ivProfileImage)
                tvName.text = getTranslatedText(user.name)
                tvMajor.text = "전공 : ${getTranslatedText(user.major)}"
            }

            itemView.setOnClickListener {
                onClick?.invoke(user)
            }

        }

//        private fun getDeviceLanguage(): String {
//            return itemView.context.resources.configuration.locales.get(0).language
//        }

        private fun getTranslatedText(translatedText: TranslatedText?): String? {
            val language = LanguageUtils.getDeviceLanguage(itemView.context)
            return when (language) {
                "ko" -> translatedText?.ko
                "en" -> translatedText?.en
                "ja" -> translatedText?.ja ?: translatedText?.en
                "zh" -> translatedText?.zh ?: translatedText?.en
                else -> translatedText?.en
            }
        }

        private fun getTranslatedList(translatedList: TranslatedList?): List<String>? {
            val language = LanguageUtils.getDeviceLanguage(itemView.context)
            Log.e("language",language)
            return when (language) {
                "ko" -> translatedList?.ko
                "en" -> translatedList?.en
                "ja" -> translatedList?.ja
                "zh" -> translatedList?.zh
                "zh-TW" -> translatedList?.zh
                else -> translatedList?.en
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentCardViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentCardViewHolder(binding)
    }

    val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: StudentCardViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((User) -> Unit)? = null
    var onDoubleClick: ((User) -> Unit)? = null

    fun submitList(usersList: List<User>?) {
        differ.submitList(usersList)
    }
}