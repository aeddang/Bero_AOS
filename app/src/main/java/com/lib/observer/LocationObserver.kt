package com.lib.observer

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
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
import java.util.Date
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
}