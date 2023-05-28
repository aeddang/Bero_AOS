package com.ironraft.pupping.bero.scene.page.history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.page.ComponentViewModel
import com.lib.page.PagePresenter
import com.lib.util.ComponentLog
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.PropertyInfo
import com.skeleton.component.item.PropertyInfoType
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject


@Composable
fun TotalWalkSection(
    modifier: Modifier = Modifier,
    user:User
) {
    val appTag = "TotalWalkSection"
    val pagePresenter: PagePresenter = get()
    val appSceneObserver: AppSceneObserver = get()
    val dataProvider: DataProvider = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    var totalDistance:Double by remember { mutableStateOf(0.0) }
    var totalDuration:Double by remember { mutableStateOf(0.0) }
    var totalWalkCount:Int by remember { mutableStateOf(0) }
    var speed:String by remember { mutableStateOf("") }
    var totalPct:String? by remember { mutableStateOf(null) } // 사용안함
    var profile:PetProfile? by remember { mutableStateOf(user.currentPet) }

    fun onUpdatedWalk(){
        if(profile != null) {
            profile?.let {
                totalDistance = it.exerciseDistance
                totalDuration = it.exerciseDuration
                totalWalkCount = it.totalWalkCount.value ?: 0
            }
        } else {
            totalDistance = user.exerciseDistance
            totalDuration = user.exerciseDuration
            totalWalkCount = user.totalWalkCount
        }
        val d = totalDistance
        val dr = totalDuration
        val dh = dr/3600
        val spd = if(d == 0.0 || dh == 0.0) 0.0 else d/dh
        speed = WalkManager.viewSpeed(spd, unit = null)
    }
    fun getPetData():Boolean{
        onUpdatedWalk()
        if(user.pets.isEmpty()){
            user.userId?.let {
                val q = ApiQ(appTag, ApiType.GetPets, contentID = it, isOptional = true)
                dataProvider.requestData(q)
            }
        }
        return true
    }
    val isInit by remember { mutableStateOf(getPetData()) }


    fun onSort(){
        val activity = pagePresenter.activity
        val pets = user.pets.map{it.name.value ?: ""}
        var selects = arrayListOf<String>()
        selects.addAll(pets)
        selects.add(activity.getString(R.string.button_all))
        appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = activity.getString(R.string.walkHistorySeletReport),
            buttons = selects
        ){ select ->
            if(select < 0) return@ActivitRadioEvent
            val name = selects[select]
            val find = user.pets.firstOrNull{it.name.value == name}
            profile = find
            user.currentPet = find
            onUpdatedWalk()

        }
    }

    user.event.value?.let { evt ->
        when (evt.type){
            UserEventType.UpdatedPlayData -> {
                onUpdatedWalk()
                user.event.value = null
            }
            else -> {}
        }
    }

    val apiResult = dataProvider.result.observeAsState()
    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
        if (res.contentID != user.userId) return@let
        when ( res.type ){
            ApiType.GetPets -> {
                (res.data as? List<PetData>)?.let{ user.setData(it, isMyPet = false) }
            }
            else ->{}
        }
    }
    AppTheme {
        if (isInit) {
            Column (
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column (
                        modifier = Modifier.weight(1.0f),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        totalPct?.let {
                            Text(
                                text = it,
                                fontSize = FontSize.tiny.sp,
                                color = ColorBrand.primary
                            )
                        }
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tinyExtra.dp,
                                alignment = Alignment.Start
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = WalkManager.viewDistance(totalDistance, unit = null),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 64.sp,
                                color = ColorApp.black
                            )
                            Text(
                                text = stringResource(id = R.string.km),
                                fontSize = FontSize.tiny.sp,
                                color = ColorApp.grey400,
                                modifier = Modifier.padding(top = 30.dp)
                            )
                        }
                    }
                    SortButton(
                        type = SortButtonType.Stroke,
                        sizeType = SortButtonSizeType.Big,
                        userProfile = if(profile == null) user.currentProfile else null,
                        petProfile = profile,
                        text = if(profile == null) {
                            stringResource(id = R.string.button_all)
                        } else {
                            profile?.name?.value ?: ""
                        },
                        color = ColorApp.grey400,
                        isSort = true
                    ) {
                        onSort()
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(start = DimenMargin.tiny.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.regularUltra.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PropertyInfo(
                        type = PropertyInfoType.Impect,
                        value = totalWalkCount.toString(),
                        unit = stringResource(id = R.string.walks),
                        bgColor = ColorTransparent.clear,
                        alignment = Alignment.Start
                    )
                    PropertyInfo(
                        type = PropertyInfoType.Impect,
                        value = WalkManager.viewDuration(totalDuration),
                        unit = stringResource(id = R.string.time) + "(" + stringResource(id = R.string.min) + ")",
                        bgColor = ColorTransparent.clear,
                        alignment = Alignment.Start
                    )
                    PropertyInfo(
                        type = PropertyInfoType.Impect,
                        value = speed,
                        unit =  stringResource(id = R.string.speed) + "(" + stringResource(id = R.string.kmPerH) + ")",
                        bgColor = ColorTransparent.clear,
                        alignment = Alignment.Start
                    )
                }
            }
        }
    }
}