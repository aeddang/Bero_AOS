package com.ironraft.pupping.bero.store.walk.model

import androidx.core.text.isDigitsOnly
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.store.api.rest.MissionPlace
import com.ironraft.pupping.bero.store.api.rest.PlaceCategory
import com.ironraft.pupping.bero.store.api.rest.PlaceData
import com.ironraft.pupping.bero.store.api.rest.UserAndPet
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.skeleton.component.map.MapUserData
import com.skeleton.theme.ColorBrand
import java.util.UUID

class Place: MapUserData(){
    val id:String = UUID.randomUUID().toString()

    var placeId:Int = -1; private set
    var googlePlaceId: String? = null
    var visitorCount:Int = 0
    var visitors: List<UserAndPet> = listOf()
    var playExp:Double = 0.0
    var playPoint:Int = 0
    var place:MissionPlace? = null
    var category:PlaceCategory? = null
    var isMark:Boolean = false

    fun setData(data:PlaceData):Place{
        title = data.name
        googlePlaceId = data.googlePlaceId
        placeId = data.placeId ?: -1
        category = PlaceCategory.getType(data.placeCategory)
        data.place?.geometry?.location?.let {loc->
            location = LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
        }
        if (location == null){
            data.location?.split(" ")?.let { locs ->
                if (locs.count() >= 2) {
                    val latitude = locs[1].isDigitsOnly()
                    val longitude = locs[0].isDigitsOnly()
                }
            }
        }
        visitors = data.visitors ?: listOf()
        visitorCount = data.visitorCnt ?: 0
        place = data.place ?: MissionPlace()
        isMark = data.isVisited ?: false
        playPoint = data.point ?: 0
        playExp = data.exp ?: 0.0
        return this
    }

    fun addMark(user:User){
        isMark = true
        visitorCount += 1
        user.currentProfile.originData?.let {userProfile->
            user.representativePet.value?.originData?.let {
                val data = UserAndPet(userProfile, it)
                val sumList = ArrayList<UserAndPet>()
                sumList.add(data)
                sumList.addAll(this.visitors)
                this.visitors = sumList
            }
        }
    }

    fun copySummry(origin:Place, title:String?):Place{
        this.title = title //String.app.place.lowercased()
        color = ColorBrand.primary
        category = origin.category
        googlePlaceId = UUID.randomUUID().toString()
        placeId = origin.placeId
        location = origin.location
        place = origin.place
        isMark = origin.isMark
        count = 1
        origin.location?.let {
            locations.add(it)
        }
        return this
    }

}