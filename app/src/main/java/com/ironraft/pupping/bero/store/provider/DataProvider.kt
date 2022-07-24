package com.ironraft.pupping.bero.store.provider

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.store.api.ApiError
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiSuccess
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.model.User


class DataProvider {
    val user = User()
    val request = MutableLiveData<ApiQ?>()
    val result = MutableLiveData<ApiSuccess<ApiType>?>()
    val error = MutableLiveData<ApiError<ApiType>?>()

    fun requestData(q:ApiQ?){
        request.value = q
    }

    fun removeObserve(owner: LifecycleOwner){
        request.removeObservers(owner)
        result.removeObservers(owner)
        error.removeObservers(owner)
    }

    fun clearEvent(){
        request.value = null
        result.value = null
        error.value = null
    }
}