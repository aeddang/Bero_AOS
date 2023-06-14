package com.ironraft.pupping.bero.scene.page.walk.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.request.ImageRequest
import coil.target.Target
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEvent
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEventType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupData
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.walk.WalkEventType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.ironraft.pupping.bero.store.walk.WalkUiEventType
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.ironraft.pupping.bero.store.walk.model.Place
import com.lib.util.cropCircle
import com.lib.util.toDp
import com.skeleton.component.map.CircleData
import com.skeleton.component.map.MapCircle
import com.skeleton.component.map.MapMarker
import com.skeleton.component.map.MapModel
import com.skeleton.component.map.MapUserData
import com.skeleton.component.map.MarkerData
import com.skeleton.theme.DimenProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

enum class PlayMapUiEvent {
    ResetMap
}
enum class PlayZoomType {
    Close, Normal, FarAway
}
class PlayMapModel(
    val repo:PageRepository,
    val walkManager:WalkManager,
    val viewModel: PageWalkViewModel
): MapModel() {
    companion object{
        const val uiHeight:Float = 130f
        const val zoomRatio:Float = 17.0f
        const val zoomCloseup:Float = 18.5f
        const val zoomDefault:Float = 16.5f
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
    private var emptyUser:BitmapDescriptor? = null
    private var pinPlace:BitmapDescriptor? = null
    private var pinUsers:List<BitmapDescriptor> = listOf()

    private var zoomType = PlayZoomType.Normal
    private var isForceMove = false
    override fun onInitMap() {
        super.onInitMap()
        repo.ctx.let { ctx->
            myLocationOn = bitMapFromVector(ctx, R.drawable.pin_my_location_off)
            myLocationOff = bitMapFromVector(ctx, R.drawable.pin_my_location_off)
            myWalkingOn = bitMapFromVector(ctx, R.drawable.pin_my_walking_on)
            myWalkingOff = bitMapFromVector(ctx, R.drawable.pin_my_walking_off)
            emptyUser = bitMapFromVector(ctx, R.drawable.pin_user)
            pinPlace = bitMapFromVector(repo.ctx, R.drawable.pin_mission)
            pinUsers = (1..7).map {
                val res = ctx.resources.getIdentifier("pin_user_$it", "drawable",  ctx.packageName);
                bitMapFromVector(repo.ctx, res)
            }
        }
        if (isInit) resetMap()
    }

    override fun onMoveMap() {
        super.onMoveMap()
        if (isFollowMe.value == true) {
            isFollowMe.value = false
            rotate = 0f
        }
    }

    override fun onMovePosition() {
        super.onMovePosition()
        val pos = cameraPositionState?.position ?: return
        val zoom = pos.zoom
        var willType = zoomType
        willType = if (zoom < zoomFarAwayView) {
            PlayZoomType.FarAway
        } else if (zoom > zoomCloseView) {
            PlayZoomType.Close
        } else {
            PlayZoomType.Normal
        }
        if (willType != zoomType) {
            zoomType = willType
            onMarkerUpdate()
        }
    }
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        walkManager.status.observe(owner) { status ->
            val stat = status ?: return@observe
            when (stat) {
                WalkStatus.Walking -> {
                    isWalk.value = true
                    repo.pagePresenter.isKeepScreen = true
                }
                WalkStatus.Ready -> {
                    isWalk.value = false
                    repo.pagePresenter.isKeepScreen = false
                }
            }
            resetMap()
        }

        walkManager.currentLocation.observe(owner) {
            val loc = it ?: return@observe
            move(loc)
        }

        walkManager.event.observe(owner) { event ->
            val evt = event ?: return@observe
            when (evt.type) {
                WalkEventType.ChangeMapStatus -> {
                    prevUsers = listOf()
                    prevPlaces = listOf()
                    clearAll()
                }
                WalkEventType.UpdatePlaces -> clearMarkers(prevPlaces)
                WalkEventType.UpdateUsers -> clearMarkers(prevUsers)
                WalkEventType.UpdatedPlaces -> if (isInitMap) addMarkers(getPlaces())
                WalkEventType.UpdatedUsers -> if (isInitMap) updateUsers()
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
        clearAll()
        val location = walkManager.currentLocation.value
        val followMe = isFollowMe.value ?: false
        angle = if (followMe) mapMoveAngle else 0f
        zoom = if (followMe) zoomCloseup else zoomRatio
        rotate = if (followMe) rotate else 0f
        location?.let { loc->
            isInit = true
            move(loc = loc, zoom = zoom, rotate = rotate, duration = mapMoveDuration)
            forceMoveLock{
                moveMe(loc, isMove = true)
            }
        }
        updateUsers()
        addMarkers(getPlaces())
    }

    private fun onMarkerUpdate(){
        clearAll()
        addMarkers(getPlaces())
        if (zoomType != PlayZoomType.Close) {
            updateUsers()
        }
        if (zoomType == PlayZoomType.FarAway) {
            addCircles(getSummarys())
        }
    }

    private fun moveMe(loc:LatLng, isMove:Boolean? = null){
        if (!isInitMap) return
        if (isForceMove) return
        val move = isMove ?: isFollowMe.value ?: false
        me(getMyMarker(move))
        if (move) move(loc, zoom = null)
    }

    private fun getMyMarker(move: Boolean = false): MapMarker {
        val loc = position ?: walkManager.currentLocation.value ?: LatLng(0.0, 0.0)
        val marker = getMe(loc)
        return MapMarker(
            id = "me",
            marker = marker,
            isRotationMap = isFollowMe.value ?: false
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

    private var prevUsers:List<String> = listOf()
    private fun updateUsers(){
        val origin = if (zoomType == PlayZoomType.FarAway) walkManager.missionUsersSummary else walkManager.missionUsers
        val datas = origin.filter{ it.location != null }
        prevUsers = datas.map { it.missionId.toString() }
        datas.forEach{ data ->
            getUserMarker(data, data.missionId.toString())?.let {
                addMarker(
                    MapMarker(
                        id = data.missionId.toString(),
                        marker = it
                    )
                )
            }
        }
    }
    private fun getUserMarker(data:Mission, key:String) : MarkerData? {
        val loc = data.location ?: return null
        val icon = if (data.isGroup) pinUsers.randomOrNull() else emptyUser
        val marker = MarkerData(
            key = key,
            position = loc
        )
        if (data.isGroup) {
            marker.icon = icon
            marker.title = data.count.toString() + " " + (data.title ?: "")
            marker.zIndex = 900f
            marker.anchor = Offset(0.5f, 0.5f)
            marker.infoWindowAnchor = Offset( 0.5f, 0.1f)
            marker.onClick = {
                moveLocation(loc, zoomFarAway)
                false
            }
            return marker
        }

        marker.anchor = Offset(0.5f,  0.3f)
        marker.infoWindowAnchor = Offset(0.5f,0.16f)
        marker.zIndex = 300f
        marker.icon = icon
        marker.onClick = {
            viewModel.event.value = PageWalkEvent(
                PageWalkEventType.OpenPopup,
                WalkPopupData(WalkPopupType.WalkUser, value = data)
            )
            false
        }
        data.pictureUrl?.let {path->
            marker.title = data.title ?: "User"
            val size = DimenProfile.lightExtra.toInt().toDp
            val request = ImageRequest.Builder(repo.ctx)
                .data(path)
                .target(
                    object : Target {
                        override fun onError(error: Drawable?) {
                            super.onError(error)
                            addMarker(
                                MapMarker(
                                    id = key,
                                    marker = marker
                                )
                            )
                        }
                        override fun onSuccess(result: Drawable) {
                            val bitmap = result.toBitmap(
                                size, size
                            ).cropCircle()
                            marker.icon = BitmapDescriptorFactory.fromBitmap( bitmap )
                            addMarker(
                                MapMarker(
                                    id = key,
                                    marker = marker
                                )
                            )
                        }
                    }
                )
                .build()
            ImageLoader.Builder(repo.ctx).build().enqueue(request)
            return null
        }
        return marker
    }

    private var prevPlaces:List<String> = listOf()
    private fun getPlaces():List<MapMarker>{
        val origin = if (zoomType == PlayZoomType.FarAway) walkManager.placesSummary else walkManager.places
        val datas = origin.filter{ it.location != null }

        prevPlaces = datas.map { it.googlePlaceId.toString() }
        return datas.map{ data ->
            MapMarker(
                id = data.googlePlaceId.toString(),
                marker = getPlaceMarker(data, data.googlePlaceId.toString())
            )
        }
    }

    private fun getPlaceMarker(data:Place, key:String) : MarkerData{
        val loc = data.location ?: return MarkerData(position = LatLng(0.0, 0.0))

        val marker = MarkerData(
            key = key,
            position = loc
        )
        if(data.isGroup){
            marker.icon = pinPlace
            marker.title = data.count.toString() + " " + (data.title ?: "")
            marker.zIndex = 900f
            marker.onClick = {
                moveLocation(loc, zoomFarAway)
                false
            }
            return marker
        }
        val type = data.category ?: return MarkerData(position = LatLng(0.0, 0.0))
        val iconRes = if(data.isMark) type.iconMark else type.icon
        marker.icon = bitMapFromVector(repo.ctx, iconRes)
        marker.title = data.title ?: "Place"
        marker.anchor = Offset(0.5f,  0.5f)
        marker.zIndex = if(data.isMark) 100f else 200f
        marker.onClick = {
            viewModel.event.value = PageWalkEvent(
                PageWalkEventType.OpenPopup,
                WalkPopupData(WalkPopupType.WalkPlace, value = data)
            )
            false
        }
        //marker.tracksInfoWindowChanges = true
        return marker
    }

    private fun getSummarys():List<MapCircle>{
        val summary:ArrayList<MapUserData> = arrayListOf()
        walkManager.missionUsersSummary.forEach { summary.add(it) }
        walkManager.placesSummary.forEach{ summary.add(it) }
        val markers:List<MapCircle> = summary.map{ data ->
            MapCircle(data.id, getCircle(data))
        }
        return markers
    }
    private fun getCircle(data: MapUserData): CircleData {
        val loc = data.location ?: return CircleData(center = LatLng(0.0, 0.0))

        val radius: Double = min(max(100.0, data.count * 20.0), 1000.0)
        return CircleData(
            center = loc,
            radius = radius,
            fillColor = data.color.copy(alpha = 0.3f),
            strokeWidth = 0f
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
