package com.skeleton.component.progress

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.progress.Step

@Composable
fun StepInfo(
    index:Int,
    total:Int,
    image:Bitmap? = null,
    info:String? = null,
    subInfo:String? = null
) {
    AppTheme {
        Column (
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.Start
        ) {
            var isOn by remember { mutableStateOf(true) }
            image?.let {
                ProfileImage(
                    image = it,
                    size = DimenProfile.light
                )
            }
            Step(
                index = index,
                total = total
            )
            info?.let {
                Text(
                    it,
                    fontSize = FontSize.bold.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorApp.black,
                    modifier = Modifier.padding(top = DimenMargin.tinyExtra.dp)
                )
            }
            subInfo?.let {
                Text(
                    it,
                    fontSize = FontSize.thin.sp,
                    color = ColorApp.grey400,
                    modifier = Modifier.padding(top = DimenMargin.regular.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun StepInfoComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StepInfo(
            index = 1,
            total = 10,
            info = "info",
            subInfo = "subInfo"
        )
    }

}