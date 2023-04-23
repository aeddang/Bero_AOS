package com.skeleton.component.dialog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.ImageButton


data class AlertBtnData(
    val title:String,
    @DrawableRes var img:Int? = null,
    val index :Int
)

@Composable
fun Alert(
    title: String? = null,
    text: String? = null,
    subText: String? = null,
    tipText: String? = null,
    referenceText: String? = null,
    imgButtons:ArrayList<AlertBtnData>? = null,
    buttons: ArrayList<AlertBtnData>? = null,
    buttonColor:Color? = null,
    action:(Int) -> Unit
) {
    AppTheme {
        AlertDialog(
            onDismissRequest = {action(-1)},
            title = {
                title?.let {
                    Text(
                        it,
                        fontSize = FontSize.regular.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorApp.black
                    )
                }
            },
            text = {
                Column (
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    text?.let {
                        Text(
                            it,
                            fontSize = FontSize.light.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorApp.black
                        )
                    }
                    subText?.let {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorApp.grey50,
                            modifier = Modifier.padding(top = DimenMargin.tiny.dp)
                        )
                    }
                    tipText?.let {
                        Text(
                            it,
                            fontSize = FontSize.tiny.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorBrand.primary,
                            modifier = Modifier.padding(top = DimenMargin.regular.dp)
                        )
                    }
                    referenceText?.let {
                        Text(
                            it,
                            fontSize = FontSize.tiny.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorApp.grey50,
                            modifier = Modifier.padding(top = DimenMargin.tiny.dp)
                        )
                    }
                }
            },
            buttons = {
                Column (
                    modifier = Modifier.padding(
                        start = DimenMargin.regular.dp,
                        end = DimenMargin.regular.dp,
                        bottom = DimenMargin.regular.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imgButtons?.let { btns ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tiny.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            btns.forEach { btn ->
                                ImageButton(
                                    defaultImage = btn.img ?: R.drawable.tag,
                                    text = btn.title,
                                    isSelected = true
                                ) {
                                    action(btn.index)
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
                            verticalAlignment = Alignment.CenterVertically
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
                                            ?: ColorBrand.primary else ColorApp.grey200
                                    ) {
                                        action(btn.index)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(DimenMargin.regular.dp),
            shape = RoundedCornerShape(DimenRadius.lightExtra.dp),
            backgroundColor = ColorApp.white
        )
    }
}

@Preview
@Composable
fun AlertComposePreview(){
    Box(modifier = Modifier.fillMaxSize().background(ColorApp.white)) {
        Alert(
            title = "Alert",
            text = "texttexttexttext texttexttexttext texttext",
            subText = "subText",
            tipText = "tipText",
            referenceText = "referenceText",
            imgButtons = arrayListOf<AlertBtnData>(
                AlertBtnData(title = "btn0", index = 0),
                AlertBtnData(title = "btn1", index = 1)
            ),
            buttons = arrayListOf<AlertBtnData>(
                AlertBtnData(title = "btn0", index = 0),
                AlertBtnData(title = "btn1", index = 1)
            ),
            buttonColor = ColorBrand.primary,
        ) {

        }
    }
}