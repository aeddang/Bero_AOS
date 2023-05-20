
package com.ironraft.pupping.bero

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ironraft.pupping.bero.activityui.*
import com.ironraft.pupping.bero.scene.component.tab.BottomTab
import com.ironraft.pupping.bero.scene.page.intro.PageIntro
import com.ironraft.pupping.bero.scene.page.login.PageLogin
import com.ironraft.pupping.bero.scene.page.PageSplash
import com.ironraft.pupping.bero.scene.page.PageTest
import com.ironraft.pupping.bero.scene.page.PageTest1
import com.ironraft.pupping.bero.scene.page.my.PageMy
import com.ironraft.pupping.bero.scene.page.pet.PageDog
import com.ironraft.pupping.bero.scene.page.popup.PageAlbum
import com.ironraft.pupping.bero.scene.page.popup.PagePictureViewer
import com.ironraft.pupping.bero.scene.page.popup.PageServiceTerms
import com.ironraft.pupping.bero.scene.page.profile.PageAddDog
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogCompleted
import com.ironraft.pupping.bero.scene.page.user.PageUser
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.RepositoryEvent
import com.ironraft.pupping.bero.store.RepositoryStatus
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.lib.page.*
import com.lib.util.PageLog
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject

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



@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PageApp(
    pageNavController: NavHostController,
    radioState: ModalBottomSheetState,
    selectState: ModalBottomSheetState,
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier
) {
    val activityModel = koinInject<ActivityModel>()
    val pagePresenter = koinInject<PageComposePresenter>()
    val pageAppViewModel = koinInject<PageAppViewModel>()
    val currentTopPage by pageAppViewModel.currentTopPage.observeAsState()
    val viewModel = koinInject<AppSceneObserver>()
    var loadingInfo:ArrayList<String>? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isLock by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(ColorApp.black)){
        Scaffold(
            bottomBar = {
                currentTopPage?.let {
                    if (activityModel.useBottomTabPage(it.pageID)) BottomTab()
                }
            }
        ) { innerPadding ->
            AppTheme {
                AnimatedNavHost(
                    navController = pageNavController,
                    startDestination = PageID.Splash.value,
                    modifier = modifier
                        .padding(innerPadding)
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
            PageID.Explore.value -> PageTest1(Modifier.fillMaxSize())
            PageID.Chat.value -> PageTest(Modifier.fillMaxSize())
            PageID.My.value -> PageMy(Modifier.fillMaxSize())
            PageID.Dog.value -> PageDog(Modifier.fillMaxSize())
            PageID.User.value -> PageUser(Modifier.fillMaxSize())
            PageID.Album.value -> PageAlbum(Modifier.fillMaxSize())
            PageID.Splash.value -> PageSplash(Modifier.fillMaxSize())
            PageID.ServiceTerms.value -> PageServiceTerms(Modifier.fillMaxSize())
            PageID.AddDog.value -> PageAddDog(Modifier.fillMaxSize())
            PageID.AddDogCompleted.value -> PageAddDogCompleted(Modifier.fillMaxSize())
            PageID.PictureViewer.value -> PagePictureViewer(Modifier.fillMaxSize())
        }
    }
}




