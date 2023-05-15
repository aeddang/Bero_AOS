package com.lib.page

import com.ironraft.pupping.bero.store.api.ApiError
import com.ironraft.pupping.bero.store.api.ApiSuccess
import com.ironraft.pupping.bero.store.api.ApiType

open class ComponentViewModel{
    var isBusy = false
    var apiResult: ApiSuccess<ApiType>? = null; private set
    var apiError: ApiError<ApiType>? = null; private set

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
}