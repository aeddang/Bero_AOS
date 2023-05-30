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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*

enum class EmptyItemType {
    MyList, Chat;
    @get:DrawableRes
    val image: Int?
        get() = when (this) {
            MyList -> R.drawable.paw
            Chat -> R.drawable.add_dog
        }

    val imageMode: ColorFilter?
        get() = when (this) {
            MyList -> ColorFilter.tint(ColorApp.grey200)
            Chat -> null
        }
    val imageHeight: Float
        get() = when (this) {
            MyList -> DimenIcon.medium
            Chat -> 104f
        }
    val spacing: Float
        get() = when (this) {
            MyList -> DimenMargin.tinyExtra
            Chat -> DimenMargin.medium
        }

    val text: String?
        get() = when (this) {
            MyList -> "It’s empty!"
            Chat -> "Looks like you haven't started any conversations yet! Add friends to your list and start new chats."
        }
    val bgColor: Color
        get() = when (this) {
            MyList -> ColorApp.grey50
            Chat -> ColorTransparent.clear
        }

    val radius: Float
        get() = when (this) {
            MyList -> DimenRadius.light
            Chat -> 0f
        }
}

@Composable
fun EmptyItem(
    type:EmptyItemType = EmptyItemType.MyList,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(CornerSize(type.radius.dp)))
                .fillMaxWidth()
                .background(type.bgColor)
                .padding(all = DimenMargin.regular.dp)
            ,
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(type.spacing.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                type.image?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = type.imageMode,
                        modifier = Modifier.fillMaxWidth().height(type.imageHeight.dp)
                    )
                }
                type.text?.let {
                    Text(
                        text = it,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey300,
                        textAlign = TextAlign.Center
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
            type = EmptyItemType.Chat
        )
        EmptyData(

        )
    }
}