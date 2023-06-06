package com.skeleton.component.dialog

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.ImageButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Check(
    @DrawableRes icon: Int? = null,
    isShow: Boolean = false,
    text: String,
    isAuto: Boolean,
    duration:Long = 2500,
    action:(() -> Unit)
) {

    val coroutineScope = rememberCoroutineScope()
    var isChecked by remember { mutableStateOf(false) }

    val offset: Dp by animateDpAsState(
        if (isShow) 0.dp else 100.dp,
        tween()
    )
    val opacity: Float by animateFloatAsState(
        if (isShow) 1.0f else 0f,
        tween()
    )

    if (isAuto && isShow) {
        coroutineScope.launch {
            delay(duration)
            action()
        }
    }

    AppTheme {
        Box(
            modifier = Modifier
                .offset(y = offset)
                .alpha(opacity)
            ,
            contentAlignment = Alignment.Center
        ){
            Column(
                Modifier
                    .wrapContentHeight()
                    .width(208.dp)
                    .clip(RoundedCornerShape(DimenRadius.tiny.dp))
                    .background(ColorApp.white)
                    .border(
                        width = DimenStroke.light.dp,
                        color = ColorBrand.primary.copy(0.3f),
                        shape = RoundedCornerShape(DimenRadius.tiny.dp)
                    )
                    .padding(vertical = DimenMargin.regular.dp, horizontal = DimenMargin.light.dp),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                if (isAuto) {
                    Image(
                        painterResource(
                            icon ?: R.drawable.check_circle
                        ),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(ColorBrand.primary),
                        modifier = Modifier.size(DimenIcon.heavy.dp)
                    )
                } else {
                    ImageButton(
                        defaultImage = icon ?: R.drawable.check_circle,
                        size = DimenIcon.heavy,
                        defaultColor = ColorApp.grey400,
                        activeColor = ColorBrand.primary,
                        isSelected = isChecked
                    ) {
                        isChecked = true
                        coroutineScope.launch {
                            delay(200)
                            action()
                        }
                    }
                }
                Text(
                    text,
                    fontSize = FontSize.thin.sp,
                    color = ColorBrand.primary,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Preview
@Composable
fun CheckComposePreview(){

    var isShow by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier.fillMaxSize().background(ColorApp.white),
        contentAlignment = Alignment.Center

    ) {
        Button(
            onClick = {
                isShow = true
            },
        ) {
            Text(text = "Open Check")
        }
        Check(
            isShow = isShow,
            text = "texttexttexttext texttexttexttext texttext",
            isAuto = true

        ) {
            isShow = false
        }
    }

}