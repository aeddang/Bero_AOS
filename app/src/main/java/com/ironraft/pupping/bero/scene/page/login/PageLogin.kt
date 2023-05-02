package com.ironraft.pupping.bero.scene.page.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.scene.component.button.AgreeButton
import com.ironraft.pupping.bero.scene.component.button.AgreeButtonType
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
import com.skeleton.theme.FontSize
import com.skeleton.view.button.FillButton
import org.koin.compose.koinInject


@Composable
fun PageLogin(
    modifier: Modifier = Modifier
){
    val pagePresenter = koinInject<PageComposePresenter>()
    val repository = koinInject<PageRepository>()
    val appSceneObserver = koinInject<AppSceneObserver>()
    val snsManager = koinInject<SnsManager>()
    var isAgree by remember { mutableStateOf(false) }

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
        when (it.event){
            SnsEvent.Login->
                appSceneObserver.alert.value = ActivitAlertEvent(
                    type = ActivitAlertType.Alert,
                    title = errorMsg
                )
            SnsEvent.GetProfile -> join()
            else -> {}
        }
    }
    snsUser.value?.let {snsManager.getUserInfo() }
    snsUserInfo.value?.let { join() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
    ) {

        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = DimenMargin.medium.dp),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.thin.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f),
                contentAlignment = Alignment.TopCenter
            ) {

                Image(
                    painterResource(R.drawable.onboarding_img_0),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    modifier = Modifier.padding(top = 60.dp),
                    text = stringResource(R.string.loginText),
                    fontSize = FontSize.black.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorApp.black
                )
            }
            AgreeButton(
                type = AgreeButtonType.Service, isChecked = isAgree,
                modifier = Modifier.padding(horizontal = DimenMargin.regular.dp)
            ){
                isAgree = it
            }
            FillButton(
                icon = SnsType.Google.logo,
                isOriginIcon = true,
                text = stringResource(id = R.string.loginButtonText) + SnsType.Google.title,
                color = SnsType.Google.color,
                isActive = isAgree,
                modifier = Modifier.padding(horizontal = DimenMargin.regular.dp)
            ){
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
                text = stringResource(id = R.string.loginButtonText) + SnsType.Fb.title,
                color = SnsType.Fb.color,
                isActive = isAgree,
                modifier = Modifier.padding(horizontal = DimenMargin.regular.dp)
            ){
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

    }
}

@Preview
@Composable
fun PageLoginPreview(){
    PageLogin(
    )
}
