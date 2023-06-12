package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.util.replace
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.Koin


@Composable
fun PlaceInfo(
    modifier: Modifier = Modifier,
    sortIconPath:String? = null,
    sortTitle:String? = null,
    title:String? = null,
    description:String? = null,
    distance:Double? = null
) {


    AppTheme {
        Column(
            modifier = modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp)
            ) {
                if(sortTitle?.isEmpty() == false || sortIconPath?.isEmpty() == false){
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DimenMargin.tiny.dp,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        sortIconPath?.let {
                            val painter = rememberAsyncImagePainter(
                                it,
                                placeholder = painterResource(R.drawable.noimage_1_1)
                            )
                            Image(
                                painter,
                                contentDescription = "",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(DimenIcon.light.dp)
                            )
                        }
                        sortTitle?.let {
                            Text(
                                it,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = FontSize.light.sp,
                                color = ColorBrand.primary
                            )
                        }
                    }
                }
                title?.let{
                    Text(
                        it,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontSize.medium.sp,
                        color = ColorApp.black
                    )
                }
                description?.let{
                    Text(
                        it,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey400
                    )
                }
                distance?.let {
                    Row(
                        modifier = Modifier.padding(top = DimenMargin.tiny.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DimenMargin.tinyExtra.dp,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Image(
                            painterResource(R.drawable.walk),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(ColorApp.grey300),
                            modifier = Modifier.size(DimenIcon.thin.dp)
                        )
                        Text(
                            WalkManager.viewDistance(it),
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey300
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PlaceInfoComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier.background(ColorApp.white).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            PlaceInfo(

            )
        }
    }
}