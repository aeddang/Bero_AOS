package com.ironraft.pupping.bero.store.walk.model

import android.graphics.Bitmap
import android.location.Location
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.MissionData
import com.ironraft.pupping.bero.store.api.rest.MissionPlace
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.api.rest.WalkUserData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.util.AppUtil
import com.lib.util.toDate
import com.lib.util.toFormatString
import com.lib.util.toLocalDate
import com.lib.util.toLocalDateTime
import com.skeleton.component.map.MapUserData
import com.skeleton.theme.ColorApp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


enum class MissionType {
    New, History, User, Walk;
    val apiDataKey:String
        get() = when(this) {
            Walk -> "Walk"
            else -> "Mission"
        }
    val text :String
        get() = when(this) {
            Walk -> "Walk"
            else -> "Mission"
        }

    @get:DrawableRes
    val icon :Int
        get() = when(this) {
            Walk -> R.drawable.paw
            else -> R.drawable.goal
        }

    @get:StringRes
    val completeButton :Int
        get() = when(this) {
            Walk -> R.string.button_walkComplete
            else -> R.string.button_missionComplete
        }

}

class Mission:MapUserData(){
    val id:String = UUID.randomUUID().toString()
    var missionId:Int = -1; private set
    var type: MissionType = MissionType.Walk; private set
    var description:String? = null; private set
    var pictureUrl:String? = null; private set
    var point:Int = 0; private set
    var exp:Double = 0.0; private set
    var departure:Location? = null; private set
    var waypoints:List<Location> = listOf(); private set
    var distance:Double = 0.0; private set
    var duration:Double = 0.0; private set
    var isStart:Boolean = false; private set
    var isCompleted:Boolean = false; private set
    var playStartDate:Date? = null; private set
    var playTime:Double = 0.0; private set
    var playStartDistance:Double = 0.0; private set
    var playDistance:Double = 0.0; private set
    var walkPath:WalkPath? = null; private set
    var place:MissionPlace? = null; private set
    var userId:String? = null; private set
    var isFriend:Boolean = false; private set
    var user: User? = null; private set
    var startDate:Date? = null; private set
    var endDate:Date? = null; private set
    var completedMissions:ArrayList<Int> = arrayListOf()
    var distanceFromMe:Double? = null; private set

    var petProfile:PetProfile? = null
    var previewImg:Bitmap? = null

    val isExpose:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val viewDistance:String
        get() =  (WalkManager.viewDistance( distance) )
    val viewDuration:String
        get() = WalkManager.viewDuration(duration)
    val viewPlayTime:String
        get() = WalkManager.viewDuration(playTime)
    val viewPlayDistance:String
        get() = WalkManager.viewDuration(playDistance)
    val viewSpeed:String
        get() {
            val d = distance
            val dr = duration /3600.0
            val spd:Double = if (d == 0.0 || dr == 0.0) 0.0 else d/dr
            return WalkManager.viewSpeed(spd)
        }

    fun start(location:Location, walkDistance:Double) {
        departure = location
        playStartDate = AppUtil.networkDate()
        playStartDistance = walkDistance
        playDistance = 0.0
        playTime = 0.0
        isStart = true
        isCompleted = false
    }

    fun end(isCompleted:Boolean? = null, imgPath:String? = null) {
        departure = null
        playStartDate = null
        playDistance = 0.0
        playTime = 0.0
        isStart = false
        pictureUrl = imgPath
        isCompleted?.let {
            this.isCompleted = it
        }
    }
    fun completed(walkDistance:Double) {
        playDistance = walkDistance - playStartDistance
        val start = playStartDate ?: Date()
        playTime = start.toLocalDate()?.until(AppUtil.networkDate().toLocalDate(), ChronoUnit.SECONDS)?.toDouble() ?: 0.0
        isCompleted = true
    }

    fun setData(data:User?):Mission{
        user = data
        return this
    }
    fun setData(data:WalkData, userId:String? = null, isMe:Boolean = false):Mission{
        type = MissionType.Walk
        userId?.let { this.userId = it }
        missionId = data.walkId ?: UUID.randomUUID().hashCode()
        //self.title = data.createdAt
        data.locations?.let {
            walkPath = WalkPath().setData(it)
        }
        isExpose.value = walkPath?.picture?.isExpose ?: false
        pictureUrl = walkPath?.picture?.pictureUrl
        point = data.point ?: 0
        exp = data.exp ?: 0.0
        data.createdAt?.let { date->
            val start = date.toDate()
            this.startDate = start
            start.toLocalDateTime()?.let {
                this.endDate = it.plusSeconds(data.duration?.toLong() ?: 0).toFormatString()?.toDate()
            }
        }
        data.geos?.lastOrNull()?.let { loc->
            this.location = LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
        }
        isCompleted = true
        user = User().setWalkData(data, isMe = isMe)
        distance = data.distance ?: 0.0
        duration = data.duration ?: 0.0
        fixDestination()
        return this
    }

    fun setData(data:WalkUserData): Mission{
        type = MissionType.Walk
        data.userId?.let { this.userId = it }
        missionId = data.walkId ?: UUID.randomUUID().hashCode()
        isFriend = data.isFriend ?: false
        data.pet?.let {pet->
            petProfile = PetProfile().init(data = pet, userId = userId)
            petProfile?.isFriend = isFriend
            petProfile?.level = data.level
        }
        title = petProfile?.name?.value
        pictureUrl = petProfile?.imagePath?.value
        data.createdAt?.let { date->
            date.toDate()?.let { endDate = it }
        }
        data.location?.let {loc->
            location = LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
        }
        isCompleted = true
        fixDestination()
        return this
    }
    fun setData(data:MissionData, type:MissionType):Mission{
        this.type = type
        missionId = data.missionId ?: UUID.randomUUID().hashCode()
        title = data.title
        description = data.description
        pictureUrl = data.pictureUrl
        point = data.point ?: 0
        exp = data.exp ?: 0.0
        data.createdAt?.let { date->
            date.toDate()?.let {
                endDate = it
                val d:Long = data.duration?.toLong() ?: 0
                startDate = it.toLocalDateTime()?.minusSeconds(d)?.toFormatString()?.toDate()
            }
        }
        data.place?.let {place ->
            this.place = place
           place.geometry?.location?.let {loc->
                location = LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
            }
        }
        if (location == null) {
            data.geos?.lastOrNull()?.let {loc->
                location = LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
            }
        }
        isCompleted = data.user != null
        user = User().setMissionData(data)
        distance = data.distance ?: 0.0
        duration = data.duration ?: 0.0
        fixDestination()
        return this
    }
    private fun fixDestination(){
        when (type) {
            MissionType.Walk -> {
                location?.let {origin->
                    val randX:Double = Random.nextDouble (-0.003,0.003)
                    val randY:Double = Random.nextDouble (-0.003,0.003)
                    location = LatLng(origin.latitude+randX , origin.longitude+randY)
                }
            }
            else -> {}
        }
    }

    fun setDistance(me:Location?):Mission{
        me?.let { me ->
            location?.let { loc ->
                //distanceFromMe = me.distance(from: loc)
            }
        }
        return this
    }


    fun copySummry(origin:Mission, title:String?):Mission{
        color = ColorApp.yellow
        this.title = title //String.app.users.lowercased()
        missionId = origin.missionId
        type = origin.type
        user = origin.user
        location = origin.location
        distance = origin.distance
        duration = origin.duration
        count = 1
        origin.location?.let {
            locations.add(it)
        }
        return this
    }
}



