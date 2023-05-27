package com.skeleton.component.map

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.skeleton.theme.ColorApp

interface MapUserDataInterface {
    var isSelected:Boolean
    var isGroup:Boolean
    var startPos:Float
    var midPos:Float
    var endPos:Float
    var title:String?
    var location:LatLng?
    var locations:ArrayList<LatLng>
    var count:Int
    var color:Color
}

open class MapUserData: MapUserDataInterface, Comparable<MapUserData>{
    override var isSelected:Boolean = false
    final override var isGroup:Boolean = false;
    final override var startPos:Float = 0.0f
    final override var midPos:Float = 0.0f
    final override var endPos:Float = 0.0f
    override var title:String? = null
    override var location:LatLng? = null
    override var locations:ArrayList<LatLng> = arrayListOf()
    override var count:Int = 0
    override var color:Color = ColorApp.white
    var index:Int = -1
    fun setPosition(pos:Float):MapUserData{
        midPos = pos
        return this
    }

    fun setRange(idx:Int, width:Float):MapUserData{
        index = idx
        val sPos = idx.toFloat() * width
        val range = (width / 2)
        startPos = sPos
        endPos = sPos + width
        midPos = sPos + range
        return this
    }
    fun isBelong(pos:Float):Boolean{
        if (startPos <= pos && endPos > pos) return true
        return false
    }

    fun addCount(count:Int = 1, loc:LatLng){
        this.count += count
        locations.add(loc)
    }
    fun addCompleted(){
        var latSum: Double = 0.0
        var lngSum: Double = 0.0
        val count:Double = locations.count().toDouble()
        locations.forEach{ loc ->
            latSum += loc.latitude
            lngSum += loc.longitude
        }
        isGroup = true
        val latitude = latSum/count
        val longitude = lngSum/count
        location = LatLng(latitude, longitude)
    }

    override fun compareTo(other: MapUserData): Int {
        if (isBelong(other.midPos)) return 0
        if (this.midPos < other.midPos) return 1
        return -1
    }
}
