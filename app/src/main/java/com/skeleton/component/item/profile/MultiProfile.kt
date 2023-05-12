package com.skeleton.component.item.profile

import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.button.FriendButton
import com.ironraft.pupping.bero.scene.component.button.FriendButtonType
import com.ironraft.pupping.bero.scene.component.button.LvButton
import com.ironraft.pupping.bero.scene.component.button.LvButtonType
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.theme.*
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject


enum class MultiProfileType{
    Pet {
        @DrawableRes override var emptyImage: Int = R.drawable.profile_dog_default
    },
    User{
        @DrawableRes override var emptyImage: Int = R.drawable.profile_user_default

    };
    @DrawableRes open var emptyImage:Int = -1
    fun getButtonSelectStatus(circleButtontype:CircleButtonType):Boolean{
        return when (circleButtontype){
            CircleButtonType.Text, CircleButtonType.Image -> true
            else -> false
        }
    }

    fun getStroke(circleButtontype:CircleButtonType, size:MultiProfileSizeType):Float{
        return when (circleButtontype){
            CircleButtonType.Image ->
                if(size == MultiProfileSizeType.Small) DimenStroke.regular
                else DimenStroke.heavy
            else -> DimenStroke.regular
        }
    }
}
enum class MultiProfileSizeType{
    Small {
        override var imageSize:Float = DimenProfile.thin
    },
    Big{
        override var imageSize:Float = DimenProfile.mediumUltra

    };
    open var imageSize:Float = 0.0f

}



@Composable
fun MultiProfile(
    modifier: Modifier = Modifier,
    type:MultiProfileType = MultiProfileType.Pet,
    sizeType:MultiProfileSizeType = MultiProfileSizeType.Big,
    circleButtonType:CircleButtonType? = null,
    @DrawableRes circleButtonIcon: Int? = null,
    circleButtonValue:String? = null,
    userId:String? = null,
    friendStatus:FriendStatus? = null,
    image:Bitmap? = null,
    imagePath:String? = null,
    imageSize:Float? = null,
    name:String? = null,
    lv:Int? = null,
    buttonAction: (() -> Unit)? = null
) {
    val pagePresenter:PageComposePresenter = get()
    AppTheme {
        Column (
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = modifier
                    .wrapContentSize()
                    .height((imageSize ?: sizeType.imageSize).dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                ProfileImage(
                    image = image,
                    imagePath = imagePath,
                    size = imageSize ?: DimenProfile.mediumUltra,
                    emptyImagePath = type.emptyImage
                )
                Row(
                    modifier = Modifier
                        .width(((imageSize ?: DimenProfile.mediumUltra)+DimenMargin.thin).dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.micro.dp,
                        alignment = Alignment.End
                    ),
                    verticalAlignment = Alignment.Bottom
                ) {

                    if(friendStatus != null) {
                        friendStatus.buttons.forEach {
                            FriendButton(
                                type = FriendButtonType.Icon,
                                userId = userId,
                                userName = name,
                                funcType = it
                            )
                        }
                    } else if(circleButtonType != null) {
                        CircleButton(
                            type = circleButtonType,
                            value = circleButtonValue,
                            icon = circleButtonIcon,
                            isSelected = type.getButtonSelectStatus(circleButtonType),
                            strokeWidth = type.getStroke(circleButtonType, sizeType),
                            defaultColor = ColorApp.black,
                            activeColor = ColorBrand.primary
                        ){
                            buttonAction?.let { it() }
                        }

                    } else if(lv != null) {
                        val lvValue = Lv.getLv(lv)
                        LvButton(
                            lv = lvValue,
                            type = LvButtonType.Small,
                            text = lv.toString()
                        ){
                            Toast(pagePresenter.activity).showCustomToast(
                               lvValue.title,
                               pagePresenter.activity
                            )
                        }
                    }
                }
            }
            name?.let {
                Text(
                    it,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontSize.tiny.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = ColorApp.white,
                    modifier = Modifier.width((imageSize ?: sizeType.imageSize).dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun MultiProfileComposePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MultiProfile(
                type = MultiProfileType.Pet,
                circleButtonType = CircleButtonType.Icon,
                circleButtonIcon = R.drawable.edit,
                imagePath = null,
                name = "name"
            ) {

            }
            MultiProfile(
                type = MultiProfileType.User,
                sizeType = MultiProfileSizeType.Small,
                circleButtonType = CircleButtonType.Icon,
                circleButtonIcon = R.drawable.edit,
                imagePath = null,
                imageSize = DimenProfile.thin,
                name = "name"
            ) {

            }
        }
    }
}