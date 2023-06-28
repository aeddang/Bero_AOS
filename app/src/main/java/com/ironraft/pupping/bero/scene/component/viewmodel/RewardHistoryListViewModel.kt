package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.scene.component.item.RewardHistoryListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.RewardHistoryData
import com.ironraft.pupping.bero.store.api.rest.RewardValueType
import com.lib.page.ListViewModel
import com.skeleton.component.item.HistoryType
import java.util.*

open class RewardHistoryListViewModel(val repo: PageRepository, val type: HistoryType)
    :ListViewModel<RewardHistoryListItemData,List<RewardHistoryData>>() {
    val currentId:String = repo.dataProvider.user.userId ?: ""

    fun initSetup(owner: LifecycleOwner, pageSize:Int): RewardHistoryListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }


    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetRewardHistory,
            contentID = currentId,
            page = page,
            pageSize = pageSize,
            requestData = type.apiType
        )
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<RewardHistoryListItemData>?, addDatas: List<RewardHistoryData>?): List<RewardHistoryListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<RewardHistoryListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            RewardHistoryListItemData().setData(d,  type = type, idx = start + idx)
        }
        isLoadCompleted = datas.count() < pageSize
        return added
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentId) return@observe
            if(res.requestData as? RewardValueType != type.apiType) return@observe
            when ( res.type ){
                ApiType.GetRewardHistory -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<RewardHistoryData> ?: listOf())
                }
                ApiType.RequestBlock -> {
                    reset()
                    load()
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            when ( err.type ){
                ApiType.GetBlockUsers -> {
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