package com.ironraft.pupping.bero.scene.page.walk.model

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.walk.WalkEventType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.ironraft.pupping.bero.store.walk.WalkUiEventType
import com.skeleton.component.map.MapMarker
import com.skeleton.component.map.MapModel
import com.skeleton.component.map.MarkerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PlayMapUiEvent {
    ResetMap
}
enum class PlayZoomType {
    Close, Normal, FarAway
}
class PlayMapModel(
    val repo:PageRepository,
    val walkManager:WalkManager
): MapModel() {
    companion object{
        const val uiHeight:Float = 130f
        const val zoomRatio:Float = 17.0f
        const val zoomCloseup:Float = 18.5f
        const val zoomDefault:Float = 17.0f
        const val zoomOut:Float = 16.0f
        const val zoomFarAway:Float = 15f
        const val mapMoveDuration:Double = 0.5
        const val mapMoveAngle:Float = 0f //3D 맵사용시 설정
        const val zoomFarAwayView:Float = 14.5f
        const val zoomCloseView:Float = 18.0f
    }

    val playUiEvent: MutableLiveData<PlayMapUiEvent?> = MutableLiveData(null)
    val componentHidden: MutableLiveData<Boolean> = MutableLiveData(false)
    var isFollowMe:MutableLiveData<Boolean> = MutableLiveData(false)
    val isWalk:MutableLiveData<Boolean> = MutableLiveData(false)

    private var myLocationOn:BitmapDescriptor? = null
    private var myLocationOff:BitmapDescriptor? = null
    private var myWalkingOn:BitmapDescriptor? = null
    private var myWalkingOff:BitmapDescriptor? = null


    private var isForceMove = false
    override fun onInitMap() {
        super.onInitMap()
        myLocationOn = BitmapDescriptorFactory.fromResource(R.drawable.pin_my_location_on)
        myLocationOff = BitmapDescriptorFactory.fromResource(R.drawable.pin_my_location_off)
        myWalkingOn = BitmapDescriptorFactory.fromResource(R.drawable.pin_my_walking_on)
        myWalkingOff = BitmapDescriptorFactory.fromResource(R.drawable.pin_my_walking_off)
        if (isInit) resetMap()
    }
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        walkManager.status.observe(owner) { status ->
            val stat = status ?: return@observe
            when (stat) {
                WalkStatus.Walking -> isWalk.value = true
                WalkStatus.Ready -> isWalk.value = false
            }
        }
        walkManager.event.observe(owner) { event ->
            val evt = event ?: return@observe
            when (evt.type) {
                WalkEventType.ChangeMapStatus -> clearAll()
                //WalkEventType.UpdatedPlaces -> onMarkerUpdate()
               // WalkEventType.UpdatedUsers -> onMarkerUpdate()
                else -> {}
            }
        }
        walkManager.uiEvent.observe(owner) { uiEvent ->
            val evt = uiEvent ?: return@observe
            when (evt.type) {
                WalkUiEventType.MoveMap -> {
                    isFollowMe.value = false
                    evt.value?.loc?.let {
                       moveLocation(it, evt.value?.zoom ?: zoomCloseup)
                    }
                }
                else -> {}
            }
        }
        playUiEvent.observe(owner) { uiEvent ->
            val evt = uiEvent ?: return@observe
            when (evt) {
                PlayMapUiEvent.ResetMap -> resetMap()
            }
        }
    }
    private fun move(loc:LatLng){
        if (!isInitMap) return
        if (isInit) {
            moveMe(loc)
            return
        }
        resetMap()
    }
    private fun moveLocation(loc:LatLng, zoom:Float){
        if (!isInitMap) return
        move(loc = loc, zoom = zoom, duration = mapMoveDuration)
        forceMoveLock()
    }
    private fun resetMap(){
        if (!isInitMap) {
            isInit = true
            return
        }
        if (isForceMove) return
        val location = walkManager.currentLocation.value
        val followMe = isFollowMe.value ?: false
        angle = if (followMe) mapMoveAngle else 0f
        zoom = if (followMe) zoomCloseup else zoomRatio
        location?.let { loc->
            isInit = true
            move(loc = loc, zoom = zoom, duration = mapMoveDuration)
            forceMoveLock{
                moveMe(loc, isMove = true)
            }
        }
    }

    private fun moveMe(loc:LatLng, isMove:Boolean? = null){
        if (!isInitMap) return
        if (isForceMove) return
        val move = isMove ?: isFollowMe.value ?: false
        /*
        var rotate:Double? = nil
        if let target = self.walkManager.currentMission?.location?.coordinate {
            let targetPoint = CGPoint(x: target.latitude, y: target.longitude)
            let mePoint = CGPoint(x: loc.coordinate.latitude, y: loc.coordinate.longitude)
            rotate = mePoint.getAngleBetweenPoints(target: targetPoint)
        }
        */
        me(getMyMarker(move))
        if (move) move(loc, zoom = null)
    }

    private fun getMyMarker(move: Boolean = false): MapMarker {
        val loc = position ?: walkManager.currentLocation.value ?: LatLng(0.0, 0.0)
        val marker = getMe(loc)
        return MapMarker(
            id = "me",
            marker = marker,
            isRotationMap = move
        )
    }

    private fun getMe(loc: LatLng): MarkerData {
        val followMe = isFollowMe.value ?: false
        val icon = if (isWalk.value == true) {
            if (followMe) myWalkingOn else myWalkingOff
        } else {
            if (followMe) myLocationOn else myLocationOff
        }
        val user = repo.dataProvider.user
        return MarkerData(
            key = "me",
            position = loc,
            title = user.representativePet.value?.name?.value ?: "me",
            anchor = Offset(0.5f, 0.5f),
            infoWindowAnchor = Offset(0.5f, 0.18f),
            icon = icon,
            zIndex = 700f
        )
    }


    private fun forceMoveLock(delayTime:Double = 0.0, closer:(() -> Unit)? = null){
        isForceMove = true
        scope?.launch(Dispatchers.Default) {
            delay((mapMoveDuration + delayTime).toLong() * 1000L)
            withContext(Dispatchers.Main) {
                isForceMove = false
                closer?.let { it() }
            }
        }
    }
}
