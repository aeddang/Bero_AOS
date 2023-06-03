package com.ironraft.pupping.bero.scene.page.walk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.maps.android.compose.GoogleMap
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.skeleton.component.map.googlemap.CPGoogleMap
import com.skeleton.theme.ColorBrand
import dev.burnoo.cokoin.get

@Composable
fun PageWalk(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Walk.value
    val dataProvider: DataProvider = get()
    val pagePresenter: PageComposePresenter = get()
    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        contentAlignment = Alignment.Center
    ){
        CPGoogleMap()
    }
}

@Preview
@Composable
fun PageSplashPreview(){
    PageWalk(
    )
}
