package com.ironraft.pupping.bero.scene.page.my

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.page.*
import com.lib.util.ComponentLog
import com.lib.util.rememberForeverScrollState
import com.lib.util.toggle
import com.skeleton.sns.SnsEvent
import com.skeleton.sns.SnsManager
import com.skeleton.sns.SnsType
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.RadioButton
import com.skeleton.view.button.RadioButtonType
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import dev.burnoo.cokoin.get


@Composable
fun PageMyAccount(
    modifier: Modifier = Modifier
){
    val appTag = PageID.MyAccount.value
    val appSceneObserver:AppSceneObserver = get()
    val walkManager:WalkManager = get()
    val pageRepository:PageRepository = get()
    val dataProvider:DataProvider = get()
    val pagePresenter:PageComposePresenter = get()
    val snsManager: SnsManager = get()


    fun onDelete(){
        val ctx = pagePresenter.activity
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = ctx.getString(R.string.alert_deleteConfirm),
            text = ctx.getString(R.string.alert_deleteAccountConfirmText),
            buttons = arrayListOf(
                ctx.getString(R.string.cancel),
                ctx.getString(R.string.alert_deleteConfirm)
            ),
            isNegative = true
        ){ select ->
            if (select == 1) {
                val q = ApiQ(appTag, ApiType.DeleteUser,
                    requestData = dataProvider.user.userId , isLock = true)
                dataProvider.requestData(q)
            }
        }
    }

    fun onSignout(){
        val ctx = pagePresenter.activity
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = ctx.getString(R.string.alert_signOutConfirm),
            text = ctx.getString(R.string.alert_signOutConfirmText),
            buttons = arrayListOf(
                ctx.getString(R.string.cancel),
                ctx.getString(R.string.button_logOut)
            ),
            isNegative = true
        ){ select ->
            if (select == 1) {
                walkManager.endWalk()
                pageRepository.clearLogin()
            }
        }
    }

    val snsUser = snsManager.user.observeAsState()
    val snsError = snsManager.error.observeAsState()
    val errorMsg = stringResource(R.string.alert_snsLoginError)
    snsError.value?.let{
        it ?: return@let
        when (it.event){
            SnsEvent.Login->
                appSceneObserver.alert.value = ActivitAlertEvent(
                    type = ActivitAlertType.Alert,
                    title = errorMsg,
                    isNegative = true
                )
            else -> {}
        }
        snsManager.error.value = null
    }
    snsUser.value?.let {
        onDelete()
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_myAccount),
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .padding(
                    vertical = DimenMargin.medium.dp,
                    horizontal = DimenApp.pageHorinzontal.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
        ) {
            SelectButton(
                type = SelectButtonType.Medium,
                text = stringResource(id = R.string.button_logOut),
                useStroke = false,
                useMargin = false
            ){
                onSignout()
            }
            SelectButton(
                type = SelectButtonType.Medium,
                text = stringResource(id = R.string.button_deleteAccount),
                useStroke = false,
                useMargin = false
            ){
                dataProvider.user.snsUser?.snsType?.let {
                    snsManager.requestLogin(it)
                    return@SelectButton
                }
                onDelete()
            }
        }
    }
}

@Preview
@Composable
fun PageMyAccountPreview(){
    PageMyAccount(
    )
}
