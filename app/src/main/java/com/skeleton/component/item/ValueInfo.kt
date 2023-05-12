package com.skeleton.component.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.skeleton.theme.*

enum class ValueInfoType {
    Point, Coin, Heart, Walk, Mission, WalkComplete, WalkDistance, Lv,
    MissionComplete, Exp , ExpEarned , PointEarned;

    @get:DrawableRes
    val icon: Int
        get() = when (this) {
            Point,  PointEarned -> R.drawable.point
            Exp,  ExpEarned -> R.drawable.exp
            Coin -> R.drawable.coin
            Heart -> R.drawable.favorite_on
            WalkComplete -> R.drawable.paw
            WalkDistance -> R.drawable.walk
            MissionComplete -> R.drawable.goal
            Lv -> R.drawable.lv_green
            else -> R.drawable.puppy
        }
    val iconColor: Color?
        get() = when (this) {
            Lv,  Exp, ExpEarned -> null
            Coin, Point, PointEarned, Heart -> null
            WalkDistance -> ColorApp.black
            else -> ColorBrand.primary
        }

    val text:String
        get() = when (this) {
            Lv -> "Level"
            Exp -> "EXP"
            Point -> "Points"
            ExpEarned -> "EXP earned"
            PointEarned -> "Points earned"
            Coin -> "Coins"
            Heart -> "Heart Level"
            Walk -> "from walk"
            Mission -> "from mission"
            WalkComplete -> "Walks done"
            WalkDistance -> "Walk distance"
            MissionComplete -> "Missions completed"
        }

    fun getValue(value:Double) : String{
        return when (this) {
            Coin -> value.toString()
            WalkDistance -> WalkManager.viewDistance(value)
            else -> value.toInt().toString()
        }
    }
    fun getTextColor(lv:com.ironraft.pupping.bero.store.provider.model.Lv?) : Color?{
        return when (this) {
            Lv -> lv?.color
            else -> iconColor
        }
    }

    val isIconFirst:Boolean
        get() = when (this) {
            Heart, Lv -> true
            else -> false
        }

}

@Composable
fun ValueInfo(
    type:ValueInfoType = ValueInfoType.Point,
    value:Double,
    lv:Lv? = null,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Column(
            modifier = Modifier.padding(DimenMargin.micro.dp),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.micro.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(type.isIconFirst){
                    if(type.iconColor != null)
                        Image(
                            painterResource(type.icon),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if(value.equals(0.0)) ColorApp.grey300 else type.iconColor!!),
                            modifier = Modifier.size(DimenIcon.regular.dp)
                        )
                    else
                        when(type){
                            ValueInfoType.Heart ->
                                Image(
                                    painterResource(type.icon),
                                    contentDescription = "",
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(Lv.getLv(value.toInt()).color),
                                    modifier = Modifier.size(DimenIcon.regular.dp)
                                )
                            else ->
                                Image(
                                    painterResource(type.icon),
                                    contentDescription = "",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(DimenIcon.regular.dp)
                                )
                        }
                }
                Text(
                    text = type.getValue(value),
                    fontSize = FontSize.medium.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(value.equals(0.0)) ColorApp.grey300 else type.getTextColor(lv) ?: ColorBrand.primary
                )
                if(!type.isIconFirst){
                    if(type.iconColor != null)
                        Image(
                            painterResource(type.icon),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if(value.equals(0.0)) ColorApp.grey300 else type.iconColor!!),
                            modifier = Modifier.size(DimenIcon.regular.dp)
                        )
                    else
                        Image(
                            painterResource(type.icon),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(DimenIcon.regular.dp)
                        )
                }
            }
            Text(
                text = type.text,
                fontSize = FontSize.tiny.sp,
                color = ColorApp.grey300
            )
        }
    }
}

@Preview
@Composable
fun ValueInfoComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ValueInfo(
            type = ValueInfoType.Heart,
            value = 100.0
        )
        ValueInfo(
            type = ValueInfoType.Point,
            value = 100.0
        )
        ValueInfo(
            type = ValueInfoType.Coin,
            value = 100.0
        )

    }
}