package com.ironraft.pupping.bero.scene.page.my.component

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
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.rest.MissionApi
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
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
fun MyHistorySection(
    modifier: Modifier = Modifier
) {
    val pagePresenter: PagePresenter = get()
    val dataProvider: DataProvider = get()
    var walkDistance:Double by remember { mutableStateOf(dataProvider.user.exerciseDistance) }
    var walkDescription:String by remember { mutableStateOf(dataProvider.user.totalWalkCount.toString()) }

    fun setupDatas(){
        val user = dataProvider.user
        walkDistance = user.exerciseDistance
        walkDescription = user.totalWalkCount.toString()
    }
    val userEvent = dataProvider.user.event.observeAsState()
    userEvent.value?.let { evt ->
        when(evt.type){
            UserEventType.UpdatedPlayData -> setupDatas()
            else -> {}
        }
    }
    fun moveHistory(){
        dataProvider.user.currentPet = null
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.WalkHistory)
            .addParam(PageParam.data, value = dataProvider.user)
        )
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
            WrapTransparentButton({
                moveHistory()
            }) {
                HorizontalProfile(
                    type = HorizontalProfileType.Place,
                    typeIcon = MissionCategory.Walk.icon,
                    sizeType = HorizontalProfileSizeType.Small,
                    funcType = HorizontalProfileFuncType.More,
                    name = MissionCategory.Walk.text + " " + stringResource(id = R.string.pageTitle_history),
                    description = stringResource(id = R.string.historyCompleted).replace(walkDescription),
                    distance = walkDistance,
                    action = {
                        moveHistory()
                    }
                )
            }
        }
    }
}
