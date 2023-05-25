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
import com.ironraft.pupping.bero.scene.component.item.BlockUserItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.item.RewardHistoryListItem
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.BlockUserListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.RewardHistoryListViewModel
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
import com.skeleton.component.item.HistoryType
import com.skeleton.theme.*
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get



@Composable
fun RewardHistoryList(
    type:HistoryType = HistoryType.Exp,
    rewardHistoryListViewModel:RewardHistoryListViewModel? = null,
    modifier: Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    marginBottom:Float = DimenMargin.medium
) {

    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: RewardHistoryListViewModel by remember { mutableStateOf(
        rewardHistoryListViewModel ?: RewardHistoryListViewModel(repo = repository, type = type)
            .initSetup(owner, ApiValue.PAGE_SIZE )
    )}

    fun updateUser(): Boolean {
        viewModel.reset()
        viewModel.load()
        return true
    }

    val isEmpty = viewModel.isEmpty.observeAsState()
    val users by viewModel.listDatas.observeAsState()
    var onInit:Boolean by remember { mutableStateOf(updateUser()) }

    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) { viewModel.load() }
    AppTheme {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp)
        ) {
            if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
            else if(users != null)
                users?.let { datas->
                    LazyColumn(
                        modifier = Modifier.weight(1.0f),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        contentPadding = PaddingValues(bottom = marginBottom.dp)
                    ) {
                        items(datas) {data ->
                            RewardHistoryListItem(
                                data = data
                            )
                        }
                    }
                }
            else
                Spacer(modifier = Modifier.fillMaxSize())
        }
    }
}
