package com.skeleton.component.dialog

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Alert() {
    MaterialTheme {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
            },
            title = {
                Text(text = "Dialog Title")
            },
            text = {
                Text("Here is a text ")
            },
            confirmButton = {
                Button(
                    onClick = {
                    }
                    ) {
                    Text("This is the Confirm Button")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                    }) {
                    Text("This is the dismiss Button")
                }
            }
        )
    }
}

@Preview
@Composable
fun AlertComposePreview(){
    Alert()
}