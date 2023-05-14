package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.FriendData
import com.ironraft.pupping.bero.store.api.rest.MissionData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.lib.util.replace
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

class FriendListItemData{
    var index:Int = -1; private set
    var imagePath:String? = null; private set
    var subImagePath:String? = null; private set
    var name:String? = null; private set
    var petName:String? = null; private set
    var text:String? = null; private set
    var userId:String? = null; private set
    var lv:Int? = null; private set
    fun setData(data:MissionData, idx:Int) : FriendListItemData{
        index = idx
        userId = data.user?.userId ?: ""
        lv = data.user?.level
        val userName = data.user?.name
        var petName:String? = null
        data.pets?.let { pets->
            val representative = pets.find { it.isRepresentative ?: false } ?: pets.firstOrNull()
            if (representative != null){
                imagePath = representative.pictureUrl
                subImagePath = data.user?.pictureUrl
                petName = representative.name
            } else {
                imagePath = data.user?.pictureUrl
                subImagePath = data.pets?.firstOrNull()?.pictureUrl
                petName = data.pets?.firstOrNull()?.name
            }
        }

        if (petName != null && userName != null) {
            text = "$petName & $userName"
        } else if (petName != null) {
            text = petName
        } else if (userName != null) {
            text = userName
        }
        this.petName = petName
        this.name = userName
        return this
    }
    fun setData(data:FriendData, idx:Int, type:FriendStatus) : FriendListItemData{
        this.index = idx
        this.userId = data.refUserId
        this.lv = data.level
        val petName = data.petName
        val userName = data.userName
        if (petName?.isEmpty() == false) {
            this.imagePath = data.petImg
            this.subImagePath = data.userImg
        } else {
            this.imagePath = data.userImg
        }
        if (petName != null && userName != null) {
            text = "$petName & $userName"
        } else if (petName != null) {
            text = petName
        } else if (userName != null) {
            text = userName
        }
        this.petName = petName
        this.name = userName
        return this
    }

}

@Composable
fun FriendListItem(
    modifier: Modifier = Modifier,
    data:FriendListItemData,
    imgSize:Float,
    isMe:Boolean,
    status:FriendStatus? = null,
    isHorizontal:Boolean = true,
    action: () -> Unit
){
    val dataProvider:DataProvider = get()
    var currentStatus: FriendStatus? by remember { mutableStateOf(status) }
    val apiResult = dataProvider.result.observeAsState()

    @Suppress("UNCHECKED_CAST")
    apiResult.value.let { res ->
        res?.type ?: return@let
        if(res.contentID != data.userId) return@let
        when ( res.type ){
            ApiType.RequestFriend -> {
                currentStatus = FriendStatus.RequestFriend
            }
            ApiType.AcceptFriend -> {
                currentStatus = FriendStatus.Friend
            }
            ApiType.RejectFriend, ApiType.DeleteFriend -> {
                currentStatus = FriendStatus.Norelation
            }
            else ->{}
        }
    }

    WrapTransparentButton(action = action) {
        if(isHorizontal)
            FriendListItemBodyHorizontal(
                modifier = modifier,
                data = data,
                imgSize = imgSize,
                isMe = isMe,
                currentStatus = currentStatus
            ) {
                action()
            }
        else
            FriendListItemBodyVertical(
                modifier = modifier,
                data = data,
                imgSize = imgSize,
                isMe = isMe,
                currentStatus = currentStatus
            ) {
                action()
            }
    }
}

@Composable
fun FriendListItemBodyHorizontal(
    modifier: Modifier = Modifier,
    data:FriendListItemData,
    imgSize:Float,
    isMe:Boolean,
    currentStatus:FriendStatus? = null,
    action: () -> Unit
){
    MultiProfile(
        type = MultiProfileType.Pet,
        circleButtonType = if(data.lv == null) CircleButtonType.Image else null,
        circleButtonValue = if(data.lv == null) data.subImagePath else null,
        userId = if(isMe) data.userId else null,
        friendStatus = currentStatus,
        imagePath = data.imagePath,
        imageSize = imgSize,
        name = data.text,
        lv = data.lv,
        buttonAction = action
    )
}
@Composable
fun FriendListItemBodyVertical(
    modifier: Modifier = Modifier,
    data:FriendListItemData,
    imgSize:Float,
    isMe:Boolean,
    currentStatus:FriendStatus? = null,
    action: () -> Unit
){
    val appSceneObserver:AppSceneObserver = get()
    val pagePresenter: PageComposePresenter = get()

    fun block(){
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_blockUserConfirm).replace(data.name ?: ""),
            text = pagePresenter.activity.getString(R.string.alert_blockUserConfirmText),
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.cancel),
                pagePresenter.activity.getString(R.string.button_block)
            ),
            isNegative = true
        ){
            if(it == 1){
                //self.dataProvider.requestData(q: .init(type: .blockUser(userId: self.data.userId  ?? "", isBlock: true)))
            }
        }
    }
    fun accuse(){
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_accuseUserConfirm).replace(data.name ?: data.petName ?: ""),
            text = pagePresenter.activity.getString(R.string.alert_accuseUserConfirmText),
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.cancel),
                pagePresenter.activity.getString(R.string.button_accuse)
            ),
            isNegative = true
        ){
            if(it == 1){
                //self.dataProvider.requestData(q: .init(type: .sendReport(
                //                    reportType: .user , userId: self.data.userId
                //                ))
            }
        }
    }
    fun more(){
        val datas:List<RadioBtnData> = listOf(
            RadioBtnData(
                index = 0,
                icon = R.drawable.block,
                title = pagePresenter.activity.getString(R.string.button_block)
            ),
            RadioBtnData(
                index = 1,
                icon = R.drawable.warning,
                title = pagePresenter.activity.getString(R.string.button_accuseUser)
            )
        )
        appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = pagePresenter.activity.getString(R.string.alert_supportAction),
            radioButtons = datas
        ){
            when(it){
                0 -> block()
                1 -> accuse()
                else -> {}
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = DimenMargin.thin.dp),
        contentAlignment = Alignment.Center
    ){

        HorizontalProfile(
            type = if(data.lv == null) HorizontalProfileType.Multi else HorizontalProfileType.Pet,
            typeValue = data.subImagePath,
            sizeType = HorizontalProfileSizeType.Small,
            funcType = if(currentStatus?.useMore == true) HorizontalProfileFuncType.More  else null,
            userId = if(isMe) data.userId else null,
            friendStatus = currentStatus,
            imagePath = data.imagePath,
            lv = data.lv,
            name = data.name,
            isSelected = false,
            useBg = false
        ){
            when(it){
                HorizontalProfileFuncType.MoreFunc -> more()
                else -> action()
            }
        }
    }
}


@Preview
@Composable
fun FriendListItemComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            FriendListItem(
                data = FriendListItemData(),
                imgSize = 100.0f,
                isMe = true,
                isHorizontal = true,
                status = null
            ){

            }
            FriendListItem(
                data = FriendListItemData(),
                imgSize = 100.0f,
                isMe = false,
                isHorizontal = false,
                status = FriendStatus.Friend
            ){

            }
        }
    }
}