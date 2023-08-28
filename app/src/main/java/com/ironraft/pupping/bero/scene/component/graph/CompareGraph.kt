package com.ironraft.pupping.bero.scene.component.graph

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.*

data class CompareGraphData(
    var value:Float = 0.0f,
    var max:Float = 7.0f,
    var color:Color = ColorBrand.primary,
    var title:String = "You",
    var end:String = "days"
)
@Composable
fun CompareGraph(
    modifier:Modifier = Modifier,
    textSize:Float = 52.0f,
    datas:List<CompareGraphData> = listOf()
) {
    AppTheme {
        Column (
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
            horizontalAlignment = Alignment.Start
        ) {
            datas.forEach {data->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 0.dp)
                ) {
                    Text(
                        data.title,
                        fontSize = FontSize.tiny.sp,
                        fontWeight = FontWeight.Bold,
                        color = data.color,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.width(textSize.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1.0f)
                            .height(DimenBar.regular.dp)
                            .clip(RoundedCornerShape(DimenRadius.micro.dp))
                        ,
                        contentAlignment = Alignment.CenterEnd
                    )
                    {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxSize(),
                            color = ColorBrand.primary,
                            backgroundColor = ColorApp.gray200,
                            strokeCap = StrokeCap.Square,
                            progress = data.value/data.max)
                        Row(
                            modifier = modifier.padding(end = DimenMargin.micro.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 0.dp)
                        ) {
                            Text(
                                String.format("%.2f",data.value),
                                fontSize = FontSize.micro.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorApp.gray500
                            )
                            Text(
                                "/" + String.format("%.0f",data.max) + data.end,
                                fontSize = FontSize.micro.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorApp.gray300
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CompareGraphComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CompareGraph(
            datas = listOf(
                CompareGraphData(),
                CompareGraphData()
            )
        )

    }

}