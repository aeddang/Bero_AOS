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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skeleton.theme.*
import com.skeleton.view.button.*
import kotlinx.coroutines.launch


data class SelectBtnData(
    val title:String,
    val index :Int,
    var isSelected:Boolean = false,
    var tip:String? = null,
    @DrawableRes var icon:Int? = null

)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Select(
    sheetState: ModalBottomSheetState,
    buttons: List<SelectBtnData>? = null,
    cancel:() -> Unit,
    action:(Int) -> Unit
) {

    AppTheme {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                Column(
                    Modifier.padding(DimenMargin.regular.dp),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp)
                ) {
                    buttons?.let { btns ->
                        btns.forEach { btn ->
                            var isSelect = btn.isSelected
                            SelectButton(
                                type = SelectButtonType.Small,
                                icon = btn.icon,
                                text = btn.title,
                                description = btn.tip,
                                isSelected = btn.isSelected
                            ) {
                                isSelect = !isSelect
                                btn.isSelected = isSelect
                                action(btn.index)
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
fun SelectComposePreview(){
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
            Text(text = "Open Select")
        }
        Select(
            sheetState = modalSheetState,
            buttons = listOf<SelectBtnData>(
                SelectBtnData(title = "btn0", index = 0, isSelected = false),
                SelectBtnData(title = "btn1", index = 1, isSelected = true)
            ),
            cancel = {

            },
            action = {

            }
        )
    }
}