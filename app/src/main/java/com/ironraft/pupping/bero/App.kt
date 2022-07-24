package com.ironraft.pupping.bero

import android.app.Application
import com.facebook.stetho.Stetho
import com.ironraft.pupping.bero.BuildConfig
import com.lib.util.Log

import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
abstract class HiltApp : Application(){
}

@HiltAndroidApp
class App @Inject constructor(): HiltApp() {
    private val appTag = javaClass.simpleName
    override fun onCreate() {
        super.onCreate()

        Log.enable = 1 //AppUtil.getDebugLevel()
        //AnalyticsLog(applicationContext).init()

        when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                Log.d(appTag, "Start Memory Debug")
                Log.d(appTag, "Start Remote Debug")
                Stetho.initializeWithDefaults(this)
            }
        }
    }

}