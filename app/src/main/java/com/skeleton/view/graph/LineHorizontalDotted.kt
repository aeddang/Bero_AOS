package com.skeleton.view.graph

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenStroke


@Composable
fun LineHorizontalDotted (
    modifier:Modifier = Modifier,
    dash:FloatArray =  floatArrayOf(3f,3f),
    color:Color = ColorApp.grey300,
    stroke:Float = DimenStroke.light
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(stroke.dp),
        onDraw = {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(dash, 0f)
            )
        }
    )
}

@Preview
@Composable
fun LineHorizontalDottedComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LineHorizontalDotted(
            modifier = Modifier.padding(top=20.dp)
        )
    }
}