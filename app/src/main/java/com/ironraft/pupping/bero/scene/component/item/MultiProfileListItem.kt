package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.rest.UserAndPet
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PagePresenter
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
class MultiProfileListItemData{
    var index:Int = -1; private set
    var user:UserProfile? = null; private set
    var pet:PetProfile? = null; private set
    var lv:Int? = null; private set

    fun setData(data:UserAndPet, idx:Int) : MultiProfileListItemData{
        index = idx
        data.user?.let {
            user = UserProfile().setData(it)
            lv = it.level
        }
        data.pet?.let {
            pet = PetProfile().init(it)
        }
        return this
    }
}
@Composable
fun MultiProfileListItem(
    modifier: Modifier = Modifier,
    data:MultiProfileListItemData,
) {
    val pagePresenter: PagePresenter = get()
    AppTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(124.dp)
                .clip(RoundedCornerShape(CornerSize(DimenRadius.light.dp)))
                .background(ColorApp.white)
                .border(
                    width = DimenStroke.light.dp,
                    color = ColorApp.grey100,
                    shape = RoundedCornerShape(DimenRadius.light.dp)
                )
                .padding(DimenMargin.light.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = DimenMargin.regularExtra.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.pet?.let {petProfile ->
                Column(
                    Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    HorizontalProfile(
                        type = HorizontalProfileType.Pet,
                        imagePath = petProfile.imagePath.value,
                        lv = data.lv,
                        name = petProfile.name.value,
                        gender = petProfile.gender.value,
                        age = petProfile.birth.value?.toAge(),
                        useBg = false,
                        action = {
                            pagePresenter.openPopup(
                                PageProvider.getPageObject(PageID.Dog)
                                    .addParam(PageParam.data, petProfile)
                            )
                        }
                    )
                    petProfile.breed.value?.let { breed->
                        SystemEnvironment.breedCode[breed]?.let {
                            Text(
                                it,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = FontSize.thin.sp,
                                color = ColorApp.grey400
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier
                .fillMaxHeight()
                .width(DimenLine.light.dp)
                .background(ColorApp.grey100)
            )
            data.user?.let { userProfile ->
                Column(
                    Modifier.weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    HorizontalProfile(
                        type = HorizontalProfileType.User,
                        imagePath = userProfile.imagePath.value,
                        lv = data.lv,
                        name = userProfile.nickName.value,
                        gender = userProfile.gender.value,
                        age = userProfile.birth.value?.toAge(),
                        useBg = false,
                        action = {
                            pagePresenter.openPopup(
                                PageProvider.getPageObject(PageID.User)
                                    .addParam(PageParam.id, userProfile.userId)
                            )
                        }
                    )
                }
            }
        }
    }

}


