package com.kwonminseok.newbusanpartners.ui.message

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.compose.ui.components.CancelIcon
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.Attachment

val locationAttachmentFactory: AttachmentFactory = AttachmentFactory(
    canHandle = { attachments -> attachments.any { it.type == "location" } },
    content = @Composable { modifier, attachmentState ->
        LocationAttachmentContent(
            modifier = modifier,
            attachmentState = attachmentState,
        )
    },
    previewContent = { modifier, attachments, onAttachmentRemoved ->
        LocationAttachmentPreviewContent(
            modifier = modifier,
            attachments = attachments,
            onAttachmentRemoved = onAttachmentRemoved,
        )
    },
    textFormatter = { attachment ->
        attachment.extraData["payload"].toString()
    },
)

@Composable
fun LocationAttachmentPreviewContent(
    attachments: List<Attachment>,
    onAttachmentRemoved: (Attachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val attachment = attachments.first { it.type == "location" }

    Box(
        modifier = modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(color = ChatTheme.colors.barsBackground),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .fillMaxWidth(),
            text = "Share Map",
            style = ChatTheme.typography.body,
            maxLines = 1,
            color = ChatTheme.colors.textHighEmphasis,
        )

        CancelIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            onClick = { onAttachmentRemoved(attachment) },
        )
    }
}

@Composable
fun LocationAttachmentContent(
    attachmentState: AttachmentState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val attachment = attachmentState.message.attachments.first { it.type == "location" }
    val latitude = attachment.extraData["latitude"] as? Double ?: 0.0
    val longitude = attachment.extraData["longitude"] as? Double ?: 0.0
    val location = LatLng(latitude, longitude)
    val mapView = rememberMapViewWithLifecycle()

    LaunchedEffect(mapView) {
        mapView.getMapAsync { naverMap ->
            naverMap.moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
            naverMap.minZoom = 14.0

            Marker().apply {
                position = location
                map = naverMap
            }

            naverMap.uiSettings.apply {
                isScrollGesturesEnabled = false
                isZoomGesturesEnabled = false
                isTiltGesturesEnabled = false
                isRotateGesturesEnabled = false
                isScaleBarEnabled = false
                isZoomControlEnabled = false
            }

            naverMap.setOnMapClickListener { _, _ ->
                val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                    putExtra("latitude", latitude)
                    putExtra("longitude", longitude)
                }
                context.startActivity(intent)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                            putExtra("latitude", latitude)
                            putExtra("longitude", longitude)
                        }
                        context.startActivity(intent)
                    }
            ) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.matchParentSize()
                )
            }

            Text(
                text = "Share Map",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.Black,
                style = ChatTheme.typography.body
            )
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return mapView
}
