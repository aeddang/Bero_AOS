package com.ironraft.pupping.bero.scene.page.explore

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
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
import com.ironraft.pupping.bero.scene.component.item.PetProfileTopInfo
import com.ironraft.pupping.bero.scene.component.item.UserProfileTopInfo
import com.ironraft.pupping.bero.scene.component.list.AlbumList
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.component.list.UserAlbumList
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.UserAlbumListViewModel
import com.ironraft.pupping.bero.scene.page.component.AlbumSection
import com.ironraft.pupping.bero.scene.page.component.FriendSection
import com.ironraft.pupping.bero.scene.page.my.component.MyDogsSection
import com.ironraft.pupping.bero.scene.page.my.component.MyHistorySection
import com.ironraft.pupping.bero.scene.page.component.UserPlayInfo
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.rest.ExplorerSearchType
import com.ironraft.pupping.bero.store.api.rest.ReportType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.rememberForeverLazyListState
import com.lib.util.rememberForeverScrollState
import com.lib.util.toDp
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.ValueInfoType
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch

@Composable
fun PageExplore(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Explore.value
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: UserAlbumListViewModel by remember { mutableStateOf(
         UserAlbumListViewModel(repo = repository).initSetup(owner)
    )}
    val albumPickViewModel: AlbumPickViewModel by remember { mutableStateOf(
        AlbumPickViewModel(repo = repository).initSetup(owner).meSetup())
    }
    val coroutineScope = rememberCoroutineScope()
    val scrollState: LazyListState = rememberForeverLazyListState(key = appTag)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val hasNewAlarm by repository.hasNewAlarm.observeAsState()
    var searchType:ExplorerSearchType by remember { mutableStateOf( ExplorerSearchType.All ) }

    fun onResetScroll(){
        coroutineScope.launch {
            scrollState.scrollToItem(0)
        }
    }
    fun onSort(){
        repository.appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = repository.pagePresenter.activity.getString(R.string.exploreSeletReport),
            radioButtons = arrayListOf(
                RadioBtnData(
                    icon = R.drawable.global,
                    title = repository.pagePresenter.activity.getString(ExplorerSearchType.All.title),
                    isSelected = searchType == ExplorerSearchType.All,
                    index = 0
                ),
                RadioBtnData(
                    icon = R.drawable.human_friends,
                    title = repository.pagePresenter.activity.getString(ExplorerSearchType.Friends.title),
                    isSelected = searchType == ExplorerSearchType.Friends,
                    index = 1
                ),
            )
        ){ select ->
            when(select){
                0 ->{
                    onResetScroll()
                    searchType = ExplorerSearchType.All
                    viewModel.resetLoad(ExplorerSearchType.All)
                }

                1 -> {
                    onResetScroll()
                    searchType = ExplorerSearchType.Friends
                    viewModel.resetLoad(ExplorerSearchType.Friends)
                }
                else -> {}
            }
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_explore),
            sortButton = stringResource(id = searchType.title),
            sort = { onSort() },
            buttons = arrayListOf(TitleTabButtonType.AddAlbum,TitleTabButtonType.Alarm),
            icons = if(hasNewAlarm == true) arrayListOf(null, "N") else arrayListOf()
        ){
            when(it){
                TitleTabButtonType.AddAlbum -> albumPickViewModel.onPick()
                TitleTabButtonType.Alarm -> {
                    pagePresenter.openPopup(
                        PageProvider.getPageObject(PageID.Alarm)
                    )
                }
                else -> {}
            }
        }
        UserAlbumList(
            modifier = Modifier.weight(1.0f),
            userAlbumListViewModel = viewModel,
            scrollState = scrollState,
            type = searchType,
            listSize = screenWidth.toFloat(),
            marginBottom = DimenApp.bottom
        )

    }
}
@Preview
@Composable
fun PageExplorePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageExplore(
        )
    }
}
