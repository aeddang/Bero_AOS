package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.ironraft.pupping.bero.scene.component.viewmodel.FriendFunctionViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.ReportType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.get

@Composable
fun UserProfileItem(
    modifier: Modifier = Modifier,
    profile:UserProfile,
    type:HorizontalProfileType = HorizontalProfileType.Pet,
    reportType:ReportType = ReportType.User,
    postId:String? = null,
    title:String? = null,
    lv:Int? = null,
    imagePath:String? = null,
    description:String? = null,
    date:String? =null,
    useBg:Boolean = false,
    action: (() -> Unit)? = null
) {
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val viewModel: FriendFunctionViewModel by remember { mutableStateOf(
        FriendFunctionViewModel(repository, profile.userId, profile.status.value).initSetup(owner)
    ) }
    val currentStatus = viewModel.currentStatus.observeAsState()
    currentStatus.value?.let {
        profile.status.value = it
    }

    if(action != null)
        WrapTransparentButton(
            { action() }
        ) {
            UserProfileItemBody(
                modifier = modifier,
                profile = profile,
                type = type,
                title = title,
                lv = lv,
                imagePath = imagePath,
                description = description,
                date = date,
                useBg = useBg
            )
        }
    else
        UserProfileItemBody(
            modifier = modifier,
            profile = profile,
            type = type,
            title = title,
            lv = lv,
            imagePath = imagePath,
            description = description,
            date = date,
            useBg = useBg
        )
}


@Composable
fun UserProfileItemBody(
    modifier: Modifier = Modifier,
    profile:UserProfile,
    type:HorizontalProfileType = HorizontalProfileType.Pet,
    title:String? = null,
    lv:Int? = null,
    imagePath:String? = null,
    description:String? = null,
    date:String? =null,
    useBg:Boolean = false
) {
    val dataProvider: DataProvider = get()
    AppTheme {
        HorizontalProfile(
            modifier = modifier,
            type = type,
            sizeType = HorizontalProfileSizeType.Small,
            funcType = if(dataProvider.user.isSameUser(profile)) null else HorizontalProfileFuncType.MoreFunc,
            imagePath = imagePath,
            lv = lv,
            name = title ?: profile.nickName.value,
            gender = if(date == null) profile.gender.value else null,
            age = if(date == null) profile.birth.value?.toAge() else null,
            description = description ?: date,
            isSelected = false,
            useBg = useBg
        )
    }
}