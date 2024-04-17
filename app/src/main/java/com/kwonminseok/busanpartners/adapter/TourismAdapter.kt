package com.kwonminseok.busanpartners.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kwonminseok.busanpartners.data.TourismItem
import com.kwonminseok.busanpartners.data.TouristDestination
import com.kwonminseok.busanpartners.databinding.ItemTourismBinding
import kotlin.math.roundToInt

class   TourismAdapter : RecyclerView.Adapter<TourismAdapter.TouristDestinationViewHolder>() {

    inner class TouristDestinationViewHolder(val binding: ItemTourismBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(tourismItem: TourismItem){

            if (tourismItem.firstimage == "") {
                return
            } else {
                binding.textViewFestivalName.text = tourismItem.title
                binding.textViewFestivalPeriod.text = "${tourismItem.dist.toDouble().roundToInt().toString()}m"
                binding.textViewLocation.text = "${tourismItem.addr1} ${tourismItem.addr2}"
                Glide.with(binding.root.context)
                    .load(tourismItem.firstimage)
                    .into(binding.imageViewThumbnail)

            }

        }
    }

    // RecyclerView의 성능 향상을 위해 사용하는 DiffUtil은 서로 다른 아이템인지를 체크하여 달라진 아이템만 갱신을 도와주는 Util이다. 아래는 자세한 내용
    //https://zion830.tistory.com/86
    private val diffCallback = object : DiffUtil.ItemCallback<TourismItem>() {
        // 컨트롤 + I를 누르면 필요한 함수를 자동으로 추가할 수 있다.
        // items는 고유값을 비교하는 것
        // 이게 먼저 실행된다. 이게 true로 반환이 되어야 contents로 넘어간다.
        override fun areItemsTheSame(oldItem: TourismItem, newItem: TourismItem): Boolean {
            return oldItem.title == newItem.title
        }
        // contents는 아이템을 비교하는 것
        override fun areContentsTheSame(oldItem: TourismItem, newItem: TourismItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TouristDestinationViewHolder {
        return TouristDestinationViewHolder(
            ItemTourismBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // getCurrentList() : adapter에서 사용하는 item 리스트에 접근하고 싶다면 사용하면 된다.
    override fun onBindViewHolder(holder: TouristDestinationViewHolder, position: Int) {
        val touristItem = differ.currentList[position]
        // 여기서 따로 SpecialProductsViewHolder를 들고 오는게 아니라 holder를 사용하면 된다.
        if (touristItem.firstimage == "") {
            return
        }else {
            holder.bind(touristItem)
        }

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(touristItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((TourismItem) -> Unit)? = null
}