package com.skeleton.component.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.skeleton.theme.*

enum class PropertyInfoType {
    Blank, Normal, Impect;


    val bgColor: Color
        get() = when (this) {
            Blank -> ColorTransparent.clear
            Normal -> ColorApp.whiteDeepLight
            Impect -> ColorApp.grey50
        }

    val valueTextStyle:FontWeight
        get() = when (this) {
            Blank -> FontWeight.Medium
            Normal -> FontWeight.Medium
            Impect -> FontWeight.Bold
        }

    val valueTextSize:Float
        get() = when (this) {
            Blank -> FontSize.light
            Normal -> FontSize.light
            Impect -> FontSize.medium
        }

    val boxHeight:Float
        get() = when (this) {
            Impect -> 72.0f
            else -> 80.0f
        }

    val spacing:Float
        get() = when (this) {
            Impect -> 0.0f
            else -> DimenMargin.micro
        }
}

@Composable
fun PropertyInfo(
    modifier: Modifier = Modifier,
    type:PropertyInfoType = PropertyInfoType.Normal,
    @DrawableRes icon:Int? = null,
    title:String? = null,
    value:String = "",
    unit:String? = null,
    color:Color = ColorBrand.primary,
    bgColor:Color? = null,
    alignment:Alignment.Horizontal = Alignment.CenterHorizontally
) {
    AppTheme {
        Column(
            modifier = (
                    if(alignment == Alignment.CenterHorizontally)
                        modifier.clip(RoundedCornerShape(CornerSize(DimenRadius.light.dp)))
                            .background(bgColor ?: type.bgColor)
                            .fillMaxWidth()
                            .height(type.boxHeight.dp)
                    else
                        modifier.background(bgColor ?: type.bgColor)
                            .wrapContentSize()
                )
            ,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp, Alignment.CenterVertically),
            horizontalAlignment = alignment
        ) {
            icon?.let {
                Image(
                    painterResource(it),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier.size(DimenIcon.light.dp)
                )
            }
            title?.let {
                Text(
                    text = it,
                    fontSize = FontSize.tiny.sp,
                    color = ColorApp.grey400
                )
            }
            Text(
                text = value,
                fontWeight = type.valueTextStyle,
                fontSize = type.valueTextSize.sp,
                color = ColorApp.grey400
            )
            unit?.let {
                Text(
                    text = it,
                    fontSize = FontSize.tiny.sp,
                    color = ColorApp.grey400
                )
            }
        }
    }
}

@Preview
@Composable
fun PropertyInfoComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PropertyInfo(
            type = PropertyInfoType.Blank,
            icon = R.drawable.speed,
            title = "Weight",
            value = "8.1 kg"
        )
        PropertyInfo(
            type = PropertyInfoType.Normal,
            title = "Weight",
            value = "8.1 kg"
        )

        PropertyInfo(
            type = PropertyInfoType.Impect,
            title = "Weight",
            value = "8.1",
            unit = "kg",
            alignment = Alignment.Start
        )
    }
}