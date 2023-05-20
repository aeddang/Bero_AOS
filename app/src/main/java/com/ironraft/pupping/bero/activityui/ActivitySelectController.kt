package com.ironraft.pupping.bero.activityui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ironraft.pupping.bero.AppSceneObserver
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
import com.skeleton.component.dialog.*
import com.skeleton.module.network.ErrorType
import kotlinx.coroutines.launch

enum class ActivitSelectType {
    Select, ImgPicker, Cancel
}

data class ActivitSelectEvent(
    val type: ActivitSelectType,
    var selected:Int? = null,
    var buttons:ArrayList<String>? = null,
    var selectButtons:List<SelectBtnData>? = null,
    var handler: ((Int) -> Unit)? = null
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivitySelectController(modalSheetState: ModalBottomSheetState){
    val coroutineScope = rememberCoroutineScope()
    val viewModel = koinInject<AppSceneObserver>()
    var currentEvent: ActivitSelectEvent? by remember { mutableStateOf(null) }
    var buttons: List<SelectBtnData>? by remember { mutableStateOf(null) }

    val select = viewModel.select.observeAsState()
    select.value.let { selectEvt ->
        selectEvt?.let {evt ->
            buttons = (evt.buttons?.mapIndexed {
                    index,
                    btn -> SelectBtnData(title = btn, index = index, isSelected = index == evt.selected) } )

            when (evt.type) {
                ActivitSelectType.ImgPicker -> {
                    if (buttons == null) buttons = listOf(
                        SelectBtnData(title = stringResource(id = R.string.button_selectAlbum),index = 0, icon = R.drawable.album),
                        SelectBtnData(title = stringResource(id = R.string.button_takeCamera),index = 1, icon = R.drawable.add_photo),
                        SelectBtnData(title = stringResource(id = R.string.cancel),index = 2, isSelected = true)
                    )
                }
                ActivitSelectType.Select -> {

                }
                ActivitSelectType.Cancel -> {
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                    viewModel.select.value = null
                    return@let
                }
            }
            currentEvent = evt
            coroutineScope.launch {
                modalSheetState.show()
            }
            viewModel.select.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Select(
            sheetState = modalSheetState,
            buttons = currentEvent?.selectButtons ?: buttons,
            cancel = {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            },
            action = { selected ->
                currentEvent?.let { evt ->
                    evt.handler?.let {
                        it(selected)
                    }
                }
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        )
    }
}