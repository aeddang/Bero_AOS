package com.ironraft.pupping.bero.scene.page.layer

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
import dev.burnoo.cokoin.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LayerPageLevelUp(
    modifier: Modifier = Modifier,
    close:()->Unit
){
    val dataProvider:DataProvider = get()
    val coroutineScope = rememberCoroutineScope()
    var lvValue:String? by remember { mutableStateOf(null)}
    var lv:Lv? by remember { mutableStateOf(null)}
    var prevLv:String by remember { mutableStateOf("")}
    var currentLv:String by remember { mutableStateOf("")}
    var color by remember { mutableStateOf(ColorApp.white)}
    var isEffect:Boolean by remember { mutableStateOf(false)}

    fun onInit():Boolean{
        val userlv = dataProvider.user.lv
        val prev = userlv-1
        prevLv = Lv.prefix + prev.toString()
        currentLv = Lv.prefix + userlv.toString()
        coroutineScope.launch {
            delay(500)
            color = Lv.getLv(prev).color
            lv = Lv.getLv(prev)
            lvValue = prev.toString()
        }

        coroutineScope.launch {
            delay(2500)
            color = Lv.getLv(userlv).color
            lv = Lv.getLv(userlv)
            lvValue = userlv.toString()
            isEffect = true
        }
        return true
    }

    val isInit:Boolean by remember { mutableStateOf(onInit())}
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorTransparent.black80),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier.padding(bottom = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(visible = isEffect, enter = fadeIn(), exit = fadeOut()) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.levelup)
                )
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.wrapContentSize(),
                    contentScale = ContentScale.Fit
                )
            }
            AnimatedVisibility(visible = lv != null, enter = fadeIn(), exit = fadeOut()) {
                lv?.icon?.let { icon ->
                    Image(
                        painterResource(icon),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(bottom = 260.dp)
                    )
                }

            }
            lvValue?.let {
                Text(
                    it,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize.black.sp,
                    color = ColorApp.white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 240.dp)
                )
            }
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                stringResource(id = R.string.levelUpText),
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.black.sp,
                color = ColorApp.white,
                textAlign = TextAlign.Center
            )
            ChangeBox(
                modifier = Modifier
                    .padding(top = DimenMargin.regularUltra.dp),
                prev = prevLv,
                next = currentLv,
                activeColor = color
            )
            FillButton(
                modifier = Modifier.width(164.dp)
                    .padding(top = DimenMargin.mediumUltra.dp),
                type = FillButtonType.Fill,
                text = stringResource(id = R.string.confirm),
                size = DimenButton.regular,
                color = ColorBrand.primary,
                isActive = true
            ) {
                close()
            }
        }
    }
}

