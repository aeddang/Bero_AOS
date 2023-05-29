package com.skeleton.view.graph

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenBar


@Composable
fun GraphArc(
    modifier:Modifier = Modifier,
    size:Float = 100.0f,
    progress: Float, // or some value binded
    progressColor:Color = ColorBrand.primary,
    bgColor:Color = ColorApp.grey50,
    stroke:Float = DimenBar.regular,
    start:Float = 180.0f,
    end:Float = 180.0f
) {
    val arcSize by remember { mutableStateOf(
        Size(size-stroke/2, size-stroke/2)
    )}

   Box(
       modifier = modifier,
       contentAlignment = Alignment.TopStart
   ){
       Canvas(
           modifier = Modifier.fillMaxSize().padding(all = (stroke/2).dp),
           onDraw = {
               drawArc(
                   color = bgColor,
                   startAngle = start,
                   sweepAngle = end,
                   useCenter = false,
                   style = Stroke(
                       width = stroke.dp.toPx()
                   ),
                   size = Size(arcSize.width.dp.toPx(), arcSize.height.dp.toPx())
               )
               drawArc(
                   color = progressColor,
                   startAngle = start,
                   sweepAngle = end*progress,
                   useCenter = false,
                   style = Stroke(
                       width = stroke.dp.toPx()
                   ),
                   size = Size(arcSize.width.dp.toPx(), arcSize.height.dp.toPx())
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
        GraphArc(
            modifier = Modifier.size(156.dp, 78.dp),
            size = 156.0f,
            progress = 0.3f
        )
    }
}