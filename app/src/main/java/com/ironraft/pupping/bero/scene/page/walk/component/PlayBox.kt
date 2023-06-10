package com.ironraft.pupping.bero.scene.page.walk.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import dev.burnoo.cokoin.get


@Composable
fun PlayBox(
    modifier: Modifier = Modifier,
    viewModel: PageWalkViewModel,
    playMapModel: PlayMapModel,
    walkPickViewModel: WalkPickViewModel,
    isInitable:Boolean = true
) {
    val pagePresenter: PagePresenter = get()
    val appSceneObserver: AppSceneObserver = get()
    val dataProvider: DataProvider = get()
    val walkManager: WalkManager = get()


    var isWalk:Boolean by remember { mutableStateOf(walkManager.status.value == WalkStatus.Walking) }
    val isSimple by walkManager.isSimpleView.observeAsState()
    val walkStatus = walkManager.status.observeAsState()
    val isHidden by playMapModel.componentHidden.observeAsState()
    val isFollowMe by playMapModel.isFollowMe.observeAsState()
    val pickImage = walkPickViewModel.pickImage.observeAsState()

    walkStatus.value.let { status ->
        val walk =  status == WalkStatus.Walking
        if (walk == isWalk) return@let
        isWalk = walk
    }
    pickImage.value?.let { image->
        walkManager.updateStatus(image)
        walkPickViewModel.pickImage.value = null
    }

    fun needDog(){
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_addDogTitle),
            text = pagePresenter.activity.getString(R.string.alert_addDogText),
            image = R.drawable.add_dog,
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.button_later),
                pagePresenter.activity.getString(R.string.button_ok)
            )
        ){
            if(it == 1){
                pagePresenter.openPopup(PageProvider.getPageObject(PageID.AddDog))
            }
        }
    }

    fun startWalk(){
        val ctx = pagePresenter.activity
        if (walkManager.currentLocation.value == null) {
            Toast(ctx).showCustomToast(
                ctx.getString(R.string.walkLocationNotFound),
                ctx
            )
            return
        }
        if (dataProvider.user.pets.count() >= 2) {
            viewModel.event.value = PageWalkEvent(
                PageWalkEventType.OpenPopup,
                WalkPopupData(WalkPopupType.ChooseDog)
            )
            return
        }
        walkManager.requestWalk()
    }
    fun checkFinish(){
        val ctx = pagePresenter.activity
        if (SystemEnvironment.isTestMode && (walkManager.walkDistance.value ?: 0.0) < WalkManager.minDistance) {
            Toast(ctx).showCustomToast(
                ctx.getString(R.string.walkFinishCheckDistance).replace(WalkManager.minDistance.toString()),
                ctx
            )
            return
        }
        walkManager.completeWalk()
    }

    fun cancelWalk(){
        appSceneObserver.alert.value  = ActivitAlertEvent(
            type = ActivitAlertType.Alert,
            text = pagePresenter.activity.getString(R.string.alert_completedExitConfirm)
        ){
            if(it == 1) {
                walkManager.endWalk()
            }
        }
    }
    fun finishWalk(){
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.walkFinishConfirm),
            text = pagePresenter.activity.getString(R.string.alert_completedNeedPicture),
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.cancel),
                pagePresenter.activity.getString(R.string.button_finish)
            )
        ){
            if(it == 1){
                checkFinish()
            } else {
                cancelWalk()
            }
        }
    }


    AppTheme {
        AnimatedVisibility(visible = isHidden != true && isSimple == false, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(isWalk) {
                        CircleButton(
                            type = CircleButtonType.Icon,
                            icon = R.drawable.minimize,
                            strokeWidth = DimenStroke.regular,
                            defaultColor = ColorApp.grey500
                        ) {
                            val isSimple = isSimple?.toggle() ?: false
                            walkManager.updateSimpleView(isSimple)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(CircleButtonType.Icon.size.dp))
                    }

                    Spacer(modifier = Modifier.weight(1.0f))
                    SortButton(
                        type = SortButtonType.Stroke,
                        sizeType = SortButtonSizeType.Small,
                        icon = R.drawable.refresh,
                        text = stringResource(id = R.string.button_searchArea),
                        color = ColorBrand.primary,
                        isSort = false,
                        isSelected = false
                    ) {
                        val pos = playMapModel.position?: return@SortButton
                        walkManager.replaceMapStatus(pos)
                        walkManager.uiEvent.value = WalkUiEvent(
                            type = WalkUiEventType.MoveMap,
                            value = WalkMapData(pos, PlayMapModel.zoomDefault)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    CircleButton(
                        type = CircleButtonType.Icon,
                        icon = R.drawable.my_location,
                        strokeWidth = DimenStroke.regular,
                        defaultColor = if (isFollowMe == true) ColorBrand.primary else ColorApp.grey500
                    ){
                        playMapModel.isFollowMe.value?.let {
                            playMapModel.isFollowMe.value = it.toggle()
                        }
                        playMapModel.playUiEvent.value = PlayMapUiEvent.ResetMap
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(DimenRadius.light.dp))
                        .background(ColorApp.white)
                        .border(
                            width = DimenStroke.light.dp,
                            color = ColorApp.grey100,
                            shape = RoundedCornerShape(DimenRadius.light.dp)
                        )
                        .padding(
                            all = DimenMargin.regularExtra.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(isWalk){
                        CircleButton(
                            type = CircleButtonType.Icon,
                            icon = R.drawable.camera,
                            originSize = DimenIcon.heavyExtra,
                            isSelected = true,
                            defaultColor = ColorApp.white,
                            activeColor = ColorApp.black
                        ){
                            walkPickViewModel.onPick()
                        }
                    }
                    FillButton(
                        type = FillButtonType.Fill,
                        text = stringResource(id = if(isWalk) R.string.button_finishTheWalk else R.string.button_startWalking),
                        size = DimenButton.regular,
                        color = if(isWalk) ColorApp.black else ColorBrand.primary,
                        isActive = true
                    ){
                        if(isWalk){
                            finishWalk()
                        } else {
                            if(isInitable) startWalk()
                            else needDog()
                        }
                    }
                }
            }
        }
    }
}
