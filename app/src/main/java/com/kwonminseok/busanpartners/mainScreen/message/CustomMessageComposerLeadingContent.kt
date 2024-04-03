package com.kwonminseok.busanpartners.mainScreen.message

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.kwonminseok.busanpartners.databinding.CustomMessageComposerLeadingContentBinding
import io.getstream.chat.android.models.ChannelCapabilities
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.composer.MessageComposerState
import io.getstream.chat.android.ui.feature.messages.composer.MessageComposerContext
import io.getstream.chat.android.ui.feature.messages.composer.MessageComposerViewStyle
import io.getstream.chat.android.ui.feature.messages.composer.content.MessageComposerContent

class CustomMessageComposerLeadingContent : FrameLayout, MessageComposerContent {

    private lateinit var binding: CustomMessageComposerLeadingContentBinding
    private lateinit var style: MessageComposerViewStyle

    var attachmentsButtonClickListener: () -> Unit = {}
    // Click listener for the date picker button
    var calendarButtonClickListener: () -> Unit = {}
    var locationButtonClickListener: () -> Unit = {}

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        binding = CustomMessageComposerLeadingContentBinding.inflate(LayoutInflater.from(context), this)
        binding.attachmentsButton.setOnClickListener { attachmentsButtonClickListener()
        }

        // Set click listener for the date picker button
        binding.calendarButton.setOnClickListener { calendarButtonClickListener() }

        //지도
        binding.locationButton.setOnClickListener { locationButtonClickListener() }

    }

    override fun attachContext(messageComposerContext: MessageComposerContext) {
        this.style = messageComposerContext.style
    }

    override fun renderState(state: MessageComposerState) {
        val canSendMessage = state.ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
        val canUploadFile = state.ownCapabilities.contains(ChannelCapabilities.UPLOAD_FILE)
        val hasTextInput = state.inputValue.isNotEmpty()
        val hasAttachments = state.attachments.isNotEmpty()
        val hasCommandInput = state.inputValue.startsWith("/")
        val hasCommandSuggestions = state.commandSuggestions.isNotEmpty()
        val hasMentionSuggestions = state.mentionSuggestions.isNotEmpty()
        val isInEditMode = state.action is Edit

        binding.attachmentsButton.isEnabled =
            !hasCommandInput && !hasCommandSuggestions && !hasMentionSuggestions
        binding.attachmentsButton.isVisible =
            style.attachmentsButtonVisible && canSendMessage && canUploadFile && !isInEditMode
    }
}



