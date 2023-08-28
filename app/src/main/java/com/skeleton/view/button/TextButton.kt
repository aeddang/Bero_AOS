package com.skeleton.view.button
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.module.firebase.Analytics
import com.skeleton.theme.*
import dev.burnoo.cokoin.get

enum class TextButtonType() {
    Box {
        override var bgColor:Color = ColorApp.gray200
        override var bgRadius:Float = DimenRadius.regular
        override var paddingVertical:Float = DimenMargin.tinyExtra
        override var paddingHorizontal:Float = DimenMargin.light
        override var textFamily:FontWeight = FontWeight.Medium
        override var textSize:Float = FontSize.thin
        override var textColor:Color = ColorApp.gray400
        override var activeColor:Color = ColorApp.white
    },
    Blank {};

    open var bgColor:Color = ColorTransparent.clear
    open var bgRadius:Float = 0.0f
    open var paddingVertical:Float = 0.0f
    open var paddingHorizontal:Float = 0.0f
    open var textFamily:FontWeight = FontWeight.SemiBold
    open var textSize:Float = FontSize.light
    open var textColor:Color = ColorApp.black
    open var activeColor:Color = ColorBrand.primary

}

@Composable
fun TextButton(
    type: TextButtonType = TextButtonType.Blank,
    defaultText:String,
    activeText:String? = null,
    isSelected:Boolean = false,
    index:Int = 0,
    textFamily:FontWeight? = null,
    textSize:Float? = null,
    textColor:Color? = null,
    textActiveColor:Color? = null,
    isUnderLine:Boolean = false,
    @DrawableRes image:Int? = null,
    imageSize:Float = DimenIcon.tiny,
    isOriginImage:Boolean = true,
    spacing:Float = DimenMargin.tiny,
    modifier: Modifier = Modifier,
    action:(Int) -> Unit
) {
    val analytics: Analytics = get()
    AppTheme {
        Box(
            modifier = modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(type.bgRadius.dp))
                .background(type.bgColor)
            ,
            contentAlignment = Alignment.CenterStart

        ) {
            Row(
                Modifier.padding(horizontal = type.paddingHorizontal.dp, vertical = type.paddingVertical.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if(isSelected) activeText ?: defaultText else defaultText,
                    fontSize = (textSize ?: type.textSize).sp,
                    fontWeight = textFamily ?: type.textFamily,
                    color = if (isSelected) textActiveColor ?: type.activeColor else textColor ?: type.textColor,
                    textAlign = TextAlign.Start,
                    style = if(isUnderLine) TextStyle(textDecoration = TextDecoration.Underline) else LocalTextStyle.current
                )
                image?.let {
                    Image(
                        painterResource(it),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = if (isOriginImage) null
                            else ColorFilter.tint(if (isSelected) textActiveColor ?: type.activeColor else textColor ?: type.textColor),
                        modifier = Modifier.size(imageSize.dp)
                    )
                }
            }
            TransparentButton(
                modifier = Modifier.matchParentSize(),
                action =  {
                    val parameter = HashMap<String,String>()
                    parameter["buttonType"] = "TextButton"
                    parameter["buttonText"] = defaultText
                    action(index)
                }
            )
        }
    }
}

@Preview
@Composable
fun TextButtonComposePreview(){
    Column (
        modifier = Modifier
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(
            type = TextButtonType.Box,
            defaultText = "TextButton",
            image = com.ironraft.pupping.bero.R.drawable.add
        ) {

        }
        TextButton(
            type = TextButtonType.Blank,
            defaultText = "TexgButtonp",
            image = com.ironraft.pupping.bero.R.drawable.add
        ) {

        }
        TextButton(
            type = TextButtonType.Blank,
            defaultText = "TexgButtonp",
            image = com.ironraft.pupping.bero.R.drawable.add,
            isSelected = true
        ) {

        }
    }

}