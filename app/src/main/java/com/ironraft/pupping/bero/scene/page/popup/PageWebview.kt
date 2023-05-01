package com.ironraft.pupping.bero.scene.page.popup

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.ironraft.pupping.bero.R
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject


/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked] lambda
 * that triggers canceling the order and passes the final order to [onSendButtonClicked] lambda
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PageWebview(
    modifier: Modifier = Modifier,
    page:PageObject? = null
){
    val webViewState =
        rememberWebViewState(
            url = "www.naver.com",
            additionalHttpHeaders = emptyMap()
        )
    Box (
        modifier = modifier.fillMaxSize().background(ColorBrand.bg)
    ) {
        WebView(
            state = webViewState,
            onCreated = { it.settings.javaScriptEnabled = true }

        )
    }
}

@Preview
@Composable
fun PageWebviewPreview(){
    PageWebview(
    )
}
