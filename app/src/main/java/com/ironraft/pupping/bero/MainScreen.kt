
package com.ironraft.pupping.bero

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ironraft.pupping.bero.scene.page.PageSplashCompose
import com.ironraft.pupping.bero.scene.page.PageSplashComposePreview
import com.ironraft.pupping.bero.scene.page.viewmodel.BasePageViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
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
            IconButton(onClick = {
                pagePresenter.changePage(PageObject(PageID.Login.value))
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.loginStart)
                )
            }
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
fun PageApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentPageID = PageID.valueOf(
        backStackEntry?.destination?.route ?: PageID.Walk.value
    )

    Scaffold(
        topBar = {
            PageAppBar(
                page = currentPageID,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = PageID.Walk.value,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = PageID.Walk.value) {
                PageSplashCompose("Walk")
            }
            composable(route = PageID.My.value) {
                PageSplashCompose("My")
            }
            composable(route = PageID.Login.value) {
                PageSplashCompose("Login")
            }
        }
    }
}


