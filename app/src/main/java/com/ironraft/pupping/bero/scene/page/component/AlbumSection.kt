package com.ironraft.pupping.bero.scene.page.component

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSelectEvent
import com.ironraft.pupping.bero.activityui.ActivitSelectType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.*
import com.lib.util.*
import com.skeleton.module.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun AlbumSection(
    modifier: Modifier = Modifier,
    user:User,
    pet:PetProfile? = null,
    listSize:Float = 300.0f,
    pageSize:Int = if(SystemEnvironment.isTablet) 12 else 6,
    rowSize:Int = if(SystemEnvironment.isTablet) 4 else 2
) {

    val appTag = "AlbumSection"
    val repository:PageRepository = get()
    val appSceneObserver:AppSceneObserver = get()
    val pagePresenter: PageComposePresenter = get()
    val dataProvider: DataProvider = get()
    val activityModel: PageAppViewModel = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    fun getTitle(): String {
        val defaultTitle = pagePresenter.activity.getString(R.string.pageTitle_album)
        pet?.name?.let {
            return it.value + pagePresenter.activity.getString(R.string.owners) + " " + defaultTitle
        }
        return defaultTitle
    }
    val title:String by remember { mutableStateOf(getTitle()) }

    fun getType(): AlbumCategory {
        pet?.petId?.let { return AlbumCategory.Pet }
        return AlbumCategory.User
    }
    val currentType:AlbumCategory by remember { mutableStateOf(getType()) }

    fun getId(): String {
        pet?.petId?.let { return it.toString() }
        return user.userId ?: ""
    }
    val currentId:String  by remember { mutableStateOf(getId()) }

    fun updateAlbum(): Size {
        val query = HashMap<String,String>()
        query[ApiField.pictureType] = currentType.getApiCode
        val q = ApiQ(appTag,
            ApiType.GetAlbumPictures,
            contentID = currentId,
            page = 0,
            pageSize = pageSize,
            query = query,
            requestData = 0)
        dataProvider.requestData(q)

        val r: Float = rowSize.toFloat()
        val margin = DimenMargin.regularExtra.toInt().toDp
        val w = (listSize - (margin * (r - 1))) / r
        return Size(w, w*DimenItem.albumList.height/DimenItem.albumList.width)
    }
    val albumSize:Size by remember { mutableStateOf(updateAlbum()) }
    var isEmpty:Boolean by remember { mutableStateOf(true) }
    var albums:List<AlbumListItemData> by remember { mutableStateOf(listOf()) }
    var lineMax by remember { mutableStateOf(0) }
    val apiResult = dataProvider.result.observeAsState()
    fun reset(){
        albums = listOf()
    }
    fun loaded(datas:List<PictureData>){
        var added:List<AlbumListItemData> = listOf()
        val start = albums.count()
        val end = min(pageSize, datas.count()) - 1
        added = datas.slice(0..end).mapIndexed { idx, d  ->
            AlbumListItemData().setData(d,  idx = start + idx)
        }
        val prev = albums.toMutableList()
        prev.addAll(added)
        albums = prev
        isEmpty = albums.isEmpty()
        val value = albums.count().toFloat()/rowSize.toFloat()
        lineMax = ceil(value).toInt()
    }

    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
        if(res.contentID != currentId) return@let
        when ( res.type ){
            ApiType.GetAlbumPictures -> {
                if(res.requestData != 0) return@let
                reset()
                loaded(res.data as? List<PictureData> ?: listOf())
            }
            ApiType.RegistAlbumPicture ->{
                if (res.id != appTag) return@let
                (res.requestData as? AlbumData)?.let { album ->
                    if(album.type != currentType) return@let
                    reset()
                    updateAlbum()
                }
            }
            else ->{}
        }
    }
    fun needDog(){
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_addDogTitle),
            text = pagePresenter.activity.getString(R.string.alert_addDogText),
            image = R.drawable.add_dog,
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.button_later),
                pagePresenter.activity.getString(R.string.button_ok)
            )
        ){
            if(it == 1){
                pagePresenter.openPopup(PageProvider.getPageObject(PageID.AddDog))
            }
        }
    }

    val requestId:Int  by remember {mutableStateOf(UUID.randomUUID().hashCode())}
    fun onPick(){
        appSceneObserver.select.value = ActivitSelectEvent(
            type = ActivitSelectType.ImgPicker
        ){ select ->
            if (select == -1) return@ActivitSelectEvent
            when(select){
                0 ->
                    AppUtil.openIntentImagePick(pagePresenter.activity, false, requestId)
                1 ->
                    pagePresenter.requestPermission(
                        arrayOf(Manifest.permission.CAMERA),
                        requester = object : PageRequestPermission {
                            override fun onRequestPermissionResult(
                                resultAll: Boolean,
                                permissions: List<Boolean>?
                            ) {
                                if (!resultAll) return
                                AppUtil.openIntentImagePick(pagePresenter.activity, true, requestId)
                            }
                        }
                    )
            }
        }
    }

    fun update(img:Bitmap, isExpose:Boolean){
        val album:AlbumData = AlbumData(type = currentType, image = img)
        val q = ApiQ(appTag,
            ApiType.RegistAlbumPicture,
            contentID = currentId,
            requestData = album)
        dataProvider.requestData(q)
    }

    fun updateConfirm(img:Bitmap){
        val isExpose = repository.storage.isExpose
        if (repository.storage.isExposeSetup) {
            update(img, isExpose)
        } else {
            appSceneObserver.sheet.value = ActivitSheetEvent(
                type = ActivitSheetType.Select,
                text = pagePresenter.activity.getString(R.string.alert_exposeConfirm),
                isNegative = false,
                buttons = arrayListOf(
                    pagePresenter.activity.getString(R.string.alert_unExposed),
                    pagePresenter.activity.getString(R.string.alert_exposed)
                )
            ){
                update(img, it == 1)
            }
        }
    }
    fun onResultData(data: Intent){
        val imageBitmap = data.extras?.get("data") as? Bitmap
        imageBitmap?.let{ resource->
            updateConfirm(resource)
            return
        }
        data.data?.let { galleryImgUri ->
            galleryImgUri.getBitmap(pagePresenter.activity)?.let { updateConfirm(it) }
        }
    }

    val pageEvent = activityModel.event.observeAsState()
    pageEvent.value?.let { evt ->
        when(evt.type) {
            PageEventType.OnActivityForResult -> {
                if (requestId == evt.hashId){
                    val data = evt.data as? ActivityResult
                    data?.data?.let { onResultData(it) }
                }
            }
            else -> {}
        }
    }

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                type = TitleTabType.Section,
                title = title,
                buttons =
                    if (isEmpty)
                        if(user.isMe) arrayListOf(TitleTabButtonType.ViewMore) else arrayListOf()
                    else arrayListOf(TitleTabButtonType.ViewMore)
            ){
                when(it){
                    TitleTabButtonType.ViewMore -> {
                        /*
                        PageProvider.getPageObject(.album)
                            .addParam(key: .data, value: self.user)
                            .addParam(key: .subData, value: self.pet)
                         */
                    }
                    TitleTabButtonType.Add -> {
                        if (dataProvider.user.pets.isEmpty()) needDog()
                        else onPick()
                    }
                    else -> {}
                }
            }
            if (isEmpty)
                EmptyItem(type = EmptyItemType.MyList)
            else if(lineMax > 0)
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
                ) {
                    (0..lineMax).forEach {group->
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
                        ) {
                            val start = group * rowSize
                            val end = min(start + rowSize, albums.count()) - 1
                            (start..end).forEach { index->
                                AlbumListItem(
                                    data = albums[index],
                                    user = user,
                                    pet = pet,
                                    imgSize = albumSize,
                                    isEdit = false
                                )
                            }
                        }
                    }
                }
        }
    }
}
