package com.ironraft.pupping.bero.scene.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject


/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked] lambda
 * that triggers canceling the order and passes the final order to [onSendButtonClicked] lambda
 */
@Composable
fun PageTest(
    modifier: Modifier = Modifier,
    page:PageObject? = null
){
    val repository = koinInject<PageRepository>()
    val pagePresenter = koinInject<PageComposePresenter>()
    val resources = LocalContext.current.resources
    Column (
        modifier = modifier.fillMaxSize().background(ColorBrand.bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                repository.clearLogin()
            }
        ) {
            Text(stringResource(R.string.button_more))
        }
    }
}

@Preview
@Composable
fun PageTestPreview(){
    PageTest(
    )
}
