package com.skeleton.view.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton

@Composable
fun Step(
    index:Int,
    total:Int,
) {

    AppTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = DimenMargin.micro.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Step $index",
                fontSize = FontSize.thin.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorBrand.primary
            )
            Text(
                "of $total",
                fontSize = FontSize.thin.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorApp.grey300
            )
        }

    }
}

@Preview
@Composable
fun StepComposePreview(){
    Column (
        modifier = Modifier
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var isOn by remember { mutableStateOf(true) }

        Step(
            1,
            2
        )

    }
}

