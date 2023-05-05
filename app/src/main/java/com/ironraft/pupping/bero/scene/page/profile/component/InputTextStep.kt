package com.ironraft.pupping.bero.scene.page.profile.component

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.progress.Step


@Composable
fun InputTextStep(
    index:Int,
    total:Int,
    image: Bitmap? = null,
    info:String? = null,
    subInfo:String? = null
) {
    AppTheme {
        Column (
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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
fun InputTextStepComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InputTextStep(
            index = 1,
            total = 10,
            info = "info",
            subInfo = "subInfo"
        )
    }

}