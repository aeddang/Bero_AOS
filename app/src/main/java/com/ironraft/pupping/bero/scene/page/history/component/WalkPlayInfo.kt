package com.ironraft.pupping.bero.scene.page.history.component

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import dev.burnoo.cokoin.get


@Composable
fun WalkPlayInfo(
    modifier: Modifier = Modifier,
    mission:Mission

) {
    fun getDatas():List<ValueData>{
        val walkData = ValueData(idx = 0, valueType = ValueInfoType.ExpEarned, value = mission.exp)
        val pointData = ValueData(idx = 1, valueType =ValueInfoType.PointEarned, value = mission.point.toDouble())
        return listOf(walkData, pointData)
    }

    var datas:List<ValueData> by remember { mutableStateOf(getDatas()) }

    AppTheme {
        ValueBox(
            modifier = modifier,
            datas = datas
        )
    }
}
