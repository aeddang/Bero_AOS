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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
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
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.lib.util.toDp
import com.skeleton.component.item.ValueInfoType
import com.skeleton.module.Repository
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlin.math.ceil
import kotlin.math.min

internal class PageDogViewModel(repo:PageRepository): PageViewModel(PageID.Dog, repo){
    var fromUserPage:Boolean = false
    var currentPetId:Int = -1
    var currentUserId:String = ""

    var user = MutableLiveData<User?>(null)
    var profile = MutableLiveData<PetProfile?>(null)

    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        when (type) {
            PageEventType.ChangedPage -> {
                val profile = pageObj.getParamValue(PageParam.data) as? PetProfile
                val user = pageObj.getParamValue(PageParam.subData) as? User
                val petId = profile?.petId ?: pageObj.getParamValue(PageParam.id) as? Int ?: -1
                this.user.value = user
                this.profile.value = profile
                fromUserPage = user != null
                currentUserId = user?.userId ?: ""
                currentPetId = petId
                if (profile == null) getPetProfileData(petId)
            }
            else ->{}
        }
    }
    fun getPetProfileData(petId:Int){
        val q = ApiQ(appTag, ApiType.GetPet, contentID = petId.toString())
        repo.dataProvider.requestData(q)
    }
    fun getUser(){
        val q = ApiQ(appTag, ApiType.GetUser, contentID = currentUserId)
        repo.dataProvider.requestData(q)
    }
    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) { it ->
            val res = it ?: return@observe
            when ( res.type ){
                ApiType.GetPet -> {
                    if(res.contentID != currentPetId.toString()) return@observe
                    (res.data as? PetData)?.let{ data->
                        val pet = PetProfile().init(data = data, isMyPet = user.value?.isMe ?: false)
                        profile.value = pet
                        if (user.value == null) {
                            currentUserId = pet.userId
                            getUser()
                        }
                    }
                }
                ApiType.GetUser ->{
                    if(res.contentID != currentUserId) return@observe
                    (res.data as? UserData)?.let{ data->
                        user.value = User().setData(data)
                    }
                }
                else ->{}
            }
        }

    }
}

@Composable
fun PageDog(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageDogViewModel by remember { mutableStateOf(
        PageDogViewModel(repository).initSetup(owner) as PageDogViewModel
    )}
    val currentPage by viewModel.currentPage.observeAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp


    fun getListWidth(): Float {
        val margin = DimenApp.pageHorinzontal * 2.0f
        return screenWidth.toFloat() - margin
    }
    val listWidth: Float by remember { mutableStateOf( getListWidth() ) }
    val user by viewModel.user.observeAsState()
    val profile by viewModel.profile.observeAsState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        currentPage?.let { pageObject->
            val scrollState: ScrollState = rememberForeverScrollState(key = pageObject.key)
            TitleTab(
                parentScrollState = scrollState,
                useBack = true
            ){
                when(it){
                    TitleTabButtonType.Back -> { pagePresenter.goBack() }
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
                            profile.imagePath.value?.let {
                                if (it.isNotEmpty()) {
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.PictureViewer)
                                            .addParam(PageParam.data, it)
                                    )
                                }
                            }

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
}
@Preview
@Composable
fun PageDogPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageDog(
        )
    }
}
