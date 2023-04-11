
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
import com.ironraft.pupping.bero.scene.page.PageSplashCompose
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
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
                        contentDescription = stringResource(R.string.btnClose)
                    )
                }
            }
        }
    )
}

@Composable
fun PageAppNavi(
    modifier: Modifier = Modifier
) {
    val pagePresenter = koinInject<PageComposePresenter>()
    val pageActivityViewModel = koinInject<PageActivityViewModel>()
    Column (
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextButton({
            pagePresenter.changePage(PageObject(PageID.Login.value))
        }){
            Text(
                text = PageID.Login.value,
                color = colorResource(R.color.app_black),
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextButton({
            pagePresenter.changePage(PageObject(PageID.Walk.value))
        }){
            Text(
                text = PageID.Walk.value,
                color = colorResource(R.color.app_black),
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextButton({
            pagePresenter.openPopup(PageObject(PageID.My.value))
        }){
            Text(
                text = PageID.My.value,
                color = colorResource(R.color.app_black),
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextButton({
            pageActivityViewModel.onTestChanged(!pageActivityViewModel.isTest.value)
        }){
            Text(
                text = "test",
                color = colorResource(R.color.app_black),
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
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
    val viewModel = koinInject<PageActivityViewModel>()
    val isTest by viewModel.isTest.collectAsState()
    val backStackEntry by pageNavController.currentBackStackEntryAsState()

    val currentPageID = PageID.valueOf(
        backStackEntry?.destination?.route ?: PageID.Walk.value
    )

    Scaffold(
        topBar = {
            Column (
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PageAppBar(
                    page = currentPageID,
                    canNavigateBack = pageNavController.previousBackStackEntry != null,
                    navigateUp = { pageNavController.navigateUp() }
                )
                PageAppNavi(

                )
            }
        }
    ) { innerPadding ->
        val currentPage = pagePresenter.currentTopPage

        AnimatedNavHost(
            navController = pageNavController,
            startDestination = PageID.Walk.value,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(ColorApp.blue)
        ) {
            currentPage?.let {
                PageLog.d(it, tag = "AnimatedNavHost")
            }
            PageID.values().forEach {
                getPageComposable(nav = this, route = it.value,currentPage?.isPopup ?: false)
            }
        }
        if (isTest){
            Alert()
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




