package com.ironraft.pupping.bero.scene.page.my.component

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import dev.burnoo.cokoin.get


@Composable
fun UserPlayInfo(
    modifier: Modifier = Modifier,
    user:User? = null,
    action: ((ValueData) -> Unit)? = null

) {
    val dataProvider: DataProvider = get()
    fun getDatas():List<ValueData>{
        val lv = dataProvider.user.lv.toDouble()
        val point = dataProvider.user.point.toDouble()
        val lvData = ValueData(idx = 0, valueType = ValueInfoType.Lv, value = lv)
        val pointData = ValueData(idx = 1, valueType = ValueInfoType.Point, value = point)
        return listOf(lvData, pointData)
    }

    var datas:List<ValueData> by remember { mutableStateOf(getDatas()) }
    val userEvent = dataProvider.user.event.observeAsState()
    if (user == null ) {
        userEvent.value?.let { evt ->
            when (evt.type) {
                UserEventType.UpdatedLvData -> datas = getDatas()
                else -> {}
            }
        }
    }

    AppTheme {
        ValueBox(
            modifier = modifier,
            datas = datas,
            action =  action
        )
    }
}
