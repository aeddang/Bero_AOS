package com.ironraft.pupping.bero.scene.page.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.viewmodel.ReportFunctionViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.rest.ChatData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.PageComposePresenter
import com.lib.util.toAge
import com.lib.util.toDate
import com.lib.util.toFormatString
import com.skeleton.component.item.profile.HorizontalProfile
import com.skeleton.component.item.profile.HorizontalProfileSizeType
import com.skeleton.component.item.profile.HorizontalProfileType
import com.skeleton.view.button.TransparentButton
import dev.burnoo.cokoin.get
import java.time.LocalDate



@Composable
fun ChatUser(
    modifier: Modifier = Modifier,
    user: User?,
    pet:PetProfile?,
    roomData:ChatRoomListItemData?
){

    val pagePresenter: PageComposePresenter = get()

    Box (
        modifier = modifier
            .fillMaxWidth().height(90.dp)
            .background(ColorApp.orangeSub)
            .padding(
                horizontal = DimenApp.pageHorinzontal.dp,
                vertical = DimenMargin.thin.dp
            )
    ) {
        if(pet != null) {
            pet?.let { data ->
                HorizontalProfile(
                    type = HorizontalProfileType.Pet,
                    sizeType = HorizontalProfileSizeType.Small,
                    imagePath = data.imagePath.value,
                    lv = user?.lv,
                    name = data.name.value,
                    gender = data.gender.value,
                    isNeutralized = data.isNeutralized.value,
                    age = data.birth.value?.toAge(),
                    useBg = false
                )
            }
        }else {
            user?.currentProfile?.let { data ->
                HorizontalProfile(
                    type = HorizontalProfileType.User,
                    sizeType = HorizontalProfileSizeType.Small,
                    imagePath = data.imagePath.value,
                    lv = user.lv,
                    name = data.nickName.value,
                    gender = data.gender.value,
                    age = data.birth.value?.toAge(),
                    useBg = false
                )
            }
        }
        TransparentButton (modifier = Modifier.matchParentSize()){
            pagePresenter.openPopup(
                PageProvider.getPageObject(PageID.User)
                    .addParam(PageParam.data, user)
                    .addParam(PageParam.subData, roomData)
            )
        }
    }
}


