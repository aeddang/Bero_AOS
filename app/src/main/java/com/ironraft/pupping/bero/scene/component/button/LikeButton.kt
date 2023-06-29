package com.ironraft.pupping.bero.scene.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.lib.util.toThousandUnit
import com.skeleton.theme.*
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.TransparentButton



@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    likeCount:Double? = null,
    isLike:Boolean = false,
    likeSize: SortButtonSizeType = SortButtonSizeType.Big,
    action:() -> Unit
) {
    AppTheme {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(space = DimenMargin.tinyExtra.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortButton(
                type = SortButtonType.Stroke,
                sizeType = likeSize,
                icon = if(isLike) R.drawable.favorite_on else R.drawable.favorite_on,
                color = if(isLike) ColorBrand.primary else ColorApp.grey400,
                isSort = false
            ){
                action()
            }
            likeCount?.let { count->
                Box(modifier = Modifier.wrapContentSize()){
                    Text(
                        count.toThousandUnit() + " " + stringResource(id = R.string.likes),
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(corner = CornerSize(DimenRadius.regular.dp)))
                            .background(ColorApp.whiteDeepLight)
                            .padding(
                                horizontal = DimenMargin.light.dp,
                                vertical = DimenMargin.tinyExtra.dp
                            )
                    )
                    TransparentButton(modifier = Modifier.matchParentSize()) {
                        action()
                    }
                }
            }
        }
    }
}

