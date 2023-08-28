package com.ironraft.pupping.bero.scene.component.graph

import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenStroke
import com.skeleton.theme.FontSize
import com.skeleton.view.graph.GraphArc
import java.util.UUID


data class ArcGraphData(
    var value:Float = 0.0f,
    var max:Float = 7.0f,
    var start:String = "Goal",
    var end:String = "days"
)

@Composable
fun ArcGraph(
    modifier:Modifier = Modifier,
    size:Float = 156.0f,
    textSize:Float = 100.0f,
    data:ArcGraphData = ArcGraphData(),
    action: ((Int) -> Void)? = null
) {

    val progress:Float by remember { mutableStateOf( data.value/data.max ) }

    AppTheme {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GraphArc(
                modifier = Modifier.size(size.dp, (size/2).dp),
                progress = progress,
                size = size
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    data.start,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize.tiny.sp,
                    color = ColorApp.gray400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(textSize.dp)
                )
                Spacer(modifier = Modifier.width(
                    (size - textSize -DimenStroke.heavy).dp
                ))
                Row(
                    modifier = Modifier.width(textSize.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 0.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        data.value.toInt().toString(),
                        fontWeight = FontWeight.Medium,
                        fontSize = FontSize.thin.sp,
                        color = ColorBrand.primary
                    )
                    Text(
                        "/" + data.max.toInt().toString() + data.end,
                        fontWeight = FontWeight.Medium,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.black
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun ArcGraphComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ArcGraph(
            modifier = Modifier.fillMaxWidth(),

        )
    }
}