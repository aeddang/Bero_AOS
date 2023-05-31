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
import com.ironraft.pupping.bero.store.provider.model.Gender
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.util.isEqualDay
import com.lib.util.toAge
import com.lib.util.toDate
import com.lib.util.toDateFormatter
import com.lib.util.toLocalDate
import com.lib.util.toLocalDateTime
import com.lib.util.toggle
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.Koin
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

@Composable
fun SelectDateEdit(
    prevData: Date = Date(),
    type: ProfileEditType,
    needAgree:Boolean = false,
    edit: (ProfileEditData) -> Unit
) {
    val appTag = "SelectDateEdit"
    var selectDate:Date by remember { mutableStateOf(prevData) }
    var isAgree:Boolean by remember { mutableStateOf(prevData.time != Date().time) }

    val calendar:Calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    fun onAction(){
        if (!isAgree && needAgree) return
        if (prevData.isEqualDay(selectDate)) return
        when (type){
            ProfileEditType.Birth -> edit(
                ProfileEditData(birth = selectDate)
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
                WheelDatePicker(
                    modifier = Modifier.fillMaxWidth(),
                    startDate = selectDate.toLocalDate() ?: LocalDate.now(),
                    maxDate = LocalDate.now(),
                    yearsRange = IntRange(currentYear - 100, currentYear),
                    size = DpSize(256.dp, 256.dp),
                    rowCount = 7,
                    textColor = ColorBrand.primary,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = true,
                        shape = RoundedCornerShape(DimenRadius.thinExtra.dp),
                        color = ColorBrand.primary.copy(alpha = 0.1f),
                        border = null
                    )
                ) { date ->
                    selectDate = date.toDateFormatter("yyyyMMdd")?.toDate("yyyyMMdd") ?: Date()
                }
                SortButton(
                    type = SortButtonType.StrokeFill,
                    sizeType = SortButtonSizeType.Small,
                    text = selectDate.toAge(),
                    color = ColorApp.orange,
                    isSort = false,
                    isSelected = true
                ) {
                    //selectDate = LocalDate.now()
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
                    else (!prevData.isEqualDay(selectDate))
                ) {
                    onAction()
                }
            }
        }
    }
}


@Preview
@Composable
fun SelectDateEditComposePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SelectDateEdit(
                type = ProfileEditType.Birth,
                needAgree = true
            ) {

            }
        }
    }

}