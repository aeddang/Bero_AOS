package com.skeleton.component.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.*
import com.skeleton.view.button.TransparentButton
import com.skeleton.view.button.WrapTransparentButton

enum class MenuTabType{
    Line {
        override fun bgColor(color:Color):Color = ColorTransparent.clearUi
    },
    Box {
        override var strokeWidth: Float = DimenStroke.light
        override var radius: Float = DimenRadius.medium
        override var textSize: Float = FontSize.thin
        override var btnBgColor:Color = ColorApp.white

    };
    open var strokeWidth:Float = 0.0f
    open var radius:Float = 0.0f
    open var textSize:Float = FontSize.light
    open var btnBgColor:Color = ColorTransparent.clearUi
    open fun bgColor(color:Color):Color = color
}

@Composable
fun MenuTab(
    modifier:Modifier = Modifier,
    type:MenuTabType = MenuTabType.Box,
    buttons:List<String>,
    selectedIdx:Int = 0,
    color:Color = ColorBrand.primary,
    bgColor:Color = ColorApp.gray200,
    height:Float = DimenButton.regular,
    isDivision:Boolean = true,
    action: ((Int) -> Unit)
) {
    AppTheme {
        Row(
            modifier = (if (isDivision) modifier.fillMaxWidth() else modifier.wrapContentSize())
                .height(height.dp)
                .clip(RoundedCornerShape(type.radius.dp))
                .background(type.bgColor(bgColor)),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttons.forEachIndexed { index, btn ->
                if (isDivision)
                    Box( modifier = Modifier.weight(1.0f)){
                        MenuTabButton(type = type, btn = btn, isSelected = index == selectedIdx, color = color, isDivision = true)
                        TransparentButton(
                            action = {
                                action(index)
                            }
                        )
                    }
                else
                    WrapTransparentButton(
                        action = {
                            action(index)
                        }
                    ){
                        MenuTabButton(type = type, btn = btn, isSelected = index == selectedIdx, color = color, isDivision = false)
                    }
            }
        }
    }
}
@Composable
private fun MenuTabButton(
    type:MenuTabType,
    btn:String,
    isSelected:Boolean,
    color:Color,
    isDivision:Boolean
) {
    Box(
        modifier = (if (isDivision) Modifier.fillMaxWidth() else Modifier.wrapContentSize())
            .fillMaxHeight()
            .clip(RoundedCornerShape(type.radius.dp))
            .background(if (isSelected) type.btnBgColor else ColorTransparent.clearUi)
            .border(
                width = if (isSelected) type.strokeWidth.dp else 0.dp,
                color = if (type.strokeWidth == 0.0f) ColorTransparent.clearUi
                else if (isSelected) ColorBrand.primary else ColorTransparent.clearUi,
                shape = RoundedCornerShape(type.radius.dp)
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            btn,
            fontSize = type.textSize.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) color else ColorApp.gray400,
            modifier = Modifier.padding(horizontal = DimenMargin.regular.dp),
        )
        if (type == MenuTabType.Line)
            Box(
                modifier = (if (isDivision) Modifier.fillMaxWidth() else Modifier.matchParentSize()),
                contentAlignment = Alignment.BottomCenter
            ){
                Spacer(
                    modifier = Modifier.fillMaxWidth()
                        .height(if (isSelected) DimenLine.regular.dp else DimenLine.light.dp)
                        .background(if (isSelected) color else ColorApp.gray100)
                )
            }


    }
}
@Preview
@Composable
fun MenuTabComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MenuTab(
            type = MenuTabType.Box,
            buttons = listOf("test0" , "test2", "test3"),
            selectedIdx = 1,
            isDivision = false
        ){

        }
        MenuTab(
            type = MenuTabType.Line,
            buttons = listOf("testttssdsdsds0" , "test2"),
            selectedIdx = 0,
            isDivision = false
        ){

        }
    }

}