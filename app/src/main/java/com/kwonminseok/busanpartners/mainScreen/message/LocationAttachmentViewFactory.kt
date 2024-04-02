//package com.kwonminseok.busanpartners.mainScreen.message
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleObserver
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.OnLifecycleEvent
//import com.getstream.sdk.chat.adapter.MessageListItem
//import com.kwonminseok.busanpartners.databinding.LocationAttachementViewBinding
//import com.naver.maps.geometry.LatLng
//import com.naver.maps.map.MapView
//import io.getstream.chat.android.ui.feature.messages.list.MessageListItemStyle
//import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListItem
//import io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListListenerContainer
//
//class LocationAttachmentViewFactory(
//    private val lifecycleOwner: LifecycleOwner
//) : AttachmentView() {
//    private lateinit var mapView: MapView
//
//    override fun createAttachmentView(
//        data: MessageListItem.MessageItem,
//        listeners: MessageListListenerContainer,
//        style: MessageListItemStyle,
//        parent: ViewGroup
//    ): View {
//        val location = data.message.attachments.find { it.type == "location" }
//        return if (location != null) {
//            val lat = location.extraData["latitude"] as Double
//            val long = location.extraData["longitude"] as Double
//            val latLng = LatLng(lat, long)
//            createLocationView(parent, latLng)
//        } else {
//            super.createAttachmentView(data, listeners, style, parent)
//        }
//    }
//
//    private fun createLocationView(parent: ViewGroup, location: LatLng): View {
//        val binding = LocationAttachementViewBinding
//            .inflate(LayoutInflater.from(parent.context), parent, false)
//
//        mapView = binding.mapView
//        mapView.onCreate(Bundle())
//        mapView.getMapAsync { googleMap ->
////            googleMap.setMinZoomPreference(18f)
////            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
//        }
//
//        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
//            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//            fun destroyMapView() {
//                mapView.onDestroy()
//            }
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_START)
//            fun startMapView() {
//                mapView.onStart()
//            }
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//            fun resumeMapView() {
//                mapView.onResume()
//            }
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//            fun stopMapView() {
//                mapView.onStop()
//            }
//        })
//
//        return binding.root
//    }
//}