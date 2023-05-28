package com.ironraft.pupping.bero.store.walk.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.api.rest.WalkLocationData
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class WalkPathItem (
    val id:String = UUID.randomUUID().toString(),
    var idx:Int = 0,
    val location:LatLng,
    var smallPictureUrl:String? = null,
    var tx:Float = 0.0f,
    var ty:Float = 0.0f,
)
data class WalkPictureItem(
    val id:String = UUID.randomUUID().toString(),
    val location:LatLng,
    var pictureId:Int? = null,
    var pictureUrl:String? = null,
    var smallPictureUrl:String? = null,
    var isExpose:Boolean = false
)

class WalkPath {
    var paths:List<WalkPathItem> = listOf(); private set
    var pictures:List<WalkPictureItem> = listOf(); private set
    var picture:WalkPictureItem? = null; private set
    fun setData(datas:ArrayList<WalkLocationData>) : WalkPath?{
        var minX:Double = 180.0
        var maxX:Double = -180.0
        var minY:Double = 90.0
        var maxY:Double = -90.0
        val locations:ArrayList<WalkPathItem> = arrayListOf()
        val addPictures:ArrayList<WalkPictureItem> = arrayListOf()

        datas.forEachIndexed { idx, data ->
            val originlng = data.lng ?: return@forEachIndexed
            val originlat = data.lat ?: return@forEachIndexed
            var lat = originlat
            var lng = originlng
            if (SystemEnvironment.isTestMode){
                val randX = Random.nextDouble (-0.003,0.003)
                val randY = Random.nextDouble (-0.003,0.003)
                lat = originlat + randX
                lng = originlng + randY
            }
            minX = min(minX, lng)
            maxX = max(maxX, lng)
            minY = min(minY, lat)
            maxY = max(maxY, lat)
            data.pictureId?.let{ id->
                addPictures.add(WalkPictureItem(
                    location =  LatLng(lat, lng),
                    pictureId = id,
                    pictureUrl = data.pictureUrl,
                    smallPictureUrl = data.smallPictureUrl,
                    isExpose = data.isExpose ?:false
                ))
            }
            locations.add(WalkPathItem(
                idx = idx,
                location = LatLng(lat, lng),
                smallPictureUrl = data.smallPictureUrl)
            )
        }
        pictures = addPictures
        picture = addPictures.lastOrNull()
        val diffX:Double = abs(minX-maxX)
        val diffY:Double = abs(minY-maxY)
        val range:Double = max( diffX, diffY )
        if (range <= 0.0) return this
        val modifyX:Double = (range - diffX) / 2
        val modifyY:Double = (range - diffY) / 2
        minX -= modifyX
        maxX += modifyX
        minY -= modifyY
        maxY += modifyY
        paths = locations.map{ loc ->
            val tx = (loc.location.longitude - minX)/range
            val ty = (loc.location.latitude - minY)/range
            WalkPathItem(
                idx = loc.idx,
                location = loc.location,
                smallPictureUrl = loc.smallPictureUrl,
                tx = tx.toFloat(), ty = ty.toFloat()
            )
        }
        return this
    }

    fun setData(datas:List<Location>) : WalkPath{
        var minX:Double = 180.0
        var maxX:Double = -180.0
        var minY:Double = 90.0
        var maxY:Double = -90.0
        val locations:ArrayList<WalkPathItem> = arrayListOf()

        datas.forEachIndexed { idx, data ->
            val originlng = data.longitude
            val originlat = data.latitude
            var lat = originlat
            var lng = originlng
            if (SystemEnvironment.isTestMode){
                val randX = Random.nextDouble (-0.003,0.003)
                val randY = Random.nextDouble (-0.003,0.003)
                lat = originlat + randX
                lng = originlng + randY
            }
            minX = min(minX, lng)
            maxX = max(maxX, lng)
            minY = min(minY, lat)
            maxY = max(maxY, lat)
            locations.add(WalkPathItem(
                idx = idx,
                location = LatLng(lat, lng)
            ))
        }
        val diffX:Double = abs(minX-maxX)
        val diffY:Double = abs(minY-maxY)
        val range:Double = max(max( diffX, diffY ), 0.001)
        val modifyX:Double = (range - diffX) / 2
        val modifyY:Double = (range - diffY) / 2
        minX -= modifyX
        maxX += modifyX
        minY -= modifyY
        maxY += modifyY

        paths = locations.map{ loc ->
            val tx = (loc.location.longitude - minX)/range
            val ty = (loc.location.latitude - minY)/range
            WalkPathItem(
                idx = loc.idx,
                location = loc.location,
                tx = tx.toFloat(), ty = ty.toFloat()
            )
        }
        return this
    }
}