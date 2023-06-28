package com.ironraft.pupping.bero.scene.page.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.component.item.UserAlbumListItem
import com.ironraft.pupping.bero.scene.component.viewmodel.UserAlbumListViewModel
import com.ironraft.pupping.bero.scene.page.chat.viewmodel.ChatRoomListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.ExplorerSearchType
import com.lib.page.PageComposePresenter
import com.lib.util.isScrolledToEnd
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatRoomList(
    modifier: Modifier,
    chatRoomListViewModel:ChatRoomListViewModel? = null,
    scrollState: LazyListState = rememberLazyListState(),
    isEdit: Boolean = false,
    marginTop:Float = DimenMargin.regular,
    marginBottom:Float = DimenMargin.medium
) {
    val owner = LocalLifecycleOwner.current
    val pagePresenter: PageComposePresenter = get()
    val repository: PageRepository = get()
    val viewModel: ChatRoomListViewModel by remember { mutableStateOf(
        chatRoomListViewModel ?: ChatRoomListViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE)
    )}

    val isEmpty by viewModel.isEmpty.observeAsState()
    val isLoading = viewModel.isLoading.observeAsState()
    val rooms by viewModel.listDatas.observeAsState()

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        viewModel.resetLoad()
    }
    val refreshState = rememberPullRefreshState(refreshing, ::refresh)
    isLoading.value?.let{
        if (refreshing && !it) refreshing = false
    }
    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) {
        viewModel.continueLoad()
    }

    AppTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isEmpty == true)
                EmptyItem(type = EmptyItemType.Chat)

            else if(rooms != null)
                rooms?.let { datas->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = DimenApp.pageHorinzontal.dp),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        contentPadding = PaddingValues(
                            top = marginTop.dp,
                            bottom = marginBottom.dp
                        )
                    ) {
                        items ( datas ,  key = {it.index}){ data->
                            ChatRoomListItem(
                                data = data,
                                isEdit = isEdit,
                                onRead = {
                                    if (data.isRead.value == false) viewModel.read(data.roomId)
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.ChatRoom)
                                            .addParam(PageParam.data, data)
                                    )},
                                onExit = { viewModel.exit(data.roomId) }
                            )
                        }
                    }
                }
            else 
                Spacer(modifier = Modifier.fillMaxSize())

            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

