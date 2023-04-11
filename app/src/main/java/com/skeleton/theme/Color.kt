package com.skeleton.theme

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Colors

class ColorBrand {
    companion object{
        val primary = ColorApp.orange
        val primaryVariant = ColorApp.orangeSub
        val secondary = ColorApp.green
        val secondaryVariant = ColorApp.greenDeep
        val thirdly = ColorApp.red
        val bg = ColorApp.white
    }
}

class ColorApp {
    companion object{
        val orange = Color(0xFFFF7D1F)
        val orangeSub = Color(0xFFFFF1E7)
        val orangeSub2 = Color(0xFFFFD2B1)
        val green =Color(0xFF13CEA1)
        val greenDeep = Color(0xFF00B189)
        val red = Color(0xFFF2270B)
        val blue = Color(0xFF88A1FB)
        val sky = Color(0xFF5EB3E4)
        val brown = Color(0xFF965F36)
        val pink = Color(0xFFFA7598)
        val yellow = Color(0xFFFFE749)
        val yellowDeep = Color(0xFFFFAC2F)
        val yellowSub = Color(0xFFFFF4D7)
        val black =  Color(0xFF333333)
        val white =  Color(0xFFffffff)
        val whiteDeepLight =  Color(0xFFf9f9fb)
        val whiteDeep =  Color(0xFFDEDEDE)
        val grey50 = Color(0xFFF9F9FB)
        val grey100 = Color(0xFFF1F2F5)
        val grey200 = Color(0xFFD4D8E1)
        val grey300 = Color(0xFFA7ABB5)
        val grey400 = Color(0xFF7C818B)
        val grey500 = Color(0xFF545861)
    }
}
class ColorTransparent {
    companion object {
        val clear = Color.Black.copy(0.0f)
        val clearUi = Color.Black.copy(0.0001f)
        val black80 = Color.Black.copy(0.8f)
        val black70 = Color.Black.copy(0.7f)
        val black50 = Color.Black.copy(0.5f)
        val black45 = Color.Black.copy(0.45f)
        val black15 = Color.Black.copy(0.15f)
    }
}

val DefaultColorPalette = Colors(
    primary = ColorBrand.primary,
    primaryVariant = ColorBrand.primaryVariant,
    secondary = ColorBrand.secondary,
    secondaryVariant = ColorBrand.secondaryVariant,
    background = ColorBrand.bg,
    surface = ColorApp.white,
    error = ColorApp.red,
    onPrimary = ColorApp.white,
    onSecondary = ColorApp.white,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = ColorApp.white,
    isLight = true
)

