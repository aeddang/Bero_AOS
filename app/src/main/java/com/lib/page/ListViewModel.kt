package com.lib.page

import androidx.lifecycle.MutableLiveData
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
        currentPage = 0
        isBusy = false
        isEmpty.value = false
        isLoading.value = false
        onReset()
    }
    open fun onReset(){}

    fun load():Boolean {
        if(isBusy) return false
        if(isLoadCompleted) return false
        isBusy = true
        isLoading.value = true
        onLoad(currentPage)
        currentPage += 1
        return true
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