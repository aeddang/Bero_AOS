package com.ironraft.pupping.bero.scene.page.profile

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ironraft.pupping.bero.store.provider.model.Gender
import java.time.LocalDate
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.profile.component.edit.InputTextEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.SelectDateEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.SelectGenderEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.SelectListEdit
import com.ironraft.pupping.bero.scene.page.profile.component.edit.SelectTagEdit
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.ModifyUserProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.get
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

enum class ProfileEditType {
    Name, Gender, Birth, Introduction, Weight, Height, Immun, Hash, AnimalId, Microchip;
    val title: String
        get() = when (this) {
            Name -> "Edit Name"
            Gender -> "Edit Gender"
            Birth -> "Edit Age"
            Introduction ->  "Edit Introduction"
            Weight -> "Edit Weight"
            Height -> "Edit Height"
            Immun -> "Edit Immunization"
            Hash -> "Edit Tags"
            AnimalId -> "Edit Animal ID"
            Microchip -> "Edit microchip"
        }

    val caption: String?
        get() = when (this) {
            Name -> "Name"
            Weight -> "Weight (kg)"
            Height -> "Height (cm)"
            Birth -> "Select your birthday"
            Immun -> "Select all that applies"
            AnimalId -> "Animal ID"
            Microchip -> "Microchip"
            else -> null
        }


    val placeHolder: String
        get() = when (this) {
            Name -> "ex. Bero"
            AnimalId -> "ex) 123456789012345"
            Microchip -> "ex) 123456789"
            else -> ""
        }
    val limitLine: Int
        get() = when (this) {
            Introduction -> 5
            else -> 1
        }
    val limitLength: Int
        get() = when (this) {
            Introduction -> 100
            Weight, Height -> 10
            AnimalId -> 15
            Microchip -> 9
            else -> 20
        }

    val keyboardOptions: KeyboardOptions
        get() = when (this) {
            Name -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
            Microchip, AnimalId  -> KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            Weight, Height  -> KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
            Introduction -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            )
            else -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        }
}
data class ProfileEditData (
    var name:String? = null,
    var gender:Gender? = null,
    var isNeutralized:Boolean? = null,
    var birth:LocalDate? = null,
    var introduction:String? = null,
    var microchip:String? = null,
    var animalId:String? = null,
    var immunStatus:String? = null,
    var hashStatus:String? = null,
    var weight:Double? = null,
    var size:Double? = null
)





@Composable
fun PageEditProfile(
    modifier: Modifier = Modifier
){
    val appTag = PageID.EditProfile.value
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val dataProvider:DataProvider = get()
    val pagePresenter:PagePresenter = get()
    val viewModel:PageViewModel by remember { mutableStateOf(PageViewModel(PageID.EditProfile, repository).initSetup(owner)) }

    var currentType:ProfileEditType? by remember { mutableStateOf( null ) }
    var user:User? by remember { mutableStateOf( null ) }
    var profile:PetProfile? by remember { mutableStateOf( null ) }
    var needAgree:Boolean by remember { mutableStateOf( false ) }
    var name:String by remember { mutableStateOf( "" ) }
    var weight:String by remember { mutableStateOf( "" ) }
    var height:String by remember { mutableStateOf( "" ) }
    var birth:LocalDate by remember { mutableStateOf( LocalDate.now() ) }
    var gender:Gender? by remember { mutableStateOf( null ) }
    var isNeutralized:Boolean by remember { mutableStateOf( false ) }
    var introduction:String by remember { mutableStateOf( "" ) }
    var immunStatus:String by remember { mutableStateOf( "" ) }
    var hashStatus:String by remember { mutableStateOf( "" ) }
    var microchip:String by remember { mutableStateOf( "" ) }
    var animalId:String by remember { mutableStateOf( "" ) }

    val currentPage = viewModel.currentPage.observeAsState()
    currentPage.value?.let { page->
        if(!viewModel.isInit){
            viewModel.isInit = true
            profile = page.getParamValue(PageParam.data) as? PetProfile
            if (profile == null) user = dataProvider.user

            profile?.let { profile->
                name = profile.name.value ?: ""
                birth = profile.birth.value ?: LocalDate.now()
                gender = profile.gender.value
                introduction = profile.getIntroduction(LocalContext.current)
                weight = profile.weight.value?.toString() ?: ""
                height = profile.size.value?.toString() ?: ""
                immunStatus = profile.immunStatus.value ?: ""
                hashStatus = profile.hashStatus.value ?: ""
                microchip = profile.microchip.value ?: ""
                animalId = profile.animalId.value ?: ""
                isNeutralized = profile.isNeutralized.value ?: false
                needAgree = false
            }
            user?.let {user->
                name = user.currentProfile.nickName.value ?: ""
                birth = user.currentProfile.birth.value ?: LocalDate.now()
                gender = user.currentProfile.gender.value
                introduction = user.currentProfile.getIntroduction(LocalContext.current)
                needAgree = true
            }

            currentType = page.getParamValue(PageParam.type) as? ProfileEditType
        }
    }

    fun onEdit(data:ProfileEditData){
        user?.snsUser?.let {
            val modifyData = ModifyUserProfileData(
                nickName = data.name,
                gender = data.gender,
                birth = data.birth,
                introduction = data.introduction
            )
            val q = ApiQ(appTag,
                ApiType.UpdateUser,
                contentID = it.snsID,
                requestData = modifyData)
            dataProvider.requestData(q)
            pagePresenter.goBack()
            return
        }
        profile?.let {
            val modifyData = ModifyPetProfileData(
                name = data.name,
                gender = data.gender,
                isNeutralized = if(data.gender != null) data.isNeutralized else null,
                birth = data.birth,
                microchip = data.microchip,
                animalId = data.animalId,
                immunStatus = data.immunStatus,
                hashStatus = data.hashStatus,
                introduction = data.introduction,
                weight = data.weight,
                size = data.size
            )
            val q = ApiQ(appTag,
                ApiType.UpdatePet,
                contentID = it.petId.toString(),
                requestData = modifyData)
            dataProvider.requestData(q)
            pagePresenter.goBack()
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(top = DimenApp.pageHorinzontal.dp)
            .padding(horizontal = DimenApp.pageHorinzontal.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
    ) {
        currentType?.let { type->
            TitleTab(
                type = TitleTabType.Section,
                title = type.title,
                alignment = TextAlign.Center,
                useBack = true
            ){
                when(it){
                    TitleTabButtonType.Back -> pagePresenter.goBack()
                    else -> {}
                }
            }
            when (type){
                ProfileEditType.Name ->
                    InputTextEdit(type = type, prevData = name, needAgree = needAgree ){
                        onEdit(it)
                    }
                ProfileEditType.Microchip ->
                    InputTextEdit(type = type, prevData = microchip, needAgree = false ){
                        onEdit(it)
                    }
                ProfileEditType.AnimalId ->
                    InputTextEdit(type = type, prevData = animalId, needAgree = false ){
                        onEdit(it)
                    }
                ProfileEditType.Weight ->
                    InputTextEdit(type = type, prevData = weight, needAgree = false ){
                        onEdit(it)
                    }
                ProfileEditType.Height ->
                    InputTextEdit(type = type, prevData = height, needAgree = false ){
                        onEdit(it)
                    }
                ProfileEditType.Gender ->
                    SelectGenderEdit(type = type, prevData = gender, needAgree = needAgree ){
                        onEdit(it)
                    }
                ProfileEditType.Birth ->
                    SelectDateEdit(type = type, prevData = birth, needAgree = needAgree ){
                        onEdit(it)
                    }

                ProfileEditType.Introduction ->
                    InputTextEdit(type = type, prevData = introduction ){
                        onEdit(it)
                    }

                ProfileEditType.Immun ->
                    SelectListEdit(type = type, prevData = immunStatus ){
                        onEdit(it)
                    }
                ProfileEditType.Hash ->
                    SelectTagEdit(type = type, prevData = hashStatus ){
                        onEdit(it)
                    }
            }

        }
    }
}

@Preview
@Composable
fun PageEditProfilePreview(){
    PageEditProfile(
    )
}
