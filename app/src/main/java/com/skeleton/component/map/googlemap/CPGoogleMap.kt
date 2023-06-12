package com.skeleton.component.map.googlemap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.skeleton.component.map.MapModel
import com.skeleton.theme.AppTheme


@Composable
fun CPGoogleMap(
    modifier:Modifier = Modifier,
    mapModel:MapModel? = null,
) {
    val appTag = "CPGoogleMap"
    val owner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: MapModel by remember { mutableStateOf(
        mapModel ?: MapModel().initSetup(owner)
    )}
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom( viewModel.startLocation,  viewModel.zoom)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            viewModel.onMoveMap()
        }
    }
    LaunchedEffect(cameraPositionState.position) {
        viewModel.onMovePosition()
    }

    val markers by viewModel.mapMarkers.observeAsState()
    val circles by viewModel.mapCircles.observeAsState()
    val me by viewModel.mapMe.observeAsState()

    fun onInit():Boolean{
        viewModel.lazySetup(cameraPositionState, coroutineScope)
        return true
    }
    val isInit:Boolean by remember { mutableStateOf( onInit() )}
    var isMapInit by remember { mutableStateOf( false ) }
    AppTheme {
        if (isInit) {
            GoogleMap(
                modifier = modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                googleMapOptionsFactory = {
                    val opt = GoogleMapOptions()
                    opt.mapId("c968f8193ed79966")
                    return@GoogleMap opt
                },
                properties = MapProperties(
                    isBuildingEnabled = false,
                    isIndoorEnabled = false,
                    isMyLocationEnabled = true,
                    isTrafficEnabled = false,
                    latLngBoundsForCameraTarget = null,
                    mapStyleOptions = null,
                    mapType = MapType.NORMAL,
                    maxZoomPreference = 21.0f,
                    minZoomPreference = 3.0f
            ),
            uiSettings = MapUiSettings(
                    compassEnabled = true,
                    indoorLevelPickerEnabled = false,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = true,
                    tiltGesturesEnabled = true,
                    zoomControlsEnabled = false,
                    zoomGesturesEnabled = true
                ),
                onMapLoaded = {
                    viewModel.onInitMap()
                    isMapInit = true
                }
            ) {
                if (isMapInit)

                    circles?.forEach { it ->
                        Circle(
                            it.center,
                            it.clickable,
                            it.fillColor,
                            it.radius,
                            it.strokeColor,
                            it.strokePattern,
                            it.strokeWidth,
                            it.tag,
                            it.visible,
                            it.zIndex,
                            it.onClick
                        )
                    }

                    markers?.forEach { it ->
                        Marker(
                            MarkerState(
                                position = it.position
                            ),
                            it.alpha,
                            it.anchor,
                            it.draggable,
                            it.flat,
                            it.icon,
                            it.infoWindowAnchor,
                            it.rotation,
                            it.snippet,
                            it.tag,
                            it.title,
                            it.visible,
                            it.zIndex,
                            it.onClick,
                            it.onInfoWindowClick,
                            it.onInfoWindowClose,
                            it.onInfoWindowLongClick
                        )
                    }
                    /*
                    me?.let {
                        Marker(
                            MarkerState(
                                position = it.position
                            ),
                            it.alpha,
                            it.anchor,
                            it.draggable,
                            it.flat,
                            it.icon,
                            it.infoWindowAnchor,
                            it.rotation,
                            it.snippet,
                            it.tag,
                            it.title,
                            it.visible,
                            it.zIndex,
                            it.onClick,
                            it.onInfoWindowClick,
                            it.onInfoWindowClose,
                            it.onInfoWindowLongClick
                        )
                    }
                    */

            }
        }
    }
}

@Preview
@Composable
fun CPGoogleMapComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CPGoogleMap(
        )
    }

}