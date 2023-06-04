package com.ironraft.pupping.bero.scene.page.walk.component

import android.graphics.PointF
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.graph.PolygonGraph
import com.ironraft.pupping.bero.scene.page.walk.model.PlayMapModel
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.WalkEvenType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.WalkStatus
import com.ironraft.pupping.bero.store.walk.model.WalkPathItem
import com.lib.page.ComponentViewModel
import com.lib.page.PagePresenter
import com.lib.util.replace
import com.skeleton.component.item.PropertyInfo
import com.skeleton.component.item.PropertyInfoType
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenRadius
import com.skeleton.theme.DimenStroke
import com.skeleton.theme.FontSize
import com.skeleton.view.button.ImageButton
import dev.burnoo.cokoin.get

@Composable
fun WalkBox(
    modifier: Modifier = Modifier,
    playMapModel:PlayMapModel
) {
    val pagePresenter: PagePresenter = get()
    val dataProvider: DataProvider = get()
    val walkManager: WalkManager = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    fun updateTitle(status: WalkStatus?):String{
        var title = ""
        when (status){
            WalkStatus.Walking -> {
                title = pagePresenter.activity.getString(R.string.walkPlayText)
            }
            else -> {
                val name = dataProvider.user.representativeName
                title = pagePresenter.activity.getString(R.string.walkStartText).replace(name)
            }
        }
        return title
    }

    var title:String by remember { mutableStateOf( updateTitle(walkManager.status.value) ) }
    var isWalk:Boolean by remember { mutableStateOf(walkManager.status.value == WalkStatus.Walking) }
    var pathSelectIdx:List<Int> by remember { mutableStateOf(listOf()) }
    var pathPoints:List<PointF> by remember { mutableStateOf(listOf()) }
    val isSimple by walkManager.isSimpleView.observeAsState()
    val walkTime by walkManager.walkTime.observeAsState()
    val walkDistance by walkManager.walkDistance.observeAsState()
    val walkStatus = walkManager.status.observeAsState()
    val walkEvent = walkManager.event.observeAsState()
    val isHidden by playMapModel.componentHidden.observeAsState()

    fun updatePath(paths:List<WalkPathItem>?){
        val path = paths ?: listOf()
        pathSelectIdx = path.map{it.idx}
        pathPoints = path.map { PointF(it.tx, it.ty) }
    }
    walkStatus.value.let { status ->
        val walk = status == WalkStatus.Walking
        if (walk == isWalk) return@let
        isWalk = walk
        title = updateTitle(status)
        updatePath(walkManager.walkPath?.paths)
    }

    walkEvent.value.let { evt ->
        val e = evt ?: return@let
        when (e.type){
            WalkEvenType.UpdateViewLocation -> {
                if (!viewModel.isValidValue(e)) return@let
                updatePath(walkManager.walkPath?.paths)
            }
            else -> {}
        }
    }

    AppTheme {
        AnimatedVisibility(visible = isHidden != true ) {
            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(DimenRadius.light.dp))
                    .background(if (isSimple == false) ColorApp.white else ColorTransparent.clear)
                    .border(
                        width = DimenStroke.light.dp,
                        color = if (isSimple == false) ColorApp.grey100 else ColorTransparent.clear,
                        shape = RoundedCornerShape(DimenRadius.light.dp)
                    )
                    .padding(
                        all = (if (isSimple == false) DimenMargin.regularExtra else 0f).dp
                    )
                ,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AnimatedVisibility(visible = isSimple == false ) {
                        Text(
                            title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = FontSize.regular.sp,
                            color = if (isWalk) ColorBrand.primary else ColorApp.grey500,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    ImageButton(
                        isSelected = false,
                        defaultImage = R.drawable.search_user,
                        isOrigin = true,
                        size = DimenIcon.heavyExtra
                    ){
                        //self.pagePresenter.openPopup(PageProvider.getPageObject(.popupWalkUsers))
                    }
                }
                AnimatedVisibility(visible = isSimple == false ) {
                    if(isWalk) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DimenMargin.thin.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DimenMargin.tiny.dp,
                            )
                        ) {
                            PropertyInfo(
                                modifier = Modifier.weight(1.0f),
                                type = PropertyInfoType.Impect,
                                value = WalkManager.viewDuration(walkTime),
                                unit = stringResource(id = R.string.time)
                            )
                            PropertyInfo(
                                modifier = Modifier.weight(1.0f),
                                type = PropertyInfoType.Impect,
                                value = WalkManager.viewDistance(walkDistance),
                                unit = stringResource(id = R.string.km)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(DimenRadius.light.dp))
                                    .background(ColorApp.grey50)
                            ) {
                                PolygonGraph(
                                    modifier = Modifier.size(50.dp, 50.dp),
                                    screenHeight = 50f,
                                    screenWidth = 50f,
                                    selectIdx = pathSelectIdx,
                                    points = pathPoints
                                )
                            }
                        }
                    } else {
                        LocationInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DimenMargin.medium.dp)
                        )
                    }
                }
            }
        }
    }
}
