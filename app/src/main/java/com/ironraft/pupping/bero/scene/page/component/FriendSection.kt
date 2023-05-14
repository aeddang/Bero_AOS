package com.ironraft.pupping.bero.scene.page.component

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.FriendData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.provider.model.FriendStatus

@Composable
fun FriendSection(
    modifier: Modifier = Modifier,
    user:User? = null,
    listSize:Float? = null,
    type:FriendListType = FriendListType.Friend,
    isEdit:Boolean = false,
    pageSize:Int = if(SystemEnvironment.isTablet) 5 else 3,
    rowSize:Int = if(SystemEnvironment.isTablet) 5 else 3
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val appTag = "FriendSection"
    val pagePresenter: PageComposePresenter = get()
    val dataProvider: DataProvider = get()

    val currentId:String  by remember { mutableStateOf(user?.userId ?: "") }
    val isMe:Boolean by remember { mutableStateOf(user?.isMe ?: false) }
    fun updateFriend(): Float {
        val screenWidth:Float = listSize ?: screenWidth.toFloat()
        val q = ApiQ(appTag,
            type.apiType,
            contentID = currentId,
            page = 0,
            pageSize = pageSize,
            requestData = 0)

        dataProvider.requestData(q)
        val r: Float = rowSize.toFloat()
        return (screenWidth - (DimenMargin.regularExtra * (r - 1))) / r
    }
    val imageSize:Float by remember { mutableStateOf(updateFriend()) }
    var isEmpty:Boolean by remember { mutableStateOf(true) }
    var friends:List<FriendListItemData> by remember { mutableStateOf(listOf()) }

    val apiResult = dataProvider.result.observeAsState()
    fun reset(){
        friends = listOf()
    }
    fun loaded(datas:List<FriendData>){
        var added:List<FriendListItemData> = listOf()
        val start = friends.count()
        added = datas.mapIndexed { idx, d  ->
            FriendListItemData().setData(d,  idx = start + idx, type = type.status)
        }
        val prev = friends.toMutableList()
        prev.addAll(added)
        friends = prev
        isEmpty = friends.isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    apiResult.value.let { res ->
        res?.type ?: return@let
        if(res.contentID != currentId) return@let
        if(res.requestData != 0) return@let
        when ( res.type ){
            ApiType.GetFriends -> {
                if(type == FriendListType.Friend)
                    reset()
                    loaded(res.data as? List<FriendData> ?: listOf())
            }
            ApiType.GetRequestFriends -> {
                if(type == FriendListType.Friend)
                    reset()
                    loaded(res.data as? List<FriendData> ?: listOf())
            }
            ApiType.GetRequestedFriends -> {
                if(type == FriendListType.Friend)
                    reset()
                    loaded(res.data as? List<FriendData> ?: listOf())
            }
            else ->{}
        }
    }

    fun moveFriend(id:String? = null){
        if (dataProvider.user.isSameUser(id)) {
            Toast(pagePresenter.activity).showCustomToast(
                R.string.alert_itsMe,
                pagePresenter.activity
            )

        } else {
            /*
            pagePresenter.openPopup(
                PageProvider.getPageObject(.user).addParam(key: .id, value:id)
            )
            */
        }

    }

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                type = TitleTabType.Section,
                title = stringResource(id = type.title),
                buttons = arrayListOf(TitleTabButtonType.ViewMore)
            ){
                when(it){
                    TitleTabButtonType.ViewMore -> {
                        /*
                        PageProvider.getPageObject(.friend)
                        .addParam(key: .data, value: self.user)
                        .addParam(key: .type, value: self.type)
                        .addParam(key: .isEdit, value: self.isEdit)
                         */
                    }
                    else -> {}
                }
            }
            if (isEmpty)
                EmptyItem(type = EmptyItemType.MyList)
            else
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    friends.forEach { data ->
                        FriendListItem(
                            data = data,
                            imgSize = imageSize,
                            isMe = isMe,
                            //status = if(isEdit && type == FriendListType.Friend) FriendStatus.Friend else type.status,
                            isHorizontal = true)
                        {
                            moveFriend(data.userId)
                        }
                    }
                }
        }
    }
}
