package com.ironraft.pupping.bero.scene.page.user.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.provider.model.UserEventType
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.skeleton.component.item.ValueInfoType
import com.skeleton.component.item.profile.*
import com.skeleton.component.tab.ValueBox
import com.skeleton.component.tab.ValueData
import com.skeleton.theme.*
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@Composable
fun UserHistorySection(
    modifier: Modifier = Modifier,
    user:User
) {
    val pagePresenter: PagePresenter = get()
    fun moveHistory(){
        user.userId?.let {
            pagePresenter.openPopup(
                PageProvider.getPageObject(PageID.WalkList)
                .addParam(PageParam.id, it)
            )
        }
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
                ValueBox(
                    datas = listOf(
                        ValueData(
                            valueType = ValueInfoType.WalkComplete,
                            value = user.totalWalkCount.toDouble(),
                            idx = 0
                        ),
                        ValueData(
                            valueType = ValueInfoType.WalkDistance,
                            value = user.exerciseDistance,
                            idx = 1
                        )
                    )
                )
            }
        }
    }
}
