package com.ironraft.pupping.bero.store.provider.model

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.lib.util.*
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.ironraft.pupping.bero.store.walk.model.MissionType
import java.time.LocalDate
import java.util.*

data class ModifyPetProfileData (
    var image:Bitmap? = null,
    var name:String? = null,
    var breed:String? = null,
    var gender:Gender? =null,
    var isNeutralized:Boolean? = null,
    var isRepresentative:Boolean? = null,
    var birth:LocalDate? = null,
    var microchip:String? = null,
    var animalId:String? = null,
    var immunStatus:String? = null,
    var hashStatus:String? = null,
    var introduction:String? = null,
    var weight:Double? = null,
    var size:Double? = null
){
    fun update(data:ModifyPetProfileData) : ModifyPetProfileData {
        return ModifyPetProfileData(
            image = data.image ?: image,
            name = data.name ?: name,
            breed = data.breed ?: breed,
            gender = data.gender ?: gender,
            isNeutralized = data.isNeutralized ?: isNeutralized,
            isRepresentative = data.isRepresentative ?: isRepresentative,
            birth = data.birth ?: birth,
            microchip = data.microchip ?: microchip,
            animalId = data.animalId ?: animalId,
            immunStatus = data.immunStatus ?: immunStatus,
            hashStatus = data.hashStatus ?: hashStatus,
            introduction = data.introduction ?: introduction,
            weight = data.weight ?: weight,
            size = data.size ?: size
        )
    }
}


class PetProfile {
    companion object{
        fun exchangeListToString(list:List<String>?):String{
            list?.let { values ->
                if (values.isEmpty()) return ""
                return values.reduce { acc, s ->
                    return "$acc,$s"
                }
            }
            return ""
        }
        fun exchangeStringToList(str:String?):List<String>{
            str?.let{ value ->
                if (value.isEmpty()) return emptyList()
                return value.split(",")
            }
            return emptyList()
        }

    }
    private val appTag = javaClass.simpleName
    var id:String = UUID.randomUUID().toString(); private set
    var petId:Int = 0; private set
    var userId:String = ""; private set
    val imagePath:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val image:MutableLiveData<Bitmap?> = MutableLiveData<Bitmap?>(null)
    val name:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val breed:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val gender:MutableLiveData<Gender?> = MutableLiveData<Gender?>(null)
    val birth:MutableLiveData<LocalDate?> = MutableLiveData<LocalDate?>(null)
    val isNeutralized:MutableLiveData<Boolean?> = MutableLiveData<Boolean?>(null)
    val immunStatus:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val hashStatus:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val animalId:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val microchip:MutableLiveData<String?> = MutableLiveData<String?>(null)
    val weight:MutableLiveData<Double?> = MutableLiveData<Double?>(null)
    val size:MutableLiveData<Double?> = MutableLiveData<Double?>(null)
    val introduction:MutableLiveData<String?> = MutableLiveData<String?>(null)
    var isEmpty:Boolean = false; private set
    var isMypet:Boolean = false; private set
    var exerciseDistance: Double = 0.0; private set
    var exerciseDuration: Double = 0.0; private set
    val totalWalkCount: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    var originData:PetData? = null
    var isWith:Boolean = true
    var isRepresentative:Boolean = false
    var isFriend:Boolean = false
    var level:Int? = null
    val sortIdx:Int
        get() = if (isRepresentative) 0 else 1

    fun getIntroduction(ctx:Context):String{
        introduction.value?.let {
            return it
        }
        return ctx.getString(R.string.introductionDefault).replace(name.value ?: "")
    }

    override fun equals(other: Any?): Boolean {
        (other as? PetProfile)?.let {
            return this.id == it.id
        }
        return super.equals(other)
    }
    fun init(nickName:String?,breed:String?, gender:Gender?, birth:LocalDate?) : PetProfile{
        this.name.value = nickName
        this.breed.value = breed
        this.gender.value = gender
        this.birth.value = birth
        this.isMypet = true
        return this
    }

    fun init(isMyPet:Boolean) : PetProfile{
        isMypet = isMyPet
        return this
    }
    fun init(data:PetData, userId:String? = null,isMyPet:Boolean = false, isFriend:Boolean = false, lv:Int? = null, index:Int = -1): PetProfile{
        if (isMyPet) originData = data
        lv?.let { level = it }
        this.userId = data.userId ?: userId ?: ""
        this.isMypet = isMyPet
        this.petId = data.petId ?: 0
        this.isRepresentative = data.isRepresentative ?: false
        this.imagePath.value = data.pictureUrl
        this.name.value = data.name
        this.breed.value = data.tagBreed
        this.gender.value = Gender.getGender(data.sex)
        this.birth.value = data.birthdate?.toDate( )
        this.introduction.value = data.introduce
        this.microchip.value = data.regNumber
        this.animalId.value = data.animalId
        this.weight.value = data.weight
        this.size.value = data.size
        this.isNeutralized.value = data.isNeutered ?: false
        this.immunStatus.value = data.tagStatus
        this.hashStatus.value = data.tagPersonality
        this.exerciseDistance = data.exerciseDistance ?: 0.0
        this.exerciseDuration = data.exerciseDuration ?: 0.0
        this.totalWalkCount.value = data.walkCompleteCnt

        return this
    }


    fun empty() : PetProfile{
        this.isEmpty = true
        this.name.value = ""
        this.isMypet = true
        return this
    }
    fun dummy() : PetProfile{

        this.name.value = "name"
        this.isMypet = true
        this.introduction.value = "introduction"
        return this
    }
    fun update(data:ModifyPetProfileData) : PetProfile{
        data.image?.let { this.image.value = it }
        data.name?.let { this.name.value = it }
        data.breed?.let { this.breed.value = it }
        data.gender?.let { this.gender.value = it }
        data.microchip?.let { this.microchip.value = it }
        data.birth?.let { this.birth.value = it }
        data.isNeutralized?.let { this.isNeutralized.value = it }
        data.immunStatus?.let { this.immunStatus.value = it }
        data.hashStatus?.let { this.hashStatus.value = it }
        data.animalId?.let { this.animalId.value = it }
        data.weight?.let { this.weight.value = it }
        data.size?.let { this.size.value = it }
        data.introduction?.let  { this.introduction.value = it }
        //ProfileCoreData().update(id: self.id, data: data)
        return this
    }

    fun missionCompleted(mission: Mission) {
        if (!mission.isCompleted) return
        when (mission.type){
            MissionType.Walk -> {
                totalWalkCount.value = totalWalkCount.value?.plus(1)
                exerciseDistance = exerciseDistance.plus(mission.distance)
                exerciseDuration = exerciseDuration.plus(mission.duration)
            }
            else -> {}
        }
    }

    fun update(image:Bitmap?) : PetProfile{
        this.image.value = image
        return this
    }

    fun removeObservers(owner: LifecycleOwner){
        imagePath.removeObservers(owner)
        image.removeObservers(owner)
        name.removeObservers(owner)
        breed.removeObservers(owner)
        gender.removeObservers(owner)
        birth.removeObservers(owner)
        introduction.removeObservers(owner)
        isNeutralized.removeObservers(owner)
        immunStatus.removeObservers(owner)
        hashStatus.removeObservers(owner)
        animalId.removeObservers(owner)
        microchip.removeObservers(owner)
        weight.removeObservers(owner)
        size.removeObservers(owner)
        totalWalkCount.removeObservers(owner)
    }

}
