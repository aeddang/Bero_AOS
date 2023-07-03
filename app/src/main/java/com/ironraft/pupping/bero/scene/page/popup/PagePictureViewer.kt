package com.ironraft.pupping.bero.scene.page.popup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.button.LikeButton
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumFunctionViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageAnimationType
import com.lib.page.PageComposePresenter
import com.lib.util.toggle
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.ImageButton
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@Composable
fun PagePictureViewer(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.Album, repository).initSetup(owner)) }
    val albumFunctionViewModel: AlbumFunctionViewModel by remember { mutableStateOf(
        AlbumFunctionViewModel(repository).initSetup(owner)
    ) }

    val currentPage = viewModel.currentPage.observeAsState()

    var data:AlbumListItemData? by remember { mutableStateOf( null ) }

    var showUi:Boolean by remember { mutableStateOf( true ) }
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
        data = imageData
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
            .pointerInput(Unit) {
                detectTapGestures (
                    onTap = { showUi = showUi.toggle() }
                )
            }
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
        AnimatedVisibility(visible = showUi,enter = fadeIn(), exit = fadeOut()) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DimenApp.pageHorinzontal.dp,
                        vertical = DimenMargin.regular.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.Start

            ) {
                if (data == null) {
                    ImageButton(
                        defaultImage = R.drawable.back,
                        defaultColor = ColorApp.white
                    ) {
                        pagePresenter.goBack()
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                } else {
                    data?.let { data->
                        albumFunctionViewModel.lazySetup(data)
                        val isLike by data.isLike.observeAsState()
                        val likeCount by data.likeCount.observeAsState()
                        val isExpose by data.isExpose.observeAsState()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageButton(
                                defaultImage = R.drawable.back,
                                defaultColor = ColorApp.white
                            ) {
                                pagePresenter.goBack()
                            }
                            Spacer(modifier = Modifier.weight(1.0f))
                            LikeButton(
                                isLike = isLike ?: false,
                                likeCount = likeCount
                            ){
                                albumFunctionViewModel.updateLike(isLike?.toggle() ?: false)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1.0f))
                        if( user?.isMe == true) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1.0f))
                                SortButton(
                                    type = SortButtonType.Stroke,
                                    sizeType = SortButtonSizeType.Big,
                                    icon = R.drawable.global,
                                    text = stringResource(id = R.string.share),
                                    color = if (isExpose == true) ColorBrand.primary else ColorApp.grey400,
                                    isSort = false
                                ) {
                                    albumFunctionViewModel.updateExpose(isExpose?.toggle() ?: false)
                                }
                            }
                        }

                    }
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
