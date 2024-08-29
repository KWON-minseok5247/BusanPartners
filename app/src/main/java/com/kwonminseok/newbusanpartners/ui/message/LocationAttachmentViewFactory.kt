package com.kwonminseok.newbusanpartners.ui.message

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.databinding.LocationAttachementViewBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListListenerContainer
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactory
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.InnerAttachmentViewHolder

//class LocationAttachmentViewFactory(
//    private val lifecycleOwner: LifecycleOwner, // LifecycleOwner를 생성 시 받도록 함
//) : AttachmentFactory {
//
//
//    override fun canHandle(message: Message): Boolean {
//        return message.attachments.any { it.type == "location" }
//    }
//
//    override fun createViewHolder(
//        message: Message,
//        listeners: MessageListListenerContainer?,
//        parent: ViewGroup
//    ): InnerAttachmentViewHolder {
//        // FragmentManager를 가져오기 위해 Context를 FragmentActivity로 캐스팅
////        val fragmentManager = (parent.context as? FragmentActivity)?.supportFragmentManager
////            ?: throw IllegalStateException("Context cannot be cast to FragmentActivity")
//
//        return LocationAttachementViewBinding
//            .inflate(LayoutInflater.from(parent.context), parent, false)
//            .let {
//                LocationAttachmentViewHolder(
//                    it,
//                    lifecycleOwner,
//                    listeners
//                )
//            } // FragmentManager 전달
//
////        return LocationAttachementViewBinding
////            .inflate(LayoutInflater.from(parent.context), parent, false)
////            .let { LocationAttachmentViewHolder(it, listeners) }
//    }
//
//    class LocationAttachmentViewHolder(
//        private val binding: LocationAttachementViewBinding,
////        private val fragmentManager: FragmentManager, // FragmentManager 추가
//        private val lifecycleOwner: LifecycleOwner,
//        listeners: MessageListListenerContainer?,
//        ) : InnerAttachmentViewHolder(binding.root), LifecycleObserver {
//
//        private lateinit var message: Message
//
//        private val mapView: MapView = binding.mapView.apply {
////            onCreate(null) // MapView 초기화
//        }
//
//
//        init {
//            // 이미지 클릭 리스너 설정
//
//            lifecycleOwner.lifecycle.addObserver(this) // Lifecycle Observer를 등록
//
//
//
//            // 이미지 롱 클릭 리스너 설정
//            mapView.setOnLongClickListener {
////                listeners?.messageLongClickListener?.onMessageLongClick(message)
//
//                true
//            }
//        }
//
//
//
//
//        override fun onBindViewHolder(message: Message) {
//            this.message = message
//
//
//            val latitude = message.attachments
//                .first { it.type == "location" }
//                .extraData["latitude"] as Double
//
//            val longitude = message.attachments
//                .first { it.type == "location" }
//                .extraData["longitude"] as Double
//
////            val mapView = binding.mapView
//            mapView.onCreate(Bundle())
//
//            mapView.getMapAsync { naverMap ->
//                naverMap.apply {
//                    // 카메라 위치와 줌 레벨을 설정합니다.
//                    // 예를 들어, 서울 시청의 위도와 경도를 사용합니다.
//                    val location = LatLng(latitude, longitude)
//
//                    // 카메라를 특정 위치로 이동시킵니다.
//                    moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
//
//                    // 최소 줌 레벨을 설정합니다. (1~21 사이의 값을 사용할 수 있습니다.)
//                    minZoom = 14.0
//
//                    // 마커 생성 및 지도에 추가
//                    val marker = Marker()
//                    marker.position = location
//                    marker.map = this // 이 코드를 실행하는 시점에서 naverMap 객체를 가리킵니다.
//
//                    //거리 안보이게 만들기
//                    uiSettings.isScaleBarEnabled = false
//
//                    // 줌 안보이게 하기
//                    uiSettings.isZoomControlEnabled = false
//
//                }
//
//            }
//            lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
//                @OnLifecycleEvent(Lifecycle.Event.ON_START)
//                fun start() {
//                    mapView.onStart()
//                    mapView.onResume()
//                }
//
//                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//                fun stop() {
//                    mapView.onPause()
//                    mapView.onStop()
//                }
//
//                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//                fun destroy() {
//                    mapView.onDestroy()
//                    lifecycleOwner.lifecycle.removeObserver(this)
//                }
//            })
//            binding.transparentView.setOnClickListener {
////                listeners?.attachmentClickListener?.onAttachmentClick(
////                    message,
////                    message.attachments.first()
////                )
////                Log.e("hihi", "hihi")
//                // 위치 정보가 포함된 메시지의 위도와 경도를 가져옵니다.
//                // MapActivity를 시작하는 인텐트 생성
//                val intent = Intent(context, AttachmentMapActivity::class.java).apply {
//                    putExtra("latitude", latitude)
//                    putExtra("longitude", longitude)
//                }
//                context.startActivity(intent) // MapActivity 시작
//
//            }
//
////            // 동적으로 FrameLayout 생성
////            val mapContainer = FrameLayout(binding.root.context).apply {
////                id = mapContainerId // 고유한 ID 할당
////                Log.e("mapContainer id", id.toString())
////
////                // Convert dp to pixel
////                val heightInPixels = TypedValue.applyDimension(
////                    TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics
////                ).toInt()
////
////                layoutParams = ViewGroup.LayoutParams(
////                    ViewGroup.LayoutParams.MATCH_PARENT, // 너비는 매치 부모
////                    heightInPixels // 200dp를 픽셀로 변환한 높이 값
////                )
////            }
////
//// 동적으로 FrameLayout 생성
////            val mapContainerId = View.generateViewId() // 매번 새로운 ID 생성
////            val mapContainer = FrameLayout(binding.root.context).apply {
////                id = mapContainerId
////
////                // Convert dp to pixel
////                val heightInPixels = TypedValue.applyDimension(
////                    TypedValue.COMPLEX_UNIT_DIP, 200f, binding.root.context.resources.displayMetrics
////                ).toInt()
////
////                layoutParams = ViewGroup.LayoutParams(
////                    ViewGroup.LayoutParams.MATCH_PARENT, // 너비는 매치 부모
////                    heightInPixels // 200dp를 픽셀로 변환한 높이 값
////                )
////            }
////            // 동적으로 생성된 FrameLayout을 레이아웃에 추가
////            binding.root.addView(mapContainer)
//
//            // MapFragment 인스턴스 생성 및 설정
////            val mapFragment = fragmentManager.findFragmentById(R.id.map_container) as MapFragment?
////                ?: MapFragment.newInstance().also {
////                    fragmentManager.beginTransaction().add(R.id.map_container, it).commit()
////                }
////
////
////            mapFragment.getMapAsync { naverMap ->
////                // 지도 설정
////                val location = LatLng(
////                    message.attachments.first { it.type == "location" }.extraData["latitude"] as Double,
////                    message.attachments.first { it.type == "location" }.extraData["longitude"] as Double
////                )
////
////                naverMap.moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
////                    naverMap.minZoom = 14.0
////
////                Marker().apply {
////                    position = location
////                    map = naverMap
////                }
////
////                naverMap.uiSettings.apply {
////                    isScaleBarEnabled = false
////                    isZoomControlEnabled = false
////                }
////
////            }
////            //            (binding.root.context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, mapFragment)?.commit()
////            // fragmentManager를 사용하여 MapFragment를 추가합니다.
//////            fragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit()
////
////        }
//
//
////            val latitude = message.attachments
////                .first { it.type == "location" }
////                .extraData["latitude"] as Double
////            val longitude = message.attachments
////                .first { it.type == "location" }
////                .extraData["longitude"] as Double
////
////
////            // MapFragment를 동적으로 추가하기 위한 FrameLayout 컨테이너 ID가 필요합니다.
////            // 예를 들어, 레이아웃에 <FrameLayout android:id="@+id/map_container" .../>가 있다고 가정합니다.
////            val mapFragment = MapFragment.newInstance().apply {
////                getMapAsync { naverMap ->
////                    // 지도 설정
////                    val location = LatLng(latitude, longitude)
////                    naverMap.moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
////                    naverMap.minZoom = 14.0
////
////                    // 마커 생성 및 추가
////                    Marker().apply {
////                        position = location
////                        map = naverMap
////                    }
////
////                    // UI 설정 변경
////                    naverMap.uiSettings.apply {
////                        isScaleBarEnabled = false
////                        isZoomControlEnabled = false
////                    }
////                }
////            }
////
////            // MapFragment를 액티비티의 FragmentManager를 사용하여 레이아웃에 추가합니다.
////            // 이 작업을 위해서는 ViewHolder나 이를 사용하는 컨텍스트가 FragmentManager에 접근할 수 있어야 합니다.
////            // 이 예제에서는 FragmentActivity의 인스턴스를 직접 사용하는 방식을 가정합니다.
//////            (binding.root.context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, mapFragment)?.commit()
////            // fragmentManager를 사용하여 MapFragment를 추가합니다.
////            fragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit()
////
////
//
//
//        }
//
//    }
//}
class LocationAttachmentViewFactory(
    private val lifecycleOwner: LifecycleOwner
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
            .let {
                LocationAttachmentViewHolder(
                    it,
                    lifecycleOwner,
                    listeners
                )
            }
    }

    class LocationAttachmentViewHolder(
        private val binding: LocationAttachementViewBinding,
        private val lifecycleOwner: LifecycleOwner,
        listeners: MessageListListenerContainer?
    ) : InnerAttachmentViewHolder(binding.root), LifecycleObserver {

        private lateinit var message: Message

        private val mapView: MapView = binding.mapView.apply {
            onCreate(null) // MapView 초기화
        }

        init {
            lifecycleOwner.lifecycle.addObserver(this) // Lifecycle Observer를 등록

            mapView.setOnLongClickListener {
                true
            }

            binding.composeView.setContent {
                LocationAttachmentContent()
            }
        }

        override fun onBindViewHolder(message: Message) {
            this.message = message

            val latitude = message.attachments
                .first { it.type == "location" }
                .extraData["latitude"] as Double

            val longitude = message.attachments
                .first { it.type == "location" }
                .extraData["longitude"] as Double

            mapView.getMapAsync { naverMap ->
                naverMap.apply {
                    val location = LatLng(latitude, longitude)
                    moveCamera(CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing))
                    minZoom = 14.0

                    val marker = Marker()
                    marker.position = location
                    marker.map = this

                    uiSettings.isScaleBarEnabled = false
                    uiSettings.isZoomControlEnabled = false
                }
            }

            binding.transparentView.setOnClickListener {
                val intent = Intent(binding.root.context, AttachmentMapActivity::class.java).apply {
                    putExtra("latitude", latitude)
                    putExtra("longitude", longitude)
                }
                binding.root.context.startActivity(intent)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            mapView.onStart()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            mapView.onResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            mapView.onPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            mapView.onStop()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            mapView.onDestroy()
            lifecycleOwner.lifecycle.removeObserver(this)
        }

        @Composable
        fun LocationAttachmentContent() {
            val context = LocalContext.current

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .clickable {
                        val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                            putExtra("latitude", message.attachments
                                .first { it.type == "location" }
                                .extraData["latitude"] as Double)
                            putExtra("longitude", message.attachments
                                .first { it.type == "location" }
                                .extraData["longitude"] as Double)
                        }
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
