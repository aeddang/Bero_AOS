package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.rememberWebViewState
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.theme.ColorBrand
import com.skeleton.view.WebViewPage
import org.koin.compose.koinInject


/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked] lambda
 * that triggers canceling the order and passes the final order to [onSendButtonClicked] lambda
 */
@Composable
fun PageServiceTerms(
    modifier: Modifier = Modifier,
    page:PageObject? = null
){
    val pagePresenter = koinInject<PageComposePresenter>()
    val webViewState =
        rememberWebViewState(
            url = "https://bero.dog/termsofservice",
            additionalHttpHeaders = emptyMap()
        )
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TitleTab(
                useBack = true
            ){
                when(it){
                    TitleTabButtonType.Back -> pagePresenter.goBack()
                    else -> {}
                }
            }
            WebViewPage("https://bero.dog/termsofservice")
        }
    }
}

@Preview
@Composable
fun PageServiceTermsPreview(){
    PageServiceTerms(
    )
}
