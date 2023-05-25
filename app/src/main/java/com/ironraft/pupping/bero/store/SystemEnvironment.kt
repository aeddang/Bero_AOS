package com.ironraft.pupping.bero.store

import android.provider.Settings
import com.ironraft.pupping.bero.store.api.ApiResponse

class SystemEnvironment {
    companion object {
        var model:String = ""
        var systemVersion:String = ""
        var firstLaunch :Boolean = false
        var isTablet = false
        var isTestMode = false
        var breedCode = HashMap<String,String>()
        const val platform = "AOS"
    }
}