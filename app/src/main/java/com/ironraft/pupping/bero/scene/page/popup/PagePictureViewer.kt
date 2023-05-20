package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.scene.component.item.UserProfileItem
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.skeleton.component.item.profile.HorizontalProfileType
import com.skeleton.theme.ColorApp
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.ImageButton
import dev.burnoo.cokoin.get


@Composable
fun PagePictureViewer(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Album, repository).initSetup(owner)) }
    val currentPage = viewModel.currentPage.observeAsState()

    var imagePath:String? by remember { mutableStateOf( null ) }
    var title:String? by remember { mutableStateOf( null ) }
    var user:User? by remember { mutableStateOf( null ) }

    currentPage.value?.let { page ->
        if(imagePath != null) return@let
        val imageData = page.getParamValue(PageParam.data) as? AlbumListItemData
        imagePath = if(imageData == null) page.getParamValue(PageParam.data) as? String
        else imageData.imagePath
        title = page.getParamValue(PageParam.title) as? String
        user = page.getParamValue(PageParam.userData) as? User

    }

    fun moveUser(user:User){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.User)
                .addParam(PageParam.data, user)
        )
    }

    Box (
        modifier = modifier
            .fillMaxSize()
            .background(ColorApp.black)
    ) {
        if (imagePath == null) {
            Image(
                painterResource(R.drawable.noimage_1_1),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        imagePath?.let {
            val painter = rememberAsyncImagePainter( it,
                placeholder = painterResource(R.drawable.noimage_1_1),
                error = painterResource(R.drawable.noimage_1_1)
            )
            Image(
                painter,
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DimenMargin.regular.dp)
                .padding(
                    horizontal = DimenApp.pageHorinzontal.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Start

        ) {
            ImageButton(
                defaultImage = R.drawable.back,
                defaultColor = ColorApp.white
            ){
                pagePresenter.goBack()
            }
            Spacer(modifier = Modifier.weight(1.0f))
            user?.let {
                UserProfileItem(
                    profile = it.currentProfile,
                    type = HorizontalProfileType.Pet,
                    title = it.representativeName,
                    lv = it.lv,
                    imagePath = it.representativeImage,
                ){
                    moveUser(it)
                }
            }
        }
    }
}

@Preview
@Composable
fun PagePictureViewerPreview(){
    PagePictureViewer(
    )
}
