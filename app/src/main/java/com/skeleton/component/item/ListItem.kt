package com.skeleton.component.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
import com.skeleton.theme.*
import com.skeleton.view.button.SortButtonSizeType


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    imagePath:String? = null,
    @DrawableRes emptyImage:Int = R.drawable.noimage_1_1,
    imgSize:Size? = Size(100.0f, 100.0f),
    title:String? = null,
    subTitle:String? = null,
    icon:String? = null,
    iconText:String? = null,
    iconColor:Color = ColorApp.black,
    iconSize:SortButtonSizeType = SortButtonSizeType.Big
    likeCount:Double? = null,
    isLike:Bool = false
    likeSize:SortButtonSizeType = SortButtonSizeType.Big
    pets:List<PetProfile> = listOf(),
    iconAction: (() -> Unit)? = null,
    action: (() -> Unit)? = null,
    move:(() -> Unit)? = null,

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

        }
    }
}

@Preview
@Composable
fun ListItemComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ListItem(

        )
    }
}