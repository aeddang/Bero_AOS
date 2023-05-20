package com.ironraft.pupping.bero.scene.page.user.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileInfo
import com.ironraft.pupping.bero.scene.component.item.UserProfileInfo
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PagePresenter
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.util.replace

@Composable
fun UserDogsSection(
    modifier: Modifier = Modifier,
    user:User
) {
    val pagePresenter:PagePresenter = get()
    val hasRepresentative by remember { mutableStateOf(user.representativePet.value != null) }
    val pets:List<PetProfile> by remember { mutableStateOf( user.pets) }
    val me:UserProfile? by remember { mutableStateOf(user.currentProfile) }


    fun movePetPag(profile:PetProfile){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Dog)
                .addParam(key = PageParam.data, value = profile)
                .addParam(key = PageParam.subData, value = user)
        )
    }
    val scrollState: ScrollState = rememberScrollState()
    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                type = TitleTabType.Section,
                title = stringResource(id = R.string.pageTitle_usersDogs).replace(user.representativeName),
            )
            if (pets.isEmpty()){
                EmptyItem(
                    type = EmptyItemType.MyList,
                    modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = DimenApp.pageHorinzontal.dp)
                    ,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.tiny.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(hasRepresentative) {
                        me?.let {
                            UserProfileInfo(
                                profile = it,
                                sizeType = HorizontalProfileSizeType.Big,
                                modifier = Modifier.width(DimenItem.petList.dp)
                            ) {
                                /*
                                pagePresenter.openPopup(
                                    PageProvider.getPageObject(.modifyUser)
                                )*/
                            }
                        }
                    }
                    pets.filter { !it.isRepresentative }.forEach {
                        PetProfileInfo(
                            profile = it,
                            modifier = Modifier.width(DimenItem.petList.dp)
                        ) {
                            movePetPag(it)
                        }
                    }
                }
            }
        }
    }
}
