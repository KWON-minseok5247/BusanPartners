package com.kwonminseok.newbusanpartners.ui.message.messageTemp

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kwonminseok.newbusanpartners.databinding.ItemDateAttachmentPreviewBinding
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.ui.feature.messages.composer.MessageComposerViewStyle
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.AttachmentPreviewViewHolder
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.AttachmentPreviewFactory

class DateAttachmentPreviewFactory : AttachmentPreviewFactory {

    override fun canHandle(attachment: Attachment): Boolean {
        return attachment.type == "date"
    }

    override fun onCreateViewHolder(
        parentView: ViewGroup,
        attachmentRemovalListener: (Attachment) -> Unit,
        style: MessageComposerViewStyle?
    ): AttachmentPreviewViewHolder {
        return ItemDateAttachmentPreviewBinding
            .inflate(LayoutInflater.from(parentView.context), parentView, false)
            .let { DateAttachmentPreviewViewHolder(it, attachmentRemovalListener) }
    }

    class DateAttachmentPreviewViewHolder(
        private val binding: ItemDateAttachmentPreviewBinding,
        private val attachmentRemovalListener: (Attachment) -> Unit,
    ) : AttachmentPreviewViewHolder(binding.root) {

        private lateinit var attachment: Attachment

        init {
            binding.deleteButton.setOnClickListener {
                attachmentRemovalListener(attachment)
            }
        }

        override fun bind(attachment: Attachment) {
            this.attachment = attachment

            binding.dateTextView.text = attachment.extraData["payload"].toString()
        }
    }
}




