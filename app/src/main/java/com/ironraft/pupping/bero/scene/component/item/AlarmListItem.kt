package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PagePresenter
import com.lib.util.toggle
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

class AlarmListItemData{
    var index:Int = -1; private set
    var type:AlarmType? = null; private set
    var imagePath:String? = null; private set
    var title:String? = null; private set
    var description:String? = null; private set
    var user:User? = null; private set
    var pet:PetProfile? = null; private set
    var album:AlbumListItemData? = null; private set
    var isDelete:MutableLiveData<Boolean> = MutableLiveData(false)

    fun setData(data:AlarmData, idx:Int) : AlarmListItemData{
        index = idx
        data.user?.let {
            user = User().setData(it)
        }
        data.pet?.let{
            pet = PetProfile().init(data = it, userId = user?.userId)
        }
        data.album?.let {
            album = AlbumListItemData().setData(it)
        }
        title = data.title
        description = data.contents
        imagePath = album?.thumbIagePath
        return this
    }
}

@Composable
fun AlarmListItem(
    modifier: Modifier = Modifier,
    data:AlarmListItemData,
    isEdit:Boolean = false,
    isOriginSize:Boolean = false
){
    val dataProvider:DataProvider = get()
    val pagePresenter:PagePresenter = get()
    val isDelete by data.isDelete.observeAsState()

    fun onMove(){
        when(data.type) {
           AlarmType.Friend ->
               pagePresenter.openPopup(
                   PageProvider.getPageObject(PageID.Friend)
                       .addParam(key = PageParam.data, value = dataProvider.user)
                       .addParam(key = PageParam.type, value = FriendListType.Requested)
                       .addParam(key = PageParam.isEdit, value = true)
               )
            AlarmType.User ->
                data.user?.userId?.let {
                    pagePresenter.openPopup(
                        PageProvider.getPageObject(PageID.User)
                            .addParam(key = PageParam.id, value = it)
                    )
                }

            AlarmType.Chat ->
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.Chat)
                )

            AlarmType.Album ->
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.Picture)
                        .addParam(key = PageParam.title,
                            value = pagePresenter.activity.getString(R.string.pageTitle_alarm)
                        )
                        .addParam(key = PageParam.subText, value = data.title ?: data.description)
                        .addParam(key = PageParam.data, value = data.album)
                        .addParam(key = PageParam.subData, value = data.user)
                        .addParam(key = PageParam.userData, value = dataProvider.user)
                )
            else -> {}
        }
    }
    fun onMoveUser(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Album)
                .addParam(key = PageParam.data, value = data.user)
                .addParam(key = PageParam.isEdit, value = true)
                .addParam(key = PageParam.id, value = data.pet?.userId)
        )
    }
    Row(
        modifier = modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        WrapTransparentButton(action = { onMove()}) {
            HorizontalProfile(
                type = HorizontalProfileType.Pet,
                color = ColorBrand.primary,
                imagePath = data.pet?.imagePath?.value
                    ?: data.user?.currentProfile?.imagePath?.value,
                name = data.title,
                description = data.description,
                withImagePath = data.imagePath,
                isSelected = false
            ){ type ->
                when (type) {
                    null -> onMoveUser()
                    HorizontalProfileFuncType.View -> onMove()
                    else -> onMove()
                }
            }
        }

        if (isEdit){
            CircleButton(
                modifier = Modifier.padding(all = DimenMargin.thin.dp),
                type = CircleButtonType.Icon,
                icon = R.drawable.delete,
                isSelected = isDelete ?: false,
                activeColor = ColorBrand.primary
            ){
                data.isDelete.value?.toggle()?.let {
                    data.isDelete.value = it
                }
            }
        }
    }
}


@Preview
@Composable
fun AlarmListItemComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            AlarmListItem(
                data = AlarmListItemData()
            )

        }
    }
}