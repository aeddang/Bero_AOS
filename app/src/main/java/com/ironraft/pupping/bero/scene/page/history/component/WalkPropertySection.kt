package com.ironraft.pupping.bero.scene.page.history.component

import android.graphics.PointF
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.WalkReport
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.skeleton.component.item.PropertyInfo
import com.skeleton.component.item.PropertyInfoType
import com.skeleton.theme.AppTheme
import com.skeleton.theme.DimenMargin
import com.ironraft.pupping.bero.scene.component.graph.PolygonGraph

@Composable
fun WalkPropertySection(
    modifier: Modifier = Modifier,
    mission:Mission,
    action: ((Int) -> Unit)? = null
) {

    AppTheme {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            mission.walkPath?.paths?.let { paths ->
                if(paths.isNotEmpty()){
                    Box (contentAlignment = Alignment.Center){
                        Image(
                            painterResource(R.drawable.route_bg),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        val size:Float = 160.0f
                        PolygonGraph(
                            modifier = Modifier.size(size.dp, size.dp),
                            screenHeight = size,
                            screenWidth = size,
                            selectIdx = paths.filter { it.smallPictureUrl != null }.map { it.idx },
                            points = paths.map{PointF(it.tx, it.ty )},
                            action = action
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tiny.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ){
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.schedule,
                    title = stringResource(id = R.string.time),
                    value = mission.viewDuration
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.speed,
                    title = stringResource(id = R.string.speed),
                    value = mission.viewSpeed
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.navigation_outline,
                    title = stringResource(id = R.string.distance),
                    value = mission.viewDistance
                )
            }
        }
    }
}

@Composable
fun PetWalkPropertySection(
    modifier: Modifier = Modifier,
    profile:PetProfile
) {


    var duration by remember { mutableStateOf("") }
    var speed by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    fun setupDatas():Boolean{
        val d = profile.exerciseDistance
        val dr = profile.exerciseDuration
        distance = WalkManager.viewDistance(d)
        duration = WalkManager.viewDuration(dr)
        val dh = dr/3600
        val spd = if(d == 0.0 || dh == 0.0) 0.0 else d/dh
        speed = WalkManager.viewSpeed(spd)
        return true
    }
    val isInit by remember { mutableStateOf(setupDatas() ) }
    AppTheme {
        if(isInit) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.thin.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.schedule,
                    title = "Total. " + stringResource(id = R.string.time),
                    value = duration
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.speed,
                    title = "Avg. " + stringResource(id = R.string.speed),
                    value = speed
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.navigation_outline,
                    title = "Total. " + stringResource(id = R.string.distance),
                    value = distance
                )
            }
        }
    }
}


@Composable
fun ReportWalkPropertySection(
    modifier: Modifier = Modifier,
    data:WalkReport
) {

    var duration by remember { mutableStateOf("") }
    var speed by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    fun setupDatas():Boolean{
        val d = data.distance ?: 0.0
        val dr = data.duration ?: 0.0
        distance = WalkManager.viewDistance(d)
        duration = WalkManager.viewDuration(dr)
        val dh = dr/3600
        val spd = if(d == 0.0 || dh == 0.0) 0.0 else d/dh
        speed = WalkManager.viewSpeed(spd)
        return true
    }
    val isInit by remember { mutableStateOf(setupDatas() ) }
    AppTheme {
        if (isInit) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.thin.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.schedule,
                    title = "Total. " + stringResource(id = R.string.time),
                    value = duration
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.speed,
                    title = "Avg. " + stringResource(id = R.string.speed),
                    value = speed
                )
                PropertyInfo(
                    modifier = Modifier.weight(1.0f),
                    type = PropertyInfoType.Blank,
                    icon = R.drawable.navigation_outline,
                    title = "Total. " + stringResource(id = R.string.distance),
                    value = distance
                )
            }
        }
    }
}
