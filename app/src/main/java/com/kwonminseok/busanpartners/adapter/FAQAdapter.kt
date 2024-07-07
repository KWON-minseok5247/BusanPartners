package com.kwonminseok.busanpartners.adapter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
//            questionTextView.text = "${position + 1}. ${faqItem.question}"
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
//            questionTextView.text = "${position + 1}. ${faqItem.question}"
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
//
//            // URL과 이메일을 포함한 텍스트 설정
//            val spannableString = SpannableString(
//                faqItem.answer +
//                        (faqItem.url?.let { " $it" } ?: "") +
//                        (faqItem.email?.let { " $it" } ?: "")
//            )
//            faqItem.url?.let { url ->
//                val start = spannableString.indexOf(url)
//                val end = start + url.length
//
//                val clickableSpan = object : ClickableSpan() {
//                    override fun onClick(widget: View) {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        itemView.context.startActivity(intent)
//                    }
//
//                    override fun updateDrawState(ds: TextPaint) {
//                        super.updateDrawState(ds)
//                        ds.color = Color.BLUE // 링크 색상
//                        ds.isUnderlineText = true // 링크 밑줄
//                    }
//                }
//
//                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//
//            faqItem.email?.let { email ->
//                val start = spannableString.indexOf(email)
//                val end = start + email.length
//
//                val clickableSpan = object : ClickableSpan() {
//                    override fun onClick(widget: View) {
//                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
//                        itemView.context.startActivity(intent)
//                    }
//
//                    override fun updateDrawState(ds: TextPaint) {
//                        super.updateDrawState(ds)
//                        ds.color = Color.BLUE // 이메일 링크 색상
//                        ds.isUnderlineText = true // 링크 밑줄
//                    }
//                }
//
//                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//
//            answerTextView.text = spannableString
//            answerTextView.movementMethod = LinkMovementMethod.getInstance()
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

            // URL과 이메일을 포함한 텍스트 설정
            val modifiedAnswer = faqItem.answer
                .replace("<URL>", faqItem.url ?: "")
                .replace("<EMAIL>", faqItem.email ?: "")
            val spannableString = SpannableString(modifiedAnswer)

            faqItem.url?.let { url ->
                val start = spannableString.indexOf(url)
                if (start >= 0) {
                    val end = start + url.length
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            itemView.context.startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.BLUE // 링크 색상
                            ds.isUnderlineText = true // 링크 밑줄
                        }
                    }
                    spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            faqItem.email?.let { email ->
                val start = spannableString.indexOf(email)
                if (start >= 0) {
                    val end = start + email.length
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                            itemView.context.startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.BLUE // 이메일 링크 색상
                            ds.isUnderlineText = true // 링크 밑줄
                        }
                    }
                    spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            answerTextView.text = spannableString
            answerTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}

