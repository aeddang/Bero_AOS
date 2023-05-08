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
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.ApiType
import com.lib.page.PageApns
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.module.network.ErrorType

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
    var error:ApiError<ApiType>? = null,
    var apns:PageApns? = null,
    var handler: ((Int) -> Unit)? = null
)



@Composable
fun ActivityAlertController(){
    val pagePresenter = koinInject<PageComposePresenter>()
    val viewModel = koinInject<AppSceneObserver>()
    var isShow by remember { mutableStateOf(false) }
    var currentEvent: ActivitAlertEvent? by remember { mutableStateOf(null) }
    var imgButtons:List<AlertBtnData>? by remember { mutableStateOf(null) }
    var buttons: List<AlertBtnData>? by remember { mutableStateOf(null) }
    var buttonColor:Color? by remember { mutableStateOf(null) }
    var title:String? by remember { mutableStateOf(null) }
    var text:String? by remember { mutableStateOf(null) }
    var movePage:PageObject? by remember { mutableStateOf(null) }
    val alert = viewModel.alert.observeAsState()
    alert.value.let { alertEvt ->
        alertEvt?.let {evt ->
            title = null
            text = null
            imgButtons = null
            movePage = null
            buttons = (evt.buttons?.mapIndexed { index, btn -> AlertBtnData(title = btn, index = index) } )
            imgButtons = (evt.imgButtons?.mapIndexed { index, btn -> AlertBtnData(img = btn, index = index) } )
            buttonColor = if (evt.isNegative) ColorApp.black else ColorBrand.primary
            when (evt.type) {
                ActivitAlertType.RecivedApns -> {
                    evt.apns?.let {apns ->
                        title = apns.title ?: stringResource(R.string.alert_apns)
                        text = apns.text
                        movePage = PageProvider.getPageObject(apns.page)
                        if (buttons == null) {
                            buttons = if (movePage == null) {
                                listOf(AlertBtnData(title = stringResource(id = R.string.confirm),index = 1))
                            } else {
                                listOf(
                                    AlertBtnData(title = stringResource(id = R.string.cancel),index = 0),
                                    AlertBtnData(title = stringResource(id = R.string.confirm),index = 1)
                                )
                            }
                        }
                    }
                    
                }
                ActivitAlertType.ApiError -> {
                    title = stringResource(R.string.alert_api)
                    text =
                        if ( evt.error?.errorType != ErrorType.API ) stringResource(R.string.alert_apiErrorServer)
                        else evt.error?.msg ?: stringResource(R.string.alert_apiErrorServer)
                    if (buttons == null) buttons = listOf(AlertBtnData(title = stringResource(id = R.string.confirm),index = 1))
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
                title = title ?: currentEvent?.title,
                text = text ?: currentEvent?.text,
                subText = currentEvent?.subText,
                imgButtons = imgButtons,
                buttons = buttons,
                buttonColor = buttonColor,
            ) {selected ->
                isShow = false
                currentEvent?.let { evt ->
                    evt.handler?.let {
                        it(selected)
                        return@Alert
                    }
                    movePage?.let {page ->
                        if (selected == 1)
                            if ( page.isHome) pagePresenter.changePage(page)
                            else pagePresenter.openPopup(page)
                        return@Alert
                    }
                }



            }
        }

    }
}