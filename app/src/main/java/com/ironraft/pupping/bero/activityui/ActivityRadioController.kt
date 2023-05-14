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

enum class ActivitRadioType {
    Select, Filter
}

data class ActivitRadioEvent(
    val type: ActivitRadioType,
    var title:String? = null,
    var text:String? = null,
    var selected:Int? = null,
    var buttons:List<String>? = null,
    var radioButtons:List<RadioBtnData>? = null,
    var handler: ((Int) -> Unit)? = null
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityRadioController(){
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded},
        skipHalfExpanded = true
    )
    val viewModel = koinInject<AppSceneObserver>()
    var currentEvent: ActivitRadioEvent? by remember { mutableStateOf(null) }
    var buttons: List<RadioBtnData>? by remember { mutableStateOf(null) }
    var isMultiSelectAble by remember { mutableStateOf(false) }
    val radio = viewModel.radio.observeAsState()
    radio.value.let { radioEvt ->
        radioEvt?.let {evt ->
            isMultiSelectAble = false
            buttons = (evt.buttons?.mapIndexed {
                    index,
                    btn -> RadioBtnData(title = btn, index = index, isSelected = index == evt.selected) } )

            when (evt.type) {
                ActivitRadioType.Select -> {

                }
                ActivitRadioType.Filter -> {
                    isMultiSelectAble = true
                }
            }
            currentEvent = evt
            coroutineScope.launch {
                modalSheetState.show()
            }
            viewModel.radio.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Radio(
            sheetState = modalSheetState,
            title = currentEvent?.title,
            description = currentEvent?.text,
            isMultiSelectAble = isMultiSelectAble,
            buttons = currentEvent?.radioButtons ?: buttons,
            cancel = {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            },
            action = { idx , isSelect ->
                if (isMultiSelectAble == true) {

                } else {
                    currentEvent?.let { evt ->
                        evt.handler?.let {
                            it(idx)
                        }
                    }
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                }
            }
        )
    }
}