package com.lib.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skeleton.theme.DimenMargin

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }


@Composable
fun Grid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    verticalSpace:Float = 0.0f,
    horizontalSpace:Float = 0.0f,
    content: @Composable() (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpace.dp)

    ) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }
        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns
            Row (
                horizontalArrangement = Arrangement.spacedBy(horizontalSpace.dp)
            ){
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    if (index < itemCount) {
                        content(index)
                    }
                }
            }
        }
    }
}