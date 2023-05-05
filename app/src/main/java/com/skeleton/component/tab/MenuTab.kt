package com.skeleton.component.tab

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.progress.Step
enum class MenuTabType() {
    Line {
        override fun bgColor(color:Color):Color = ColorTransparent.clearUi
    },
    Box {
        override var strokeWidth: Float = DimenStroke.light
        override var radius: Float = DimenRadius.medium
        override var textSize: Float = FontSize.thin
        override var btnBgColor:Color = ColorApp.white

    };
    open var strokeWidth:Float = 0.0f
    open var radius:Float = 0.0f
    open var textSize:Float = FontSize.light
    open var btnBgColor:Color = ColorTransparent.clearUi
    open fun bgColor(color:Color):Color = color
}

@Composable
fun MenuTab(
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
fun MenuTabComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MenuTab(
            index = 1,
            total = 10,
            info = "info",
            subInfo = "subInfo"
        )
    }

}