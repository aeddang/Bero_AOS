package com.ironraft.pupping.bero.scene.page.user
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.item.PetProfileTopInfo
import com.ironraft.pupping.bero.scene.component.item.UserProfileTopInfo
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendFunctionViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.ReportFunctionViewModel
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomListItemData
import com.ironraft.pupping.bero.scene.page.component.AlbumSection
import com.ironraft.pupping.bero.scene.page.component.FriendFunctionBox
import com.ironraft.pupping.bero.scene.page.component.FriendSection
import com.ironraft.pupping.bero.scene.page.component.UserPlayInfo
import com.ironraft.pupping.bero.scene.page.user.component.UserDogsSection
import com.ironraft.pupping.bero.scene.page.user.component.UserHistorySection
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.*
import com.lib.util.rememberForeverScrollState
import com.lib.util.showCustomToast
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.ValueInfoType
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get

class PageUserViewModel(repo:PageRepository): PageViewModel(PageID.User, repo){
    var fromChatRoom:Boolean = false
    var roomData:ChatRoomListItemData? = null
    var currentUserId:String = ""
    var user = MutableLiveData<User?>(null)
    var currentUser:User? = null
    var representativePet = MutableLiveData<PetProfile?>(null)
    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        super.onCurrentPageEvent(type, pageObj)
        when (type) {
            PageEventType.ChangedPage -> {
                roomData = pageObj.getParamValue(PageParam.subData) as? ChatRoomListItemData
                fromChatRoom = roomData != null
                val user = pageObj.getParamValue(PageParam.data) as? User
                val userId = user?.userId ?: pageObj.getParamValue(PageParam.id) as? String ?: ""
                currentUser = user
                currentUserId = userId
                val pet = user?.representativePet?.value
                representativePet.value = pet
                if (user == null) getUser()
                else if( pet != null)  this.user.value = user
                else getPets()
            }
            else ->{}
        }
    }

    fun getUser(){
        val q = ApiQ(appTag, ApiType.GetUser, contentID = currentUserId)
        repo.dataProvider.requestData(q)
    }
    fun getPets(){
        val q = ApiQ(appTag, ApiType.GetPets, contentID = currentUserId)
        repo.dataProvider.requestData(q)
    }
    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) { it ->
            val res = it ?: return@observe
            when ( res.type ){
                ApiType.GetUser ->{
                    if(res.contentID != currentUserId) return@observe
                    (res.data as? UserData)?.let{ data->
                        val user = User().setData(data)
                        currentUser = user
                        getPets()
                    }

                }
                ApiType.GetPets ->{
                    if(res.contentID != currentUserId) return@observe
                    (res.data as? List<PetData>)?.let{ currentUser?.setData(it, false) }
                    representativePet.value = currentUser?.representativePet?.value
                    user.value = currentUser
                }
                else ->{}
            }
        }

    }
}

@Composable
fun PageUser(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageUserViewModel by remember { mutableStateOf(
        PageUserViewModel(repository).initSetup(owner) as PageUserViewModel
    )}
    val reportFunctionViewModel: ReportFunctionViewModel by remember { mutableStateOf(
        ReportFunctionViewModel(repository).initSetup(owner)
    )}
    val friendFunctionViewModel: FriendFunctionViewModel by remember { mutableStateOf(
         FriendFunctionViewModel(repository).initSetup(owner)
    ) }

    val currentPage by viewModel.currentPage.observeAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp
    fun getListWidth(): Float {
        val margin = DimenApp.pageHorinzontal * 2.0f
        return screenWidth.toFloat() - margin
    }
    val listWidth: Float by remember { mutableStateOf( getListWidth() ) }
    val user by viewModel.user.observeAsState()
    val representativePet by viewModel.representativePet.observeAsState()

    fun more(){
        val currentUser = user ?: return
        if (currentUser.isMe) {
            Toast(pagePresenter.activity).showCustomToast(
                pagePresenter.activity.getString(R.string.alert_itsMe),
                pagePresenter.activity
            )
            return
        }
        reportFunctionViewModel.lazySetup(currentUser.userId, currentUser.representativeName)
        if (!currentUser.isFriend) {
            reportFunctionViewModel.more(ReportType.User)
            return
        }
        repository.appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = repository.pagePresenter.activity.getString(R.string.alert_supportAction),
            radioButtons = arrayListOf(
                RadioBtnData(
                    icon = R.drawable.remove_friend,
                    title = repository.pagePresenter.activity.getString(R.string.button_removeFriend),
                    index = 0
                ),
                RadioBtnData(
                    icon = R.drawable.block,
                    title = repository.pagePresenter.activity.getString(R.string.button_block),
                    index = 1
                ),
                RadioBtnData(
                    icon = R.drawable.warning,
                    title = repository.pagePresenter.activity.getString(R.string.button_accuse),
                    index = 2
                )
            )
        ){ select ->
            when(select){
                0 -> friendFunctionViewModel.removeFriend()
                1 -> reportFunctionViewModel.block()
                2 -> reportFunctionViewModel.accuseUser(ReportType.User)
                else -> {}
            }
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp),

    ) {
        user?.let { user ->
            currentPage?.let { pageObject ->
                val scrollState: ScrollState = rememberForeverScrollState(key = pageObject.key)
                TitleTab(
                    parentScrollState = scrollState,
                    useBack = true,
                    buttons = arrayListOf(TitleTabButtonType.More)
                ) {
                    when (it) {
                        TitleTabButtonType.Back -> {
                            pagePresenter.goBack()
                        }
                        TitleTabButtonType.More -> more()
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

                    if (representativePet != null)
                        representativePet?.let { profile ->
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
                                viewProfile = {
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.Dog)
                                            .addParam(PageParam.data, profile)
                                            .addParam(PageParam.subData, user)
                                    )
                                }
                            )
                        }
                    else
                        UserProfileTopInfo(
                            modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                            profile = user.currentProfile
                        ) {
                            user.currentProfile.imagePath.value?.let {
                                if (it.isNotEmpty()) {
                                    pagePresenter.openPopup(
                                        PageProvider.getPageObject(PageID.PictureViewer)
                                            .addParam(PageParam.data, it)
                                    )
                                }
                            }
                        }


                    UserPlayInfo(
                        modifier = Modifier
                            .padding(horizontal = DimenApp.pageHorinzontal.dp)
                            .padding(top = DimenMargin.regular.dp),
                        user = user
                    ) { data ->
                        when (data.valueType) {
                            ValueInfoType.Point -> {
                                val msg =
                                    pagePresenter.activity.getString(R.string.alert_itsNotYourPoint)
                                Toast(pagePresenter.activity).showCustomToast(
                                    msg,
                                    pagePresenter.activity
                                )
                            }
                            ValueInfoType.Lv -> {
                                Toast(pagePresenter.activity).showCustomToast(
                                    Lv.getLv(user.lv).title,
                                    pagePresenter.activity
                                )
                            }
                            else -> {}
                        }
                    }
                    if (!user.isMe)
                        (if (user.isFriend) FriendStatus.Chat else user.currentProfile.status.value)?.let {
                            FriendFunctionBox(
                                friendFunctionViewModel = friendFunctionViewModel.lazySetup(user.userId, it),
                                modifier = Modifier
                                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                                    .padding(top = DimenMargin.regular.dp),
                                userId = user.userId ?: "",
                                userName = user.currentProfile.nickName.value,
                                status = it
                            )
                        }

                    Spacer(
                        modifier = Modifier
                            .padding(top = DimenMargin.medium.dp)
                            .fillMaxWidth()
                            .height(DimenLine.heavy.dp)
                            .background(ColorApp.gray200)
                    )
                    UserHistorySection(
                        modifier = Modifier
                            .padding(horizontal = DimenApp.pageHorinzontal.dp)
                            .padding(top = DimenMargin.regular.dp),
                        user = user
                    )
                    Spacer(
                        modifier = Modifier
                            .padding(top = DimenMargin.medium.dp)
                            .fillMaxWidth()
                            .height(DimenLine.heavy.dp)
                            .background(ColorApp.gray200)
                    )
                    UserDogsSection(
                        modifier = Modifier
                            .padding(top = DimenMargin.regular.dp),
                        user = user
                    )
                    FriendSection(
                        modifier
                            .padding(horizontal = DimenApp.pageHorinzontal.dp)
                            .padding(top = DimenMargin.heavyExtra.dp),
                        listSize = listWidth,
                        user = user
                    )
                    AlbumSection(
                        modifier
                            .padding(horizontal = DimenApp.pageHorinzontal.dp)
                            .padding(top = DimenMargin.heavyExtra.dp),
                        listSize = listWidth,
                        user = user
                    )
                }
            }
        }

    }
}
@Preview
@Composable
fun PageUserPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageUser(
        )
    }
}
