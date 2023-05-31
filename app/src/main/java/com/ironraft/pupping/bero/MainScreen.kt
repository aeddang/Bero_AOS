
package com.ironraft.pupping.bero

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.ironraft.pupping.bero.activityui.*
import com.ironraft.pupping.bero.scene.component.tab.BottomTab
import com.ironraft.pupping.bero.scene.page.intro.PageIntro
import com.ironraft.pupping.bero.scene.page.login.PageLogin
import com.ironraft.pupping.bero.scene.page.PageSplash
import com.ironraft.pupping.bero.scene.page.PageTest
import com.ironraft.pupping.bero.scene.page.chat.PageChat
import com.ironraft.pupping.bero.scene.page.chat.PageChatRoom
import com.ironraft.pupping.bero.scene.page.explore.PageExplore
import com.ironraft.pupping.bero.scene.page.history.PageWalkHistory
import com.ironraft.pupping.bero.scene.page.history.PageWalkInfo
import com.ironraft.pupping.bero.scene.page.history.PageWalkList
import com.ironraft.pupping.bero.scene.page.history.PageWalkReport
import com.ironraft.pupping.bero.scene.page.my.PageBlockUser
import com.ironraft.pupping.bero.scene.page.my.PageManageDogs
import com.ironraft.pupping.bero.scene.page.my.PageMy
import com.ironraft.pupping.bero.scene.page.my.PageMyAccount
import com.ironraft.pupping.bero.scene.page.my.PageMyLv
import com.ironraft.pupping.bero.scene.page.my.PageMyPoint
import com.ironraft.pupping.bero.scene.page.my.PageSetup
import com.ironraft.pupping.bero.scene.page.pet.PageDog
import com.ironraft.pupping.bero.scene.page.popup.PageAlarm
import com.ironraft.pupping.bero.scene.page.popup.PageAlbum
import com.ironraft.pupping.bero.scene.page.popup.PageFriend
import com.ironraft.pupping.bero.scene.page.popup.PagePicture
import com.ironraft.pupping.bero.scene.page.popup.PagePictureViewer
import com.ironraft.pupping.bero.scene.page.popup.PagePrivacy
import com.ironraft.pupping.bero.scene.page.popup.PageServiceTerms
import com.ironraft.pupping.bero.scene.page.profile.PageAddDog
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogCompleted
import com.ironraft.pupping.bero.scene.page.profile.PageEditProfile
import com.ironraft.pupping.bero.scene.page.profile.PageModifyPet
import com.ironraft.pupping.bero.scene.page.profile.PageModifyPetHealth
import com.ironraft.pupping.bero.scene.page.profile.PageModifyUser
import com.ironraft.pupping.bero.scene.page.user.PageUser
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.page.*
import com.lib.util.PageLog
import com.skeleton.theme.*
import com.skeleton.view.progress.LoadingIndicator
import dev.burnoo.cokoin.get

enum class SceneEventType {
    Initate, Check,
    SetupChat, CloseChat, SendChat
}
data class SceneEvent(val type: SceneEventType,
                      val value:String? = null,
                      @DrawableRes val imgRes:Int? = null,
                      var isOn:Boolean = true,
                      val handler: (() -> Unit)? = null)


class AppSceneObserver {
    val event = MutableLiveData<SceneEvent?>(null)
    val alert = MutableLiveData<ActivitAlertEvent?>(null)
    val sheet = MutableLiveData<ActivitSheetEvent?>(null)
    val select = MutableLiveData<ActivitSelectEvent?>(null)
    val radio = MutableLiveData<ActivitRadioEvent?>(null)
    var isAlertShow:Boolean = false
}



@SuppressLint("MutableCollectionMutableState", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PageApp(
    pageNavController: NavHostController,
    radioState: ModalBottomSheetState,
    selectState: ModalBottomSheetState,
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier
) {
    val activityModel:ActivityModel = get()
    val pageAppViewModel:PageAppViewModel = get()
    val repository: PageRepository = get()
    val owner = LocalLifecycleOwner.current
    val chatFunctionViewModel: ChatFunctionViewModel by remember { mutableStateOf(
        ChatFunctionViewModel(repository).initSetup(owner)
    ) }

    val currentTopPage by pageAppViewModel.currentTopPage.observeAsState()

    var loadingInfo:ArrayList<String>? by remember { mutableStateOf(null) }
    val isLoading by pageAppViewModel.isLoading.observeAsState()
    val isLock by pageAppViewModel.isLock.observeAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(ColorApp.black)){
        Scaffold(
            bottomBar = {
                Column(
                    verticalArrangement =  Arrangement.spacedBy(0.dp)
                ) {
                    ChatBox(
                        modifier = Modifier.weight(1.0f),
                        chatFunctionViewModel = chatFunctionViewModel
                    )
                    currentTopPage?.let {
                        if (activityModel.useBottomTabPage(it.pageID)) BottomTab()
                    }
                }

            }
        ) {
            AppTheme {
                AnimatedNavHost(
                    navController = pageNavController,
                    startDestination = PageID.Splash.value,
                    modifier = modifier
                        .fillMaxSize()
                        .background(ColorBrand.bg)
                ) {
                    //val routePage = pagePresenter.findPage(route)
                    //PageLog.d(route ?: "", "PAGEROUTE")
                    PageID.values().forEach {
                        getPageComposable(
                            nav = this,
                            page = it,
                            routePage = currentTopPage
                        )
                    }
                }
            }
        }
        ActivitySelectController(modalSheetState = selectState)
        ActivityRadioController(modalSheetState = radioState)
        ActivitySheetController(modalSheetState = sheetState)
        ActivityAlertController()

        AnimatedVisibility(visible = isLoading == true, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isLock == true) ColorTransparent.black70 else ColorTransparent.clearUi),
                contentAlignment = Alignment.BottomCenter,
            ) {
                LoadingIndicator(
                    modifier = Modifier.padding(bottom = DimenApp.bottom.dp)
                )
            }
        }

    }

}

@OptIn(ExperimentalAnimationApi::class)
fun getPageComposable(nav:NavGraphBuilder,page:PageID, routePage:PageObject?){
    val currentRoutePage = if(page.value == routePage?.pageID) routePage else null
    val pageObj = currentRoutePage ?: PageProvider.getPageObject(page)
    val ani = pageObj.animationType
    val duration = PageAnimationType.duration
    nav.composable(
        route = page.value,
        enterTransition = {ani.enter},
        exitTransition = {ani.exit},
        popEnterTransition = {fadeIn(animationSpec = tween(duration)) },
        popExitTransition = {ani.exit}
    ) {
        when (page.value) {
            PageID.Intro.value -> PageIntro(Modifier.fillMaxSize())
            PageID.Login.value -> PageLogin(Modifier.fillMaxSize())
            PageID.Walk.value -> PageTest(Modifier.fillMaxSize())
            PageID.Explore.value -> PageExplore(Modifier.fillMaxSize())
            PageID.Chat.value -> PageChat(Modifier.fillMaxSize())
            PageID.My.value -> PageMy(Modifier.fillMaxSize())
            PageID.Dog.value -> PageDog(Modifier.fillMaxSize())
            PageID.User.value -> PageUser(Modifier.fillMaxSize())
            PageID.Album.value -> PageAlbum(Modifier.fillMaxSize())
            PageID.Alarm.value -> PageAlarm(Modifier.fillMaxSize())
            PageID.Friend.value -> PageFriend(Modifier.fillMaxSize())
            PageID.Splash.value -> PageSplash(Modifier.fillMaxSize())
            PageID.ServiceTerms.value -> PageServiceTerms(Modifier.fillMaxSize())
            PageID.Privacy.value -> PagePrivacy(Modifier.fillMaxSize())
            PageID.AddDog.value -> PageAddDog(Modifier.fillMaxSize())
            PageID.AddDogCompleted.value -> PageAddDogCompleted(Modifier.fillMaxSize())
            PageID.Picture.value -> PagePicture(Modifier.fillMaxSize())
            PageID.PictureViewer.value -> PagePictureViewer(Modifier.fillMaxSize())
            PageID.ModifyUser.value -> PageModifyUser(Modifier.fillMaxSize())
            PageID.ModifyPet.value -> PageModifyPet(Modifier.fillMaxSize())
            PageID.ModifyPetHealth.value -> PageModifyPetHealth(Modifier.fillMaxSize())
            PageID.EditProfile.value -> PageEditProfile(Modifier.fillMaxSize())
            PageID.Setup.value -> PageSetup(Modifier.fillMaxSize())
            PageID.MyAccount.value -> PageMyAccount(Modifier.fillMaxSize())
            PageID.BlockUser.value -> PageBlockUser(Modifier.fillMaxSize())
            PageID.ManageDogs.value -> PageManageDogs(Modifier.fillMaxSize())
            PageID.MyLv.value -> PageMyLv(Modifier.fillMaxSize())
            PageID.MyPoint.value -> PageMyPoint(Modifier.fillMaxSize())
            PageID.WalkList.value -> PageWalkList(Modifier.fillMaxSize())
            PageID.WalkHistory.value -> PageWalkHistory(Modifier.fillMaxSize())
            PageID.WalkInfo.value -> PageWalkInfo(Modifier.fillMaxSize())
            PageID.WalkReport.value -> PageWalkReport(Modifier.fillMaxSize())
            PageID.ChatRoom.value -> PageChatRoom(Modifier.fillMaxSize())
        }
    }
}




