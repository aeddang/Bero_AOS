package com.ironraft.pupping.bero.scene.page.walk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.walk.component.PlayBox
import com.ironraft.pupping.bero.scene.page.walk.component.WalkBox
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.scene.page.walk.model.WalkPickViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.ComponentViewModel
import com.lib.page.PageComposePresenter
import com.skeleton.component.map.googlemap.CPGoogleMap
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.DimenStroke
import dev.burnoo.cokoin.get

@Composable
fun PageWalk(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Walk.value
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val playMapModel:PlayMapModel by remember { mutableStateOf(PlayMapModel()) }
    val walkPickViewModel:WalkPickViewModel by remember {
        mutableStateOf(WalkPickViewModel(repository).initSetup(owner))
    }
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        contentAlignment = Alignment.Center
    ){
        CPGoogleMap()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = DimenMargin.regular.dp,
                    bottom = (DimenApp.bottom + DimenMargin.thin).dp
                )
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            WalkBox(playMapModel = playMapModel)
            Spacer(modifier = Modifier.weight(1.0f))
            PlayBox(
                playMapModel = playMapModel,
                walkPickViewModel = walkPickViewModel,
                isInitable = true
            )
        }
    }
}

@Preview
@Composable
fun PageSplashPreview(){
    PageWalk(
    )
}
