package com.ironraft.pupping.bero.scene.page.profile.component.step

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
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.skeleton.theme.*
import com.skeleton.view.button.*


@Composable
fun InputTextStep(
    profile:ModifyPetProfileData?,
    focusRequester :FocusRequester? = null,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {
    val appTag = "InputTextStep"
    fun getPrevData() : String{
        return when (step){
            PageAddDogStep.Name -> profile?.name ?: ""
            else -> ""
        }
    }
    var input:String by remember { mutableStateOf(getPrevData()) }
    var isEditing:Boolean by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    fun onAction(){
        if (input.isEmpty()) return
        when (step){
            PageAddDogStep.Name -> next(ModifyPetProfileData(name = input))
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
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            isEditing = it.isFocused
                        },
                    placeholder = {
                        Text(
                            step.placeHolder,
                            fontSize = FontSize.light.sp,
                            color = ColorApp.grey200
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onAction()
                        }
                    ),

                    value = input,
                    onValueChange = { if (it.length <= step.limitedTextLength) input = it },
                    shape = RoundedCornerShape(DimenRadius.thin.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = ColorApp.white,
                        cursorColor = ColorBrand.primary,
                        focusedBorderColor = ColorBrand.primary,
                        unfocusedBorderColor = ColorApp.grey200
                    ),
                    singleLine = true
                )
                step.inputDescription?.let {
                    AnimatedVisibility(visible = !isEditing) {
                        Text(
                            it,
                            fontSize = FontSize.thin.sp,
                            color = ColorApp.grey400
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1.0f))
                if (step.isSkipAble) {
                    AnimatedVisibility(visible = !isEditing) {
                        TextButton(
                            defaultText = stringResource(id = R.string.button_skipNow),
                            textFamily = FontWeight.Medium,
                            textSize = FontSize.thin,
                            textColor = ColorApp.grey500,
                            isUnderLine = true
                        ) {
                            next(ModifyPetProfileData())
                        }
                    }
                }
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
                        isActive = input.isNotEmpty()
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