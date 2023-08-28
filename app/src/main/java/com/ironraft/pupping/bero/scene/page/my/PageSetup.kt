package com.ironraft.pupping.bero.scene.page.my

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.BuildConfig
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.lib.util.toggle
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.RadioButton
import com.skeleton.view.button.RadioButtonType
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import dev.burnoo.cokoin.get


@Composable
fun PageSetup(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Setup.value
    val pageRepository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val scrollState: ScrollState = rememberForeverScrollState(key = appTag)

    var isTestMode:Boolean by remember { mutableStateOf( SystemEnvironment.isTestMode ) }
    var isReceivePush:Boolean by remember { mutableStateOf( pageRepository.storage.isReceivePush ) }
    var isExpose:Boolean  by remember { mutableStateOf( pageRepository.storage.isExpose) }


    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            parentScrollState = scrollState,
            title = stringResource(id = R.string.pageTitle_setup),
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
                .verticalScroll(scrollState)
                .padding(
                    vertical = DimenMargin.medium.dp,
                    horizontal = DimenApp.pageHorinzontal.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
        ) {
            RadioButton(
                type = RadioButtonType.SwitchOn,
                isChecked = isReceivePush,
                icon = R.drawable.notice,
                text = stringResource(id = R.string.setupNotification),
                color = ColorApp.black
            ){
                isReceivePush = it
                pageRepository.setupPush(it)
            }
            RadioButton(
                type = RadioButtonType.SwitchOn,
                isChecked = isExpose,
                icon = R.drawable.place,
                text = stringResource(id = R.string.setupExpose),
                color = ColorApp.black
            ){
                isExpose = it
                pageRepository.setupExpose(it)
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.gray200)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                icon = R.drawable.account,
                text = stringResource(id = R.string.pageTitle_myAccount),
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.MyAccount)
                )
            }
            SelectButton(
                type = SelectButtonType.Medium,
                icon = R.drawable.block,
                text = stringResource(id = R.string.pageTitle_blockUser),
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.BlockUser)
                )
            }
            SelectButton(
                type = SelectButtonType.Medium,
                icon = R.drawable.block,
                text = stringResource(id = R.string.pageTitle_service),
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.ServiceTerms)
                )
            }
            SelectButton(
                type = SelectButtonType.Medium,
                icon = R.drawable.block,
                text = stringResource(id = R.string.pageTitle_privacy),
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.Privacy)
                )
            }
        }
        Text(
            "v${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
            fontSize = FontSize.thin.sp,
            color = ColorApp.gray400,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(DimenMargin.thin.dp)
        )
    }
}

@Preview
@Composable
fun PageSetupPreview(){
    PageSetup(
    )
}
