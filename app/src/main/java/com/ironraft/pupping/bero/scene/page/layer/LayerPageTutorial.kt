package com.ironraft.pupping.bero.scene.page.layer

import androidx.annotation.RawRes
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
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.skeleton.component.tab.ChangeBox
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenButton
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.TransparentButton
import dev.burnoo.cokoin.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LayerPageTutorial(
    modifier: Modifier = Modifier,
    @RawRes ani:Int,
    isAutoClose:Boolean = false,
    close:()->Unit
){
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(ani)
    )
    val progress by animateLottieCompositionAsState(composition)
    LaunchedEffect(key1 = progress){
        if (!isAutoClose) return@LaunchedEffect
        if (progress == 1f) close()
    }

    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorTransparent.black80),
        contentAlignment = Alignment.Center
    ) {

        LottieAnimation(
            composition = composition,
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Fit
        )
        TransparentButton(
            action = {
                close()
            }
        )
    }
}

