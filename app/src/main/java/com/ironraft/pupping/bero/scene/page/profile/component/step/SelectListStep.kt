package com.ironraft.pupping.bero.scene.page.profile.component.step

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.theme.*
import com.skeleton.view.button.*
import org.koin.compose.koinInject

internal class SelectListStepData{
    var isSearching = false
}

@Composable
fun SelectListStep(
    profile:ModifyPetProfileData?,
    focusRequester :FocusRequester? = null,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {
    val appTag = "InputTextStep"
    val dataProvider = koinInject<DataProvider>()
    val viewData: SelectListStepData  by remember { mutableStateOf(SelectListStepData()) }
    var btnType:RadioButtonType by remember { mutableStateOf(RadioButtonType.Blank)}

    fun getPrevData() : RadioBtnData?{
        return when (step){
            PageAddDogStep.Breed -> {
                btnType = RadioButtonType.Blank
                profile?.breed?.let {
                    return RadioBtnData(title = it, value = it, index = -1)
                }
            }
            else -> null
        }
    }

    var keyword:String by remember { mutableStateOf("") }
    var isSearch:Boolean by remember { mutableStateOf(false) }
    var selectData:RadioBtnData? by remember { mutableStateOf(getPrevData()) }
    var buttons:List<RadioBtnData> by remember { mutableStateOf(listOf()) }
    val scrollState:ScrollState = rememberScrollState()

    val apiResult = dataProvider.result.observeAsState()
    val apiError = dataProvider.error.observeAsState()

    @Suppress("UNCHECKED_CAST")
    apiResult.value.let { res ->
        res?.type ?: return@let
        if (res.requestData != CodeCategory.Breed) return@let
        when ( res.type ){
            ApiType.GetCode -> {
                (res.data as? List<CodeData>)?.let { datas ->
                    buttons = datas.mapIndexed { index, codeData ->
                        RadioBtnData(title = codeData.value ?: "", value = codeData.id.toString(), index = index )
                    }
                }
                viewData.isSearching = false
            }
            else ->{}
        }
    }
    apiError.value.let { err ->
        err?.type ?: return@let
        if (err.requestData != CodeCategory.Breed) return@let
        when ( err.type ){
            ApiType.GetCode -> viewData.isSearching = false
            else ->{}
        }
    }
    val focusManager = LocalFocusManager.current

    fun onSelected(btn:RadioBtnData, isSelect:Boolean) {
        selectData = if (isSelect) btn else null
    }
    fun onAction(){
        if (selectData == null) return
        when (step){
            PageAddDogStep.Breed -> next(ModifyPetProfileData(breed = selectData?.value))
            else -> {}
        }
    }
    fun onSearch(){
        if (viewData.isSearching) return
        if (keyword.isEmpty()) {
            if (buttons.isNotEmpty()) buttons = listOf()
            return
        }
        viewData.isSearching = true
        val params = HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Breed.name.lowercase()
        params[ApiField.searchText] = keyword
        val q = ApiQ(appTag, ApiType.GetCode, query = params, isOptional = true, requestData = CodeCategory.Breed)
        dataProvider.requestData(q)
    }

    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() }
                    )
            },
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            isSearch = it.isFocused
                        },
                    placeholder = {
                        Text(
                            step.placeHolder,
                            fontSize = FontSize.light.sp,
                            color = ColorApp.grey200
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onAction()
                        }
                    ),
                    trailingIcon = {
                        Image(
                            painterResource(R.drawable.search),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if (keyword.isEmpty()) ColorApp.grey200 else ColorBrand.primary),
                            modifier = Modifier.size(DimenIcon.light.dp)
                        )
                    },
                    value = keyword,
                    onValueChange = {
                        keyword = it
                        onSearch()
                    },

                    shape = RoundedCornerShape(DimenRadius.thin.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = ColorApp.white,
                        cursorColor = ColorBrand.primary,
                        focusedBorderColor = ColorBrand.primary,
                        unfocusedBorderColor = ColorApp.grey200
                    ),
                    singleLine = true
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(btnType.spacing.dp)
                ) {
                    buttons.forEach { btn ->
                        RadioButton(
                            type = btnType,
                            isChecked = selectData?.value == btn.value ,
                            text = btn.title){
                            focusManager.clearFocus()
                            onSelected(btn, it)
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.tinyExtra.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!step.isFirst) {
                        FillButton(
                            modifier = Modifier.weight(1.0f),
                            type = FillButtonType.Fill,
                            text = stringResource(id = R.string.button_prev),
                            color = ColorApp.black
                        ) {
                            prev()
                        }
                    }
                    FillButton(
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_next),
                        color = ColorBrand.primary,
                        isActive = selectData != null
                    ) {
                        onAction()
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun SelectListStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectListStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Breed,
            next = {

            },
            prev = {

            }
        )
    }
}