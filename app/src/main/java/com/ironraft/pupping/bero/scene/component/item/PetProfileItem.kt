package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import com.ironraft.pupping.bero.R
import com.skeleton.view.button.WrapTransparentButton
@Composable
fun PetProfileInfo(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Big,
    action: () -> Unit
){
    WrapTransparentButton(action = action) {
        PetProfileBody(
            modifier = modifier,
            profile = profile,
            sizeType = sizeType,
            action = {action()}
        )
    }
}

@Composable
fun PetProfileUser(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    friendStatus:FriendStatus? = null,
    distance:Double? = null,
    action: () -> Unit
){
    WrapTransparentButton(action = action) {
        PetProfileBody(
            modifier = modifier,
            profile = profile,
            sizeType = HorizontalProfileSizeType.Small,
            userId = profile.userId,
            friendStatus = friendStatus,
            distance = distance
        )
    }
}

@Composable
fun PetProfileEditable(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Big,
    funcType:HorizontalProfileFuncType = HorizontalProfileFuncType.Delete,
    isSelected:Boolean = false,
    action: (HorizontalProfileFuncType?) -> Unit
){
    PetProfileBody(
        modifier = modifier,
        profile = profile,
        sizeType = sizeType,
        funcType = funcType,
        isSelected = isSelected,
        action = action
    )
}

@Composable
fun PetProfileEmpty(
    modifier: Modifier = Modifier,
    description:String? = stringResource(id = R.string.addDogEmpty),
    action: (() -> Unit)
){
    WrapTransparentButton(action = action) {
        HorizontalProfile(
            modifier = modifier,
            type = HorizontalProfileType.Pet,
            sizeType = HorizontalProfileSizeType.Small,
            description = description,
            isEmpty = true,
            action = { action() }
        )
    }
}


@Composable
fun PetProfileBody(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Big,
    funcType:HorizontalProfileFuncType? = null,
    userId:String? = null,
    friendStatus:FriendStatus? = null,
    distance:Double? = null,
    isSelected:Boolean = false,
    action: ((HorizontalProfileFuncType?) -> Unit)? = null
) {

    val name by profile.name.observeAsState()
    val gender by profile.gender.observeAsState()
    val birth by profile.birth.observeAsState()
    val breed by profile.breed.observeAsState()
    val imagePath by profile.imagePath.observeAsState()
    val image by profile.image.observeAsState()
    val isNeutralized by profile.isNeutralized.observeAsState()

    HorizontalProfile(
        modifier = modifier,
        type = HorizontalProfileType.Pet,
        sizeType = sizeType,
        funcType = funcType,
        userId = userId,
        friendStatus = friendStatus,
        imagePath = imagePath,
        image = image,
        lv = profile.level,
        name = name,
        gender = gender,
        isNeutralized = isNeutralized,
        age = birth?.toAge(),
        breed = if(distance == null) breed else null,
        distance = distance,
        isSelected = isSelected,
        action = action
    )
}

@Preview
@Composable
fun PetProfileBodyComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(ColorApp.white)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            PetProfileEditable(
                profile = PetProfile().dummy()
            ){

            }
            PetProfileEmpty {
                
            }
            PetProfileBody(
                profile = PetProfile().dummy()
            )
        }
    }
}