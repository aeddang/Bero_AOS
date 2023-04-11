package com.skeleton.theme
import androidx.compose.material.Typography
import androidx.compose.ui.text.font.*
import com.ironraft.pupping.bero.R

class FontSize {
    companion object {
        var black:Float = 28.0f
        var bold:Float =  24.0f
        var medium:Float =  20.0f
        var regular:Float = 18.0f
        var light:Float =  16.0f
        var thin:Float = 14.0f
        var tiny:Float = 12.0f
        var micro:Float = 9.0f
        var microExtra:Float = 8.0f
    }
}

val DefaultFontFamily = FontFamily(
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold) ,
    Font(R.font.poppins_semibold, FontWeight.SemiBold) ,
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_black, FontWeight.Black)
)

val DefaultTypography = Typography(
    defaultFontFamily = DefaultFontFamily
)