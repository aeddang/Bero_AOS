package com.skeleton.component.dialog

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
    var tip:String? = null,
    @DrawableRes var icon:Int? = null

)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Select(
    sheetState: ModalBottomSheetState,
    buttons: ArrayList<SelectBtnData>? = null,
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
                            SelectButton(
                                type = SelectButtonType.Small,
                                icon = btn.icon,
                                text = btn.title,
                                description = btn.tip
                            ) {
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
        Select(
            sheetState = modalSheetState,
            buttons = arrayListOf<SelectBtnData>(
                SelectBtnData(title = "btn0", index = 0),
                SelectBtnData(title = "btn1", index = 1)
            ),
            cancel = {

            },
            action = {

            }
        )
    }
}