package com.ironraft.pupping.bero.scene.page.my
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenMargin
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun PageMy(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = "PageMy"
    val pagePresenter:PageComposePresenter = get()
    val dataProvider:DataProvider = get()

    var representativePet = dataProvider.user.representativePet.observeAsState()
    val scroll: ScrollState = rememberScrollState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(horizontal = DimenApp.pageHorinzontal.dp)
            .padding(bottom = DimenMargin.regular.dp),
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
        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1.0f)
                .verticalScroll(scroll)
                .padding(
                    top = DimenMargin.medium.dp,
                    bottom = (DimenApp.bottom + DimenMargin.heavyExtra).dp
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {

        }

    }
}
@Preview
@Composable
fun PageMyPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageMy(
        )
    }
}
