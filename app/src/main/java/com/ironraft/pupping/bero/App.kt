package com.ironraft.pupping.bero

import android.app.Application
import com.facebook.stetho.Stetho
import com.ironraft.pupping.bero.koin.pageModelModule
import com.lib.page.AppObserver
import com.lib.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
class PageAppObserver : AppObserver() {}



class App constructor(): Application() {
    private val appTag = javaClass.simpleName
    override fun onCreate() {
        super.onCreate()
        Log.enable = 1 //AppUtil.getDebugLevel()
        //AnalyticsEventLogger(applicationContext).init()
        when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                Log.d(appTag, "Start Memory Debug")
                Log.d(appTag, "Start Remote Debug")
                Stetho.initializeWithDefaults(this)
            }
        }
        val koin = startKoin() {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(pageModelModule)

        }
    }

}