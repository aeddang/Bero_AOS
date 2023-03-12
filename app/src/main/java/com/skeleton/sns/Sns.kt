package com.skeleton.sns

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.ironraft.pupping.bero.R
import java.util.*

interface Sns {
    fun requestLogin(){}
    fun requestLogin(fragment: Fragment){}
    fun requestLogOut()
    fun getAccessTokenInfo()
    fun getUserInfo()
    fun requestUnlink()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
    fun destroy()
}

enum class SnsType(val code:String){
    Fb("FACEBOOK"), Apple("APPLE"), Google("GOOGLE");
    fun apiCode() : String {
        return when(this) {
            SnsType.Fb -> "Facebook"
            SnsType.Apple -> "Apple"
            SnsType.Google -> "Google"
        }
    }
    @DrawableRes
    fun logo() : Int {
        return when(this) {
            SnsType.Fb -> R.drawable.ic_facebook
            SnsType.Apple -> R.drawable.ic_apple
            SnsType.Google -> R.drawable.ic_google
        }
    }

    companion object{
        fun getType(value:String?):SnsType?{
            return when(value?.lowercase(Locale.getDefault())){
                "facebook" -> Fb
                "apple" -> Apple
                "google" -> Google
                else -> null
            }
        }
    }
}

enum class SnsStatus{
    Login, Logout
}

enum class SnsEvent{
    Login, Logout, GetProfile, GetToken, InvalidToken, ReflashToken
}

data class SnsResponds(
    val event:SnsEvent,
    val type:SnsType,
    var data:Any? = null
)

data class SnsError(
    val event:SnsEvent,
    val type:SnsType,
    var error:Throwable? = null
)

data class SnsUser(
    var snsType:SnsType,
    var snsID:String,
    var snsToken:String
)

data class SnsUserInfo(
    var nickName:String? = null,
    var profile:String? = null,
    var email:String? = null
)