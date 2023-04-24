
package com.ironraft.pupping.bero

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ironraft.pupping.bero.scene.component.tab.BottomTab
import com.ironraft.pupping.bero.scene.page.PageSplashCompose
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.lib.page.PageAppViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.PageLog
import com.skeleton.component.dialog.Alert
import com.skeleton.theme.ColorApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject
/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun PageAppBar(
    page: PageID,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagePresenter = koinInject<PageComposePresenter>()
    TopAppBar(
        title = { Text(page.value) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.button_close)
                    )
                }
            }
        }
    )
}


class PageActivityViewModel {
    private val _isTest = MutableStateFlow<Boolean>(false)
    val isTest: StateFlow<Boolean> = _isTest
    fun onTestChanged(test: Boolean) {
        _isTest.value = test
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageApp(
    pageNavController: NavHostController = rememberAnimatedNavController(),
    modifier: Modifier = Modifier
) {
    val pagePresenter = koinInject<PageComposePresenter>()
    val activityModel = koinInject<ActivityModel>()
    val pageAppViewModel = koinInject<PageAppViewModel>()
    val viewModel = koinInject<PageActivityViewModel>()
    val currentTopPage:PageObject? by pageAppViewModel.currentTopPage.observeAsState(pagePresenter.currentPage)
    //val isTest by viewModel.isTest.collectAsState()
    //val backStackEntry by pageNavController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            currentTopPage?.let {
                if (activityModel.useBottomTabPage(it.pageID)) BottomTab()
            }
        }
    ) { innerPadding ->

        AnimatedNavHost(
            navController = pageNavController,
            startDestination = "",
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(ColorApp.blue)
        ) {
            PageID.values().forEach {
                getPageComposable(nav = this, route = it.value,pagePresenter.currentTopPage?.isPopup ?: false)
            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
fun getPageComposable(nav:NavGraphBuilder, route:String, isPopup:Boolean){
    nav.composable(route = route,
        enterTransition = {
            if(isPopup) slideInVertically (tween(1500))
            else fadeIn(animationSpec = tween(1500))
        },
        exitTransition = {
            if(isPopup) slideOutVertically (tween(1500))
            else fadeOut(animationSpec = tween(1500))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(1500))
        },
        popExitTransition = {
            if(isPopup) slideOutVertically(tween(1500))
            else fadeOut(animationSpec = tween(1500))
        }
    ) {
        when (route) {
            PageID.Walk.value -> PageSplashCompose("Walk", Modifier.fillMaxSize())
            PageID.My.value -> PageSplashCompose("My", Modifier.fillMaxSize())
            PageID.Login.value -> PageSplashCompose("Login", Modifier.fillMaxSize())
            else -> PageSplashCompose("Walk", Modifier.fillMaxSize())
        }
    }
}




