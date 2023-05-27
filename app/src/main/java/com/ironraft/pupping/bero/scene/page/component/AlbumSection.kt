package com.ironraft.pupping.bero.scene.page.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.*
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.*
import com.lib.util.*
import com.skeleton.view.button.WrapTransparentButton
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlbumSection(
    modifier: Modifier = Modifier,
    user:User?,
    pet:PetProfile? = null,
    listSize:Float = 300.0f,
    pageSize:Int = if(SystemEnvironment.isTablet) 12 else 6,
    rowSize:Int = if(SystemEnvironment.isTablet) 4 else 2
) {
    val owner = LocalLifecycleOwner.current
    val appTag = "AlbumSection"
    val repository:PageRepository = get()
    val appSceneObserver:AppSceneObserver = get()
    val pagePresenter: PageComposePresenter = get()
    val dataProvider: DataProvider = get()

    val viewModel: AlbumListViewModel by remember { mutableStateOf(
        AlbumListViewModel(repo = repository).initSetup(owner, pageSize, limitedSize = pageSize)
    )}
    val pickViewModel: AlbumPickViewModel by remember { mutableStateOf(
        AlbumPickViewModel(repo = repository).initSetup(owner)
    )}

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

    fun getId(): String {
        pet?.petId?.let { return it.toString() }
        return user?.userId ?: ""
    }

    fun updateAlbum(): Size {
        val id = getId()
        val type = getType()
        viewModel.lazySetup(id, type)
        pickViewModel.lazySetup(id, type)
        viewModel.reset()
        viewModel.load()
        val r: Float = rowSize.toFloat()
        val margin = DimenMargin.regularExtra.toInt()
        val w = (listSize - (margin * (r - 1))) / r
        return Size(w, w*DimenItem.albumList.height/DimenItem.albumList.width)
    }
    val albumSize:Size by remember { mutableStateOf(updateAlbum()) }
    val isEmpty = viewModel.isEmpty.observeAsState()
    val albums = viewModel.listDatas.observeAsState()

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

    fun moveAlbum(){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Album)
                .addParam(key = PageParam.data, value = user)
                .addParam(key = PageParam.subData, value = pet)
        )
    }

    AppTheme {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                type = TitleTabType.Section,
                title = title,
                buttons =
                    if (isEmpty.value == true)
                        if(user?.isMe == true) arrayListOf(TitleTabButtonType.Add) else arrayListOf()
                    else arrayListOf(TitleTabButtonType.ViewMore)
            ){
                when(it){
                    TitleTabButtonType.ViewMore -> {
                        moveAlbum()
                    }
                    TitleTabButtonType.Add -> {
                        if (dataProvider.user.pets.isEmpty()) needDog()
                        else pickViewModel.onPick()
                    }
                    else -> {}
                }
            }
            if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
            else
                albums.value?.let { datas ->
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
                        ) {
                            datas.forEach { data ->
                                WrapTransparentButton({
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.PictureViewer)
                                            .addParam(PageParam.data, data)
                                    )
                                }) {
                                    AlbumListItem(
                                        data = data,
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
}
