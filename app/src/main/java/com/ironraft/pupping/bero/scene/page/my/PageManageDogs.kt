package com.ironraft.pupping.bero.scene.page.my

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.item.PetProfileEditable
import com.ironraft.pupping.bero.scene.component.item.PetProfileEmpty
import com.ironraft.pupping.bero.scene.component.item.PetProfileInfo
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.lib.util.toggle
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenItem
import com.skeleton.theme.DimenLine
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.RadioButton
import com.skeleton.view.button.RadioButtonType
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject


@Composable
fun PageManageDogs(
    modifier: Modifier = Modifier
){
    val appTag = PageID.ManageDogs.value
    val dataProvider:DataProvider= get()
    val pagePresenter:PageComposePresenter = get()
    val walkManager:WalkManager = get()
    val appSceneObserver = koinInject<AppSceneObserver>()
    val scrollState: ScrollState = rememberForeverScrollState(key = appTag)

    val pepresentativePet by dataProvider.user.representativePet.observeAsState()
    var pets:List<PetProfile> by remember { mutableStateOf(dataProvider.user.pets) }
    fun setupDatas(){
        pets = dataProvider.user.pets
    }
    val userEvent = dataProvider.user.event.observeAsState()
    userEvent.value?.let { evt ->
        when(evt.type){
            UserEventType.AddedDog, UserEventType.DeletedDog -> setupDatas()
            else -> {}
        }
    }

    fun onSelect(pet:PetProfile){
        if (pet.petId == pepresentativePet?.petId) return
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_representativePetChangeConfirm),
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.cancel),
                pagePresenter.activity.getString(R.string.confirm)
            )
        ){
            if(it == 1){
                val q = ApiQ(appTag,
                    ApiType.ChangeRepresentativePet,
                    contentID = pet.petId.toString()
                )
                dataProvider.requestData(q)
            }
        }
    }
    fun onDelete(pet:PetProfile){
        val activity = pagePresenter.activity
        if(walkManager.status.value == WalkStatus.Walking){
            Toast(activity).showCustomToast(
                R.string.alert_walkDisableRemovePet,
                activity
            )
            return
        }
        if (pet.petId == pepresentativePet?.petId) {
            Toast(activity).showCustomToast(
                R.string.alert_representativeDisableRemovePet,
                activity
            )
            return
        }
        appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = pagePresenter.activity.getString(R.string.alert_deleteDogTitle).replace(pet.name.value ?: ""),
            text = pagePresenter.activity.getString(R.string.alert_deleteDogText),
            buttons = arrayListOf(
                pagePresenter.activity.getString(R.string.cancel),
                pagePresenter.activity.getString(R.string.alert_deleteConfirm)
            )
        ){
            if(it == 1){
                val q = ApiQ(appTag,
                    ApiType.DeletePet,
                    contentID = pet.petId.toString()
                )
                dataProvider.requestData(q)
            }
        }

    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            parentScrollState = scrollState,
            title = stringResource(id = R.string.button_manageDogs),
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
                    vertical = DimenMargin.medium.dp,
                    horizontal = DimenApp.pageHorinzontal.dp
                ),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
        ) {
            pets.forEach {pet->
                WrapTransparentButton({ onSelect(pet)}) {
                    PetProfileEditable(
                        profile = pet,
                        isSelected = pet.petId == pepresentativePet?.petId
                    ) {
                        if(it == null ) onSelect(pet)
                        else onDelete(pet)
                    }
                }
            }
            if (pets.count() < 3)
                PetProfileEmpty(
                    description = if(pets.isEmpty()) stringResource(id = R.string.addDogEmpty) else null
                ) {
                    pagePresenter.openPopup(PageProvider.getPageObject(PageID.AddDog))
                }
        }
    }
}

@Preview
@Composable
fun PageManageDogsPreview(){
    PageManageDogs(
    )
}
