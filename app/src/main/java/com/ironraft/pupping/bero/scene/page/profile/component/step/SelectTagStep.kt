package com.ironraft.pupping.bero.scene.page.profile.component.step

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectTagStep(
    profile:ModifyPetProfileData?,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {
    val appTag = "SelectTagStep"
    val dataProvider = koinInject<DataProvider>()
    fun getCodeData(){
        val params = HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Personality.name.lowercase()
        val q = ApiQ(appTag, ApiType.GetCode, query = params, requestData = CodeCategory.Personality)
        dataProvider.requestData(q)
    }
    fun getPrevData() : List<String>?{
        return when (step){
            PageAddDogStep.Hash -> {
                getCodeData()
                profile?.hashStatus ?: return null
                return PetProfile.exchangeStringToList(profile.hashStatus)
            }
            else -> null
        }
    }

    var selects:List<String> by remember { mutableStateOf(getPrevData() ?: listOf()) }
    var buttons:List<RadioBtnData> by remember { mutableStateOf(listOf()) }
    val apiResult = dataProvider.result.observeAsState()


    @Suppress("UNCHECKED_CAST")
    apiResult.value.let { res ->
        res?.type ?: return@let
        if (res.requestData != CodeCategory.Personality) return@let
        when ( res.type ){
            ApiType.GetCode -> {
                (res.data as? List<CodeData>)?.let { datas ->
                    buttons = datas.mapIndexed { index, codeData ->
                        RadioBtnData(title = codeData.value ?: "", value = codeData.id.toString(), index = index )
                    }
                }
            }
            else ->{}
        }
    }

    fun onSelected(btn:RadioBtnData, isSelect:Boolean) {
        btn.value?.let { value ->
            if(isSelect) {
                selects = selects.plus(value)
            } else {
                selects = selects.filter { it != value }
            }
        }
    }
    fun onAction(){
        //if (selects.isEmpty()) return
        when (step){
            PageAddDogStep.Hash -> next(ModifyPetProfileData(
                hashStatus = PetProfile.exchangeListToString(selects)))
            else -> {}
        }
    }

    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp)
                ) {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
                        ) {
                            buttons.forEach { btn ->
                                SortButton(
                                    type = if (selects.indexOf(btn.value) == -1) SortButtonType.Stroke else SortButtonType.Fill,
                                    sizeType = SortButtonSizeType.Big,
                                    text = btn.title,
                                    color = if (selects.indexOf(btn.value) == -1) ColorApp.grey400 else ColorBrand.primary,
                                    isSort = false,
                                    modifier = Modifier.padding(bottom = DimenMargin.regular.dp)
                                ) {
                                    val isSelect = !btn.isSelected
                                    btn.isSelected = isSelect
                                    onSelected(btn, isSelect)
                                }
                            }
                        }
                    }
                }
                /*
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(vertical = DimenMargin.regular.dp, horizontal = DimenMargin.thin.dp)
                ) {
                    items(buttons) { btn ->
                        SortButton(
                            type = if (selects.indexOf(btn.value) == -1) SortButtonType.Stroke else SortButtonType.Fill,
                            sizeType = SortButtonSizeType.Big,
                            text = btn.title,
                            color = if (selects.indexOf(btn.value) == -1) ColorApp.grey400 else ColorBrand.primary,
                            isSort = false
                        ) {
                            val isSelect = !btn.isSelected
                            btn.isSelected = isSelect
                            onSelected(btn, isSelect)
                        }
                    }
                }
                */
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
                        isActive = selects.isNotEmpty()
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
fun SelectTagStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectTagStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Hash,
            next = {

            },
            prev = {

            }
        )
    }
}