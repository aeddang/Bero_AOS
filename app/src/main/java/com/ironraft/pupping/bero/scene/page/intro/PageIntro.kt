package com.ironraft.pupping.bero.scene.page.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEvent
import com.ironraft.pupping.bero.SceneEventType
import com.lib.page.PageComposePresenter
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked] lambda
 * that triggers canceling the order and passes the final order to [onSendButtonClicked] lambda
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIntro(
    modifier: Modifier = Modifier
){
    val pagePresenter = koinInject<PageComposePresenter>()
    val appSceneObserver = koinInject<AppSceneObserver>()
    data class IntroData (
        var idx:Int = -1,
        @androidx.annotation.RawRes var ani:Int,
    )
    val pages:ArrayList<IntroData> = arrayListOf(
        IntroData(0, R.raw.onboarding_01),
        IntroData(1, R.raw.onboarding_02),
        IntroData(2, R.raw.onboarding_03)
    )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var isComplete by remember { mutableStateOf(false) }

    fun movePage(dr:Int){
        val move = pagerState.currentPage + dr
        if (move < 0) return
        if (move >= pages.count()) return
        coroutineScope.launch {
            pagerState.animateScrollToPage(move)
            isComplete = pagerState.currentPage == pages.count()-1
        }
    }
    fun moveIdx(idx:Int){
        coroutineScope.launch {
            pagerState.animateScrollToPage(idx)
            isComplete = pagerState.currentPage == pages.count()-1
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        contentAlignment = Alignment.BottomCenter

    ) {
        HorizontalPager(
            pageCount = pages.count(),
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Our page content
            val data = pages[page]
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(data.ani)
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = DimenMargin.medium.dp)
                .padding(horizontal = DimenMargin.regular.dp),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tiny.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                pages.forEach {page ->
                    CircleButton(
                        type = CircleButtonType.Tiny,
                        isSelected = page.idx == pagerState.currentPage
                    ) {
                        moveIdx(page.idx)
                    }
                }
            }
            FillButton(
                type = FillButtonType.Fill,
                text = if (isComplete) stringResource(R.string.introComplete) else stringResource(R.string.button_next),
                color = if (isComplete) ColorBrand.primary else ColorApp.black
            ) {
                if (isComplete) appSceneObserver.event.value = SceneEvent(SceneEventType.Initate)
                else movePage(1)
            }
        }
    }
}

@Preview
@Composable
fun PageIntroPreview(){
    PageIntro(
    )
}
