package com.ironraft.pupping.bero.scene.page.walk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomListItemData
import com.ironraft.pupping.bero.scene.page.user.PageUserViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.scene.page.walk.component.PlayBox
import com.ironraft.pupping.bero.scene.page.walk.component.PlayMap
import com.ironraft.pupping.bero.scene.page.walk.component.WalkBox
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.scene.page.walk.model.WalkPickViewModel
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkHalfPopup
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopup
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupData
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkUiEvent
import com.lib.page.ComponentViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PageEventType
import com.lib.page.PageObject
import com.lib.page.PagePresenter
import com.skeleton.component.map.googlemap.CPGoogleMap
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.DimenStroke
import dev.burnoo.cokoin.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PageWalkEventType{
    OpenPopup, ClosePopup, CloseAllPopup
}

data class PageWalkEvent(
    val type:PageWalkEventType,
    var value:Any? = null
)


class PageWalkViewModel(repo:PageRepository): PageViewModel(PageID.Walk, repo){

    val event:MutableLiveData<PageWalkEvent?> = MutableLiveData(null)
    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        super.onCurrentPageEvent(type, pageObj)
        when (type) {
            PageEventType.ChangedPage -> {
                repo.walkManager.startMap()
                if(!repo.storage.isFirstWalk) {
                    repo.storage.isFirstWalk = true
                    repo.walkManager.firstWalk()
                }
                repo.walkManager.currentLocation.value?.let {
                    repo.walkManager.updateMapStatus(it)
                }
            }
            else ->{}
        }
    }

    override fun onClosePage() {
        super.onClosePage()
        event.value = PageWalkEvent(
            PageWalkEventType.CloseAllPopup
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PageWalk(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val walkManager: WalkManager = get()
    val appSceneObserver: AppSceneObserver = get()
    val pagePresenter: PagePresenter = get()

    val coroutineScope = rememberCoroutineScope()
    val viewModel: PageWalkViewModel by remember { mutableStateOf(
        PageWalkViewModel(repository).initSetup(owner) as PageWalkViewModel
    )}
    val playMapModel:PlayMapModel by remember { mutableStateOf(
        PlayMapModel(repository, walkManager).initSetup(owner) as PlayMapModel
    )}
    val walkPickViewModel:WalkPickViewModel by remember { mutableStateOf(
        WalkPickViewModel(repository).initSetup(owner)
    )}

    var popup:WalkPopupData? by remember { mutableStateOf(null) }
    val popupState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var isOpenPopup:Boolean by remember { mutableStateOf(false) }

    var halfPopup:WalkPopupData? by remember { mutableStateOf(null) }
    val halfPopupState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var isOpenHalfPopup:Boolean by remember { mutableStateOf(false) }

    val event = viewModel.event.observeAsState()
    fun onOpenPopup(){
        isOpenPopup = true
        appSceneObserver.useBottom.value = false
        viewModel.currentPage.value?.isGoBackAble = false
        if(!popupState.isVisible) {
            coroutineScope.launch {
                delay(100)
                popupState.show()
            }
        }
    }
    fun onClosePopup(){
        if(popupState.isVisible) {
            coroutineScope.launch { popupState.hide() }
        }
    }

    LaunchedEffect( popupState.currentValue ){
        popupState.currentValue.let {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    if (isOpenPopup) return@let
                    if (isOpenHalfPopup) return@let
                    if (halfPopupState.isVisible) return@let
                    popup = null
                    if (pagePresenter.currentTopPage?.pageID != PageID.Walk.value) return@let
                    appSceneObserver.useBottom.value = true
                    viewModel.currentPage.value?.isGoBackAble = true

                }
                else ->{
                    isOpenPopup = false
                }
            }
        }
    }

    fun onOpenHalfPopup(){
        isOpenHalfPopup = true
        appSceneObserver.useBottom.value = false
        viewModel.currentPage.value?.isGoBackAble = false
        if(!halfPopupState.isVisible) {
            coroutineScope.launch {
                delay(100)
                halfPopupState.show()
            }
        }
    }
    fun onCloseHalfPopup(){
        if(halfPopupState.isVisible) {
            coroutineScope.launch { halfPopupState.hide() }
        }
    }
    LaunchedEffect( halfPopupState.currentValue ) {
        halfPopupState.currentValue.let {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    if (isOpenPopup) return@let
                    if (isOpenHalfPopup) return@let
                    if (popupState.isVisible) return@let
                    halfPopup = null
                    if (pagePresenter.currentTopPage?.pageID != PageID.Walk.value) return@let
                    appSceneObserver.useBottom.value = true
                    viewModel.currentPage.value?.isGoBackAble = true

                }

                else -> {
                    isOpenHalfPopup = false
                }
            }
        }
    }

    event.value?.let {
        val evt = it ?: return@let
        when (evt.type) {
            PageWalkEventType.OpenPopup ->
                (evt.value as? WalkPopupData)?.let { popData ->
                    if (popData.type.isHalf) {
                        halfPopup = popData
                        onOpenHalfPopup()
                    } else {
                        popup = popData
                        onOpenPopup()
                    }
                }

            PageWalkEventType.ClosePopup ->
                (evt.value as? WalkPopupData)?.let { popData ->
                    if (popData.type.isHalf) onCloseHalfPopup() else onClosePopup()
                }

            PageWalkEventType.CloseAllPopup -> {
                onClosePopup()
                onCloseHalfPopup()
            }
        }
        viewModel.event.value = null
    }


    val goBackPage = viewModel.goBack.observeAsState()
    goBackPage.value?.let {
        viewModel.goBackCompleted()
        if(popupState.isVisible) {
            onClosePopup()
            return@let
        }
        onCloseHalfPopup()
    }

    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        contentAlignment = Alignment.Center
    ){
        PlayMap(
            viewModel = viewModel,
            playMapModel = playMapModel
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = DimenMargin.regular.dp,
                    bottom = (DimenApp.bottom + DimenMargin.thin).dp
                )
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            WalkBox(
                viewModel = viewModel,
                playMapModel = playMapModel)
            Spacer(modifier = Modifier.weight(1.0f))
            PlayBox(
                viewModel = viewModel,
                playMapModel = playMapModel,
                walkPickViewModel = walkPickViewModel,
                isInitable = true
            )
        }
        WalkHalfPopup(
            modifier = Modifier,
            sheetState = halfPopupState,
            viewModel = viewModel,
            type = halfPopup?.type ?: WalkPopupType.None,
            value = halfPopup?.value
        ){
            onCloseHalfPopup()
        }
        WalkPopup(
            modifier = Modifier,
            sheetState = popupState,
            viewModel = viewModel,
            type = popup?.type ?: WalkPopupType.None,
            value = popup?.value
        ){
            onClosePopup()
        }
    }
}

@Preview
@Composable
fun PageSplashPreview(){
    PageWalk(
    )
}
