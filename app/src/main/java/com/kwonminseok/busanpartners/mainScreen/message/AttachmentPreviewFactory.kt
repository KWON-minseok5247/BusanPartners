package com.kwonminseok.busanpartners.mainScreen.message

import android.view.ViewGroup
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.AttachmentPreviewViewHolder

interface AttachmentPreviewFactory {

    //해당 어태치먼트를 처리할 수 있는지 확인합니다
    fun canHandle(attachment: Attachment): Boolean

    //첨부 파일 미리보기 UI를 나타냅니다
    fun onCreateViewHolder(
        parentView: ViewGroup,
        attachmentRemovalListener: (Attachment) -> Unit,
    ): AttachmentPreviewViewHolder
}

