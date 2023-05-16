package com.ironraft.pupping.bero.scene.page.pet
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
import com.ironraft.pupping.bero.scene.page.pet.component.PetPhysicalSection
import com.ironraft.pupping.bero.scene.page.pet.component.PetTagSection
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.toDp
import com.skeleton.component.item.ValueInfoType
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get

@Composable
fun PageDog(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = "PageDog"
    val screenWidth = LocalConfiguration.current.screenWidthDp
    fun getListWidth(): Float {
        val margin = DimenApp.pageHorinzontal * 2.0f
        return screenWidth.toFloat() - margin
    }
    val listWidth: Float by remember { mutableStateOf( getListWidth() ) }
    val pagePresenter:PageComposePresenter = get()
    val dataProvider:DataProvider = get()

    var profile by remember { mutableStateOf( PetProfile() ) }
    val scrollState: ScrollState = rememberScrollState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
            parentScrollState = scrollState,
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
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
                    bottom = DimenMargin.heavyExtra.dp
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            PetProfileTopInfo(
                modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                profile = profile,
                viewProfileImage = {
                    /*
                    if profile.imagePath?.isEmpty == false {
                        self.pagePresenter.openPopup(
                            PageProvider.getPageObject(.pictureViewer)
                            .addParam(key: .data, value:profile.imagePath)
                        )
                    }
                     */
                },
                editProfile = {
                    /*
                    self.pagePresenter.openPopup(
                        PageProvider.getPageObject(.modifyPet)
                        .addParam(key: .data, value: profile)
                    )
                     */
                }
            )

            PetTagSection(
                modifier = Modifier
                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                    .padding(top = DimenMargin.regular.dp),
                profile = profile
            )
            PetPhysicalSection(
                modifier = Modifier
                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                    .padding(top = DimenMargin.heavyExtra.dp),
                profile = profile
            )
            Spacer(modifier = Modifier
                .padding(top = DimenMargin.medium.dp)
                .fillMaxWidth()
                .height(DimenLine.heavy.dp)
                .background(ColorApp.grey50)
            )
            AlbumSection(
                modifier
                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                    .padding(top = DimenMargin.heavyExtra.dp),
                listSize = listWidth,
                user = dataProvider.user,
                pet = profile
            )
        }

    }
}
@Preview
@Composable
fun PageDogPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageDog(
        )
    }
}
