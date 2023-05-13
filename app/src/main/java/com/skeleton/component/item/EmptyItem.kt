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

enum class EmptyItemType {
    MyList;
    @get:DrawableRes
    val image: Int?
        get() = when (this) {
            MyList -> R.drawable.paw
        }
    val height: Float
        get() = when (this) {
            MyList -> 92.0f
        }

    val text: String?
        get() = when (this) {
            MyList -> "It’s empty!"
        }
    val radius: Float
        get() = when (this) {
            MyList -> DimenRadius.light
        }
}

@Composable
fun EmptyItem(
    type:EmptyItemType =EmptyItemType.MyList,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(CornerSize(type.radius.dp)))
                .background(ColorApp.grey50)
                .fillMaxWidth()
                .height(type.height.dp)
            ,
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(DimenMargin.tinyExtra.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                type.image?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(ColorApp.grey200),
                        modifier = Modifier.size(DimenIcon.medium.dp)
                    )
                }
                type.text?.let {
                    Text(
                        text = it,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey300
                    )
                }
            }
        }

    }
}
@Composable
fun EmptyData(
    text:String? = null,
    @DrawableRes image:Int = R.drawable.add_dog,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.tinyExtra.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            text?.let {
                Text(
                    text = it,
                    fontSize = FontSize.thin.sp,
                    color = ColorApp.grey300
                )
            }
            Image(
                painterResource(image),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(128.dp)
            )
        }
    }
}

@Preview
@Composable
fun EmptyItemComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EmptyItem(

        )
        EmptyData(
            text = "testssss"
        )
    }
}