package com.ironraft.pupping.bero.scene.page.profile.component.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.button.AgreeButton
import com.ironraft.pupping.bero.scene.component.button.AgreeButtonType
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditData
import com.ironraft.pupping.bero.scene.page.profile.ProfileEditType
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.util.toggle
import com.skeleton.theme.*
import com.skeleton.view.button.*


@Composable
fun InputTextEdit(
    prevData:String = "",
    type:ProfileEditType,
    needAgree:Boolean = false,
    edit: (ProfileEditData) -> Unit
) {
    val appTag = "InputTextEdit"

    var input:String by remember { mutableStateOf(prevData) }
    var isEditing:Boolean by remember { mutableStateOf(false) }
    var isAgree:Boolean by remember { mutableStateOf(prevData.isNotEmpty()) }
    val focusManager = LocalFocusManager.current
    fun onAction(){
        if (input.isEmpty()) return
        if (prevData == input) return
        when (type){

            ProfileEditType.Name -> edit(ProfileEditData(name = input))
            ProfileEditType.Introduction -> edit(ProfileEditData(introduction = input))
            ProfileEditType.Weight -> edit(ProfileEditData(weight = input.toDouble()))
            ProfileEditType.AnimalId -> {}
            ProfileEditType.AnimalId -> {}

            case .introduction :
            self.edit(.init(introduction : self.input))
            case .weight :
            self.edit(.init(weight : self.input.toDouble()))
            case .height :
            self.edit(.init(size : self.input.toDouble()))
            case .microchip :
            self.edit(.init(microchip : self.input))
            case .animalId :
            self.edit(.init(animalId : self.input))
            else -> {}
        }
    }
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() }
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.heavy.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            isEditing = it.isFocused
                        },
                    placeholder = {
                        Text(
                            type.placeHolder,
                            fontSize = FontSize.light.sp,
                            color = ColorApp.grey200
                        )
                    },
                    keyboardOptions = type.keyboardOptions,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onAction()
                        }
                    ),
                    value = input,
                    onValueChange = { if (it.length <= type.limitLength) input = it },
                    shape = RoundedCornerShape(DimenRadius.thin.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = ColorApp.white,
                        cursorColor = ColorBrand.primary,
                        focusedBorderColor = ColorBrand.primary,
                        unfocusedBorderColor = ColorApp.grey200
                    ),
                    singleLine = type.limitLine == 1,
                    minLines = type.limitLine
                )

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
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_save),
                        color = ColorBrand.primary,
                        isActive = input.isNotEmpty() && (prevData != input)
                    ) {
                        onAction()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun InputTextStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InputTextStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Name,
            next = {

            },
            prev = {

            }
        )
    }
}