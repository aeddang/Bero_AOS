package com.ironraft.pupping.bero.scene.page.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.page.history.component.WalkPlayInfo
import com.ironraft.pupping.bero.scene.page.history.component.WalkPropertySection
import com.ironraft.pupping.bero.scene.page.history.component.WalkTopInfo
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.ironraft.pupping.bero.store.walk.model.WalkPictureItem
import com.lib.page.*
import com.lib.util.Grid
import com.lib.util.rememberForeverScrollState
import com.skeleton.component.item.ListItem
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenApp
import com.skeleton.theme.DimenItem
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenProfile
import com.skeleton.theme.DimenRadius
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlin.math.floor

class PageWalkInfoViewModel(repo:PageRepository): PageViewModel(PageID.WalkInfo, repo){

    val mission = MutableLiveData<Mission?>(null)
    var pictures:List<WalkPictureItem> = listOf(); private set
    var isMe = false; private set
    var userProfile:UserProfile? = null; private set
    var user:User? = null; private set
    var userId:String? = null; private set
    var userImagePath:String? = null; private set
    var walkId:Int = -1; private set


    private fun updatedData(missionData:Mission){
        walkId = missionData.missionId
        val userId = user?.userId ?: userProfile?.userId ?: missionData.userId ?:  ""
        isMe = repo.dataProvider.user.isSameUser(userId = userId)
        missionData.walkPath?.pictures?.let { datas->
            pictures = datas
        }
        this.userId = userId
        this.userImagePath = user?.representativeImage ?: userProfile?.imagePath?.value
        mission.value = missionData
    }

    override fun onCurrentPageEvent(type: PageEventType, pageObj: PageObject) {
        when (type) {
            PageEventType.ChangedPage -> {
                val missionData = pageObj.getParamValue(PageParam.data) as? Mission
                missionData?.let {
                    user = it.user
                    updatedData(it)
                    return
                }
                user = pageObj.getParamValue(PageParam.data) as? User
                userProfile = pageObj.getParamValue(PageParam.data) as? UserProfile
                (pageObj.getParamValue(PageParam.id) as? Int)?.let{id->
                    walkId = id
                    val q = ApiQ(appTag, ApiType.GetWalk, contentID = id.toString())
                    repo.dataProvider.requestData(q)
                }
            }
            else ->{}
        }
    }
    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) { it ->
            val res = it ?: return@observe
            if(res.contentID != walkId.toString()) return@observe
            when ( res.type ){
                ApiType.GetWalk -> {
                    (res.data as? WalkData)?.let{ data->
                        val me = repo.dataProvider.user.isSameUser(userId = data.user?.userId)
                        val missionData = Mission().setData(data, userId = user?.userId, isMe = me)
                        updatedData(missionData)
                    }
                }
                else ->{}
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageWalkInfo(
    modifier: Modifier = Modifier
){
    val owner = LocalLifecycleOwner.current
    val repository:PageRepository = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel:PageWalkInfoViewModel by remember { mutableStateOf(
        PageWalkInfoViewModel(repository).initSetup(owner) as PageWalkInfoViewModel
    )}


    val scrollState: ScrollState = rememberForeverScrollState(key = PageID.WalkHistory.value)
    val screenWidth = LocalConfiguration.current.screenWidthDp

    fun getPictireSize(): Size {
        val w = (screenWidth
                - (DimenMargin.regularExtra)
                - (DimenApp.pageHorinzontal*2)) / 2.0f
       return Size(floor(w), w * DimenItem.albumList.height / DimenItem.albumList.width)
    }
    val pictureSize by remember { mutableStateOf( getPictireSize() ) }
    val mission by viewModel.mission.observeAsState()

    fun onMovePicture(){
        val pictures = mission?.walkPath?.pictures ?: return
        val title = pagePresenter.activity.getString(R.string.pageTitle_walkPicture)
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Picture)
                .addParam(PageParam.datas, pictures)
                .addParam(PageParam.title, title)
        )
    }
    fun onMovePicturViewer(path:String?){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.PictureViewer)
                .addParam(PageParam.data, path)
        )
    }
    fun onMovePet(pet:PetProfile){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.Dog)
                .addParam(PageParam.data, pet)
        )
    }
    fun onMoveUser(userId:String){
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.User)
                .addParam(PageParam.data, viewModel.user)
                .addParam(PageParam.id, userId)
        )
    }
    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = stringResource(id = R.string.pageTitle_walkSummary),
            useBack = true
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                else -> {}
            }
        }
        mission?.let { mission->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.TopCenter
            ){
                var painter: AsyncImagePainter? = null
                mission.pictureUrl?.let {
                    painter = rememberAsyncImagePainter( it,
                        placeholder = painterResource(R.drawable.noimage_1_1),
                        onSuccess = { success ->
                            //val size = success.result.drawable.bounds
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth.dp)
                        .clipToBounds(),
                    contentAlignment = Alignment.TopEnd
                ){
                    painter?.let {
                        Image(
                            it,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    TransparentButton{
                        onMovePicture()
                    }
                    if (viewModel.isMe){
                        mission.user?.pets?.let { petDatas->
                            Row(
                                modifier = Modifier.padding(all = DimenMargin.regular.dp),
                                horizontalArrangement = Arrangement.spacedBy(space = DimenMargin.microExtra.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                petDatas.forEach {pet->
                                    WrapTransparentButton(action = {
                                        onMovePet(pet)
                                    }) {
                                        ProfileImage(
                                            image = pet.image.value,
                                            imagePath = pet.imagePath.value,
                                            size = DimenProfile.thin,
                                            emptyImagePath = R.drawable.profile_dog_default,
                                            isSelected = true
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        viewModel.userId?.let {
                            WrapTransparentButton(action = {
                                onMoveUser(it)
                            }) {
                                ProfileImage(
                                    imagePath = viewModel.userImagePath,
                                    size = DimenProfile.thin,
                                    emptyImagePath = R.drawable.profile_user_default,
                                    isSelected = true
                                )
                            }
                        }

                    }
                }
                Column (
                    modifier = modifier
                        .padding(top = 240.dp)
                        .clip(
                            MaterialTheme.shapes.large.copy(
                                topStart = CornerSize(DimenRadius.medium.dp),
                                topEnd = CornerSize(DimenRadius.medium.dp)
                            )
                        )
                        .background(ColorApp.white)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(
                            top = DimenMargin.regular.dp,
                            bottom = DimenMargin.heavyExtra.dp
                        )
                    ,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WalkTopInfo(
                            modifier = Modifier.weight(1.0f),
                            mission = mission, isMe = viewModel.isMe)
                        if (!viewModel.isMe){
                            mission.user?.pets?.let { petDatas->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(space = DimenMargin.microExtra.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    petDatas.forEach {pet->
                                        ProfileImage(
                                            image = pet.image.value,
                                            imagePath = pet.imagePath.value,
                                            size = DimenProfile.tiny,
                                            emptyImagePath = R.drawable.profile_dog_default
                                        )
                                    }
                                }
                            }
                        }
                    }
                    WalkPropertySection(
                        modifier = Modifier
                            .padding(top = DimenMargin.regular.dp)
                            .padding(horizontal = DimenApp.pageHorinzontal.dp),
                        mission = mission)
                    WalkPlayInfo(
                        modifier = Modifier
                            .padding(top = DimenMargin.regular.dp, bottom = DimenMargin.medium.dp,)
                            .padding(horizontal = DimenApp.pageHorinzontal.dp),
                        mission = mission)
                    Grid(
                        modifier = Modifier
                            .padding(top = DimenMargin.regular.dp, bottom = DimenMargin.medium.dp,)
                            .padding(horizontal = DimenApp.pageHorinzontal.dp),
                        columns = 2,
                        itemCount = viewModel.pictures.count(),
                        verticalSpace = DimenMargin.regularExtra,
                        horizontalSpace = DimenMargin.regularExtra
                    ) {
                        viewModel.pictures[it].let {data ->
                            ListItem(
                                imagePath = data.pictureUrl,
                                imgSize = pictureSize
                            ) {
                                onMovePicturViewer(data.pictureUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PageWalkInfoPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageWalkInfo(
        )
    }
}
