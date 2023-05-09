package com.ironraft.pupping.bero.store.provider.model

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.lib.util.PageLog
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.lib.util.toDate
import com.lib.util.toFormatString
import com.skeleton.sns.SnsType
import com.skeleton.sns.SnsUser
import java.time.LocalDate
import java.util.*


data class ModifyUserProfileData (
    var image:Bitmap? = null,
    var nickName:String? = null,
    var gender:Gender? = null,
    var birth:LocalDate? = null,
    var email:String? = null,
    var introduction:String? = null
)

class UserProfile(val isMine:Boolean = false){
    var id:String = UUID.randomUUID().toString(); private set
    var userId:String = ""; private set
    val imagePath:MutableLiveData<String?> = MutableLiveData<String?>()
    val image: MutableLiveData<Bitmap?> = MutableLiveData<Bitmap?>()
    val nickName:MutableLiveData<String?> = MutableLiveData<String?>()
    val introduction:MutableLiveData<String?> = MutableLiveData<String?>()
    val gender:MutableLiveData<Gender?> = MutableLiveData<Gender?>()
    val birth:MutableLiveData<LocalDate?> = MutableLiveData<LocalDate?>()
    val email:MutableLiveData<String?> = MutableLiveData<String?>()
    val lv:MutableLiveData<Int> = MutableLiveData<Int>(1)
    val status:MutableLiveData<FriendStatus> = MutableLiveData<FriendStatus>(FriendStatus.Norelation)
    var date:String? = null; private set
    var type:SnsType? = null; private set
    var originData:UserData? = null; private set

    fun setData(data:SnsUser) : UserProfile{
        type = data.snsType
        return this
    }

    fun setData(data:UserData):UserProfile{
        if (isMine) originData = data
        userId = data.userId ?: ""
        nickName.value = data.name ?: "Bero User"
        email.value = data.email
        if (data.pictureUrl?.isEmpty() == false) imagePath.value = data.pictureUrl
        type = SnsType.getType(data.providerType)
        gender.value = Gender.getGender(data.sex)
        birth.value = data.birthdate?.toDate()
        introduction.value = data.introduce
        image.value = null
        status.value = if (data.isFriend == true) FriendStatus.Friend else FriendStatus.Norelation
        date = data.createdAt?.toDate()?.toFormatString("EEEE, MMMM d, yyyy")
        return this
    }
    fun setLv(value:Int){
        lv.value = value
    }

    fun update(data:ModifyUserProfileData) : UserProfile{
        data.image?.let { image.value = it }
        data.nickName?.let { nickName.value = it }
        data.gender?.let { gender.value = it }
        data.birth?.let { birth.value = it }
        data.email?.let { email.value = it }
        data.introduction?.let { introduction.value = it }
        return this
    }

    fun update(btm:Bitmap?) : UserProfile{
        image.value = btm
        if(btm == null) imagePath.value = null
        return this
    }

    fun removeObservers(owner: LifecycleOwner){
        image.removeObservers(owner)
        imagePath.removeObservers(owner)
        image.removeObservers(owner)
        nickName.removeObservers(owner)
        introduction.removeObservers(owner)
        gender.removeObservers(owner)
        birth.removeObservers(owner)
        email.removeObservers(owner)
        lv.removeObservers(owner)
        status.removeObservers(owner)
    }
}
