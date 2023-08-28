package com.ironraft.pupping.bero.scene.page.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
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
import com.skeleton.view.button.FillButton
import com.skeleton.view.button.FillButtonType
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlin.math.min


@Composable
fun PointSection(
    modifier: Modifier = Modifier,
    user:User? = null
) {
    val dataProvider: DataProvider = get()
    val pagePresenter: PagePresenter = get()
    var point:Int  by remember { mutableStateOf(0) }


    fun setupDatas():Boolean{
        val currentUser = user ?: dataProvider.user
        point = currentUser.point
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
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    point.toString(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = DimenIcon.heavyExtra.sp,
                    color = ColorApp.black,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Image(
                    painterResource(R.drawable.point),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(DimenIcon.heavyExtra.dp)
                )
            }
            FillButton(
                type = FillButtonType.Fill,
                icon = R.drawable.store,
                text = stringResource(id = R.string.myPointText2),
                color = ColorApp.gray200
            ){
                val ctx = pagePresenter.activity
                Toast(ctx).showCustomToast(
                    ctx.getString(R.string.myPointText2),
                    ctx
                )
            }
        }
    }
}
@Preview
@Composable
fun PointSectionComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            PointSection(
                user = User(),
            )
        }
    }
}