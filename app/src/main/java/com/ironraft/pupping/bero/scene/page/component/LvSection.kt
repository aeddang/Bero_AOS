package com.ironraft.pupping.bero.scene.page.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.button.LvButton
import com.ironraft.pupping.bero.scene.component.button.LvButtonType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.skeleton.component.progress.ProgressInfo
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlin.math.min


@Composable
fun LvSection(
    modifier: Modifier = Modifier,
    user:User? = null
) {
    val dataProvider: DataProvider = get()
    val pagePresenter: PagePresenter = get()
    var lvValue:Int  by remember { mutableStateOf(1) }
    var lv:Lv by remember { mutableStateOf(Lv.Green) }
    var exp:Double by remember { mutableStateOf(0.0) }
    var expMax:Double by remember { mutableStateOf(0.0) }
    var expProgress:Double by remember { mutableStateOf(0.0) }
    var needInfo:String by remember { mutableStateOf("") }

    fun setupDatas():Boolean{
        val currentUser = user ?: dataProvider.user
        lvValue = currentUser.lv
        lv = Lv.getLv(lvValue)
        exp = currentUser.exp
        val current = currentUser.prevExp
        val progress = exp - current
        expMax = currentUser.prevExp + currentUser.nextExp
        expProgress = min(progress, expMax)
        val ctx = pagePresenter.activity
        needInfo = if (exp == 0.0) {
            ctx.getString(R.string.myLvText3).replace(
                ctx.getString(R.string.appName)
            )
        } else {
            val needExp = expMax - exp
            ctx.getString(R.string.myLvText4).replace(
                needExp.toInt().toString()
            )
        }
        return true
    }

    var isInit by remember { mutableStateOf(setupDatas()) }
    val userEvent = dataProvider.user.event.observeAsState()
    if (user == null ) {
        userEvent.value?.let { evt ->
            when (evt.type) {
                UserEventType.UpdatedProfile -> setupDatas()
                else -> {}
            }
        }
    }
    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LvButton(
                lv = lv,
                type = LvButtonType.Big,
                text = "Lv.${lvValue.toString()}"
            ) {
                Toast(pagePresenter.activity).showCustomToast(
                    lv.title,
                    pagePresenter.activity
                )
            }
            ProgressInfo(
                modifier = Modifier
                    .padding(top = DimenMargin.regularExtra.dp)
                    .height(32.dp),
                leadingText = "Lv.${lvValue.toString()}",
                trailingText = stringResource(id = R.string.exp),
                progress = expProgress,
                progressMax = expMax,
                color = lv.color
            )
            Text(
                needInfo,
                fontSize = FontSize.thin.sp,
                color = ColorBrand.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = DimenMargin.regularUltra.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(corner = CornerSize(DimenRadius.tiny.dp)))
                    .background(ColorApp.orangeSub)
                    .padding(horizontal = DimenMargin.light.dp, vertical = DimenMargin.tiny.dp)

            )
        }
    }
}
@Preview
@Composable
fun LvSectionComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier.background(ColorApp.white).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            LvSection(
                user = User(),
            )
        }
    }
}