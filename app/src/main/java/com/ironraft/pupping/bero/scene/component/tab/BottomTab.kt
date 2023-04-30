package com.ironraft.pupping.bero.scene.component.tab

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.scene.component.item.RewardInfoSizeType
import com.ironraft.pupping.bero.scene.component.item.RewardInfoType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.lib.page.PageAppViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PageEvent
import com.lib.page.PageObject
import com.skeleton.view.button.ImageButton
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import org.koin.compose.koinInject

data class PageSelecterble (
    val id:PageID,
    var idx:Int = -1,
    @DrawableRes var icon:Int,
    var text:String = "",
    var isPopup:Boolean = false,
    var isSelected:Boolean = false
)

@Composable
fun BottomTab(

) {
    val pagePresenter = koinInject<PageComposePresenter>()
    val pageAppViewModel = koinInject<PageAppViewModel>()
    val pages:ArrayList<PageSelecterble> = arrayListOf(
        PageSelecterble(PageID.Walk, PageID.Walk.position, R.drawable.paw, text = stringResource(R.string.gnb_walk)),
        PageSelecterble(PageID.Explore, PageID.Explore.position, R.drawable.explore, text = stringResource(R.string.gnb_explore)),
        PageSelecterble(PageID.Chat, PageID.Chat.position, R.drawable.chat, text = stringResource(R.string.gnb_chat)),
        PageSelecterble(PageID.My, PageID.My.position, R.drawable.my, text = stringResource(R.string.gnb_my))
    )
    val currentTopPage:PageObject? by pageAppViewModel.currentTopPage.observeAsState(pagePresenter.currentPage)

    AppTheme {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(
                modifier = Modifier.fillMaxWidth().height(DimenLine.light.dp)
                    .background(ColorApp.grey50)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEach { page ->
                    ImageButton(
                        isSelected = page.idx == currentTopPage?.pageIDX,
                        defaultImage = page.icon,
                        text = page.text,
                        iconText = if (page.id == PageID.Chat) "N" else null,
                        defaultColor = ColorApp.grey200,
                        activeColor = ColorBrand.primary
                    ) {
                        pagePresenter.changePage(PageObject(page.id.value, page.idx))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BottomTabComposePreview() {
    Column(
        modifier = Modifier.padding(16.dp).width(320.dp).background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BottomTab(

        )

    }
}