package com.ironraft.pupping.bero.scene.page.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.list.AlbumList
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.component.list.BlockUserList
import com.ironraft.pupping.bero.scene.component.list.FriendList
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.rememberForeverLazyListState
import com.lib.util.replace
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get


@Composable
fun PageBlockUser(
    modifier: Modifier = Modifier
){

    val pagePresenter:PageComposePresenter = get()
    val scrollState: LazyListState = rememberForeverLazyListState(key = PageID.BlockUser.value)
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_blockUser),
            useBack = true

        ){
            when(it){
                TitleTabButtonType.Back ->  pagePresenter.goBack()
                else -> {}
            }
        }
        BlockUserList(
            modifier = Modifier
                .weight(1.0f)
                .padding(horizontal = DimenApp.pageHorinzontal.dp),
            scrollState = scrollState
        )

    }
}

@Preview
@Composable
fun PageBlockUserPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageBlockUser(
        )
    }
}
