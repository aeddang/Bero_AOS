package com.ironraft.pupping.bero.scene.page.my
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.item.PetProfileTopInfo
import com.ironraft.pupping.bero.scene.component.item.UserProfileTopInfo
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.component.AlbumSection
import com.ironraft.pupping.bero.scene.page.component.FriendSection
import com.ironraft.pupping.bero.scene.page.my.component.MyDogsSection
import com.ironraft.pupping.bero.scene.page.my.component.MyHistorySection
import com.ironraft.pupping.bero.scene.page.component.UserPlayInfo
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.toDp
import com.skeleton.component.item.ValueInfoType
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get

@Composable
fun PageMy(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = "PageMy"

    val screenWidth = LocalConfiguration.current.screenWidthDp
    fun getListWidth(): Float {
        val margin = DimenApp.pageHorinzontal * 2.0f
        return screenWidth.toFloat() - margin
    }
    val listWidth: Float by remember { mutableStateOf( getListWidth() ) }

    val pagePresenter:PageComposePresenter = get()
    val dataProvider:DataProvider = get()

    val representativePet by dataProvider.user.representativePet.observeAsState()

    val scrollState: ScrollState = rememberScrollState()
    if (scrollState.isScrollInProgress){
        println("scrolling")
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
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
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(scrollState)
                .padding(
                    top = DimenMargin.medium.dp,
                    bottom = (DimenApp.bottom + DimenMargin.heavyExtra).dp
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            if (representativePet != null){
                PetProfileTopInfo(
                    modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                    profile = representativePet!!,
                    viewProfile = {
                        /*
                        pagePresenter.openPopup(
                            PageProvider.getPageObject(.dog)
                            .addParam(key: .data, value: pet)
                            .addParam(key: .subData, value: self.dataProvider.user)
                        )
                        */
                    }
                )
            } else {
                UserProfileTopInfo(
                    modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                    profile = dataProvider.user.currentProfile){
                        /*
                        pagePresenter.openPopup(
                            PageProvider.getPageObject(.modifyUser)
                        )
                        */
                }
            }
            UserPlayInfo(
                modifier = Modifier
                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                    .padding(top = DimenMargin.regular.dp)
            ){ data ->
                when(data.valueType){
                    ValueInfoType.Point -> {
                        /*
                        pagePresenter.openPopup(
                            PageProvider.getPageObject(. myPoint)
                        )
                        */
                    }
                    ValueInfoType.Lv -> {
                        /*
                        pagePresenter.openPopup(
                            PageProvider.getPageObject(.myLv)
                        )
                        */
                    }
                    else -> {}
                }
            }
            Spacer(modifier = Modifier
                .padding(top = DimenMargin.medium.dp)
                .fillMaxWidth()
                .height(DimenLine.heavy.dp)
                .background(ColorApp.grey50)
            )
            MyHistorySection(modifier
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                .padding(top = DimenMargin.regular.dp)
            )
            MyDogsSection(modifier
                .padding(top = DimenMargin.heavyExtra.dp)
            )
            FriendSection(modifier
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                .padding(top = DimenMargin.heavyExtra.dp),
                listSize = listWidth,
                user = dataProvider.user,
                isEdit = true
            )

            AlbumSection( modifier
                .padding(horizontal = DimenApp.pageHorinzontal.dp)
                .padding(top = DimenMargin.heavyExtra.dp),
                listSize = listWidth,
                user = dataProvider.user,
                pageSize = 2
            )
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
