package com.ironraft.pupping.bero.scene.page.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.profile.component.edit.UserProfileEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.UserProfilePictureEdit
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get


@Composable
fun PageModifyUser(
    modifier: Modifier = Modifier
){

    val appTag = PageID.ModifyUser.value
    val dataProvider:DataProvider = get()
    val pagePresenter:PagePresenter = get()
    val scrollState: ScrollState = rememberForeverScrollState(key = appTag)
    val user by remember { mutableStateOf( dataProvider.user) }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            modifier = Modifier
                .padding(top = DimenApp.pageHorinzontal.dp)
                .padding(horizontal = DimenApp.pageHorinzontal.dp),
            type = TitleTabType.Section,
            title = stringResource(id = R.string.pageTitle_myProfile),
            alignment = TextAlign.Center,
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> pagePresenter.goBack()
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
            verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp),
        ) {
            UserProfilePictureEdit(profile = user.currentProfile, user = user.snsUser)
            UserProfileEdit(profile = user.currentProfile, user = user.snsUser)
        }

    }
}

@Preview
@Composable
fun PageModifyUserPreview(){
    PageModifyUser(
    )
}
