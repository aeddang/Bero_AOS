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
fun UserProfileTopInfo(
    modifier: Modifier = Modifier,
    profile:UserProfile,
    isHorizontal:Boolean = false,
    isSimple:Boolean = false,
    viewProfileImage: (() -> Unit)? = null,
    action: (() -> Unit)? = null
) {

    val name by profile.nickName.observeAsState()
    val description by profile.introduction.observeAsState()
    val gender by profile.gender.observeAsState()
    val birth by profile.birth.observeAsState()
    val lv by profile.lv.observeAsState()
    val imagePath by profile.imagePath.observeAsState()
    val image by profile.image.observeAsState()
    AppTheme {
        Column(
            modifier = modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp)
        ) {
            if (!isHorizontal)
                VerticalProfile(
                    type = VerticalProfileType.Pet,
                    sizeType = VerticalProfileSizeType.Medium,
                    isSelected = true,
                    imagePath = imagePath,
                    image = image,
                    lv = lv,
                    name = name,
                    gender = gender,
                    age = birth?.toAge(),
                    description = if(isSimple) null else description,
                    viewProfileImage = viewProfileImage,
                    editProfile = if(profile.isMine) action else null
                )
            else
                HorizontalProfile(
                    type = HorizontalProfileType.Pet,
                    sizeType = HorizontalProfileSizeType.Big,
                    imagePath = imagePath,
                    image = image,
                    lv = lv,
                    name = name,
                    gender = gender,
                    age = birth?.toAge(),
                    isSelected = false,
                    useBg = false
                )
            if(!isSimple && isHorizontal)
                description?.let {
                    Text(
                        it,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(corner = CornerSize(DimenRadius.tiny.dp)))
                            .background(ColorApp.whiteDeepLight)
                            .padding(all = DimenMargin.light.dp)

                    )
                }
        }
    }
}

@Preview
@Composable
fun UserProfileTopInfoComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier.background(ColorApp.white).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            UserProfileTopInfo(
                profile = UserProfile().dummy(),
            ){

            }
            UserProfileTopInfo(
                profile = UserProfile().dummy(),
                isSimple = true
            )
            UserProfileTopInfo(
                profile = UserProfile().dummy(),
                isHorizontal = true
            )
        }
    }
}