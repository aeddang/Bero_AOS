package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumFunctionViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendFunctionViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.PageComposePresenter
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.lib.util.toggle
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.ListDetailItem
import com.skeleton.component.item.ListItem
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

class AlbumListItemData{
    var index:Int = -1; private set
    var imagePath:String? = null; private set
    var thumbIagePath:String? = null; private set

    val isLike:MutableLiveData<Boolean> = MutableLiveData(false)
    val isExpose:MutableLiveData<Boolean> = MutableLiveData(false)
    val likeCount:MutableLiveData<Double> = MutableLiveData(0.0)
    val isDelete:MutableLiveData<Boolean> = MutableLiveData(false)

    var pictureId:Int = -1; private set
    var walkId:Int? = null; private set
    var type:MissionCategory? = null; private set

    fun setData(data:PictureData, idx:Int = -1) : AlbumListItemData{
        index = idx
        imagePath = data.pictureUrl
        thumbIagePath = data.smallPictureUrl
        pictureId = data.pictureId ?: -1
        isLike.value = data.isChecked ?: false
        isExpose.value = data.isExpose ?: false
        likeCount.value = data.thumbsupCount ?: 0.0
        walkId = data.referenceId?.toInt()
        if (walkId != null) type = MissionCategory.Walk
        return this
    }
    fun updata(isLike:Boolean) : AlbumListItemData{
        if(this.isLike.value == isLike) return this
        this.likeCount.value =
            if(isLike) this.likeCount.value?.plus(1)
            else this.likeCount.value?.minus(1)
        this.isLike.value = isLike
        return this
    }
    fun updata(isExpose:Boolean?) : AlbumListItemData{
        if (this.isExpose.value == isExpose) return this
        this.isExpose.value = isExpose
        return this
    }

}

@Composable
fun AlbumListItem(
    modifier: Modifier = Modifier,
    type: AlbumListType = AlbumListType.Normal,
    data:AlbumListItemData,
    user:User? = null,
    userProfile:UserProfile? = null,
    pet:PetProfile? = null,
    imgSize:Size,
    isEdit:Boolean = false,
    isOriginSize:Boolean = false
){
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PagePresenter = get()

    val albumFunctionViewModel: AlbumFunctionViewModel by remember { mutableStateOf(
        AlbumFunctionViewModel(repository).initSetup(owner).lazySetup(data)
    ) }

    val isDelete by data.isDelete.observeAsState()
    val isLike by data.isLike.observeAsState()
    val likeCount by data.likeCount.observeAsState()
    val isExpose by data.isExpose.observeAsState()


    fun onMoveWalk(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.WalkInfo)
                .addParam(PageParam.id, data.walkId)
                .addParam(PageParam.data, user ?: userProfile)
        )
    }
    fun onMovePicture(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.PictureViewer)
                .addParam(PageParam.data, data)
                .addParam(PageParam.userData, user)
        )
        /*
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Album)
                .addParam(key = PageParam.data, value = user)
                .addParam(key = PageParam.subData, value = pet)
                .addParam(key = PageParam.id, value = data.pictureId)
        )*/
    }

    Box(modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ){
        when(type){
            AlbumListType.Normal ->
                ListItem(
                    imagePath = data.thumbIagePath,
                    imgSize = imgSize,
                    icon = data.type?.icon,
                    iconText = data.type?.text,
                    likeCount = likeCount,
                    isLike = isLike ?: false,
                    likeSize = SortButtonSizeType.Small,
                    iconAction = { onMoveWalk() },
                    action = { albumFunctionViewModel.updateLike(isLike?.toggle() ?: false) },
                    move = { onMovePicture() }
                )
            AlbumListType.Detail ->
                ListDetailItem(
                    imagePath = data.thumbIagePath,
                    imgSize = imgSize,
                    icon = data.type?.icon,
                    iconText = data.type?.text,
                    likeCount = likeCount,
                    isLike = isLike ?: false,
                    likeSize = SortButtonSizeType.Small,
                    isShared = if(user?.isMe == true) isExpose else null,
                    isOriginSize = isOriginSize,
                    iconAction = { onMoveWalk() },
                    likeAction = { albumFunctionViewModel.updateLike(isLike?.toggle() ?: false) },
                    shareAction = { albumFunctionViewModel.updateExpose(isExpose?.toggle() ?: false) },
                    move = { onMovePicture() }
                )
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
fun AlbumListItemComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            AlbumListItem(
                data = AlbumListItemData(),
                imgSize = Size(180.0f, 100.0f),
            )

        }
    }
}