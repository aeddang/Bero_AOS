package com.ironraft.pupping.bero.activityui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ironraft.pupping.bero.AppSceneObserver
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import org.koin.compose.koinInject
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEventType
import com.ironraft.pupping.bero.scene.page.walk.component.SimpleWalkBox
import com.skeleton.component.dialog.Check
import com.skeleton.component.dialog.Sheet
import com.skeleton.component.dialog.SheetBtnData
import kotlinx.coroutines.launch

data class CheckData(
    val text: String,
    @DrawableRes var icon: Int? = null,
    var isAuto:Boolean = true
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityLayerController(){
    val viewModel = koinInject<AppSceneObserver>()
    var isShowCheck by remember { mutableStateOf(false) }
    var checkData:CheckData? by remember { mutableStateOf(null) }

    val event = viewModel.event.observeAsState()
    event.value.let {
        val evt = it ?: return@let
        when (evt.type) {
            SceneEventType.Check -> {
                (evt.value as? CheckData)?.let { data->
                    checkData = data
                    isShowCheck = true
                }
                viewModel.event.value = null
            }
            else -> return@let
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Check(
            isShow = isShowCheck,
            icon = checkData?.icon,
            text = checkData?.text ?: "",
            isAuto = checkData?.isAuto ?: true
        ) {
            isShowCheck = false
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            SimpleWalkBox()
        }
    }
}