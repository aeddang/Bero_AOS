package com.ironraft.pupping.bero.scene.page.component

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.component.item.FriendListItem
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.tab.TitleTabType
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.button.FriendButton
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendFunctionViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendListViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.model.FriendStatus

@Composable
fun FriendFunctionBox(
    modifier: Modifier = Modifier,
    userId:String,
    userName:String? = null,
    status:FriendStatus = FriendStatus.Norelation
) {

    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val viewModel: FriendFunctionViewModel by remember { mutableStateOf(
        FriendFunctionViewModel(repository, userId, status).initSetup(owner)
    ) }
    val currentStatus by viewModel.currentStatus.observeAsState()

    AppTheme {
        Row (
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp)
        ) {
            currentStatus?.let { status->
                status.buttons.forEach {
                    FriendButton(
                        modifier = Modifier.weight(1.0f),
                        funcType = it,
                        userId = userId,
                        userName = userName
                    )

                }
            }


        }
    }
}
