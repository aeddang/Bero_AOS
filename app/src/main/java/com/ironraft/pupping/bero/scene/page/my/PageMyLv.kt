package com.ironraft.pupping.bero.scene.page.my

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.list.RewardHistoryList
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.component.LvSection
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.*
import com.skeleton.component.item.HistoryType
import com.skeleton.component.tab.TitleSection
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get


@Composable
fun PageMyLv(
    modifier: Modifier = Modifier
){
    val appTag = PageID.MyLv.value
    val dataProvider:DataProvider= get()
    val pagePresenter:PageComposePresenter = get()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_myLv),
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        LvSection(
            user = dataProvider.user,
            modifier = Modifier
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                .padding(top = DimenMargin.regularExtra.dp)
        )
        Spacer(modifier = Modifier
            .padding(top = DimenMargin.medium.dp)
            .fillMaxWidth()
            .height(DimenLine.heavy.dp)
            .background(ColorApp.grey50)
        )
        TitleSection(
            title = stringResource(id = R.string.earningHistory),
            trailer = stringResource(id = R.string.myLvText1),
            modifier = Modifier
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                .padding(top = DimenMargin.regularExtra.dp)
        )
        Spacer(modifier = Modifier
            .padding(top = DimenMargin.regularExtra.dp)
            .fillMaxWidth()
            .height(DimenLine.light.dp)
            .background(ColorApp.grey50)
        )
        RewardHistoryList(
            type = HistoryType.Exp
        )
    }
}

@Preview
@Composable
fun PageMyLvPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageMyLv(
        )
    }
}
