package com.kwonminseok.busanpartners.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.FAQItem

//class FAQAdapter(private val faqList: List<FAQItem>) :
//    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {
//
//    private val expandedPositionSet = mutableSetOf<Int>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
//        return FAQViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
//        val faqItem = faqList[position]
//        holder.bind(faqItem, position)
//    }
//
//    override fun getItemCount(): Int = faqList.size
//
//    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
//        private val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
//
//        fun bind(faqItem: FAQItem, position: Int) {
//            questionTextView.text = faqItem.question
//            answerTextView.text = faqItem.answer
//
//            val isExpanded = expandedPositionSet.contains(position)
//            answerTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
//
//            itemView.setOnClickListener {
//                if (isExpanded) {
//                    expandedPositionSet.remove(position)
//                    notifyItemChanged(position)
//                } else {
//                    expandedPositionSet.add(position)
//                    notifyItemChanged(position)
//                }
//            }
//        }
//    }
//}
//class FAQAdapter(private val faqList: List<FAQItem>) :
//    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {
//
//    private val expandedPositionSet = mutableSetOf<Int>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
//        return FAQViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
//        val faqItem = faqList[position]
//        holder.bind(faqItem, position)
//    }
//
//    override fun getItemCount(): Int = faqList.size
//
//    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
//        private val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
//        private val arrowImageView: ImageView = itemView.findViewById(R.id.arrowImageView)
//        private val cardView: CardView = itemView.findViewById(R.id.cardView)
//
//        fun bind(faqItem: FAQItem, position: Int) {
//            questionTextView.text = faqItem.question
//            answerTextView.text = faqItem.answer
//
//            val isExpanded = expandedPositionSet.contains(position)
//            answerTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
//            arrowImageView.rotation = if (isExpanded) 180f else 0f
//
//            itemView.setOnClickListener {
//                if (isExpanded) {
//                    expandedPositionSet.remove(position)
//                } else {
//                    expandedPositionSet.add(position)
//                }
//                notifyItemChanged(position)
//            }
//        }
//    }
//}
class FAQAdapter(private val faqList: List<FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    private val expandedPositionSet = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        holder.bind(faqItem, position)
    }

    override fun getItemCount(): Int = faqList.size

    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
        private val arrowImageView: ImageView = itemView.findViewById(R.id.arrowImageView)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(faqItem: FAQItem, position: Int) {
            questionTextView.text = "${position + 1}. ${faqItem.question}"
            answerTextView.text = faqItem.answer

            val isExpanded = expandedPositionSet.contains(position)
            answerTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            arrowImageView.rotation = if (isExpanded) 180f else 0f

            itemView.setOnClickListener {
                if (isExpanded) {
                    expandedPositionSet.remove(position)
                } else {
                    expandedPositionSet.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }
}
