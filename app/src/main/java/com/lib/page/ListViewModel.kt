package com.lib.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.MutableLiveData
import java.util.UUID

@Suppress("UNCHECKED_CAST")
abstract class ListViewModel<T,V>:ComponentViewModel() {
    val listDatas = MutableLiveData<List<T>?>()
    val isEmpty = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    var pageSize:Int = 12
    var isLoadCompleted:Boolean = false; protected set
    var currentPage:Int = 0; protected set
    fun reset(){
        isLoadCompleted = false
        listDatas.value = null
        isBusy = false
        isEmpty.value = false
        isLoading.value = false
        finalKey = -1
        onReset()
    }
    open fun onReset(){}


    fun load():Boolean {
        if(isBusy) return false
        if(isLoadCompleted) return false
        isBusy = true
        isLoading.value = true
        val page = currentPage
        currentPage = page + 1
        onLoad(page)
        return true
    }
    private var finalKey:Int = -1
    fun continueLoad():Boolean {
        val key = UUID.randomUUID().hashCode()
        if (finalKey == key) return false
        if (listDatas.value?.isEmpty() == true) return false
        finalKey = key
        return load()
    }
    abstract fun onLoad(page:Int)

    fun loaded(datas:V?){
        val prev = listDatas.value
        val added = onLoaded(prev, datas)
        val resultList = ArrayList<T>()
        prev?.let {
            resultList.addAll(it)
        }
        resultList.addAll(added)
        listDatas.value = resultList.toList()
        isBusy = false
        isLoading.value = false
    }
    abstract fun onLoaded(prevDatas:List<T>?, addDatas:V?):List<T>
}