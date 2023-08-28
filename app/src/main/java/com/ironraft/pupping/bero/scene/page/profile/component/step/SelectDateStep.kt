package com.ironraft.pupping.bero.scene.page.profile.component.step

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.util.toAge
import com.lib.util.toDate
import com.lib.util.toDateFormatter
import com.lib.util.toLocalDate
import com.skeleton.theme.*
import com.skeleton.view.button.*
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

@Composable
fun SelectDateStep(
    profile:ModifyPetProfileData?,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {
    val appTag = "SelectDateStep"
    fun getPrevData() : Date?{
        return when (step){
            PageAddDogStep.Birth -> profile?.birth
            else -> null
        }
    }
    var selectDate:Date  by remember { mutableStateOf(getPrevData() ?: Date()) }
    val calendar:Calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    fun onAction(){
        when (step){
            PageAddDogStep.Birth -> next(ModifyPetProfileData(birth = selectDate))
            else -> {}
        }
    }

    AppTheme {
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WheelDatePicker(
                modifier = Modifier.fillMaxWidth(),
                startDate = selectDate.toLocalDate() ?: LocalDate.now(),
                maxDate = LocalDate.now(),
                yearsRange = IntRange(currentYear-100, currentYear),
                size = DpSize(256.dp, 256.dp),
                rowCount = 7,
                textColor = ColorBrand.primary,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = true,
                    shape = RoundedCornerShape(DimenRadius.thinExtra.dp),
                    color = ColorBrand.primary.copy(alpha = 0.1f),
                    border = null
                )
            ){ date ->
                selectDate = date.toDateFormatter("yyyyMMdd")?.toDate("yyyyMMdd") ?: Date()

            }
            SortButton(
                type = SortButtonType.StrokeFill,
                sizeType = SortButtonSizeType.Small,
                text = selectDate.toAge(),
                color = ColorApp.orange600,
                isSort = false,
                isSelected = true
            ) {
                //selectDate = LocalDate.now()
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
                    color = ColorBrand.primary
                ) {
                    onAction()
                }

            }
        }
    }
}


@Preview
@Composable
fun SelectDateStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectDateStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Name,
            next = {

            },
            prev = {

            }
        )
    }

}