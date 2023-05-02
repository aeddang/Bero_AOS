package com.ironraft.pupping.bero.store

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jaredrummler.android.device.DeviceName
import com.lib.page.PagePresenter
import com.lib.util.AppUtil
import com.lib.util.DataLog
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.api.*
import com.ironraft.pupping.bero.store.database.DataBaseManager
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.manager.AccountManager
import com.lib.model.SingleLiveData
//import com.skeleton.component.dialog.Alert
import com.skeleton.module.Repository
import com.skeleton.module.network.ErrorType
import com.skeleton.sns.SnsManager
import com.skeleton.sns.SnsUser
import com.skeleton.sns.SnsUserInfo

enum class RepositoryStatus{
    Initate, Ready
}

enum class RepositoryEvent{
    LoginUpdate, LoginUpdated
}

class PageRepository (ctx: Context,
                      val storage: StoragePreference,
                      val dataBaseManager: DataBaseManager,
                      val dataProvider: DataProvider,
                      val apiManager: ApiManager,
                      val pageModel: ActivityModel,
                      val pagePresenter: PagePresenter,
                      val shareManager:ShareManager,
                      val snsManager: SnsManager,
                      val topic:Topic,
                      private val interceptor: ApiInterceptor


) : Repository(ctx){
    companion object {
        var deviceID:String = "" ; private set
    }
    private val appTag = "Repository"
    val status = MutableLiveData<RepositoryStatus>(RepositoryStatus.Initate)
    val event = SingleLiveData<RepositoryEvent?>(null)
    private val accountManager = AccountManager(dataProvider.user)

    fun clearEvent(){
        dataProvider.clearEvent()
    }

    @SuppressLint("HardwareIds")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        deviceID =  Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
        snsManager.setDefaultLifecycleOwner(owner)
        accountManager.setDefaultLifecycleOwner(owner)
        apiManager.setAccountManager(accountManager)
        dataProvider.request.observe(owner, Observer{apiQ: ApiQ?->
            apiQ?.let {
                apiManager.load(it)
                if(!it.isOptional) {
                    if (it.isLock) {
                        pagePresenter.loading(true)
                    } else {
                        pagePresenter.loading(false)
                    }
                }
                dataProvider.request.value = null
            }

        })

        apiManager.event.observe(owner, Observer{ evt ->
            evt?.let {
                when (it){
                    ApiEvent.Join -> {
                        DataLog.d("apiManager initate", appTag)
                        loginCompleted()
                        dataProvider.user.snsUser?.let { snsUser->
                            apiManager.initateApi(snsUser)
                        }
                    }
                    ApiEvent.Initate -> {
                        DataLog.d("apiManager initate", appTag)
                        loginCompleted()
                    }
                    ApiEvent.Error -> {
                        DataLog.d("apiManager error", appTag)
                        clearLogin()
                    }
                }
                apiManager.event.value = null
            }
        })

        apiManager.result.observe(owner, Observer{res: ApiSuccess<ApiType>? ->
            res?.let {
                dataProvider.result.value = it
                if (!res.isOptional) pagePresenter.loaded()
                apiManager.result.value = null
                dataProvider.result.postValue(null) //value = null
            }
        })
        apiManager.error.observe(owner, Observer{err: ApiError<ApiType>?->
            err?.let {
                dataProvider.error.value = it
                apiManager.error.value = null
                dataProvider.error.postValue(null)
                if (!it.isOptional) {
                    pagePresenter.loaded()
                    val msg =
                        if ( it.errorType != ErrorType.API ) ctx.getString(R.string.alert_apiErrorServer)
                        else it.msg
                    /*
                    val builder = Alert.Builder(pagePresenter.activity)
                    builder.setTitle(R.string.alertApi)
                    builder.setText(msg ?:  ctx.getString(R.string.alertApiErrorServer)).show()
                    */
                }
            }

        })
        setupSetting()
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        snsManager.disposeDefaultLifecycleOwner(owner)
        dataProvider.removeObserve(owner)
        accountManager.disposeDefaultLifecycleOwner(owner)
        pagePresenter.activity.getPageActivityViewModel().onDestroyView(owner)

    }

    override fun disposeLifecycleOwner(owner: LifecycleOwner){
        snsManager.disposeLifecycleOwner(owner)
        accountManager.disposeLifecycleOwner(owner)
        dataProvider.removeObserve(owner)
        pagePresenter.activity.getPageActivityViewModel().onDestroyView(owner)
    }

    private fun setupSetting(){
        if (!storage.initate) {
            storage.initate = true
            SystemEnvironment.firstLaunch = true
        }
        SystemEnvironment.systemVersion = AppUtil.getAppVersion(ctx)
        SystemEnvironment.isTablet = AppUtil.isBigsizeDevice(ctx)
        if (storage.deviceModel.isEmpty()) {
            DeviceName.with(ctx).request { info, _ ->
                //val manufacturer = info.manufacturer // "Samsung"
                //val name = info.marketName // "Galaxy S8+"
                //val model = info.model // "SM-G955W"
                //val codename = info.codename // "dream2qltecan"
                val deviceName = info.name // "Galaxy S8+"
                storage.deviceModel = deviceName
                SystemEnvironment.model = deviceName
            }
        } else {
            SystemEnvironment.model = storage.deviceModel
        }

        dataProvider.user.registUser(
            storage.loginId,
            storage.loginToken,
            storage.loginType)
    }


    fun registerSnsLogin(user:SnsUser, info:SnsUserInfo?) {
        DataLog.d("registerSnsLogin $user", appTag)
        storage.loginId = user.snsID
        storage.loginToken = user.snsToken
        storage.loginType = user.snsType.apiCode()
        dataProvider.user.registUser(user)
        pagePresenter.loading(true)
        apiManager.joinAuth(user, info)
        status.value = RepositoryStatus.Initate
        event.value = RepositoryEvent.LoginUpdate
    }
    fun clearLogin() {
        DataLog.d("clearLogin", appTag)
        storage.loginId = ""
        storage.loginToken = ""
        storage.loginType = ""
        storage.authToken = ""
        apiManager.clearApi()
        dataProvider.user.clearUser()
        snsManager.requestAllLogOut()
        pagePresenter.loaded()
        status.value = RepositoryStatus.Ready
        event.value = RepositoryEvent.LoginUpdate
        event.value = RepositoryEvent.LoginUpdated
        //retryRegisterPushToken()
    }

   fun autoSnsLogin() {

        val user = dataProvider.user.snsUser
        val token = storage.authToken
        DataLog.d("$user",appTag)
        DataLog.d("token " + (token ?: ""),appTag)
        if ( user != null && token.isNotEmpty() ) {
            event.value = RepositoryEvent.LoginUpdate
            apiManager.initateApi(token, user)
        } else {
            clearLogin()
        }
    }

    private fun loginCompleted() {
        DataLog.d("loginCompleted ${interceptor.accesstoken}", appTag)
        pagePresenter.loaded()
        onReady()
    }

    private fun onReady() {
        storage.authToken = interceptor.accesstoken
        status.value = RepositoryStatus.Ready
        event.value = RepositoryEvent.LoginUpdated
        dataProvider.user.snsUser?.let {
            apiManager.load(ApiQ(appTag, ApiType.GetUser, isOptional = true, requestData = it))
            apiManager.load(ApiQ(appTag, ApiType.GetPets, isOptional = true, requestData = it))
            //self.dataProvider.requestData(q: .init(id: self.tag, type: .getChatRooms(page: 0), isOptional: true))
            //self.dataProvider.requestData(q: .init(id: self.tag, type: .getAlarm(page: 0)))
        }
        //retryRegisterPushToken()

    }

    val isLogin: Boolean get() {
        val token = storage.authToken
        DataLog.d("isLogin token $token", appTag)
        DataLog.d("isLogin token ${token.isNotEmpty()}", appTag)
        return token.isNotEmpty()
    }

}

