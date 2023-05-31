package com.ironraft.pupping.bero.scene.page.chat.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.rest.ChatRoomData
import com.lib.page.PageComposePresenter
import com.lib.util.sinceNowDate
import com.lib.util.toDate
import com.lib.util.toDateUtc
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get
import java.time.LocalDate
import java.util.Date

class ChatRoomListItemData{
    var index:Int = -1; private set
    var roomId:Int = -1; private set
    var profileImagePath:String? = null; private set
    var title:String? = null; private set
    var contents:String? = null; private set
    var date:Date? = null; private set
    var viewDate:String? = null; private set
    var unreadCount:Int = 0; private set
    var userId:String? = null; private set
    var lv:Int? = null; private set
    val isRead:MutableLiveData<Boolean> = MutableLiveData(false)
    fun setData(data:ChatRoomData, idx:Int) : ChatRoomListItemData {
        index = idx
        profileImagePath = data.receiverPet?.pictureUrl ?: data.receiver?.pictureUrl
        roomId = data.chatRoomId ?: -1
        title = data.title
        contents = data.desc
        unreadCount = data.unreadCnt ?: 0
        isRead.value = unreadCount == 0
        userId = data.receiver?.userId
        lv = data.receiver?.level
        this.date = data.updatedAt?.toDate() ?: data.createdAt?.toDate()

        val dateUtc = data.updatedAt?.toDateUtc() ?: data.createdAt?.toDateUtc()
        val viewDate = dateUtc?.sinceNowDate()
        this.viewDate = viewDate
        return this
    }
}

@Composable
fun ChatRoomListItem(
    modifier: Modifier = Modifier,
    data: ChatRoomListItemData,
    isEdit:Boolean,
    onRead:() -> Unit,
    onExit:() -> Unit
){
    val pagePresenter: PageComposePresenter = get()
    val isRead by data.isRead.observeAsState()
    WrapTransparentButton(action = {
        onRead()
    }) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                space = DimenMargin.thin.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            HorizontalProfile(
                modifier = Modifier.weight(1.0f),
                type = HorizontalProfileType.Pet,
                sizeType = HorizontalProfileSizeType.Small,
                funcType = if(isRead == true) null else HorizontalProfileFuncType.View,
                funcValue = if(isRead == true) null else " N ",
                imagePath = data.profileImagePath,
                lv = data.lv,
                name = data.title,
                date = data.viewDate,
                description = data.contents,
                isSelected = false,
                useBg = false
            ){
                when (it){
                    HorizontalProfileFuncType.View -> onRead()
                    else -> {
                        pagePresenter.openPopup(
                            PageProvider.getPageObject(PageID.User)
                                .addParam(PageParam.id, data.userId)
                        )
                    }
                }
            }
            if (isEdit){
                CircleButton(
                    modifier = Modifier.padding(all = DimenMargin.thin.dp),
                    type = CircleButtonType.Icon,
                    icon = R.drawable.exit,
                    isSelected = false,
                    activeColor = ColorBrand.primary
                ){
                    onExit()
                }
            }
        }
    }

}


