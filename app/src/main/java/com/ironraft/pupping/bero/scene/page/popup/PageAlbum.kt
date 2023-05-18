package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.list.AlbumList
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import org.koin.compose.koinInject


@Composable
fun PageAlbum(
    modifier: Modifier = Modifier,
    page:PageObject? = null
){
    val repository = koinInject<PageRepository>()
    val pagePresenter = koinInject<PageComposePresenter>()
    val viewModel: AlbumPickViewModel by remember { mutableStateOf(AlbumPickViewModel(repo = repository)) }

    val scrollState: LazyListState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var user: User? by remember { mutableStateOf( null ) }
    var pet: PetProfile? by remember { mutableStateOf( null ) }
    var isEdit: Boolean by remember { mutableStateOf( false ) }

    if(page?.pageID == PageID.Album.value && !viewModel.isInit){
        viewModel.isInit = true
        val userData = page.getParamValue(PageParam.data) as? User
        val petData = page.getParamValue(PageParam.subData) as? PetProfile
        if (petData != null){
            viewModel.currentType = AlbumCategory.Pet
            viewModel.currentId = petData.petId.toString()
        } else {
            viewModel.currentType = AlbumCategory.User
            viewModel.currentId = userData?.userId ?: ""
        }
        viewModel.setDefaultLifecycleOwner(LocalLifecycleOwner.current)
        user = userData
        pet = petData
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_album),
            useBack = true,
            buttons =
                if(user?.isMe == true)
                    if(isEdit) arrayListOf() else arrayListOf(TitleTabButtonType.AddAlbum, TitleTabButtonType.Setting)
                else arrayListOf()
        ){
            when(it){
                TitleTabButtonType.Back ->
                    if(isEdit) isEdit = false
                    else pagePresenter.goBack()
                TitleTabButtonType.AddAlbum -> viewModel.onPick()
                TitleTabButtonType.Setting -> isEdit = true
                else -> {}
            }
        }
        user?.let {
            AlbumList(
                modifier = Modifier.weight(1.0f),
                scrollState = scrollState,
                type = AlbumListType.Detail,
                user = user,
                pet = pet,
                listSize = screenWidth.toFloat(),
                isEdit = isEdit
            )
        }

    }
}

@Preview
@Composable
fun PageAlbumPreview(){
    PageAlbum(
    )
}
