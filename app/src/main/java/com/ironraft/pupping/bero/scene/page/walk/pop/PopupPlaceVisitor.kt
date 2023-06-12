package com.ironraft.pupping.bero.scene.page.walk.pop

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileTopInfo
import com.ironraft.pupping.bero.scene.component.item.PetProfileUser
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.list.VisitorList
import com.ironraft.pupping.bero.scene.page.component.FriendFunctionBox
import com.ironraft.pupping.bero.scene.page.pet.component.PetTagSection
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEvent
import com.ironraft.pupping.bero.scene.page.walk.PageWalkEventType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserAndPet
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkMapData
import com.ironraft.pupping.bero.store.walk.WalkUiEvent
import com.ironraft.pupping.bero.store.walk.WalkUiEventType
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.ListViewModel
import com.lib.page.PagePresenter
import com.lib.util.rememberForeverLazyListState
import com.skeleton.component.item.EmptyData
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.component.tab.MenuTab
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PopupPlaceVisitor(
    placeId:String?,
    close:() -> Unit
) {

    AppTheme {
        Box(
            Modifier
                .wrapContentSize()
                .heightIn(0.dp, 810.dp),
            contentAlignment = Alignment.TopEnd
        ) {

            VisitorList(
                placeId = placeId ?: ""
            )
            ImageButton(
                modifier = Modifier.padding( all = DimenMargin.regular.dp ),
                defaultImage = R.drawable.close
            ) {
                close()
            }
        }
    }
}
