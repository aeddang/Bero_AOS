package com.skeleton.view.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*


enum class CircleButtonType() {
    Tiny {
        override var size: Float = DimenIcon.microUltra
    },
    Image {
        override var size: Float = DimenIcon.light
    },
    Icon, Text;
    open var size:Float = DimenIcon.mediumUltra
}


@Composable
fun CircleButton(
    type: CircleButtonType = CircleButtonType.Tiny,
    @DrawableRes icon: Int? = null,
    value: String? = null,
    originSize:Float? = null,
    strokeWidth:Float = 0.0f,
    defaultColor: Color = ColorApp.grey300,
    activeColor: Color = ColorBrand.primary,
    index:Int = 0,
    isSelected:Boolean = false,
    modifier: Modifier = Modifier,
    action:(Int) -> Unit
) {
    val imagePath: String? by remember { mutableStateOf(
        if(type == CircleButtonType.Image) value else null
    )}
    val text:String? by remember { mutableStateOf(
        if(type == CircleButtonType.Text) value else null
    )}

    AppTheme {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(
                    if (isSelected) activeColor
                    else if (type == CircleButtonType.Tiny) defaultColor else ColorApp.white
                )
                .border(
                    width = strokeWidth.dp,
                    color = if (isSelected) ColorApp.white else ColorApp.grey200,
                    shape = CircleShape
                )
                .size((originSize ?: type.size).dp),
            contentAlignment = Alignment.Center

        ) {
            icon?.let {
                Image(
                    painterResource(it),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(if(isSelected) ColorApp.white else defaultColor),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DimenMargin.tinyExtra.dp)
                )
            }
            text?.let {
                Text(
                    it,
                    fontSize = FontSize.tiny.sp,
                    fontWeight = FontWeight.Medium,
                    color = if(isSelected) ColorApp.white else defaultColor
                )
            }
            imagePath?.let {
                ProfileImage(
                    imagePath = it,
                    size = type.size
                )
            }

            /*
            painter?.let {

                Image(
                    it,
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            */
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
fun CircleButtonComposePreview(){
    Column (
        modifier = Modifier
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircleButton(
            type = CircleButtonType.Tiny,
            isSelected = true
        ) {

        }
        CircleButton(
            type = CircleButtonType.Icon,
            icon = com.ironraft.pupping.bero.R.drawable.add_friend,
            strokeWidth = DimenStroke.regular,
            isSelected = true
        ) {

        }
        CircleButton(
            type = CircleButtonType.Text,
            value = "LV99",
            strokeWidth = DimenStroke.regular,
            defaultColor = ColorApp.green,
            isSelected = false
        ) {

        }
        CircleButton(
            type = CircleButtonType.Image,
            value = "LV99",
            strokeWidth = DimenStroke.regular,
            defaultColor = ColorApp.green,
            isSelected = false
        ) {

        }
    }

}