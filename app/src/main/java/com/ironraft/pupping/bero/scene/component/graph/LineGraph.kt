package com.ironraft.pupping.bero.scene.component.graph

import android.graphics.PointF
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenBar
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenStroke
import com.skeleton.theme.FontSize
import com.skeleton.view.graph.GraphLine
import com.skeleton.view.graph.LineHorizontalDotted


data class LineGraphData(
    var values:List<Float> = listOf(0.2f, 0.0f, 1.0f, 0.4f),
    var lines:List<String> = listOf("1/1", "1/2", "1/3", "1/4"),
    var raws:List<String> = listOf("0","10", "20", "30", "40", ""),
    var primaryRaws:List<String> = listOf("20", "30"),
    var rawsUnit:String = "(minutes)"
)

@Composable
fun LineGraph(
    modifier:Modifier = Modifier,
    size:Float = 156.0f,
    textSize:Float = 100.0f,
    selectIdx:Int = 1,
    data:LineGraphData = LineGraphData(),
    rawsWidth:Float = 30.0f,
    primaryColor:Color = ColorBrand.primary,
) {

    AppTheme {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier,
                contentAlignment = Alignment.TopStart
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        data.raws.reversed().forEach {
                            Text(
                                it,
                                fontSize = FontSize.tiny.sp,
                                color = ColorApp.gray300,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(rawsWidth.dp, DimenBar.medium.dp)
                                    .padding(top = DimenMargin.thin.dp)
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        data.raws.reversed().forEachIndexed{ idx, raw ->
                            if (idx == 0){
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(DimenBar.medium.dp)
                                    .background(ColorTransparent.clear),
                                    contentAlignment = Alignment.BottomCenter
                                ){
                                    LineHorizontalDotted(
                                        modifier = Modifier.padding(top = DimenStroke.light.dp)
                                    )
                                }
                            } else{
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(DimenBar.medium.dp)
                                    .background(
                                        if (data.primaryRaws.find { it == raw } == null) ColorTransparent.clear
                                        else primaryColor.copy(alpha = 0.1f)
                                    ),
                                    contentAlignment = Alignment.BottomCenter
                                ){
                                    Spacer(modifier = Modifier
                                        .padding(top = DimenMargin.medium.dp)
                                        .fillMaxWidth()
                                        .height(DimenStroke.light.dp)
                                        .background(ColorApp.gray300)
                                    )
                                }
                            }

                        }
                    }
                }
                GraphLine(
                    points = data.values.mapIndexed { idx, p ->
                        val max = data.values.count()-1
                        if (max <= 0) PointF(0f, p)
                        else {
                            val tx = idx.toFloat() / max.toFloat()
                            PointF(tx, p)
                        }
                    },
                    selectIdx = selectIdx,
                    modifier = Modifier
                        .padding(horizontal = rawsWidth.dp)
                        .padding(start = rawsWidth.dp)
                        .padding(top = DimenBar.medium.dp)
                        .fillMaxWidth()
                        .height( (DimenBar.medium*(data.raws.count()-1)).dp )

                )
                Text(
                    data.rawsUnit,
                    fontSize = FontSize.tiny.sp,
                    color = ColorApp.gray200,
                    textAlign = TextAlign.Center
                )
            }
            if(data.lines.count() <= 10) {
                Row(
                    modifier = Modifier.padding(start = rawsWidth.dp).height(DimenBar.medium.dp),
                    horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.lines.forEachIndexed { index, s ->
                        Text(
                            s,
                            fontSize = FontSize.tiny.sp,
                            color = if(index == selectIdx) primaryColor else ColorApp.gray300,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun LineGraphComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LineGraph(
            modifier = Modifier.fillMaxWidth(),

        )
    }
}