package com.ironraft.pupping.bero.scene.page.history

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.scene.component.graph.ArcGraph
import com.ironraft.pupping.bero.scene.component.graph.ArcGraphData
import com.ironraft.pupping.bero.scene.component.graph.CompareGraph
import com.ironraft.pupping.bero.scene.component.graph.CompareGraphData
import com.ironraft.pupping.bero.scene.component.graph.LineGraph
import com.ironraft.pupping.bero.scene.component.graph.LineGraphData
import com.ironraft.pupping.bero.scene.component.item.WalkListItemData
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.history.component.ReportText
import com.ironraft.pupping.bero.scene.page.history.component.ReportWalkPropertySection
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.api.rest.WalkReport
import com.ironraft.pupping.bero.store.api.rest.WalkSummary
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.rememberForeverScrollState
import com.lib.util.toDate
import com.lib.util.toDateFormatter
import com.lib.util.toFormatString
import com.skeleton.component.tab.MenuTab
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenBar
import com.skeleton.theme.DimenItem
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import kotlin.math.abs
import kotlin.math.min

enum class ReportType{
    Weekly, Monthly;


    @get:StringRes
    val title : Int
        get() = when(this) {
            Weekly -> R.string.reportWalkSummaryWeekly
            Monthly -> R.string.reportWalkSummaryMonthly
        }
    val text : Int
        get() = when(this) {
            Weekly -> R.string.reportWalkDayWeek
            Monthly -> R.string.reportWalkDayMonth
        }
    val idx : Int
        get() = when(this) {
            Weekly -> 0
            Monthly -> 1
        }
}
class ReportData(val ctx:Context) {
    var daysWalkData:ArcGraphData = ArcGraphData(); private set
    var daysWalkReport:String = ""; private set
    var daysWalkCompareData:List<CompareGraphData> = listOf(); private set
    var daysWalkCompareReport:String = ""; private set
    var daysWalkTimeData:LineGraphData = LineGraphData(); private set
    var currentDaysWalkTimeIdx:Int = 0; private set
    var daysWalkTimeReport:String = ""; private set
    var originData:WalkReport? = null; private set

    fun setWeeklyData(data:WalkSummary) : ReportData{
        data.weeklyReport?.let {report->
            currentDaysWalkTimeIdx = setReport(report)
            setupData(report)
        }
        return this
    }
    fun setMonthlyData(data:WalkSummary) : ReportData{
        data.monthlyReport?.let {report->
            currentDaysWalkTimeIdx = setReport(report)
            setupData(report)
        }
        return this
    }

    private fun setReport(data:WalkReport): Int{
        var todayIdx:Int = -1
        val max = (data.times?.count() ?: 7).toFloat()
        val myCount =  (data.totalCount ?: 0).toFloat()
        daysWalkCompareData = listOf(
            CompareGraphData(
                value = myCount,
                max = max ,
                color = ColorBrand.primary,
                title = ctx.getString(R.string.reportWalkDayCompareMe)
            ),
            CompareGraphData(
                value = (data.avgCount ?: 0).toFloat(),
                max = max,
                color = ColorApp.grey300,
                title = ctx.getString(R.string.reportWalkDayCompareOthers)
            )
        )
        data.times?.let { missionTimes->
            val count = missionTimes.count()
            daysWalkData = ArcGraphData(value = myCount, max = count.toFloat())
            val today = AppUtil.networkDate().toDateFormatter("yyyyMMdd")
            val values:List<Float> = missionTimes.map{ time ->
                min(50.0, time.v ?: 0.0).toFloat() / 50f
            }
            val lines:List<String> = missionTimes.mapIndexed{idx, time ->
                if (time.d == today) { todayIdx = idx }
                val date = time.d?.toDate("yyyyMMdd") ?: Date()
                val mm = date.toDateFormatter("MM").toInt().toString()
                val dd = date.toDateFormatter("dd").toInt().toString()
                "$mm/$dd"
            }
            daysWalkTimeData = LineGraphData(values=values, lines=lines)

        }
        return todayIdx
    }
    private fun setupData(data:WalkReport){
        originData = data
        val n = daysWalkData.value.toInt()
        val unit = if(n <= 1) ctx.getString(R.string.day) else ctx.getString(R.string.reportWalkDayUnit)
        daysWalkReport = "$n $unit"
        if (daysWalkCompareData.count() >= 2) {
            val me = daysWalkCompareData.first().value
            val other = daysWalkCompareData.last().value
            val diff = me - other
            if (diff > 0) {
                daysWalkCompareReport = String.format("%.2f",diff) + " " +
                        ctx.getString(R.string.reportWalkDayUnit) + " " +
                        ctx.getString(R.string.reportWalkDayCompareMore)

            } else if (diff < 0) {
                daysWalkCompareReport = String.format("%.2f",abs(diff)) + " " +
                        ctx.getString(R.string.reportWalkDayUnit) + " " +
                        ctx.getString(R.string.reportWalkDayCompareLess)
            } else {
                daysWalkCompareReport = ctx.getString(R.string.reportWalkDayCompareSame)
            }
        }
        var avg:Float = 0f
        data.times?.let {missionTimes->
            val values:List<Float> = missionTimes.map{ time ->
                (time.v ?: 0).toFloat()
            }
            avg = values.reduce { acc, fl -> acc+fl }  /  daysWalkTimeData.values.count().toFloat()
        }
        daysWalkTimeReport = String.format("%.2f",avg) + " " + ctx.getString(R.string.reportWalkRecentlyUnit)
    }
}



class PageWalkReportViewModel(repo:PageRepository): PageViewModel(PageID.WalkReport, repo){
    var currentUserId:String = ""; private set
    val user = MutableLiveData<User?>(null)
    val profile = MutableLiveData<PetProfile?>(null)
    val reportData = MutableLiveData<ReportData?>(null)
    val reportType = MutableLiveData<ReportType>(ReportType.Weekly)
    fun getWalkSummary(pet:PetProfile? = null, type:ReportType? = null){
        pet?.let { profile.value = it }
        type?.let { reportType.value = it }
        reportData.value = null
        profile.value?.petId.let {petId->
            val q = ApiQ(appTag, ApiType.GetWalkSummary, contentID = petId.toString())
            repo.dataProvider.requestData(q)
        }
    }
    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        when (type) {
            PageEventType.ChangedPage -> {
                val user = pageObj.getParamValue(PageParam.data) as? User
                user?.let {
                    this.user.value = it
                    currentUserId = it.userId ?: ""
                    val profile = pageObj.getParamValue(PageParam.subData) as? PetProfile
                    this.profile.value = profile ?: it.currentPet ?: it.pets.firstOrNull()
                    getWalkSummary()
                    return
                }
                val userId = pageObj.getParamValue(PageParam.id) as? String
                userId?.let {
                    currentUserId = it
                    getUserData()
                }


            }
            else ->{}
        }
    }
    private fun getUserData(){
        val q = ApiQ(appTag, ApiType.GetUser, contentID = currentUserId)
        repo.dataProvider.requestData(q)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) { it ->
            val res = it ?: return@observe

            when ( res.type ){
                ApiType.GetWalkSummary -> {
                    if(res.contentID != profile.value?.petId.toString()) return@observe
                    (res.data as? WalkSummary)?.let{ data->
                        val report = ReportData(repo.pagePresenter.activity)
                        this.reportData.value = when (reportType.value){
                            ReportType.Weekly -> report.setWeeklyData(data)
                            ReportType.Monthly -> report.setMonthlyData(data)
                            else-> report
                        }
                    }
                }
                ApiType.GetUser ->{
                    if(res.contentID != currentUserId) return@observe
                    (res.data as? UserData)?.let{ data->
                        val user = User().setData(data)
                        this.profile.value = user.representativePet.value ?: user.pets.firstOrNull()
                        getWalkSummary()
                    }
                }
                else ->{}
            }
        }

    }
}
@Composable
fun PageWalkReport(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val appSceneObserver: AppSceneObserver = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageWalkReportViewModel by remember { mutableStateOf(
        PageWalkReportViewModel(repository).initSetup(owner) as PageWalkReportViewModel
    )}
    val coroutineScope = rememberCoroutineScope()
    val scrollState: ScrollState = rememberForeverScrollState(key = PageID.WalkReport.value)

    val user by viewModel.user.observeAsState()
    val profile by viewModel.profile.observeAsState()
    val reportData by viewModel.reportData.observeAsState()
    val reportType by viewModel.reportType.observeAsState()
    fun onResetScroll(){
        coroutineScope.launch {
            scrollState.scrollTo(0)
        }
    }
    fun onSort(){
        val userData = user ?: return
        val datas:List<String> = userData.pets.map{it.name.value ?: ""}
        val activity = pagePresenter.activity
        appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = activity.getString(R.string.walkHistorySeletReport),
            buttons = datas
        ){ select ->
            if(select < 0) return@ActivitRadioEvent
            val selectData = userData.pets[select]
            viewModel.getWalkSummary(selectData)
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            parentScrollState = scrollState,
            title = stringResource(id = R.string.pageTitle_walkReport),
            useBack = true,
            sortPetProfile = profile,
            sortButton = profile?.name?.value,
            sort = {
                onSort()
            }
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(scrollState)
                .padding(
                    vertical = DimenMargin.medium.dp,
                    horizontal = DimenApp.pageHorinzontal.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp),
        ) {
            MenuTab(buttons = listOf(
                stringResource(id = ReportType.Weekly.title),
                stringResource(id = ReportType.Monthly.title)
            ), selectedIdx = reportType?.idx ?: -1){
                val type = if(it == 0)  ReportType.Weekly else  ReportType.Monthly
                onResetScroll()
                viewModel.getWalkSummary(type = type)
            }
            reportData?.let {data->
                reportType?.let { type ->
                    data.originData?.let {
                        ReportWalkPropertySection(data = it)
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DimenLine.light.dp)
                            .background(ColorApp.grey50)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.heavyExtra.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ReportText(
                                leading = stringResource(id = R.string.reportWalkDayText),
                                value = data.daysWalkReport,
                                trailing = stringResource(id = type.text)
                            )
                            ArcGraph(

                                data = data.daysWalkData)
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DimenLine.light.dp)
                                .background(ColorApp.grey50)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
                        ) {
                            ReportText(
                                leading = stringResource(id = R.string.reportWalkDayCompareText1),
                                value = data.daysWalkCompareReport,
                                trailing = stringResource(id = R.string.reportWalkDayCompareText2)
                            )
                            CompareGraph(datas = data.daysWalkCompareData)
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DimenLine.light.dp)
                                .background(ColorApp.grey50)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
                        ) {
                            ReportText(
                                leading = stringResource(id = R.string.reportWalkRecentlyText1),
                                value = data.daysWalkTimeReport,
                                trailing = stringResource(id = R.string.reportWalkRecentlyText2)
                            )
                            LineGraph(selectIdx = data.currentDaysWalkTimeIdx, data = data.daysWalkTimeData)
                            Text(
                                stringResource(id = R.string.reportWalkRecentlyTip),
                                fontWeight = FontWeight.Bold,
                                fontSize = FontSize.thin.sp,
                                color = ColorApp.grey400,
                                modifier = Modifier.padding(bottom = DimenMargin.medium.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun PageWalkReportPreview(){
    PageWalkReport(
    )
}
