package com.lib.observer

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.lib.page.PagePresenter
import com.lib.page.PageRequestPermission
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit

data class LocationAddress (
    var street:String? = null,
    var city:String? = null,
    var state:String? = null,
    var zipCode :String? = null,
    var country :String? = null
)
class LocationObserver(val pagePresenter:PagePresenter) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(pagePresenter.activity)
    var isSearch:Boolean = false; private set
    var requestId:String? = null; private set
    var finalLocation:MutableLiveData<LatLng?> = MutableLiveData()
    private var locationTask: Task<Location>? = null

    private val locationRequest =  LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(60)
        fastestInterval = TimeUnit.SECONDS.toMillis(30)
        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.lastLocation?.let { loc->
                finalLocation.value = LatLng(loc.latitude, loc.longitude)
            }
        }
    }
    fun requestMe(isStart:Boolean, id:String? = null){
        if (isStart) {
            if (isSearch) return
            pagePresenter.requestPermission(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
                requester = object : PageRequestPermission {
                    @SuppressLint("MissingPermission")
                    override fun onRequestPermissionResult(
                        resultAll: Boolean,
                        permissions: List<Boolean>?
                    ) {
                        if (!resultAll) return
                        id?.let { requestId = it }
                        isSearch = true
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            location ?: return@addOnSuccessListener
                            finalLocation.value = LatLng(location.latitude, location.longitude)
                        }
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    }
                }
            )
        } else  {
            if (!isSearch) return
            id?.let {
                if( requestId != it ) return
            }
            isSearch = false
            requestId = null
            fusedLocationClient.removeLocationUpdates( locationCallback )
        }
    }


    fun convertLocationToAddress(location:LatLng, result:(LocationAddress) -> Unit) {
        val geocoder:Geocoder = Geocoder(pagePresenter.activity, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1){addresses ->
                val street = addresses[0].thoroughfare
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val zip = addresses[0].postalCode
                val country = addresses[0].countryName

                result(
                    LocationAddress(
                        street = street,
                        city = city,
                        state = state,
                        zipCode = zip,
                        country = country
                    )
                )
            }
        } else {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.get(0)?.let {
                val street = it.thoroughfare
                val city = it.locality
                val state = it.adminArea
                val zip = it.postalCode
                val country = it.countryName
                result(
                    LocationAddress(
                        street = street,
                        city = city,
                        state = state,
                        zipCode = zip,
                        country = country
                    )
                )
                return
            }
            result(
                LocationAddress()
            )
        }
    }
}