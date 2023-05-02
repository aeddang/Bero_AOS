package com.ironraft.pupping.bero.scene.page.popup

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked] lambda
 * that triggers canceling the order and passes the final order to [onSendButtonClicked] lambda
 */
@SuppressLint("SetJavaScriptEnabled", "CoroutineCreationDuringComposition")
@Composable
fun PageServiceTerms(
    modifier: Modifier = Modifier,
    page:PageObject? = null
){
    val coroutineScope = rememberCoroutineScope()
    val pagePresenter = koinInject<PageComposePresenter>()
    var isInit by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val webViewState =
        rememberWebViewState(
            url = "https://bero.dog/termsofservice",
            additionalHttpHeaders = emptyMap()
        )
    coroutineScope.launch {
        delay(300)
        isInit = true
    }
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            if (isInit){
                WebView(
                    modifier = Modifier
                        .padding(top = DimenApp.top.dp)
                        .fillMaxSize()
                        .background(ColorApp.white),
                    state = webViewState,
                    onCreated = {
                        it.settings.javaScriptEnabled = true
                        isLoading = false
                    }
                )
                AnimatedVisibility(visible = isLoading) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ColorApp.white)
                    )
                }
            }
            TitleTab(
                useBack = true
            ){
                when(it){
                    TitleTabButtonType.Back -> pagePresenter.goBack()
                    else -> {}
                }
            }
        }
    }
}

@Preview
@Composable
fun PageServiceTermsPreview(){

    PageServiceTerms(
    )
}
