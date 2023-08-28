package com.skeleton.component.item.profile

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.TransparentButton

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    image:Bitmap? = null,
    imagePath:String? = null,
    isSelected:Boolean = false,
    strokeColor:Color = ColorBrand.primary,
    size:Float = DimenProfile.medium,
    @DrawableRes emptyImagePath:Int = R.drawable.profile_dog_default,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var painter:AsyncImagePainter? = null
    image?.let{ painter = rememberAsyncImagePainter(it) }
    if(painter == null) {
        imagePath?.let {
            painter = rememberAsyncImagePainter( it,
                placeholder = painterResource(emptyImagePath),
                error = painterResource(emptyImagePath)
            )
        }
    }
    AppTheme {
        Box(
            modifier = modifier.wrapContentSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) DimenStroke.medium.dp else 0.0f.dp,
                        color = if (isSelected) strokeColor else ColorTransparent.clear,
                        shape = CircleShape
                    )
                    .size((size + (DimenStroke.heavy * 2)).dp),
                contentAlignment = Alignment.Center

            ) {
                Box(
                    modifier = Modifier
                        .size(size.dp, size.dp)
                        .clip(CircleShape)
                        .background(ColorApp.white)

                ) {
                    if (painter == null) {
                        Image(
                            painterResource(emptyImagePath),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(size.dp, size.dp)
                        )
                    }
                    painter?.let {
                        Image(
                            it,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(size.dp, size.dp)
                        )
                    }
                    onEdit?.let {
                        TransparentButton(
                            action = {
                                it()
                            }
                        )
                    }
                }
            }

            onDelete?.let{
                CircleButton(
                    type = CircleButtonType.Icon,
                    icon = if (image== null && imagePath == null) R.drawable.add_photo else R.drawable.delete,
                    strokeWidth = DimenStroke.regular,
                    isSelected = false
                ) {
                    it()
                }
            }
        }
    }
}

@Composable
fun ProfileImageRect(
    modifier: Modifier = Modifier,
    imagePath:String? = null,
    action: (() -> Unit)? = null
) {
    var painter:AsyncImagePainter? = null
    imagePath?.let {
        painter = rememberAsyncImagePainter( it,
            placeholder = painterResource(R.drawable.noimage_1_1)
        )
    }

    AppTheme {
        Box(
            modifier = modifier.size(DimenButton.medium.dp)
                .clip(RoundedCornerShape(CornerSize(DimenRadius.tiny.dp)))
                .background(ColorApp.gray200)

        ) {
            painter?.let {
                Image(
                    it,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(DimenButton.medium.dp)
                )
            }
            action?.let {
                TransparentButton(
                    modifier = Modifier.matchParentSize(),
                    action =  {
                        it()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileImageComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProfileImage(
        ) {

        }
        ProfileImage(
            imagePath = "test"
        ) {

        }
        ProfileImage(
            imagePath = "test",
            isSelected = true
        ) {

        }
        ProfileImageRect(
            imagePath = "test"
        )

    }

}