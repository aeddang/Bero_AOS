package com.ironraft.pupping.bero.scene.page.walk.model

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.lib.page.ComponentViewModel

enum class PlayMapUiEvent {
    ResetMap
}
enum class PlayZoomType {
    Close, Normal, FarAway
}
class PlayMapModel: ComponentViewModel() {
    val playUiEvent: MutableLiveData<PlayMapUiEvent?> = MutableLiveData(null)
    val componentHidden: MutableLiveData<Boolean> = MutableLiveData(false)
    var position: LatLng? = null
    var isFollowMe:MutableLiveData<Boolean> = MutableLiveData(false)

    companion object{
        const val uiHeight:Float = 130f
        const val zoomRatio:Float = 17.0f
        const val zoomCloseup:Float = 18.5f
        const val zoomDefault:Float = 17.0f
        const val zoomOut:Float = 16.0f
        const val zoomFarAway:Float = 15f
        const val mapMoveDuration:Double = 0.5
        const val mapMoveAngle:Double = 0.0 //3D 맵사용시 설정
        const val routeViewDuration:Double = 4.0
        const val zoomFarAwayView:Float = 14.5f
        const val zoomCloseView:Float = 18.0f
    }
}
