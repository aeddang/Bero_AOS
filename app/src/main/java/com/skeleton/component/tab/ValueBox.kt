package com.skeleton.component.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.component.item.ValueInfo
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.progress.ProgressInfo
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton
import java.util.UUID


data class ValueData(
    val id:String = UUID.randomUUID().toString(),
    val idx:Int,
    var title:String? = null,
    var valueType:ValueInfoType? = null,
    var value:Double = 0.0,
)

@Composable
fun ValueBox(
    modifier:Modifier = Modifier,
    datas:List<ValueData> = listOf(),
    action: ((ValueData) -> Unit)? = null
) {
    AppTheme {
        Row(
            modifier = modifier
                .fillMaxWidth().height(84.dp)
                .clip(RoundedCornerShape(DimenRadius.thin.dp))
                .border(
                    width = DimenStroke.light.dp,
                    color = ColorApp.grey100,
                    shape = RoundedCornerShape(DimenRadius.thin.dp)
                )
                .background(ColorApp.white)
                .padding(horizontal = DimenMargin.light.dp),
            horizontalArrangement = Arrangement.spacedBy(DimenMargin.light.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            datas.forEachIndexed { index, data ->
                Box(
                    modifier = Modifier.weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    if (data.valueType != null) {
                        ValueInfo(
                            type = data.valueType!!,
                            value = data.value,
                        )
                    } else {
                        ProgressInfo(
                            title = data.title,
                            progress = data.value,
                            progressMax = 100.0
                        )
                    }
                    action?.let {
                        TransparentButton(modifier = Modifier.matchParentSize()) {
                            it(data)
                        }
                    }
                }
                if (index < datas.count()-1) {
                    Spacer(modifier = Modifier.fillMaxHeight()
                        .padding(vertical = DimenMargin.tiny.dp)
                        .background(ColorApp.grey100)
                        .width(DimenLine.light.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ValueBoxComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ValueBox(
            datas = listOf(
                ValueData(idx = 0, title = "LV",valueType = null, value = 50.0),
                ValueData(idx = 1, valueType = ValueInfoType.Point, value = 100.0)
            )
        ){

        }

    }

}