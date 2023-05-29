package com.ironraft.pupping.bero.scene.page.history.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.lib.page.ComponentViewModel
import com.lib.page.PagePresenter
import com.lib.util.showCustomToast
import com.lib.util.toFormatString
import com.lib.util.toggle
import com.skeleton.component.calendar.CPCalendar
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import com.skeleton.view.button.SortButton
import com.skeleton.view.button.SortButtonSizeType
import com.skeleton.view.button.SortButtonType
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get


@Composable
fun WalkTopInfo(
    modifier: Modifier = Modifier,
    mission:Mission,
    isMe:Boolean = false
) {
    val appTag = "WalkTopInfo"
    val pagePresenter: PagePresenter = get()
    val dataProvider: DataProvider = get()
    var day:String? by remember { mutableStateOf(null) }
    var time:String? by remember { mutableStateOf(null) }
    val pictureId by remember { mutableStateOf(
        mission.walkPath?.picture?.pictureId
    )}
    val isExpose by mission.isExpose.observeAsState()
    fun onInit():Boolean{
        val start = mission.startDate ?: return true
        val end = mission.endDate ?: return true
        val ymdStart = start.toFormatString("yyyyMM")
        val ymdEnd = end.toFormatString("yyyyMM")
        if (ymdStart == ymdEnd) {
            day = end.toFormatString("MMMM d, yyyy")
            time = start.toFormatString("HH:mm") + " - " + end.toFormatString("HH:mm")
        } else {
            time = start.toFormatString("MMMM d, yyyy HH:mm") + " - " + end.toFormatString("d, HH:mm")
        }
        return true
    }
    val isInit by remember { mutableStateOf(onInit()) }
    val apiResult = dataProvider.result.observeAsState()
    apiResult.value.let { res ->
        res?.type ?: return@let
        if(res.contentID != pictureId.toString()) return@let
        when ( res.type ){
            ApiType.UpdateAlbumPicturesExpose-> {
                val isExpose = res.requestData as? Boolean ?: false
                if (mission.isExpose.value != isExpose)
                    mission.isExpose.value = isExpose
                    val activity = pagePresenter.activity
                    val msg = activity.getString(if(isExpose) R.string.alert_exposed else  R.string.alert_unExposed)
                    Toast(activity ).showCustomToast(msg, activity)
            }
            else ->{}
        }
    }
    AppTheme {
        if (isInit) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = DimenMargin.tiny.dp)
            ) {
                day?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.black
                    )
                    Text(
                        text = "|",
                        fontSize = FontSize.thin.sp,
                        color = ColorBrand.primary
                    )
                }
                time?.let {
                    Text(
                        text = it,
                        fontSize = FontSize.thin.sp,
                        color = ColorApp.grey400
                    )
                }
            }
            if(isMe){
                pictureId?.let {id->
                    SortButton(
                        type = SortButtonType.Stroke,
                        sizeType = SortButtonSizeType.Small,
                        icon = R.drawable.global,
                        text = stringResource(id = R.string.share),
                        color = if(isExpose==true) ColorBrand.primary else ColorApp.grey400,
                        isSort = false
                    ){
                        val value = (isExpose ?: false).toggle()
                        val q = ApiQ(appTag, ApiType.UpdateAlbumPicturesExpose, contentID = id.toString(), requestData = value)
                        dataProvider.requestData(q)
                                }
                }
            }
        }
    }

}

@Preview
@Composable
fun WalkTopInfoComposePreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WalkTopInfo(
                mission = Mission()
            )
        }
    }
}