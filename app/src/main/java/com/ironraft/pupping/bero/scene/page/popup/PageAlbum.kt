package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.rememberForeverLazyListState
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get


@Composable
fun PageAlbum(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Album, repository).initSetup(owner)) }
    val albumPickViewModel: AlbumPickViewModel by remember { mutableStateOf(AlbumPickViewModel(repo = repository)) }

    val currentPage = viewModel.currentPage.observeAsState()
    val goBackPage = viewModel.goBack.observeAsState()
    var scrollStateKey: String by remember { mutableStateOf( "") }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var user: User? by remember { mutableStateOf( null ) }
    var pet: PetProfile? by remember { mutableStateOf( null ) }
    var isEdit: Boolean by remember { mutableStateOf( false ) }

    currentPage.value?.let { page->
        if(!albumPickViewModel.isInit){
            albumPickViewModel.isInit = true
            val userData = page.getParamValue(PageParam.data) as? User
            val petData = page.getParamValue(PageParam.subData) as? PetProfile
            var currentId:String = ""
            var currentType:AlbumCategory = AlbumCategory.User
            if (petData != null){
                currentType = AlbumCategory.Pet
                currentId = petData.petId.toString()
            } else {
                currentType = AlbumCategory.User
                currentId = userData?.userId ?: ""
            }
            albumPickViewModel.currentType = currentType
            albumPickViewModel.currentId = currentId
            scrollStateKey = page.key
            albumPickViewModel.setDefaultLifecycleOwner(LocalLifecycleOwner.current)
            user = userData
            pet = petData
        }
    }

    fun onEdit(){
        isEdit = true
        viewModel.currentPage.value?.isGoBackAble = false

    }
    fun onEdited(){
        isEdit = false
        viewModel.currentPage.value?.isGoBackAble = true
    }
    goBackPage.value?.let {
        viewModel.goBackCompleted()
        if (isEdit) onEdited()
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
                TitleTabButtonType.Back -> {
                    if (isEdit) onEdited()
                    else pagePresenter.goBack()
                }
                TitleTabButtonType.AddAlbum -> albumPickViewModel.onPick()
                TitleTabButtonType.Setting -> onEdit()
                else -> {}
            }
        }
        user?.let {
            val scrollState: LazyListState = rememberForeverLazyListState(key = scrollStateKey)
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
