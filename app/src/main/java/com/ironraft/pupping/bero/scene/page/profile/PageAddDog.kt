package com.ironraft.pupping.bero.scene.page.profile
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
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.*
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.page.PageComposePresenter
import com.lib.page.PageObject
import com.lib.util.ComponentLog
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject

@Composable
fun PageAddDog(
    modifier: Modifier = Modifier,
    page: PageObject? = null
){
    val repository = koinInject<PageRepository>()
    val appSceneObserver = koinInject<AppSceneObserver>()
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
            Text(stringResource(R.string.button_logOut))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                appSceneObserver.sheet.value = ActivitSheetEvent(
                    type = ActivitSheetType.Select,
                    title = "Sheet",
                    text = "SheetText",
                    icon = R.drawable.add_chat,
                    buttons = arrayListOf("test0", "test1")
                ){ select ->
                    ComponentLog.d("selected$select", "test")
                }
            }
        ) {
            Text(stringResource(R.string.button_more))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                appSceneObserver.select.value = ActivitSelectEvent(
                    type = ActivitSelectType.ImgPicker
                ){ select ->
                    ComponentLog.d("selected$select", "test")
                }
            }
        ) {
            Text(stringResource(R.string.button_takeCamera))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                appSceneObserver.radio.value = ActivitRadioEvent(
                    type = ActivitRadioType.Select,
                    title = "Title",
                    text = "description",
                    buttons = arrayListOf("radio1", "radio2")
                ){ select ->
                    ComponentLog.d("selected$select", "test")
                }
            }
        ) {
            Text(stringResource(R.string.button_more))
        }
    }
}

@Preview
@Composable
fun PageAddDogPreview(){
    com.ironraft.pupping.bero.scene.page.PageTest(
    )
}
