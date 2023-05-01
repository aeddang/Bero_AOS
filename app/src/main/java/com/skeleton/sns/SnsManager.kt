package com.skeleton.sns

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lib.model.SingleLiveData
import com.lib.page.PageComposeable
import com.lib.page.PageLifecycleUser
import com.lib.util.Log

class SnsManager(private val context: Context) : PageLifecycleUser {
    private val appTag = javaClass.simpleName

    private val currentSnsType = MutableLiveData<SnsType?>()
    val user = MutableLiveData<SnsUser?>()
    val userInfo = MutableLiveData<SnsUserInfo?>()

    private val respond = MutableLiveData<SnsResponds?>()
    val error = SingleLiveData<SnsError?>(null)

    private var currentManager:Sns? = null

    val fb:FaceBookManager = FaceBookManager()
    val google:GoogleSignManager = GoogleSignManager()
    fun setup(ac: PageComposeable){
        google.setup(ac)
        fb.setup(ac)
    }

    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        fb.respond.observe(owner, Observer{ res:SnsResponds? ->
            res ?: return@Observer
            onRespond(res)
        })
        fb.error.observe(owner, Observer{ err:SnsError? ->
            err ?: return@Observer
            onError(err)
        })
        google.respond.observe(owner, Observer{ res:SnsResponds? ->
            res ?: return@Observer
            onRespond(res)
        })
        google.error.observe(owner, Observer{ err:SnsError? ->
            err ?: return@Observer
            onError(err)
        })
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        fb.respond.removeObservers(owner)
        fb.error.removeObservers(owner)
        google.respond.removeObservers(owner)
        google.error.removeObservers(owner)

    }

    private fun getManager(type:SnsType? = null) : Sns?{
        return when(type) {
            SnsType.Fb -> fb
            SnsType.Google -> google
            else -> null
        }
    }

    private fun onRespond(res:SnsResponds){
        this.respond.value = res
        if (currentSnsType.value != null && res.type != currentSnsType.value) return
        when (res.event) {
            SnsEvent.Login -> {
                currentSnsType.value = res.type
                currentManager = getManager(type = res.type)
                user.value = res.data as? SnsUser
                Log.d(appTag, "login $user")
            }
            SnsEvent.Logout -> {
                Log.d(appTag,"logout ${currentSnsType.value}")
                currentSnsType.value = null
                user.value = null
                userInfo.value = null
                currentManager = null
            }
            SnsEvent.InvalidToken -> {
                Log.d(appTag,"invalidToken ${currentSnsType.value}")
                currentSnsType.value = null
                user.value = null
                userInfo.value = null
                currentManager = null
            }
            SnsEvent.GetProfile -> {
                userInfo.value = res.data as? SnsUserInfo
                Log.d(appTag,"getProfile $userInfo")
            }
            else->{}
        }
    }

    private fun onError(err:SnsError){
        this.error.value = err
        if (currentSnsType.value != null && err.type != currentSnsType.value) return
        if (err.event == SnsEvent.Login) {

        }
    }

    fun onActivityResult(requestCode: Int,  resultCode: Int, data: Intent?, activityRequstId:Int? = null): Boolean {
        fb.onActivityResult(requestCode,resultCode, data, activityRequstId)
        google.onActivityResult(requestCode,resultCode, data, activityRequstId)
        return false
    }

    fun requestLogin(type:SnsType, fragment: Fragment? = null) {
        val sns = getManager(type)
        sns?.requestLogin()
        fragment?.let {
            sns?.requestLogin(fragment = it )
        }

    }

    fun requestLogOut() {
        Log.d(appTag, "requestLogOut")
        currentManager?.requestLogOut()
    }
    fun requestAllLogOut() {
        fb.requestLogOut()
        google.requestLogOut()
    }

    fun getAccessTokenInfo() {
        currentManager?.getAccessTokenInfo()
    }

    fun getUserInfo() {
        currentManager?.getUserInfo()
    }

    fun requestUnlink() {
        currentManager?.requestUnlink()
    }
}







