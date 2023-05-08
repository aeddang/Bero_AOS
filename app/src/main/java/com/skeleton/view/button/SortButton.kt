package com.skeleton.view.button

import android.annotation.SuppressLint
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
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*

enum class SortButtonType {
    Fill {
        override fun bgColor(color:Color):Color = color
    },
    Stroke{
        override var strokeWidth: Float = DimenStroke.light
        override fun strokeColor(color:Color):Color = color.copy(0.3f)
        override fun textColor(color:Color):Color = color
    },
    StrokeFill{
        override var strokeWidth: Float = DimenStroke.light
        override fun strokeColor(color:Color):Color = color.copy(0.5f)
        override fun bgColor(color:Color):Color = color.copy(0.15f)
        override fun textColor(color:Color):Color = color
    };
    open var strokeWidth:Float = 0.0f
    open fun strokeColor(color:Color):Color {
        return ColorApp.white
    }
    open fun bgColor(color:Color):Color {
        return ColorApp.white
    }
    open fun textColor(color:Color):Color {
        return ColorApp.white
    }
}
enum class SortButtonSizeType {
    Small {

    },
    Big{
        override var iconSize: Float = DimenIcon.light
        override var textSize: Float = FontSize.light
        override var radius: Float = DimenRadius.medium
        override var marginVertical: Float = DimenMargin.microUltra
        override var marginHorizontal: Float = DimenMargin.regularExtra
        override var spacing: Float = DimenMargin.tinyExtra
    };
    open var iconSize:Float = DimenIcon.thin
    open var textSize:Float = FontSize.thin
    open var radius:Float = DimenRadius.light
    open var marginVertical:Float = DimenMargin.micro
    open var marginHorizontal:Float = DimenMargin.tiny
    open var spacing:Float = DimenMargin.micro
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun SortButton(
    type:SortButtonType = SortButtonType.Fill,
    sizeType:SortButtonSizeType = SortButtonSizeType.Small,
    userProfile:UserProfile? = null,
    petProfile:PetProfile? = null,
    @DrawableRes icon:Int? = null,
    isOriginIcon:Boolean = false,
    text:String = "",
    color:Color = ColorApp.black,
    isSort:Boolean = true,
    isSelected:Boolean = false,
    modifier: Modifier = Modifier,
    action:() -> Unit

) {

    AppTheme {
        Box(
            modifier = modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(sizeType.radius.dp))
                .background(type.bgColor(color))
                .border(
                    width = type.strokeWidth.dp,
                    color = type.strokeColor(color),
                    shape = RoundedCornerShape(sizeType.radius.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(
                    start = if (text.isEmpty()||isOriginIcon) sizeType.marginVertical.dp  else sizeType.marginHorizontal.dp,
                    end = if (text.isEmpty()||isSort) sizeType.marginVertical.dp  else sizeType.marginHorizontal.dp,
                    top = sizeType.marginVertical.dp,
                    bottom = sizeType.marginVertical.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = sizeType.spacing.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                petProfile?.let {
                    ProfileImage(
                        image = it.image.value,
                        imagePath = it.imagePath,
                        size= sizeType.iconSize,
                        emptyImagePath= R.drawable.profile_dog_default
                    )
                }
                userProfile?.let {
                    ProfileImage(
                        image = it.image.value,
                        imagePath = it.imagePath,
                        size= sizeType.iconSize,
                        emptyImagePath= R.drawable.profile_user_default
                    )
                }
                icon?.let {
                    if (isOriginIcon)
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(CircleShape)
                                .background(ColorApp.white)
                                .padding(DimenMargin.micro.dp)

                        ) {
                            Image(
                                painterResource(it),
                                contentDescription = "",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(sizeType.iconSize.dp)
                            )
                        }
                    else
                        Image(
                            painterResource(it),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(type.textColor(color)),
                            modifier = Modifier.size(sizeType.iconSize.dp)
                        )
                }
                if (text.isNotEmpty())
                    Text(
                        text,
                        fontSize = sizeType.textSize.sp,
                        fontWeight = FontWeight.Medium,
                        color = type.textColor(color),
                        textAlign = TextAlign.Start,
                        maxLines = 1
                    )
                    if(isSort)
                        Image(
                            painterResource(if(isSelected) R.drawable.direction_up else R.drawable.direction_down),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(type.textColor(color)),
                            modifier = Modifier.size(sizeType.iconSize.dp)
                        )
            }
            TransparentButton(
                modifier = Modifier.matchParentSize(),
                action = action
            )
        }
    }

}

@Preview
@Composable
fun SortButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortButton(
            type = SortButtonType.Stroke,
            sizeType = SortButtonSizeType.Small,
            text = "Chip",
            color = ColorApp.orange,
            isSelected = true
        ) {

        }
        SortButton(
            type = SortButtonType.Fill,
            sizeType = SortButtonSizeType.Big,
            icon = R.drawable.paw,
            text = "Chip",
            color = ColorApp.orange,
            isOriginIcon = true,
            isSelected = true
        ) {

        }
        SortButton(
            type = SortButtonType.Stroke,
            sizeType = SortButtonSizeType.Small,
            icon = R.drawable.paw,
            text = "",
            color = ColorApp.orange,
            isSort = false,
            isSelected = true
        ) {

        }
        SortButton(
            type = SortButtonType.StrokeFill,
            sizeType = SortButtonSizeType.Small,
            icon = R.drawable.paw,
            text = "Chip",
            color = ColorApp.orange,
            isSelected = false
        ) {

        }
        SortButton(
            type = SortButtonType.Stroke,
            sizeType = SortButtonSizeType.Small,
            icon = R.drawable.search,
            text = "이 지역 보기",
            color = ColorApp.grey300,
            isSort = false,
            isSelected = false
        ) {

        }
    }

}