package com.ironraft.pupping.bero.store

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.reflect.TypeToken
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEvent
import com.ironraft.pupping.bero.SceneEventType
import com.jaredrummler.android.device.DeviceName
import com.lib.page.PagePresenter
import com.ironraft.pupping.bero.activityui.ActivitAlertEvent
import com.ironraft.pupping.bero.activityui.ActivitAlertType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.activityui.CheckData
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.api.*
import com.ironraft.pupping.bero.store.api.rest.AlarmData
import com.ironraft.pupping.bero.store.api.rest.BannerData
import com.ironraft.pupping.bero.store.api.rest.ChatData
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.ironraft.pupping.bero.store.database.ApiCoreDataManager
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.manager.AccountManager
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.model.SingleLiveData
import com.lib.page.AppObserver
import com.lib.page.PageAppViewModel
import com.lib.page.PageCoroutineScope
import com.lib.page.PageObject
import com.lib.util.*
import com.skeleton.module.Repository
import com.skeleton.module.firebase.Analytics
import com.skeleton.sns.SnsManager
import com.skeleton.sns.SnsUser
import com.skeleton.sns.SnsUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class RepositoryStatus{
    Initate, Ready
}
enum class RepositoryEvent{
    LoginUpdate, LoginUpdated
}
class PageRepository (
    ctx: Context,
    val storage: StoragePreference,
    val apiCoreDataManager: ApiCoreDataManager,
    val dataProvider: DataProvider,
    val apiManager: ApiManager,
    val pageModel: ActivityModel,
    val pageAppViewModel: PageAppViewModel,
    val pagePresenter: PagePresenter,
    val appObserver: AppObserver,
    val appSceneObserver: AppSceneObserver,
    val walkManager: WalkManager,
    val snsManager: SnsManager,
    val analytics: Analytics,
    val topic:Topic,
    private val interceptor: ApiInterceptor
) : Repository(ctx){
    companion object {
        var deviceID:String = "" ; private set
    }
    private val appTag = "Repository"
    val status = MutableLiveData(RepositoryStatus.Initate)
    val event = SingleLiveData<RepositoryEvent?>(null)
    private val accountManager = AccountManager(ctx, dataProvider.user)
    private val scope = PageCoroutineScope()

    val hasNewAlarm = MutableLiveData(false)
    val hasNewChat = MutableLiveData(false)
    fun clearEvent(){
        dataProvider.clearEvent()
    }

    @SuppressLint("HardwareIds")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        scope.createJob()
        deviceID =  Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
        snsManager.setDefaultLifecycleOwner(owner)
        accountManager.setDefaultLifecycleOwner(owner)
        walkManager.setDefaultLifecycleOwner(owner)
        apiManager.setAccountManager(accountManager)

        pageAppViewModel.currentTopPage.observe(owner) { page: PageObject? ->
            val currentPage = page ?: return@observe
            val parameter = HashMap<String,String>()
            parameter["pageId"] = page.pageID
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW,parameter)
            if (currentPage.isPopup) return@observe
            val date = storage.getPageBannerCheckDate(currentPage.pageID)
            apiManager.load(ApiQ(appTag, ApiType.GetBanner, contentID = page.pageID, requestData = date),
                completed = { res->
                    (res.data as? BannerData)?.let{ data->
                        val url = data.url ?: return@let

                        storage.updatedPageBannerValue(currentPage.pageID)
                        appSceneObserver.event.value = SceneEvent(SceneEventType.WebView, value = url)
                    }
                },
                error = {}
            )
        }

        dataProvider.request.observe(owner, Observer{apiQ: ApiQ?->
            apiQ?.let {
                val coreDatakey = if(it.useCoreData) it.type.coreDataKey(it.requestData) else null
                if(!it.isOptional) pagePresenter.loading(it.isLock)
                if (coreDatakey == null){
                    apiManager.load(it)
                } else {
                    scope.launch {
                        if (requestApi(it,coreDatakey)) apiManager.load(it)
                    }
                }
                dataProvider.request.value = null
            }
        })

        dataProvider.result.observe(owner, Observer{res: ApiSuccess<ApiType>? ->
            res?.let {
                when (it.type){
                    ApiType.GetCode -> {
                        if (res.id == appTag) {
                            val category = res.requestData as? CodeCategory ?: return@Observer
                            if (category != CodeCategory.Breed) return@Observer
                            (res.data as? List<*>)?.let { lists ->
                                val datas = lists.filterIsInstance<CodeData>()
                                SystemEnvironment.setupBreedCode(datas)
                            }
                            onReady()
                        }
                    }
                    else -> {}
                }
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
                respondApi(it)
            }
        })
        apiManager.error.observe(owner, Observer{err: ApiError<ApiType>?->
            err?.let {
                errorApi(it)
                dataProvider.error.value = it
                if (!it.isOptional) {
                    pagePresenter.loaded()
                    appSceneObserver.alert.value = ActivitAlertEvent(ActivitAlertType.ApiError, error = it)
                }
            }

        })
        apiManager.rewardData.observe(owner, Observer{data->
            val meta = data ?: return@Observer
            this.onRewardData(meta)
        })



        AppObserver.pushToken.observe(owner, Observer{ token ->
            token?.let {
                onCurrentPushToken(it)
            }
        })
        AppObserver.pageApns.observe(owner, Observer{ apns ->
            apns?.let { pageApns ->
                val pageId = pageApns.page.pageID
                val current = pagePresenter.currentTopPage?.pageID
                when(pageId){
                    PageID.Chat.value -> {
                        if(current == PageID.Chat.value || current == PageID.ChatRoom.value) return@Observer
                    }
                    PageID.Alarm.value -> {
                        if (current == PageID.Alarm.value) return@Observer
                        if (current == PageID.Explore.value) {
                            apiManager.load(ApiQ(appTag, ApiType.GetAlarms, isOptional = true,
                                contentID = dataProvider.user.userId ?: ""))
                            return@Observer
                        }
                    }
                    else ->{}
                }
                appSceneObserver.alert.value = ActivitAlertEvent(
                    ActivitAlertType.RecivedApns, apns = pageApns
                )
                AppObserver.pageApns.value = null
            }
        })
        setupSetting()
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        scope.destoryJob()
        dataProvider.removeObserve(owner)
        snsManager.disposeDefaultLifecycleOwner(owner)
        walkManager.disposeDefaultLifecycleOwner(owner)
        accountManager.disposeDefaultLifecycleOwner(owner)
        pagePresenter.activity.getPageActivityViewModel().onDestroyView(owner)
        AppObserver.disposeDefaultLifecycleOwner(owner)
        pageAppViewModel.removeObserve(owner)
    }

    override fun disposeLifecycleOwner(owner: LifecycleOwner){
        snsManager.disposeLifecycleOwner(owner)
        accountManager.disposeLifecycleOwner(owner)
        walkManager.disposeLifecycleOwner(owner)
        dataProvider.removeObserve(owner)
        pagePresenter.activity.getPageActivityViewModel().onDestroyView(owner)
        pageAppViewModel.removeObserve(owner)
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

    private suspend fun requestApi(apiQ:ApiQ, coreDatakey:String):Boolean{
        return withContext(Dispatchers.IO) {
            var dbData:Any? = null
            when (apiQ.type){
                ApiType.GetCode -> {
                    val type = object : TypeToken<List<CodeData>>() {}.type
                    dbData = apiCoreDataManager.getDatas<List<CodeData>>(coreDatakey, type)
                }
                else ->{}
            }
            if(dbData != null) {
                val success = ApiSuccess(apiQ.type, dbData, apiQ.id, apiQ.isOptional, apiQ.contentID, apiQ.requestData)
                withContext(Dispatchers.Main) {
                    dataProvider.result.value = success
                    if (!apiQ.isOptional) pagePresenter.loaded()
                    return@withContext false
                }

            } else {
                return@withContext true
            }
        }
    }
    private fun respondApi(res:ApiSuccess<ApiType>){
        walkManager.respondApi(res)
        when (res.type) {
            ApiType.DeleteUser -> clearLogin()
            ApiType.RegistPush -> {
                val token = res.requestData as? String?
                token?.let { registedPushToken(it) }
            }
            ApiType.GetChatRooms -> if (res.page == 0) onMassageUpdated(res)
            ApiType.GetAlarms -> if (res.page == 0) onAlarmUpdated(res)
            ApiType.RequestBlock -> walkManager.replaceMapStatus()
            ApiType.CompleteWalk -> updateTodayWalkCount()
            else -> {}
        }
        val coreDatakey = if(res.useCoreData) res.type.coreDataKey(res.requestData) else null
        coreDatakey?.let {
            scope.launch{
                apiCoreDataManager.setData(it, res.data)
            }
        }
    }

    private fun errorApi(err:ApiError<ApiType>){
        accountManager.errorApi(err, appSceneObserver)
        walkManager.errorApi(err)

        when (err.type) {
            ApiType.RegistPush -> {
                val token = err.requestData as? String?
                token?.let { registFailPushToken(token) }
            }
            ApiType.GetCode -> {
                if (err.id == appTag) {
                    val category = err.requestData as? CodeCategory ?: return
                    apiCoreDataManager.getData<List<CodeData>>(CodeCategory.Breed.apiCoreKey)
                        ?.let { savedData ->
                            SystemEnvironment.setupBreedCode(savedData)
                        }
                    if (category == CodeCategory.Breed) onReady()
                }
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
        status.value = RepositoryStatus.Initate
        apiManager.joinAuth(user, info)
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
        analytics.setUserID(null)
    }

   fun autoSnsLogin() {
        val user = dataProvider.user.snsUser
        val token = storage.authToken
        DataLog.d("$user",appTag)
        DataLog.d("token " + (token ?: ""),appTag)
        if ( user != null && token.isNotEmpty() ) {
            apiManager.initateApi(token, user)
        } else {
            clearLogin()
        }
    }

    private fun loginCompleted() {
        DataLog.d("loginCompleted ${interceptor.accesstoken}", appTag)
        pagePresenter.loaded()
        if (SystemEnvironment.breedCode.isEmpty()) getBreedCodeData()
        else onReady()

    }
    private fun getBreedCodeData(){
        val params = java.util.HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Breed.name.lowercase()
        val q = ApiQ(appTag, ApiType.GetCode, query = params, requestData = CodeCategory.Breed)
        dataProvider.requestData(q)

    }
    private fun onReady() {
        storage.authToken = interceptor.accesstoken
        status.value = RepositoryStatus.Ready
        event.value = RepositoryEvent.LoginUpdated
        dataProvider.user.snsUser?.let {
            apiManager.load(ApiQ(appTag, ApiType.GetUser, isOptional = true, contentID = it.snsID))
            apiManager.load(ApiQ(appTag, ApiType.GetPets, isOptional = true, contentID = it.snsID))
            apiManager.load(ApiQ(appTag, ApiType.GetAlarms, isOptional = true, contentID = it.snsID))
            apiManager.load(ApiQ(appTag, ApiType.GetChatRooms, isOptional = true, contentID = it.snsID))
        }
        retryRegisterPushToken()
        dataProvider.user.snsUser?.let { user->
            analytics.setUserID(user.snsID)
            analytics.setUserProperty(user.snsType)
        }

    }

    val isLogin: Boolean get() {
        val token = storage.authToken
        DataLog.d("isLogin token $token", appTag)
        DataLog.d("isLogin token ${token.isNotEmpty()}", appTag)
        return token.isNotEmpty()
    }

    private fun onRewardData(data:MetaData){
        val expValue = if(data.exp == 0.0) null else data.exp
        val pointValue = if(data.point == 0) null else data.point
        if (expValue != null && pointValue != null){
            dataProvider.user.updateReward(expValue, pointValue)
        } else {
            expValue?.let { exp ->
                dataProvider.user.updateExp(exp)
            }
            pointValue?.let { point->
                dataProvider.user.updatePoint(point)
            }
        }
        getReward(data)
    }
    private fun getReward(lvData:MetaData?){
        val exp = lvData?.exp ?: 0.0
        val point = lvData?.point ?: 0
        if (exp == 0.0 && point == 0) {return}
        //SoundToolBox().play(snd:Asset.sound.reward)
        if (point >= 100){
            appSceneObserver.sheet.value  = ActivitSheetEvent(
                type = ActivitSheetType.Select,
                title= ctx.getString(R.string.alert_welcome),
                text = ctx.getString(R.string.alert_welcomeText),
                point = point,
                exp = exp,
                isNegative = false,
                handler =  { checkLevelUp(lvData) }
            )
            walkManager.updateReward(exp, point)
            return

        } else if (point == 0){
            appSceneObserver.event.value = SceneEvent(
                type = SceneEventType.Check,
                value = CheckData(
                    text = "+ exp $exp",
                    handler = { checkLevelUp(lvData) }
                )
            )
        } else if (exp == 0.0){
            appSceneObserver.event.value = SceneEvent(
                type = SceneEventType.Check,
                value = CheckData(
                    text = "+ point $point",
                    handler = { checkLevelUp(lvData) }
                )
            )
        } else {
            appSceneObserver.event.value = SceneEvent(
                type = SceneEventType.Check,
                value = CheckData(
                    text = "+ point $point\n+ exp $exp",
                    handler = { checkLevelUp(lvData) }
                )
            )
        }
        walkManager.updateReward(exp, point)
    }
    private fun checkLevelUp(lvData:MetaData?){
        if (dataProvider.user.isLevelUp(lvData)) {
            appSceneObserver.event.value = SceneEvent(type = SceneEventType.LevelUp)
        }
    }

    private fun onAlarmUpdated(res:ApiSuccess<ApiType>){
        val lists = res.data as? List<*> ?: return
        val datas = lists.filterIsInstance<AlarmData>()
        datas.firstOrNull()?.let { data ->
            if (res.id == appTag) {
                hasNewAlarm.value = storage.alarmDate != data.createdAt
            } else {
                data.createdAt?.let {
                    storage.alarmDate = it
                }
                hasNewAlarm.value = false
            }
        }
    }

    private fun onMassageUpdated(res:ApiSuccess<ApiType>){
        if (res.id != appTag)  return
        val lists = res.data as? List<*> ?: return
        val datas = lists.filterIsInstance<ChatData>()
        datas.find { it.isRead == true }?.let { hasNewChat.value = true }
    }

    fun setupPush(isOn:Boolean){
        storage.isReceivePush = isOn
        if (isOn) {
            retryRegisterPushToken()
        } else {
            val token = storage.registPushToken
            storage.registPushToken = ""
            storage.retryPushToken = token
            requestRegistPushToken("")
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
        requestRegistPushToken(token)
    }
    private fun requestRegistPushToken(token:String) {
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
    fun setupExpose(isOn:Boolean){
        storage.isExposeSetup = true
        storage.isExpose = isOn
    }

    fun updateTodayWalkCount(diff:Int = 1){
        var count = diff
        val now = AppUtil.networkDate().toDateFormatter("yyyyMMdd")
        storage.walkCount.let { pre->
            if (pre.contains(now)) {
                val preCount = pre.replace(now, "").toInt()
                if (preCount != -1) {
                    count += preCount
                }
            }
        }
        storage.walkCount = now + count.toString()
        WalkManager.todayWalkCount = count
    }

}

