package com.ironraft.pupping.bero.scene.component.list

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.isScrolledToEnd
import com.lib.util.showCustomToast
import com.lib.util.toggle
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get


enum class AlbumListType {
    Detail, Normal;
    companion object{
        val row:Int = if(SystemEnvironment.isTablet) 4 else 2
    }
    val row:Int
        get() = when (this) {
            Detail -> 1
            else -> AlbumListType.row
        }
    val marginRow:Float
        get() = when (this) {
            Detail -> 0.0f
            else -> DimenMargin.regularExtra
        }
    val marginHorizontal: Float
        get() = when (this) {
            Detail -> 0.0f
            else -> DimenApp.pageHorinzontal
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlbumList(
    modifier: Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    type:AlbumListType = AlbumListType.Normal,
    user:User? = null,
    pet:PetProfile? = null,
    listSize:Float = 300.0f,
    marginBottom:Float = DimenMargin.medium,
    isEdit:Boolean = false
) {
    val owner = LocalLifecycleOwner.current
    val appTag = "AlbumList"
    val repository: PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: AlbumListViewModel by remember { mutableStateOf(
        AlbumListViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE )
    )}

    fun getType(): AlbumCategory {
        pet?.petId?.let { return AlbumCategory.Pet }
        return AlbumCategory.User
    }

    fun getId(): String {
        pet?.petId?.let { return it.toString() }
        return user?.userId ?: ""
    }
    fun updateAlbum(): Size {
        viewModel.currentId = getId()
        viewModel.currentType = getType()
        viewModel.reset()
        viewModel.load()
        val r: Float = type.row.toFloat()
        val w = (listSize - (DimenMargin.regularExtra * (r - 1)) - (type.marginHorizontal * 2)) / r
        return Size(w, w * DimenItem.albumList.height / DimenItem.albumList.width)
    }

    val isEmpty = viewModel.isEmpty.observeAsState()
    val albums by viewModel.listDatas.observeAsState()
    val albumSize:Size by remember { mutableStateOf(updateAlbum()) }
    var isCheckAll:Boolean by remember { mutableStateOf(false) }

    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) { viewModel.continueLoad() }

    AppTheme {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp)
        ) {
            if (isEmpty.value == true) EmptyItem(type = EmptyItemType.MyList)
            else if(albums != null)
                albums?.let { datas->
                    LazyColumn(
                        modifier = Modifier.weight(1.0f),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        contentPadding = PaddingValues(bottom = marginBottom.dp)
                    ) {
                        items(datas, key = {it.index}) {data ->
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(type.marginRow.dp)
                            ) {

                                AlbumListItem(
                                    type = type,
                                    data = data,
                                    user = user,
                                    pet = pet,
                                    imgSize = albumSize,
                                    isEdit = isEdit
                                )

                            }
                        }
                    }
                }
            else 
                Spacer(modifier = Modifier.fillMaxSize())
            AnimatedVisibility(visible = isEdit) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = DimenApp.pageHorinzontal.dp,
                        vertical = DimenMargin.thin.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.micro.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FillButton(
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_checkAll),
                        color = ColorApp.black,
                        isActive = isCheckAll
                    ) {
                        isCheckAll = isCheckAll.toggle()
                        albums?.forEach { it.isDelete.value = isCheckAll }
                    }
                    FillButton(
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_delete),
                        color = ColorBrand.primary
                    ) {
                        if(!viewModel.deleteAll()){
                            Toast(pagePresenter.activity).showCustomToast(
                                R.string.alert_noItemsSelected,
                                pagePresenter.activity
                            )
                        }
                    }
                }
            }
        }
    }
}

