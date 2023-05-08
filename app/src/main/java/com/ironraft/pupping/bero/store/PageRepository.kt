package com.ironraft.pupping.bero.store

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ironraft.pupping.bero.AppSceneObserver
import com.jaredrummler.android.device.DeviceName
import com.lib.page.PagePresenter
import com.lib.util.AppUtil
import com.lib.util.DataLog
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.api.*
import com.ironraft.pupping.bero.store.database.DataBaseManager
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.manager.AccountManager
import com.lib.model.SingleLiveData
import com.lib.page.AppObserver
import com.skeleton.module.Repository
import com.skeleton.sns.SnsManager
import com.skeleton.sns.SnsUser
import com.skeleton.sns.SnsUserInfo

enum class RepositoryStatus{
    Initate, Ready
}

enum class RepositoryEvent{
    LoginUpdate, LoginUpdated
}

class PageRepository (
    ctx: Context,
    val storage: StoragePreference,
    val dataBaseManager: DataBaseManager,
    val dataProvider: DataProvider,
    val apiManager: ApiManager,
    val pageModel: ActivityModel,
    val pagePresenter: PagePresenter,
    val appObserver: AppObserver,
    val appSceneObserver: AppSceneObserver,
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
                if(!it.isOptional) pagePresenter.loading(it.isLock)
                dataProvider.request.value = null
            }
        })

        apiManager.event.observe(owner, Observer{ evt ->
            evt?.let {
                when (it){
                    ApiEvent.Join -> {
                        loginCompleted()
                        dataProvider.user.snsUser?.let { snsUser->  apiManager.initateApi(snsUser) }
                    }
                    ApiEvent.Initate -> { loginCompleted() }
                    ApiEvent.Error -> { clearLogin() }
                }
                apiManager.event.value = null
            }
        })

        apiManager.result.observe(owner, Observer{res: ApiSuccess<ApiType>? ->
            res?.let {
                dataProvider.result.value = it
                if (!res.isOptional) pagePresenter.loaded()
                apiManager.result.value = null
                //dataProvider.result.postValue(null) //value = null
            }
        })
        apiManager.error.observe(owner, Observer{err: ApiError<ApiType>?->
            err?.let {
                dataProvider.error.value = it
                apiManager.error.value = null
                //dataProvider.error.postValue(null)
                if (!it.isOptional) {
                    pagePresenter.loaded()
                    appSceneObserver.alert.value = ActivitAlertEvent(ActivitAlertType.ApiError, error = it)
                    /*
                    val builder = Alert.Builder(pagePresenter.activity)
                    builder.setTitle(R.string.alertApi)
                    builder.setText(msg ?:  ctx.getString(R.string.alertApiErrorServer)).show()
                    */
                }
            }

        })
        AppObserver.pushToken.observe(owner, Observer{ token ->
            token?.let {
                onCurrentPushToken(it)
            }
        })
        AppObserver.pageApns.observe(owner, Observer{ apns ->
            apns?.let {
                appSceneObserver.alert.value = ActivitAlertEvent(
                    ActivitAlertType.RecivedApns, apns = apns
                )
                AppObserver.pageApns.value = null
            }
        })
        setupSetting()
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        snsManager.disposeDefaultLifecycleOwner(owner)
        dataProvider.removeObserve(owner)
        accountManager.disposeDefaultLifecycleOwner(owner)
        pagePresenter.activity.getPageActivityViewModel().onDestroyView(owner)
        AppObserver.disposeDefaultLifecycleOwner(owner)

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


    private fun respondApi(res:ApiSuccess<ApiType>){
        //self.accountManager.respondApi(res, appSceneObserver: appSceneObserver)
        //self.walkManager.respondApi(res)
        when (res.type) {
            ApiType.RegistPush -> {
                val token = res.requestData as? String?
                token?.let { registedPushToken(it) }
            }
            //ApiType.getChatRooms(let page, _) -> if page == 0 { self.onMassageUpdated(res) }
            //ApiType.getAlarm(let page, _) -> if page == 0 { self.onAlarmUpdated(res) }
            //ApiType.sendReport(let reportType, _, _) -> self.appSceneObserver?.event = .toast(reportType.completeMessage)
            //case .blockUser(_, let isBlock) : self.appSceneObserver?.event = .toast(isBlock ? String.alert.blockUserCompleted : String.alert.unblockUserCompleted)
            //self.walkManager.resetMapStatus(userFilter: .all)
            else -> {}
        }
    }
    private fun errorApi(err:ApiError<ApiType>){
        //self.accountManager.errorApi(err, appSceneObserver: self.appSceneObserver)
        //self.walkManager.errorApi(err, appSceneObserver: self.appSceneObserver)
        when (err.type) {
            ApiType.RegistPush -> {
                val token = err.requestData as? String?
                token?.let { registFailPushToken(token) }
            }
            else -> {}
        }
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
        event.value = RepositoryEvent.LoginUpdated
        retryRegisterPushToken()
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
        retryRegisterPushToken()

    }

    val isLogin: Boolean get() {
        val token = storage.authToken
        DataLog.d("isLogin token $token", appTag)
        DataLog.d("isLogin token ${token.isNotEmpty()}", appTag)
        return token.isNotEmpty()
    }


    fun setupPush(isOn:Boolean){
        storage.isReceivePush = isOn
        if (isOn) {
            retryRegisterPushToken()
        } else {
            val token = storage.registPushToken
            storage.registPushToken = ""
            storage.retryPushToken = token
            registPushToken("")
        }
    }

    private fun retryRegisterPushToken(){
        if (!storage.isReceivePush) {
            return
        }
        if (storage.retryPushToken.isNotEmpty()) {
            DataLog.d("retryRegisterPushToken " + storage.retryPushToken, appTag)
            registPushToken(storage.retryPushToken)
        }
    }
    fun onCurrentPushToken(token:String) {
        if (!storage.isReceivePush) {
            storage.retryPushToken = token
            return
        }
        if (storage.registPushToken == token) return
        DataLog.d("onCurrentPushToken", appTag)
        when (status.value) {
            RepositoryStatus.Initate -> storage.retryPushToken = token
            RepositoryStatus.Ready -> registPushToken(token)
            else -> {}
        }
    }

    private fun registPushToken(token:String) {
        storage.retryPushToken = ""
        storage.registPushToken = token
        val params = HashMap<String, Any>()
        params["deviceId"] = deviceID
        params["token"] = token
        params["platform"] = SystemEnvironment.platform
        val q = ApiQ(appTag, ApiType.RegistPush, body = params, isOptional = true, requestData = token)
        dataProvider.requestData(q)
    }
    private fun registedPushToken(token:String) {
        DataLog.d("registedPushToken", appTag)
    }
    private fun registFailPushToken(token:String) {
        storage.retryPushToken = token
        storage.registPushToken = ""
        DataLog.d("registFailPushToken", appTag)
    }

}

