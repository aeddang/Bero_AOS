package com.ironraft.pupping.bero.store.preference

import android.content.Context
import com.lib.module.CachedPreference
import com.ironraft.pupping.bero.BuildConfig
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.store.TopicCategory
import com.lib.util.AppUtil
import com.lib.util.toFormatString

class StoragePreference(context: Context) : CachedPreference(context, PreferenceName.SETTING + BuildConfig.BUILD_TYPE) {
    companion object {
        private const val VS = "1.000"
        private const val initate = "initate$VS"
        private const val isReceivePush = "isReceivePush$VS"
        private const val retryPushToken = "retryPushToken$VS"
        private const val registPushToken = "registPushToken$VS"
        private const val loginType = "loginType$VS"
        private const val loginToken = "loginToken$VS"
        private const val loginId = "loginId$VS"
        private const val authToken = "authToken$VS"

        private const val walkCount = "walkCount$VS"
        private const val isFirstChat = "isFirstChat$VS"
        private const val isFirstWalk = "isFirstWalk$VS"
        private const val bannerDate = "bannerDate$VS"
        private const val bannerValue = "bannerValue$VS"
        private const val isExposeSetup = "isExposeSetup$VS"
        private const val isExpose = "isExpose$VS"
        private const val alarmDate = "alarmDate$VS"

        private const val deviceModel = "deviceModel$VS"
        private const val TOPIC = "topic_"
    }
    var initate:Boolean
        get(){ return get(StoragePreference.initate, false) as Boolean }
        set(value:Boolean){ put(StoragePreference.initate, value) }

    var isReceivePush:Boolean
        get(){ return get(StoragePreference.isReceivePush, true) as Boolean }
        set(value:Boolean){ put(StoragePreference.isReceivePush, value) }

    var retryPushToken:String
        get(){ return get(StoragePreference.retryPushToken, "") as String }
        set(value:String){ put(StoragePreference.retryPushToken, value) }

    var registPushToken:String
        get(){ return get(StoragePreference.registPushToken, "") as String }
        set(value:String){ put(StoragePreference.registPushToken, value) }

    var loginType:String
        get(){ return get(StoragePreference.loginType, "") as String }
        set(value:String){ put(StoragePreference.loginType, value) }

    var loginToken:String
        get(){ return get(StoragePreference.loginToken, "") as String }
        set(value:String){ put(StoragePreference.loginToken, value) }

    var loginId:String
        get(){ return get(StoragePreference.loginId, "") as String }
        set(value:String){ put(StoragePreference.loginId, value) }

    var authToken:String
        get(){ return get(StoragePreference.authToken, "") as String }
        set(value:String){ put(StoragePreference.authToken, value) }


    var walkCount:String
        set(value){ put(StoragePreference.walkCount, value) }
        get(){ return get(StoragePreference.walkCount, "") as String }


    var alarmDate:String
        set(value){ put(StoragePreference.alarmDate, value) }
        get(){ return get(StoragePreference.alarmDate, "") as String }

    var isFirstChat:Boolean
        get(){ return get(StoragePreference.isFirstChat, true) as Boolean }
        set(value:Boolean){ put(StoragePreference.isFirstChat, value) }

    var isFirstWalk:Boolean
        get(){ return get(StoragePreference.isFirstWalk, true) as Boolean }
        set(value:Boolean){ put(StoragePreference.isFirstWalk, value) }

    fun isDailyBannerCheck(id:PageID):Boolean{
        val now = AppUtil.networkTimeDate().toFormatString("yyyyMMdd")
        val prev =  getPageBannerCheckDate(id)
        return now == prev
    }
    fun isSameBannerCheck(id:PageID, value:String):Boolean{
        val prev =  getPageBannerCheckValue(id)
        return value == prev
    }

    fun getPageBannerCheckValue(id:PageID):String?{
        return get(StoragePreference.bannerValue + id, null) as? String
    }
    fun getPageBannerCheckDate(id:PageID):String?{
        return get(StoragePreference.bannerDate + id, null) as? String
    }
    fun updatedPageBannerValue(id:PageID, value:String){
        val now = AppUtil.networkTimeDate().toFormatString("yyyyMMdd")
        put(StoragePreference.bannerValue + id, value)
        put(StoragePreference.bannerDate + id, now)
    }

    var isExposeSetup:Boolean
        get(){ return get(StoragePreference.isExposeSetup, false) as Boolean }
        set(value:Boolean){ put(StoragePreference.isExposeSetup, value) }

    var isExpose:Boolean
        get(){ return get(StoragePreference.isExpose, true) as Boolean }
        set(value:Boolean){ put(StoragePreference.isExpose, value) }


    var deviceModel:String
        get(){ return get(StoragePreference.deviceModel, "") as String }
        set(value:String){ put(StoragePreference.deviceModel, value) }


    fun subscribeTopic(topic:TopicCategory, isSubscribe:Boolean){
        put(TOPIC +topic.name, isSubscribe)
    }
    fun isSubscribe(topic:TopicCategory):Boolean{
        return get(TOPIC +topic.name, false) as Boolean
    }


}