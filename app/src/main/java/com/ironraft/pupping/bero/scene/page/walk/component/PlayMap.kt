package com.ironraft.pupping.bero.scene.page.walk.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberMarkerState
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEvent
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEventType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapUiEvent
import com.ironraft.pupping.bero.scene.page.walk.model.WalkPickViewModel
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupData
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkMapData
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.ironraft.pupping.bero.store.walk.WalkUiEvent
import com.ironraft.pupping.bero.store.walk.WalkUiEventType
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.lib.util.toggle
import com.skeleton.component.map.googlemap.CPGoogleMap
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenButton
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.DimenStroke
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.progress.CircleWave
import dev.burnoo.cokoin.get

@Composable
fun PlayMap(
    modifier: Modifier = Modifier,
    viewModel: PageWalkViewModel,
    playMapModel: PlayMapModel,

) {

    val walkManager: WalkManager = get()
    val isFollowMe by playMapModel.isFollowMe.observeAsState()
    val isWalk by playMapModel.isWalk.observeAsState()
    val place by playMapModel.findPlace.observeAsState()

    fun onInit():Boolean{
        return true
    }
    val isInit:Boolean by remember { mutableStateOf( onInit() )}
    AppTheme {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ){
            if (isInit) {
                CPGoogleMap(
                    modifier = modifier,
                    mapModel = playMapModel
                )
                if (place != null) {
                    CircleWave(
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }

    }
}
