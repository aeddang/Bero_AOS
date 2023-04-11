package com.skeleton.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = DefaultColorPalette,
        content = content,
        typography = DefaultTypography
    )
}