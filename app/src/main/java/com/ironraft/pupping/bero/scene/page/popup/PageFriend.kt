package com.ironraft.pupping.bero.scene.page.popup

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
import dev.burnoo.cokoin.get


@Composable
fun PageFriend(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Friend, repository).initSetup(owner)) }
    val friendListViewModel: FriendListViewModel by remember { mutableStateOf(
        FriendListViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE )
    )}
    val currentPage = viewModel.currentPage.observeAsState()
    val goBackPage = viewModel.goBack.observeAsState()
    var scrollStateKey: String by remember { mutableStateOf( "") }

    val hasRequested by friendListViewModel.hasRequested.observeAsState()
    var user: User? by remember { mutableStateOf( null ) }
    var isEdit: Boolean by remember { mutableStateOf( false ) }

    var originSortType:FriendListType = FriendListType.Friend
    var sortType:FriendListType = FriendListType.Friend
    var title:String? by remember { mutableStateOf( null ) }

    currentPage.value?.let { page->
        if(!friendListViewModel.isInit){
            friendListViewModel.isInit = true
            val sortData =  page.getParamValue(PageParam.type) as? FriendListType ?: FriendListType.Friend
            val userData = page.getParamValue(PageParam.data) as? User
            friendListViewModel.lazySetup(userData?.userId, sortType)
            if( friendListViewModel.isMe ){
                isEdit = page.getParamValue(PageParam.isEdit) as? Boolean ?: false
            } else {
                userData?.representativeName?.let {
                    title = pagePresenter.activity.getString(R.string.pageTitle_friends).replace(it)
                }
            }
            scrollStateKey = page.key
            originSortType = sortData
            sortType = sortData
            user = userData
        }
    }
    fun onSort(type:FriendListType){
        friendListViewModel.resetLoad(type)
        sortType = type
        viewModel.currentPage.value?.isGoBackAble = type == originSortType
    }

    goBackPage.value?.let {
        viewModel.goBackCompleted()
        onSort(originSortType)
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = title ?: sortType.text,
            useBack = true,
            buttons =  if(user?.isMe == true) sortType.buttons else listOf(),
            icons =  if(sortType == FriendListType.Friend) {
                if( hasRequested == true ) listOf("N") else listOf(null)
            } else {
                listOf()
            }

        ){
            when(it){
                TitleTabButtonType.Back ->
                    if (sortType != originSortType) onSort(originSortType)
                    else pagePresenter.goBack()
                TitleTabButtonType.AddFriend -> onSort(FriendListType.Requested)
                TitleTabButtonType.Friend -> onSort(FriendListType.Friend)
                else -> {}
            }
        }
        user?.let {
            val scrollState: LazyListState = rememberForeverLazyListState(key = scrollStateKey)
            FriendList(
                friendListViewModel = friendListViewModel,
                modifier = Modifier
                    .weight(1.0f)
                    .padding(horizontal = DimenApp.pageHorinzontal.dp),
                scrollState = scrollState,
                type = sortType,
                user = it,
                isEdit = isEdit
            )
        }

    }
}

@Preview
@Composable
fun PageFriendPreview(){
    PageFriend(
    )
}
