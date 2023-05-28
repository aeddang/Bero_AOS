package com.ironraft.pupping.bero.scene.page.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.WalkListItem
import com.ironraft.pupping.bero.scene.component.item.WalkListItemData
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
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.rememberForeverLazyListState
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get
import java.time.LocalDate

internal class PageWalkHistoryViewModel(repo:PageRepository): PageViewModel(PageID.WalkHistory, repo){
    var currentUserId:String = ""; private set
    val user = MutableLiveData<User?>(null)
    val walkDatas = MutableLiveData<List<WalkListItemData>>(listOf())
    val selectAbleDate = MutableLiveData<List<String>>(listOf())
    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        when (type) {
            PageEventType.ChangedPage -> {
                val user = pageObj.getParamValue(PageParam.data) as? User
                this.user.value = user
                currentUserId = user?.userId ?: ""
                getMonthlyWalk(AppUtil.networkDate())
            }
            else ->{}
        }
    }
    fun getMonthlyWalk(date:LocalDate ){
        val q = ApiQ(appTag, ApiType.GetMonthlyWalk, contentID = currentUserId, requestData = date)
        repo.dataProvider.requestData(q)
    }
    fun getWalks(date:LocalDate){
        val q = ApiQ(appTag, ApiType.GetWalks, contentID = currentUserId, requestData = date, pageSize = 999 )
        repo.dataProvider.requestData(q)
    }
    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) { it ->
            val res = it ?: return@observe
            if(res.contentID != currentUserId) return@observe
            when ( res.type ){
                ApiType.GetMonthlyWalk -> {
                    (res.data as? List<String>)?.let{ datas->
                        selectAbleDate.value = datas
                        if(walkDatas.value?.isEmpty() == true) {
                            getWalks(AppUtil.networkDate())
                        }
                    }
                }
                ApiType.GetWalks ->{
                    (res.data as? List<WalkData>)?.let{ datas->
                        val ac = repo.pagePresenter.activity
                        val items = datas.mapIndexed{ idx, data ->
                            WalkListItemData().setData(data, idx, ac)
                        }
                        walkDatas.value = items
                    }
                }
                else ->{}
            }
        }

    }
}
@Composable
fun PageWalkHistory(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageWalkHistoryViewModel by remember { mutableStateOf(
        PageWalkHistoryViewModel(repository).initSetup(owner) as PageWalkHistoryViewModel
    )}
    val scrollState: LazyListState = rememberForeverLazyListState(key = PageID.WalkHistory.value)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val user by viewModel.user.observeAsState()
    val walkDatas by viewModel.walkDatas.observeAsState()
    val selectAbleDate by viewModel.walkDatas.observeAsState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            parentLazyListState = scrollState,
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
        LazyColumn(
            modifier = Modifier.weight(1.0f),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
            contentPadding = PaddingValues(
                start = DimenApp.pageHorinzontal.dp,
                end = DimenApp.pageHorinzontal.dp,
                top = DimenMargin.regular.dp,
                bottom = DimenMargin.medium.dp
            )
        ) {
            item {  }

        }

    }
}

@Preview
@Composable
fun PageWalkHistoryPreview(){
    PageWalkHistory(
    )
}
