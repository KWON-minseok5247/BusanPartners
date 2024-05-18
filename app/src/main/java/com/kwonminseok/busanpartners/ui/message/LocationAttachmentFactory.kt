/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kwonminseok.busanpartners.ui.message

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.kwonminseok.busanpartners.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import io.getstream.chat.android.client.utils.observable.Disposable
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.compose.ui.components.CancelIcon
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.Attachment

/**
 * A custom [AttachmentFactory] that adds support for date attachments.
 */
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

/**
 * Represents the UI shown in the message input preview before sending.
 *
 * @param attachments Selected attachments.
 * @param onAttachmentRemoved Handler when the user removes an attachment.
 * @param modifier Modifier for styling.
 */
@Composable
fun LocationAttachmentPreviewContent(
    attachments: List<Attachment>,
    onAttachmentRemoved: (Attachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val attachment = attachments.first { it.type == "location" }
    val formattedDate = attachment.extraData["payload"].toString()

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
            text = formattedDate,
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

/**
 * Represents the UI shown in the message list for date attachments.
 *
 * @param attachmentState The state of the attachment.
 * @param modifier Modifier for styling.
 */
//@Composable
//fun LocationAttachmentContent(
//    attachmentState: AttachmentState,
//    modifier: Modifier = Modifier,
//) {
//    val attachment = attachmentState.message.attachments.first { it.type == "location" }
//    val formattedDate = attachment.extraData["payload"].toString()
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(4.dp)
//            .clip(ChatTheme.shapes.attachment)
//            .background(ChatTheme.colors.infoAccent)
//            .padding(8.dp),
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Icon(
//                modifier = Modifier.size(16.dp),
//                painter = painterResource(id = R.drawable.ic_calendar),
//                contentDescription = null,
//                tint = ChatTheme.colors.textHighEmphasis,
//            )
//
//            Text(
//                text = formattedDate,
//                style = ChatTheme.typography.body,
//                maxLines = 1,
//                color = ChatTheme.colors.textHighEmphasis,
//            )
//        }
//    }
//}

//@Composable
//fun LocationAttachmentContent(
//    attachmentState: AttachmentState,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val attachment = attachmentState.message.attachments.first { it.type == "location" }
//    val latitude = attachment.extraData["latitude"] as? Double ?: 0.0
//    val longitude = attachment.extraData["longitude"] as? Double ?: 0.0
//    val location = LatLng(latitude, longitude)
//
//    val mapView = remember { MapView(context) }
//
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
//                Lifecycle.Event.ON_START -> mapView.onStart()
//                Lifecycle.Event.ON_RESUME -> mapView.onResume()
//                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
//                Lifecycle.Event.ON_STOP -> mapView.onStop()
//                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(4.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFE1F5FE))
//            .padding(8.dp),
//    ) {
//        AndroidView(
//            factory = {
//                mapView.apply {
//                    getMapAsync { naverMap ->
//                        naverMap.apply {
//                            moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
//                            minZoom = 14.0
//
//                            Marker().apply {
//                                position = location
//                                map = naverMap
//                            }
//
//                            uiSettings.apply {
//                                isScaleBarEnabled = false
//                                isZoomControlEnabled = false
//                            }
//                        }
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//        )
//
//        // Transparent view for clicking to open AttachmentMapActivity
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .background(Color.Transparent)
//                .clickable {
//                    val intent = Intent(context, AttachmentMapActivity::class.java).apply {
//                        putExtra("latitude", latitude)
//                        putExtra("longitude", longitude)
//                    }
//                    context.startActivity(intent)
//                }
//        )
//    }
//}

//@Composable
//fun LocationAttachmentContent(
//    attachmentState: AttachmentState,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val attachment = attachmentState.message.attachments.first { it.type == "location" }
//    val latitude = attachment.extraData["latitude"] as? Double ?: 0.0
//    val longitude = attachment.extraData["longitude"] as? Double ?: 0.0
//    val location = LatLng(latitude, longitude)
//
//    val mapView = remember { MapView(context) }
//
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
//                Lifecycle.Event.ON_START -> mapView.onStart()
//                Lifecycle.Event.ON_RESUME -> mapView.onResume()
//                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
//                Lifecycle.Event.ON_STOP -> mapView.onStop()
//                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(4.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFE1F5FE))
//            .padding(8.dp),
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .clickable {
//                    val intent = Intent(context, AttachmentMapActivity::class.java).apply {
//                        putExtra("latitude", latitude)
//                        putExtra("longitude", longitude)
//                    }
//                    context.startActivity(intent)
//                }
//        ) {
//            AndroidView(
//                factory = {
//                    mapView.apply {
//                        getMapAsync { naverMap ->
//                            naverMap.apply {
//                                moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
//                                minZoom = 14.0
//
//                                Marker().apply {
//                                    position = location
//                                    map = naverMap
//                                }
//
//                                uiSettings.apply {
//                                    isScaleBarEnabled = false
//                                    isZoomControlEnabled = false
//                                }
//                            }
//                        }
//                    }
//                },
//                modifier = Modifier.matchParentSize()
//            )
//        }
//    }
//}

@Composable
fun LocationAttachmentContent(
    attachmentState: AttachmentState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val attachment = attachmentState.message.attachments.first { it.type == "location" }
    val latitude = attachment.extraData["latitude"] as? Double ?: 0.0
    val longitude = attachment.extraData["longitude"] as? Double ?: 0.0
    val location = LatLng(latitude, longitude)


    val mapView = rememberMapViewWithLifecycle()


//    val mapView = remember { MapView(context) }

//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
//                Lifecycle.Event.ON_START -> mapView.onStart()
//                Lifecycle.Event.ON_RESUME -> mapView.onResume()
//                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
//                Lifecycle.Event.ON_STOP -> mapView.onStop()
//                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFE1F5FE))
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                        putExtra("latitude", latitude)
                        putExtra("longitude", longitude)
                    }
                    context.startActivity(intent)
                }
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        getMapAsync { naverMap ->
                            naverMap.apply {
                                moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
                                minZoom = 14.0


                                Marker().apply {
                                    position = location
                                    map = naverMap
                                }

                                uiSettings.apply {
                                    isScrollGesturesEnabled = false
                                    isZoomGesturesEnabled = false
                                    isTiltGesturesEnabled = false
                                    isRotateGesturesEnabled = false
                                    isScaleBarEnabled = false
                                    isZoomControlEnabled = false
                                }

                                setOnMapClickListener { pointF, latLng ->
                                        val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                                            putExtra("latitude", latitude)
                                            putExtra("longitude", longitude)
                                        }
                                        context.startActivity(intent)

                                }
                            }
                        }
                    }
                },
                modifier = Modifier.matchParentSize()
            )
            }

        Text(
            text = "지도 공유",
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.Black
        )

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



//@Composable
//fun LocationAttachmentContent(
//    attachmentState: AttachmentState,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//    val attachment = attachmentState.message.attachments.first { it.type == "location" }
//    val snapshotUri = attachment.extraData["snapshot_uri"] as? String
//    val b = snapshotUri?.toUri()
//    Log.e("b", b.toString())
//
//    val imageUri = snapshotUri?.let { Uri.parse(it) }
//    Log.e("imageUri", imageUri.toString())
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(4.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFE1F5FE))
//            .padding(8.dp),
//    ) {
//        imageUri?.let {
//            Image(
//                painter = rememberAsyncImagePainter(it),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color.Gray)
//            )
//        }
//    }
//}
