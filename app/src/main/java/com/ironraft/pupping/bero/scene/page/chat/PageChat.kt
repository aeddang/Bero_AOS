package com.ironraft.pupping.bero.scene.page.chat

import android.graphics.pdf.PdfRenderer.Page
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.list.UserAlbumList
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.UserAlbumListViewModel
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomList
import com.ironraft.pupping.bero.scene.page.chat.viewmodel.ChatRoomListViewModel

import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.ExplorerSearchType
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.rememberForeverLazyListState
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch

@Composable
fun PageChat(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Chat.value
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Album, repository).initSetup(owner)) }
    val chatRoomListViewModel: ChatRoomListViewModel by remember { mutableStateOf(
        ChatRoomListViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE)
    )}
    var isEdit: Boolean by remember { mutableStateOf( false ) }
    val scrollState: LazyListState = rememberForeverLazyListState(key = appTag)
    val currentPage = viewModel.currentPage.observeAsState()
    val goBackPage = viewModel.goBack.observeAsState()
    currentPage.value?.let { page->
        if(!viewModel.isInit){
            viewModel.isInit = true
            chatRoomListViewModel.resetLoad()
        }
    }

    fun onEdit(){
        isEdit = true
        viewModel.currentPage.value?.isGoBackAble = false

    }
    fun onEdited(){
        isEdit = false
        viewModel.currentPage.value?.isGoBackAble = true
    }
    goBackPage.value?.let {
        viewModel.goBackCompleted()
        if (isEdit) onEdited()
    }

    fun onNewChat(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Friend)
                .addParam(PageParam.type, FriendListType.Chat)
        )
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenApp.bottom.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title =
                if (isEdit) stringResource(id = R.string.button_manageChat)
                else stringResource(id = R.string.pageTitle_chat)
            ,
            useBack = isEdit,
            buttons =
                if (isEdit) arrayListOf()
                else arrayListOf(TitleTabButtonType.AddChat, TitleTabButtonType.Setting)
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    if (isEdit) onEdited()
                    else pagePresenter.goBack()
                }
                TitleTabButtonType.AddChat -> onNewChat()
                TitleTabButtonType.Setting -> onEdit()
                else -> {}
            }
        }
        ChatRoomList(
            modifier = Modifier.weight(1.0f),
            chatRoomListViewModel = chatRoomListViewModel,
            scrollState = scrollState,
            isEdit = isEdit
        )

    }
}
@Preview
@Composable
fun PageChatPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageChat(
        )
    }
}
