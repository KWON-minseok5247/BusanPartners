package com.kwonminseok.busanpartners.mainScreen.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ItemDateAttachmentBinding
import com.kwonminseok.busanpartners.databinding.LocationAttachementViewBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
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

//        init {
//            // 이미지 클릭 리스너 설정
//            binding.snapshotView.setOnClickListener {
//                listeners?.attachmentClickListener?.onAttachmentClick(
//                    message,
//                    message.attachments.first()
//                )
//            }
//            // 이미지 롱 클릭 리스너 설정
//            binding.snapshotView.setOnLongClickListener {
//                listeners?.messageLongClickListener?.onMessageLongClick(message)
//                true
//            }
//        }

        override fun onBindViewHolder(message: Message) {
            this.message = message

            // 메시지 어태치먼트에서 스냅샷 이미지 경로를 가져옵니다.
            val latitude = message.attachments
                .first { it.type == "location" }
                .extraData["latitude"] as Double

            val longitude = message.attachments
                .first { it.type == "location" }
                .extraData["longitude"] as Double

            val mapView = binding.mapView
            mapView.onCreate(Bundle())
            mapView.getMapAsync { naverMap ->
                naverMap.apply {
                    // 카메라 위치와 줌 레벨을 설정합니다.
                    // 예를 들어, 서울 시청의 위도와 경도를 사용합니다.
                    val location = LatLng(latitude, longitude)

                    // 카메라를 특정 위치로 이동시킵니다.
                    moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))

                    // 최소 줌 레벨을 설정합니다. (1~21 사이의 값을 사용할 수 있습니다.)
                    minZoom = 14.0

                    // 마커 생성 및 지도에 추가
                    val marker = Marker()
                    marker.position = location
                    marker.map = this // 이 코드를 실행하는 시점에서 naverMap 객체를 가리킵니다.

                    //거리 안보이게 만들기
                    uiSettings.isScaleBarEnabled = false

                    // 줌 안보이게 하기
                    uiSettings.isZoomControlEnabled = false

                }

            }

        }
    }
}