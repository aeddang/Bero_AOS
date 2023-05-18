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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendListViewModel
import com.ironraft.pupping.bero.store.PageRepository

@Composable
fun FriendSection(
    modifier: Modifier = Modifier,
    user:User? = null,
    listSize:Float = 300.0f,
    type:FriendListType = FriendListType.Friend,
    isEdit:Boolean = false,
    pageSize:Int = if(SystemEnvironment.isTablet) 5 else 3,
    rowSize:Int = if(SystemEnvironment.isTablet) 5 else 3
) {

    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter: PageComposePresenter = get()
    val dataProvider: DataProvider = get()

    val viewModel: FriendListViewModel by remember { mutableStateOf(
        FriendListViewModel(repository).initSetup(owner, pageSize, limitedSize = pageSize)
    ) }
    val isMe:Boolean by remember { mutableStateOf(user?.isMe ?: false) }
    fun updateFriend(): Float {
        viewModel.currentId = user?.userId ?: ""
        viewModel.currentType = type
        viewModel.reset()
        viewModel.load()
        val screenWidth:Float = listSize
        val r: Float = rowSize.toFloat()
        val margin = DimenMargin.regularExtra.toInt()
        return (screenWidth - (margin * (r - 1))) / r
    }
    val imageSize:Float by remember { mutableStateOf(updateFriend()) }
    val isEmpty = viewModel.isEmpty.observeAsState()
    val friends = viewModel.listDatas.observeAsState()


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
            if (isEmpty.value == true)  EmptyItem(type = EmptyItemType.MyList)
            else
                friends.value?.let { datas->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        datas.forEach { data ->
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
}
