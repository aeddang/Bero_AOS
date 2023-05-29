package com.skeleton.component.progress

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.ironraft.pupping.bero.R
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.progress.Step

@Composable
fun ProgressInfo(
    modifier:Modifier = Modifier,
    title:String? = null,
    leadingText:String? = null,
    trailingText:String? = null,
    progress:Double = 0.0,
    progressMax:Double = 0.0,
    color:Color = ColorBrand.primary
) {
    AppTheme {
        Column (
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 0.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingText?.let {
                    Text(
                        it,
                        fontSize = FontSize.thin.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBrand.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                title?.let {
                    Text(
                        it,
                        fontSize = FontSize.bold.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBrand.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                trailingText?.let {
                    Text(
                        progress.toInt().toString(),
                        fontSize = FontSize.tiny.sp,
                        color = ColorBrand.primary
                    )
                    Text(
                        "/" + progressMax.toInt().toString() + trailingText,
                        fontSize = FontSize.tiny.sp,
                        color = ColorApp.grey300
                    )
                }
            }

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = ColorBrand.primary,
                backgroundColor = ColorApp.grey50,
                strokeCap = StrokeCap.Round,
                progress = (progress/progressMax).toFloat()
            )

        }
    }
}

@Preview
@Composable
fun ProgressInfoComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProgressInfo(
            title = "test",
            leadingText = "leading",
            trailingText = "trail",
            progress = 0.5,
            progressMax = 1.0
        )

    }

}