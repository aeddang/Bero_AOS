package com.ironraft.pupping.bero.scene.page.my.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileInfo
import com.ironraft.pupping.bero.scene.component.item.UserProfileInfo
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.api.rest.MissionApi
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserEvent
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.item.profile.*
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@Composable
fun MyDogsSection(
    modifier: Modifier = Modifier
) {
    val pagePresenter:PagePresenter = get()
    val dataProvider: DataProvider = get()

    var hasRepresentative by remember { mutableStateOf(dataProvider.user.representativePet.value != null) }
    var pets:List<PetProfile> by remember { mutableStateOf(dataProvider.user.pets) }
    var me:UserProfile? by remember { mutableStateOf(dataProvider.user.currentProfile) }

    fun setupDatas(){
        pets = dataProvider.user.pets
        me = dataProvider.user.currentProfile
        hasRepresentative = dataProvider.user.representativePet.value != null
    }
    val userEvent = dataProvider.user.event.observeAsState()
    userEvent.value?.let { evt ->
        when(evt.type){
            UserEventType.AddedDog, UserEventType.DeletedDog, UserEventType.UpdatedProfile -> setupDatas()
            else -> {}
        }
    }
    fun movePetPag(profile:PetProfile){

        pagePresenter.openPopup(
            PageProvider.getPageObject(.dog)
                .addParam(key: .data, value: profile)
                .addParam(key: .subData, value: self.dataProvider.user)
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
                title = stringResource(id = R.string.pageTitle_myDogs),
                buttons = arrayListOf(TitleTabButtonType.ManageDogs)
            ){
                when(it){
                    TitleTabButtonType.ManageDogs -> {
                        //pagePresenter.openPopup(PageProvider.getPageObject(.manageDogs))
                    }
                    else ->{}
                }
            }
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
