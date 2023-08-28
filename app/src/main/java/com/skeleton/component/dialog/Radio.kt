package com.skeleton.component.dialog

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.*
import com.skeleton.view.button.*
import kotlinx.coroutines.launch


data class RadioBtnData(
    @DrawableRes var icon:Int? = null,
    val title:String,
    val index :Int,
    val value:String? = null,
    var isSelected:Boolean = false
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Radio(
    sheetState: ModalBottomSheetState,
    title:String? = null,
    description:String? = null,
    isMultiSelectAble:Boolean = false,
    buttons: List<RadioBtnData>? = null,
    cancel:() -> Unit,
    action:(Int, Boolean) -> Unit
) {
    AppTheme {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Column(
                    Modifier.padding(DimenMargin.regular.dp),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
                ) {
                    Column(
                        Modifier.wrapContentSize(),
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.tinyExtra.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tinyExtra.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.Top
                        ) {
                            title?.let {
                                Text(
                                    it,
                                    fontSize = FontSize.medium.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorApp.black
                                )
                            }
                            Spacer(modifier = Modifier.weight(1.0f))
                            ImageButton(defaultImage = R.drawable.close) {
                                cancel()
                            }
                        }
                        description?.let{
                            Text(
                                it,
                                fontSize = FontSize.thin.sp,
                                color = ColorApp.gray400
                            )
                        }
                    }
                    Column(
                        Modifier.wrapContentSize(),
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp)
                    ) {
                        buttons?.let { btns ->
                            btns.forEach { btn ->
                                var isCheck by remember { mutableStateOf(btn.isSelected) }
                                RadioButton(
                                    type = RadioButtonType.Text,
                                    isChecked = isCheck,
                                    icon = btn.icon,
                                    text = btn.title,
                                    color = ColorApp.black
                                ) {
                                    isCheck = !isCheck
                                    btn.isSelected = isCheck
                                    action(btn.index, isCheck)

                                }
                            }
                        }
                    }
                }
            },
            sheetShape = MaterialTheme.shapes.large.copy(
                topStart = CornerSize(DimenRadius.medium.dp),
                topEnd = CornerSize(DimenRadius.medium.dp)
            ),
            sheetBackgroundColor = ColorApp.white
        ) {

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun RadioComposePreview(){
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
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

        Radio(
            sheetState = modalSheetState,
            title = "RadioRadio",
            description = "description",
            buttons = arrayListOf<RadioBtnData>(
                RadioBtnData(title = "btn0", index = 0, isSelected = true),
                RadioBtnData(title = "btn1", index = 1)
            ),
            cancel = {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            },
            action = { idx , isSelect ->

            }
        )

    }
}