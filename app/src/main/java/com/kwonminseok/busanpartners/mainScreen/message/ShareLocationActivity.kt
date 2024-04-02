package com.kwonminseok.busanpartners.mainScreen.message

import com.kwonminseok.busanpartners.databinding.ActivityShareLocationBinding

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityChannelBinding
import com.naver.maps.map.MapView
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.AttachmentPreviewFactoryManager
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.AudioRecordAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.FileAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.MediaAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager
import io.getstream.chat.android.ui.helper.SupportedReactions
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.messages.bindView
import java.text.SimpleDateFormat
import java.util.Date

class ShareLocationActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private lateinit var binding: ActivityShareLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityShareLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }

}
