package com.ironraft.pupping.bero.scene.page.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.profile.component.step.*
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.replace
import com.skeleton.component.progress.StepInfo
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import org.koin.compose.koinInject

enum class PageAddDogStep{
    Name, Picture, Gender, Birth, Breed, Hash;
    val description:String
        get() = when(this) {
            Name -> "Tell us your beloved dog's name!"
            Picture -> "Select your favorite photo of %s!"
            Gender -> "What is %s’s gender?"
            Birth -> "When is %s’s birthday?"
            Breed -> "Find %s’s breed!"
            //case .immun -> "Health & Immunization"
            Hash -> "Share %s’s Personality."
            //case .identify ->  "Identify %s."
        }
    val caption:String?
        get() = when(this) {
            Birth -> "If you don’t know the exact birthday put your best guess."
            Hash -> "Choose all the tags that related with %s."
            else -> null
        }


    val inputDescription:String?
        get() = when(this) {
            //case .identify : return "An animal ID is consisted of 15 digits.\nTake your pet to be scanned at the local vet, rescue centre or dog wardens service."
            else -> null
        }

    val limitedTextLength:Int
        get() = when(this) {
            Name -> 20
            else -> 100
        }

    val keyboardType:KeyboardType
        get() = when(this) {
            //case .identify: return .numberPad
            else -> KeyboardType.Text
        }


    val autocapitalizationType: KeyboardCapitalization
        get() = when(this) {
            Name -> KeyboardCapitalization.Characters
            else -> KeyboardCapitalization.Words
        }

    val placeHolder:String
        get() = when(this) {
            Name -> "ex. Bero"
            //Identify -> "ex) 123456789"
            Breed -> "Search breed"
            else -> ""
        }

    val isFirst:Boolean
        get() = when(this) {
            Name -> true
            else -> false
        }

    val isSkipAble:Boolean
        get() = when(this) {
            //case .identify : return true
            else -> false
        }

}


@Composable
fun PageAddDog(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = "PageAddDog"
    val pagePresenter = koinInject<PageComposePresenter>()
    var profile:ModifyPetProfileData by remember { mutableStateOf(ModifyPetProfileData()) }
    var currentStep:PageAddDogStep by remember { mutableStateOf(PageAddDogStep.Name) }
    var currentCount:Int by remember { mutableStateOf(0) }
    val totalCount:Int = PageAddDogStep.values().count()

    fun onCompleted(){
        pagePresenter.changePage(
            PageProvider.getPageObject(PageID.AddDogCompleted)
                .addParam(PageParam.data, profile)
        )
        /*
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.AddDogCompleted)
            .addParam(PageParam.data, profile)
        )*/
    }
    fun onPrevStep(){
        val wiilStep = currentCount - 1
        if (wiilStep < 0) {
            pagePresenter.goBack()
            return
        }
        currentCount = wiilStep
        currentStep = PageAddDogStep.values()[wiilStep]
    }
    fun onNextStep(updateProfile:ModifyPetProfileData? = null){
        updateProfile?.let {
            profile = profile.update(it)
        }
        val wiilStep = currentCount + 1
        if (wiilStep == totalCount) {
            onCompleted()
            return
        }
        currentCount = wiilStep
        currentStep = PageAddDogStep.values()[wiilStep]
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DimenApp.pageHorinzontal.dp)
            .padding(bottom = DimenMargin.regular.dp)
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_addDog),
            alignment = TextAlign.Center,
            margin = 0.0f,
            buttons = arrayListOf(TitleTabButtonType.Close)
        ){
            when(it){
                TitleTabButtonType.Close -> {
                    pagePresenter.closePopup(key = page?.key)
                }
                else -> {}
            }
        }
        StepInfo(index = currentCount , total = totalCount,
            image = profile.image,
            info = currentStep.description.replace(profile.name ?: "")
        )
        when (currentStep){
            PageAddDogStep.Name ->
                InputTextStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
            PageAddDogStep.Picture ->
                SelectPictureStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
            PageAddDogStep.Gender ->
                SelectGenderStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
            PageAddDogStep.Birth ->
                SelectDateStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
            PageAddDogStep.Breed ->
                SelectListStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
            PageAddDogStep.Hash ->
                SelectTagStep(profile = profile, step = currentStep,
                    prev = {onPrevStep()},
                    next ={onNextStep(it)}
                )
        }
    }
}
@Preview
@Composable
fun PageAddDogPreview(){
    PageAddDog(
    )
}
