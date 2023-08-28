package com.skeleton.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable

class ColorBrand {
    companion object{
        val primary = ColorApp.orange600
        val primaryVariant = ColorApp.orange100
        val secondary = ColorApp.green600
        val secondaryVariant = ColorApp.green800
        val thirdly = ColorApp.red600
        val bg = ColorApp.white
    }
}

class ColorApp {
    companion object{
        /*
        val orange = Color(0xFFFF7D1F)
        val orange200 = Color(0xFFFFF1E7)
        val orange2002 = Color(0xFFFFD2B1)
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
        val gray200 =  Color(0xFFf9f9fb)
        val whiteDeep =  Color(0xFFDEDEDE)
        val gray50 = Color(0xFFF9F9FB)
        val gray100 = Color(0xFFF1F2F5)
        val gray200 = Color(0xFFD4D8E1)
        val gray300 = Color(0xFFA7ABB5)
        val gray400 = Color(0xFF7C818B)
        val gray500 = Color(0xFF545861)
        */
        val white = Color(0xFFFFFFFF)
        val black = Color(0xFF000000)

        val gray100 = Color(0xFFFFFFFF)
        val gray200 = Color(0xFFF3F5F9)
        val gray300 = Color(0xFFEDEFF3)
        val gray400 = Color(0xFFE5E7EB)
        val gray500 = Color(0xFFD0D3DA)
        val gray600 = Color(0xFFA7ABB5)
        val gray700 = Color(0xFF7C818B)
        val gray800 = Color(0xFF595C63)
        val gray900 = Color(0xFF3A3C41)
        val gray950 = Color(0xFF2A2B2C)

        val orange100 = Color(0xFFFFF6EF)
        val orange200 = Color(0xFFFFECDB)
        val orange300 = Color(0xFFFFD7B1)
        val orange400 = Color(0xFFFFBE82)
        val orange500 = Color(0xFFFF9E44)
        val orange600 = Color(0xFFF0802F)
        val orange700 = Color(0xFFCF6314)
        val orange800 = Color(0xFFAD4E00)
        val orange900 = Color(0xFF873800)
        val orange950 = Color(0xFF612500)

        val red100 = Color(0xFFFFF0F0)
        val red200 = Color(0xFFFFC7C7)
        val red300 = Color(0xFFFFA6A6)
        val red400 = Color(0xFFF56F6F)
        val red500 = Color(0xFFF54E4E)
        val red600 = Color(0xFFDE3737)
        val red700 = Color(0xFFC92731)
        val red800 = Color(0xFFA8071A)
        val red900 = Color(0xFF820014)
        val red950 = Color(0xFF5C0011)

        val pink100 = Color(0xFFFFF0F6)
        val pink200 = Color(0xFFFFD6E7)
        val pink300 = Color(0xFFFFADD2)
        val pink400 = Color(0xFFFF85C0)
        val pink500 = Color(0xFFF960A9)
        val pink600 = Color(0xFFEB2F96)
        val pink700 = Color(0xFFC41D7F)
        val pink800 = Color(0xFF9E1068)
        val pink900 = Color(0xFF780650)
        val pink950 = Color(0xFF520339)

        val yellow100 = Color(0xFFFEFFE6)
        val yellow200 = Color(0xFFFFFFB8)
        val yellow300 = Color(0xFFFFFB8F)
        val yellow400 = Color(0xFFFFF566)
        val yellow500 = Color(0xFFFFEC3D)
        val yellow600 = Color(0xFFFADB14)
        val yellow700 = Color(0xFFD4B106)
        val yellow800 = Color(0xFFAD8B00)
        val yellow900 = Color(0xFF876800)
        val yellow950 = Color(0xFF614700)

        val gold100 = Color(0xFFFFFBE6)
        val gold200 = Color(0xFFFFF1B8)
        val gold300 = Color(0xFFFFE58F)
        val gold400 = Color(0xFFFFD854)
        val gold500 = Color(0xFFFFC53D)
        val gold600 = Color(0xFFFAAD14)
        val gold700 = Color(0xFFD48806)
        val gold800 = Color(0xFFAD6800)
        val gold900 = Color(0xFF965F36)
        val gold950 = Color(0xFF613400)

        val green100 = Color(0xFFEBFCF0)
        val green200 = Color(0xFFC9FBDD)
        val green300 = Color(0xFF9AF1B7)
        val green400 = Color(0xFF51DF8A)
        val green500 = Color(0xFF11C579)
        val green600 = Color(0xFF0DB26D)
        val green700 = Color(0xFF0D9E61)
        val green800 = Color(0xFF04784E)
        val green900 = Color(0xFF00522B)
        val green950 = Color(0xFF05412B)

        val mint100 = Color(0xFFE6FFFB)
        val mint200 = Color(0xFFB5F5EC)
        val mint300 = Color(0xFF87E8DE)
        val mint400 = Color(0xFF71E4D0)
        val mint500 = Color(0xFF36CFC9)
        val mint600 = Color(0xFF13C2C2)
        val mint700 = Color(0xFF08979C)
        val mint800 = Color(0xFF006D75)
        val mint900 = Color(0xFF00474F)
        val mint950 = Color(0xFF002329)

        val blue100 = Color(0xFFE6F9FF)
        val blue200 = Color(0xFFBAEEFF)
        val blue300 = Color(0xFF91DEFF)
        val blue400 = Color(0xFF49C6FE)
        val blue500 = Color(0xFF19AAFB)
        val blue600 = Color(0xFF1890FF)
        val blue700 = Color(0xFF096DD9)
        val blue800 = Color(0xFF0050B3)
        val blue900 = Color(0xFF003A8C)
        val blue950 = Color(0xFF002766)

        val ocean100 = Color(0xFFF0F5FF)
        val ocean200 = Color(0xFFD6E4FF)
        val ocean300 = Color(0xFFADC6FF)
        val ocean400 = Color(0xFF85A5FF)
        val ocean500 = Color(0xFF597EF7)
        val ocean600 = Color(0xFF2F54EB)
        val ocean700 = Color(0xFF1D39C4)
        val ocean800 = Color(0xFF10239E)
        val ocean900 = Color(0xFF061178)
        val ocean950 = Color(0xFF030852)

        val purple100 = Color(0xFFF9F0FF)
        val purple200 = Color(0xFFEFDBFF)
        val purple300 = Color(0xFFD3ADF7)
        val purple400 = Color(0xFF9A7DEB)
        val purple500 = Color(0xFF8054DE)
        val purple600 = Color(0xFF622ED1)
        val purple700 = Color(0xFF531DAB)
        val purple800 = Color(0xFF391085)
        val purple900 = Color(0xFF22075E)
        val purple950 = Color(0xFF120338)
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
    error = ColorApp.red600,
    onPrimary = ColorApp.white,
    onSecondary = ColorApp.white,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = ColorApp.white,
    isLight = true
)

