package com.ironraft.pupping.bero.scene.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenIcon


@Composable
fun PageSplash(
    modifier: Modifier = Modifier
){

    Box (
        modifier = Modifier.fillMaxSize().background(ColorBrand.primary),
        contentAlignment = Alignment.Center
    ){
        Image(
            painterResource(R.drawable.logo_splash),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(top = 24.dp).size(200.dp)
        )
    }
}

@Preview
@Composable
fun PageSplashPreview(){
    PageSplash(
    )
}
