package com.ironraft.pupping.bero.scene.page.profile.component.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditType
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.PagePresenter
import com.lib.util.toAge
import com.skeleton.sns.SnsUser
import com.skeleton.theme.*
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import dev.burnoo.cokoin.get


@Composable
fun PetProfileEdit(
    modifier: Modifier = Modifier,
    profile:PetProfile
) {
    val pagePresenter: PagePresenter = get()
    val name by profile.name.observeAsState()
    val gender by profile.gender.observeAsState()
    val birth by profile.birth.observeAsState()
    val introduction by profile.introduction.observeAsState()


    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
        ) {
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.name),
                text = name ?: "",
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.EditProfile)
                        .addParam(PageParam.type, ProfileEditType.Name )
                        .addParam(PageParam.data, profile)
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.gender),
                text = if(gender != null) stringResource(gender!!.title)  else "" ,
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.EditProfile)
                        .addParam(PageParam.type, ProfileEditType.Gender )
                        .addParam(PageParam.data, profile)
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.age),
                text = if(birth != null) birth!!.toAge()  else "" ,
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.EditProfile)
                        .addParam(PageParam.type, ProfileEditType.Birth )
                        .addParam(PageParam.data, profile)
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.introduction),
                text = if(introduction?.isNotEmpty() == true) introduction!!
                else profile.getIntroduction(LocalContext.current),
                useStroke = false,
                useMargin = false
            ){
                pagePresenter.openPopup(
                    PageProvider.getPageObject(PageID.EditProfile)
                        .addParam(PageParam.type, ProfileEditType.Introduction )
                        .addParam(PageParam.data, profile)
                )
            }
        }
    }
}

@Preview
@Composable
fun PetProfileEditComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PetProfileEdit(
            profile = PetProfile()
        )
    }
}