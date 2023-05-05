package com.ironraft.pupping.bero.activityui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ironraft.pupping.bero.AppSceneObserver
import com.skeleton.component.dialog.Alert
import com.skeleton.component.dialog.AlertBtnData
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.ApiType
import com.lib.page.PageApns
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.component.dialog.Sheet
import com.skeleton.component.dialog.SheetBtnData
import com.skeleton.module.network.ErrorType
import kotlinx.coroutines.launch

enum class ActivitSheetType {
    Confirm, Select, Alert
}

data class ActivitSheetEvent(
    val type: ActivitSheetType,
    var title:String? = null,
    var text:String? = null,
    @DrawableRes var icon:Int? = null,
    @DrawableRes var image:Int? = null,
    var point:Int? = null,
    var exp:Double? = null,
    var buttons:ArrayList<String>? = null,
    var isNegative:Boolean? = null,
    var handler: ((Int) -> Unit)? = null
)



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivitySheetController(){
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded},
        skipHalfExpanded = true
    )
    val viewModel = koinInject<AppSceneObserver>()
    var isLock by remember { mutableStateOf(false) }
    var currentEvent: ActivitSheetEvent? by remember { mutableStateOf(null) }
    var buttons: List<SheetBtnData>? by remember { mutableStateOf(null) }
    var buttonColor:Color? by remember { mutableStateOf(null) }

    val sheet = viewModel.sheet.observeAsState()
    sheet.value.let { sheetEvt ->
        sheetEvt?.let {evt ->
            isLock  = false
            buttons = (evt.buttons?.mapIndexed { index, btn -> SheetBtnData(title = btn, index = index) } )
            if (evt.isNegative == null) {
                buttonColor = if (evt.handler == null) ColorApp.black else ColorBrand.primary
            }
            evt.isNegative?.let {
                buttonColor = if (it) ColorApp.black else ColorBrand.primary
            }
            when (evt.type) {
                ActivitSheetType.Select -> {
                    isLock = false
                }
                ActivitSheetType.Alert -> {
                    isLock = true
                    if (buttons == null) buttons = listOf(SheetBtnData(title = stringResource(id = R.string.confirm),index = 1))
                }
                ActivitSheetType.Confirm -> {
                    isLock = true
                    if (buttons == null) buttons = listOf(
                        SheetBtnData(title = stringResource(id = R.string.cancel),index = 0),
                        SheetBtnData(title = stringResource(id = R.string.confirm),index = 1)
                    )
                }
            }
            currentEvent = evt
            coroutineScope.launch {
                modalSheetState.show()
            }
            viewModel.sheet.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Sheet(
            sheetState = modalSheetState,
            title = currentEvent?.title ,
            description = currentEvent?.text,
            image = currentEvent?.image,
            icon = currentEvent?.icon,
            exp = currentEvent?.exp,
            point = currentEvent?.point,
            buttons = buttons,
            buttonColor = buttonColor,
            cancel = {
                if (isLock) return@Sheet
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        ){ selected ->
            currentEvent?.let { evt ->
                evt.handler?.let {
                    it(selected)
                }
            }
            coroutineScope.launch {
                modalSheetState.hide()
            }
        }
    }
}