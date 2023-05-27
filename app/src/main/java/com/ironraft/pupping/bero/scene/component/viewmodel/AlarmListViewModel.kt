package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.scene.component.item.AlarmListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlarmData
import com.lib.page.ListViewModel
import java.util.*

open class AlarmListViewModel(val repo: PageRepository)
    :ListViewModel<AlarmListItemData,List<AlarmData>>() {
    val currentId:String = repo.dataProvider.user.userId ?: ""


    fun initSetup(owner: LifecycleOwner, pageSize:Int): AlarmListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }

    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetAlarms,
            contentID = currentId,
            page = currentPage,
            pageSize = pageSize)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<AlarmListItemData>?, addDatas: List<AlarmData>?): List<AlarmListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<AlarmListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            AlarmListItemData().setData(d,  idx = start + idx)
        }
        isLoadCompleted = datas.count() < pageSize
        isEmpty.value = added.isEmpty()
        return added
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            when ( res.type ){
                ApiType.GetAlarms -> {
                    if(res.contentID != currentId) return@observe
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<AlarmData> ?: listOf())
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            when ( err.type ){
                ApiType.GetAlarms -> {
                    isBusy = false
                }
                else ->{}
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

}