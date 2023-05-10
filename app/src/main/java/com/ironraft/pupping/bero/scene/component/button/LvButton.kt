package com.ironraft.pupping.bero.scene.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.lib.page.PageComposePresenter
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.TextButton
import com.skeleton.view.button.WrapTransparentButton
import org.koin.compose.koinInject

enum class LvButtonType {
    Big{
        override var size:Float = DimenProfile.heavyExtra
        override var textSize:Float = FontSize.bold
        override var textTop:Float = 32.0f
    },
    Small {
        override var size:Float = DimenIcon.mediumUltra
        override var textSize:Float = FontSize.tiny
        override var textTop:Float = 12.0f
    },
    Tiny{
        override var size:Float = DimenIcon.thin
        override var textSize:Float = FontSize.microExtra
        override var textTop:Float = 8.0f
    };
    @DrawableRes open var icon:Int? = null
    open var size:Float = 0.0f
    open var textSize:Float = 0.0f
    open var textTop:Float = 0.0f
}


@Composable
fun LvButton(
    lv:Lv = Lv.Green,
    type:LvButtonType = LvButtonType.Small,
    text:String? = null,
    defaultColor:Color = ColorApp.grey100,
    isSelected: Boolean = true,
    index: Int = -1,
    modifier: Modifier = Modifier,
    action:(Int) -> Unit
) {
    AppTheme {
        WrapTransparentButton(
            action = {
                action(index)
            }
        ){
            Box (
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painterResource(lv.icon),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(type.size.dp),
                    colorFilter = if(isSelected) null else ColorFilter.tint(defaultColor)
                )
                text?.let {
                    Text(
                        it,
                        fontWeight = FontWeight.Bold,
                        fontSize = type.textSize.sp,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        color = ColorApp.white,
                        modifier = Modifier.padding(top = type.textTop.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LvButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp).background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LvButton(
            type = LvButtonType.Small,
            text = "99"
        ) {

        }
        LvButton(
            lv = Lv.Orange,
            type = LvButtonType.Tiny,
            text = "99"
        ) {

        }
        LvButton(
            type = LvButtonType.Big,
            text = "LV.99"
        ) {

        }
    }

}