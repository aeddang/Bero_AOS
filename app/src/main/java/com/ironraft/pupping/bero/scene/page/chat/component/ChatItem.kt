package com.ironraft.pupping.bero.scene.page.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.ChatData
import com.lib.util.toDate
import com.lib.util.toDateFormatter

import java.util.Date

class ChatItemData{
    var index:Int = -1; private set
    var chatId:Int = -1; private set
    var isMe:Boolean = false; private set
    var contents:String = ""; private set
    var date:Date? = null; private set
    val isDelete:MutableLiveData<Boolean> = MutableLiveData(false)
    fun setData(data:ChatData, me:String, idx:Int) : ChatItemData {
        chatId = data.chatId ?: -1
        isMe = data.receiver != me
        contents = data.contents ?: ""
        isDelete.value = data.isDeleted ?: false
        date = data.createdAt?.toDate()
        return this
    }
}

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    data: ChatItemData,
    onDelete:() -> Unit
){
    val isDelete by data.isDelete.observeAsState()
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .pointerInput(Unit){
                detectTapGestures (
                    onLongPress = {
                        onDelete()
                    }
                )
            }
        ,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = if(data.isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = DimenMargin.micro.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.Bottom

        ) {
            if (data.isMe) {
                data.date?.let {
                    Text(
                        it.toDateFormatter("hh:mm a"),
                        fontWeight = FontWeight.Light,
                        fontSize = FontSize.tiny.sp,
                        color = ColorApp.gray300
                    )
                }
            }
            Text(
                if(isDelete == true) stringResource(id = R.string.chatRoomDeletedMessage)
                else data.contents,
                fontSize = FontSize.thin.sp,
                color = if(data.isMe)  ColorApp.white else ColorApp.black,
                textAlign = if(data.isMe) TextAlign.End else TextAlign.Start,
                modifier = Modifier
                    .clip(RoundedCornerShape(corner = CornerSize(DimenRadius.medium.dp)))
                    .background(if(data.isMe) ColorBrand.primary else ColorApp.gray200)
                    .padding(
                        vertical = DimenMargin.micro.dp,
                        horizontal = DimenMargin.thin.dp
                    )


            )
            if (!data.isMe) {
                data.date?.let {
                    Text(
                        it.toDateFormatter("hh:mm a"),
                        fontWeight = FontWeight.Light,
                        fontSize = FontSize.tiny.sp,
                        color = ColorApp.gray300
                    )
                }
            }
        }
    }
}


