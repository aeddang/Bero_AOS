package com.ironraft.pupping.bero.scene.page.history

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
import com.ironraft.pupping.bero.scene.component.list.AlbumList
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.component.list.WalkList
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.rememberForeverLazyListState
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get


@Composable
fun PageWalkList(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.WalkList, repository).initSetup(owner)) }

    val currentPage = viewModel.currentPage.observeAsState()
    var scrollStateKey: String by remember { mutableStateOf( "") }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var userId: String? by remember { mutableStateOf( null ) }

    currentPage.value?.let { page->
        if(!viewModel.isInit){
            viewModel.isInit = true
            val user = page.getParamValue(PageParam.id) as? String
            scrollStateKey = page.key
            userId = user
        }
    }


    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_walkHistory),
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        userId?.let {
            val scrollState: LazyListState = rememberForeverLazyListState(key = scrollStateKey)
            WalkList(
                modifier = Modifier.weight(1.0f),
                scrollState = scrollState,
                userId = it,
                listSize = screenWidth.toFloat()
            )
        }

    }
}

@Preview
@Composable
fun PageWalkListPreview(){
    PageWalkList(
    )
}
