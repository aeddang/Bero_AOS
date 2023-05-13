package com.skeleton.theme

import androidx.compose.ui.geometry.Size

class Dimens {
    companion object Line {

    }
}

class  DimenItem{
    companion object
    {
        val albumList:Size = Size(width=160.0f, height=160.0f)
        val walkList:Size = Size(width=335.0f, height=200.0f)
        val petList:Float = 288.0f
    }
}

class DimenMargin {
    companion object
    {
        const val heavy : Float = 56.0f
        const val heavyExtra : Float = 48.0f
        const val mediumUltra : Float = 40.0f
        const val medium : Float = 32.0f
        const val regularUltra : Float = 24.0f
        const val regular : Float = 20.0f
        const val regularExtra : Float = 16.0f
        const val light : Float = 14.0f
        const val thin : Float = 12.0f
        const val tiny : Float = 10.0f
        const val tinyExtra : Float = 8.0f
        const val microUltra : Float = 6.0f
        const val micro : Float = 4.0f
        const val microExtra : Float = 2.0f
    }
}
class DimenIcon {
    companion object {
        val heavyUltra: Float = 72.0f
        val heavy: Float = 64.0f
        val heavyExtra: Float = 48.0f
        val mediumUltra: Float = 40.0f
        val medium: Float = 32.0f
        val regular: Float = 28.0f
        val light: Float = 24.0f
        val thin: Float = 20.0f
        val tiny: Float = 16.0f
        val microUltra: Float = 8.0f
        val micro: Float = 4.0f
    }
}

class DimenProfile {
    companion object {
        val heavy: Float = 120.0f
        val heavyExtra: Float = 96.0f
        val mediumUltra: Float = 84.0f
        val medium: Float = 80.0f
        val regular: Float = 56.0f
        val light: Float = 48.0f
        val lightExtra: Float = 46.0f
        val thin: Float = 40.0f
        val tiny: Float = 32.0f
    }
}

class DimenTab {
    companion object {
        val heavy: Float = 88.0f
        val medium: Float = 52.0f
        val regular: Float = 46.0f
        val light: Float = 36.0f//
        val thin: Float = 24.0f//
    }
}

class DimenButton {
    companion object {
        val heavy: Float = 72.0f
        val medium: Float = 56.0f
        val mediumExtra: Float = 52.0f

        val regular: Float = 48.0f
        val regularExtra: Float = 40.0f
        val light: Float = 36.0f
        val thin: Float = 32.0f //
    }

}

class DimenRadius {
    companion object {
        val heavy: Float = 32.0f
        val mediumUltra: Float = 28.0f
        val medium: Float = 24.0f
        val regular: Float = 20.0f
        val light: Float = 16.0f
        val lightExtra: Float = 14.0f
        val thin: Float = 12.0f
        val thinExtra: Float = 10.0f
        val tiny: Float = 8.0f
        val micro: Float = 4.0f//
    }
}
class DimenCircle {
    companion object {
        val regular: Float = 40.0f
        val thin: Float = 4.0f
    }
}

class DimenBar {
    companion object {
        val medium: Float = 34.0f //
        val regular: Float = 16.0f
        val light: Float = 4.0f
    }
}

class DimenLine {
    companion object {
        val heavy: Float = 12.0f
        val medium: Float = 6.0f//
        val regular: Float = 2.0f
        val light: Float = 1.0f
    }
}


class DimenStroke {
    companion object {
        val heavyUltra: Float = 5.0f
        val heavy: Float = 4.0f
        val medium: Float = 3.0f
        val regular: Float = 2.0f
        val light: Float = 1.0f
    }
}

class DimenApp {
    companion object {
        val bottom: Float = 64.0f
        val top: Float = 50.0f
        val chatBox: Float = 64.0f
        val pageHorinzontal: Float = DimenMargin.regular
    }
}