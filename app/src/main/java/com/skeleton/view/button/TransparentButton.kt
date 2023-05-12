package com.skeleton.view.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skeleton.theme.ColorTransparent

@Composable
fun TransparentButton(
    modifier: Modifier = Modifier.fillMaxSize(),
    action:() -> Unit
){
    FloatingActionButton(
        onClick = action,
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        backgroundColor = ColorTransparent.clear,
        contentColor = contentColorFor(ColorTransparent.clear),
        elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp)
    ) {

    }
}

@Composable
fun WrapTransparentButton(
    action:() -> Unit,
    content: @Composable () -> Unit
){
    FloatingActionButton(
        onClick = action,
        modifier = Modifier.wrapContentSize().padding(0.dp),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = ColorTransparent.clear,
        contentColor = contentColorFor(ColorTransparent.clear),
        elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp),
        content = content
    )
}