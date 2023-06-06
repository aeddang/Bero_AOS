package com.ironraft.pupping.bero.store

import android.provider.Settings
import com.ironraft.pupping.bero.store.api.ApiResponse
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class SystemEnvironment {
    companion object {
        var model:String = ""
        var systemVersion:String = ""
        var firstLaunch :Boolean = false
        var isTablet = false
        var isTestMode = true
        var breedCode = HashMap<String,String>()
        val zoneOffset: ZoneOffset = ZoneId
            .systemDefault()
            .rules
            .getOffset(
                Instant.now()
            )
        const val platform = "AOS"

    }
}