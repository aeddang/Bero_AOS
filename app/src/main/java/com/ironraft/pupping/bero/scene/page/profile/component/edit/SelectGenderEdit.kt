package com.ironraft.pupping.bero.scene.page.profile.component.edit

import android.graphics.Bitmap
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.button.AgreeButton
import com.ironraft.pupping.bero.scene.component.button.AgreeButtonType
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditData
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditType
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.provider.model.Gender
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.page.PageAppViewModel
import com.lib.page.PageComposePresenter
import com.lib.page.PageEventType
import com.lib.page.PagePresenter
import com.lib.util.AppUtil
import com.lib.util.DataLog
import com.lib.util.toAge
import com.lib.util.toggle
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.Koin
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID

@Composable
fun SelectGenderEdit(
    prevData:Gender? = null,
    prevNeutralized:Boolean = false,
    type: ProfileEditType,
    needAgree:Boolean = false,
    edit: (ProfileEditData) -> Unit
) {
    val appTag = "SelectGenderEdit"
    var selectGender:Gender? by remember { mutableStateOf(prevData) }
    var isAgree:Boolean by remember { mutableStateOf(prevData != null) }
    var isNeutralized:Boolean by remember { mutableStateOf(prevNeutralized ?: false) }
    fun onAction(){
        val gender = selectGender ?: return
        if (!isAgree && needAgree) return
        if (prevData == gender && isNeutralized == prevNeutralized) return
        when (type){
            ProfileEditType.Gender -> edit(
                ProfileEditData(gender = gender, isNeutralized = isNeutralized)
            )
            else -> {}
        }
    }
    AppTheme {
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.heavy.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.tinyExtra.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RectButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Gender.Male.icon,
                        text = stringResource(id = Gender.Male.title),
                        isSelected = selectGender?.equals(Gender.Male) ?: false,
                        color = Gender.Male.color
                    ) {
                        selectGender = Gender.Male
                    }
                    if (needAgree)
                        RectButton(
                            modifier = Modifier.weight(1.0f),
                            icon = Gender.Neutral.icon,
                            text = stringResource(id = Gender.Neutral.title),
                            isSelected = selectGender?.equals(Gender.Neutral) ?: false,
                            color = Gender.Neutral.color
                        ) {
                            selectGender = Gender.Neutral
                        }
                    RectButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Gender.Female.icon,
                        text = stringResource(id = Gender.Female.title),
                        isSelected = selectGender?.equals(Gender.Female) ?: false,
                        color = Gender.Female.color
                    ) {
                        selectGender = Gender.Female
                    }
                }
                if (!needAgree)
                    AgreeButton(
                        type = AgreeButtonType.Neutralized,
                        isChecked = isNeutralized
                    ) {
                        isNeutralized = it
                    }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp)
            ) {
                if(needAgree)
                    AgreeButton(
                        type = AgreeButtonType.Privacy,
                        isChecked = isAgree){
                        isAgree = isAgree.toggle()
                    }
                FillButton(
                    type = FillButtonType.Fill,
                    text = stringResource(id = R.string.button_save),
                    color = ColorBrand.primary,
                    isActive =
                        if( !isAgree && needAgree ) false
                        else (isNeutralized != prevNeutralized || selectGender != prevData)
                ) {
                    onAction()
                }
            }
        }
    }
}


@Preview
@Composable
fun SelectGenderEditComposePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SelectGenderEdit(
                type = ProfileEditType.Gender,
                needAgree = true
            ) {

            }
        }
    }

}