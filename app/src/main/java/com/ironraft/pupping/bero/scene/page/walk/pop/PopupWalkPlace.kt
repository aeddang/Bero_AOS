package com.ironraft.pupping.bero.scene.page.walk.pop

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.item.MultiProfileListItemData
import com.ironraft.pupping.bero.scene.component.item.PlaceInfo
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEventType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.scene.page.walk.component.VisitorHorizontalView
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.walk.WalkEventType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkMapData
import com.ironraft.pupping.bero.store.walk.WalkUiEvent
import com.ironraft.pupping.bero.store.walk.WalkUiEventType
import com.ironraft.pupping.bero.store.walk.model.Place
import com.lib.page.PagePresenter
import com.lib.util.distance
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PopupWalkPlace(
    viewModel: PageWalkViewModel,
    selectPlace:Place?,
    close:() -> Unit
) {
    val walkManager: WalkManager = get()
    val appSceneObserver: AppSceneObserver = get()
    val pagePresenter: PagePresenter = get()

    var current:Place? by remember { mutableStateOf( null )}
    var isMark:Boolean by remember { mutableStateOf( false )}
    var distance:Double by remember { mutableStateOf( 0.0 )}
    var visitors:List<MultiProfileListItemData>? by remember { mutableStateOf( null )}
    fun onUpdateData(){
        val place = current ?: return
        isMark = place.isMark
        walkManager.currentLocation.value?.let {loc->
            place.location?.let {destination->
                distance = destination.distance(loc)
            }

        }
        visitors = place.visitors.mapIndexed { index, userAndPet ->
            MultiProfileListItemData().setData(userAndPet,index)
        }
    }
    fun onMove(idx:Int):Boolean{
        val pages = walkManager.places
        if (idx < 0) return true
        if (idx >= pages.count()) return true
        val page = pages[idx]
        if (current?.placeId == page.placeId) return true
        current = page
        page.location?.let { loc->
            val modifyLoc = LatLng(loc.latitude-0.0005, loc.longitude)
            walkManager.uiEvent.value = WalkUiEvent(
                type = WalkUiEventType.MoveMap,
                value = WalkMapData(
                    loc = modifyLoc,
                    zoom = PlayMapModel.zoomCloseView
                )
            )
        }
        onUpdateData()
        return true
    }
    val isSimple by walkManager.isSimpleView.observeAsState()
    val isInit by remember { mutableStateOf( onMove(selectPlace?.index ?: 0) )}
    val event by viewModel.event.observeAsState()
    LaunchedEffect(key1 = event){
        event?.let { evt->
            when(evt.type){
                PageWalkEventType.OpenPopup -> {
                    (evt.value as? WalkPopupData)?.let { data->
                        when(data.type){
                            WalkPopupType.WalkPlace -> {
                                (data.value as? Place)?.let {
                                    onMove(it.index)
                                }
                            }
                            else ->{}
                        }
                    }
                }
                else ->{}
            }
        }
    }
    val walkEvent by walkManager.event.observeAsState()
    LaunchedEffect(key1 = walkEvent){
        walkEvent?.let { evt->
            when(evt.type){
               WalkEventType.MarkedPlace -> {
                    (evt.value as? Place)?.let { place->
                        if(place.placeId == current?.placeId) onUpdateData()
                    }
                }
                else ->{}
            }
        }
    }

    val offsetY: Dp by animateDpAsState(
        (if (isSimple==true) DimenMargin.mediumUltra else 0f).dp,
        tween()
    )

    AppTheme {
        if(isInit) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = offsetY),
                contentAlignment = Alignment.TopEnd
            ) {
                ImageButton(
                    modifier = Modifier.padding( all = DimenMargin.regular.dp ),
                    defaultImage = R.drawable.close
                ) {
                    close()
                }
                Column(
                    Modifier.padding(vertical = DimenMargin.regular.dp),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    current?.let {place->
                        PlaceInfo(
                            modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                            sortIconPath = place.place?.icon,
                            sortTitle = if(place.category?.title != null) stringResource(id = place.category!!.title!!) else null,
                            title = place.title,
                            description = place.place?.vicinity,
                            distance = distance
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.micro.dp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            FillButton(
                                modifier = Modifier.weight(1.0f),
                                type = FillButtonType.Fill,
                                icon = R.drawable.pin_drop,
                                text = stringResource(id = R.string.button_leaveAmark),
                                size = DimenButton.regular,
                                color = ColorBrand.primary,
                                isActive = !isMark
                            ){
                                if (isMark) return@FillButton
                                onUpdateData()
                                if(distance > WalkManager.nearDistance) {
                                    appSceneObserver.sheet.value = ActivitSheetEvent(
                                        type = ActivitSheetType.Alert,
                                        title = pagePresenter.activity.getString(R.string.walkPlaceMarkDisAbleTitle),
                                        text = pagePresenter.activity.getString(R.string.walkPlaceMarkDisAbleText),
                                        isNegative = true
                                    )
                                    return@FillButton
                                }
                                walkManager.markPlace(place = place)
                            }
                            if(SystemEnvironment.isTestMode){
                                FillButton(
                                    modifier = Modifier.weight(1.0f),
                                    type = FillButtonType.Fill,
                                    text = "완료 테스트용",
                                    size = DimenButton.regular
                                ){
                                    walkManager.markPlace(place = place)
                                }
                            }
                        }
                        visitors?.let {
                            VisitorHorizontalView(
                                viewModel = viewModel,
                                place = place,
                                datas = it,
                                close = close
                            )
                        }

                    }
                }
            }
        }
    }
}
