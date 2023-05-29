package com.skeleton.view.graph

import android.graphics.PointF
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenBar
import com.skeleton.theme.DimenStroke


@Composable
fun GraphLine(
    modifier:Modifier = Modifier,
    points:List<PointF>,
    selectIdx:Int = -1,
    color:Color = ColorApp.grey400,
    pointColor:Color = ColorApp.white,
    selectColor:Color = ColorBrand.primary,
    stroke:Float = DimenStroke.regular
) {

   Box(
       modifier = modifier,
       contentAlignment = Alignment.TopStart
   ){
       Canvas(
           modifier = Modifier.fillMaxSize().padding(all = (stroke/2).dp),
           onDraw = {
               val offsets = points.map {
                   val tx = size.width * it.x
                   val ty = size.height - (size.height * it.y)
                   Offset(tx, ty)
               }
               val select = if(selectIdx >= 0 && selectIdx < points.count()) points[selectIdx] else null
               drawPoints(
                   points = offsets,
                   pointMode = PointMode.Polygon,
                   color = color,
                   strokeWidth = stroke.dp.toPx()
               )
               drawPoints(
                   points = offsets,
                   pointMode = PointMode.Points,
                   cap = StrokeCap.Round,
                   color = color,
                   strokeWidth = stroke.dp.toPx()*3
               )
               select?.let {
                   val tx = size.width * it.x
                   val ty = size.height - (size.height * it.y)
                   drawPoints(
                       points = listOf(Offset(tx,ty)),
                       pointMode = PointMode.Points,
                       cap = StrokeCap.Round,
                       color = selectColor,
                       strokeWidth = stroke.dp.toPx()*3
                   )
               }
               drawPoints(
                   points = offsets,
                   pointMode = PointMode.Points,
                   cap = StrokeCap.Round,
                   color = pointColor,
                   strokeWidth = stroke.dp.toPx()
               )
           }
       )
   }
}
@Preview
@Composable
fun GraphLineComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GraphLine(
            modifier = Modifier.size(156.dp, 78.dp),
            points = listOf(
                PointF(0.0f, 0.0f),
                PointF(0.2f, 0.6f),
                PointF(0.4f, 0.3f),
                PointF(0.6f, 0.4f),
                PointF(0.8f, 0.8f),
                PointF(1.0f, 0.2f),
            ),
            selectIdx = 3
        )
    }
}