package com.ironraft.pupping.bero.scene.page.profile.component.step

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
import com.ironraft.pupping.bero.scene.component.button.AgreeButton
import com.ironraft.pupping.bero.scene.component.button.AgreeButtonType
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
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
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.*
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID

@Composable
fun SelectGenderStep(
    profile:ModifyPetProfileData?,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {

    fun getPrevData() : Gender?{
        return when (step){
            PageAddDogStep.Gender -> profile?.gender
            else -> null
        }
    }

    val appTag = "SelectGenderStep"
    var selectGender:Gender? by remember { mutableStateOf(getPrevData()) }
    var isNeutralized:Boolean by remember { mutableStateOf(profile?.isNeutralized ?: false) }
    fun onAction(){
        when (step){
            PageAddDogStep.Gender -> next(
                ModifyPetProfileData(gender = selectGender, isNeutralized = isNeutralized)
            )
            else -> {}
        }
    }


    AppTheme {
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
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
                ){
                    selectGender = Gender.Male
                }
                RectButton(
                    modifier = Modifier.weight(1.0f),
                    icon = Gender.Female.icon,
                    text = stringResource(id = Gender.Female.title),
                    isSelected = selectGender?.equals(Gender.Female) ?: false,
                    color = Gender.Female.color
                ){
                    selectGender = Gender.Female
                }
            }
            AgreeButton(
                type = AgreeButtonType.Neutralized,
                isChecked = isNeutralized){
                isNeutralized = it
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!step.isFirst) {
                    FillButton(
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_prev),
                        color = ColorApp.black
                    ) {
                        prev()
                    }
                }
                FillButton(
                    modifier = Modifier.weight(1.0f),
                    type = FillButtonType.Fill,
                    text = stringResource(id = R.string.button_next),
                    color = ColorBrand.primary,
                    isActive = selectGender != null
                ) {
                    onAction()
                }

            }
        }
    }
}


@Preview
@Composable
fun SelectGenderStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectGenderStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Name,
            next = {

            },
            prev = {

            }
        )
    }

}