package com.ironraft.pupping.bero.scene.page.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.component.calendar.CPCalendar
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.FontSize


@Composable
fun ReportText(
    modifier: Modifier = Modifier,
    leading:String = "",
    value:String = "",
    trailing:String = ""
) {
    val annotatedString = buildAnnotatedString {
        append(leading)
        withStyle(style = SpanStyle(ColorBrand.primary)) {
            append(" $value")
        }
        append(trailing)
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        fontWeight = FontWeight.SemiBold,
        fontSize = FontSize.medium.sp,
        color = ColorApp.black
    )
}

@Preview
@Composable
fun ReportTextComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReportText(
            leading = "leading",
            value = "value",
            trailing = "trailing"
        )
    }
}