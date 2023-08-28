package com.skeleton.view.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.skeleton.module.firebase.Analytics
import com.skeleton.view.switch.Switch
import dev.burnoo.cokoin.get

enum class RadioButtonType() {
    Text {
        override var iconColor:Color = ColorApp.black
        override var textColor:Color = ColorApp.black
    },
    Blank {
        @DrawableRes override var icon:Int? = R.drawable.check
        override var iconSize: Float = DimenIcon.light
    },
    Stroke {
        override var strokeWidth: Float = DimenStroke.light
        @DrawableRes override var icon:Int? = R.drawable.check_circle
        override var iconSize: Float = DimenIcon.medium
        override var spacing: Float = DimenMargin.tinyExtra
        override var horizontalMargin: Float = DimenMargin.thin
        override var bgColor:Color = ColorApp.white
    },
    CheckOn {
        @DrawableRes override var icon:Int? = R.drawable.check_circle
        override var useFill:Boolean = false
        override var iconSize: Float = DimenIcon.medium
    },
    SwitchOn{};

    open var strokeWidth:Float = 0.0f
    @DrawableRes open var icon:Int? = null
    open var useFill:Boolean = true
    open var iconSize:Float = 0.0f
    open var spacing:Float = 0.0f
    open var horizontalMargin:Float = 0.0f
    open var bgColor:Color = ColorTransparent.clear
    open var iconColor:Color = ColorApp.gray200
    open var textColor:Color = ColorApp.gray400
}


@Composable
fun RadioButton(
    type: RadioButtonType = RadioButtonType.Text,
    isChecked: Boolean,
    @DrawableRes icon:Int? = null,
    text:String? = null,
    description:String? = null,
    color:Color = ColorBrand.primary,
    modifier: Modifier = Modifier,
    action:(Boolean) -> Unit
) {
    val analytics: Analytics = get()
    AppTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(DimenRadius.thinExtra.dp))
                .background(type.bgColor)
                .border(
                    width = type.strokeWidth.dp,
                    color = if (type.strokeWidth > 0.0f)
                        if (isChecked) color else ColorApp.gray100
                    else type.bgColor,

                    shape = RoundedCornerShape(DimenRadius.thinExtra.dp)
                ),
            contentAlignment = Alignment.CenterStart

        ) {
            Row(
                Modifier.padding(horizontal = type.horizontalMargin.dp, vertical = DimenMargin.thin.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.thin.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = if(description?.isEmpty() == false) Alignment.Top else Alignment.CenterVertically
            ) {
                icon?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(if (isChecked) color else type.iconColor),
                        modifier = Modifier.size(DimenIcon.light.dp)
                    )
                }
                Column(
                    modifier = Modifier.padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.micro.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.Start
                ) {
                    text?.let {
                        Text(
                            it,
                            fontSize = FontSize.light.sp,
                            color = if (isChecked) color else type.textColor,
                            textAlign = TextAlign.Start
                        )
                    }
                    description?.let {
                        Text(
                            it,
                            fontSize = FontSize.tiny.sp,
                            color = ColorApp.gray400,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                if (type.useFill) Spacer(modifier = Modifier.weight(1.0f))
                when(type){
                    RadioButtonType.SwitchOn ->
                        Switch(isOn = isChecked){
                            action(!isChecked)
                        }
                    else ->
                        if (!(type == RadioButtonType.Blank && !isChecked))
                            type.icon?.let {
                                Image(
                                    painterResource(it),
                                    contentDescription = "",
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(if (isChecked) color else type.iconColor),
                                    modifier = Modifier.size(type.iconSize.dp)
                                )
                            }

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((DimenIcon.medium + (DimenMargin.thin * 2) + (if (description?.isEmpty() == false) FontSize.thin else 0.0f)).dp)
            ) {
                TransparentButton(
                    action = {
                        val checked = !isChecked
                        val parameter = HashMap<String,String>()
                        parameter["buttonType"] = "RadioButton"
                        parameter["buttonText"] = text ?: icon.toString()
                        parameter["isChecked"] = checked.toString()
                        action(checked)
                    }
                )
            }
        }

    }
}

@Preview
@Composable
fun RadioButtonComposePreview(){
    Column (
        modifier = Modifier
            .background(ColorApp.white)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(
            type = RadioButtonType.Text,
            isChecked = true,
            icon = R.drawable.neutralized,
            text = "Radio Button"
        ) {

        }
        RadioButton(
            type = RadioButtonType.Stroke,
            isChecked = true,
            text = "Radio Button"
        ) {

        }
        RadioButton(
            type = RadioButtonType.CheckOn,
            isChecked = true,
            icon = R.drawable.neutralized,
            text = "Radio Button"
        ) {

        }
        RadioButton(
            type = RadioButtonType.SwitchOn,
            isChecked = true,
            text = "Switch Button"
        ) {

        }
        RadioButton(
            type = RadioButtonType.SwitchOn,
            isChecked = false,
            text = "Switch Button"
        ) {

        }
        RadioButton(
            type = RadioButtonType.Blank,
            isChecked = true,
            icon = R.drawable.neutralized,
            text = "Blank Button",
            description = "description"
        ) {

        }
        RadioButton(
            type = RadioButtonType.Blank,
            isChecked = false,
            text = "Blank Button",
            description = "description"
        ) {

        }
    }

}