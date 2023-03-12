package com.ironraft.pupping.bero.scene.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

@Composable
fun PageSplashCompose(
    modifier: Modifier = Modifier
){
    val resources = LocalContext.current.resources
    Box {
        Image(
            painter= painterResource(R.drawable.ic_goal),
            contentDescription=null, // 필수 param
        )
        Column (
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text(
                text = "Hello, DP!",
                color = colorResource(R.color.app_black),
                fontSize = dpToSp(dimensionResource(R.dimen.font_light).value.dp),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Hello, SP",
                color = colorResource(R.color.app_black),
                fontSize = dimensionResource(R.dimen.font_light).value.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun PageSplashComposePreview(){
    PageSplashCompose(
    )
}
