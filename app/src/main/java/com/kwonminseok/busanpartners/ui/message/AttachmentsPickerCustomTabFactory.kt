package com.kwonminseok.busanpartners.ui.message

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentPickerItemState
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentsPickerMode
import io.getstream.chat.android.compose.state.messages.attachments.CustomPickerMode
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.common.state.messages.composer.AttachmentMetaData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//open class AttachmentsPickerCustomTabFactory : AttachmentsPickerTabFactory {
//
//    override val attachmentsPickerMode: AttachmentsPickerMode
//        get() = CustomPickerMode()
//
//    fun isPickerTabEnabled(): Boolean {
//        return true
//    }
//
//    @Composable
//    override fun PickerTabIcon(isEnabled: Boolean, isSelected: Boolean) {
//        Icon(
//            imageVector = Icons.Default.AddLocation,
//            contentDescription = "Custom tab",
//            tint = when {
//                isSelected -> ChatTheme.colors.primaryAccent
//                isEnabled -> ChatTheme.colors.textLowEmphasis
//                else -> ChatTheme.colors.disabled
//            },
//        )
//    }
//
//    @OptIn(ExperimentalStreamChatApi::class)
//    @Composable
//    override fun PickerTabContent(
//        attachments: List<AttachmentPickerItemState>,
//        onAttachmentsChanged: (List<AttachmentPickerItemState>) -> Unit,
//        onAttachmentItemSelected: (AttachmentPickerItemState) -> Unit,
//        onAttachmentsSubmitted: (List<AttachmentMetaData>) -> Unit,
//    ) {
//        val locationLauncher = rememberLauncherForActivityResult(
//            contract = ChannelActivity.LocationActivityResultContract()
//        ) { result ->
//            result?.let { location ->
//                val attachment = Attachment(
//                    type = "location",
//                    extraData = mutableMapOf(
//                        "latitude" to location.latitude,
//                        "longitude" to location.longitude,
//                    )
//                )
//
//                onAttachmentsSubmitted(listOf(AttachmentMetaData(attachment)))
//            }
//        }
//
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Button(onClick = {
//                MaterialDatePicker.Builder
//                            .datePicker()
//                            .build()
//                            .apply {
//                                activity?.let { show(it.supportFragmentManager, null) }
//                                addOnPositiveButtonClickListener {
////                                    onDateSelected(it)
//                                }
//                            }
////                locationLauncher.launch(null)
//            }) {
//                Text("위치 공유")
//            }
//        }
//    }
//}
class AttachmentsPickerCustomTabFactory: AttachmentsPickerTabFactory {

    override val attachmentsPickerMode: AttachmentsPickerMode
        get() = CustomPickerMode()

    fun isPickerTabEnabled(): Boolean {
        // Place your custom logic here to be able to disable the tab if needed.
        // Return true if the tab should be enabled.
        return true
    }

    @Composable
    override fun PickerTabIcon(isEnabled: Boolean, isSelected: Boolean) {
        Icon(
            imageVector = Icons.Default.AddLocation,
            contentDescription = "Custom tab",
            tint = when {
                isSelected -> ChatTheme.colors.primaryAccent
                isEnabled -> ChatTheme.colors.textLowEmphasis
                else -> ChatTheme.colors.disabled
            },
        )
    }

    @OptIn(ExperimentalStreamChatApi::class)
    @Composable
    override fun PickerTabContent(
        attachments: List<AttachmentPickerItemState>,
        onAttachmentsChanged: (List<AttachmentPickerItemState>) -> Unit,
        onAttachmentItemSelected: (AttachmentPickerItemState) -> Unit,
        onAttachmentsSubmitted: (List<AttachmentMetaData>) -> Unit,
    ) {
        val context = LocalContext.current
        val locationLauncher = rememberLauncherForActivityResult(
            contract = ChannelActivity.LocationActivityResultContract()
        ) { result ->
            result?.let { location ->
                val attachment = Attachment(
                    type = "location",
                    extraData = mutableMapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                    )
                )
//                composerViewModel.addSelectedAttachments(listOf(attachment))

                val message = Message(
                    cid = "messaging:!members-zpdKwxmT5xg3bH4HXljyiB0_EWX9Vno99BXhn8fzt40",
                    text = "지도 공유",
                    attachments = mutableListOf(attachment)
                )
                Log.e("message", message.toString())

                chatClient.channel("messaging:!members-zpdKwxmT5xg3bH4HXljyiB0_EWX9Vno99BXhn8fzt40").sendMessage(message).enqueue { result ->
                    if (result.isSuccess) {
                        Log.e("지도 사진 처리 성공", "성공")
                    } else {
                        Log.e("지도 사진 처리 실패", result.toString())
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            onAttachmentsChanged(emptyList())
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                style = ChatTheme.typography.title3Bold,
                text = "Custom tab",
                color = ChatTheme.colors.textHighEmphasis,
            )
            Button(onClick = {
//                showDatePickerDialog(context, onAttachmentsSubmitted)

                locationLauncher.launch(null)
//                ChannelActivity.LocationActivityResultContract()
//                context.startActivity(Intent(context, ShareLocationActivity::class.java))
            }) {
                Text("Pick a Date")
            }
        }
    }

    @OptIn(ExperimentalStreamChatApi::class)
    private fun showDatePickerDialog(context: Context, onAttachmentsSubmitted: (List<AttachmentMetaData>) -> Unit) {
        val activity = context as? FragmentActivity
        activity?.let {
            MaterialDatePicker.Builder.datePicker().build().apply {
                show(it.supportFragmentManager, null)
                addOnPositiveButtonClickListener { date ->
                    val attachmentMetaData = createDateAttachment(date)
                    onAttachmentsSubmitted(listOf(attachmentMetaData))
                }
            }
        }
    }

    @OptIn(ExperimentalStreamChatApi::class)
    private fun createDateAttachment(date: Long): AttachmentMetaData {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date))
        return AttachmentMetaData(
            title = "Selected date: $formattedDate",
            mimeType = "text/plain"
        )
    }

}




