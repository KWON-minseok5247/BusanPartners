package com.kwonminseok.busanpartners.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kwonminseok.busanpartners.R

class ImagesAdapter(private val context: Context, private val images: List<String>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.college_identification_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Glide를 사용하여 이미지 로드
        Glide.with(context)
            .load(images[position])
            .into(holder.imageView)

        holder.imageView.setOnClickListener{
            onClick?.invoke(images[position], position)
        }
            // onClick 콜백이 null이 아닐 경우 실행

    }

    override fun getItemCount(): Int = images.size

    // 이미지 클릭 콜백을 외부에서 설정할 수 있도록 변수 선언
    var onClick: ((String, Int) -> Unit)? = null



}