package com.skeleton.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.ComponentViewModel
import com.lib.util.AppUtil
import com.lib.util.toDate
import com.lib.util.toFixLength
import com.lib.util.toFormatString
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.ImageButton
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.TransparentButton
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import kotlin.math.floor

enum class CalenderRequestType {
    Reset, NextMonth, PrevMonth, SelectDate
}
enum class CalenderEventType {
    ChangedMonth, SelectdDate
}

data class CalenderRequest(
    val type:CalenderRequestType,
    var date:LocalDate? = null
)
data class CalenderEvent(
    val type:CalenderEventType,
    var date:LocalDate? = null
)

enum class DayStatus {
    Normal, Today, Disable
}

data class DayData (
    val yyyyMMdd:String,
    val date: LocalDate?,
    val status:DayStatus,
)

open class CalenderModel: ComponentViewModel() {
    val event: MutableLiveData<CalenderEvent?> = MutableLiveData(null)
    val request: MutableLiveData<CalenderRequest?> = MutableLiveData(null)
    var select:String = AppUtil.networkDate().toFormatString("yyyyMMdd") ?: ""
    val weekString = listOf("S", "M", "T", "W", "T", "F", "S")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CPCalendar(
    calenderModel: CalenderModel? = null,
    modifier: Modifier = Modifier,
    calendarWidth:Float = 300.0f,
    selectAbleDate:List<String> = listOf()
) {
    val appTag = "CPCalendar"
    val viewModel: CalenderModel by remember { mutableStateOf(calenderModel ?: CalenderModel()) }
    val today:String by remember { mutableStateOf(AppUtil.networkDate().toFormatString("yyyyMMdd") ?: "") }
    var yyyy:Int by remember { mutableStateOf(0) }
    var mm:Int by remember { mutableStateOf(0) }
    var currentMonth:String by remember { mutableStateOf("") }
    var days:ArrayList<DayData> by remember { mutableStateOf(arrayListOf()) }
    var select:String? by remember { mutableStateOf(viewModel.select) }
    var hasNext:Boolean by remember { mutableStateOf(true) }
    var hasPrev:Boolean by remember { mutableStateOf(true) }

    fun onUpdate(){
        val nowDate = AppUtil.networkDate()
        val calendar = Calendar.getInstance()

        calendar.set(yyyy, mm-1, 1)
        val curentDays:ArrayList<DayData> = arrayListOf()

        do{
            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)+1
            val d = calendar.get(Calendar.DATE)
            //val day = calendar.get(Calendar.DAY_OF_WEEK)
            val yyyyMMdd = y.toString().toFixLength(4) + m.toString().toFixLength(2) + d.toString().toFixLength(2)
            val isToday = yyyyMMdd == today

            curentDays.add(
                DayData(
                    yyyyMMdd = yyyyMMdd,
                    date = yyyyMMdd.toDate("yyyyMMdd"),
                    status =
                        if (isToday) DayStatus.Today
                        else DayStatus.Normal
                )
            )
            calendar.add(Calendar.DATE, 1)

        } while(calendar.get(Calendar.MONTH) == mm-1 )

        val prevDays:ArrayList<DayData> = arrayListOf()
        calendar.set(yyyy, mm-1, 1)
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ){
            calendar.add(Calendar.DATE, -1)
            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)
            val d = calendar.get(Calendar.DATE)
            val yyyyMMdd = y.toString().toFixLength(4) + m.toString().toFixLength(2) + d.toString().toFixLength(2)
            prevDays.add(
                DayData(
                    yyyyMMdd = yyyyMMdd,
                    date = yyyyMMdd.toDate("yyyyMMdd"),
                    status =DayStatus.Disable
                )
            )
        }
        prevDays.reverse()

        val nextDays:ArrayList<DayData> = arrayListOf()
        val lastDay = curentDays.last()
        calendar.set(
            lastDay.yyyyMMdd.substring(0, 4).toInt() ,
            lastDay.yyyyMMdd.substring(4, 6).toInt()-1,
            lastDay.yyyyMMdd.substring(6, 8).toInt()
        )
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ){
            calendar.add(Calendar.DATE, 1)
            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)
            val d = calendar.get(Calendar.DATE)
            val yyyyMMdd = y.toString().toFixLength(4) + m.toString().toFixLength(2) + d.toString().toFixLength(2)
            nextDays.add(
                DayData(
                    yyyyMMdd = yyyyMMdd,
                    date = yyyyMMdd.toDate("yyyyMMdd"),
                    status =DayStatus.Disable
                )
            )
        }
        val firstDay = curentDays.first()
        currentMonth = firstDay.date?.toFormatString("MMMM, yyyy") ?: ""
        val newDays:ArrayList<DayData> = arrayListOf()
        newDays.addAll(prevDays)
        newDays.addAll(curentDays)
        newDays.addAll(nextDays)
        if(days.isNotEmpty()) viewModel.event.value = CalenderEvent(CalenderEventType.ChangedMonth, date = firstDay.date)
        days = newDays
        val nowValue = if (today.length >= 6 ) today.substring(0, 6).toInt() else 999
        val currentValue = firstDay.date?.toFormatString("yyyyMM")?.toInt() ?: 0
        hasNext = nowValue > currentValue

    }
    fun onInit():Boolean{
        yyyy = viewModel.select.toDate("yyyyMMdd")?.toFormatString("yyyy")?.toInt() ?: 2022
        mm = viewModel.select.toDate("yyyyMMdd")?.toFormatString("MM")?.toInt() ?: 1
        onUpdate()
        return true
    }

    val isInit:Boolean by remember { mutableStateOf( onInit() ) }
    fun selected(date:LocalDate){
        val yyyyMMdd = date.toFormatString("yyyyMMdd")
        select = yyyyMMdd
        viewModel.event.value = CalenderEvent(type = CalenderEventType.SelectdDate, date = date)
    }
    fun reset(date:LocalDate? = null){
        date?.toFormatString("yyyyMM")?.let {yyyyMM->
            if (yyyyMM.length != 6) return
            yyyy = yyyyMM.substring(0, 4).toInt()
            mm = yyyyMM.substring(4, 6).toInt()
            onUpdate()
            return
        }

        yyyy = today.substring(0, 4).toInt()
        mm = today.substring(4, 6).toInt()
        onUpdate()
    }

    fun next(){
        if (!hasNext) return
        val willMM = mm + 1
        if (willMM > 12) {
            mm = 1
            yyyy += 1
        } else {
            mm = willMM
        }
        onUpdate()
    }
    fun prev(){
        if (!hasPrev) return
        val willMM = mm - 1
        if (willMM < 1) {
            mm = 12
            yyyy -= 1
        } else {
            mm = willMM
        }
        onUpdate()
    }

    viewModel.request.value?.let {evt ->
        when(evt.type) {
            CalenderRequestType.Reset -> reset(evt.date)
            CalenderRequestType.PrevMonth -> prev()
            CalenderRequestType.NextMonth -> next()
            CalenderRequestType.SelectDate -> selected(evt.date ?: AppUtil.networkDate())
        }
        viewModel.request.value = null
    }
    AppTheme {
        Box(
            modifier = modifier.width(calendarWidth.dp).wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp)
            ) {
                Row {
                    ImageButton(
                        modifier = Modifier.alpha(if(hasPrev) 1.0f else 0.2f),
                        defaultImage = R.drawable.direction_left
                    ){
                        prev()
                    }
                    Text(
                        currentMonth,
                        fontWeight = FontWeight.Bold,
                        fontSize = FontSize.light.sp,
                        color = ColorApp.black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)

                    )
                    ImageButton(
                        modifier = Modifier.alpha(if(hasNext) 1.0f else 0.2f),
                        defaultImage = R.drawable.direction_right
                    ){
                        next()
                    }
                }
                Row {
                    viewModel.weekString.forEach {
                        Box(modifier = Modifier
                            .width(floor(calendarWidth / 7).dp)
                            .height(DimenIcon.medium.dp) ,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                it,
                                fontSize = FontSize.thin.sp,
                                color = ColorApp.grey300
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp)
                ) {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            days.forEach{day->
                                val isSelectable = selectAbleDate.find { it == day.yyyyMMdd } != null
                                when (day.status){
                                    DayStatus.Normal ->
                                        if (isSelectable)
                                            Box(modifier = Modifier
                                                .padding(bottom = DimenMargin.micro.dp)
                                                .width(floor(calendarWidth / 7).dp)
                                                .height(DimenIcon.medium.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(DimenIcon.medium.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (select == day.yyyyMMdd) ColorBrand.primary else ColorApp.orangeSub
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        day.date?.toFormatString("dd")?.toInt()
                                                            .toString(),
                                                        fontSize = FontSize.thin.sp,
                                                        color = if (select == day.yyyyMMdd) ColorApp.white else ColorBrand.primary
                                                    )
                                                    day.date?.let {
                                                        TransparentButton{ selected(it) }
                                                    }

                                                }
                                            }
                                        else
                                            Box(modifier = Modifier
                                                .padding(bottom = DimenMargin.micro.dp)
                                                .width(floor(calendarWidth / 7).dp)
                                                .height(DimenIcon.medium.dp) ,
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    day.date?.toFormatString("dd")?.toInt().toString(),
                                                    fontSize = FontSize.thin.sp,
                                                    color = ColorApp.grey300
                                                )
                                            }
                                    DayStatus.Today ->
                                        Box(modifier = Modifier
                                            .padding(bottom = DimenMargin.micro.dp)
                                            .width(floor(calendarWidth / 7).dp)
                                            .height(DimenIcon.medium.dp),
                                            contentAlignment = Alignment.Center
                                        ){
                                            Box(
                                                modifier = Modifier
                                                    .size(DimenIcon.medium.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (select == day.yyyyMMdd) ColorBrand.primary else ColorBrand.secondary
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    day.date?.toFormatString("dd")?.toInt()
                                                        .toString(),
                                                    fontSize = FontSize.thin.sp,
                                                    color = ColorApp.white
                                                )
                                                day.date?.let {
                                                    TransparentButton{ selected(it) }
                                                }

                                            }
                                        }

                                    DayStatus.Disable ->
                                        Box(modifier = Modifier
                                            .width(floor(calendarWidth / 7).dp)
                                            .height(DimenIcon.medium.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                day.date?.toFormatString("dd")?.toInt().toString(),
                                                fontSize = FontSize.thin.sp,
                                                color = ColorApp.grey200
                                            )
                                        }

                                }
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
fun CPCalendarComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CPCalendar(
            selectAbleDate = listOf("20230521")
        )
    }
}