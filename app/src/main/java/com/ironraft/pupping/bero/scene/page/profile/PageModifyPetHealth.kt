package com.ironraft.pupping.bero.scene.page.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.profile.component.edit.PetProfileEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.PetProfileHealthEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.PetProfilePictureEdit
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get


@Composable
fun PageModifyPetHealth(
    modifier: Modifier = Modifier
){
    val appTag = PageID.ModifyPet.value
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PagePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.ModifyPet, repository).initSetup(owner)) }

    val scrollState: ScrollState = rememberForeverScrollState(key = appTag)
    var profile:PetProfile? by remember { mutableStateOf( null ) }

    val currentPage = viewModel.currentPage.observeAsState()
    currentPage.value?.let { page->
        if(!viewModel.isInit){
            viewModel.isInit = true
            profile = page.getParamValue(PageParam.data) as? PetProfile
        }
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            modifier = Modifier
                .padding(top = DimenApp.pageHorinzontal.dp)
                .padding(horizontal = DimenApp.pageHorinzontal.dp),
            type = TitleTabType.Section,
            title = stringResource(id = R.string.pageTitle_dogProfile),
            alignment = TextAlign.Center,
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> pagePresenter.goBack()
                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(scrollState)
                .padding(
                    vertical = DimenMargin.medium.dp,
                    horizontal = DimenApp.pageHorinzontal.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp),
        ) {
            profile?.let {
                PetProfileHealthEdit(profile = it)
            }

        }

    }
}

@Preview
@Composable
fun PageModifyPetHealthPreview(){
    PageModifyPetHealth(
    )
}
