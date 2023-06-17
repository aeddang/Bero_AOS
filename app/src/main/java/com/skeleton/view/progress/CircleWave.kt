package com.skeleton.view.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand


@Composable
fun CircleWave(
    modifier:Modifier = Modifier,
    color:Color = ColorBrand.primary
) {

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(1000), RepeatMode.Restart)
    )
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

       Canvas(
           modifier = Modifier.fillMaxSize().scale(scale).alpha(scale),
           onDraw = {
               drawArc(
                   color = color.copy(0.2f),
                   startAngle =0f,
                   sweepAngle = 360f,
                   useCenter = true,
                   topLeft = Offset(50.dp.toPx(), 50.dp.toPx()),
                   size = Size(100.dp.toPx(), 100.dp.toPx())
               )
               drawArc(
                   color = color.copy(0.2f),
                   startAngle =0f,
                   sweepAngle = 360f,
                   useCenter = true,
                   topLeft = Offset(25.dp.toPx(), 25.dp.toPx()),
                   size = Size(150.dp.toPx(), 150.dp.toPx())
               )
               drawArc(
                   color = color.copy(0.2f),
                   startAngle =0f,
                   sweepAngle = 360f,
                   useCenter = true,
                   size = Size(200.dp.toPx(), 200.dp.toPx())
               )
           }
       )

    }
}
@Preview
@Composable
fun GraphArcComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircleWave(
            modifier = Modifier.size(180.dp)
        )
    }
}