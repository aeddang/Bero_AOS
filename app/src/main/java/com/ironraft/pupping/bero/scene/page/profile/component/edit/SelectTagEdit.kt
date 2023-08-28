package com.ironraft.pupping.bero.scene.page.profile.component.edit

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditData
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.ComponentViewModel
import com.lib.util.toggle
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject
import java.util.HashMap


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectTagEdit(
    prevData:String = "",
    type: ProfileEditType,
    edit: (ProfileEditData) -> Unit
) {
    val appTag = "SelectTagEditt"
    val dataProvider:DataProvider = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    var currentSelect:String by remember { mutableStateOf(prevData) }
    var buttons:List<RadioBtnData> by remember { mutableStateOf(listOf()) }

    fun getCodeData():Boolean{
        val params = HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Personality.name.lowercase()
        val q = ApiQ(appTag, ApiType.GetCode, query = params, requestData = CodeCategory.Personality)
        dataProvider.requestData(q)
        return true
    }

    val isInit by remember { mutableStateOf(getCodeData()) }

    val apiResult = dataProvider.result.observeAsState()

    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
        if (res.requestData != CodeCategory.Personality) return@let
        when ( res.type ){
            ApiType.GetCode -> {
                (res.data as? List<CodeData>)?.let { datas ->
                    val selects = PetProfile.exchangeStringToList(prevData)
                    val btns = datas.mapIndexed { index, codeData ->
                        val id = codeData.id.toString()
                        RadioBtnData(
                            title = codeData.value ?: "",
                            value = id,
                            isSelected = selects.find { it == id } != null,
                            index = index )
                    }
                    buttons = btns
                }
                dataProvider.clearResult()
            }
            else ->{}
        }
    }


    fun onSelected(){
        if (buttons.isEmpty()) { return }
        val selects:String = buttons.filter{ it.isSelected }.map{it.value ?: ""}.reduce{ acc, s ->
            "$acc,$s"
        }
        currentSelect = selects
    }
    fun onAction(){
        if (prevData == currentSelect) return
        when (type){
            ProfileEditType.Hash -> edit(
                ProfileEditData(hashStatus = currentSelect)
            )
            else -> {}
        }
    }
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
            ,
            contentAlignment = Alignment.Center
        ) {
            if(buttons.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(bottom = DimenMargin.regular.dp),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
                    ) {
                        item {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
                            ) {
                                buttons.forEach { btn ->
                                    var isCheck by remember { mutableStateOf(btn.isSelected) }
                                    SortButton(
                                        type = if (isCheck) SortButtonType.Fill else SortButtonType.Stroke,
                                        sizeType = SortButtonSizeType.Big,
                                        text = btn.title,
                                        color = if (isCheck) ColorBrand.primary else ColorApp.gray400,
                                        isSort = false,
                                        modifier = Modifier.padding(bottom = DimenMargin.regular.dp)
                                    ) {
                                        isCheck = isCheck.toggle()
                                        btn.isSelected = isCheck
                                        onSelected()
                                    }
                                }
                            }
                        }
                    }
                    FillButton(
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_save),
                        color = ColorBrand.primary,
                        isActive = prevData != currentSelect
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
fun SelectTagEditComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectTagEdit(
            type = ProfileEditType.Hash
        ){
            
        }
    }
}