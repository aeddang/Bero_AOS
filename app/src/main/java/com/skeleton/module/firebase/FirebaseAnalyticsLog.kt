package com.skeleton.module.firebase

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.skeleton.sns.SnsType

class Analytics(context: Context){
    val instance =  FirebaseAnalytics.getInstance(context)
    fun logEvent(type:String, params: Map<String, String> = HashMap()){
        instance.logEvent(type){
            params.forEach { data ->
                param(data.key, data.value)

            }
        }
    }

    fun setUserID(id:String?){
        instance.setUserId(id)
    }
    fun setUserProperty(type:SnsType){
        instance.setUserProperty("type", type.title)
    }
}