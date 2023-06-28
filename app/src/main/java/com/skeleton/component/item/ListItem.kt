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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.util.toThousandUnit
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.TransparentButton


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    imagePath:String? = null,
    @DrawableRes emptyImage:Int = R.drawable.noimage_1_1,
    imgSize:Size = Size(100.0f, 100.0f),
    title:String? = null,
    subTitle:String? = null,
    @DrawableRes icon:Int? = null,
    iconText:String? = null,
    iconColor:Color = ColorApp.black,
    iconSize:SortButtonSizeType = SortButtonSizeType.Big,
    likeCount:Double? = null,
    isLike:Boolean = false,
    likeSize:SortButtonSizeType = SortButtonSizeType.Big,
    pets:List<PetProfile> = listOf(),
    iconAction: (() -> Unit)? = null,
    action: (() -> Unit)? = null,
    move:(() -> Unit)? = null

    ) {
    var painter: AsyncImagePainter? = null
    imagePath?.let {
        painter = rememberAsyncImagePainter( it,
            placeholder = painterResource(emptyImage)
        )
    }
    AppTheme {

        Column(
            modifier = modifier.width(imgSize.width.dp),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = imgSize.width.dp, height = imgSize.height.dp)
                    .clip(RoundedCornerShape(CornerSize(DimenRadius.light.dp)))
                    .background(ColorApp.grey100),
                contentAlignment = Alignment.Center
            ) {
                if (painter == null) {
                    Image(
                        painterResource(emptyImage),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
                painter?.let {
                    Image(
                        it,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
                move?.let {
                    TransparentButton { it() }
                }
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(all = DimenMargin.tiny.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    icon?.let {
                        SortButton(
                            type = SortButtonType.Stroke,
                            sizeType = iconSize,
                            icon = icon,
                            text = iconText ?: "",
                            color = iconColor,
                            isSort = false
                        ){
                            iconAction?.let { it() }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    likeCount?.let {likeCount ->
                        Row(
                            modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(space = 0.dp, alignment = Alignment.End)
                        ) {
                            SortButton(
                                type = SortButtonType.Stroke,
                                sizeType = likeSize,
                                icon = if(isLike) R.drawable.favorite_on else R.drawable.favorite_on,
                                text = likeCount.toThousandUnit(),
                                color = if(isLike) ColorBrand.primary else ColorApp.grey400,
                                isSort = false
                            ){
                                action?.let { it() }
                            }
                        }
                    }
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 0.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.microExtra.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    title?.let {
                        Text(
                            it,
                            fontSize = FontSize.light.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorApp.black,
                            textAlign = TextAlign.Start
                        )
                    }
                    subTitle?.let {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey300,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                if(pets.isNotEmpty())
                    Box(
                        modifier = modifier.wrapContentSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        pets.reversed().forEachIndexed {index, pet ->
                            ProfileImage(
                                image = pet.image.value,
                                imagePath = pet.imagePath.value,
                                size = DimenProfile.thin,
                                emptyImagePath = R.drawable.profile_dog_default,
                                modifier = Modifier.padding(end = (DimenMargin.thin * index).dp )
                            )
                        }
                    }

            }
        }
    }
}

@Preview
@Composable
fun ListItemComposePreview() {
    Column(
        modifier = Modifier
            .background(ColorApp.white)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ListItem(
            imgSize = Size(width = 240.0f, height = 120.0f),
            title = "title",
            subTitle = "subTitle",
            icon = null,
            iconText = "Walk",
            likeCount = 100.0,
            isLike = true,
            pets = listOf(
                PetProfile().init(PetData()),
                PetProfile().init(PetData()),
                PetProfile().init(PetData()),
                PetProfile().init(PetData()),
                PetProfile().init(PetData())
            )
        )
    }
}