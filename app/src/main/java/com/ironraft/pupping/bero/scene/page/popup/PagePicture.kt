package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.scene.component.item.UserProfileItem
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.model.WalkPictureItem
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.skeleton.component.item.ListDetailItem
import com.skeleton.component.item.profile.HorizontalProfileType
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get
import java.lang.invoke.WrongMethodTypeException


@Composable
fun PagePicture(
    modifier: Modifier = Modifier
){
    val appTag = PageID.Picture.value
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Picture, repository).initSetup(owner)) }
    val currentPage = viewModel.currentPage.observeAsState()
    val scrollState: ScrollState = rememberForeverScrollState(key = appTag)
    val screenWidth = LocalConfiguration.current.screenWidthDp

    var title:String? by remember { mutableStateOf( null ) }
    var subTitle:String? by remember { mutableStateOf( null ) }
    var user: User? by remember { mutableStateOf( null ) }
    var other:User? by remember { mutableStateOf( null ) }
    var datas:List<AlbumListItemData> by remember { mutableStateOf( listOf() ) }
    var walkPictures:List<WalkPictureItem> by remember { mutableStateOf( listOf() ) }
    var itemSize:Size by remember { mutableStateOf( Size(100f, 100f) ) }

    currentPage.value?.let { page->
        if(!viewModel.isInit){
            itemSize = Size(screenWidth.toFloat(), screenWidth.toFloat())
            viewModel.isInit = true
            title = page.getParamValue(PageParam.title) as? String
            subTitle = page.getParamValue(PageParam.subText) as? String
            user = page.getParamValue(PageParam.data) as? User
            other = page.getParamValue(PageParam.subData) as? User
            (page.getParamValue(PageParam.data) as? AlbumListItemData)?.let {
                datas = listOf(it)
            }
            (page.getParamValue(PageParam.datas) as? List<WalkPictureItem>)?.let {
                walkPictures = it
            }

        }
    }

    fun onMoveUser(user:User){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.User)
            .addParam(PageParam.data, user)
        )
    }
    fun onMovePicture(path:String?){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.PictureViewer)
                .addParam(PageParam.data, path)
        )
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_album),
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(scrollState)
                .padding(
                    top = DimenMargin.regularUltra.dp,
                    bottom = DimenMargin.medium.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
        ) {
            other?.let {user->
                UserProfileItem(
                    profile = user.currentProfile,
                    type = HorizontalProfileType.Pet,
                    title = user.representativeName,
                    lv = user.lv,
                    imagePath = user.representativeImage,
                    description = subTitle
                ){
                    onMoveUser(user)
                }
            }
            datas.forEach { data->
                AlbumListItem(
                    type = AlbumListType.Detail,
                    data = data,
                    imgSize = itemSize,
                    isOriginSize = true
                )
            }

            walkPictures.forEach{data->
                WrapTransparentButton(action = {
                    onMovePicture(data.pictureUrl)
                }) {
                    ListDetailItem(
                        imagePath = data.pictureUrl,
                        imgSize = itemSize
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun PagePicturePreview(){
    PagePicture(
    )
}
