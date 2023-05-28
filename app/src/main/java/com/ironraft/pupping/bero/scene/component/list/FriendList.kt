package com.ironraft.pupping.bero.scene.component.list

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.isScrolledToEnd
import com.lib.util.showCustomToast
import com.lib.util.toggle
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


enum class FriendListType {
    Friend, Request, Requested, Chat;

    @get:StringRes
    val title: Int
        get() = when (this) {
            Friend, Chat -> R.string.pageTitle_friends
            Request, Requested -> R.string.pageTitle_friendRequest
        }
    val text: String
        get() = when (this) {
            Friend -> "My Friends"
            Chat -> "Direct Message"
            Request -> "Request Friends"
            Requested -> "Friends Request"
        }
    val buttons: List<TitleTabButtonType>
        get() = when (this) {
            Friend -> listOf(TitleTabButtonType.AddFriend)
            else -> listOf()
        }

    val action:String
        get() = when (this) {
            Chat -> "Chat"
            Friend -> "Friend"
            Request -> "Request"
            Requested -> "Get Request"
        }
    val status:FriendStatus
        get() = when (this) {
            Friend, Chat -> FriendStatus.Chat
            Request -> FriendStatus.RequestFriend
            Requested -> FriendStatus.RecieveFriend
        }

    val apiType:ApiType
        get() = when (this) {
            Friend, Chat -> ApiType.GetFriends
            Request -> ApiType.GetRequestFriends
            Requested -> ApiType.GetRequestedFriends
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendList(
    friendListViewModel:FriendListViewModel? = null,
    modifier: Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    type:FriendListType = FriendListType.Friend,
    user:User,
    marginBottom:Float = DimenMargin.medium,
    isEdit:Boolean = false
) {

    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: FriendListViewModel by remember { mutableStateOf(
        friendListViewModel ?: FriendListViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE )
    )}

    fun updateFriend(): Boolean {
        viewModel.lazySetup(id = user.userId, type = type)
        viewModel.reset()
        viewModel.load()
        return true
    }

    val isEmpty = viewModel.isEmpty.observeAsState()
    val friends by viewModel.listDatas.observeAsState()
    val isInit:Boolean by remember { mutableStateOf(updateFriend()) }

    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) { viewModel.continueLoad() }
    AppTheme {
        if (isInit) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp)
            ) {
                if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
                else if (friends != null)
                    friends?.let { datas ->
                        LazyColumn(
                            modifier = Modifier.weight(1.0f),
                            state = scrollState,
                            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                            contentPadding = PaddingValues(bottom = marginBottom.dp)
                        ) {
                            items(datas, key = {it.index}) { data ->
                                FriendListItem(
                                    data = data,
                                    isMe = viewModel.isMe,
                                    status = if (isEdit && type == FriendListType.Friend) FriendStatus.Friend else type.status,
                                    isHorizontal = false
                                ) {
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.User)
                                            .addParam(key = PageParam.id, value = data.userId)
                                    )
                                }
                            }
                        }
                    }
                else
                    Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
