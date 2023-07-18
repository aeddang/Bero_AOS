package com.ironraft.pupping.bero.scene.page.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.button.AgreeButton
import com.ironraft.pupping.bero.scene.component.button.AgreeButtonType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.sns.SnsError
import com.skeleton.sns.SnsEvent
import com.skeleton.sns.SnsManager
import com.skeleton.sns.SnsType
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.FontSize
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.TextButton
import com.skeleton.view.button.TextButtonType
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject


@Composable
fun PageLogin(
    modifier: Modifier = Modifier
){
    val pagePresenter:PageComposePresenter = get()
    val repository:PageRepository = get()
    val appSceneObserver:AppSceneObserver = get()
    val snsManager:SnsManager = get()
    var isAgree by remember { mutableStateOf(true) }

    val snsUser = snsManager.user.observeAsState()
    val snsUserInfo = snsManager.userInfo.observeAsState()
    val snsError = snsManager.error.observeAsState()
    val errorMsg = stringResource(R.string.alert_snsLoginError)

    fun join() {
        snsUser.value?.let {
            repository.registerSnsLogin(it, info = snsUserInfo.value)
            return
        }
        appSceneObserver.alert.value = ActivitAlertEvent(
            type = ActivitAlertType.Alert,
            title = errorMsg
        )
    }
    snsError.value?.let{
        it ?: return@let
        when (it.event){
            SnsEvent.Login->
                appSceneObserver.alert.value = ActivitAlertEvent(
                    type = ActivitAlertType.Alert,
                    title = errorMsg,
                    isNegative = true
                )
            SnsEvent.GetProfile -> {
                if (!isAgree) return@let
                join()
            }
            else -> {}
        }
        snsManager.error.value = null
    }
    LaunchedEffect(key1 = snsUser.value){
        snsUser.value?.let {
            snsManager.getUserInfo()
        }
    }

    LaunchedEffect(key1 = snsUserInfo.value) {
        snsUserInfo.value?.let {
            join()
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.primary)
    ) {

        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f)
                ,
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painterResource(R.drawable.logo_splash),
                    modifier = Modifier.width(142.dp),
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )

            }
            Column (
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.large.copy(
                            topStart = CornerSize(DimenRadius.medium.dp),
                            topEnd = CornerSize(DimenRadius.medium.dp)
                        )
                    )
                    .background(ColorApp.white)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(all = DimenMargin.regular.dp)
                    .padding(vertical = DimenMargin.regular.dp)
                ,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /*
                AgreeButton(
                    type = AgreeButtonType.Service, isChecked = isAgree,
                    modifier = Modifier.padding(horizontal = DimenMargin.regular.dp)
                ) {
                    isAgree = it
                }*/
                Text(
                    text = stringResource(R.string.loginText0),
                    fontSize = FontSize.light.sp,
                    color = ColorApp.grey500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(DimenMargin.tinyExtra.dp)
                )

                Text(
                    text = stringResource(R.string.loginText1),
                    fontSize = FontSize.medium.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorApp.black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(DimenMargin.tinyExtra.dp)
                )
                Column (
                    modifier = Modifier.wrapContentHeight().padding(top = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
                ) {
                    FillButton(
                        icon = SnsType.Google.logo,
                        isOriginIcon = true,
                        text = stringResource(id = R.string.loginButtonText) + " " + SnsType.Google.title,
                        color = SnsType.Google.color,
                        isActive = isAgree
                    ) {
                        if (!isAgree) {
                            Toast(pagePresenter.activity).showCustomToast(
                                R.string.alert_needAgreement,
                                pagePresenter.activity
                            )
                            return@FillButton
                        }
                        snsManager.requestLogin(SnsType.Google)
                    }
                    FillButton(
                        icon = SnsType.Fb.logo,
                        text = stringResource(id = R.string.loginButtonText) + " " + SnsType.Fb.title,
                        color = SnsType.Fb.color,
                        isActive = isAgree
                    ) {
                        if (!isAgree) {
                            Toast(pagePresenter.activity).showCustomToast(
                                R.string.alert_needAgreement,
                                pagePresenter.activity
                            )
                            return@FillButton
                        }
                        snsManager.requestLogin(SnsType.Fb)
                    }
                }
                Column (
                    modifier = Modifier.wrapContentHeight().padding(top = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.loginText2),
                        fontWeight= FontWeight.Light,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey500,
                        textAlign = TextAlign.Center
                    )
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        TextButton(
                            type = TextButtonType.Blank,
                            defaultText = AgreeButtonType.Service.text,
                            textFamily = FontWeight.Normal,
                            textSize = FontSize.thin,
                            isUnderLine = true
                        ) {
                            AgreeButtonType.Service.page?.let {
                                pagePresenter.openPopup(PageProvider.getPageObject(it))
                            }
                        }
                        Text(
                            text = stringResource(R.string.loginText3),
                            fontWeight= FontWeight.Light,
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey500,
                            textAlign = TextAlign.Center
                        )
                        TextButton(
                            type = TextButtonType.Blank,
                            defaultText = AgreeButtonType.Privacy.text,
                            textFamily = FontWeight.Normal,
                            textSize = FontSize.thin,
                            isUnderLine = true
                        ) {
                            AgreeButtonType.Privacy.page?.let {
                                pagePresenter.openPopup(PageProvider.getPageObject(it))
                            }
                        }
                        Text(
                            text = stringResource(R.string.loginText4),
                            fontWeight= FontWeight.Light,
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey500,
                            textAlign = TextAlign.Center
                        )

                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun PageLoginPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageLogin(
        )
    }
}
