package com.skeleton.sns

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.ironraft.pupping.bero.R
import com.skeleton.theme.ColorApp
import java.util.*

interface Sns {
    fun requestLogin(){}
    fun requestLogin(fragment: Fragment){}
    fun requestLogOut()
    fun getAccessTokenInfo()
    fun getUserInfo()
    fun requestUnlink()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activityRequestCode:Int? = null): Boolean
    fun destroy()
}

enum class SnsType(val code:String){
    Fb("FACEBOOK"), Google("GOOGLE");
    fun apiCode() : String {
        return when(this) {
            SnsType.Fb -> "Facebook"
            SnsType.Google -> "Google"
        }
    }

    val logo : Int
        get() = when(this) {
                SnsType.Fb -> R.drawable.facebook
                SnsType.Google -> R.drawable.google
        }

    val title : String
        get() = when(this) {
            SnsType.Fb -> "Facebook"
            SnsType.Google -> "Google"
        }

    val color:Color
        get() = when(this) {
            SnsType.Fb -> Color(0xFF3578E5)
            SnsType.Google -> ColorApp.black
        }

    companion object{
        fun getType(value:String?):SnsType?{
            return when(value?.lowercase(Locale.getDefault())){
                "facebook" -> Fb
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