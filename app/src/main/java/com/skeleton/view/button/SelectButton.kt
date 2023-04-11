package com.skeleton.view.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

enum class SelectButtonType {
    Tiny {
        override var height: Float = DimenButton.regular
        override var radius: Float = DimenRadius.thin
    },
    Small{
        override var height: Float = DimenButton.medium
        override var radius: Float = DimenRadius.thin
    },
    Medium{
    };
    open var height:Float = DimenButton.heavy
    open var radius:Float = 0.0f
}


@Composable
fun SelectButton(
    type:SelectButtonType = SelectButtonType.Small,
    @DrawableRes icon:Int? = null,
    title:String? = null,
    text:String = "",
    description:String? = null,
    bgColor:Color = ColorApp.white,
    index:Int = 0,
    isMore:Boolean = true,
    useStroke:Boolean = true,
    useMargin:Boolean = true,
    isSelected:Boolean = false,
    action:(Int) -> Unit

) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(type.height.dp)
                .clip(RoundedCornerShape(type.radius.dp))
                .background(bgColor)
                .border(
                    width = if(useStroke) DimenStroke.light.dp else 0.0f.dp,
                    color = if(isSelected) ColorBrand.primary  else ColorApp.grey200,
                    shape = RoundedCornerShape(type.radius.dp)
                )
            ,
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = if(useMargin) DimenMargin.light.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.light.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if(type == SelectButtonType.Medium && useStroke) {
                            Box(
                                modifier = Modifier
                                    .size(DimenCircle.regular.dp, DimenCircle.regular.dp)
                                    .border(
                                        width = DimenStroke.light.dp,
                                        color = ColorApp.grey200,
                                        shape = CircleShape
                                    )
                            )
                        }
                        Image(
                            painterResource(it),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if (isSelected) ColorBrand.primary else ColorApp.black),
                            modifier = Modifier.size(DimenIcon.regular.dp, DimenIcon.regular.dp)
                        )
                    }
                }
                Column (
                    modifier = Modifier.padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    title?.let {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorApp.grey400,
                            textAlign = TextAlign.Start
                        )
                    }
                    Text(
                        text,
                        fontSize = FontSize.light.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) ColorBrand.primary else ColorApp.black,
                        textAlign = TextAlign.Start
                    )
                    description?.let {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorApp.grey400,
                            textAlign = TextAlign.Start
                        )
                    }
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
fun SelectButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectButton(
            type = SelectButtonType.Tiny,
            icon = R.drawable.noimage_1_1,
            title = "title",
            text = "Select BUTTON",
            isSelected = true
        ) {

        }
        SelectButton(
            type = SelectButtonType.Small,
            icon = R.drawable.noimage_1_1,
            title = "title",
            text = "Select BUTTON",
            isSelected = true
        ) {

        }
        SelectButton(
            type = SelectButtonType.Medium,
            icon = R.drawable.noimage_1_1,
            text = "Select BUTTON",
            description = "description",
        ) {

        }
        SelectButton(
            type = SelectButtonType.Medium,
            text = "Select BUTTON",
            description = "description",
            useStroke = false
        ) {

        }
    }

}