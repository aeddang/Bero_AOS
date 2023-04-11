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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*

enum class FillButtonType {
    Fill {
        override fun textColor(color:Color):Color = ColorApp.white
    },
    Stroke{
        override fun bgColor(color:Color):Color = ColorApp.white
        override fun iconType(color:Color): ColorFilter = ColorFilter.tint(color = color)
        override var strokeWidth: Float = DimenStroke.light

    };
    open fun bgColor(color:Color):Color {
        return color
    }
    open fun textColor(color:Color):Color {
        return color
    }
    open fun iconType(color:Color):ColorFilter? {
        return null
    }
    open var strokeWidth:Float = 0.0f
}

@Composable
fun TransparentButton(
    action:() -> Unit
){
    FloatingActionButton(
        onClick = action,
        Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = ColorTransparent.clear,
        contentColor = contentColorFor(ColorTransparent.clear),
        elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp)
    ) {

    }
}

@Composable
fun FillButton(
    type:FillButtonType = FillButtonType.Fill,
    @DrawableRes icon:Int? = null,
    iconType:ColorFilter? = null,
    text:String = "",
    index:Int = 0,
    size:Float = DimenButton.mediumExtra,
    radius:Float = DimenRadius.thin,
    color:Color = ColorApp.black,
    textColor:Color? = null,
    gradient:Brush? = null,
    textSize:Float = FontSize.light,
    isActive:Boolean = true,
    action:(Int) -> Unit

) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size.dp)
                .clip(RoundedCornerShape(radius.dp))
                .background(
                    type
                        .bgColor(color)
                        .copy(if (isActive) 1.0f else 0.3f)
                )
                .border(
                    width = type.strokeWidth.dp,
                    color = color,
                    shape = RoundedCornerShape(radius.dp)
                )

                ,
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = iconType ?: type.iconType(color),
                        modifier = Modifier.size(DimenIcon.light.dp, DimenIcon.light.dp)
                    )
                }
                Text(
                    text,
                    fontSize = textSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor ?: type.textColor(color)
                )
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
fun FillButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FillButton(
            type = FillButtonType.Fill,
            text = "FILL BUTTON"
        ) {

        }
        FillButton(
            type = FillButtonType.Fill,
            iconType = ColorFilter.tint(ColorBrand.primary),
            icon = R.drawable.noimage_1_1,
            text = "FILL BUTTON",
            color = ColorBrand.primary,
            isActive = true
        ) {

        }
        FillButton(
            type = FillButtonType.Stroke,
            text = "STROKE BUTTON"
        ) {

        }
    }

}