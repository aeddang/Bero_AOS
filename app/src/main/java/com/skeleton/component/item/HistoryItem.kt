package com.skeleton.component.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.RewardValueType
import com.skeleton.theme.*

enum class HistoryType {
    Point, Exp, Mission, Walk;

    @get:DrawableRes
    val icon: Int
        get() = when (this) {
            Point -> R.drawable.point
            Exp -> R.drawable.exp
            Walk -> R.drawable.paw
            Mission -> R.drawable.goal
        }

    val apiType: RewardValueType
        get() = when (this) {
            Point -> RewardValueType.Point
            else -> RewardValueType.Exp
        }
    val colorFilter:ColorFilter?
        get() = when (this) {
            Mission, Walk -> ColorFilter.tint(ColorBrand.primary)
            else -> null
        }
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    type:HistoryType = HistoryType.Exp,
    title:String? = null,
    date:String? = null,
    value:Int = 0
) {
    AppTheme {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = DimenMargin.tinyExtra.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                title?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontSize.light.sp,
                        color = ColorApp.black
                    )
                }
                date?.let {
                    Text(
                        text = it,
                        fontSize = FontSize.tiny.sp,
                        color = ColorApp.grey400
                    )
                }
            }
            Text(
                text = "+${value.toString()}",
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.medium.sp,
                color = ColorBrand.primary
            )
            Image(
                painterResource(type.icon),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = type.colorFilter,
                modifier = Modifier.size(DimenIcon.light.dp)
            )
        }
    }
}

@Preview
@Composable
fun HistoryItemPreview() {
    Column(
        modifier = Modifier.background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HistoryItem(
            type = HistoryType.Exp
        )
        HistoryItem(
            type = HistoryType.Walk,
            title = "title",
            date = "yyyy.mm.dd"
        )
    }
}