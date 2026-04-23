package com.limelight.ui.utils

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.input.InputMode

enum class InputMethod {
    Touch,
    Dpad,
    Mouse
}

fun InputMethod.toInputMode(): InputMode {
    return when (this) {
        InputMethod.Touch -> InputMode.Touch
        InputMethod.Dpad -> InputMode.Keyboard
        InputMethod.Mouse -> InputMode.Touch
    }
}

val LocalInputMethod = staticCompositionLocalOf { InputMethod.Touch }
