package com.ironraft.pupping.bero.scene.page.layer

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.lib.page.PageComposePresenter
import com.skeleton.component.tab.ChangeBox
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenButton
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import dev.burnoo.cokoin.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LayerPageWebview(
    modifier: Modifier = Modifier,
    link:String,
    close:()->Unit
){
    var isLoading by remember { mutableStateOf(true) }
    val webViewState =
        rememberWebViewState(
            url = link,
            additionalHttpHeaders = emptyMap()
        )

    fun onInit():Boolean{

        return true
    }

    val isInit:Boolean by remember { mutableStateOf(onInit())}
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorApp.white),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
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
            TitleTab(
                useBack = true
            ){
                when(it){
                    TitleTabButtonType.Back -> close()
                    else -> {}
                }
            }
        }
    }
}

