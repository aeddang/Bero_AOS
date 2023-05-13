package com.skeleton.component.tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.component.item.ValueInfo
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.progress.ProgressInfo
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton
import java.util.UUID



@Composable
fun ChangeBox(
    prev:String? = null,
    next:String? = null,
    color:Color = ColorApp.white,
    activeColor:Color = ColorBrand.primary
) {
    AppTheme {
        Row(
            modifier = Modifier
                .wrapContentWidth().height(DimenButton.medium.dp)
                .clip(RoundedCornerShape(DimenRadius.mediumUltra.dp))
                .border(
                    width = DimenStroke.light.dp,
                    color = color,
                    shape = RoundedCornerShape(DimenRadius.mediumUltra.dp)
                )
                .padding(
                    horizontal = DimenMargin.medium.dp,
                    vertical = DimenMargin.light.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            prev?.let {
                Text(
                    it,
                    fontSize = FontSize.medium.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Image(
                painterResource(R.drawable.arrow_right),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier.size(DimenIcon.light.dp, DimenIcon.light.dp)
            )
            next?.let {
                Text(
                    it,
                    fontSize = FontSize.bold.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
            }
        }
    }
}

@Preview
@Composable
fun ChangeBoxComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChangeBox(
            prev = "Lv.1",
            next = "Lv.2"
        )

    }

}