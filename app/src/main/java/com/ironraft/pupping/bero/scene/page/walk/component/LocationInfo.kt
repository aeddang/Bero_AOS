package com.ironraft.pupping.bero.scene.page.walk.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.WeatherData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.WalkEventType
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.observer.LocationObserver
import com.lib.page.ComponentViewModel
import com.lib.util.DataLog
import com.lib.util.toTruncateDecimal
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.FontSize
import dev.burnoo.cokoin.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.HashMap


@Composable
fun LocationInfo(
    modifier: Modifier = Modifier
) {
    val appTag = "LocationInfo"
    val locationObserver: LocationObserver = get()
    val dataProvider: DataProvider = get()
    val walkManager: WalkManager = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    var locationTitle:String? by remember { mutableStateOf( null ) }
    var temperature:String? by remember { mutableStateOf( null ) }
    var weatherIcon:String? by remember { mutableStateOf( null ) }

    val apiResult = dataProvider.result.observeAsState()
    val walkEvent = walkManager.event.observeAsState()

    fun requestWeather(loc:LatLng):Boolean{
        val params = HashMap<String, String>()
        params[ApiField.lat] = loc.latitude.toString()
        params[ApiField.lng] = loc.longitude.toString()
        val q = ApiQ(appTag, ApiType.GetWeather, query = params)
        dataProvider.requestData(q)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updatedLocation(latlng:LatLng? = null):Boolean{
        val loc = latlng ?: walkManager.currentLocation.value ?: locationObserver.finalLocation.value ?: return true
        requestWeather(loc)

        locationObserver.convertLocationToAddress(loc){ address ->
            //zipCode = address.zipCode
            val state = address.state ?: return@convertLocationToAddress
            val title = if (address.city != null) {
                if(address.street != null) {
                    address.street
                } else {
                    address.city
                }
            } else {
                state
            }
            title?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    locationTitle = it
                    DataLog.d(it, appTag)
                }
            }

        }


        return true
    }

    apiResult.value?.let { res ->
        if (res.id != appTag) return@let
        when ( res.type ){
            ApiType.GetWeather -> {
                if(!viewModel.isValidResult(res)) return@let
                (res.data as? WeatherData)?.let { data ->
                    data.temp?.let { temp->
                        temperature = temp.toTruncateDecimal(1) + "°C"
                    }
                    data.iconId?.let {icon->
                        weatherIcon = "http://openweathermap.org/img/wn/$icon@2x.png"
                    }
                }
            }
            else ->{}
        }
    }
    walkEvent.value.let { evt ->
        val e = evt ?: return@let
        when (e.type){
            WalkEventType.UpdateViewLocation -> {
                if (!viewModel.isValidValue(e)) return@let
                updatedLocation(e.value as? LatLng)
            }
            else -> {}
        }
    }

    val isInit:Boolean by remember { mutableStateOf( updatedLocation() ) }

    AppTheme {
        if (isInit) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    locationTitle ?: stringResource(id = R.string.walkLocationNotFound),
                    fontSize = FontSize.light.sp,
                    color = ColorApp.gray400
                )
                Text(
                    "|",
                    fontWeight = FontWeight.Medium,
                    fontSize = FontSize.light.sp,
                    color = ColorBrand.primary
                )
                weatherIcon?.let {
                    val painter = rememberAsyncImagePainter(it)
                    Image(
                        painter,
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(DimenIcon.light.dp, DimenIcon.light.dp)
                    )
                }
                temperature?.let {
                    Text(
                        it,
                        fontSize = FontSize.light.sp,
                        color = ColorApp.gray400
                    )
                }
            }
        }
    }
}
