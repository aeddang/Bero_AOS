package com.skeleton.component.item.profile

import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.button.*
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.Gender
import com.ironraft.pupping.bero.store.provider.model.Lv
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.page.PageComposePresenter
import com.lib.util.showCustomToast
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get

enum class HorizontalProfileType{
    Pet {
        @DrawableRes override var emptyImage: Int = R.drawable.profile_dog_default
        @StringRes override var emptyTitle: Int = R.string.pageTitle_addDog
    },
    User{
        @DrawableRes override var emptyImage: Int = R.drawable.profile_user_default

    },
    Place{},
    Multi{};
    @DrawableRes open var emptyImage:Int = -1
    @StringRes open var emptyTitle:Int = -1
    open var radius:Float = DimenRadius.light
    open var padding:Float = DimenMargin.regularExtra

}
enum class HorizontalProfileSizeType{
    Small {
        override var imageSize:Float = DimenProfile.regular
    },
    Big{
        override var imageSize:Float = DimenProfile.heavyExtra
        override var titleSpacing:Float = DimenMargin.light
        override var lvType:LvButtonType = LvButtonType.Small
        override var nameSize:Float = FontSize.medium
    },
    Tiny{
        override var imageSize:Float = DimenProfile.lightExtra
    };
    open var lvType:LvButtonType = LvButtonType.Tiny
    open var imageSize:Float = 0.0f
    open var titleSpacing:Float = DimenMargin.micro
    open var nameSize:Float = FontSize.light
}

enum class HorizontalProfileFuncType{
    AddFriend, SortButton, More, MoreFunc, Delete, Send, Check, UnCheck,
    View, Block, UnBlock;
    @get:DrawableRes
    val icon: Int
        get() = when (this) {
            More -> R.drawable.direction_right
            MoreFunc -> R.drawable.more_vert
            Delete -> R.drawable.delete
            Block, UnBlock -> R.drawable.block
            AddFriend -> R.drawable.add_friend
            Send -> R.drawable.chat
            Check, UnCheck -> R.drawable.check
            else -> R.drawable.search
        }

    val strokeColor:Color?
        get() = when (this) {
            Check -> ColorBrand.primary
            else -> null
        }

}

@Composable
fun HorizontalProfile(
    modifier: Modifier = Modifier,
    type:HorizontalProfileType = HorizontalProfileType.Pet,
    @DrawableRes typeIcon:Int? = null,
    typeValue:String? = null,
    sizeType:HorizontalProfileSizeType = HorizontalProfileSizeType.Small,
    funcType:HorizontalProfileFuncType? = null,
    funcValue:String? = null,
    funcViewColor:Color = ColorBrand.primary,
    userId:String? = null,
    friendStatus:FriendStatus? = null,
    color:Color = ColorBrand.primary,
    image:Bitmap? = null,
    imagePath:String? = null,
    lv:Int? = null,
    name:String? = null,
    date:String? = null,
    gender:Gender? = null,
    isNeutralized:Boolean? = null,
    age:String? = null,
    breed:String? = null,
    description:String? = null,
    distance:Double? = null,
    withImagePath:String? = null,
    isSelected:Boolean = false,
    isEmpty:Boolean = false,
    useBg:Boolean = true,
    bgColor:Color = ColorApp.white,
    action: ((HorizontalProfileFuncType?) -> Unit)? = null
) {
    val pagePresenter:PageComposePresenter = get()
    AppTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerSize(if (useBg) type.radius.dp else 0.dp)))
                .background(
                    if (useBg) {
                        if (isSelected && !isEmpty) color else bgColor
                    } else {
                        ColorTransparent.clear
                    }
                )
                .border(
                    width = if (useBg) DimenStroke.light.dp else 0.dp,
                    color = if (useBg) funcType?.strokeColor
                        ?: ColorApp.grey100 else ColorTransparent.clear,
                    shape = RoundedCornerShape(type.radius.dp)
                )
                .padding(if (useBg) type.padding.dp else 0.dp)
            ,
            horizontalArrangement = Arrangement.spacedBy(
                space = DimenMargin.regularExtra.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(type){
                HorizontalProfileType.Place ->
                    typeIcon?.let {
                        Box(
                            modifier = Modifier
                                .size(DimenButton.medium.dp)
                                .clip(RoundedCornerShape(CornerSize(DimenRadius.tiny.dp)))
                                .background(ColorApp.orangeSub),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painterResource(it),
                                contentDescription = "",
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(if (isSelected) ColorApp.white else color),
                                modifier = Modifier.size(DimenIcon.regular.dp)
                            )
                        }
                    }
                HorizontalProfileType.Multi ->
                    MultiProfile(
                        type = MultiProfileType.User,
                        sizeType = MultiProfileSizeType.Small,
                        circleButtonType = CircleButtonType.Image,
                        circleButtonValue = typeValue,
                        image = image,
                        imagePath = imagePath,
                        imageSize = DimenProfile.thin
                    ){
                        action?.let { it(null) }
                    }

                else ->
                    Box(
                        modifier = Modifier.wrapContentSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        ProfileImage(
                            image = image,
                            imagePath = imagePath,
                            size = sizeType.imageSize,
                            emptyImagePath = type.emptyImage
                        )
                        lv?.let {
                            val lvValue = Lv.getLv(it)
                            LvButton(
                                lv = lvValue,
                                type = sizeType.lvType,
                                text = it.toString()
                            ){
                                Toast(pagePresenter.activity).showCustomToast(
                                    lvValue.title,
                                    pagePresenter.activity
                                )
                            }
                        }
                        action?.let { ac ->
                            TransparentButton(modifier = Modifier.matchParentSize()){
                                ac(null)
                            }
                        }
                    }
            }
            Column (
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (isEmpty){
                    Text(
                        stringResource(id = type.emptyTitle),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontSize.medium.sp,
                        color = ColorApp.grey300
                    )
                    description?.let {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey300
                        )
                    }
                } else {
                    if(name != null || date != null)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.thin.dp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            name?.let {
                                Text(
                                    it,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = sizeType.nameSize.sp,
                                    color = if(isSelected) ColorApp.white else ColorApp.black
                                )
                            }
                            date?.let {
                                Text(
                                    it,
                                    fontSize = FontSize.tiny.sp,
                                    color = ColorApp.grey300
                                )
                            }
                        }

                    Column (
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if(gender != null || age != null)
                            ProfileInfoDescription(
                                age = age,
                                gender = gender,
                                isNeutralized = isNeutralized,
                                useCircle = false,
                                color = if(isSelected) ColorApp.white else ColorApp.grey500
                            )

                        breed?.let { breed ->
                            SystemEnvironment.breedCode[breed]?.let { breedValue ->
                                Text(
                                    breedValue ,
                                    fontSize = FontSize.thin.sp,
                                    color = if(isSelected) ColorApp.white else color
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tiny.dp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            description?.let {
                                Text(
                                    it ,
                                    fontSize = FontSize.thin.sp,
                                    color = if(isSelected) ColorApp.white else ColorApp.grey500
                                )
                            }
                            distance?.let {
                                Text(
                                    WalkManager.viewDistance(it),
                                    fontSize = FontSize.thin.sp,
                                    color = if(isSelected) ColorApp.white else color
                                )
                            }
                        }
                    }
                }
            }
            userId?.let {userId ->
                friendStatus?.let { friendStatus ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp)
                    ) {
                        friendStatus.buttons.forEach {
                            FriendButton(
                                type = FriendButtonType.Icon,
                                userId = userId,
                                funcType = it
                            )
                        }
                    }
                }
            }
            if (isEmpty) {
                ImageButton(
                    defaultImage = R.drawable.add,
                    defaultColor = if(isSelected) ColorApp.white else color
                ){
                    action?.let { it(null) }
                }

            } else if (funcType != null){
                when (funcType) {
                    HorizontalProfileFuncType.Delete, HorizontalProfileFuncType.More,
                    HorizontalProfileFuncType.MoreFunc, HorizontalProfileFuncType.Block, HorizontalProfileFuncType.UnBlock ->
                        ImageButton(
                            defaultImage = funcType.icon,
                            defaultColor = if(isSelected) ColorApp.white else ColorApp.grey400
                        ){
                            action?.let { it(funcType) }
                        }
                    HorizontalProfileFuncType.SortButton ->
                        SortButton(
                            type = SortButtonType.Fill,
                            sizeType = SortButtonSizeType.Small,
                            text = funcValue ?: "",
                            color = if(isSelected) ColorApp.white else color,
                            isSort = false
                        ) {
                            action?.let { it(funcType) }
                        }
                    HorizontalProfileFuncType.AddFriend, HorizontalProfileFuncType.Send,
                    HorizontalProfileFuncType.Check, HorizontalProfileFuncType.UnCheck ->
                        CircleButton(
                            type = CircleButtonType.Icon,
                            icon = funcType.icon,
                            isSelected = true,
                            activeColor = color

                        ){
                            action?.let { it(funcType) }
                        }
                    HorizontalProfileFuncType.View ->
                        WrapTransparentButton(
                            action = { action?.let { it(funcType) } }
                        ) {
                            Text(
                                funcValue ?: "" ,
                                fontWeight = FontWeight.Medium,
                                fontSize = FontSize.tiny.sp,
                                color = ColorApp.white ,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(funcViewColor)
                                    .padding(
                                        all = DimenMargin.micro.dp
                                    )
                            )
                        }
                }
            }
            withImagePath?.let {
                ProfileImageRect(imagePath = it){
                    action?.let { it(HorizontalProfileFuncType.View) }
                }
            }
        }
    }
}

@Preview
@Composable
fun HorizontalProfileComposePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        val scroll: ScrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalProfile(
                type = HorizontalProfileType.Pet,
                funcType = HorizontalProfileFuncType.SortButton,
                funcValue = "button",
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog"
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.Pet,
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog",
                isSelected = true,
                withImagePath = ""

            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.Pet,
                sizeType = HorizontalProfileSizeType.Small,
                description = "jadhjkasdhjksahdjkashdjksa",
                isEmpty = true
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.Place,
                typeIcon = R.drawable.goal,
                sizeType = HorizontalProfileSizeType.Small,
                color = ColorApp.red,
                name = "name",
                description = "August 23, 2023"
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.User,
                funcType = HorizontalProfileFuncType.AddFriend,
                sizeType = HorizontalProfileSizeType.Big,
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog",
                isSelected = false
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.Multi,
                typeValue = "",
                funcType = HorizontalProfileFuncType.AddFriend,
                sizeType = HorizontalProfileSizeType.Small,
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog",
                isSelected = false
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.Pet,
                typeValue = "",
                funcType = HorizontalProfileFuncType.Check,
                sizeType = HorizontalProfileSizeType.Small,
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog",
                isSelected = false
            ) {

            }
            HorizontalProfile(
                type = HorizontalProfileType.User,
                funcType = HorizontalProfileFuncType.AddFriend,
                sizeType = HorizontalProfileSizeType.Big,
                color = ColorBrand.primary,
                image = null,
                imagePath = null,
                name = "name",
                gender = Gender.Female,
                age = "20",
                breed = "dog",
                isSelected = false,
                useBg = false
            ) {

            }
        }
    }
}