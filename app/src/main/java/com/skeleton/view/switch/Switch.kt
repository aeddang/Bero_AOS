package com.skeleton.view.switch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton

@Composable
fun Switch(
    isOn:Boolean = false,
    thumbColor:Color = ColorApp.white,
    activeColor:Color = ColorApp.green600,
    defaultColor:Color = ColorApp.gray200,
    modifier: Modifier = Modifier,
    action:(Boolean) -> Unit
) {
    val offset: Dp by animateDpAsState(
        if (isOn) 26.dp else 0.dp,
        tween()
    )
    AppTheme {
        Box(
            modifier = modifier
                .size(56.dp, 30.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(if (isOn) activeColor else defaultColor)
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .absoluteOffset(x = offset)
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
        modifier = Modifier
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var isOn by remember { mutableStateOf(true) }

        Switch(
            isOn = isOn
        ) {
            isOn = it
        }

        Switch(
            isOn = !isOn
        ) {
            isOn = it
        }
    }
}

