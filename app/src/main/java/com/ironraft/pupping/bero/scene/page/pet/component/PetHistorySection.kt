package com.ironraft.pupping.bero.scene.page.pet.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.api.rest.MissionApi
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserEvent
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.lib.page.PageComposePresenter
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.item.profile.*
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@Composable
fun PetHistorySection(
    modifier: Modifier = Modifier,
    user:User,
    profile:PetProfile
) {
    val pagePresenter: PagePresenter = get()
    val dataProvider: DataProvider = get()

    fun getDatas():List<ValueData>{
        val walk = ValueData(idx = 0, valueType = ValueInfoType.WalkComplete, value = profile.totalWalkCount.value?.toDouble() ?: 0.0)
        return listOf(walk)
    }
    val totalWalkCount by profile.totalWalkCount.observeAsState()
    var datas:List<ValueData> by remember { mutableStateOf(getDatas()) }

    fun moveHistory(){
        /*
        self.user.currentPet = profile
                self.pagePresenter.openPopup(
                    PageProvider.getPageObject(.walkHistory)
                    .addParam(key: .data, value: self.user)
                )
        )*/
    }

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                type = TitleTabType.Section,
                title = stringResource(id = R.string.pageTitle_history)
            )
            if(profile.isMypet)
                WrapTransparentButton({
                    moveHistory()
                }) {
                    HorizontalProfile(
                        type = HorizontalProfileType.Place,
                        typeIcon = MissionCategory.Walk.icon,
                        sizeType = HorizontalProfileSizeType.Small,
                        funcType = HorizontalProfileFuncType.More,
                        name = MissionCategory.Walk.text + " " + stringResource(id = R.string.pageTitle_history),
                        description = stringResource(id = R.string.historyCompleted).replace(totalWalkCount.toString()),
                        action = {
                            moveHistory()
                        }
                    )
                }
            else
                ValueBox(datas = datas)
        }
    }
}
