package com.ironraft.pupping.bero.scene.component.item

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
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
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PageComposePresenter
import com.lib.page.PagePresenter
import com.lib.util.*
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.ListDetailItem
import com.skeleton.component.item.ListItem
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

class UserAlbumListItemData{
    var index:Int = -1; private set
    var albumData:AlbumListItemData? = null; private set
    var userProfile:UserProfile? = null; private set
    var petProfile:PetProfile? = null; private set
    var date:String? = null; private set
    var lv:Int? = null; private set
    var postId:String? = null; private set
    fun setData(data:PictureData, idx:Int) : UserAlbumListItemData{
        index = idx
        albumData = AlbumListItemData().setData(data, idx = 0)
        data.user?.let {
            userProfile = UserProfile().setData(it)
            lv = it.level
        }
        data.pets?.find { it.isRepresentative == true }?.let {
            petProfile = PetProfile().init(data = it, userId = userProfile?.userId)
        }
        date = data.createdAt?.toDate()?.toFormatString("MMMM d, yyyy")
        postId = albumData?.pictureId.toString()
        return this
    }
}

@Composable
fun UserAlbumListItem(
    modifier: Modifier = Modifier,
    data:UserAlbumListItemData,
    imgSize:Size
){
    val pagePresenter:PageComposePresenter = get()
    val dataProvider:DataProvider = get()
    fun onMoveUser(){
        data.userProfile?.userId?.let { userId->
            if (dataProvider.user.isSameUser(userId)) {
                Toast(pagePresenter.activity).showCustomToast(
                    R.string.alert_itsMe,
                    pagePresenter.activity
                )

            } else {
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.User)
                        .addParam(PageParam.id, userId)
                )
            }
        }
    }
    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        data.userProfile?.let { user->
            if( data.petProfile != null ){
                data.petProfile?.let { pet->
                    UserProfileItem(
                        modifier = Modifier.padding(
                            vertical = DimenMargin.regularExtra.dp,
                            horizontal = DimenApp.pageHorinzontal.dp
                        ),
                        profile = user,
                        type = HorizontalProfileType.Pet,
                        reportType = ReportType.Post,
                        postId = data.postId,
                        title = pet.name.value,
                        lv = data.lv,
                        imagePath = pet.imagePath.value,
                        date = data.date,
                        action = { onMoveUser() }
                    )
                }

            } else {
                UserProfileItem(
                    modifier = Modifier.padding(
                        vertical = DimenMargin.regularExtra.dp,
                        horizontal = DimenApp.pageHorinzontal.dp
                    ),
                    profile = user,
                    type = HorizontalProfileType.User,
                    reportType = ReportType.Post,
                    postId = data.postId,
                    title = user.nickName.value,
                    lv = data.lv,
                    imagePath = user.imagePath.value,
                    date = data.date,
                    action = { onMoveUser() }
                )
            }
            data.albumData?.let { albumData->
                WrapTransparentButton(action = {
                    pagePresenter.openPopup(
                        PageProvider.getPageObject(PageID.PictureViewer)
                            .addParam(PageParam.data, albumData)
                    )

                }){
                    AlbumListItem(
                        data = albumData,
                        type = AlbumListType.Detail,
                        userProfile = user,
                        imgSize = imgSize
                    )
                }
            }
        }


    }
}


@Preview
@Composable
fun UserAlbumListItemComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            UserAlbumListItem(
                data = UserAlbumListItemData(),
                imgSize = Size(180.0f, 100.0f),
            )

        }
    }
}