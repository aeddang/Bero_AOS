package com.skeleton.view.progress

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skeleton.theme.ColorApp


@Composable
fun LoadingIndicator(modifier: Modifier){
    CircularProgressIndicator(
        modifier = modifier.size(40.dp),
        color = ColorApp.gray100,
        strokeWidth = 5.dp)

}