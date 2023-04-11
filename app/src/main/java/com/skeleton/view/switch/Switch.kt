package com.skeleton.view.switch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton

@Composable
fun Switch(
    isOn:Boolean = false,
    thumbColor:Color = ColorApp.white,
    activeColor:Color = ColorApp.green,
    defaultColor:Color = ColorApp.grey200,
    action:(Boolean) -> Unit
) {
    AppTheme {
        Box(
            modifier = Modifier
                .size(56.dp, 30.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(if (isOn) activeColor else defaultColor)
                .padding(2.dp)
            ,
            contentAlignment = if (isOn) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
            TransparentButton(
                action = {
                    action(!isOn)
                }
            )
        }

    }
}

@Preview
@Composable
fun SwitchComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp).background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Switch(
            isOn = true
        ) {

        }
        Switch(
            isOn = false
        ) {

        }
    }

}