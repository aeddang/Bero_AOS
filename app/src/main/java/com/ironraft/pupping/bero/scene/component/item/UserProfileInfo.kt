package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.skeleton.view.button.WrapTransparentButton
import dev.burnoo.cokoin.Koin

@Composable
fun UserProfileInfo(
    modifier: Modifier = Modifier,
    profile:UserProfile,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Small,
    action: (() -> Unit)? = null
) {
    if(action != null)
        WrapTransparentButton(
            { action() }
        ) {
            UserProfileInfoBody(
                modifier = modifier,
                profile = profile,
                sizeType = sizeType,
                action = {action()}
            )
        }
    else
        UserProfileInfoBody(
            modifier = modifier,
            profile = profile,
            sizeType = sizeType
        )
}
@Composable
fun UserProfileInfoBody(
    modifier: Modifier = Modifier,
    profile:UserProfile,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Small,
    action: ((HorizontalProfileFuncType) -> Unit)? = null
) {

    val name by profile.nickName.observeAsState()
    val description by profile.introduction.observeAsState()
    val gender by profile.gender.observeAsState()
    val birth by profile.birth.observeAsState()
    val imagePath by profile.imagePath.observeAsState()
    val image by profile.image.observeAsState()
    AppTheme {
        HorizontalProfile(
            type = HorizontalProfileType.Pet,
            sizeType = sizeType,
            imagePath = imagePath,
            image = image,
            name = name,
            gender = gender,
            age = birth?.toAge(),
            isSelected = false,
            useBg = false
        )
    }
}

@Preview
@Composable
fun UserProfileInfoComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier.background(ColorApp.white).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            UserProfileInfo(
                profile = UserProfile().dummy(),
                sizeType = HorizontalProfileSizeType.Small
            ){

            }
            UserProfileInfo(
                profile = UserProfile().dummy(),
                sizeType = HorizontalProfileSizeType.Big
            )
            UserProfileInfo(
                profile = UserProfile().dummy(),
                sizeType = HorizontalProfileSizeType.Tiny
            )
        }
    }
}