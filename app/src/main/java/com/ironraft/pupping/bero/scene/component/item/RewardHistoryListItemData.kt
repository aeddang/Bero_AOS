package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ironraft.pupping.bero.store.api.rest.RewardHistoryData
import com.ironraft.pupping.bero.store.api.rest.RewardType
import com.lib.util.toDate
import com.lib.util.toDateFormatter
import com.lib.util.toFormatString
import com.skeleton.component.item.HistoryItem
import com.skeleton.component.item.HistoryType

class RewardHistoryListItemData{
    var index:Int = -1; private set
    var title:String? = null; private set
    var date:String? = null; private set
    var value:Int = 0; private set
    var valueType:HistoryType = HistoryType.Exp; private set
    var rewardType:RewardType? = null; private set

    fun setData(data:RewardHistoryData, type:HistoryType = HistoryType.Exp, idx:Int) : RewardHistoryListItemData{
        index = idx
        rewardType = RewardType.getType(data.expType)
        title = rewardType?.text
        date = data.createdAt?.toDate()?.toDateFormatter("MMMM d, yyyy")
        valueType = type
        value = when (valueType) {
            HistoryType.Exp -> data.exp?.toInt() ?: 0
            HistoryType.Point -> data.point?.toInt() ?: 0
            else -> 0
        }
        return this
    }
}

@Composable
fun RewardHistoryListItem(
    modifier: Modifier = Modifier,
    data:RewardHistoryListItemData
){
    HistoryItem(
        type = data.valueType,
        title = data.title,
        date = data.date,
        value = data.value
    )
}


