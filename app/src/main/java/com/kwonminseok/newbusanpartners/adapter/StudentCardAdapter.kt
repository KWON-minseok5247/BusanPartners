package com.example.kelineyt.adapter.makeIt

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.data.TranslatedList
import com.kwonminseok.newbusanpartners.data.TranslatedText
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.databinding.StudentCardFrontBinding
import com.kwonminseok.newbusanpartners.util.LanguageUtils

//class StudentCardAdapter : RecyclerView.Adapter<StudentCardAdapter.StudentCardViewHolder>() {
//
//    inner class StudentCardViewHolder(val binding: StudentCardFrontBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(user: User) {
//            binding.apply {
//                Glide.with(itemView).load(user.imagePath).into(binding.imageViewPhoto)
//                textViewName.text = user.name?.ko
//                textViewUniversity.text = "${user.college} ${user.major?.ko}"
//                // ChipGroup 초기화
//                chipGroupTags.removeAllViews()
//
//                // 사용자의 취미 목록을 받아와 Chip으로 변환 후 ChipGroup에 추가
//                user.chipGroup?.ko?.forEach { hobby ->
//                    val chip = Chip(itemView.context).apply {
//                        text = hobby
//                        isClickable = false
//                        isCheckable = false
//                    }
//                    chipGroupTags.addView(chip)
//                }
//                description.text = user.introduction?.ko
//
//            }
//        }
//    }
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentCardViewHolder {
//        val binding =
//            StudentCardFrontBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return StudentCardViewHolder(binding)
//    }
//
//
//    val diffCallback = object : DiffUtil.ItemCallback<User>() {
//        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
//            return oldItem == newItem
//        }
//    }
//    val differ = AsyncListDiffer(this, diffCallback)
//
//    override fun onBindViewHolder(holder: StudentCardViewHolder, position: Int) {
//        val user = differ.currentList[position]
//        holder.bind(user)
//
////        holder.binding.buttonAddress.setOnClickListener {
////            onClick?.invoke(user)
////
////
////        }
//
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return differ.currentList.size
//    }
//
//    var onClick: ((User) -> Unit)? = null
//    var onDoubleClick: ((User) -> Unit)? = null
//
//    fun submitList(usersList: List<User>?) {
//        differ.submitList(usersList)
//    }
//
//
//}


class StudentCardAdapter : RecyclerView.Adapter<StudentCardAdapter.StudentCardViewHolder>() {

    inner class StudentCardViewHolder(val binding: StudentCardFrontBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(itemView).load(user.imagePath).into(binding.imageViewPhoto)
                textViewName.text = getTranslatedText(user.name)
                textViewUniversity.text = "${user.college} ${getTranslatedText(user.major)}"

                // ChipGroup 초기화
                chipGroupTags.removeAllViews()

                // 사용자의 취미 목록을 받아와 Chip으로 변환 후 ChipGroup에 추가
                getTranslatedList(user.chipGroup)?.forEach { hobby ->
                    val chip = Chip(itemView.context).apply {
                        text = hobby
                        isClickable = false
                        isCheckable = false
                        setChipBackgroundColorResource(R.color.chipgroup_color)
                    }
                    chipGroupTags.addView(chip)
                }

                description.text = getTranslatedText(user.introduction)
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
        val binding = StudentCardFrontBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
