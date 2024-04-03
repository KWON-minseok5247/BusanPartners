package com.kwonminseok.busanpartners.mainScreen.message

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kwonminseok.busanpartners.databinding.ItemDateAttachmentBinding
import com.kwonminseok.busanpartners.databinding.LocationAttachementViewBinding
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListListenerContainer
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactory
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.InnerAttachmentViewHolder

class LocationAttachmentViewFactory(
) : AttachmentFactory {

    override fun canHandle(message: Message): Boolean {
        return message.attachments.any { it.type == "location" }
    }

    override fun createViewHolder(
        message: Message,
        listeners: MessageListListenerContainer?,
        parent: ViewGroup
    ): InnerAttachmentViewHolder {
        return LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
            .let { LocationAttachmentViewHolder(it, listeners) }
    }

    class LocationAttachmentViewHolder(
        private val binding: LocationAttachementViewBinding,
        listeners: MessageListListenerContainer?,
    ) : InnerAttachmentViewHolder(binding.root) {

        private lateinit var message: Message

        init {
            // 이미지 클릭 리스너 설정
            binding.snapshotView.setOnClickListener {
                listeners?.attachmentClickListener?.onAttachmentClick(
                    message,
                    message.attachments.first()
                )
            }
            // 이미지 롱 클릭 리스너 설정
            binding.snapshotView.setOnLongClickListener {
                listeners?.messageLongClickListener?.onMessageLongClick(message)
                true
            }
        }

        override fun onBindViewHolder(message: Message) {
            this.message = message

            // 메시지 어태치먼트에서 스냅샷 이미지 경로를 가져옵니다.
            val snapshotPath = message.attachments
                .first { it.type == "location" }
                .extraData["image"] as? String

            // 여기에서 이미지 뷰에 이미지를 로드합니다.
            // 예를 들어 Glide 라이브러리를 사용하는 경우:
            Glide.with(binding.snapshotView.context)
                .load(snapshotPath)
                .into(binding.snapshotView)
        }
    }
}