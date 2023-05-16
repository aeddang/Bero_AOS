package com.ironraft.pupping.bero.scene.page.pet.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.api.rest.MissionApi
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserEvent
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.lib.page.ComponentViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.item.profile.*
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetTagSection(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    title:String? = stringResource(id = R.string.pageTitle_tag)
) {
    val appTag = "PetTagSection"
    val pagePresenter: PagePresenter = get()
    val dataProvider: DataProvider = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    fun getCodeData():List<RadioBtnData>{
        val params = HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Personality.name.lowercase()
        val q = ApiQ(appTag, ApiType.GetCode, query = params, requestData = CodeCategory.Personality)
        dataProvider.requestData(q)
        return listOf()
    }

    val hashStatus = profile.hashStatus.observeAsState()
    var buttons:List<RadioBtnData> by remember { mutableStateOf(getCodeData()) }
    val apiResult = dataProvider.result.observeAsState()

    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
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

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            title?.let {
                TitleTab(
                    type = TitleTabType.Section,
                    title = it,
                    buttons = if(profile.isMypet) arrayListOf(TitleTabButtonType.Edit) else arrayListOf()
                ){ type ->
                    when (type){
                        TitleTabButtonType.Edit ->{
                            /*
                            self.pagePresenter.openPopup(
                            PageProvider.getPageObject(.editProfile)
                                .addParam(key: .data, value: self.profile)
                                .addParam(key: .type, value: PageEditProfile.EditType.hash)
                            )
                             */
                        }
                        else -> {}
                    }

                }
            }
            hashStatus.value?.let {status->
                val selects = PetProfile.exchangeStringToList(status)
                LazyColumn(
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp)
                ) {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
                        ) {
                            buttons.filter {d->
                                val id = d.value
                                val find = selects.find { id == it }
                                return@filter find != null
                            }.forEach { btn ->
                                SortButton(
                                    type = SortButtonType.StrokeFill,
                                    sizeType = SortButtonSizeType.Small,
                                    text = btn.title,
                                    color = ColorBrand.primary,
                                    isSort = false,
                                    modifier = Modifier.padding(bottom = DimenMargin.regular.dp)
                                ) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
