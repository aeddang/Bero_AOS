package com.ironraft.pupping.bero.store.provider.model


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.lib.util.DataLog
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.button.FriendButtonFuncType
import com.ironraft.pupping.bero.store.api.MetaData
import com.ironraft.pupping.bero.store.api.rest.*
import com.lib.page.PageEventType
import com.lib.util.toDate
import com.lib.util.toFormatString
import com.skeleton.sns.SnsType
import com.skeleton.sns.SnsUser
import com.skeleton.theme.ColorApp
import java.util.*


enum class UserEventType{
    UpdatedProfile, AddedDog, DeletedDog, UpdatedDog , UpdatedDogs,
    UpdatedPlayData, UpdatedLvData;
}
data class UserEvent(val type: UserEventType, var petProfile: PetProfile? = null)
class User(val isMe:Boolean = false){
    private val appTag = javaClass.simpleName

    var id:String = UUID.randomUUID().toString(); private set
    val event:MutableLiveData<UserEvent?> = MutableLiveData(null)

    var point:Int = 0; private set
    var lv:Int = 1; private set
    var exp:Double = 0.0; private set
    var prevExp:Double = 0.0; private set
    var nextExp:Double = 0.0; private set
    var exerciseDuration:Double = 0.0; private set
    var exerciseDistance:Double = 0.0; private set
    var totalWalkCount: Int = 0; private set
    var currentProfile:UserProfile = UserProfile(isMe); private set

    var pets:List<PetProfile> = listOf(); private set
    var snsUser:SnsUser? = null; private set
    var finalGeo:GeoData? = null; private set

    val representativePet:MutableLiveData<PetProfile?> = MutableLiveData(null)
    var currentPet:PetProfile? = null
    val userId:String?
        get() = snsUser?.snsID ?: (if (currentProfile.userId.isEmpty()) null else currentProfile.userId)

    val representativeName:String
        get() = (representativePet.value?.name?.value ?: currentProfile.nickName.value) ?: "Bero user"

    val representativeImage:String?
        get() = representativePet.value?.imagePath?.value ?: currentProfile.imagePath.value

    val isFriend:Boolean
        get() = currentProfile.status.value?.isFriend ?: false



    fun isSameUser(user:User?):Boolean{
        user?.currentProfile?.userId?.let {
            return it == snsUser?.snsID
        }
        return false
    }
    fun isSameUser(user:UserProfile?):Boolean{
        user?.userId?.let {
            return it == snsUser?.snsID
        }
        return false
    }
    fun isSameUser(userId:String?):Boolean{
        userId?.let {
            return it == snsUser?.snsID
        }
        return false
    }
    fun registUser(user:SnsUser){
        snsUser = user
    }
    fun clearUser(){
        snsUser = null
        currentProfile = UserProfile(isMe)
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


    /*
    fun setData(data:WalkData, isMe:Boolean = false): User {
        if let user = data.user {
            self.setData(data:user)
        }
        if let pets = data.pets {
            self.setData(data:pets, isMyPet:isMe)
        }
        if let type = SnsType.getType(code: data.user?.providerType), let id = data.user?.userId {
            self.snsUser = SnsUser(
                snsType: type,
                snsID: id,
                snsToken: ""
            )
        }
        self.finalGeo = data.geos?.first
        return self
    }
    */
    fun setData(data:MissionData) : User {
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
    fun setData(data:UserData):User{
        if (snsUser == null) {
            val type = SnsType.getType(data.providerType)
            val userId = data.userId
            if (type != null && userId != null){
                snsUser = SnsUser(type, userId, "")
            }
        }
        point = data.point ?: 0
        lv = data.level ?: 1
        exp = data.exp ?: ((lv-1) * Lv.expRange)
        prevExp = data.prevLevelExp ?: ((lv-1) * Lv.expRange)
        nextExp = data.nextLevelExp ?: ((lv) * Lv.expRange)
        totalWalkCount = data.walkCompleteCnt ?: 0
        exerciseDistance = data.exerciseDistance ?: 0.0
        exerciseDuration = data.exerciseDuration ?: 0.0
        currentProfile.setData(data)
        currentProfile.setLv(lv)
        event.value = UserEvent(UserEventType.UpdatedProfile)
        return this
    }

    fun setData(data:List<PetData>, isMyPet:Boolean = true){
        val profiles = data.map{ PetProfile().init(it, isMyPet = isMyPet, lv = lv) }
        pets = profiles.toMutableList()
        findRepresentativePet()
        event.value = UserEvent(UserEventType.UpdatedDogs)
    }
    fun deletePet(petId:Int) {
        pets.find { it.petId == petId }?.let{
            val newList = pets.toMutableList()
            newList.remove(it)
            pets = newList
            event.value = UserEvent(UserEventType.DeletedDog, petProfile = it)
        }

    }
    fun registPetComplete(profile:PetProfile)  {
        if(this.currentPet == null ) currentPet = profile
        val newList = this.pets.toMutableList()
        newList.add(profile)
        pets = newList
        event.value = UserEvent(UserEventType.AddedDog, petProfile = profile)
    }
    fun updatedPet(petId:Int, data:ModifyPetProfileData) {
        this.pets.find{ it.petId == petId }?.let{
            it.update(data)
            event.value = UserEvent(UserEventType.UpdatedDog, petProfile = it)
        }
    }
    fun representativePetChanged(petId:Int){
        pets.forEach{
            it.isRepresentative = it.petId == petId
        }
        findRepresentativePet()
        event.value = UserEvent(UserEventType.UpdatedDogs)
    }
    private fun findRepresentativePet(){
        pets = pets.sortedBy { it.sortIdx }
        representativePet.value = pets.find { it.isRepresentative }
    }

    fun getPet(id :String) : PetProfile? {
        return this.pets.find{it.id == id}
    }


    fun missionCompleted(mission:Mission) {
        if (!mission.isCompleted) return
        when(mission.type){
            MissionType.Walk ->{
                totalWalkCount += 1
                exerciseDistance += mission.distance
                exerciseDuration += mission.duration
            }
            else ->{}
        }
        pets.filter{it.isWith}.forEach{
            it.missionCompleted(mission)
        }
        event.value = UserEvent(UserEventType.UpdatedPlayData)
    }
    fun isLevelUp(lvData:MetaData?) : Boolean{
        val lv = lvData?.level ?: return false
        lvData.nextLevelExp?.let{
            nextExp = it
        }
        lvData.prevLevelExp?.let {
            prevExp = it
        }
        if (this.lv < lv) {
            this.lv = lv
            currentProfile.setLv(lv)
            return true
        }
        return false
    }

    fun updateExp(exp:Double) {
        this.exp += exp
        event.value = UserEvent(UserEventType.UpdatedLvData)
    }
    fun updatePoint(point:Int) {
        this.point += point
        event.value = UserEvent(UserEventType.UpdatedLvData)
    }
    fun updateReward(exp:Double, point:Int) {
        this.point += point
        updateExp(exp)
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

enum class FriendStatus{
    Norelation, RequestFriend, Friend, RecieveFriend, Chat, Move, MoveFriend;
    @get:DrawableRes
    val icon:Int
        get() = when(this) {
            Chat -> R.drawable.chat
            RequestFriend -> R.drawable.check
            Friend -> R.drawable.remove_friend
            RecieveFriend -> R.drawable.add_friend
            Move -> R.drawable.add_friend
            MoveFriend -> R.drawable.chat
            else -> R.drawable.add_friend
        }

    @get:StringRes
    val text:Int
        get() = when(this) {
            Chat -> R.string.button_chat
            RequestFriend -> R.string.button_requestSent
            Friend -> R.string.button_removeFriend
            RecieveFriend -> R.string.button_addFriend
            Move -> R.string.button_addFriend
            MoveFriend -> R.string.button_chat
            else -> R.string.button_addFriend
        }


    val buttons:List<FriendButtonFuncType>
        get() = when(this) {
            Chat -> listOf(FriendButtonFuncType.Chat)
            RequestFriend -> listOf()
            Friend -> listOf(FriendButtonFuncType.Delete)
            RecieveFriend -> listOf(FriendButtonFuncType.Reject, FriendButtonFuncType.Accept)
            Move -> listOf(FriendButtonFuncType.Move, FriendButtonFuncType.Request)
            MoveFriend -> listOf(FriendButtonFuncType.Move, FriendButtonFuncType.Chat)
            else -> listOf()
        }



    val isFriend:Boolean
        get() = when(this) {
            Friend, Chat,  MoveFriend -> true
            else -> false
        }


    val useMore:Boolean
        get() = when(this) {
            FriendStatus.Chat -> false
            else -> true
        }
}
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

enum class Lv {
    Green, Blue, Yellow, Pink, Orange;
    companion object{
        const val expRange:Double = 100.0
        const val prefix:String = "Lv."
        fun getLv(value:Int) : Lv {
            if ((0..5).contains(value)) return Green
            if ((5..10).contains(value)) return Blue
            if ((10..15).contains(value)) return Yellow
            if ((15..20).contains(value)) return Pink
            return Orange
        }
    }
    @get:DrawableRes
    val icon:Int
        get() = when(this) {
            Green -> R.drawable.lv_green
            Blue -> R.drawable.lv_blue
            Yellow -> R.drawable.lv_yellow
            Pink -> R.drawable.lv_pink
            Orange -> R.drawable.lv_orange
        }

    @get:DrawableRes
    val effect:Int
        get() = when(this) {
            Green -> R.drawable.lv_effect
            Blue -> R.drawable.lv_effect
            Yellow -> R.drawable.lv_effect
            Pink -> R.drawable.lv_effect
            Orange -> R.drawable.lv_effect
        }

    val color : Color
        get() = when(this) {
            Green -> ColorApp.green
            Blue -> ColorApp.blue
            Yellow -> ColorApp.yellow
            Pink -> ColorApp.pink
            Orange -> ColorApp.orange
        }

    val title : String
        get() = when(this) {
            Green -> "Avocado"
            Blue -> "Blueberry"
            Yellow -> "Banana"
            Pink -> "Peach"
            Orange -> "Carrot"
        }

}

data class ModifyUserData (
    val point:Double? = null,
    var mission:Double? = null,
    var coin:Double? = null
)