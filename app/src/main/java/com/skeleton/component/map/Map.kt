package com.skeleton.component.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import com.lib.page.ComponentViewModel
import com.lib.util.getAngleBetweenPoints
import com.skeleton.theme.ColorApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID


data class MapMarker (
    var id:String = UUID.randomUUID().toString(),
    var marker:MarkerData,
    var isRotationMap:Boolean = false
)
data class MarkerData(
    val key: String = UUID.randomUUID().toString(),
    var position: LatLng,
    var state: MarkerState? = null,
    var alpha: Float = 1.0f,
    var anchor: Offset = Offset(0.5f, 1.0f),
    var draggable: Boolean = false,
    var flat: Boolean = false,
    var icon: BitmapDescriptor? = null,
    var infoWindowAnchor: Offset = Offset(0.5f, 0.0f),
    var rotation: Float = 0.0f,
    var snippet: String? = null,
    var tag: Any? = null,
    var title: String? = null,
    var visible: Boolean = true,
    var zIndex: Float = 0.0f,
    var onClick: (Marker) -> Boolean = { false },
    var onInfoWindowClick: (Marker) -> Unit = {},
    var onInfoWindowClose: (Marker) -> Unit = {},
    var onInfoWindowLongClick: (Marker) -> Unit = {},
)
data class MapCircle (
    var id:String = UUID.randomUUID().toString(),
    val circle: CircleData
)
data class CircleData(
    var center: LatLng,
    var clickable: Boolean = false,
    var fillColor: Color = Color.Transparent,
    var radius: Double = 0.0,
    var strokeColor: Color = Color.Black,
    var strokePattern: List<PatternItem>? = null,
    var strokeWidth: Float = 10f,
    var tag: Any? = null,
    var visible: Boolean = true,
    var zIndex: Float = 0f,
    var onClick: (Circle) -> Unit = {},
    )
data class CameraData(
    var rotate:Float? = null,
    var zoom:Float? = null,
    var angle:Float? = null,
    var duration:Double? = null
)

enum class MapUiEventType {
    Me,
    AddMarker,
    AddCircle,
    Clear, ClearAll,
    Move
}
data class MapUiEvent (
    val type:MapUiEventType,
    var loc: LatLng? = null,
    var marker:MapMarker? = null,
    var circle:MapCircle? = null,
    var markers:List<MapMarker>? = null,
    var circles:List<MapCircle>? = null,
    var camera: CameraData? = null,
    var selectId:String? = null,
    var selectIds:List<String> = listOf()
)

enum class MapViewEventType {
    TabMarker, TabOffMarker, Move, Tab
}
data class MapViewEvent (
    val type:MapViewEventType,
    var loc: LatLng? = null,
    var marker:MapMarker? = null,
    var isUser: Boolean = true
)

interface MapUserDataInterface {
    var isSelected:Boolean
    var isGroup:Boolean
    var startPos:Float
    var midPos:Float
    var endPos:Float
    var title:String?
    var location:LatLng?
    var locations:ArrayList<LatLng>
    var count:Int
    var color:Color
}

open class MapUserData: MapUserDataInterface, Comparable<MapUserData>{
    override var isSelected:Boolean = false
    final override var isGroup:Boolean = false;
    final override var startPos:Float = 0.0f
    final override var midPos:Float = 0.0f
    final override var endPos:Float = 0.0f
    override var title:String? = null
    override var location:LatLng? = null
    override var locations:ArrayList<LatLng> = arrayListOf()
    override var count:Int = 0
    override var color:Color = ColorApp.white
    var id:String = UUID.randomUUID().toString(); protected set
    var index:Int = -1
    fun setPosition(pos:Float):MapUserData{
        midPos = pos
        return this
    }

    fun setRange(idx:Int, width:Float):MapUserData{
        index = idx
        val sPos = idx.toFloat() * width
        val range = (width / 2)
        startPos = sPos
        endPos = sPos + width
        midPos = sPos + range
        return this
    }
    fun isBelong(pos:Float):Boolean{
        if (startPos <= pos && endPos > pos) return true
        return false
    }

    fun addCount(count:Int = 1, loc:LatLng){
        this.count += count
        locations.add(loc)
    }
    fun addCompleted(){
        var latSum: Double = 0.0
        var lngSum: Double = 0.0
        val count:Double = locations.count().toDouble()
        locations.forEach{ loc ->
            latSum += loc.latitude
            lngSum += loc.longitude
        }
        isGroup = true
        val latitude = latSum/count
        val longitude = lngSum/count
        location = LatLng(latitude, longitude)
    }

    override fun compareTo(other: MapUserData): Int {
        if (isBelong(other.midPos)) return 0
        if (this.midPos < other.midPos) return 1
        return -1
    }
}


open class MapModel: ComponentViewModel() {
    val uiEvent: MutableLiveData<MapUiEvent?> = MutableLiveData(null)
    val event: MutableLiveData<MapViewEvent?> = MutableLiveData(null)
    var startLocation: LatLng = LatLng(0.0, 0.0); protected set
    var angle: Float = 0f; protected set
    var rotate: Float = 0f; protected set
    var zoom: Float = 0f; protected set
    var isInitMap: Boolean = false

    val position: LatLng?
        get() {
            return cameraPositionState?.position?.target ?: startLocation
        }

    private var markers: HashMap<String, MapMarker> = HashMap()
    private var circles: HashMap<String, MapCircle> = HashMap()
    private var me:MapMarker? = null
    val mapMe:MutableLiveData<MarkerData?> = MutableLiveData(null)
    val mapMarkers: MutableLiveData<ArrayList<MarkerData>> = MutableLiveData(arrayListOf())
    val mapCircles: MutableLiveData<ArrayList<CircleData>> = MutableLiveData(arrayListOf())

    var cameraPositionState: CameraPositionState? = null; private set
    var scope: CoroutineScope? = null
    fun initSetup(owner: LifecycleOwner): MapModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun lazySetup(state: CameraPositionState, coroutineScope: CoroutineScope): MapModel {
        cameraPositionState = state
        scope = coroutineScope
        cameraPositionState
        return this
    }

    open fun onInitMap() {
        isInitMap = true
    }
    open fun onMoveMap() {}
    open fun onMovePosition() {}
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        uiEvent.observe(owner) { uiEvent ->
            val evt = uiEvent ?: return@observe
            when (evt.type) {
                MapUiEventType.Me -> evt.marker?.let { me(it) }
                MapUiEventType.AddMarker -> {
                    evt.marker?.let { addMarker(it) }
                    evt.markers?.let { addMarkers(it) }
                }

                MapUiEventType.AddCircle -> {
                    evt.circle?.let { addCircle(it) }
                    evt.circles?.let { addCircles(it) }
                }

                MapUiEventType.Clear -> evt.selectId?.let { clear(it) }
                MapUiEventType.ClearAll -> clearAll(evt.selectIds)
                MapUiEventType.Move ->
                    evt.loc?.let { loc ->
                        evt.camera?.let { move(loc, it) }
                    }
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        uiEvent.removeObservers(owner)
    }

    protected fun me(marker: MapMarker) {
        val prevMarker = me
        mapMe.value = marker.marker
        if (marker.isRotationMap) {
            prevMarker?.marker?.position?.let { prev ->
                marker.marker.position.let {
                    val targetPoint = PointF(it.latitude.toFloat(), it.longitude.toFloat())
                    val mePoint = PointF(prev.latitude.toFloat(), prev.longitude.toFloat())
                    val rt = mePoint.getAngleBetweenPoints(targetPoint)
                    this.rotate = rt
                }
            }
        }
        me = marker
    }

    protected fun move(loc: LatLng, camera: CameraData) {
        this.move(loc, camera.rotate, camera.zoom, camera.angle, camera.duration)
    }

    protected fun move(
        loc: LatLng,
        rotate: Float? = null,
        zoom: Float? = null,
        angle: Float? = null,
        duration: Double? = null
    ) {
        rotate?.let { this.rotate = it }
        angle?.let { this.angle = it }
        zoom?.let { this.zoom = it }
        cameraPositionState?.let { state ->
            val update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(
                    loc,
                    this.zoom,
                    this.angle,
                    this.rotate
                )
            )
            scope?.let { cs ->
                duration?.let {
                    cs.launch {
                        state.animate(update, (it * 1000).toInt())
                    }
                    return
                }
            }

            state.move(update)
        }
    }

    protected fun addMarker(marker: MapMarker) {

        markers[marker.id]?.let { prevMarker ->
            if (prevMarker.marker != marker.marker || prevMarker.marker.icon != marker.marker.icon) {
                mapMarkers.value?.remove(prevMarker.marker)
                mapMarkers.value?.add(marker.marker)
                prevMarker.marker = marker.marker
            }
            return
        }
        markers[marker.id] = marker
        mapMarkers.value?.add(marker.marker)
    }

    protected fun addMarkers(markers: List<MapMarker>) {
        markers.forEach { addMarker(it) }
    }

    protected fun addCircle(circle: MapCircle) {
        circles[circle.id]?.let { prevCircle ->
            prevCircle.circle.radius = circle.circle.radius
            prevCircle.circle.fillColor = circle.circle.fillColor
            prevCircle.circle.tag = circle.circle.tag
            return
        }
        circles[circle.id] = circle
        mapCircles.value?.add(circle.circle)
    }

    protected fun addCircles(circles: List<MapCircle>) {
        circles.forEach { addCircle(it) }
    }

    protected fun clear(id: String) {
        markers[id]?.let { marker ->
            markers.remove(id)
            mapMarkers.value?.remove(marker.marker)
        }
        circles[id]?.let { circle ->
            circles.remove(id)
            mapCircles.value?.remove(circle.circle)
        }
    }

    protected fun clearMarkers(ids: List<String>) {
        ids.forEach { id->
            markers[id]?.let { marker ->
                markers.remove(id)
                mapMarkers.value?.remove(marker.marker)
            }
        }
    }
    protected fun clearCircles(ids: List<String>) {
        ids.forEach { id->
            circles[id]?.let { marker ->
                circles.remove(id)
                mapCircles.value?.remove(marker.circle)
            }
        }
    }
    protected fun clearAll(exception: List<String>? = null) {
        val newMarkers: HashMap<String, MapMarker> = HashMap()
        val newCircles: HashMap<String, MapCircle> = HashMap()
        markers.forEach { marker ->
            exception?.find { it == marker.key }?.let {
                newMarkers[marker.key] = marker.value
                return@forEach
            }
            mapMarkers.value?.remove(marker.value.marker)
        }
        circles.forEach { marker ->
            exception?.find { it == marker.key }?.let {
                newCircles[marker.key] = marker.value
            }
            mapCircles.value?.remove(marker.value.circle)
        }
        markers = newMarkers
        circles = newCircles
    }

    protected fun bitMapFromVector(ctx: Context, @DrawableRes vectorResID: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(ctx, vectorResID)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
