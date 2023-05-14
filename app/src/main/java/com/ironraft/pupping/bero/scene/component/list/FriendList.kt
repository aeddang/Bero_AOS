package com.ironraft.pupping.bero.scene.component.list

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.skeleton.theme.*
import dev.burnoo.cokoin.get


enum class FriendListType {
    Friend, Request, Requested, Chat;

    @get:StringRes
    val title: Int
        get() = when (this) {
            Friend, Chat -> R.string.pageTitle_friends
            Request, Requested -> R.string.pageTitle_friendRequest
        }
    val text: String
        get() = when (this) {
            Friend -> "My Friends"
            Chat -> "Direct Message"
            Request -> "Request Friends"
            Requested -> "Friends Request"
        }
    val buttons: List<TitleTabButtonType>
        get() = when (this) {
            Friend -> listOf(TitleTabButtonType.AddFriend)
            else -> listOf()
        }

    val action:String
        get() = when (this) {
            Chat -> "Chat"
            Friend -> "Friend"
            Request -> "Request"
            Requested -> "Get Request"
        }
    val status:FriendStatus
        get() = when (this) {
            Friend, Chat -> FriendStatus.Chat
            Request -> FriendStatus.RequestFriend
            Requested -> FriendStatus.RecieveFriend
        }

    val apiType:ApiType
        get() = when (this) {
            Friend, Chat -> ApiType.GetFriends
            Request -> ApiType.GetRequestFriends
            Requested -> ApiType.GetRequestedFriends
        }
}

@Composable
fun FriendList(
    type:FriendListType = FriendListType.Friend,
    user:User? = null,
    listSize:Float = 300.0f,
    marginBottom:Float = DimenMargin.medium,
    isHorizontal:Boolean = false,
    isEdit:Boolean = false
) {

    val appTag = "FriendList"
    val pagePresenter:PageComposePresenter = get()
    val appSceneObserver:AppSceneObserver = get()
    val dataProvider:DataProvider = get()

    fun getImageSize(): Float {
        val row: Int = if (SystemEnvironment.isTablet) 6 else 3
        val r: Float = row.toFloat()
        return (listSize - (DimenMargin.regularExtra * (r - 1)) - (DimenApp.pageHorinzontal * 2)) / r
    }

    var currentId:String  by remember { mutableStateOf(user?.userId ?: "") }
    var isEmpty:Boolean by remember { mutableStateOf(false) }
    var friends:List<FriendListItemData> by remember { mutableStateOf(listOf()) }
    var imageSize:Float by remember { mutableStateOf(getImageSize()) }
    var isMe:Boolean by remember { mutableStateOf(user?.isMe ?: false) }


    AppTheme {
        Box (
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ){

        }
    }
}

@Preview
@Composable
fun FriendListComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FriendList(

        )
    }
}