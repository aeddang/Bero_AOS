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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.TextButton
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject

enum class AgreeButtonType {
    Privacy {
        override var text:String = "Privacy usage agreement"
        override var page:PageID? = PageID.Privacy
    },
    Service{
        override var text:String = "Terms of service agreement"
        override var page:PageID? = PageID.ServiceTerms
    },
    Neutralized{
        @DrawableRes override var icon:Int? = R.drawable.neutralized
        override var text:String = "Neutralized/Spayed"
    };

    @DrawableRes open var icon:Int? = null
    open var text:String = ""
    open var page:PageID? = null

}


@Composable
fun AgreeButton(
    type:AgreeButtonType = AgreeButtonType.Privacy,
    isChecked: Boolean,
    text:String? = null,
    modifier: Modifier = Modifier,
    action:(Boolean) -> Unit
) {
    val pagePresenter:PageComposePresenter = get()
    AppTheme {
        WrapTransparentButton(
            action = {
                action(!isChecked)
            }
        ){
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1.0f),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.tiny.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    type.icon?.let {
                        Image(
                            painterResource(it),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(DimenIcon.light.dp),
                            alpha = if (isChecked) 1.0f else 0.4f
                        )
                    }
                    Text(
                        text ?: type.text,
                        modifier = Modifier.weight(1.0f, false),
                        fontSize = FontSize.regular.sp,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        color = if (isChecked) ColorApp.black else ColorApp.grey400,
                        textAlign = TextAlign.Start,

                    )
                    type.page?.let { page ->
                        TextButton(
                            modifier = Modifier.padding(bottom = 3.dp),
                            defaultText = stringResource(R.string.button_terms),
                            isUnderLine = true
                        ) {
                            pagePresenter.openPopup(PageProvider.getPageObject(page))
                        }
                    }
                }
                Image(
                    painterResource(R.drawable.check_circle),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(DimenIcon.light.dp),
                    colorFilter = ColorFilter.tint(if(isChecked) ColorBrand.primary else ColorApp.grey400)
                )
            }

        }
    }
}

@Preview
@Composable
fun AgreeButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp).background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AgreeButton(
            type = AgreeButtonType.Privacy,
            isChecked = true
        ) {

        }
        AgreeButton(
            type = AgreeButtonType.Service,
            isChecked = true
        ) {

        }
        AgreeButton(
            type = AgreeButtonType.Neutralized,
            isChecked = true
        ) {

        }
    }

}