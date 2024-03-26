package com.example.kelineyt.adapter.makeIt

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.StudentCardFrontBinding

class StudentCardAdapter : RecyclerView.Adapter<StudentCardAdapter.StudentCardViewHolder>() {

    inner class StudentCardViewHolder(val binding: StudentCardFrontBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(itemView).load(user.imagePath).into(binding.imageViewPhoto)
                textViewName.text = user.name
                textViewUniversity.text = "${user.college} ${user.major}"
                // ChipGroup 초기화
                chipGroupTags.removeAllViews()

                // 사용자의 취미 목록을 받아와 Chip으로 변환 후 ChipGroup에 추가
                user.chipGroup?.forEach { hobby ->
                    val chip = Chip(itemView.context).apply {
                        text = hobby
                        isClickable = false
                        isCheckable = false
                    }
                    chipGroupTags.addView(chip)
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentCardViewHolder {
        val binding =
            StudentCardFrontBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

//        holder.binding.buttonAddress.setOnClickListener {
//            onClick?.invoke(user)
//
//
//        }



    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((User) -> Unit)? = null
    var onDoubleClick: ((User) -> Unit)? = null

}


