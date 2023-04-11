package com.skeleton.view.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*

enum class RectButtonType {
    Tiny {
        override var iconSize: Float = DimenIcon.thin
        override var bgSize: Float = DimenButton.regularExtra
        override var textSize: Float = FontSize.micro
        override var spacing: Float = 0.0f
        override var radius: Float = DimenRadius.thin
    },

    Medium{};
    open var iconSize:Float = DimenIcon.heavy
    open var bgSize:Float = 164.0f
    open var textSize:Float = FontSize.light
    open var spacing:Float = DimenMargin.regularUltra
    open var radius:Float = DimenRadius.regular

}


@Composable
fun RectButton(
    type:RectButtonType = RectButtonType.Medium,
    @DrawableRes icon:Int? = null,
    text:String? = null,
    index:Int = 0,
    color:Color = ColorBrand.primary,
    defaultColor:Color = ColorApp.grey500,
    bgColor:Color = ColorApp.white,
    isSelected:Boolean = false,
    action:(Int) -> Unit

) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(type.bgSize.dp)
                .clip(RoundedCornerShape(type.radius.dp))
                .background(if (isSelected) color else bgColor)
                .border(
                    width = DimenStroke.light.dp,
                    color = if(isSelected) color  else ColorApp.grey200,
                    shape = RoundedCornerShape(type.radius.dp)
                )
            ,
            contentAlignment = Alignment.Center
        ) {
            Column (
                modifier = Modifier.padding(0.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = type.spacing.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(if (!isSelected) defaultColor else bgColor),
                        modifier = Modifier.size(type.iconSize.dp, type.iconSize.dp)
                    )
                }
                text?.let {
                    Text(
                        it,
                        fontSize = type.textSize.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (!isSelected) defaultColor else bgColor,
                        textAlign = TextAlign.Start
                    )
                }
            }
            TransparentButton(
                action = {
                    action(index)
                }
            )
        }
    }
}

@Preview
@Composable
fun RectButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RectButton(
            type = RectButtonType.Medium,
            icon = R.drawable.noimage_1_1,
            text = "Rect BUTTON",
            color = ColorApp.grey400,
            isSelected = false
        ) {

        }
        RectButton(
            type = RectButtonType.Medium,
            icon = R.drawable.noimage_1_1,
            text = "Rect BUTTON",
            color = ColorBrand.primary,
            isSelected = true
        ) {

        }
        RectButton(
            type = RectButtonType.Tiny,
            icon = R.drawable.noimage_1_1,
            text = "Rect BUTTON",
            color = ColorBrand.primary,
            isSelected = true
        ) {

        }
    }

}