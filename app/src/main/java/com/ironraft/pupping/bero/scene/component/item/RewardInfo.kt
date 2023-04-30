package com.ironraft.pupping.bero.scene.component.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
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
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType

enum class RewardInfoType {
    Point {
        @DrawableRes override var icon: Int? = R.drawable.point
        override var bgcolor:Color = ColorApp.yellowSub
    },
    Exp{
        @DrawableRes override var icon: Int? = R.drawable.exp
        override var iconFilter:ColorFilter? = ColorFilter.tint(ColorBrand.primary)
        override var bgcolor:Color = ColorApp.orangeSub
    };

    @DrawableRes open var icon:Int? = null
    open var iconFilter:ColorFilter? = null
    open var color:Color = ColorBrand.primary
    open var bgcolor:Color = ColorBrand.bg

}
enum class RewardInfoSizeType {
    Small {
        override var boxSize:Size = Size(75.0f, DimenButton.light)
    },
    Big{
        override var textSize:Float = FontSize.bold
        override var iconSize:Float = DimenIcon.medium
        override var boxSize:Size = Size(124.0f, DimenButton.regular)
    };

    open var textSize:Float = FontSize.thin
    open var iconSize:Float = DimenIcon.thin
    open var boxSize:Size = Size(DimenButton.regular, DimenButton.regular)
    open var strokeSize:Float = 0.0f

}

@Composable
fun RewardInfo(
    type:RewardInfoType = RewardInfoType.Point,
    sizeType:RewardInfoSizeType = RewardInfoSizeType.Small,
    value:Int,
    isActive:Boolean = false,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Box(
            modifier = modifier
                .size(sizeType.boxSize.width.dp, sizeType.boxSize.height.dp)
                .clip(RoundedCornerShape(DimenRadius.regular.dp))
                .background(if (isActive) type.bgcolor else ColorApp.grey100)
                .border(
                    width = sizeType.strokeSize.dp,
                    color = ColorBrand.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(DimenRadius.regular.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+$value",
                    fontSize = sizeType.textSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) type.color else ColorApp.grey400,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top= 4.dp)
                )
                type.icon?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = type.iconFilter,
                        modifier = Modifier.size(sizeType.iconSize.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardInfoComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RewardInfo(
            type = RewardInfoType.Point,
            sizeType = RewardInfoSizeType.Small,
            value = 100
        )
        RewardInfo(
            type = RewardInfoType.Point,
            sizeType = RewardInfoSizeType.Big,
            value = 100,
            isActive = true
        )
        RewardInfo(
            type = RewardInfoType.Exp,
            sizeType = RewardInfoSizeType.Big,
            value = 100,
            isActive = true
        )
    }
}