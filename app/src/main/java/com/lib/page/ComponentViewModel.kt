package com.lib.page

import androidx.lifecycle.ViewModel
import com.ironraft.pupping.bero.store.api.ApiError
import com.ironraft.pupping.bero.store.api.ApiSuccess
import com.ironraft.pupping.bero.store.api.ApiType
import org.intellij.lang.annotations.Identifier

open class ComponentViewModel:ViewModel(),PageLifecycleUser{
    val tag = javaClass.simpleName
    var isInit = false
    var isBusy = false
    var apiResult: ApiSuccess<ApiType>? = null; private set
    var apiError: ApiError<ApiType>? = null; private set
    var observeredValue: Any? = null; private set
    val scope = PageCoroutineScope()

    fun isValidResult(result: ApiSuccess<ApiType>):Boolean{
        if(result.hashId == apiResult?.hashId) return false
        apiResult = result
        return true
    }
    fun isValidError(error: ApiError<ApiType>):Boolean{
        if(error.hashId == apiError?.hashId) return false
        apiError = error
        return true
    }

    fun isValidValue(value: Any):Boolean{
        if(observeredValue == value) return false
        observeredValue = value
        return true
    }

}