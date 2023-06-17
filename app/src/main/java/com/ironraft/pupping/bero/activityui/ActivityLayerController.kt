package com.ironraft.pupping.bero.activityui

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import org.koin.compose.koinInject
import com.ironraft.pupping.bero.SceneEventType
import com.ironraft.pupping.bero.scene.page.layer.LayerPageLevelUp
import com.ironraft.pupping.bero.scene.page.layer.LayerPageTutorial
import com.ironraft.pupping.bero.scene.page.walk.component.SimpleWalkBox
import com.skeleton.component.dialog.Check
import com.skeleton.view.progress.CircleWave

data class CheckData(
    val text: String,
    @DrawableRes var icon: Int? = null,
    var isAuto:Boolean = true,
    var handler: (() -> Unit)? = null
)


@Composable
fun ActivityLayerController(){
    val viewModel = koinInject<AppSceneObserver>()
    var isShowCheck by remember { mutableStateOf(false) }
    var checkData:CheckData? by remember { mutableStateOf(null) }

    var isShowLevelUp by remember { mutableStateOf(false) }

    var isShowTutorial by remember { mutableStateOf(false) }
    @RawRes var tutorialId:Int? by remember { mutableStateOf(null) }
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
            SceneEventType.LevelUp -> {
                isShowLevelUp = true
                viewModel.event.value = null
            }
            SceneEventType.ShowTutorial -> {
                (evt.value as? Int)?.let { data->
                    tutorialId = data
                    isShowTutorial = true
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
        AnimatedVisibility(visible = isShowLevelUp, enter = fadeIn(), exit = fadeOut()) {
            LayerPageLevelUp {
                isShowLevelUp = false
            }
        }
        AnimatedVisibility(visible = isShowTutorial, enter = fadeIn(), exit = fadeOut()) {
            LayerPageTutorial(ani = tutorialId ?: -1 ) {
                isShowTutorial = false
            }
        }
        Check(
            isShow = isShowCheck,
            icon = checkData?.icon,
            text = checkData?.text ?: "",
            isAuto = checkData?.isAuto ?: true
        ) {
            isShowCheck = false
            checkData?.handler?.let { it() }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            SimpleWalkBox()
        }

    }
}