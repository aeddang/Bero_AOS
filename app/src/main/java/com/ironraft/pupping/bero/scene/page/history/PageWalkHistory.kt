package com.ironraft.pupping.bero.scene.page.history

import android.graphics.Rect
import android.graphics.drawable.shapes.RectShape
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.ironraft.pupping.bero.scene.page.history.component.TotalWalkSection
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.rememberForeverLazyListState
import com.lib.util.rememberForeverScrollState
import com.lib.util.toDate
import com.lib.util.toDateFormatter
import com.lib.util.toFormatString
import com.skeleton.component.calendar.CPCalendar
import com.skeleton.component.calendar.CalenderEventType
import com.skeleton.component.calendar.CalenderModel
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenItem
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import dev.burnoo.cokoin.get
import java.time.LocalDate
import java.util.Date

class PageWalkHistoryViewModel(repo:PageRepository): PageViewModel(PageID.WalkHistory, repo){
    var currentUserId:String = ""; private set
    var isInitAction:Boolean = false
    val user = MutableLiveData<User?>(null)
    val walkDatas = MutableLiveData<List<WalkListItemData>>(listOf())
    val openWalkData = MutableLiveData<WalkListItemData?>(null)
    val selectAbleDate = MutableLiveData<List<String>>(listOf())
    val isEmpty = MutableLiveData<Boolean>(false)
    val currentDate = MutableLiveData<String>("")
    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        when (type) {
            PageEventType.ChangedPage -> {
                isInitAction = pageObj.getParamValue(PageParam.isInitAction) as? Boolean ?: false
                val user = pageObj.getParamValue(PageParam.data) as? User
                this.user.value = user
                currentUserId = user?.userId ?: ""
                pageObj.addParam(PageParam.isInitAction, false)
                getMonthlyWalk(AppUtil.networkDate())
            }
            else ->{}
        }
    }
    fun getMonthlyWalk(date:Date ){
        val q = ApiQ(appTag, ApiType.GetMonthlyWalk, contentID = currentUserId, requestData = date)
        repo.dataProvider.requestData(q)
    }
    fun getWalks(date:Date){

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
                        selectAbleDate.value = datas.map {
                            it.toDate("yyyy-MM-dd").toDateFormatter("yyyyMMdd")}
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
                        (res.requestData as? Date)?.let {
                            val dateStr = it.toDateFormatter("EEEE, MMMM d") ?: ""
                            if(it.toDateFormatter("yyyyMMdd") == AppUtil.networkDate().toDateFormatter("yyyyMMdd")){
                                currentDate.value = dateStr + "(" + repo.pagePresenter.activity.getString(R.string.today) + ")"
                            } else {
                                currentDate.value = dateStr
                            }


                        }
                        walkDatas.value = items
                        isEmpty.value = items.isEmpty()
                        if (isInitAction) {
                            isInitAction = false
                            openWalkData.value = items.firstOrNull()
                        }
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
    val walkManager:WalkManager = get()
    val repository:PageRepository = get()
    val dataProvider:DataProvider = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageWalkHistoryViewModel by remember { mutableStateOf(
        PageWalkHistoryViewModel(repository).initSetup(owner) as PageWalkHistoryViewModel
    )}
    val calenderModel:CalenderModel by remember { mutableStateOf(CalenderModel())}
    val scrollState: ScrollState = rememberForeverScrollState(key = PageID.WalkHistory.value)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val user by viewModel.user.observeAsState()
    val walkDatas by viewModel.walkDatas.observeAsState()
    val selectAbleDate by viewModel.selectAbleDate.observeAsState()
    val isEmpty by viewModel.isEmpty.observeAsState()
    val currentDate by viewModel.currentDate.observeAsState()
    fun getWalkListSize(): Size {
        val w = screenWidth - (DimenApp.pageHorinzontal*2)
        return Size(w, w * DimenItem.walkList.height / DimenItem.walkList.width)
    }
    val walkListSize: Size by remember { mutableStateOf(getWalkListSize()) }

    val calenderEvent = calenderModel.event.observeAsState()
    calenderEvent.value?.let {evt->
        calenderModel.event.value = null
        when (evt.type){
            CalenderEventType.ChangedMonth ->
                evt.date?.let {
                    viewModel.getMonthlyWalk(it)
                }
            CalenderEventType.SelectdDate -> {
                evt.date?.let {
                    viewModel.getWalks(it)
                }
            }
        }
    }

    fun onMoveInfo(data:WalkListItemData){
        val walkData = data.originData ?: return
        val isMe = viewModel.user.value?.isMe ?: false
        val mission = Mission().setData(walkData, userId = viewModel.currentUserId, isMe = isMe)
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.WalkInfo)
                .addParam(PageParam.data, mission)
        )
    }
    fun onMoveReport(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.WalkReport)
                .addParam(PageParam.data, dataProvider.user)
        )
    }

    val openWalkData by viewModel.openWalkData.observeAsState()
    LaunchedEffect(key1 = openWalkData){
        openWalkData?.let { onMoveInfo(it) }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            parentScrollState = scrollState,
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
        Box(modifier = Modifier
            .weight(1.0f),
            contentAlignment = Alignment.TopCenter
        ){
             if (walkDatas != null) {
                walkDatas?.let { datas->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(
                                bottom = DimenMargin.heavyExtra.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column (
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            user?.let {
                                TotalWalkSection(
                                    modifier = Modifier
                                        .padding(horizontal = DimenApp.pageHorinzontal.dp),
                                    user = it)
                            }
                            SelectButton(
                                modifier = Modifier
                                    .padding(top = DimenMargin.regular.dp)
                                    .padding(horizontal = DimenApp.pageHorinzontal.dp),
                                type = SelectButtonType.Tiny,
                                icon = R.drawable.chart,
                                text = stringResource(id = R.string.pageTitle_walkReport),
                                isSelected = false
                            ){
                                onMoveReport()
                            }
                            Spacer(modifier = Modifier
                                .padding(top = DimenMargin.medium.dp)
                                .fillMaxWidth()
                                .height(DimenLine.heavy.dp)
                                .background(ColorApp.gray200)
                            )
                            CPCalendar(modifier = Modifier
                                .padding(top = DimenMargin.thin.dp)
                                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                                .height(280.dp),
                                calenderModel = calenderModel,
                                calendarWidth = walkListSize.width,
                                selectAbleDate = selectAbleDate ?: listOf()
                            )
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(DimenLine.light.dp)
                                .background(ColorApp.gray200)
                            )


                        }
                        currentDate?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Medium,
                                fontSize = FontSize.thin.sp,
                                color = ColorBrand.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = DimenApp.pageHorinzontal.dp),
                            )
                        }
                        datas.forEach {data->
                            WalkListItem(
                                data = data,
                                imgSize = walkListSize
                            ){
                                onMoveInfo(data)
                            }
                        }
                        if(isEmpty == true) {
                            EmptyItem(
                                modifier = Modifier
                                    .padding(horizontal = DimenApp.pageHorinzontal.dp),
                                type = EmptyItemType.MyList
                            )
                            if (walkManager.status.value == WalkStatus.Ready)
                                FillButton(
                                    modifier = Modifier
                                        .padding(horizontal = DimenApp.pageHorinzontal.dp),
                                    type = FillButtonType.Fill,
                                    text = stringResource(id = R.string.button_startWalking),
                                    color = ColorBrand.primary
                                ) {
                                    pagePresenter.changePage(
                                        PageProvider.getPageObject(PageID.Walk)
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PageWalkHistoryPreview(){
    PageWalkHistory(
    )
}
