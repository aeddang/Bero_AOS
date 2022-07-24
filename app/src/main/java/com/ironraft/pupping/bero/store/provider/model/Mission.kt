package com.ironraft.pupping.bero.store.mission

import android.content.Context
import android.location.Location
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.ironraft.pupping.bero.R
import java.util.*
import kotlin.math.ceil
import kotlin.math.min


enum class MissionType {
    Today, Event, Always;

    fun info(): String {
        return when (this) {
            Today -> "Today’s Mission"
            Event -> "Event!! Mission"
            Always -> "Any Time Mission"
        }
    }

    @ColorRes
    fun color(): Int {
        return when (this) {
            Today -> R.color.brand_primary
            Event -> R.color.brand_thirdly
            Always -> R.color.brand_secondary
        }
    }

    companion object {
        fun random(): MissionType {
            return MissionType.values().random()
        }
    }
}



enum class MissionLv{
    Lv1, Lv2, Lv3, Lv4;
    fun apiDataKey() : String {
        return when(this) {
            Lv1 -> "lv1"
            Lv2 -> "lv2"
            Lv3 -> "lv3"
            Lv4 -> "lv4"
        }
    }

    fun info() : String {
        return when(this) {
            Lv1 -> "Easy"
            Lv2 -> "Normal"
            Lv3 -> "Difficult"
            Lv4 -> "Very Difficult"
        }
    }
    @DrawableRes
    fun icon() : Int {
        return when(this) {
            Lv1 -> R.drawable.ic_difficulty_easy
            Lv2 -> R.drawable.ic_difficulty_easy
            Lv3 -> R.drawable.ic_difficulty_hard
            Lv4 -> R.drawable.ic_difficulty_hard
        }
    }

    @ColorRes
    fun color() : Int{
        return when(this) {
            Lv1 -> R.color.brand_secondary
            Lv2 -> R.color.brand_primary
            Lv3 -> R.color.brand_thirdly
            Lv4 -> R.color.brand_thirdly
        }
    }

    companion object {
        fun getMissionLv(value :String?) : MissionLv?
        {
            return when(value) {
                "lv1" -> Lv1
                "lv2" -> Lv2
                "lv3" -> Lv3
                "lv4" -> Lv4
                else -> null
            }
        }
    }
}


enum class MissionKeyword{
    Convenience, AnimalHospital, Mart;
    fun keyword() : String{
        return when(this){
            Convenience -> "편의점"
            AnimalHospital -> "동물병원"
            Mart -> "마트"
        }
    }
    companion object {
        fun random() : MissionKeyword
        {
            return MissionKeyword.values().random()
        }
    }
}


class Mission(){
    companion object {
        fun viewSpeed(ctx:Context, value :Double) : String
        {
            return String.format("%.1f", (value * 3600 / 1000)) + ctx.resources.getString(R.string.kmPerH)
        }
        fun viewDistance(ctx:Context, value :Double) : String
        {
            return String.format("%.1f", (value / 1000)) + ctx.resources.getString(R.string.km)
        }
        fun viewDuration(ctx:Context, value :Double) : String
        {
            return String.format("%.1f", (value / 60)) + ctx.resources.getString(R.string.min)
        }
    }

    val id:String = UUID.randomUUID().toString()
    // Use fields to define the data types to return.
    var type:MissionType = MissionType.Today; private set
    var lv:MissionLv = MissionLv.Lv1; private set
    var description:String = ""; private set
    var summary:String = ""; private set
    var recommandPlaces:List<AutocompletePrediction> = arrayListOf(); private set
    var start:Place? = null; private set
    var destination:Place? = null; private set
    var waypoints:List<Place> = arrayListOf(); private set
    var startTime:Double = 0.0; private set
    var totaldistance:Double = 0.0; private set //miter
    var duration:Double = 0.0; private set //sec
    var speed:Double = 0.0; private set //meter per hour

    var isCompleted:Boolean = false; private set
    var playTime:Double = 0.0; private set
    var playdistance:Double = 0.0; private set
    var pictureUrl:String? = null


    fun completed(playTime:Double, playdistance:Double) {
        this.playTime = playTime
        this.playdistance = playdistance
        isCompleted = true
    }



    fun addRecommandPlaces(values:List<AutocompletePrediction>) : Mission {
        this.recommandPlaces = values
        return this
    }

    fun viewSpeed(ctx:Context):String { return Mission.viewSpeed(ctx, speed) }
    fun viewDistance(ctx:Context):String { return Mission.viewDistance(ctx, totaldistance) }
    fun viewDuration(ctx:Context):String { return Mission.viewDuration(ctx, duration) }

    val allPoint:List<Place>; get(){
        val points:MutableList<Place> = arrayListOf()
        this.start?.let { points.add(it) }
        points.addAll(waypoints)
        this.destination?.let { points.add(it) }
        return points
    }

    
}



