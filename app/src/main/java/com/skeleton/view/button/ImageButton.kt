package com.skeleton.view.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*



@Composable
fun ImageButton(
    @DrawableRes defaultImage:Int = R.drawable.noimage_1_1,
    @DrawableRes activeImage:Int? = null,
    size:Float = DimenIcon.light,
    sizeHeight:Float? = null,
    iconText:String? = null,
    text:String? = null,
    defaultColor:Color = ColorApp.black,
    activeColor:Color = ColorBrand.primary,
    padding:Float = 0.0f,
    index:Int = 0,
    isSelected:Boolean = false,
    modifier: Modifier = Modifier,
    action:(Int) -> Unit
) {
    AppTheme {
        Box(
            modifier = modifier
                .wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(padding.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .padding(if (iconText == null) 0.0f.dp else DimenMargin.micro.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(0.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            space = DimenMargin.micro.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(
                                if (isSelected) activeImage ?: defaultImage else defaultImage
                            ),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if (isSelected) activeColor else defaultColor),
                            modifier = Modifier.size(size.dp, sizeHeight?.dp ?: size.dp)
                        )
                        text?.let {
                            Text(
                                it,
                                fontSize = FontSize.tiny.sp,
                                color = if (isSelected) activeColor else defaultColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                iconText?.let {
                    Text(
                        it,
                        fontSize = FontSize.micro.sp,
                        color = ColorApp.white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(DimenIcon.tiny.dp, DimenIcon.tiny.dp)
                            .clip(CircleShape)
                            .background(ColorBrand.primary)
                            .border(
                                width = DimenStroke.light.dp,
                                color = ColorApp.white,
                                shape = CircleShape
                            )
                    )
                }
            }
            TransparentButton(
                modifier = Modifier.matchParentSize(),
                action = {
                    action(index)
                }
            )
            /*
            Box(
                modifier = Modifier
                    .size(
                        (size + (padding + DimenIcon.micro)*2.0f).dp,
                        ((sizeHeight ?: size)
                                + (if (text == null) 0.0f else (FontSize.tiny+DimenMargin.micro))
                                + (padding + DimenIcon.micro)*2.0f).dp
                    )
            ) {

            }*/

        }
    }
}

@Preview
@Composable
fun ImageButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp).background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ImageButton(
            defaultImage = R.drawable.noimage_1_1,
            activeImage = R.drawable.noimage_1_1,
            text = "Test",
            isSelected = false
        ) {

        }
        ImageButton(
            defaultImage = R.drawable.noimage_1_1,
            activeImage = R.drawable.noimage_1_1,
            text = "Test",
            iconText = "N",
            isSelected = true
        ) {

        }
        ImageButton(
            defaultImage = R.drawable.noimage_1_1,
            activeImage = R.drawable.noimage_1_1,
            text = "Test2",
            iconText = "N",
            isSelected = true
        ) {

        }

    }

}