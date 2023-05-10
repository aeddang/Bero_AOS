package com.skeleton.component.item.profile

import android.graphics.Bitmap
import android.view.inspector.InspectionCompanion
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.button.LvButton
import com.ironraft.pupping.bero.store.provider.model.Gender
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.theme.*
import com.skeleton.view.button.CircleButton
import com.skeleton.view.button.CircleButtonType
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.WrapTransparentButton
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.koin.compose.koinInject
import java.util.Objects

enum class VerticalProfileType{
    Pet {
        @DrawableRes override var emptyImage: Int = R.drawable.profile_dog_default
    },
    User{
        @DrawableRes override var emptyImage: Int = R.drawable.profile_user_default

    };
    @DrawableRes open var emptyImage:Int = -1
}
enum class VerticalProfileSizeType{
    Small {
        override var imageSize:Float = DimenProfile.light
    },
    Medium{
        override var imageSize:Float = DimenProfile.medium
    };
    open var imageSize:Float = 0.0f
}

@Composable
fun VerticalProfile(
    modifier: Modifier = Modifier,
    type:VerticalProfileType = VerticalProfileType.Pet,
    alignment:Alignment.Horizontal = Alignment.CenterHorizontally,
    sizeType:VerticalProfileSizeType = VerticalProfileSizeType.Medium,
    isSelected:Boolean = false,
    image:Bitmap? = null,
    imagePath:String? = null,
    lv:Int? = null,
    name:String? = null,
    gender:Gender? = null,
    isNeutralized:Boolean? = null,
    age:String? = null,
    breed:String? = null,
    info:String? = null,
    description:String? = null,
    viewProfileImage: (() ->Unit)? = null,
    viewProfile: (() -> Unit)? = null,
    editProfile: (() -> Unit)? = null
) {
    val pagePresenter = koinInject<PageComposePresenter>()
    AppTheme {
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
            horizontalAlignment = alignment
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = if(alignment == Alignment.CenterHorizontally) Alignment.BottomCenter else Alignment.BottomStart

            ) {
                WrapTransparentButton(
                    {
                        viewProfileImage?.let {
                            it()
                            return@WrapTransparentButton
                        }
                        viewProfile?.let { it() }
                    }
                ){
                    ProfileImage(
                        image = image,
                        imagePath = imagePath,
                        size = sizeType.imageSize,
                        emptyImagePath = type.emptyImage,
                        onEdit = editProfile
                    )
                }
                if  (editProfile != null) {
                    CircleButton(
                        type = CircleButtonType.Icon,
                        icon = R.drawable.edit,
                        isSelected = false,
                        strokeWidth = DimenStroke.regular,
                        defaultColor = ColorApp.black
                    ){
                        editProfile()
                    }
                } else if(viewProfile != null){
                    CircleButton(
                        type = CircleButtonType.Icon,
                        icon = R.drawable.search,
                        isSelected = false,
                        strokeWidth = DimenStroke.regular,
                        defaultColor = ColorApp.black
                    ){
                        viewProfile()
                    }
                } else if(lv != null){
                    val lvValue = Lv.getLv(lv)
                    LvButton(lv = lvValue, text = lv.toString() ){
                        /*
                        Toast(context).showCustomToast(
                            lvValue.title,
                            context
                        )*/
                    }
                }


            }

        }
    }
}

@Preview
@Composable
fun VerticalProfileComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        VerticalProfile(
        ) {

        }

    }

}