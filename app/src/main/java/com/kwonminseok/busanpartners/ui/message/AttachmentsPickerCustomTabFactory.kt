package com.kwonminseok.busanpartners.ui.message

import android.content.Context
import android.content.Intent
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
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentPickerItemState
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentsPickerMode
import io.getstream.chat.android.compose.state.messages.attachments.CustomPickerMode
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.ui.common.state.messages.composer.AttachmentMetaData

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

    @Composable
    override fun PickerTabContent(
        attachments: List<AttachmentPickerItemState>,
        onAttachmentsChanged: (List<AttachmentPickerItemState>) -> Unit,
        onAttachmentItemSelected: (AttachmentPickerItemState) -> Unit,
        onAttachmentsSubmitted: (List<AttachmentMetaData>) -> Unit,
    ) {
        val context = LocalContext.current

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
                context.startActivity(Intent(context, ShareLocationActivity::class.java))
            }) {

            }
        }
    }
}


