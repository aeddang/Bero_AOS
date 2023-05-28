package com.ironraft.pupping.bero.scene.component.list

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
import com.ironraft.pupping.bero.scene.component.item.WalkListItem
import com.ironraft.pupping.bero.scene.component.viewmodel.WalkListViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.lib.page.PageComposePresenter
import com.lib.util.isScrolledToEnd
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get

@Composable
fun WalkList(
    walkListViewModel:WalkListViewModel? = null,
    modifier: Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    userId:String,
    listSize:Float = 300.0f,
    marginBottom:Float = DimenMargin.medium
) {

    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: WalkListViewModel by remember { mutableStateOf(
        walkListViewModel ?: WalkListViewModel(repository, userId).initSetup(owner, ApiValue.PAGE_SIZE )
    )}

    fun updateWalkList(): Size {
        viewModel.reset()
        viewModel.load()
        val w = listSize - (DimenApp.pageHorinzontal*2)
        return Size(w, w * DimenItem.walkList.height / DimenItem.walkList.width)
    }


    val isEmpty = viewModel.isEmpty.observeAsState()
    val walks by viewModel.listDatas.observeAsState()
    val walkListSize:Size by remember { mutableStateOf(updateWalkList()) }

    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) { viewModel.continueLoad() }
    AppTheme {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp)
        ) {
            if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
            else if(walks != null)
                walks?.let { datas->
                    LazyColumn(
                        modifier = Modifier.weight(1.0f),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        contentPadding = PaddingValues(
                            start = DimenApp.pageHorinzontal.dp,
                            end = DimenApp.pageHorinzontal.dp,
                            bottom = marginBottom.dp
                        )
                    ) {
                        items(datas, key = {it.index}) {data ->
                            WalkListItem(
                                data = data,
                                imgSize = walkListSize
                            ){

                            }
                        }
                    }
                }
            else
                Spacer(modifier = Modifier.fillMaxSize())
        }
    }
}
