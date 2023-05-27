package com.ironraft.pupping.bero.scene.page.history.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.item.AlbumListItem
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumListViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.AlbumPickViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.PageComposePresenter
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.AppTheme
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenItem
import com.skeleton.theme.DimenMargin
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

@Composable
fun WalkPropertySection(
    modifier: Modifier = Modifier,
    mission:Mission,
    action: ((Int) -> Unit)? = null
) {

    AppTheme {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box (contentAlignment = Alignment.Center){
                Image(
                    painterResource(R.drawable.route_bg),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )

            }

        }
    }
}
