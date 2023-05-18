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
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
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
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.ComponentViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.toDp
import com.skeleton.component.item.ValueInfoType
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlin.math.ceil
import kotlin.math.min

internal class PageDogViewModel:ComponentViewModel(){
    var fromUserPage:Boolean = false
    var currentPetId:Int = -1
    var currentUserId:String = ""
}

@Composable
fun PageDog(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = "PageDog"
    val pagePresenter:PageComposePresenter = get()
    val dataProvider:DataProvider = get()
    val viewModel:PageDogViewModel by remember { mutableStateOf(PageDogViewModel()) }
    val scrollState: ScrollState = rememberScrollState()

    val screenWidth = LocalConfiguration.current.screenWidthDp
    fun getListWidth(): Float {
        val margin = DimenApp.pageHorinzontal * 2.0f
        return screenWidth.toFloat() - margin
    }
    val listWidth: Float by remember { mutableStateOf( getListWidth() ) }
    var user:User? by remember { mutableStateOf( null ) }
    var profile:PetProfile? by remember { mutableStateOf( null ) }

    fun getInitUser():User?{
        val user = page?.getParamValue(PageParam.subData) as? User
        viewModel.fromUserPage = user != null
        viewModel.currentUserId = user?.userId ?: ""
        return user
    }
    fun getPetProfileData():PetProfile?{
        val profile = page?.getParamValue(PageParam.data) as? PetProfile
        val petId = profile?.petId ?: page?.getParamValue(PageParam.data) as? Int ?: -1
        viewModel.currentPetId = petId
        if (profile == null) {
            val q = ApiQ(appTag, ApiType.GetPet, contentID = petId.toString())
            dataProvider.requestData(q)
        } else {
            if(viewModel.currentUserId.isNotEmpty() && user == null) {
                val q = ApiQ(appTag, ApiType.GetUser, contentID = viewModel.currentUserId)
                dataProvider.requestData(q)
            }
        }
        return profile
    }
    fun getUser(){
        if (user != null) return
        profile?.userId?.let {
            viewModel.currentUserId = it
        }
        if(viewModel.currentUserId.isNotEmpty()) {
            val q = ApiQ(appTag, ApiType.GetUser, contentID = viewModel.currentUserId)
            dataProvider.requestData(q)
        }
    }
    if(page?.pageID == PageID.Dog.value && !viewModel.isInit){
        viewModel.isInit = true
        getInitUser()?.let {
            user = it
        }
        getPetProfileData()?.let {
            profile = it
        }
    }

    val apiResult = dataProvider.result.observeAsState()
    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
        when ( res.type ){
            ApiType.GetPet -> {
                if(res.contentID != viewModel.currentPetId.toString()) return@let
                (res.data as? PetData)?.let{
                    profile = PetProfile().init(data = it, isMyPet = user?.isMe ?: false)
                    getUser()
                }
            }
            ApiType.GetUser ->{
                if(res.contentID != viewModel.currentUserId) return@let
                (res.data as? UserData)?.let{
                    user = User().setData(it)
                }
            }
            else ->{}
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
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
            profile?.let { profile ->
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
            }

            Spacer(modifier = Modifier
                .padding(top = DimenMargin.medium.dp)
                .fillMaxWidth()
                .height(DimenLine.heavy.dp)
                .background(ColorApp.grey50)
            )
            if(user != null && profile != null) {
                AlbumSection(
                    modifier
                        .padding(horizontal = DimenApp.pageHorinzontal.dp)
                        .padding(top = DimenMargin.heavyExtra.dp),
                    listSize = listWidth,
                    user = user,
                    pet = profile
                )
            }

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
