package com.ironraft.pupping.bero.scene.page.profile
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.component.step.*
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.page.PagePresenter
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject
@Composable
fun PageAddDogCompleted(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val appTag = PageID.AddDogCompleted.value
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val dataProvider:DataProvider = get()
    val pagePresenter: PagePresenter = get()

    val viewModel: PageViewModel by remember { mutableStateOf(PageViewModel(PageID.AddDogCompleted, repository).initSetup(owner)) }
    var profile:ModifyPetProfileData by remember { mutableStateOf( ModifyPetProfileData() ) }
    val userEvent = dataProvider.user.event.observeAsState()
    @Suppress("UNCHECKED_CAST")
    userEvent.value.let { evt ->
        evt?.type ?: return@let
        when ( evt.type ){
            UserEventType.AddedDog -> {
                pagePresenter.goBack()
                dataProvider.user.event.value = null
            }
            else ->{}
        }
    }

    val currentPage = viewModel.currentPage.observeAsState()
    currentPage.value?.let { page ->
        if (!viewModel.isInit) {
            viewModel.isInit = true
            val pro = page.getParamValue(PageParam.data) as? ModifyPetProfileData
            pro?.let {
                profile = it
            }
        }
    }

    fun onRegist(){
        profile.isRepresentative = dataProvider.user.representativePet.value == null
        val q = ApiQ(appTag, ApiType.RegistPet, requestData = profile, isLock = true)
        dataProvider.requestData(q)
    }
    Column (
        modifier = modifier
            .background(ColorBrand.bg)
            .fillMaxSize()
            .padding(horizontal = DimenApp.pageHorinzontal.dp)
            .padding(bottom = DimenMargin.regular.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.0f))
        Box(
            modifier =Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ){
            Image(
                painterResource(R.drawable.profile_deco),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(136.dp, 90.dp)
            )
            ProfileImage(
                image = profile.image,
                size = DimenProfile.medium
            )
        }
        Text(
            stringResource(id = R.string.addDogCompletedText1),
            fontSize = FontSize.thin.sp,
            color = ColorApp.gray500,
            modifier = Modifier.padding(vertical = DimenMargin.regularExtra.dp)
        )
        profile.name?.let {
            Text(
                it,
                fontSize = FontSize.black.sp,
                fontWeight = FontWeight.Bold,
                color = ColorApp.black,
                modifier = Modifier.padding(vertical = DimenMargin.micro.dp)
            )
        }
        Text(
            stringResource(id = R.string.addDogCompletedText2),
            fontSize = FontSize.light.sp,
            color = ColorApp.black,
            modifier = Modifier.padding(vertical = DimenMargin.heavy.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1.0f))
        FillButton(
            type = FillButtonType.Fill,
            text = stringResource(id = R.string.addDogCompletedConfirm),
            color = ColorBrand.primary
        ) {
            onRegist()
        }
    }
}
@Preview
@Composable
fun PageAddDogCompleted(){
    PageAddDogCompleted(
    )
}
