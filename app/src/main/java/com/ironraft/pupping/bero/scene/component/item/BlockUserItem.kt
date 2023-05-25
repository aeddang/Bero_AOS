package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendFunctionViewModel
import com.ironraft.pupping.bero.scene.component.viewmodel.ReportFunctionViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.FriendData
import com.ironraft.pupping.bero.store.api.rest.MissionData
import com.ironraft.pupping.bero.store.api.rest.ReportType
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PageComposePresenter
import com.lib.util.replace
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

class BlockUserItemData{
    var index:Int = -1; private set
    var user:UserProfile? = null; private set
    var refUserId:String? = null; private set

    fun setData(data:UserData, idx:Int) : BlockUserItemData{
        index = idx
        user = UserProfile().setData(data)
        refUserId = data.refUserId ?: ""
        return this
    }
}

@Composable
fun BlockUserItem(
    modifier: Modifier = Modifier,
    data:BlockUserItemData
){
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val reportFunctionViewModel: ReportFunctionViewModel by remember { mutableStateOf(
        ReportFunctionViewModel(repository,
            data.refUserId ?: "",
            data.user?.nickName?.value,
            initIsBlock = true).initSetup(owner)
    ) }
    val isBlock by reportFunctionViewModel.isBlock.observeAsState()

    WrapTransparentButton({
        if (isBlock == true) reportFunctionViewModel.unblock()
        else reportFunctionViewModel.block()
    }) {
        HorizontalProfile(
            type = HorizontalProfileType.User,
            sizeType = HorizontalProfileSizeType.Small,
            funcType = if(isBlock == true) HorizontalProfileFuncType.UnBlock else HorizontalProfileFuncType.Block ,
            imagePath = data.user?.imagePath?.value,
            name = data.user?.nickName?.value,
            description = data.user?.date,
            isSelected = false,
            useBg = true
        ){
            when(it){
                HorizontalProfileFuncType.Block -> reportFunctionViewModel.block()
                HorizontalProfileFuncType.UnBlock -> reportFunctionViewModel.unblock()
                else -> {}
            }
        }
    }
}


