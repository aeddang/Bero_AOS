package com.ironraft.pupping.bero.scene.page.walk.component

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.MultiProfileListItemData
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEvent
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEventType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupData
import com.ironraft.pupping.bero.scene.page.walk.pop.WalkPopupType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.model.Place
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get


@Composable
fun VisitorHorizontalView(
    modifier: Modifier = Modifier,
    viewModel: PageWalkViewModel,
    place: Place,
    datas:List<MultiProfileListItemData>,
    close:() -> Unit
) {
    val pagePresenter:PagePresenter = get()
    val dataProvider:DataProvider = get()
    val scrollState: ScrollState = rememberScrollState()

    fun onMove(id:String? = null){
        if (dataProvider.user.isSameUser(id)) {
            Toast(pagePresenter.activity).showCustomToast(
                pagePresenter.activity.getString(R.string.alert_itsMe),
                pagePresenter.activity
            )
            return
        }
        pagePresenter.openPopup(
            PageProvider.getPageObject(PageID.User)
                .addParam(PageParam.id, id)
        )
    }

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            TitleTab(
                modifier = Modifier.padding(horizontal = DimenApp.pageHorinzontal.dp),
                type = TitleTabType.Section,
                title = stringResource(id = R.string.walkVisitorTitle).replace(place.visitorCount.toString()),
                buttons = if(place.placeId != -1) arrayListOf(TitleTabButtonType.ViewMore) else arrayListOf()
            ){
                when(it){
                    TitleTabButtonType.ViewMore -> {
                        close()
                        viewModel.event.value = PageWalkEvent(
                            PageWalkEventType.OpenPopup,
                            WalkPopupData(WalkPopupType.WalkPlaceVistor, value = place.placeId.toString())
                        )
                    }
                    else ->{}
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = DimenApp.pageHorinzontal.dp)
                ,
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.thin.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                datas.forEach { data->
                    WrapTransparentButton(
                        action = {
                            onMove(data.user?.userId)
                        }
                    ) {
                        MultiProfile(
                            type = MultiProfileType.Pet,
                            imagePath = data.pet?.imagePath?.value,
                            imageSize = DimenProfile.mediumUltra,
                            name = data.pet?.name?.value ?: data.user?.nickName?.value,
                            lv = data.lv,
                            buttonAction = {
                                onMove(data.user?.userId)
                            }
                        )
                    }
                }
            }
        }
    }
}
