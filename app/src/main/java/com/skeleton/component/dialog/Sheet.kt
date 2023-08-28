package com.skeleton.component.dialog

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.RewardInfo
import com.ironraft.pupping.bero.scene.component.item.RewardInfoSizeType
import com.ironraft.pupping.bero.scene.component.item.RewardInfoType
import com.skeleton.theme.*
import com.skeleton.view.button.*
import kotlinx.coroutines.launch


data class SheetBtnData(
    val title:String,
    @DrawableRes var img:Int? = null,
    val index :Int
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Sheet(
    sheetState: ModalBottomSheetState,
    @DrawableRes icon:Int? = null,
    title:String? = null,
    description:String? = null,
    @DrawableRes image:Int? = null,
    point:Int? = null,
    exp:Double? = null,
    buttons: List<SheetBtnData>? = null,
    buttonColor: Color? = null,
    cancel:() -> Unit,
    action:(Int) -> Unit
) {

    AppTheme {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Column(
                    Modifier.padding(DimenMargin.regular.dp).wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.light.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        icon?.let {
                            Image(
                                painterResource(it),
                                contentDescription = "",
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(ColorBrand.primary),
                                modifier = Modifier.size(
                                    DimenIcon.heavyUltra.dp,
                                    DimenIcon.heavyUltra.dp
                                )
                            )
                        }
                        title?.let {
                            Text(
                                it,
                                fontSize = FontSize.medium.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorApp.black
                            )
                        }
                        description?.let {
                            Text(
                                it,
                                fontSize = FontSize.thin.sp,
                                color = ColorApp.gray400
                            )
                        }
                    }
                    image?.let {
                        Image(
                            painterResource(it),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().height(146.dp)
                        )
                    }
                    if (point != null || exp != null ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.welcome_gift_box
                                )
                            )
                            LottieAnimation(
                                composition = composition,
                                modifier = Modifier.size(230.dp, 275.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = DimenMargin.thin.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                point?.let{
                                    RewardInfo(
                                        type = RewardInfoType.Point,
                                        sizeType = RewardInfoSizeType.Big,
                                        value = it,
                                        isActive = true
                                    )
                                }
                                exp?.let{
                                    RewardInfo(
                                        type = RewardInfoType.Exp,
                                        sizeType = RewardInfoSizeType.Big,
                                        value = it.toInt(),
                                        isActive = true
                                    )
                                }
                            }
                        }
                    }
                    buttons?.let { btns ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tiny.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top= DimenMargin.medium.dp)
                        ) {
                            btns.forEach { btn ->
                                Box(
                                    modifier = Modifier.weight(1.0f)
                                ) {
                                    FillButton(
                                        type = FillButtonType.Fill,
                                        icon = btn.img,
                                        text = btn.title,
                                        color = if (btn.index % 2 == 1) buttonColor
                                            ?: ColorBrand.primary else ColorApp.gray200
                                    ) {
                                        action(btn.index)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            sheetShape = MaterialTheme.shapes.large.copy(
                topStart = CornerSize(DimenRadius.medium.dp),
                topEnd = CornerSize(DimenRadius.medium.dp)
            )
            ,
            sheetBackgroundColor = ColorApp.white
        ) {

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SheetComposePreview(){
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded},
        skipHalfExpanded = true
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(ColorApp.white)) {
        Button(
            onClick = {
                coroutineScope.launch {
                    if (modalSheetState.isVisible)
                        modalSheetState.hide()
                    else
                        modalSheetState.show()
                }
            },
        ) {
            Text(text = "Open Sheet")
        }
        Sheet(
            sheetState = modalSheetState,
            title = "SheetRadio",
            description = "description",
            image = R.drawable.onboarding_img_0,
            exp = 100.0,
            point = 99,
            buttons = listOf<SheetBtnData>(
                SheetBtnData(title = "btn0", index = 0),
                SheetBtnData(title = "btn1", index = 1)
            ),
            cancel = {

            }
        ){

        }
    }
}