package com.ironraft.pupping.bero.scene.page.walk.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.lib.page.PageAnimationType
import com.lib.page.PagePresenter
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenButton
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.DimenStroke
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import dev.burnoo.cokoin.get

@Composable
fun SimpleWalkBox(
    modifier: Modifier = Modifier
) {
    val pagePresenter: PagePresenter = get()
    val walkManager: WalkManager = get()
    val appSceneObserver : AppSceneObserver = get()
    val isSimple by walkManager.isSimpleView.observeAsState()
    val walkDistance by walkManager.walkDistance.observeAsState()
    val walkStatus by walkManager.status.observeAsState()
    val isActiveChat by appSceneObserver.isActiveChat.observeAsState()
    val useBottom by appSceneObserver.useBottom.observeAsState()
    fun getOffset():Float{
        var pos = DimenMargin.thin
        if (isActiveChat==true) pos += DimenApp.chatBox
        if (useBottom ==true) pos += DimenApp.bottom
        return pos
    }
    val offsetY: Dp by animateDpAsState(
        getOffset().dp,
        tween()
    )
    val offsetX: Dp by animateDpAsState(
        ((if (isSimple==false) 150 else 0) + DimenRadius.light).dp,
        tween()
    )
    AppTheme {
        if(walkStatus == WalkStatus.Walking){
            Box(modifier = modifier.offset(y = -offsetY, x= -offsetX)
                .wrapContentSize()
                .clip(RoundedCornerShape(DimenRadius.light.dp))
                .background(ColorApp.white)
                .border(
                    width = DimenStroke.light.dp,
                    color = ColorApp.grey100,
                    shape = RoundedCornerShape(DimenRadius.light.dp)
                )
                .padding(
                    start = (DimenMargin.tiny+DimenRadius.light).dp,
                    end = DimenMargin.tiny.dp,
                    top =  DimenMargin.tiny.dp,
                    bottom = DimenMargin.tiny.dp
                )
            ){
                FillButton(
                    modifier = Modifier
                        .width(115.dp),
                    type = FillButtonType.Fill,
                    icon = R.drawable.paw,
                    text = WalkManager.viewDistance(walkDistance),
                    size = DimenButton.regularExtra,
                    color = ColorApp.black,
                    isActive = true
                ) {
                    if (pagePresenter.currentPage?.pageID == PageID.Walk.value) {
                        walkManager.updateSimpleView(false)
                    } else {
                        pagePresenter.changePage(
                            PageProvider.getPageObject(PageID.Walk)
                        )
                    }
                }
            }
        }
    }
}
