package com.skeleton.component.tab

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*

enum class TitleSectionType{
    Small {
        override var titleSize: Float = FontSize.medium
        override var titleFamily:FontWeight = FontWeight.Medium
    },
    Normal {
        override var titleFamily:FontWeight = FontWeight.SemiBold
    },
    Strong {
        override var titleFamily:FontWeight = FontWeight.Bold
    };
    open var titleSize:Float = FontSize.bold
    open var titleFamily:FontWeight = FontWeight.Normal

}

@Composable
fun TitleSection(
    type:TitleSectionType = TitleSectionType.Normal,
    @DrawableRes icon:Int? = null,
    header:String? = null,
    title:String? = null,
    trailer:String? = null,
    color:Color = ColorApp.black,
    action: (() -> Unit)
) {
    AppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Image(
                    painterResource(it),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier.size(DimenIcon.heavyExtra.dp)
                )
            }
            Column (
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                header?.let {
                    Text(
                        it,
                        fontSize = FontSize.thin.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorApp.grey400,
                        textAlign = TextAlign.Start
                    )
                }
                title?.let {
                    Text(
                        it,
                        fontSize = type.titleSize.sp,
                        fontWeight = type.titleFamily,
                        color = color,
                        lineHeight = 0.sp,
                        textAlign = TextAlign.Start
                    )
                }
                trailer?.let {
                    Text(
                        it,
                        fontSize = FontSize.thin.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorApp.grey400,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TitleSectionComposePreview(){
    Column (
        modifier = Modifier.background(ColorApp.white).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TitleSection(
            type = TitleSectionType.Strong,
            icon = R.drawable.chart,
            header = "bero's",
            title = "Strong",
            trailer = "bero"
        ){

        }
        TitleSection(
            type = TitleSectionType.Small,
            icon = R.drawable.chart,
            header = "bero's",
            title = "Small",
        ){

        }
        TitleSection(
            type = TitleSectionType.Normal,
            header = "bero's",
            title = "Normal",
        ){

        }
        TitleSection(
            title = "Normal",
        ){

        }
    }

}