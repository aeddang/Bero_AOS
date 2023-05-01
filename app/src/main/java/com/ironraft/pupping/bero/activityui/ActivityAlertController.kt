package com.ironraft.pupping.bero.activityui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.store.api.ApiError
import com.skeleton.component.dialog.Alert
import com.skeleton.component.dialog.AlertBtnData
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject
import com.ironraft.pupping.bero.R
enum class ActivitAlertType {
    Confirm, Select, Alert,
    RecivedApns, ApiError,
    Cancel
}

data class ActivitAlertEvent(
    val type: ActivitAlertType,
    var title:String? = null,
    var text:String? = null,
    var subText:String? = null,
    var buttons:ArrayList<String>? = null,
    @DrawableRes var imgButtons:ArrayList<Int>? = null,
    var isNegative:Boolean = true,
    var error:ApiError<Any>? = null,
    var handler: ((Int) -> Unit)? = null
)



@Composable
fun ActivityAlertController(){
    val viewModel = koinInject<AppSceneObserver>()
    var isShow by remember { mutableStateOf(false) }
    var currentEvent: ActivitAlertEvent? by remember { mutableStateOf(null) }
    var imgButtons:List<AlertBtnData>? by remember { mutableStateOf(null) }
    var buttons: List<AlertBtnData>? by remember { mutableStateOf(null) }
    var buttonColor:Color? by remember { mutableStateOf(null) }

    val alert = viewModel.alert.observeAsState()
    alert.value.let {
        it?.let {evt ->
            imgButtons = null
            buttons = (evt.buttons?.mapIndexed { index, btn -> AlertBtnData(title = btn, index = index) } )
            imgButtons = (evt.imgButtons?.mapIndexed { index, btn -> AlertBtnData(img = btn, index = index) } )
            buttonColor = if (evt.isNegative) ColorApp.black else ColorBrand.primary
            when (evt.type) {
                ActivitAlertType.RecivedApns -> {
                }
                ActivitAlertType.ApiError -> {

                }
                ActivitAlertType.Select -> {

                }
                ActivitAlertType.Alert -> {
                    if (buttons == null) buttons = listOf(AlertBtnData(title = stringResource(id = R.string.confirm),index = 1))
                }
                ActivitAlertType.Confirm -> {
                    if (buttons == null) buttons = listOf(
                        AlertBtnData(title = stringResource(id = R.string.cancel),index = 0),
                        AlertBtnData(title = stringResource(id = R.string.confirm),index = 1)
                    )
                }
                ActivitAlertType.Cancel -> {
                    isShow = false
                    return
                }
            }
            currentEvent = evt
            isShow = true
            viewModel.alert.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = isShow) {
            Alert(
                title = currentEvent?.title,
                text = currentEvent?.text,
                subText = currentEvent?.subText,
                //tipText = "tipText",
                //referenceText = "referenceText",
                imgButtons = imgButtons,
                buttons = buttons,
                buttonColor = ColorBrand.primary,
            ) {selected ->
                isShow = false
                currentEvent?.handler?.let {
                    it(selected)
                }
            }
        }

    }
}