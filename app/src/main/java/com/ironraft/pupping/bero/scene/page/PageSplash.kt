package com.ironraft.pupping.bero.scene.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ironraft.pupping.bero.R
import com.skeleton.theme.ColorBrand


@Composable
fun PageSplash(
    modifier: Modifier = Modifier
){

    Box (
        modifier = Modifier.fillMaxSize().background(ColorBrand.primary),
        contentAlignment = Alignment.Center
    ){
        Image(
            painterResource(R.drawable.splash_logo),
            modifier = Modifier.wrapContentSize(),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
    }
}

@Preview
@Composable
fun PageSplashPreview(){
    PageSplash(
    )
}
