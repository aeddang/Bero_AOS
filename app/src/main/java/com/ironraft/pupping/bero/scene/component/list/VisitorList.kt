package com.ironraft.pupping.bero.scene.component.list

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileUser
import com.ironraft.pupping.bero.scene.component.item.WalkListItem
import com.ironraft.pupping.bero.scene.component.item.WalkListItemData
import com.ironraft.pupping.bero.scene.component.viewmodel.VisitorListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.WalkListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.PageComposePresenter
import com.lib.util.isScrolledToEnd
import com.lib.util.showCustomToast
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get

@Composable
fun VisitorList(
    visitorListViewModel:VisitorListViewModel? = null,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    placeId:String,
    marginVertical:Float = DimenMargin.heavy
) {

    val owner = LocalLifecycleOwner.current
    val dataProvider: DataProvider = get()
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: VisitorListViewModel by remember { mutableStateOf(
        visitorListViewModel ?: VisitorListViewModel(repository, placeId).initSetup(owner, ApiValue.PAGE_SIZE )
    )}
    fun updateWalkList(): Boolean {
        viewModel.reset()
        viewModel.load()
        return true
    }

    val isEmpty = viewModel.isEmpty.observeAsState()
    val visitors by viewModel.listDatas.observeAsState()
    val isInit by remember { mutableStateOf(updateWalkList()) }

    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) { viewModel.continueLoad() }

    fun onMove(id:String? = null){
        if (dataProvider.user.isSameUser(id)) {
            Toast(pagePresenter.activity).showCustomToast(
                pagePresenter.activity.getString(R.string.alert_itsMe),
                pagePresenter.activity
            )
            return
        }
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.User)
                .addParam(PageParam.id, id)
        )
    }
    AppTheme {
        if (isInit)
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp)
            ) {
                if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
                else if(visitors != null)
                    visitors?.let { datas->
                        LazyColumn(
                            modifier = Modifier.weight(1.0f),
                            state = scrollState,
                            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                            contentPadding = PaddingValues(
                                horizontal = DimenApp.pageHorinzontal.dp,
                                vertical = marginVertical.dp
                            )
                        ) {
                            items(datas, key = {it.index}) {data ->
                                PetProfileUser(
                                    profile = data,
                                    friendStatus =
                                        if(data.isMypet) null
                                        else {
                                            if(data.isFriend) FriendStatus.Chat else FriendStatus.Norelation
                                        }
                                ) {
                                    onMove(data.userId)
                                }
                            }
                        }
                    }
                else
                    Spacer(modifier = Modifier.fillMaxSize())
            }
    }
}
