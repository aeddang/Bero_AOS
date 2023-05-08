package com.ironraft.pupping.bero.store.provider.model


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.lib.util.DataLog
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.rest.*
import com.lib.util.toDate
import com.lib.util.toFormatString
import com.skeleton.sns.SnsType
import com.skeleton.sns.SnsUser
import com.skeleton.theme.ColorApp
import java.util.*

enum class Gender {
    Male, Female, Neutral;
    @get:DrawableRes
    val icon:Int
        get() = when(this) {
            Male -> R.drawable.male
            Female -> R.drawable.female
            Neutral -> R.drawable.neutrality
        }

    @get:StringRes
    val title:Int
        get() = when(this) {
            Male -> R.string.male
            Female -> R.string.female
            Neutral -> R.string.neutral
        }

    val color : Color
        get() = when(this) {
            Male -> ColorApp.blue
            Female -> ColorApp.orange
            Neutral -> ColorApp.green
        }


    val coreDataKey : Int
        get() = when(this) {
            Male -> 1
            Female -> 2
            Neutral -> 3
        }

    val apiDataKey : String
        get() = when(this) {
            Male -> "Male"
            Female -> "Female"
            Neutral -> "Neutral"
        }


    companion object{
        fun getGender(value:Int) : Gender?{
            return when(value) {
                1  -> Male
                2 -> Female
                3 -> Neutral
                else -> null
            }
        }
        fun getGender(value:String?) : Gender?{
            return when(value) {
                "Male"  -> Gender.Male
                "Female" -> Gender.Female
                "Neutral" -> Gender.Neutral
                else -> null
            }
        }
    }
}

data class ModifyUserData (
    val point:Double? = null,
    var mission:Double? = null,
    var coin:Double? = null
)

class User {
    private val appTag = javaClass.simpleName
    var id:String = UUID.randomUUID().toString(); private set
    val point:MutableLiveData<Double> = MutableLiveData<Double>()
    val coin:MutableLiveData<Double> = MutableLiveData<Double>()
    val mission:MutableLiveData<Double> = MutableLiveData<Double>()

    val pets:MutableLiveData<List<PetProfile>> = MutableLiveData(arrayListOf())
    var currentProfile:UserProfile = UserProfile(); private set
    var currentPet:PetProfile? = null; private set
    var snsUser:SnsUser? = null; private set
    var recentMission:History? = null; private set
    var finalGeo:GeoData? = null; private set

    fun registUser(user:SnsUser){
        snsUser = user
    }
    fun clearUser(){
        snsUser = null
    }
    fun registUser(id:String?, token:String?, code:String?){
        DataLog.d("id " + (id ?: ""),appTag)
        DataLog.d("token " + (token ?: ""),appTag)
        DataLog.d("code " + (code ?: ""),appTag)
        val sndId = id ?: return
        val sndToken = token ?: return
        if (sndToken.isEmpty()) return
        if (sndId.isEmpty()) return
        val type = SnsType.getType(code) ?: return
        DataLog.d("user init " + (code ?: ""), appTag)
        snsUser = SnsUser(type, sndId, sndToken)
    }

    fun setData(data:UserData){
        point.value = data.point ?: 0.0
        currentProfile.setData(data)
    }

    fun setData(data:MissionData) : User {
        recentMission = History().setData(data)
        data.user?.let{
            setData(it)
        }
        data.pets?.let{
            setData(it, false)
        }
        val type = SnsType.getType(data.user?.providerType)
        val userId = data.user?.userId
        if (type != null && userId != null){
            snsUser = SnsUser(type, userId, "")
        }
        data.geos?.let {
            if (it.isEmpty()) return@let
            finalGeo = it.first()
        }

        return this
    }

    fun missionCompleted(mission:Mission) {
        this.mission.value = this.mission.value?.plus(1)
        /*
        pets.value?.let{pets->
            pets.filter{it.isWith}.forEach{
                it.up
            }
        }*/
    }


    fun setData(data:List<PetData>, isMyPet:Boolean = true){
        val profiles = data.map{ PetProfile().init(it, isMyPet) }
        this.pets.value = profiles.toMutableList()
    }

    fun deletePet(petId:Int) {
        this.pets.value?.first { it.petId == petId }?.let{
            val newList = this.pets.value?.toMutableList()
            newList?.remove(it)
            this.pets.value = newList
        }
    }

    fun updatedPet(petId:Int, data:ModifyPetProfileData) {
        this.pets.value?.first { it.petId == petId }?.let{
            it.update(data)
        }
    }


    fun registPetComplete(profile:PetProfile)  {
        if(this.currentPet == null ) currentPet = profile
        this.pets.value?.let{
            val newList = this.pets.value?.toMutableList()
            newList?.add(profile)
            this.pets.value = newList
        }
    }

    fun getPet(id :String) : PetProfile? {
        return this.pets.value?.first{it.id == id}
    }

    fun addPet(profile:PetProfile) {
        this.pets.value?.let{
            val newList = this.pets.value?.toMutableList()
            newList?.add(profile)
            this.pets.value = newList
        }
    }
}

class History{
    var missionId: Int? = null ; private set
    var category: String? = null ; private set
    var title: String? = null ; private set
    var imagePath: String? = null ; private set
    var description: String? = null ; private set
    var date: String? = null ; private set
    var duration: Double? = null ; private set
    var distance: Double? = null ; private set
    var point: Double? = null ; private set
    var missionCategory:MissionCategory? = null ; private set
    var index:Int = -1; private set
    var isExpanded:Boolean = false
    fun setData(data:MissionData) : History{
        missionCategory = MissionCategory.getCategory(data.missionCategory)
        missionId = data.missionId
        title = data.title
        imagePath = data.pictureUrl
        description = data.description
        duration = data.duration
        distance = data.distance
        point = data.point ?: 0.0
        date = data.createdAt?.toDate("yyyy-MM-dd'T'HH:mm:ss")?.toFormatString("yy-MM-dd HH:mm")
        return this
    }
}